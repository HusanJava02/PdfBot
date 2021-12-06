package com.google.generates;

import com.google.controller.UpdatesController;
import com.google.init.MainInitializer;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

public class PDFGenerator {
    public void generatePDF(List<String> filesPath, Long chatId) throws IOException, DocumentException {
        String apiUrl = "https://api.telegram.org/file/bot" + new UpdatesController().getBotToken() + '/';
        for (int i = 0; i < filesPath.size(); i++) {
            String path = filesPath.get(i);
            downloadImage(apiUrl + path, chatId, i);
        }
        generate(filesPath.size(), chatId);
    }

    public void generate(int size, Long chatId) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream("src/main/java/PDFS/" + chatId + ".pdf"));
        document.open();
        document.setPageCount(size);
        for (int i = 0; i < size; i++) {
            Image image = Image.getInstance("src/main/java/images/" + chatId + "_" + i + ".jpg");
            image.scaleToFit(new Rectangle(image.getWidth(), image.getHeight()));
            image.setAbsolutePosition(0, 0);
            document.setPageSize(new Rectangle(image.getWidth(), image.getHeight()));
            document.newPage();
            document.add(image);
        }

        document.close();
        pdfWriter.close();
    }

    public void downloadImage(String url, Long chatId, int which) throws IOException {
        URL imageUrl = new URL(url);
        URLConnection urlConnection = imageUrl.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        Path path = Paths.get("src/main/java/images/" + chatId + "_" + which + ".jpg");
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
    }

}
