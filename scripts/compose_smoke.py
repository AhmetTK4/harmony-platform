"""Disposable full-stack smoke test. Run after docker compose up, never on production."""
import json
import os
import subprocess
import time
import urllib.error
import urllib.request
import uuid


def request(path, data=None, token=None, expected=200):
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = "Bearer " + token
    req = urllib.request.Request(path, data=json.dumps(data).encode() if data is not None else None, headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=10) as response:
            status, body = response.status, response.read().decode()
    except urllib.error.HTTPError as error:
        status, body = error.code, error.read().decode()
    assert status == expected, f"{path}: expected {expected}, got {status}"
    return body


def eventually(label, check, seconds=240):
    deadline = time.monotonic() + seconds
    while time.monotonic() < deadline:
        try:
            check()
            print("PASS:", label, flush=True)
            return
        except (AssertionError, OSError, subprocess.CalledProcessError):
            time.sleep(3)
    raise AssertionError(f"Timed out: {label}")


def logs_contain(service, text):
    logs = subprocess.check_output(["docker", "compose", "logs", "--no-color", service], text=True)
    assert text in logs, f"Missing expected event in {service}"


def main():
    for port in (8761, 8080, 8081, 8082, 8083, 8084, 8085):
        eventually(f"service health on {port}", lambda port=port: request(f"http://localhost:{port}/actuator/health"))
    for name, url in (
        ("Prometheus", "http://localhost:9090/-/ready"),
        ("Grafana", "http://localhost:3001/api/health"),
        ("Elasticsearch", "http://localhost:9200/_cluster/health?wait_for_status=yellow&timeout=5s"),
        ("Kibana", "http://localhost:5601/api/status"),
    ):
        eventually(name, lambda url=url: request(url))
    eventually("Logstash pipeline", lambda: subprocess.run(
        ["docker", "compose", "exec", "-T", "logstash", "curl", "-fsS", "http://localhost:9600/_node/pipelines"],
        check=True, stdout=subprocess.DEVNULL))
    eventually("RabbitMQ", lambda: subprocess.run(
        ["docker", "compose", "exec", "-T", "rabbitmq", "rabbitmq-diagnostics", "-q", "ping"], check=True))

    base = "http://localhost:8080"
    # Readiness probe is deliberately read-only: registration must never be retried.
    eventually("gateway discovers user-service", lambda: request(base + "/api/auth/login", {"username": "not-created", "password": "invalid"}, expected=401))
    username = "smoke-" + uuid.uuid4().hex
    password = uuid.uuid4().hex
    request(base + "/api/auth/register", {"username": username, "email": username + "@example.invalid", "password": password})
    request(base + "/api/auth/login", {"username": username, "password": "wrong"}, expected=401)
    token = json.loads(request(base + "/api/auth/login", {"username": username, "password": password}))["token"]
    request(base + "/api/products", expected=401)
    request(base + "/api/products", token="invalid", expected=401)
    eventually("authenticated gateway route", lambda: request(base + "/api/products", token=token))
    print("PASS: registration, login, rejected credentials and JWT enforcement", flush=True)

    # Wait for real consumers/bindings before publishing a message.
    for service in ("payment-service", "shipping-service", "order-service"):
        eventually(service + " RabbitMQ connection", lambda service=service: logs_contain(service, "Created new connection"))
    order = {"orderId": "success-" + uuid.uuid4().hex, "userId": username, "productId": "demo", "quantity": 1, "address": "Disposable CI address"}
    request(base + "/api/orders", order, token)
    eventually("order -> payment -> shipping", lambda: logs_contain("shipping-service", "Shipping completed for Order ID: " + order["orderId"]))

    os.environ["PAYMENT_SIMULATION_SUCCESS_RATE"] = "0.0"
    subprocess.run(["docker", "compose", "up", "-d", "--no-deps", "--force-recreate", "payment-service"], check=True)
    eventually("failure simulator healthy", lambda: request("http://localhost:8084/actuator/health"))
    eventually("failure consumer connected", lambda: logs_contain("payment-service", "Created new connection"))
    order["orderId"] = "failure-" + uuid.uuid4().hex
    request(base + "/api/orders", order, token)
    eventually("order -> failed payment -> rollback", lambda: logs_contain("order-service", "Rollback complated for Order ID " + order["orderId"]))
    # Both outcomes are simulations: rollback currently logs, not a database compensation.
    print("PASS: full-stack smoke test completed", flush=True)


if __name__ == "__main__":
    main()
