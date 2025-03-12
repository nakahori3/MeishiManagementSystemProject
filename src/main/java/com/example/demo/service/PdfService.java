package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
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

    
    
	/*private int drawItemWithBox(PDPageContentStream contentStream, PDType0Font font, String label, String value, float textStartX, float textStartY, float maxWidth, float lineHeight, int lineCount) throws IOException {
	    // 枠線の下辺と次の枠線の間隔を確保するため、初期Y位置を計算
	    float adjustedTextStartY = textStartY - (lineHeight * lineCount) - 10; // 次の枠線間隔内に項目名を配置
	
	    // 項目名の描画（次の枠線間隔に項目名を収める）
	    contentStream.beginText();
	    contentStream.setFont(font, 16); // 項目名のフォントサイズ
	    contentStream.newLineAtOffset(textStartX, adjustedTextStartY); // 調整されたY位置で項目名を描画
	    contentStream.showText(label);
	    contentStream.endText(); // テキスト描画終了
	    System.out.println("項目名描画完了: " + label);
	    lineCount++;
	
	    // データの描画位置（そのまま保持）
	    float dataY = textStartY - (lineHeight * lineCount) - 20; // 項目名の下に適切な余白を持たせてデータを配置
	    contentStream.beginText();
	    contentStream.setFont(font, 12); // データ部分のフォントサイズ
	    contentStream.newLineAtOffset(textStartX, dataY);
	    contentStream.showText(value);
	    contentStream.endText(); // テキスト描画終了
	    System.out.println("データ描画完了: " + value);
	    lineCount++;
	
	    // 枠線の描画
	    float rectX = textStartX - 5; // 左に余白を追加
	    float rectY = dataY + lineHeight - 3; // データの基準点から調整
	    float rectWidth = maxWidth + 10; // 幅を調整
	    float rectHeight = lineHeight + 10; // 高さを調整して余裕を持たせる
	
	    contentStream.addRect(rectX, rectY - rectHeight, rectWidth, rectHeight);
	    contentStream.setStrokingColor(211, 211, 211); // 薄いグレー（RGB: 211, 211, 211）
	    contentStream.stroke();
	    System.out.println("枠線描画完了: X=" + rectX + ", Y=" + rectY + ", 幅=" + rectWidth + ", 高さ=" + rectHeight);
	
	    // 次の枠線との間隔を保持するためlineCountを増やす
	    lineCount++;
	
	    return lineCount;
	}
	*/
}


