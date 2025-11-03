package com.jadavpur.durgamandir.service.imp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.jadavpur.durgamandir.service.FileExtractionService;

@Component
public class FileExtractionServiceImp implements FileExtractionService {

	@Value("${tika.tesseract.path}")
	private String tessPath;

	@Value("${tika.tesseract.tessdata}")
	private String tessDataPath;

	@Value("${tika.tesseract.language}")
	private String tessLanguage;

	@Override
	public String extractTextFromFile(MultipartFile file) {
		// Validate input
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Uploaded file is empty");
		}

		Tika tika = new Tika();
		try (InputStream in = file.getInputStream()) {
			String text = tika.parseToString(in);
			if (text == null) {
				return "";
			}
			return text.trim();
		} catch (IOException | TikaException e) {
			// Wrap and rethrow so callers can handle/report a clear error
			throw new RuntimeException("Failed to extract text from file: " + e.getMessage(), e);
		}
	}

	public String extractTextFromImage(MultipartFile file) {
		try {

			System.setProperty("tesseract.path", "C:/Program Files/Tesseract-OCR/tesseract.exe");
			System.setProperty("tessdata.path", "C:/Program Files/Tesseract-OCR/tessdata");
			TikaConfig config = new TikaConfig();

			TesseractOCRConfig ocr = new TesseractOCRConfig();

			ocr.setLanguage(tessLanguage);

			ParseContext context = new ParseContext();
			context.set(TesseractOCRConfig.class, ocr);

			BodyContentHandler handler = new BodyContentHandler(-1);
			Parser parser = new AutoDetectParser(config);

			parser.parse(file.getInputStream(), handler, new Metadata(), context);

			return handler.toString().trim();

		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

}