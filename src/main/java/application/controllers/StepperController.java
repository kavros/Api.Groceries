package application.controllers;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import application.domain.ui.table.creator.ITableCreator;
import application.model.table.Table;
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
	ITableCreator tableCreator;

    /*public StepperController(InvoiceParser parser)
	{
		invoiceParser = parser;
	}*/

    @GetMapping("/test")
	public void test(){
    	System.out.println("EEEEEE");
	}

	@PostMapping("/upload")
	public ResponseEntity<?> getTableData(@RequestParam("pdfFile") MultipartFile file) throws IOException {

		//TODO: If file format is not valid -> Bad request
		File convFile = new File("./uploaded_files/"+file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		PDDocument document = PDDocument.load(convFile);
		PDFTextStripper pdfStripper = new PDFTextStripper();
		pdfStripper.setSortByPosition(true);


		Table data =  tableCreator.createTable(pdfStripper.getText(document));
		String json = gson.toJson(data);

		document.close();
		return  new ResponseEntity<>(json, HttpStatus.OK);

	}
}
