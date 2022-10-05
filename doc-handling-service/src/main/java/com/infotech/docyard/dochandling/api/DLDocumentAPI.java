package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import com.infotech.docyard.dochandling.dto.UploadDocumentDTO;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.exceptions.NoDataFoundException;
import com.infotech.docyard.dochandling.service.DLDocumentService;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.CustomResponse;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/dl-document")
@Log4j2
public class DLDocumentAPI {

    @Autowired
    private DLDocumentService documentService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllDLDocumentsByFolderAndArchive(HttpServletRequest request,
                                                              @RequestParam(value = "folderId", required = false) Long folderId,
                                                              @RequestParam(value = "archived") Boolean archived) throws CustomException {
        log.info("getAllDLDocumentsByFolderAndArchive API initiated...");

        List<DLDocument> documents = null;
        try {
            documents = documentService.getDocumentsByFolderIdAndArchive(folderId, archived);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documents);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public CustomResponse uploadDocuments(HttpServletRequest request,
                                          @RequestPart("reqObj") UploadDocumentDTO uploadDocumentDTO,
                                          @RequestPart(name = "doc") MultipartFile[] files)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("uploadDocuments API initiated...");

        if (AppUtility.isEmpty(files)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocument dlDocument = null;
        try {
            dlDocument = documentService.uploadDocuments(uploadDocumentDTO, files);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocument, new DLDocumentDTO(), true);
    }

    @RequestMapping(value = "/folder", method = RequestMethod.POST)
    public CustomResponse createFolder(HttpServletRequest request,
                                       @RequestBody DLDocumentDTO folderRequestDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("createFolder API initiated...");

        DLDocument dlDocument = null;
        try {
            dlDocument = documentService.createFolder(folderRequestDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocument, new DLDocumentDTO(), true);
    }

    @RequestMapping(value = "/archive/{dlDocumentId}", method = RequestMethod.PUT)
    public CustomResponse archiveDlDocument(HttpServletRequest request,
                                            @PathVariable(value = "dlDocumentId") Long dlDocumentId,
                                            @RequestParam(value = "archive") Boolean archive)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("archiveDlDocument API initiated...");

        DLDocument dlDocument = null;
        if (AppUtility.isEmpty(dlDocumentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            dlDocument = documentService.archiveDlDocument(dlDocumentId, archive);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponseForPut(dlDocument, "Document Archived");
    }
}
