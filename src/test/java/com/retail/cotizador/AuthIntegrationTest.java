package com.retail.cotizador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.cotizador.auth.dto.LoginRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe autenticar correctamente a admin y devolver JWT sin sucursal fija (SaaS Admin)")
    void testLoginExitoso() throws Exception {
        LoginRequestDto req = new LoginRequestDto("ERAMIREZ", "admin123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is("ERAMIREZ")))
                .andExpect(jsonPath("$.rol", is("ROLE_ADMIN")))
                .andExpect(jsonPath("$.sucursalId", nullValue()));
    }

    @Test
    @DisplayName("Debe rechazar credenciales incorrectas con 401")
    void testLoginFallido() throws Exception {
        LoginRequestDto req = new LoginRequestDto("ERAMIREZ", "password_invalido");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe denegar acceso a endpoint protegido sin token Bearer")
    void testEndpointProtegidoSinToken() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe permitir acceso a usuarios con token válido de ADMIN")
    void testEndpointProtegidoConTokenAdmin() throws Exception {
        LoginRequestDto req = new LoginRequestDto("ERAMIREZ", "admin123");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();

        mockMvc.perform(get("/api/v1/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }
}
