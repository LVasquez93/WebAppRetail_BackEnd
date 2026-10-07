package com.retail.cotizador.cotizaciones.service;

import com.retail.cotizador.common.exception.ResourceNotFoundException;
import com.retail.cotizador.common.util.NumeroALetrasUtil;
import com.retail.cotizador.cotizaciones.dto.CotizacionRequestDto;
import com.retail.cotizador.cotizaciones.dto.CotizacionResponseDto;
import com.retail.cotizador.cotizaciones.dto.ItemDto;
import com.retail.cotizador.cotizaciones.entity.Cotizacion;
import com.retail.cotizador.cotizaciones.entity.ItemCotizacion;
import com.retail.cotizador.cotizaciones.repository.CotizacionRepository;
import com.retail.cotizador.empresas.entity.Empresa;
import com.retail.cotizador.empresas.repository.EmpresaRepository;
import com.retail.cotizador.sucursales.entity.Sucursal;
import com.retail.cotizador.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public CotizacionResponseDto crearCotizacion(CotizacionRequestDto dto) {
        Cotizacion cotizacion = mapearEntidadTemporal(dto);

        // Identificar empresa según DTO o sucursal
        Long empresaId = dto.getEmpresaId();
        if (empresaId == null && cotizacion.getSucursalId() != null) {
            empresaId = sucursalRepository.findById(cotizacion.getSucursalId())
                    .map(Sucursal::getEmpresaId)
                    .orElse(null);
        }
        cotizacion.setEmpresaId(empresaId);

        // Identificar prefijo según sucursal
        String prefijo = "COT";
        if (cotizacion.getSucursalId() != null) {
            prefijo = sucursalRepository.findById(cotizacion.getSucursalId())
                    .map(Sucursal::getPrefijoCotizacion)
                    .orElse("COT");
        }

        // Siempre generamos el código oficial correlativo con formato: Prefijo + AAAA + MM + 3 dígitos
        cotizacion.setCodigoCotizacion(generarCodigoCotizacion(cotizacion.getFechaEmision(), prefijo));
        
        cotizacion.setTotalEnLetras(NumeroALetrasUtil.convertir(cotizacion.getTotalInversion()));

        Cotizacion savedCotizacion = cotizacionRepository.save(cotizacion);
        return mapearADto(savedCotizacion);
    }

    @Transactional(readOnly = true)
    public Page<CotizacionResponseDto> listarCotizaciones(Pageable pageable) {
        return listarCotizaciones(pageable, null, null);
    }

    @Transactional(readOnly = true)
    public Page<CotizacionResponseDto> listarCotizaciones(Pageable pageable, Long sucursalId) {
        return listarCotizaciones(pageable, null, sucursalId);
    }

    @Transactional(readOnly = true)
    public Page<CotizacionResponseDto> listarCotizaciones(Pageable pageable, Long empresaId, Long sucursalId) {
        return cotizacionRepository.filtrarCotizaciones(empresaId, sucursalId, pageable).map(this::mapearADto);
    }

    @Transactional(readOnly = true)
    public CotizacionResponseDto obtenerPorId(Long id) {
        return mapearADto(obtenerEntidadPorId(id));
    }

    @Transactional(readOnly = true)
    public Cotizacion obtenerEntidadPorId(Long id) {
        return cotizacionRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotizacion", "id", id));
    }

    public Cotizacion mapearEntidadTemporal(CotizacionRequestDto dto) {
        LocalDate fecha = dto.getFechaEmision() != null ? dto.getFechaEmision() : LocalDate.now();
        Long sucursalId = dto.getSucursalId() != null ? dto.getSucursalId() : 1L;

        Long empresaId = dto.getEmpresaId();
        if (empresaId == null && sucursalId != null) {
            empresaId = sucursalRepository.findById(sucursalId)
                    .map(Sucursal::getEmpresaId)
                    .orElse(null);
        }

        String prefijo = "COT";
        if (sucursalId != null) {
            prefijo = sucursalRepository.findById(sucursalId)
                    .map(Sucursal::getPrefijoCotizacion)
                    .orElse("COT");
        }

        String codigo = dto.getCodigoCotizacion();
        if (codigo == null || codigo.isBlank() || codigo.startsWith("#") || codigo.contains("PRE")) {
            codigo = prefijo + fecha.format(DateTimeFormatter.ofPattern("yyyyMM")) + "--- (PREVIA)";
        }

        String contacto = (dto.getContactoCliente() != null && !dto.getContactoCliente().isBlank())
                ? dto.getContactoCliente() : "(Nombre del Contacto)";
        String razonSocial = (dto.getRazonSocialCliente() != null && !dto.getRazonSocialCliente().isBlank())
                ? dto.getRazonSocialCliente() : "(Empresa / Razón Social)";
        String usuario = (dto.getUsuarioEmisor() != null && !dto.getUsuarioEmisor().isBlank())
                ? dto.getUsuarioEmisor() : "VENTASSR001";
        String formaPago = (dto.getFormaPago() != null && !dto.getFormaPago().isBlank())
                ? dto.getFormaPago() : "Crédito 30 días, Transferencia Bancaria o Cheque";

        BigDecimal subtotal = dto.getSubtotalSinIva() != null ? dto.getSubtotalSinIva() : java.math.BigDecimal.ZERO;
        BigDecimal iva = dto.getMontoIva() != null ? dto.getMontoIva() : java.math.BigDecimal.ZERO;
        BigDecimal total = dto.getTotalInversion() != null ? dto.getTotalInversion() : java.math.BigDecimal.ZERO;

        Cotizacion cotizacion = Cotizacion.builder()
                .codigoCotizacion(codigo)
                .empresaId(empresaId)
                .sucursalId(sucursalId)
                .usuarioEmisor(usuario)
                .fechaEmision(fecha)
                .contactoCliente(contacto)
                .razonSocialCliente(razonSocial)
                .nombreComercial(dto.getNombreComercial())
                .formaPago(formaPago)
                .notaImportante(dto.getNotaImportante())
                .subtotalSinIva(subtotal)
                .montoIva(iva)
                .totalInversion(total)
                .totalEnLetras(NumeroALetrasUtil.convertir(total))
                .build();

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (int i = 0; i < dto.getItems().size(); i++) {
                ItemDto itemDto = dto.getItems().get(i);
                String desc = (itemDto.getDescripcionEquipo() != null && !itemDto.getDescripcionEquipo().isBlank())
                        ? itemDto.getDescripcionEquipo() : "(Descripción del equipo / servicio)";
                String entrega = (itemDto.getTiempoEntrega() != null && !itemDto.getTiempoEntrega().isBlank())
                        ? itemDto.getTiempoEntrega() : "De 5 a 6 semanas";
                int cant = itemDto.getCantidad() != null && itemDto.getCantidad() > 0 ? itemDto.getCantidad() : 1;
                BigDecimal precio = itemDto.getPrecioUnitario() != null ? itemDto.getPrecioUnitario() : java.math.BigDecimal.ZERO;
                BigDecimal totalLinea = itemDto.getTotalLinea() != null ? itemDto.getTotalLinea() : precio.multiply(java.math.BigDecimal.valueOf(cant));

                ItemCotizacion item = ItemCotizacion.builder()
                        .itemNumero(i + 1)
                        .descripcionEquipo(desc)
                        .partNumber(itemDto.getPartNumber())
                        .caracteristicas(itemDto.getCaracteristicas())
                        .tiempoEntrega(entrega)
                        .cantidad(cant)
                        .precioUnitario(precio)
                        .totalLinea(totalLinea)
                        .build();
                cotizacion.addItem(item);
            }
        } else {
            ItemCotizacion defaultItem = ItemCotizacion.builder()
                    .itemNumero(1)
                    .descripcionEquipo("(Descripción del equipo / servicio)")
                    .partNumber(null)
                    .caracteristicas(null)
                    .tiempoEntrega("De 5 a 6 semanas")
                    .cantidad(1)
                    .precioUnitario(java.math.BigDecimal.ZERO)
                    .totalLinea(java.math.BigDecimal.ZERO)
                    .build();
            cotizacion.addItem(defaultItem);
        }
        return cotizacion;
    }

    private String generarCodigoCotizacion(LocalDate fecha, String prefijo) {
        LocalDate f = (fecha != null) ? fecha : LocalDate.now();
        String prefix = (prefijo != null && !prefijo.isBlank() ? prefijo.trim() : "COT")
                + f.format(DateTimeFormatter.ofPattern("yyyyMM"));
        long count = cotizacionRepository.countByCodigoCotizacionStartingWith(prefix);
        long nextSeq = count + 1;
        String code = prefix + String.format("%03d", nextSeq);
        while (cotizacionRepository.findByCodigoCotizacion(code).isPresent()) {
            nextSeq++;
            code = prefix + String.format("%03d", nextSeq);
        }
        return code;
    }

    private CotizacionResponseDto mapearADto(Cotizacion cotizacion) {
        List<ItemDto> itemDtos = cotizacion.getItems().stream().map(item -> ItemDto.builder()
                .id(item.getId())
                .itemNumero(item.getItemNumero())
                .descripcionEquipo(item.getDescripcionEquipo())
                .partNumber(item.getPartNumber())
                .caracteristicas(item.getCaracteristicas())
                .tiempoEntrega(item.getTiempoEntrega())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .totalLinea(item.getTotalLinea())
                .build()
        ).collect(Collectors.toList());

        String nombreEmpresa = null;
        if (cotizacion.getEmpresaId() != null) {
            nombreEmpresa = empresaRepository.findById(cotizacion.getEmpresaId())
                    .map(Empresa::getNombre)
                    .orElse("Empresa #" + cotizacion.getEmpresaId());
        }

        String nombreSucursal = "Matriz";
        if (cotizacion.getSucursalId() != null) {
            nombreSucursal = sucursalRepository.findById(cotizacion.getSucursalId())
                    .map(Sucursal::getNombre)
                    .orElse("Sucursal #" + cotizacion.getSucursalId());
        }

        return CotizacionResponseDto.builder()
                .id(cotizacion.getId())
                .codigoCotizacion(cotizacion.getCodigoCotizacion())
                .empresaId(cotizacion.getEmpresaId())
                .nombreEmpresa(nombreEmpresa)
                .sucursalId(cotizacion.getSucursalId())
                .nombreSucursal(nombreSucursal)
                .usuarioEmisor(cotizacion.getUsuarioEmisor())
                .fechaEmision(cotizacion.getFechaEmision())
                .contactoCliente(cotizacion.getContactoCliente())
                .razonSocialCliente(cotizacion.getRazonSocialCliente())
                .nombreComercial(cotizacion.getNombreComercial())
                .formaPago(cotizacion.getFormaPago())
                .notaImportante(cotizacion.getNotaImportante())
                .subtotalSinIva(cotizacion.getSubtotalSinIva())
                .montoIva(cotizacion.getMontoIva())
                .totalInversion(cotizacion.getTotalInversion())
                .totalEnLetras(cotizacion.getTotalEnLetras())
                .items(itemDtos)
                .fechaCreacion(cotizacion.getFechaCreacion())
                .build();
    }
}
