package application.domain.labels_generator;

import application.controllers.stepper.dto.LabelsDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ILabelsGenerator {
    ByteArrayOutputStream GetPdf(LabelsDTO dto) throws IOException;
}
