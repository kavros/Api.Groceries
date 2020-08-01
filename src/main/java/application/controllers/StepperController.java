package application.controllers;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.controllers.dtos.UpdatePricesDTO;
import application.controllers.dtos.ImportDTO;
import application.domain.prices_updater.IPricesUpdater;
import application.domain.importer.services.ITableComposer;
import application.model.settings.Settings;
import application.model.settings.services.ISettingsRepository;
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
	ISettingsRepository settingsRepository;

	@PutMapping("/addSetting")
	public ResponseEntity<?> updatePrices(@RequestBody Settings setting) {
		settingsRepository.add(setting);
		return  new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/updatePrices")
	public ResponseEntity<?> updatePrices(@RequestBody UpdatePricesDTO dto) {
		List<Map.Entry<String,Float>> data = new ArrayList<>();
		for(UpdatePricesDTO.Entry entry: dto.getProducts()){
			data.add(
					new AbstractMap.SimpleEntry<>(
						entry.getName(),entry.getNewPrice())
					);
		}
		pricesUpdater.updatePrices(data,dto.getInvoiceDate());

    	return null;
	}

	@PostMapping("/import")
	public ResponseEntity<?> importAndReturnStepperData(@RequestParam("pdfFile") MultipartFile file) throws IOException {

		//TODO: If file format is not valid -> Bad request
		File convFile = new File("./uploaded_files/"+file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		PDDocument document = PDDocument.load(convFile);
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
