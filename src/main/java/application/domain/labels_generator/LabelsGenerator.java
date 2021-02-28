package application.domain.labels_generator;

import application.controllers.dtos.LabelsDTO;
import application.model.erp.Smast;
import application.model.erp.services.IERPRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component("labelsGenerator")
public class LabelsGenerator implements ILabelsGenerator{

    @Autowired
    IERPRepository erpRepo;

    public ByteArrayOutputStream GetPdf(LabelsDTO dto) throws IOException {
        Document document = new Document();
        setPrices(dto);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();
            int labelsPerPage = 8;
            Position[] p = getStartPositions(document);
            for (int i = 0; i < dto.labels.size(); i++) {
                if (i > 0 && i % labelsPerPage == 0) {
                    document.newPage();
                }
                int index = i % labelsPerPage;
                addLabel(dto.labels.get(i), p[index], document, writer);
            }

            document.close();
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream;
    }

    private void setPrices(LabelsDTO dto) {
        List<Smast> smastList =
                erpRepo
                        .getProducts(dto
                                .labels
                                .stream()
                                .map(Label::getsCode)
                                .collect(Collectors.toList())
                        ) ;
        for(Label label: dto.labels){
            String retailPrice = String.valueOf(
                    smastList
                        .stream()
                        .filter(x -> x.getsCode().equals(label.getsCode()))
                        .findFirst().get()
                        .getsRetailPrice()
            );
            label.setPrice(retailPrice);
        }
    }

    private Position[] getStartPositions(Document document) {
        float width = document.getPageSize().getWidth();
        float height = document.getPageSize().getHeight();
        int oX = 50;
        int oY = 50;
        Position sp0 = new Position(oX, oY);
        Position sp1 = new Position(oX, oY + height / 4);
        Position sp2 = new Position(oX, oY + 2 * height / 4);
        Position sp3 = new Position(oX, oY + 3 * height / 4);
        Position sp4 = new Position(oX + width / 2, oY);
        Position sp5 = new Position(oX + (width / 2), oY + height / 4);
        Position sp6 = new Position(oX + (width / 2), oY + (2 * height / 4));
        Position sp7 = new Position(oX + (width / 2), oY + (3 * height / 4));

        return new Position[]{sp0, sp1, sp2, sp3, sp4, sp5, sp6, sp7};
    }

    private void addLabel(Label label, Position p, Document document, PdfWriter writer) throws IOException, DocumentException {

        BaseFont bf = BaseFont.createFont(".\\fonts\\arial.ttf", "Identity-H", false);

        Font huge = new Font(bf, 52.0f, Font.BOLD, BaseColor.BLACK);
        Font big = new Font(bf, 32.0f, Font.BOLD, BaseColor.BLACK);
        Font small = new Font(bf, 12.0f, Font.BOLD, BaseColor.BLACK);

        String[] names = getNameAndOrigin(label.getName(), label.getOrigin());
        Phrase name = new Phrase(names[0], big);
        Phrase origin = new Phrase(names[1], big);
        Phrase price = new Phrase(label.getPrice() + "€", huge);
        Phrase number = new Phrase(label.getNumber(), small);
        PdfContentByte contentByte = writer.getDirectContent();

        setPara(contentByte, document, name, p.getStartX(), p.getStartY());
        setPara(contentByte, document, origin, p.getStartX(), p.getStartY() + 50);
        setPara(contentByte, document, price, p.getStartX() + 30, p.getStartY() + 100);
        setPara(contentByte, document, number, p.getStartX(), p.getStartY() + 115);
    }

    private String[] getNameAndOrigin(String name, String origin) {
        String[] names = new String[2];
        int cPerRow = 12;
        names[0] = name.substring(0, Math.min(name.length(), cPerRow));
        names[1] = origin.substring(0, Math.min(origin.length(), cPerRow));
        return names;
    }

    private void setPara(PdfContentByte canvas, Document document, Phrase p, float x, float y) {
        y = document.getPageSize().getHeight() - y;

        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, p, x, y, 0);
    }
}