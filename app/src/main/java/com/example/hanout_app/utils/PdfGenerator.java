package com.example.hanout_app.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.example.hanout_app.adapter.CartAdapter;
import com.example.hanout_app.database.Data.UserData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfGenerator {

    private Context context;

    public PdfGenerator(Context context) {
        this.context = context;
    }

    public File generateInvoice(UserData user, List<CartAdapter.CartItem> items, double totalAmount) {
        PdfDocument document = new PdfDocument();

        // Page Info (A4 size approx in points: 595 x 842)
        int pageWidth = 595;
        int pageHeight = 842;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        Paint tableHeaderPaint = new Paint();

        // Colors
        int colorPrimary = Color.parseColor("#C17431"); // Maron
        int colorDark = Color.parseColor("#2b241c");
        int colorLight = Color.parseColor("#6B7280");

        // --- HEADER ---
        // Hanout Name
        titlePaint.setColor(colorPrimary);
        titlePaint.setTextSize(30);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Hanoute de " + user.getName(), 40, 60, titlePaint);

        // Contact Info
        paint.setColor(colorDark);
        paint.setTextSize(12);
        canvas.drawText("Tél: " + user.getPhone(), 40, 85, paint);
        canvas.drawText("Email: " + user.getEmail(), 40, 100, paint);

        // Date & Invoice Number
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String invoiceNum = "FAC-" + System.currentTimeMillis() / 1000;

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Date: " + date, pageWidth - 40, 60, paint);
        canvas.drawText("N°: " + invoiceNum, pageWidth - 40, 80, paint);
        paint.setTextAlign(Paint.Align.LEFT);

        // Separator
        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(1);
        canvas.drawLine(40, 120, pageWidth - 40, 120, paint);

        // --- TABLE HEADERS ---
        int startY = 160;
        tableHeaderPaint.setColor(Color.LTGRAY);
        canvas.drawRect(40, startY - 20, pageWidth - 40, startY + 10, tableHeaderPaint);

        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Produit", 50, startY, paint);
        canvas.drawText("Qté", 300, startY, paint);
        canvas.drawText("Prix U.", 380, startY, paint);
        canvas.drawText("Total", 480, startY, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // --- ITEMS ---
        int y = startY + 30;
        for (CartAdapter.CartItem item : items) {
            canvas.drawText(truncate(item.product.getName(), 30), 50, y, paint);
            canvas.drawText(String.valueOf(item.quantity), 300, y, paint);
            canvas.drawText(String.format(Locale.US, "%.2f", item.product.getPrice()), 380, y, paint);
            canvas.drawText(String.format(Locale.US, "%.2f", item.getTotalPrice()), 480, y, paint);

            y += 25;

            // Check page bound
            if (y > pageHeight - 100) {
                // In a real app, strict multi-page logic is needed. For simplicity, we stop
                // here or start new page.
            }
        }

        // Separator Line
        canvas.drawLine(40, y + 10, pageWidth - 40, y + 10, paint);

        // --- TOTAL ---
        y += 40;
        titlePaint.setTextSize(18);
        titlePaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("TOTAL À PAYER: " + String.format(Locale.US, "%.2f DH", totalAmount), pageWidth - 40, y,
                titlePaint);

        // --- FOOTER (Marketing) ---
        y = pageHeight - 60;
        paint.setColor(colorPrimary);
        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

        canvas.drawText("Merci de votre visite !", pageWidth / 2, y, paint);

        paint.setColor(colorLight);
        paint.setTextSize(10);
        canvas.drawText("Propulsé par l'application Hanouty", pageWidth / 2, y + 20, paint);

        document.finishPage(page);

        // Save File
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, "Facture_" + invoiceNum + ".pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            document.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            document.close();
            return null;
        }
    }

    private String truncate(String ignoredStr, int val) {
        if (ignoredStr.length() > val) {
            return ignoredStr.substring(0, val) + "...";
        }
        return ignoredStr;
    }
}
