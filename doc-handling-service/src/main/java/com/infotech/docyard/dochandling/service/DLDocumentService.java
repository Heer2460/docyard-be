package com.infotech.docyard.dochandling.service;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.repository.DLDocumentRepository;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.util.AppUtility;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DLDocumentService {
    private DLDocumentRepository documentRepository;
    public List<DLDocument> getAllDocuments() {
        List<DLDocument> listOfDocuments = documentRepository.findAll();
        if (AppUtility.isEmpty(listOfDocuments)) {
            throw new DataValidationException(AppUtility.getResourceMessage("document.not.found"));
        }
        return listOfDocuments;
    }


}