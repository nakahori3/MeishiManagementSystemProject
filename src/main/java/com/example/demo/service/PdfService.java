package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.MeishiEntity;
import com.example.demo.repository.MeishisRepository;


@Service
public class PdfService {

    @Autowired
    private MeishisRepository meishisRepository;

    public byte[] generatePdf(int id, String pgpassword) throws IOException {
        MeishiEntity meishi = meishisRepository.findDecryptedById(id, pgpassword);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDType0Font font = PDType0Font.load(document, new File("src/main/resources/fonts/Noto_Sans_JP/static/NotoSansJP-Regular.ttf"));

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            if (meishi.getPhotoomotePath() != null) {
                PDImageXObject omoteImage = PDImageXObject.createFromFile(meishi.getPhotoomotePath(), document);
                contentStream.drawImage(omoteImage, 50, 450, 250, 350);
            }
            if (meishi.getPhotouraPath() != null) {
                PDImageXObject uraImage = PDImageXObject.createFromFile(meishi.getPhotouraPath(), document);
                contentStream.drawImage(uraImage, 50, 50, 250, 350);
            }

            float textStartX = 320;
            float textStartY = 750;
            float textMaxWidth = 200;
            float lineHeight = 20f;
            int lineCount = 0;

            contentStream.setFont(font, 16);
            contentStream.setLeading(lineHeight);

            // デバッグ用ログ
            System.out.println("PDF生成処理開始");

            // 各項目を描画
            lineCount = drawItemWithBox(contentStream, font, "企業名", meishi.getCompanyname(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "企業名（カナ）", meishi.getCompanykananame(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "担当者名", meishi.getPersonalname(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "担当者名（カナ）", meishi.getPersonalkananame(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "所属", meishi.getBelong(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "役職", meishi.getPosition(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "所在地", meishi.getAddress(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "会社電話番号", meishi.getCompanytel(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "携帯電話番号", meishi.getMobiletel(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);
            lineCount = drawItemWithBox(contentStream, font, "Eメールアドレス", meishi.getEmail(), textStartX, textStartY, textMaxWidth, lineHeight, lineCount);

            System.out.println("PDF生成処理完了");
        }

        document.save(byteArrayOutputStream);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    
    private int drawItemWithBox(PDPageContentStream contentStream, PDType0Font font, String label, String value, float textStartX, float textStartY, float maxWidth, float lineHeight, int lineCount) throws IOException {
        // フォントサイズを取得して項目名の位置調整量を計算
        float fontAdjustment = 15 / 15.0f; // 項目名のフォントサイズ

        // 項目名の描画（位置をフォントサイズの半分分だけ下げる）
        contentStream.beginText();
        contentStream.setFont(font, 15); // 項目名のフォントサイズ
        contentStream.newLineAtOffset(textStartX, textStartY - (lineHeight * lineCount) - fontAdjustment); // 位置を調整
        contentStream.showText(label);
        contentStream.endText(); // テキスト描画終了
        System.out.println("項目名描画完了: " + label);
        lineCount++;

        
     

       // データ参照情報の描画位置を微調整
        float dataAdjustment = -4.0f; // 微調整値（少しだけ下げるには負の値を設定）
        float dataY = textStartY - (lineHeight * lineCount) + dataAdjustment; // 調整値を反映したY位置を計算 float dataY = textStartY - (lineHeight * lineCount); // 項目名の下にデータを描画
        contentStream.beginText();
        contentStream.setFont(font, 14); // データ部分のフォントサイズ
        contentStream.newLineAtOffset(textStartX, dataY);
        contentStream.showText(value);
        contentStream.endText(); // テキスト描画終了
        System.out.println("データ描画完了: " + value);
        lineCount++;

        // 枠線の描画
        float rectX = textStartX - 5; // 左に余白を追加
        float rectY = dataY + lineHeight ; // データの基準点から調整
        float rectWidth = maxWidth + 10; // 幅を調整
        float rectHeight = lineHeight + 10; // 高さを調整して余裕を持たせる

        // 枠線を描画
        contentStream.addRect(rectX, rectY - rectHeight, rectWidth, rectHeight);
        contentStream.setStrokingColor(211, 211, 211); // 薄いグレー（RGB: 211, 211, 211）
        contentStream.stroke();
        System.out.println("枠線描画完了: X=" + rectX + ", Y=" + rectY + ", 幅=" + rectWidth + ", 高さ=" + rectHeight);

        // 次の枠線との間隔を保持するためlineCountを増やす
        lineCount++;

        return lineCount;
    }
     

    
    public byte[] generateMultiResultPdf(List<MeishiEntity> searchResults, String pgpassword) throws IOException {
        System.out.println("Starting generateMultiResultPdf. Total results: " + searchResults.size());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            File fontFile = new File("src/main/resources/fonts/Noto_Sans_JP/static/NotoSansJP-Regular.ttf");
            if (!fontFile.exists()) {
                throw new IOException("フォントファイルが見つかりません: " + fontFile.getPath());
            }
            System.out.println("Font file loaded successfully.");
            PDType0Font font = PDType0Font.load(document, fontFile);

            int itemsPerPage = 8; // 1ページに表示する最大件数
            int itemsPerColumn = 4; // 1列に表示する件数
            float startXLeft = 40; // 左列の開始位置を左にずらす（余白を調整）
            float startXRight = 320; // 右列の開始位置を調整して左右均等に
            float startY = 750; // ページ上部の開始位置
            float rowHeight = 140f; // 各アイテムの高さ（画像とテキストを含む）

            PDPage page = null;
            PDPageContentStream contentStream = null;

            try {
                for (int i = 0; i < searchResults.size(); i++) {
                    // 新しいページの作成
                    if (i % itemsPerPage == 0) {
                        if (contentStream != null) {
                            contentStream.close();
                            System.out.println("Closed content stream for previous page.");
                        }
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(font, 10.5f); // フォントサイズを10.5に設定
                        System.out.println("Created new page for items starting at index: " + i);
                    }

                    MeishiEntity result = searchResults.get(i);

                    // 列と行の位置を計算
                    float columnX = (i % 2 == 0) ? startXLeft : startXRight; // 偶数インデックスは左列、奇数インデックスは右列
                    float rowY = startY - (rowHeight * ((i % itemsPerPage) / 2)); // Y座標を計算（各列ごとの位置を調整）

                    System.out.println("Rendering entity at index: " + i + ", Column X: " + columnX + ", Row Y: " + rowY);

                    // 画像描画（名刺表面、左側半分）
                    if (result.getPhotoomotePath() != null && new File(result.getPhotoomotePath()).exists()) {
                        System.out.println("Drawing image for entity at index: " + i);
                        PDImageXObject image = PDImageXObject.createFromFile(result.getPhotoomotePath(), document);
                        contentStream.drawImage(image, columnX, rowY - 100, 80, 80); // 画像の位置を下げてサイズを小さく調整
                    } else {
                        System.err.println("Image file not found for entity at index: " + i);
                    }

                    // テキストと枠線描画（右側）
                    float textStartX = columnX + 100; // 画像の右側にテキストを配置
                    float textStartY = rowY; // 行の開始位置
                    float boxWidth = 180; // 枠線の幅を短く調整
                    float boxHeight = 20; // 各枠線の高さ

                    // 項目名と値（値のみに枠線を付ける）
                    drawLabelAndBox(contentStream, font, "企業名:", result.getCompanyname(), textStartX, textStartY, boxWidth, boxHeight);
                    drawLabelAndBox(contentStream, font, "担当者名:", result.getPersonalname(), textStartX, textStartY - 40, boxWidth, boxHeight);
                    drawLabelAndBox(contentStream, font, "Eメール:", result.getEmail(), textStartX, textStartY - 80, boxWidth, boxHeight);
                }
            } finally {
                if (contentStream != null) contentStream.close();
            }

            document.save(byteArrayOutputStream);
        }

        System.out.println("Finished generateMultiResultPdf.");
        return byteArrayOutputStream.toByteArray();
    }

 // 項目名と値を描画し、値のみに枠線を追加するヘルパーメソッド
    private void drawLabelAndBox(PDPageContentStream contentStream, PDType0Font font, String label, String value, float x, float y, float width, float height) throws IOException {
        // 項目名を描画（枠線なし）
        contentStream.beginText();
        contentStream.setFont(font, 10.5f); // フォントサイズを10.5に設定
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(label);
        contentStream.endText();

        // 項目名と値（枠線）の間隔を設定
        float valueY = y - 25; // 項目名から値までの間隔を追加
        contentStream.beginText();
        contentStream.newLineAtOffset(x + 10, valueY + 5); // 枠線内に収まるように調整
        contentStream.showText(value != null ? value : "N/A");
        contentStream.endText();

        // 値の枠線を描画
        contentStream.setStrokingColor(211, 211, 211); // 薄い灰色
        float adjustedWidth = width - 20; // 枠線の幅を短縮
        contentStream.addRect(x, valueY, adjustedWidth, height);
        contentStream.stroke();
    }

    
    


}
    
