package com.example.demo.util;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class PdfTextUtils {

    public static int drawWrappedText(PDPageContentStream contentStream, PDType0Font font, String text, float fontSize, float maxWidth, float lineHeight, int lineCount) throws IOException {
        float textWidth = 0;
        String[] words = text.split(" ");
        StringBuilder lineBuffer = new StringBuilder();

        for (String word : words) {
            float wordWidth = font.getStringWidth(word) / 1000 * fontSize;
            if (textWidth + wordWidth > maxWidth) {
                contentStream.showText(lineBuffer.toString().trim());
                contentStream.newLine();
                lineBuffer = new StringBuilder();
                textWidth = 0;
                lineCount++;
            }
            lineBuffer.append(word).append(" ");
            textWidth += wordWidth;
        }

        // 最終行を描画
        if (lineBuffer.length() > 0) {
            contentStream.showText(lineBuffer.toString().trim());
            contentStream.newLine();
            lineCount++;
        }

        return lineCount; // 描画された行数を返す
    }
}