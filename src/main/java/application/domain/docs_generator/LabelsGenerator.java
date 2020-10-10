package application.domain.docs_generator;

import application.controllers.dtos.LabelsDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

public class LabelsGenerator {

    public ByteArrayOutputStream GetPdf(LabelsDTO dto){
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();
            Position[] p = getStartPositions(document);
            for(int i=0; i < 8; i++){
                Label label = new Label(
                        p[i],"name", "origin",
                        "121212123321","1.13");
                addLabel(label, document, writer);
            }

            document.close();
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream;
    }

    private Position[] getStartPositions(Document document){
        float width = document.getPageSize().getWidth();
        float height = document.getPageSize().getHeight();
        int oX = 50;
        int oY = 50;
        Position sp0 = new Position(oX,oY);
        Position sp1 = new Position(oX,oY + height/4);
        Position sp2 = new Position(oX,oY + 2*height/4);
        Position sp3 = new Position(oX,oY + 3*height/4);
        Position sp4 = new Position(oX+width/2,oY);
        Position sp5 = new Position(oX+(width/2),oY+height/4);
        Position sp6 = new Position(oX+(width/2),oY+(2*height/4));
        Position sp7 = new Position(oX+(width/2),oY+(3*height/4));
        Position[] p = {sp0,sp1,sp2,sp3,sp4,sp5,sp6,sp7};

        return p;
    }

    private void addLabel(Label label, Document document, PdfWriter writer){
        Font nameOriginFont = new Font(Font.FontFamily.TIMES_ROMAN,40.0f,Font.BOLD,BaseColor.BLACK);
        Font numFont = new Font(Font.FontFamily.TIMES_ROMAN,12.0f,Font.BOLD,BaseColor.BLACK);

        Phrase name= new Phrase(label.getName(),nameOriginFont);
        Phrase origin = new Phrase(label.getOrigin(),nameOriginFont);
        Phrase price = new Phrase(label.getPrice(),nameOriginFont);
        Phrase number = new Phrase(label.getNumber(), numFont);
        PdfContentByte contentByte = writer.getDirectContent();

        setPara(contentByte, document, name, label.getStartX(), label.getStartY() );
        setPara(contentByte, document, origin, label.getStartX(), label.getStartY()+50 );
        setPara(contentByte, document, price, label.getStartX(), label.getStartY()+ 100 );
        setPara(contentByte, document, number, label.getStartX(), label.getStartY()+ 115 );
    }

    private void setPara(PdfContentByte canvas, Document document , Phrase p, float x, float y) {
        y =  document.getPageSize().getHeight() - y;

        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, p, x, y, 0);
    }
    /*private static void setCellContent(XWPFTableRow row, int cell, LabelsDTO.Label label){

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

    private static String getFirstNCharsFrom(String str,int n) {
        return str.substring(0,Math.min(str.length(),n));
    }*/

}
