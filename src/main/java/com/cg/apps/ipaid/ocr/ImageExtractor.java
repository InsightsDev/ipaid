package com.cg.apps.ipaid.ocr;

import java.io.File;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ImageExtractor {

	public String extractTextFromImage(File imageFile) {
		String result = null;
		ITesseract instance = new Tesseract(); 
        try {
            result = instance.doOCR(imageFile);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
	}
}
