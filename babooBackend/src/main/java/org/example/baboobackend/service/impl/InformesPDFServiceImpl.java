package org.example.baboobackend.service.impl;
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

            String fontPath = "src/main/resources/fonts/Montserrat-Regular.ttf";
            PdfFont montserrat = PdfFontFactory.createFont(fontPath, "UTF-8");

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
        Table table = new Table(4);
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezado de la tabla con borde
        Cell headerProductoCell = generarCeldaSinBordes();
        Cell headerCantidadCell = generarCeldaSinBordes();
        Cell headerPrecioCell = generarCeldaSinBordes();
        Cell headerTotalCell = generarCeldaSinBordes();

        table.addHeaderCell(headerProductoCell.add(new Paragraph("Producto").setFont(montserrat).setBold().setFontSize(16f)));
        table.addHeaderCell(headerCantidadCell.add(new Paragraph("Cantidad").setFont(montserrat).setBold().setFontSize(16f)));
        table.addHeaderCell(headerPrecioCell.add(new Paragraph("Precio").setFont(montserrat).setBold().setFontSize(16f)));
        table.addHeaderCell(headerTotalCell.add(new Paragraph("Total").setFont(montserrat).setBold().setFontSize(16f)));

        // Agregar filas a la tabla (simulación de productos)
        for (ProductoInformeDTO producto: productos) {
            Cell nombreCell = generarCeldaSinBordes();
            Cell stockCell = generarCeldaSinBordes();
            Cell precioCell = generarCeldaSinBordes();
            Cell totalCell = generarCeldaSinBordes();
            table.addCell(nombreCell.add(createParagraph(producto.getNombre().toUpperCase(), montserrat, 10)));
            table.addCell(stockCell.add(createParagraph(String.valueOf(producto.getStock()), montserrat, 10)));
            table.addCell(precioCell.add(createParagraph("$ "+numberFormat.format(producto.getPrecio()), montserrat, 10)));
            int total = (int) (producto.getPrecio() * producto.getStock());
            table.addCell(totalCell.add(createParagraph("$ "+numberFormat.format(total), montserrat, 10)));
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
        Image logo = new Image(ImageDataFactory.create("src/main/resources/templates/logo.png"));
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
