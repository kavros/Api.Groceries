package application.controllers;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import com.google.gson.Gson;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin
public class StepperController {

    Gson gson = new Gson();

    class test{
        int a;
        int b;
    }

	@PostMapping("/upload")
	public ResponseEntity<?> getMainTableData(@RequestParam("pdfFile") MultipartFile file) throws IOException {

		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		PDDocument document = PDDocument.load(convFile);
		PDFTextStripper pdfStripper = new PDFTextStripper();
		String text = pdfStripper.getText(document);
		test t = new test();
		t.a = 1;
		t.b =2;
		System.out.println(file.getOriginalFilename());
		System.out.println(text);

		return  new ResponseEntity<>(gson.toJson(t), HttpStatus.OK);

	}
}
