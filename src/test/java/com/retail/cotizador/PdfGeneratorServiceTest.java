package com.retail.cotizador;

import com.retail.cotizador.cotizaciones.entity.Cotizacion;
import com.retail.cotizador.cotizaciones.entity.ItemCotizacion;
import com.retail.cotizador.cotizaciones.service.PdfGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfGeneratorServiceTest {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private com.retail.cotizador.cotizaciones.service.CotizacionService cotizacionService;

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testGenerarPdfDesdeBaseDeDatos() {
        com.retail.cotizador.cotizaciones.dto.CotizacionRequestDto dto = com.retail.cotizador.cotizaciones.dto.CotizacionRequestDto.builder()
                .usuarioEmisor("ERAMIREZ")
                .fechaEmision(LocalDate.now())
                .contactoCliente("Ing. Mario Rivas")
                .razonSocialCliente("BANCO INDUSTRIAL EL SALVADOR SA")
                .formaPago("Crédito 30 días")
                .subtotalSinIva(new BigDecimal("750.00"))
                .montoIva(new BigDecimal("97.50"))
                .totalInversion(new BigDecimal("847.50"))
                .items(List.of(
                        com.retail.cotizador.cotizaciones.dto.ItemDto.builder()
                                .itemNumero(1)
                                .descripcionEquipo("COMPUTADORA MOVIL INDUSTRIAL HONEYWELL SCANPAL EDA52")
                                .partNumber("EDA52-111-E2101RK")
                                .caracteristicas("Sistema Operativo Android 11 actualizable\nPantalla táctil Gorilla Glass\nMotor de escaneo 2D Honeywell S0703")
                                .tiempoEntrega("De 4 a 6 semanas")
                                .cantidad(1)
                                .precioUnitario(new BigDecimal("750.00"))
                                .totalLinea(new BigDecimal("750.00"))
                                .build()
                ))
                .build();

        com.retail.cotizador.cotizaciones.dto.CotizacionResponseDto creada = cotizacionService.crearCotizacion(dto);
        assertNotNull(creada.getId());

        Cotizacion entidad = cotizacionService.obtenerEntidadPorId(creada.getId());
        byte[] pdf = pdfGeneratorService.generarCotizacionPdf(entidad);
        assertNotNull(pdf);
        assertTrue(pdf.length > 1000);
    }

    @Test
    void testGenerarPdfConCaracteristicas() throws Exception {
        ItemCotizacion item = ItemCotizacion.builder()
                .itemNumero(1)
                .descripcionEquipo("IMPRESORA DE TARJETAS ZEBRA ZC300 DUAL")
                .partNumber("ZCD-800300-250LA")
                .caracteristicas("Sublimación directa a tarjeta\nImpresión doble cara\nResolución 300 dpi\nConectividad USB y Ethernet")
                .tiempoEntrega("De 5 a 6 semanas")
                .cantidad(1)
                .precioUnitario(new BigDecimal("1250.00"))
                .totalLinea(new BigDecimal("1250.00"))
                .build();

        Cotizacion cotizacion = Cotizacion.builder()
                .codigoCotizacion("#20261002001")
                .usuarioEmisor("VENTASSR001")
                .fechaEmision(LocalDate.now())
                .contactoCliente("Tania Argueta")
                .razonSocialCliente("ALIMENTOS Y TURISMO SA DE CV")
                .nombreComercial("PREMIUM RESTAURANTS")
                .formaPago("Crédito 30 días, Transferencia Bancaria o Cheque")
                .notaImportante("Tiempos de entrega y precios sujetos a cambios")
                .subtotalSinIva(new BigDecimal("1250.00"))
                .montoIva(new BigDecimal("162.50"))
                .totalInversion(new BigDecimal("1412.50"))
                .totalEnLetras("UN MIL CUATROCIENTOS DOCE DOLARES CON 50/100")
                .items(List.of(item))
                .build();

        byte[] pdf = pdfGeneratorService.generarCotizacionPdf(cotizacion);
        assertNotNull(pdf);
        assertTrue(pdf.length > 1000);
        assertEquals('%', (char) pdf[0]);
        assertEquals('P', (char) pdf[1]);
        assertEquals('D', (char) pdf[2]);
        assertEquals('F', (char) pdf[3]);

        try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.pdmodel.PDDocument.load(pdf)) {
            assertEquals(1, doc.getNumberOfPages(), "Una cotización de 1 solo item debe caber exactamente en 1 página");
            org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(doc);
            java.awt.image.BufferedImage img = renderer.renderImageWithDPI(0, 100);
            javax.imageio.ImageIO.write(img, "PNG", new java.io.File("target/single_page_1.png"));
        }
    }

    @Test
    void testGenerarPdfMultiplesItemsYBloques() throws Exception {
        java.util.ArrayList<ItemCotizacion> items = new java.util.ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            items.add(ItemCotizacion.builder()
                    .itemNumero(i)
                    .descripcionEquipo("EQUIPO INDUSTRIAL ZEBRA SERIE " + i + "00")
                    .partNumber("PN-ZEB-00" + i)
                    .caracteristicas("Característica técnica número 1 del equipo\nCaracterística técnica número 2 del equipo\nCaracterística técnica número 3 del equipo")
                    .tiempoEntrega("De 5 a 6 semanas")
                    .cantidad(2)
                    .precioUnitario(new BigDecimal("850.00"))
                    .totalLinea(new BigDecimal("1700.00"))
                    .build());
        }

        Cotizacion cotizacion = Cotizacion.builder()
                .codigoCotizacion("COT202610001")
                .usuarioEmisor("VENTASSR001")
                .fechaEmision(LocalDate.now())
                .contactoCliente("Tania Argueta")
                .razonSocialCliente("ALIMENTOS Y TURISMO SA DE CV")
                .nombreComercial("PREMIUM RESTAURANTS")
                .formaPago("Crédito 30 días, Transferencia Bancaria o Cheque")
                .notaImportante("Tiempos de entrega y precios sujetos a cambios")
                .subtotalSinIva(new BigDecimal("8500.00"))
                .montoIva(new BigDecimal("1105.00"))
                .totalInversion(new BigDecimal("9605.00"))
                .totalEnLetras("NUEVE MIL SEISCIENTOS CINCO DOLARES CON 00/100")
                .items(items)
                .build();

        byte[] pdf = pdfGeneratorService.generarCotizacionPdf(cotizacion);
        assertNotNull(pdf);
        assertTrue(pdf.length > 2000);
        assertEquals('%', (char) pdf[0]);
        assertEquals('P', (char) pdf[1]);
        assertEquals('D', (char) pdf[2]);
        assertEquals('F', (char) pdf[3]);

        // Guardar muestra para inspección en target/
        java.nio.file.Files.write(java.nio.file.Paths.get("target/cotizacion_multiples_items.pdf"), pdf);

        try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.pdmodel.PDDocument.load(pdf)) {
            org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(doc);
            for (int p = 0; p < doc.getNumberOfPages(); p++) {
                java.awt.image.BufferedImage img = renderer.renderImageWithDPI(p, 100);
                javax.imageio.ImageIO.write(img, "PNG", new java.io.File("target/page_" + (p + 1) + ".png"));
            }
        }
    }
}
