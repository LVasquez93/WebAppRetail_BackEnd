package com.retail.cotizador.sucursales.service;

import com.retail.cotizador.common.exception.ResourceNotFoundException;
import com.retail.cotizador.sucursales.dto.SucursalDto;
import com.retail.cotizador.sucursales.entity.Sucursal;
import com.retail.cotizador.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<SucursalDto> listarActivas() {
        return listarActivas(null);
    }

    @Transactional(readOnly = true)
    public List<SucursalDto> listarActivas(Long empresaId) {
        List<Sucursal> lista = (empresaId != null)
                ? sucursalRepository.findAllByEmpresaIdAndActivoTrueOrderByIdAsc(empresaId)
                : sucursalRepository.findAllByActivoTrueOrderByIdAsc();
        return lista.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SucursalDto obtenerPorId(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));
        return mapearADto(sucursal);
    }

    @Transactional(readOnly = true)
    public Sucursal obtenerEntidadPorId(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));
    }

    @Transactional
    public SucursalDto crear(SucursalDto dto) {
        Sucursal sucursal = Sucursal.builder()
                .empresaId(dto.getEmpresaId())
                .codigo(dto.getCodigo().trim().toUpperCase())
                .nombre(dto.getNombre().trim())
                .razonSocial(dto.getRazonSocial().trim())
                .nombreComercial(dto.getNombreComercial())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .correo(dto.getCorreo())
                .prefijoCotizacion(dto.getPrefijoCotizacion() != null && !dto.getPrefijoCotizacion().isBlank()
                        ? dto.getPrefijoCotizacion().trim().toUpperCase() : "COT")
                .headerBannerBase64(dto.getHeaderBannerBase64())
                .footerBannerBase64(dto.getFooterBannerBase64())
                .firmaBase64(dto.getFirmaBase64())
                .nombreFirmante(dto.getNombreFirmante() != null ? dto.getNombreFirmante() : "ING. ERICK RAMIREZ")
                .cargoFirmante(dto.getCargoFirmante() != null ? dto.getCargoFirmante() : "GERENTE GENERAL")
                .formaPagoPredeterminada(dto.getFormaPagoPredeterminada())
                .notaPredeterminada(dto.getNotaPredeterminada())
                .porcentajeIva(dto.getPorcentajeIva() != null ? dto.getPorcentajeIva() : new java.math.BigDecimal("13.00"))
                .monedaCodigo(dto.getMonedaCodigo() != null && !dto.getMonedaCodigo().isBlank() ? dto.getMonedaCodigo().trim().toUpperCase() : "USD")
                .monedaSimbolo(dto.getMonedaSimbolo() != null && !dto.getMonedaSimbolo().isBlank() ? dto.getMonedaSimbolo().trim() : "$")
                .monedaNombre(dto.getMonedaNombre() != null && !dto.getMonedaNombre().isBlank() ? dto.getMonedaNombre().trim().toUpperCase() : "DOLARES")
                .diasValidezCotizacion(dto.getDiasValidezCotizacion() != null ? dto.getDiasValidezCotizacion() : 15)
                .tiempoEntregaPredeterminado(dto.getTiempoEntregaPredeterminado() != null && !dto.getTiempoEntregaPredeterminado().isBlank() ? dto.getTiempoEntregaPredeterminado().trim() : "De 5 a 6 semanas")
                .garantiaPredeterminada(dto.getGarantiaPredeterminada() != null && !dto.getGarantiaPredeterminada().isBlank() ? dto.getGarantiaPredeterminada().trim() : "1 año contra defectos de fábrica")
                .mostrarIvaDesglosado(dto.getMostrarIvaDesglosado() != null ? dto.getMostrarIvaDesglosado() : true)
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();

        return mapearADto(sucursalRepository.save(sucursal));
    }

    @Transactional
    public SucursalDto actualizar(Long id, SucursalDto dto) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));

        if (dto.getCodigo() != null && !dto.getCodigo().isBlank()) {
            sucursal.setCodigo(dto.getCodigo().trim().toUpperCase());
        }
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            sucursal.setNombre(dto.getNombre().trim());
        }
        if (dto.getRazonSocial() != null && !dto.getRazonSocial().isBlank()) {
            sucursal.setRazonSocial(dto.getRazonSocial().trim());
        }
        sucursal.setNombreComercial(dto.getNombreComercial());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());
        sucursal.setCorreo(dto.getCorreo());
        if (dto.getPrefijoCotizacion() != null && !dto.getPrefijoCotizacion().isBlank()) {
            sucursal.setPrefijoCotizacion(dto.getPrefijoCotizacion().trim().toUpperCase());
        }
        if (dto.getHeaderBannerBase64() != null) {
            sucursal.setHeaderBannerBase64(dto.getHeaderBannerBase64());
        }
        if (dto.getFooterBannerBase64() != null) {
            sucursal.setFooterBannerBase64(dto.getFooterBannerBase64());
        }
        if (dto.getFirmaBase64() != null) {
            sucursal.setFirmaBase64(dto.getFirmaBase64());
        }
        if (dto.getNombreFirmante() != null) {
            sucursal.setNombreFirmante(dto.getNombreFirmante());
        }
        if (dto.getCargoFirmante() != null) {
            sucursal.setCargoFirmante(dto.getCargoFirmante());
        }
        if (dto.getFormaPagoPredeterminada() != null) {
            sucursal.setFormaPagoPredeterminada(dto.getFormaPagoPredeterminada());
        }
        if (dto.getNotaPredeterminada() != null) {
            sucursal.setNotaPredeterminada(dto.getNotaPredeterminada());
        }
        if (dto.getPorcentajeIva() != null) {
            sucursal.setPorcentajeIva(dto.getPorcentajeIva());
        }
        if (dto.getMonedaCodigo() != null && !dto.getMonedaCodigo().isBlank()) {
            sucursal.setMonedaCodigo(dto.getMonedaCodigo().trim().toUpperCase());
        }
        if (dto.getMonedaSimbolo() != null && !dto.getMonedaSimbolo().isBlank()) {
            sucursal.setMonedaSimbolo(dto.getMonedaSimbolo().trim());
        }
        if (dto.getMonedaNombre() != null && !dto.getMonedaNombre().isBlank()) {
            sucursal.setMonedaNombre(dto.getMonedaNombre().trim().toUpperCase());
        }
        if (dto.getDiasValidezCotizacion() != null) {
            sucursal.setDiasValidezCotizacion(dto.getDiasValidezCotizacion());
        }
        if (dto.getTiempoEntregaPredeterminado() != null) {
            sucursal.setTiempoEntregaPredeterminado(dto.getTiempoEntregaPredeterminado());
        }
        if (dto.getGarantiaPredeterminada() != null) {
            sucursal.setGarantiaPredeterminada(dto.getGarantiaPredeterminada());
        }
        if (dto.getMostrarIvaDesglosado() != null) {
            sucursal.setMostrarIvaDesglosado(dto.getMostrarIvaDesglosado());
        }
        if (dto.getEmpresaId() != null) {
            sucursal.setEmpresaId(dto.getEmpresaId());
        }
        if (dto.getActivo() != null) {
            sucursal.setActivo(dto.getActivo());
        }

        return mapearADto(sucursalRepository.save(sucursal));
    }

    @Transactional
    public void eliminar(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));
        sucursal.setActivo(false);
        sucursalRepository.save(sucursal);
    }

    private SucursalDto mapearADto(Sucursal s) {
        return SucursalDto.builder()
                .id(s.getId())
                .empresaId(s.getEmpresaId())
                .codigo(s.getCodigo())
                .nombre(s.getNombre())
                .razonSocial(s.getRazonSocial())
                .nombreComercial(s.getNombreComercial())
                .direccion(s.getDireccion())
                .telefono(s.getTelefono())
                .correo(s.getCorreo())
                .prefijoCotizacion(s.getPrefijoCotizacion())
                .headerBannerBase64(s.getHeaderBannerBase64())
                .footerBannerBase64(s.getFooterBannerBase64())
                .firmaBase64(s.getFirmaBase64())
                .nombreFirmante(s.getNombreFirmante())
                .cargoFirmante(s.getCargoFirmante())
                .formaPagoPredeterminada(s.getFormaPagoPredeterminada())
                .notaPredeterminada(s.getNotaPredeterminada())
                .porcentajeIva(s.getPorcentajeIva())
                .monedaCodigo(s.getMonedaCodigo())
                .monedaSimbolo(s.getMonedaSimbolo())
                .monedaNombre(s.getMonedaNombre())
                .diasValidezCotizacion(s.getDiasValidezCotizacion())
                .tiempoEntregaPredeterminado(s.getTiempoEntregaPredeterminado())
                .garantiaPredeterminada(s.getGarantiaPredeterminada())
                .mostrarIvaDesglosado(s.getMostrarIvaDesglosado())
                .activo(s.getActivo())
                .build();
    }
}
