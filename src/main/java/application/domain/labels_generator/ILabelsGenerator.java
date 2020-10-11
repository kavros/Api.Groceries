package application.domain.labels_generator;

import application.controllers.dtos.LabelsDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ILabelsGenerator {
    ByteArrayOutputStream GetPdf(LabelsDTO dto) throws IOException;
}
