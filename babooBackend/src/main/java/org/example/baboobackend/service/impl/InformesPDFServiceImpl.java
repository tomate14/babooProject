package org.example.baboobackend.service.impl;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;

import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.layout.properties.VerticalAlignment;
import org.example.baboobackend.dto.ProductoInformeDTO;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.enumerados.TipoPedido;
import org.example.baboobackend.service.InformesPDFService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class InformesPDFServiceImpl implements InformesPDFService {
    private final static NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("es", "AR"));

    public ByteArrayInputStream generarORVPDF(List<ProductoInformeDTO> productos, TipoPedido tipoPedido, Pedido pedido) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Crear un nuevo documento PDF
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            PdfFont montserrat;
            try {
                String fontPath = "src/main/resources/fonts/Montserrat-Regular.ttf";
                montserrat = PdfFontFactory.createFont(
                        getClass().getClassLoader().getResource(fontPath).getPath(),
                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
                );
            } catch(Exception e) {
                montserrat = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            }


            Paragraph date = new Paragraph("Tandil, "+new SimpleDateFormat("dd MMM yyyy").format(new Date()))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setFontSize(10)
                    .setFont(montserrat)
                    .setTextAlignment(TextAlignment.RIGHT);

            document.add(date);
            try {
                agregarFirmaHoja(document);
            } catch (Exception e) {
                e.printStackTrace();
            }

            agregarSeparador(document, 0.3f);

            Paragraph paragraph = new Paragraph(tipoPedido.getDescripcion() + ": " + pedido.getNumeroComprobante())
                    .setFontSize(18)
                    .setFont(montserrat)
                    .setWidth(UnitValue.createPercentValue(100))
                    .setFontColor(ColorConstants.BLACK)
                    .setBold()
                    .setPadding(5) // Opcional: agrega un poco de espacio alrededor del texto
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);

            // Añadir firma en el encabezado
            document.add(paragraph);

            agregarSeparador(document, 0.3f);

            addTablaProductos(document, productos, montserrat);

            addFooter(document, pedido, montserrat);

            agregarPieDePagina(pdfDoc);
            document.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public String getNombreArchivo(Pedido pedido, TipoPedido tipoPedido) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fechaActual = sdf.format(new Date());
        return fechaActual + "_"+tipoPedido.getDescripcion()+"-00000001";
    }

    private void addTablaProductos(Document document, List<ProductoInformeDTO> productos, PdfFont montserrat) throws IOException {

        // Añadir tabla de productos
        // Crear una tabla con proporciones específicas para las columnas
        float[] columnWidths = {60f, 8f, 16f, 16f};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezado de la tabla con borde
        Cell headerProductoCell = generarCeldaSinBordes();
        Cell headerCantidadCell = generarCeldaSinBordes();
        Cell headerPrecioCell = generarCeldaSinBordes();
        Cell headerTotalCell = generarCeldaSinBordes();

        table.addHeaderCell(headerProductoCell.add(new Paragraph("Producto").setFont(montserrat).setBold().setFontSize(14f)));
        table.addHeaderCell(headerCantidadCell.add(new Paragraph("Unidad").setFont(montserrat).setBold().setFontSize(14f)).setTextAlignment(TextAlignment.RIGHT));
        table.addHeaderCell(headerPrecioCell.add(new Paragraph("Precio").setFont(montserrat).setBold().setFontSize(14f)).setTextAlignment(TextAlignment.RIGHT));
        table.addHeaderCell(headerTotalCell.add(new Paragraph("Total").setFont(montserrat).setBold().setFontSize(14f)).setTextAlignment(TextAlignment.RIGHT));

        // Agregar filas a la tabla (simulación de productos)
        for (ProductoInformeDTO producto: productos) {
            // Línea separadora como nueva fila
            Cell separatorCell = new Cell(1, 4); // Fila que abarca 4 columnas
            separatorCell.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.5f)); // Línea negra de 1px de espesor
            separatorCell.setBorderTop(Border.NO_BORDER);
            separatorCell.setBorderLeft(Border.NO_BORDER);
            separatorCell.setBorderRight(Border.NO_BORDER);
            table.addCell(separatorCell);

            Cell nombreCell = generarCeldaSinBordes();
            Cell stockCell = generarCeldaSinBordes();
            Cell precioCell = generarCeldaSinBordes();
            Cell totalCell = generarCeldaSinBordes();
            table.addCell(nombreCell.add(createParagraph(producto.getNombre().toUpperCase(), montserrat, 10)));
            table.addCell(stockCell.add(createParagraph(String.valueOf(producto.getStock()), montserrat, 10)).setTextAlignment(TextAlignment.RIGHT));
            table.addCell(precioCell.add(createParagraph("$ "+numberFormat.format(producto.getPrecio()), montserrat, 10)).setTextAlignment(TextAlignment.RIGHT));
            int total = (int) (producto.getPrecio() * producto.getStock());
            table.addCell(totalCell.add(createParagraph("$ "+numberFormat.format(total), montserrat, 10)).setTextAlignment(TextAlignment.RIGHT));

        }

        Cell emptyCell = generarCeldaSinBordes();
        table.addCell(emptyCell);

        document.add(table);
    }

    private Paragraph createParagraph(String text, PdfFont montserrat, int fontSize) {
        return new Paragraph(text).setFont(montserrat).setFontSize(fontSize);
    }

    private void addFooter(Document document, Pedido pedido, PdfFont montserrat) {
        agregarSeparador(document, 0.3f);
        int total = pedido.getTotal() < 0 ? -pedido.getTotal() : pedido.getTotal();
        Paragraph footer = new Paragraph("Total: $ "+numberFormat.format(total))
                .setFontSize(12)
                .setFont(montserrat)
                .setWidth(UnitValue.createPercentValue(100))
                .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .setBold()
                .setFontSize(16f);

        document.add(footer);
    }
    private Cell generarCeldaSinBordes() {
        Cell headerPrecioCell = new Cell();
        headerPrecioCell.setBorder(new SolidBorder(ColorConstants.WHITE, 0));

        return headerPrecioCell;
    }

    private void agregarFirmaHoja(Document document) throws MalformedURLException {
        // Cargar la imagen desde la ruta
        Image logo = new Image(ImageDataFactory.create(
                getClass().getClassLoader().getResource("templates/logo.png").toString()
        ));
        logo.setWidth(300);
        logo.setHeight(100);

        Cell nuevaCelda = generarCeldaSinBordes();
        nuevaCelda.add(logo)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setWidth(UnitValue.createPercentValue(100));

        document.add(nuevaCelda);
    }

    private void agregarSeparador(Document document, float size) {
        LineSeparator footerLineSeparator = new LineSeparator(new SolidLine(size)); // Grosor de la línea en puntos
        document.add(footerLineSeparator);
    }

    public void agregarPieDePagina(PdfDocument pdfDoc) {
        // Obtener el tamaño de la página
        PageSize pageSize = pdfDoc.getDefaultPageSize();
        float x = pageSize.getWidth() / 2;
        float y = 5; // Distancia desde la parte inferior

        // Crear un canvas para el pie de página
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.getLastPage());

        // Crear el texto para el pie de página
        Paragraph footer = new Paragraph("Comprobante no válido como factura")
                .setFontSize(10)
                .setPaddingRight(10)
                .setTextAlignment(TextAlignment.RIGHT);

        // Añadir el texto al canvas en la posición especificada
        Canvas canvas = new Canvas(pdfCanvas, new Rectangle(0, y, pageSize.getWidth(), 50));
        canvas.add(footer);
        canvas.close();
    }
}
