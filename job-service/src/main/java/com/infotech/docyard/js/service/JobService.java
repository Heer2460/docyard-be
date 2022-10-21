package com.infotech.docyard.js.service;

import com.infotech.docyard.js.dl.entity.DLDocument;
import com.infotech.docyard.js.service.FTPService;
import com.infotech.docyard.js.util.AppUtility;
import com.infotech.docyard.js.dl.repository.DLDocumentRepository;
import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Log4j2
public class JobService {

    @Autowired
    private DLDocumentRepository dlDocumentRepository;
    @Autowired
    private FTPService ftpService;

    public synchronized void getContentFromAllDocuments() {
        log.info("DLDocumentService - getContentFromAllDocuments method called...");

        try {
            List<DLDocument> dlDocumentList = dlDocumentRepository.findAllByFolderFalseAndArchivedFalseAndOcrDoneFalseAndOcrSupportedTrue();
            for (DLDocument doc : dlDocumentList) {
                InputStream inputStream = ftpService.downloadInputStream(doc.getVersionGUId());
                if (!AppUtility.isEmpty(inputStream)) {
                    ITesseract instance = new Tesseract();
                    instance.setOcrEngineMode(1);
                    Path dataDirectory = Paths.get(ClassLoader.getSystemResource("tesseractdata").toURI());
                    instance.setDatapath(dataDirectory.toString());
                    String result = null;

                    if (doc.getExtension().equalsIgnoreCase("pdf")) {
                        //TODO will do it later
//                        PDDocument document = PDDocument.load(inputStream);
//                        PDFRenderer pdfRenderer = new PDFRenderer(document);
//                        StringBuffer stringBuffer = new StringBuffer();
//                        for (int page = 0; page < document.getNumberOfPages(); page++) {
//                            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
//                            stringBuffer.append(instance.doOCR(bufferedImage));
//                        }
//                        result = stringBuffer.toString();
//                        document.close();
                    } else {
                        BufferedImage bufferedImage = ImageIO.read(inputStream);
                        result = instance.doOCR(bufferedImage);
                    }

                    doc.setContent(result);
                    doc.setOcrDone(true);
                    doc.setOcrSupported(true);
                    dlDocumentRepository.save(doc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
