package com.example.meditrack;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.OutputStream;
import java.util.List;

public class PDFHelper {

    //Generate PDF File using data from records
    public static String generatePDF(Context context, List<RecordPDF> records) {

        try {

            // File name
            String fileName = "MediTrack_Records.pdf";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            }

            Uri uri = context.getContentResolver().insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
            );

            if (uri == null) return null;

            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Title
            document.add(new Paragraph("MediTrack Records\n\n")
                    .setBold()
                    .setFontSize(16));

            float[] columnWidths = {3, 2, 3, 2};
            //Create Table
            Table table = new Table(columnWidths);

            table.addHeaderCell(new Cell().add(new Paragraph("Name").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Dosage").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Time").setBold()));

            for (RecordPDF record : records) {

                table.addCell(new Cell().add(new Paragraph(record.getName())));
                table.addCell(new Cell().add(new Paragraph(record.getDosage() + "mg")));
                table.addCell(new Cell().add(new Paragraph(record.getDate())));
                table.addCell(new Cell().add(new Paragraph(record.getTime())));
            }

            document.add(table);
            document.close();

            return uri.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}