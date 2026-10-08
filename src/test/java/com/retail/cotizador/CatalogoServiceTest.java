package com.retail.cotizador;

import com.retail.cotizador.clientes.dto.ClienteDto;
import com.retail.cotizador.clientes.service.ClienteService;
import com.retail.cotizador.equipos.dto.EquipoDto;
import com.retail.cotizador.equipos.service.EquipoService;
import com.retail.cotizador.usuarios.dto.UsuarioDto;
import com.retail.cotizador.usuarios.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CatalogoServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EquipoService equipoService;

    @Test
    void testClientesCatalog() {
        ClienteDto nuevo = ClienteDto.builder()
                .razonSocial("BANCO INDUSTRIAL EL SALVADOR SA")
                .nombreComercial("BANCO INDUSTRIAL")
                .contactoPrincipal("Ing. Mario Rivas")
                .telefono("2211-3344")
                .correo("mrivas@bi.com.sv")
                .build();

        ClienteDto guardado = clienteService.crear(nuevo);
        assertNotNull(guardado.getId());
        assertEquals("BANCO INDUSTRIAL EL SALVADOR SA", guardado.getRazonSocial());

        List<ClienteDto> busqueda = clienteService.listarOBuscar("INDUSTRIAL");
        assertFalse(busqueda.isEmpty());
        assertTrue(busqueda.stream().anyMatch(c -> c.getRazonSocial().contains("INDUSTRIAL")));
    }

    @Test
    void testUsuariosCatalog() {
        String testUser = "VENTAS_TEST_" + System.currentTimeMillis();
        UsuarioDto nuevo = UsuarioDto.builder()
                .username(testUser)
                .nombreCompleto("Ana Hernández de Prueba")
                .cargo("Ejecutiva de Cuentas Clave")
                .rol("ROLE_VENTAS")
                .build();

        UsuarioDto guardado = usuarioService.crear(nuevo);
        assertNotNull(guardado.getId());
        assertEquals(testUser, guardado.getUsername());

        List<UsuarioDto> busqueda = usuarioService.listarOBuscar("Hernández de Prueba");
        assertFalse(busqueda.isEmpty());
    }

    @Test
    void testEquiposCatalogYBusquedaInteractiva() {
        EquipoDto nuevoEquipo = EquipoDto.builder()
                .descripcion("LECTOR OMNIDIRECCIONAL HONEYWELL ORBIT 7120")
                .partNumber("MK7120-31A38")
                .caracteristicas("• Patrón de lectura de 20 líneas omnidireccional\n• Velocidad de 1,120 lecturas por segundo\n• Conexión USB")
                .precioReferencial(new BigDecimal("210.00"))
                .tiempoEntregaPredeterminado("De 2 a 3 semanas")
                .categoria("Lectores")
                .build();

        EquipoDto guardado = equipoService.crearOActualizar(nuevoEquipo);
        assertNotNull(guardado.getId());
        assertEquals("MK7120-31A38", guardado.getPartNumber());

        // Búsqueda por descripción
        List<EquipoDto> busquedaPorDesc = equipoService.listarOBuscar("ORBIT");
        assertFalse(busquedaPorDesc.isEmpty());
        assertEquals("MK7120-31A38", busquedaPorDesc.get(0).getPartNumber());

        // Búsqueda por Part Number
        List<EquipoDto> busquedaPorPN = equipoService.listarOBuscar("7120");
        assertFalse(busquedaPorPN.isEmpty());

        // Búsqueda por palabra clave dentro de características
        List<EquipoDto> busquedaPorCaract = equipoService.listarOBuscar("omnidireccional");
        assertFalse(busquedaPorCaract.isEmpty());

        // Verificación de consultas paginadas
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<EquipoDto> pageEquipos = equipoService.listarOBuscarPaginado("ORBIT", null, null, pageable);
        assertNotNull(pageEquipos);
        assertFalse(pageEquipos.isEmpty());
        assertEquals(1, pageEquipos.getTotalElements());

        org.springframework.data.domain.Page<ClienteDto> pageClientes = clienteService.listarOBuscarPaginado(null, null, null, pageable);
        assertNotNull(pageClientes);
        assertFalse(pageClientes.isEmpty());

        org.springframework.data.domain.Page<UsuarioDto> pageUsuarios = usuarioService.listarOBuscarPaginado(null, null, false, pageable);
        assertNotNull(pageUsuarios);
        assertFalse(pageUsuarios.isEmpty());
    }
}
