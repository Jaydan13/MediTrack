package com.example.meditrack;

import android.content.Context;
import android.os.Environment;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.File;
import java.util.List;

public class PDFHelper {

    public static String generatePDF(Context context, List<RecordPDF> records) {

        try {
            File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path, "MediTrack_Records.pdf");

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            try {
                // Load logo from drawable
                ImageData imageData = ImageDataFactory.create(
                        context.getResources().openRawResource(R.drawable.meditracklogo).readAllBytes()
                );

                Image logo = new Image(imageData);

                logo.setWidth(100); // adjust size
                logo.setAutoScale(true);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

                document.add(logo);

            } catch (Exception e) {
                e.printStackTrace();
            }

            document.add(new Paragraph("\n"));

            // Fonts
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Title
            document.add(new Paragraph("MediTrack Records\n\n")
                    .setFont(bold)
                    .setFontSize(16));

            // Create table with 4 columns
            float[] columnWidths = {3, 2, 3, 2};
            Table table = new Table(columnWidths);

            // Header cells (bold)
            table.addHeaderCell(new Cell().add(new Paragraph("Name").setFont(bold)));
            table.addHeaderCell(new Cell().add(new Paragraph("Dosage").setFont(bold)));
            table.addHeaderCell(new Cell().add(new Paragraph("Date").setFont(bold)));
            table.addHeaderCell(new Cell().add(new Paragraph("Time").setFont(bold)));

            // Data rows
            for (RecordPDF record : records) {

                table.addCell(new Cell().add(new Paragraph(record.getName()).setFont(normal)));
                table.addCell(new Cell().add(new Paragraph(record.getDosage() + "mg").setFont(normal)));
                table.addCell(new Cell().add(new Paragraph(record.getDate()).setFont(normal)));
                table.addCell(new Cell().add(new Paragraph(record.getTime()).setFont(normal)));
            }

            // Add table to document
            document.add(table);

            document.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}