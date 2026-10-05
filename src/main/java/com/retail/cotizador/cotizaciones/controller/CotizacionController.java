package com.retail.cotizador.cotizaciones.controller;

import com.retail.cotizador.common.dto.PageResponseDto;
import com.retail.cotizador.cotizaciones.dto.CotizacionRequestDto;
import com.retail.cotizador.cotizaciones.dto.CotizacionResponseDto;
import com.retail.cotizador.cotizaciones.entity.Cotizacion;
import com.retail.cotizador.cotizaciones.service.CotizacionService;
import com.retail.cotizador.cotizaciones.service.PdfGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService cotizacionService;
    private final PdfGeneratorService pdfGeneratorService;

    @PostMapping
    public ResponseEntity<CotizacionResponseDto> crear(@Valid @RequestBody CotizacionRequestDto dto) {
        CotizacionResponseDto response = cotizacionService.crearCotizacion(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<CotizacionResponseDto>> listar(
            @RequestParam(required = false) Long sucursalId,
            Pageable pageable) {
        Page<CotizacionResponseDto> page = cotizacionService.listarCotizaciones(pageable, sucursalId);
        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.obtenerPorId(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Cotizacion cotizacion = cotizacionService.obtenerEntidadPorId(id);
        byte[] pdfBytes = pdfGeneratorService.generarCotizacionPdf(cotizacion);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "cotizacion_" + cotizacion.getCodigoCotizacion().replace("#", "") + ".pdf";
        headers.setContentDisposition(ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build());
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/preview-pdf")
    public ResponseEntity<byte[]> previewPdf(@RequestBody CotizacionRequestDto dto) {
        Cotizacion cotizacion = cotizacionService.mapearEntidadTemporal(dto);
        if (cotizacion.getCodigoCotizacion() == null || cotizacion.getCodigoCotizacion().isBlank()) {
             java.time.LocalDate f = dto.getFechaEmision() != null ? dto.getFechaEmision() : java.time.LocalDate.now();
             cotizacion.setCodigoCotizacion("COT" + f.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")) + "--- (PREVIA)");
        }
        byte[] pdfBytes = pdfGeneratorService.generarCotizacionPdf(cotizacion);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("preview_cotizacion.pdf", StandardCharsets.UTF_8).build());
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
