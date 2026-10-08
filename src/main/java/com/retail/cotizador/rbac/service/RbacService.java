package com.retail.cotizador.rbac.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.cotizador.rbac.dto.PermisoDefinicionDto;
import com.retail.cotizador.rbac.dto.RbacMatrizDto;
import com.retail.cotizador.rbac.dto.RolPermisosDto;
import com.retail.cotizador.rbac.entity.RolPermisoConfig;
import com.retail.cotizador.rbac.repository.RolPermisoConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RbacService {

    private final RolPermisoConfigRepository rolPermisoConfigRepository;
    private final ObjectMapper objectMapper;

    // Catálogo estático de permisos disponibles en el sistema
    private static final List<PermisoDefinicionDto> CATALOGO_PERMISOS = List.of(
            // 1. Módulo Empresas (Tenants)
            new PermisoDefinicionDto("EMPRESAS_VER", "Ver Empresas", "Consultar empresas registradas en la plataforma", "Empresas (Tenants)"),
            new PermisoDefinicionDto("EMPRESAS_CREAR", "Crear Empresas", "Registrar nuevas empresas clientes en el SaaS", "Empresas (Tenants)"),
            new PermisoDefinicionDto("EMPRESAS_EDITAR", "Editar Empresas", "Modificar razón social, NIT y datos de empresas", "Empresas (Tenants)"),
            new PermisoDefinicionDto("EMPRESAS_ELIMINAR", "Eliminar Empresas", "Desactivar o eliminar empresas clientes", "Empresas (Tenants)"),

            // 2. Módulo Sucursales & Sedes
            new PermisoDefinicionDto("SUCURSALES_VER", "Ver Sucursales", "Visualizar sedes y datos generales", "Sucursales"),
            new PermisoDefinicionDto("SUCURSALES_CREAR", "Crear Nuevas Sucursales", "Abrir y registrar nuevas sedes para la empresa", "Sucursales"),
            new PermisoDefinicionDto("SUCURSALES_EDITAR", "Editar Sucursales", "Modificar datos fiscales, membretes, firmas y notas", "Sucursales"),
            new PermisoDefinicionDto("SUCURSALES_ELIMINAR", "Eliminar Sucursales", "Desactivar sedes de la empresa", "Sucursales"),

            // 3. Módulo Usuarios & Personal
            new PermisoDefinicionDto("USUARIOS_VER", "Ver Usuarios", "Ver listado de personal y credenciales", "Usuarios"),
            new PermisoDefinicionDto("USUARIOS_CREAR_GERENTE", "Crear Gerentes de Sucursal", "Designar encargados de sucursal", "Usuarios"),
            new PermisoDefinicionDto("USUARIOS_CREAR_VENTAS", "Crear Ejecutivos de Ventas", "Crear usuarios vendedores para la sede", "Usuarios"),
            new PermisoDefinicionDto("USUARIOS_EDITAR", "Editar Usuarios", "Modificar datos y restablecer contraseñas", "Usuarios"),
            new PermisoDefinicionDto("USUARIOS_ELIMINAR", "Eliminar Usuarios", "Desactivar accesos de colaboradores", "Usuarios"),

            // 4. Módulo Catálogos Maestros
            new PermisoDefinicionDto("CLIENTES_VER", "Ver Clientes", "Consultar la cartera de clientes", "Catálogos"),
            new PermisoDefinicionDto("CLIENTES_CREAR_EDITAR", "Crear y Editar Clientes", "Registrar y actualizar clientes", "Catálogos"),
            new PermisoDefinicionDto("CLIENTES_ELIMINAR", "Eliminar Clientes", "Eliminar clientes del catálogo", "Catálogos"),
            new PermisoDefinicionDto("CLIENTES_IMPORTAR", "Importar Clientes CSV", "Carga masiva de cartera de clientes", "Catálogos"),
            new PermisoDefinicionDto("EQUIPOS_VER", "Ver Equipos / Items", "Consultar catálogo de equipos y precios", "Catálogos"),
            new PermisoDefinicionDto("EQUIPOS_CREAR_EDITAR", "Crear y Editar Equipos", "Registrar productos y especificaciones", "Catálogos"),
            new PermisoDefinicionDto("EQUIPOS_ELIMINAR", "Eliminar Equipos", "Eliminar productos del catálogo", "Catálogos"),
            new PermisoDefinicionDto("EQUIPOS_IMPORTAR", "Importar Equipos CSV", "Carga masiva de productos vía CSV", "Catálogos"),

            // 5. Módulo Cotizaciones
            new PermisoDefinicionDto("COTIZACIONES_VER_TODAS", "Ver Todas las Cotizaciones", "Ver cotizaciones de todas las sucursales de la empresa", "Cotizaciones"),
            new PermisoDefinicionDto("COTIZACIONES_VER_PROPIA", "Ver Cotizaciones Sede Propia", "Ver cotizaciones de la sucursal asignada", "Cotizaciones"),
            new PermisoDefinicionDto("COTIZACIONES_CREAR", "Generar Nuevas Cotizaciones", "Emitir cotizaciones con cálculo automático de IVA", "Cotizaciones"),
            new PermisoDefinicionDto("COTIZACIONES_DESCARGAR_PDF", "Descargar PDFs Membretados", "Previsualizar y exportar cotizaciones oficiales en PDF", "Cotizaciones"),

            // 6. Módulo Seguridad y RBAC
            new PermisoDefinicionDto("RBAC_GESTIONAR", "Administrar Roles & RBAC", "Configurar matriz de permisos interactiva", "Seguridad & RBAC")
    );

    // Mapeo de presets predeterminados
    public static final Map<String, List<String>> DEFAULT_PERMISOS = Map.of(
            "ROLE_ADMIN", List.of(
                    "EMPRESAS_VER", "EMPRESAS_CREAR", "EMPRESAS_EDITAR", "EMPRESAS_ELIMINAR",
                    "SUCURSALES_VER", "SUCURSALES_CREAR", "SUCURSALES_EDITAR", "SUCURSALES_ELIMINAR",
                    "USUARIOS_VER", "USUARIOS_CREAR_GERENTE", "USUARIOS_CREAR_VENTAS", "USUARIOS_EDITAR", "USUARIOS_ELIMINAR",
                    "CLIENTES_VER", "CLIENTES_CREAR_EDITAR", "CLIENTES_ELIMINAR", "CLIENTES_IMPORTAR",
                    "EQUIPOS_VER", "EQUIPOS_CREAR_EDITAR", "EQUIPOS_ELIMINAR", "EQUIPOS_IMPORTAR",
                    "COTIZACIONES_VER_TODAS", "COTIZACIONES_VER_PROPIA", "COTIZACIONES_CREAR", "COTIZACIONES_DESCARGAR_PDF",
                    "RBAC_GESTIONAR"
            ),
            "ROLE_GERENTE_GENERAL", List.of(
                    "SUCURSALES_VER", "SUCURSALES_CREAR", "SUCURSALES_EDITAR", "SUCURSALES_ELIMINAR",
                    "USUARIOS_VER", "USUARIOS_CREAR_GERENTE", "USUARIOS_CREAR_VENTAS", "USUARIOS_EDITAR", "USUARIOS_ELIMINAR",
                    "CLIENTES_VER", "CLIENTES_CREAR_EDITAR", "CLIENTES_ELIMINAR", "CLIENTES_IMPORTAR",
                    "EQUIPOS_VER", "EQUIPOS_CREAR_EDITAR", "EQUIPOS_ELIMINAR", "EQUIPOS_IMPORTAR",
                    "COTIZACIONES_VER_TODAS", "COTIZACIONES_VER_PROPIA", "COTIZACIONES_CREAR", "COTIZACIONES_DESCARGAR_PDF"
            ),
            "ROLE_GERENTE_SUCURSAL", List.of(
                    "SUCURSALES_VER", "SUCURSALES_EDITAR",
                    "USUARIOS_VER", "USUARIOS_CREAR_VENTAS", "USUARIOS_EDITAR",
                    "CLIENTES_VER", "CLIENTES_CREAR_EDITAR", "CLIENTES_IMPORTAR",
                    "EQUIPOS_VER", "EQUIPOS_CREAR_EDITAR", "EQUIPOS_IMPORTAR",
                    "COTIZACIONES_VER_PROPIA", "COTIZACIONES_CREAR", "COTIZACIONES_DESCARGAR_PDF"
            ),
            "ROLE_VENTAS", List.of(
                    "CLIENTES_VER",
                    "EQUIPOS_VER",
                    "COTIZACIONES_VER_PROPIA", "COTIZACIONES_CREAR", "COTIZACIONES_DESCARGAR_PDF"
            )
    );

    private static final Map<String, String> NOMBRES_ROLES = Map.of(
            "ROLE_ADMIN", "Administrador Global SaaS",
            "ROLE_GERENTE_GENERAL", "Gerente General (Empresa)",
            "ROLE_GERENTE_SUCURSAL", "Gerente de Sucursal (Sede)",
            "ROLE_VENTAS", "Vendedor / Emisor Final"
    );

    private static final Map<String, String> DESCRIPCIONES_ROLES = Map.of(
            "ROLE_ADMIN", "Control root total de la plataforma multi-tenant y administración de empresas",
            "ROLE_GERENTE_GENERAL", "Dueño de la empresa: gestiona todas las sucursales, catálogos y personal de su organización",
            "ROLE_GERENTE_SUCURSAL", "Encargado de sede: administra su sucursal, catálogos y crea vendedores para su sede",
            "ROLE_VENTAS", "Colaborador operativo: consulta catálogos y genera cotizaciones en su sucursal asignada"
    );

    @Transactional
    public void inicializarPermisosSiNoExisten() {
        for (String rol : List.of("ROLE_ADMIN", "ROLE_GERENTE_GENERAL", "ROLE_GERENTE_SUCURSAL", "ROLE_VENTAS")) {
            if (rolPermisoConfigRepository.findByRol(rol).isEmpty()) {
                List<String> permisos = DEFAULT_PERMISOS.getOrDefault(rol, Collections.emptyList());
                try {
                    String json = objectMapper.writeValueAsString(permisos);
                    rolPermisoConfigRepository.save(RolPermisoConfig.builder()
                            .rol(rol)
                            .permisosJson(json)
                            .build());
                    log.info("Inicializada configuración RBAC para rol: {}", rol);
                } catch (Exception e) {
                    log.error("Error al serializar permisos por defecto para {}", rol, e);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public RbacMatrizDto obtenerMatriz() {
        List<String> rolesOrdenados = List.of("ROLE_ADMIN", "ROLE_GERENTE_GENERAL", "ROLE_GERENTE_SUCURSAL", "ROLE_VENTAS");
        List<RolPermisosDto> rolesDtoList = new ArrayList<>();

        for (String rol : rolesOrdenados) {
            List<String> permisos = obtenerPermisosDirectos(rol);
            rolesDtoList.add(RolPermisosDto.builder()
                    .rol(rol)
                    .nombreRol(NOMBRES_ROLES.getOrDefault(rol, rol))
                    .descripcion(DESCRIPCIONES_ROLES.getOrDefault(rol, ""))
                    .permisos(permisos)
                    .build());
        }

        return RbacMatrizDto.builder()
                .catalogoPermisos(CATALOGO_PERMISOS)
                .roles(rolesDtoList)
                .build();
    }

    @Transactional
    public RbacMatrizDto guardarMatriz(RbacMatrizDto matrizDto) {
        if (matrizDto != null && matrizDto.getRoles() != null) {
            for (RolPermisosDto rolDto : matrizDto.getRoles()) {
                String rol = rolDto.getRol();
                List<String> permisos = rolDto.getPermisos() != null ? rolDto.getPermisos() : Collections.emptyList();

                // El admin siempre debe retener permisos críticos de seguridad para no quedar bloqueado
                if ("ROLE_ADMIN".equals(rol) && !permisos.contains("RBAC_GESTIONAR")) {
                    List<String> mutable = new ArrayList<>(permisos);
                    mutable.add("RBAC_GESTIONAR");
                    permisos = mutable;
                }

                try {
                    String json = objectMapper.writeValueAsString(permisos);
                    RolPermisoConfig config = rolPermisoConfigRepository.findByRol(rol)
                            .orElseGet(() -> RolPermisoConfig.builder().rol(rol).build());
                    config.setPermisosJson(json);
                    rolPermisoConfigRepository.save(config);
                } catch (Exception e) {
                    log.error("Error al guardar configuración de permisos para {}", rol, e);
                    throw new RuntimeException("Error al guardar la matriz de permisos: " + e.getMessage());
                }
            }
        }
        return obtenerMatriz();
    }

    @Transactional
    public RbacMatrizDto restablecerDefaults() {
        for (Map.Entry<String, List<String>> entry : DEFAULT_PERMISOS.entrySet()) {
            try {
                String json = objectMapper.writeValueAsString(entry.getValue());
                RolPermisoConfig config = rolPermisoConfigRepository.findByRol(entry.getKey())
                        .orElseGet(() -> RolPermisoConfig.builder().rol(entry.getKey()).build());
                config.setPermisosJson(json);
                rolPermisoConfigRepository.save(config);
            } catch (Exception e) {
                log.error("Error al restablecer permisos para {}", entry.getKey(), e);
            }
        }
        return obtenerMatriz();
    }

    @Transactional(readOnly = true)
    public List<String> obtenerPermisosEfectivosUsuario(String rolUsuario) {
        if (rolUsuario == null) return Collections.emptyList();

        // Normalizar rol
        String rolNormalizado = rolUsuario.trim();
        if (!rolNormalizado.startsWith("ROLE_")) {
            rolNormalizado = "ROLE_" + rolNormalizado;
        }

        // Compatibilidad hacia atrás: ROLE_GERENTE -> ROLE_GERENTE_GENERAL
        if ("ROLE_GERENTE".equals(rolNormalizado)) {
            rolNormalizado = "ROLE_GERENTE_GENERAL";
        }

        // Si es ROLE_ADMIN, tiene todos los permisos
        if ("ROLE_ADMIN".equals(rolNormalizado)) {
            return CATALOGO_PERMISOS.stream().map(PermisoDefinicionDto::getCodigo).toList();
        }

        Set<String> permisosAcumulados = new HashSet<>(obtenerPermisosDirectos(rolNormalizado));

        // Jerarquía en cascada: ROLE_GERENTE_GENERAL hereda de ROLE_GERENTE_SUCURSAL y ROLE_VENTAS
        if ("ROLE_GERENTE_GENERAL".equals(rolNormalizado)) {
            permisosAcumulados.addAll(obtenerPermisosDirectos("ROLE_GERENTE_SUCURSAL"));
            permisosAcumulados.addAll(obtenerPermisosDirectos("ROLE_VENTAS"));
        } else if ("ROLE_GERENTE_SUCURSAL".equals(rolNormalizado)) {
            permisosAcumulados.addAll(obtenerPermisosDirectos("ROLE_VENTAS"));
        }

        return new ArrayList<>(permisosAcumulados);
    }

    public List<String> obtenerPermisosDirectos(String rol) {
        return rolPermisoConfigRepository.findByRol(rol)
                .map(config -> {
                    try {
                        return objectMapper.readValue(config.getPermisosJson(), new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        log.warn("No se pudo parsear permisos para {}: {}", rol, e.getMessage());
                        return DEFAULT_PERMISOS.getOrDefault(rol, Collections.emptyList());
                    }
                })
                .orElseGet(() -> DEFAULT_PERMISOS.getOrDefault(rol, Collections.emptyList()));
    }
}
