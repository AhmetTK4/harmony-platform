# Contributing

Harmony is a learning platform with simulated commerce flows. Read the README's setup and limitations before proposing changes.

1. Use JDK 21. Clone the repository and run `./gradlew buildAll` (`.\gradlew.bat buildAll` on Windows).
2. Tests have an isolated, test-only JWT key. Running the application requires your own `JWT_SECRET`, as described in the README. Never commit `.env` or real credentials.
3. For a bug, report reproduction steps, expected/actual behavior, and service names. Redact tokens and personal data.
4. Discuss substantial changes in an issue first. Keep a PR focused and include its test commands, result, and any configuration changes.
5. Test security and message-flow changes with meaningful success/failure cases. Update documentation when API behavior changes.

Maintenance is best-effort. Please email sensitive security findings to ahmettemelkundupoglu@gmail.com instead of posting credentials or an exploitable deployment's details publicly.
