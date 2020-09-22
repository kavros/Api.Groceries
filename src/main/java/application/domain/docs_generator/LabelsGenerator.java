package application.domain.docs_generator;

import application.controllers.dtos.LabelsDTO;
import org.apache.poi.xwpf.usermodel.*;

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
            name = getFirstNCharsFrom(words[0], 3) +" "+
                    getFirstNCharsFrom(words[1], 4) ;
        } else {
            name = getFirstNCharsFrom(label.getName(),8);
        }

        String origin = getFirstNCharsFrom(label.getOrigin(),8);
        r0.setText(name+'\n');
        r0.setText(origin);
        r0.setFontSize(36);
        r0.setBold(true);

        //price
        XWPFParagraph p1 = row.getCell(cell).addParagraph();
        p1.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r1 = p1.createRun();
        r1.setText(label.getPrice()+"€\n");
        r1.setBold(true);
        r1.setFontSize(48);

        //number
        XWPFParagraph p2 = row.getCell(cell).addParagraph();
        p2.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r2 = p1.createRun();
        r2.setBold(true);
        r2.setFontSize(10);
        r2.setText(label.getNumber());
    }

    private static String getFirstNCharsFrom(String str,int n) {
        return str.substring(0,Math.min(str.length(),n));
    }

}
