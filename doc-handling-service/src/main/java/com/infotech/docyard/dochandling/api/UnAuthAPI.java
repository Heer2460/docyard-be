package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import com.infotech.docyard.dochandling.dto.DLDocumentCommentDTO;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.exceptions.NoDataFoundException;
import com.infotech.docyard.dochandling.service.DLDocCommentService;
import com.infotech.docyard.dochandling.service.DLDocumentService;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.CustomResponse;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/un-auth")
@Log4j2
public class UnAuthAPI {

    @Autowired
    private DLDocumentService dlDocumentService;

    @RequestMapping(value = "/document/{guid}", method = RequestMethod.GET)
    public CustomResponse getDocumentByGUID(HttpServletRequest request,
                                            @PathVariable(name = "guid") String guid,
                                            @RequestParam(value = "shared") Boolean shared) throws CustomException {
        log.info("getDocumentByGUID API initiated...");

        if (AppUtility.isEmpty(guid)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocumentDTO dlDocumentDTO = null;
        try {
            dlDocumentDTO = dlDocumentService.getDocumentByGUID(guid, shared);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocumentDTO);
    }

    @RequestMapping(value = "/document/download/{dlDocumentId}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadDLDocument(HttpServletRequest request,
                                                                  @PathVariable(value = "dlDocumentId") Long dlDocumentId)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("downloadDLDocument API initiated...");

        if (AppUtility.isEmpty(dlDocumentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=testfile");
        InputStreamResource inputStreamResource = null;
        if (AppUtility.isEmpty(dlDocumentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            inputStreamResource = dlDocumentService.downloadDLDocument(dlDocumentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/folder/{folderId}", method = RequestMethod.GET)
    public CustomResponse getAllDlDocumentsByFolderId(HttpServletRequest request,
                                                      @PathVariable("folderId") Long folderId,
                                                      @RequestParam(value = "shared") Boolean shared) throws CustomException {
        log.info("getAllDlDocumentsByFolderId API initiated...");

        if (AppUtility.isEmpty(folderId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getAllDlDocumentsByFolderId(folderId, shared);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }
}
