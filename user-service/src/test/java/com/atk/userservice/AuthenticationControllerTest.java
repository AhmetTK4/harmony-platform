package com.atk.userservice;

import com.atk.userservice.config.PasswordConfig;
import com.atk.userservice.controller.AuthenticationController;
import com.atk.userservice.entity.UserInformation;
import com.atk.userservice.repository.UserInformationRepository;
import com.atk.userservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@Import(PasswordConfig.class)
class AuthenticationControllerTest {
    @Autowired MockMvc mvc;
    @Autowired PasswordEncoder encoder;
    @MockBean UserInformationRepository repository;
    @MockBean JwtUtil jwt;

    @Test void registrationStoresHashInsteadOfPassword() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"demo\",\"email\":\"demo@example.com\",\"password\":\"demo-password\"}"))
                .andExpect(status().isOk());
        var saved = ArgumentCaptor.forClass(UserInformation.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isNotEqualTo("demo-password");
        assertThat(encoder.matches("demo-password", saved.getValue().getPassword())).isTrue();
    }

    @Test void correctPasswordReturnsToken() throws Exception {
        when(repository.findByUsername("demo")).thenReturn(Optional.of(UserInformation.builder()
                .username("demo").password(encoder.encode("demo-password")).build()));
        when(jwt.generateToken("demo")).thenReturn("signed-token");
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"demo\",\"password\":\"demo-password\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").value("signed-token"));
    }

    @Test void wrongPasswordReturnsUnauthorizedWithoutToken() throws Exception {
        when(repository.findByUsername("demo")).thenReturn(Optional.of(UserInformation.builder()
                .username("demo").password(encoder.encode("correct-password")).build()));
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"demo\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(jwt);
    }

    @Test void unknownUserReturnsUnauthorizedWithoutToken() throws Exception {
        when(repository.findByUsername("missing")).thenReturn(Optional.empty());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"missing\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(jwt);
    }
}
