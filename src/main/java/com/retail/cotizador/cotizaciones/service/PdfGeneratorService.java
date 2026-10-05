package com.retail.cotizador.cotizaciones.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.retail.cotizador.cotizaciones.entity.Cotizacion;
import com.retail.cotizador.sucursales.entity.Sucursal;
import com.retail.cotizador.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;
    private final SucursalRepository sucursalRepository;

    public byte[] generarCotizacionPdf(Cotizacion cotizacion) {
        Context context = new Context(new Locale("es", "SV"));
        context.setVariable("c", cotizacion);

        Optional<Sucursal> sucursalOpt = (cotizacion.getSucursalId() != null)
                ? sucursalRepository.findById(cotizacion.getSucursalId())
                : Optional.empty();

        String signatureBase64 = sucursalOpt
                .map(Sucursal::getFirmaBase64)
                .filter(s -> s != null && !s.isBlank())
                .orElseGet(() -> imageToBase64("templates/assets/signature-erick-ramirez.jpeg"));

        String nombreFirmante = sucursalOpt
                .map(Sucursal::getNombreFirmante)
                .filter(s -> s != null && !s.isBlank())
                .orElse("ING. ERICK RAMIREZ");

        String cargoFirmante = sucursalOpt
                .map(Sucursal::getCargoFirmante)
                .filter(s -> s != null && !s.isBlank())
                .orElse("GERENTE GENERAL");

        context.setVariable("signatureBase64", signatureBase64);
        context.setVariable("nombreFirmante", nombreFirmante);
        context.setVariable("cargoFirmante", cargoFirmante);

        String baseUri = getTemplatesBaseUri();
        String htmlContent = templateEngine.process("cotizacion-template", context);

        byte[] rawPdfBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, baseUri);
            builder.toStream(outputStream);
            builder.run();
            rawPdfBytes = outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al compilar el PDF de la cotización: " + e.getMessage(), e);
        }

        // Obtener bytes de cintillos para la sucursal o fallback a los predeterminados
        byte[] headerBytes = sucursalOpt
                .map(Sucursal::getHeaderBannerBase64)
                .map(this::extractBase64Bytes)
                .orElse(null);
        if (headerBytes == null) {
            headerBytes = loadResourceBytes("templates/assets/header-banner.jpeg");
        }

        byte[] footerBytes = sucursalOpt
                .map(Sucursal::getFooterBannerBase64)
                .map(this::extractBase64Bytes)
                .orElse(null);
        if (footerBytes == null) {
            footerBytes = loadResourceBytes("templates/assets/footer-banner.jpeg");
        }

        return estamparBannersEnTodasLasPaginas(rawPdfBytes, headerBytes, footerBytes);
    }

    private byte[] estamparBannersEnTodasLasPaginas(byte[] pdfBytes, byte[] headerBytes, byte[] footerBytes) {
        if (headerBytes == null || footerBytes == null) {
            return pdfBytes;
        }

        try (PDDocument document = PDDocument.load(pdfBytes);
             ByteArrayOutputStream finalOut = new ByteArrayOutputStream()) {

            PDImageXObject headerImage = PDImageXObject.createFromByteArray(document, headerBytes, "header");
            PDImageXObject footerImage = PDImageXObject.createFromByteArray(document, footerBytes, "footer");

            for (PDPage page : document.getPages()) {
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();

                float headerHeight = pageWidth * headerImage.getHeight() / (float) headerImage.getWidth();
                float footerHeight = pageWidth * footerImage.getHeight() / (float) footerImage.getWidth();

                try (PDPageContentStream contentStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                    // Cintillo superior en el tope exacto de la página
                    contentStream.drawImage(headerImage, 0, pageHeight - headerHeight, pageWidth, headerHeight);

                    // Cintillo inferior en la base exacta de la página
                    contentStream.drawImage(footerImage, 0, 0, pageWidth, footerHeight);
                }
            }

            document.save(finalOut);
            return finalOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al estampar cintillos corporativos en el PDF: " + e.getMessage(), e);
        }
    }

    private byte[] extractBase64Bytes(String dataUri) {
        if (dataUri == null || dataUri.isBlank()) return null;
        try {
            int commaIndex = dataUri.indexOf(",");
            String base64Data = (commaIndex != -1) ? dataUri.substring(commaIndex + 1) : dataUri;
            return Base64.getDecoder().decode(base64Data.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String getTemplatesBaseUri() {
        try {
            return new ClassPathResource("templates/").getURL().toExternalForm();
        } catch (Exception e) {
            return new java.io.File("src/main/resources/templates/").toURI().toString();
        }
    }

    private byte[] loadResourceBytes(String classpathLocation) {
        try {
            return new ClassPathResource(classpathLocation).getInputStream().readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private String imageToBase64(String classpathLocation) {
        try {
            byte[] bytes = new ClassPathResource(classpathLocation).getInputStream().readAllBytes();
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            return "";
        }
    }
}
