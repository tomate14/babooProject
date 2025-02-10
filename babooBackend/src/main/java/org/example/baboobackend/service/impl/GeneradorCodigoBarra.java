package org.example.baboobackend.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class GeneradorCodigoBarra {
    private static final String TEMPLATE_RESOURCE_PATH = "templates/etiqueta.pdf";

    public ByteArrayOutputStream generarEtiquetaConCodigoBarra(String codigoBarra) {
        InputStream templateStream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_RESOURCE_PATH);
        if (templateStream == null) {
            throw new RuntimeException("No se encontró la plantilla en el recurso: " + TEMPLATE_RESOURCE_PATH);
        }

        try {
            // Copiar a un archivo temporal para trabajar con PdfReader
            Path tempTemplatePath = Files.createTempFile("EtiquetasMultiples", ".pdf");
            Files.copy(templateStream, tempTemplatePath, StandardCopyOption.REPLACE_EXISTING);

            // Crear un ByteArrayOutputStream para generar el PDF en memoria
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfReader reader = new PdfReader(tempTemplatePath.toFile());
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(reader, writer);

            // Generar código de barras
            BufferedImage barcodeImage = generateEAN13BarcodeImage(codigoBarra);

            // Convertir BufferedImage a ImageData
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(barcodeImage, "png", baos);
            ImageData barcodeImageData = ImageDataFactory.create(baos.toByteArray());
            // Agregar el código de barras a la primera página del PDF
            Document document = new Document(pdfDoc);
            int bottom = 160;
            for (int vertical = 0; vertical < 2; vertical++) {
                int incrementarX = 17;
                for (int i=0; i < 3; i++) {
                    // Crear una instancia de Image para agregar al PDF
                    Image barcodeImageElement = new Image(barcodeImageData);

                    // Establecer la posición y tamaño del código de barras
                    barcodeImageElement.setFixedPosition(incrementarX, bottom); // Ajustar coordenadas (X, Y)
                    barcodeImageElement.setWidth(40); // Ajustar ancho
                    barcodeImageElement.setHeight(20); // Ajustar alto
                    document.add(barcodeImageElement);

                    incrementarX = incrementarX + 144;
                }
                bottom = bottom - 140;
            }



            pdfDoc.close();

            // Devolver el PDF como un ByteArrayOutputStream
            return outputStream;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF con código de barras", e);
        }
    }

    // Genera una imagen BufferedImage del código de barras EAN-13
    public static BufferedImage generateEAN13BarcodeImage(String barcodeText) {
        EAN13Bean barcodeGenerator = new EAN13Bean();
        barcodeGenerator.setModuleWidth(0.2); // Ajustar tamaño
        barcodeGenerator.doQuietZone(true);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    baos, "image/png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);

            barcodeGenerator.generateBarcode(canvas, barcodeText);
            canvas.finish();

            return canvas.getBufferedImage();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el código de barras EAN-13", e);
        }
    }
}
