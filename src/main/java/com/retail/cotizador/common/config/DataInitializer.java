package com.retail.cotizador.common.config;

import com.retail.cotizador.clientes.entity.Cliente;
import com.retail.cotizador.clientes.repository.ClienteRepository;
import com.retail.cotizador.equipos.entity.Equipo;
import com.retail.cotizador.equipos.repository.EquipoRepository;
import com.retail.cotizador.sucursales.entity.Sucursal;
import com.retail.cotizador.sucursales.repository.SucursalRepository;
import com.retail.cotizador.usuarios.entity.Usuario;
import com.retail.cotizador.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        inicializarSucursales();
        inicializarUsuarios();
        inicializarClientes();
        inicializarEquipos();
        asegurarSucursalEnRegistrosExistentes();
    }

    private void inicializarSucursales() {
        if (sucursalRepository.count() == 0) {
            log.info("Cargando sucursales iniciales...");
            Sucursal sucursal1 = Sucursal.builder()
                    .codigo("SUC_SV")
                    .nombre("Retail El Salvador (Matriz)")
                    .razonSocial("RETAIL SERVICES EL SALVADOR S.A. DE C.V.")
                    .nombreComercial("RETAIL EL SALVADOR")
                    .direccion("Calle Los Bambúes, Col. San Benito, San Salvador")
                    .telefono("2250-7700")
                    .correo("contacto.sv@retail.com.sv")
                    .prefijoCotizacion("COT")
                    .nombreFirmante("ING. ERICK RAMIREZ")
                    .cargoFirmante("GERENTE GENERAL")
                    .formaPagoPredeterminada("Crédito 30 días, Transferencia Bancaria o Cheque")
                    .notaPredeterminada("** IMPORTANTE ** Tiempos de entrega y precios, podrían estar sujetos a cambios en inventario")
                    .activo(true)
                    .build();

            Sucursal sucursal2 = Sucursal.builder()
                    .codigo("SUC_02")
                    .nombre("Retail Sucursal 2 (Occidente)")
                    .razonSocial("RETAIL SERVICES DE OCCIDENTE S.A. DE C.V.")
                    .nombreComercial("RETAIL OCCIDENTE")
                    .direccion("Av. Independencia Sur #45, Santa Ana")
                    .telefono("2440-1100")
                    .correo("ventas.occidente@retail.com.sv")
                    .prefijoCotizacion("COT2")
                    .nombreFirmante("LIC. CARLOS VALENCIA")
                    .cargoFirmante("GERENTE DE SUCURSAL")
                    .formaPagoPredeterminada("Contado contra entrega / Transferencia Bancaria")
                    .notaPredeterminada("** IMPORTANTE ** Precios sujetos a confirmación de stock local en Santa Ana")
                    .activo(true)
                    .build();

            sucursalRepository.saveAll(List.of(sucursal1, sucursal2));
        }
    }

    private void inicializarUsuarios() {
        log.info("Verificando y asegurando credenciales y sucursales de usuarios...");

        // 1. Asegurar Admin Matriz (ERAMIREZ)
        usuarioRepository.findByUsername("ERAMIREZ").ifPresentOrElse(u -> {
            boolean mod = false;
            if (u.getPassword() == null || !u.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode("admin123"));
                mod = true;
            }
            if (u.getSucursalId() == null) {
                u.setSucursalId(1L);
                mod = true;
            }
            if (!"ROLE_ADMIN".equals(u.getRol())) {
                u.setRol("ROLE_ADMIN");
                mod = true;
            }
            if (mod) usuarioRepository.save(u);
        }, () -> {
            usuarioRepository.save(Usuario.builder()
                    .username("ERAMIREZ")
                    .password(passwordEncoder.encode("admin123"))
                    .nombreCompleto("Ing. Erick Ramírez")
                    .cargo("Gerente General")
                    .correo("erick.ramirez@retail.com.gt")
                    .rol("ROLE_ADMIN")
                    .sucursalId(1L)
                    .activo(true)
                    .build());
        });

        // 2. Asegurar Gerente Sucursal 2 (CVALENCIA)
        usuarioRepository.findByUsername("CVALENCIA").ifPresentOrElse(u -> {
            boolean mod = false;
            if (u.getPassword() == null || !u.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode("gerente123"));
                mod = true;
            }
            if (u.getSucursalId() == null) {
                u.setSucursalId(2L);
                mod = true;
            }
            if (!"ROLE_GERENTE".equals(u.getRol())) {
                u.setRol("ROLE_GERENTE");
                mod = true;
            }
            if (mod) usuarioRepository.save(u);
        }, () -> {
            usuarioRepository.save(Usuario.builder()
                    .username("CVALENCIA")
                    .password(passwordEncoder.encode("gerente123"))
                    .nombreCompleto("Lic. Carlos Valencia")
                    .cargo("Gerente de Sucursal")
                    .correo("carlos.valencia@retail.com.sv")
                    .rol("ROLE_GERENTE")
                    .sucursalId(2L)
                    .activo(true)
                    .build());
        });

        // 3. Asegurar Ejecutivo Ventas Matriz (VENTASSR001)
        usuarioRepository.findByUsername("VENTASSR001").ifPresentOrElse(u -> {
            boolean mod = false;
            if (u.getPassword() == null || !u.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode("ventas123"));
                mod = true;
            }
            if (u.getSucursalId() == null) {
                u.setSucursalId(1L);
                mod = true;
            }
            if (mod) usuarioRepository.save(u);
        }, () -> {
            usuarioRepository.save(Usuario.builder()
                    .username("VENTASSR001")
                    .password(passwordEncoder.encode("ventas123"))
                    .nombreCompleto("Ejecutivo Ventas Retail SV")
                    .cargo("Ejecutivo de Ventas")
                    .correo("ventas.sv@retail.com.gt")
                    .rol("ROLE_VENTAS")
                    .sucursalId(1L)
                    .activo(true)
                    .build());
        });

        // 4. Asegurar Ejecutivo Ventas Sucursal 2 (VENTASOCC01)
        usuarioRepository.findByUsername("VENTASOCC01").ifPresentOrElse(u -> {
            boolean mod = false;
            if (u.getPassword() == null || !u.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode("ventas123"));
                mod = true;
            }
            if (u.getSucursalId() == null) {
                u.setSucursalId(2L);
                mod = true;
            }
            if (mod) usuarioRepository.save(u);
        }, () -> {
            usuarioRepository.save(Usuario.builder()
                    .username("VENTASOCC01")
                    .password(passwordEncoder.encode("ventas123"))
                    .nombreCompleto("Ejecutivo Ventas Occidente")
                    .cargo("Ejecutivo de Ventas")
                    .correo("ventas.occidente@retail.com.sv")
                    .rol("ROLE_VENTAS")
                    .sucursalId(2L)
                    .activo(true)
                    .build());
        });

        // 5. Asegurar cualquier otro usuario existente
        usuarioRepository.findAll().forEach(u -> {
            boolean mod = false;
            if (u.getPassword() == null || !u.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode("123456"));
                mod = true;
            }
            if (u.getSucursalId() == null) {
                u.setSucursalId(1L);
                mod = true;
            }
            if (mod) usuarioRepository.save(u);
        });
    }

    private void inicializarClientes() {
        boolean sucursal1TieneClientes = clienteRepository.findAll().stream()
                .anyMatch(c -> Long.valueOf(1L).equals(c.getSucursalId()));
        if (!sucursal1TieneClientes) {
            log.info("Cargando clientes iniciales para Sucursal 1 (Matriz)...");
            clienteRepository.saveAll(List.of(
                    Cliente.builder()
                            .razonSocial("ALIMENTOS Y TURISMO SA DE CV")
                            .nombreComercial("PREMIUM RESTAURANTS")
                            .contactoPrincipal("Tania Argueta")
                            .telefono("2263-0000")
                            .correo("compras@premiumrestaurants.com")
                            .direccion("San Salvador, El Salvador")
                            .sucursalId(1L)
                            .activo(true)
                            .build(),
                    Cliente.builder()
                            .razonSocial("SUPERMERCADOS DE CENTROAMERICA SA DE CV")
                            .nombreComercial("SUPER SELECTOS")
                            .contactoPrincipal("Carlos Mendoza")
                            .telefono("2288-5500")
                            .correo("adquisiciones@selectos.com.sv")
                            .direccion("Antiguo Cuscatlán, La Libertad")
                            .sucursalId(1L)
                            .activo(true)
                            .build(),
                    Cliente.builder()
                            .razonSocial("DISTRIBUIDORA FARMACEUTICA SV SA DE CV")
                            .nombreComercial("FARMACIAS ECONOMICAS")
                            .contactoPrincipal("Lic. Roberto Gómez")
                            .telefono("2244-1122")
                            .correo("compras@farmaciaseconomicas.com.sv")
                            .direccion("San Salvador, El Salvador")
                            .sucursalId(1L)
                            .activo(true)
                            .build()
            ));
        }

        boolean sucursal2TieneClientes = clienteRepository.findAll().stream()
                .anyMatch(c -> Long.valueOf(2L).equals(c.getSucursalId()));
        if (!sucursal2TieneClientes) {
            log.info("Cargando clientes iniciales para Sucursal 2 (Occidente)...");
            clienteRepository.saveAll(List.of(
                    Cliente.builder()
                            .razonSocial("AGROINDUSTRIAS DE OCCIDENTE SA DE CV")
                            .nombreComercial("AGRO OCCIDENTE")
                            .contactoPrincipal("Ing. Roberto Salguero")
                            .telefono("2447-8899")
                            .correo("compras@agrooccidente.com.sv")
                            .direccion("Santa Ana, El Salvador")
                            .sucursalId(2L)
                            .activo(true)
                            .build(),
                    Cliente.builder()
                            .razonSocial("BENEFICIO EL MOLINO SA DE CV")
                            .nombreComercial("CAFES DE SANTA ANA")
                            .contactoPrincipal("Licda. Claudia Méndez")
                            .telefono("2440-5522")
                            .correo("adquisiciones@elmolino.com.sv")
                            .direccion("Carretera a Metapán Km 66, Santa Ana")
                            .sucursalId(2L)
                            .activo(true)
                            .build(),
                    Cliente.builder()
                            .razonSocial("HOTEL Y RESTAURANTE CASABLANCA SA DE CV")
                            .nombreComercial("HOTEL CASABLANCA OCCIDENTE")
                            .contactoPrincipal("Arq. Fernando Castaneda")
                            .telefono("2441-9900")
                            .correo("gerencia@hotelcasablanca.sv")
                            .direccion("Boulevard Los 44, Santa Ana")
                            .sucursalId(2L)
                            .activo(true)
                            .build()
            ));
        }
    }

    private void inicializarEquipos() {
        boolean sucursal1TieneEquipos = equipoRepository.findAll().stream()
                .anyMatch(e -> Long.valueOf(1L).equals(e.getSucursalId()));
        if (!sucursal1TieneEquipos) {
            log.info("Cargando catálogo maestro inicial de equipos para Sucursal 1 (Matriz)...");
            equipoRepository.saveAll(List.of(
                    Equipo.builder()
                            .descripcion("IMPRESORA DE TARJETAS ZEBRA ZC300 DUAL")
                            .partNumber("ZCD-800300-250LA")
                            .caracteristicas("• Sublimación directa a tarjeta\n• Impresión doble cara automática\n• Resolución 300 dpi\n• Conectividad USB y Ethernet")
                            .precioReferencial(new BigDecimal("1250.00"))
                            .tiempoEntregaPredeterminado("De 5 a 6 semanas")
                            .categoria("Impresoras de Tarjetas")
                            .sucursalId(1L)
                            .activo(true)
                            .build(),
                    Equipo.builder()
                            .descripcion("IMPRESORA TERMICA INDUSTRIAL ZEBRA ZT411")
                            .partNumber("ZT41142-T010000Z")
                            .caracteristicas("• Impresión térmica directa y transferencia térmica\n• Ancho de impresión 4 pulgadas\n• Resolución 203 dpi\n• Pantalla táctil a color de 4.3 pulgadas\n• Puertos USB, Serial, Ethernet y Bluetooth")
                            .precioReferencial(new BigDecimal("1650.00"))
                            .tiempoEntregaPredeterminado("De 4 a 5 semanas")
                            .categoria("Impresoras Térmicas")
                            .sucursalId(1L)
                            .activo(true)
                            .build(),
                    Equipo.builder()
                            .descripcion("TERMINAL PUNTO DE VENTA TOUCH ELO 15E3")
                            .partNumber("E001463")
                            .caracteristicas("• Pantalla táctil de 15.6 pulgadas antirreflejo\n• Procesador Intel Celeron J1900 Quad-Core\n• Memoria RAM 4GB DDR3L\n• Disco de estado sólido 128GB SSD\n• Sellado contra derrames y polvo")
                            .precioReferencial(new BigDecimal("890.00"))
                            .tiempoEntregaPredeterminado("De 3 a 4 semanas")
                            .categoria("Terminales POS")
                            .sucursalId(1L)
                            .activo(true)
                            .build(),
                    Equipo.builder()
                            .descripcion("LECTOR DE CODIGO DE BARRAS 2D DATALOGIC QUICKSCAN QD2500")
                            .partNumber("QD2590-BKK1S")
                            .caracteristicas("• Tecnología de lectura óptica 2D Imager\n• Lectura de códigos 1D y 2D en pantallas de celulares\n• Puntero LED azul de alta precisión\n• Conectividad USB Plug & Play")
                            .precioReferencial(new BigDecimal("185.00"))
                            .tiempoEntregaPredeterminado("Stock Inmediato")
                            .categoria("Lectores de Código de Barras")
                            .sucursalId(1L)
                            .activo(true)
                            .build(),
                    Equipo.builder()
                            .descripcion("COMPUTADORA MOVIL INDUSTRIAL HONEYWELL SCANPAL EDA52")
                            .partNumber("EDA52-111-E2101RK")
                            .caracteristicas("• Sistema Operativo Android 11 actualizable\n• Pantalla táctil Gorilla Glass de 5.5 pulgadas\n• Motor de escaneo 2D Honeywell S0703 integrado\n• Batería de 4500 mAh de larga duración\n• Conectividad Wi-Fi, 4G LTE y Bluetooth 5.1")
                            .precioReferencial(new BigDecimal("750.00"))
                            .tiempoEntregaPredeterminado("De 4 a 6 semanas")
                            .categoria("Computadoras Móviles")
                            .sucursalId(1L)
                            .activo(true)
                            .build()
            ));
        }

        boolean sucursal2TieneEquipos = equipoRepository.findAll().stream()
                .anyMatch(e -> Long.valueOf(2L).equals(e.getSucursalId()));
        if (!sucursal2TieneEquipos) {
            log.info("Cargando catálogo maestro inicial de equipos para Sucursal 2 (Occidente)...");
            equipoRepository.saveAll(List.of(
                    Equipo.builder()
                            .descripcion("IMPRESORA FISCAL EPSON TM-T88VI DUAL OCCIDENTE")
                            .partNumber("C31CE94061")
                            .caracteristicas("• Impresión térmica de recibos de alta velocidad (350 mm/s)\n• Conexión simultánea USB, Ethernet y Serial\n• Corte automático de papel")
                            .precioReferencial(new BigDecimal("340.00"))
                            .tiempoEntregaPredeterminado("Stock Local Inmediato")
                            .categoria("Impresoras Térmicas")
                            .sucursalId(2L)
                            .activo(true)
                            .build(),
                    Equipo.builder()
                            .descripcion("LECTOR CODIGO DE BARRAS ZEBRA DS2208 OCCIDENTE")
                            .partNumber("DS2208-SR7U2100AZW")
                            .caracteristicas("• Escáner de mano 1D/2D con cable USB\n• Rango de lectura omnidireccional\n• Tolerancia a caídas de 1.5 m sobre concreto")
                            .precioReferencial(new BigDecimal("145.00"))
                            .tiempoEntregaPredeterminado("Stock Inmediato Santa Ana")
                            .categoria("Lectores de Código de Barras")
                            .sucursalId(2L)
                            .activo(true)
                            .build(),
                    Equipo.builder()
                            .descripcion("TERMINAL POS TACTIL TOUCH DYNAPOS 15 PULGADAS")
                            .partNumber("DYNA-POS-15-J19")
                            .caracteristicas("• Pantalla táctil capacitiva 15.6 pulgadas\n• Procesador Intel Celeron J1900\n• 4GB RAM DDR3L / 128GB SSD")
                            .precioReferencial(new BigDecimal("780.00"))
                            .tiempoEntregaPredeterminado("De 2 a 3 semanas")
                            .categoria("Terminales POS")
                            .sucursalId(2L)
                            .activo(true)
                            .build()
            ));
        }
    }

    private void asegurarSucursalEnRegistrosExistentes() {
        clienteRepository.findAll().forEach(c -> {
            if (c.getSucursalId() == null) {
                c.setSucursalId(1L);
                clienteRepository.save(c);
            }
        });
        equipoRepository.findAll().forEach(e -> {
            if (e.getSucursalId() == null) {
                e.setSucursalId(1L);
                equipoRepository.save(e);
            }
        });
    }
}
