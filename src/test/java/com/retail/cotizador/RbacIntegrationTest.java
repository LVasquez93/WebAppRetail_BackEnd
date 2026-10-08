package com.retail.cotizador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.cotizador.auth.dto.LoginRequestDto;
import com.retail.cotizador.rbac.dto.RbacMatrizDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RbacIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obtenerTokenAdmin() throws Exception {
        LoginRequestDto req = new LoginRequestDto("ERAMIREZ", "admin123");
        MvcResult res = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @DisplayName("Debe consultar la matriz RBAC completa con catálogo y roles")
    void testObtenerMatrizRbac() throws Exception {
        String token = obtenerTokenAdmin();

        mockMvc.perform(get("/api/v1/rbac/matriz")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.catalogoPermisos", not(empty())))
                .andExpect(jsonPath("$.roles", hasSize(4)))
                .andExpect(jsonPath("$.roles[?(@.rol == 'ROLE_ADMIN')].permisos", not(empty())));
    }

    @Test
    @DisplayName("Debe devolver todos los permisos para el SuperAdmin en mis-permisos")
    void testObtenerMisPermisosAdmin() throws Exception {
        String token = obtenerTokenAdmin();

        mockMvc.perform(get("/api/v1/rbac/mis-permisos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("RBAC_GESTIONAR")))
                .andExpect(jsonPath("$", hasItem("COTIZACIONES_CREAR")));
    }

    @Test
    @DisplayName("Debe permitir al admin restablecer valores por defecto")
    void testResetRbacDefaults() throws Exception {
        String token = obtenerTokenAdmin();

        mockMvc.perform(post("/api/v1/rbac/reset")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasSize(4)));
    }

    @Test
    @DisplayName("Debe consultar catálogo de permisos general")
    void testObtenerCatalogoPermisos() throws Exception {
        String token = obtenerTokenAdmin();

        mockMvc.perform(get("/api/v1/rbac/catalogo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @DisplayName("Debe consultar y asignar permisos especiales a un usuario específico")
    void testGestionarPermisosUsuario() throws Exception {
        String token = obtenerTokenAdmin();

        // 1. Consultar permisos de usuario VENTASSR001
        MvcResult resUsers = mockMvc.perform(get("/api/v1/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String usersJson = resUsers.getResponse().getContentAsString();
        long usuarioId = objectMapper.readTree(usersJson).get("content").get(0).get("id").asLong();

        // 2. Obtener permisos del usuario
        mockMvc.perform(get("/api/v1/rbac/usuarios/" + usuarioId + "/permisos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", notNullValue()))
                .andExpect(jsonPath("$.permisosRolPorDefecto", not(empty())));

        // 3. Asignar permisos especiales
        java.util.List<String> especiales = java.util.List.of("CLIENTES_CREAR_EDITAR", "CLIENTES_IMPORTAR", "COTIZACIONES_CREAR");
        mockMvc.perform(put("/api/v1/rbac/usuarios/" + usuarioId + "/permisos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(especiales)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tienePermisosPersonalizados", is(true)))
                .andExpect(jsonPath("$.permisosEfectivos", hasItem("CLIENTES_IMPORTAR")));

        // 4. Restablecer permisos
        mockMvc.perform(post("/api/v1/rbac/usuarios/" + usuarioId + "/permisos/reset")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tienePermisosPersonalizados", is(false)));
    }
}
