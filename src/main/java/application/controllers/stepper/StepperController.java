package application.controllers.stepper;

import java.io.*;
import application.controllers.stepper.dto.LabelsDTO;
import application.controllers.stepper.dto.UpdatePricesDTO;
import application.controllers.stepper.dto.ImportDTO;
import application.domain.history_doc_generator.IHistoryDocGenerator;
import application.domain.labels_generator.ILabelsGenerator;
import application.domain.prices_updater.IPricesUpdater;
import application.domain.importer.services.ITableComposer;
import com.google.gson.Gson;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins="*")
public class StepperController {

	Gson gson = new Gson();
    @Autowired
	ITableComposer tableCreator;
	@Autowired
	IPricesUpdater pricesUpdater;
	@Autowired
	ILabelsGenerator labelsGenerator;
	@Autowired
	IHistoryDocGenerator historyDocGenerator;

	@GetMapping("/downloadHistoryDoc")
	public byte[] downloadHistoryDoc() throws IOException {
		return historyDocGenerator.getDoc();
	}

	@PutMapping("/downloadLabels")
	public byte[] getPriceLabels(@RequestBody LabelsDTO dto) throws IOException {
		ByteArrayOutputStream outputStream = labelsGenerator.GetPdf(dto);
        return outputStream.toByteArray();
	}

	@PutMapping("/updatePrices")
	public ResponseEntity<?> updatePrices(@RequestBody UpdatePricesDTO dto) {
		pricesUpdater.updatePrices(dto);
    	return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/import")
	public ResponseEntity<?> importAndReturnStepperData(@RequestParam("pdfFile") MultipartFile file) throws IOException {

		PDDocument document = PDDocument.load(file.getInputStream());
		PDFTextStripper pdfStripper = new PDFTextStripper();
		pdfStripper.setSortByPosition(true);

		HttpStatus statusCode;
		ImportDTO data =  tableCreator.createTable(pdfStripper.getText(document));
		if(!data.errors.isEmpty())
		{
			statusCode = HttpStatus.BAD_REQUEST;

		} else {
			statusCode = HttpStatus.OK;
		}

		String json = gson.toJson(data);

		document.close();
		return  new ResponseEntity<>(json, statusCode);
	}
}
