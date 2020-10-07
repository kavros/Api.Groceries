package application.domain.docs_generator;

import application.controllers.dtos.LabelsDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class LabelsGenerator {

    public static XWPFDocument GetDoc(LabelsDTO dto){
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        int currRow=0;
        int numOfLabels = dto.labels.size();
        for( int i=0; i < numOfLabels; i+=2){
            if (i>0){ table.createRow(); }
            LabelsDTO.Label currLabel = dto.labels.get(i);
            setCellContent(table.getRow(currRow), 0, currLabel);

            if(numOfLabels > i+1 ) {
                currLabel = dto.labels.get(i+1);
                setCellContent(table.getRow(currRow), 1, currLabel);
            }
            currRow++;
        }
        return document;
    }

    public static ByteArrayOutputStream GetPdf(){
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();
            setPara(writer.getDirectContent(), new Phrase("test"),
                    document.getPageSize().getWidth()/2, document.getPageSize().getHeight()/2 );

            document.add(new Paragraph("A Hello World PDF document."));
            document.close();
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream;
    }

    private static void setPara(PdfContentByte canvas, Phrase p, float x, float y) {
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, p, x, y, 0);
    }
    private static void setCellContent(XWPFTableRow row, int cell, LabelsDTO.Label label){

        if(row.getCell(cell) == null){
            row.addNewTableCell();
        }

        XWPFParagraph p0 =  row.getCell(cell).addParagraph();
        p0.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r0 = p0.createRun();
        String name = label.getName();
        if(name.contains(" ")){
            String[] words = name.split(" ");
            name = getFirstNCharsFrom(words[0], 8) +" "+
                    getFirstNCharsFrom(words[1], 3) ;
        } else {
            name = getFirstNCharsFrom(label.getName(),11);
        }
        String origin = getFirstNCharsFrom(label.getOrigin(),11);

        r0.setText(name);
        r0.setFontSize(30);
        r0.setBold(true);

        XWPFParagraph p1 = row.getCell(cell).addParagraph();
        p1.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r1 = p1.createRun();
        r1.setText(origin);
        r1.setBold(true);
        r1.setFontSize(30);

        //price
        XWPFParagraph p2 = row.getCell(cell).addParagraph();
        p2.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r2 = p2.createRun();
        r2.setText(label.getPrice()+"€");
        r2.setBold(true);
        r2.setFontSize(64);

        //number
        XWPFParagraph p3 = row.getCell(cell).addParagraph();
        p3.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r3 = p3.createRun();
        r3.setBold(true);
        r3.setFontSize(12);
        r3.setText(label.getNumber());
    }

    private static String getFirstNCharsFrom(String str,int n) {
        return str.substring(0,Math.min(str.length(),n));
    }

}
