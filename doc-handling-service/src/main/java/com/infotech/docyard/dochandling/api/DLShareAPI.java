package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dto.DLDocumentCommentDTO;
import com.infotech.docyard.dochandling.dto.DLDocumentShareDTO;
import com.infotech.docyard.dochandling.dto.ShareRequestDTO;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.exceptions.NoDataFoundException;
import com.infotech.docyard.dochandling.service.DLShareService;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.CustomResponse;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/share")
@Log4j2
public class DLShareAPI {

    @Autowired
    private DLShareService dlShareService;

    @RequestMapping(value = "/dl-document/{dlDocId}", method = RequestMethod.GET)
    public CustomResponse getAllSharingDetailsByDLDocId(HttpServletRequest request,
                                                        @PathVariable(name = "dlDocId") Long dlDocId) throws CustomException {
        log.info("getAllSharingDetailsByDLDocId API initiated...");

        List<DLDocumentShareDTO> shareDTOS = null;
        try {
            shareDTOS = dlShareService.getAllSharingDetailsByDLDocId(dlDocId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(shareDTOS);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public CustomResponse shareDLDocument(HttpServletRequest request,
                                          @RequestBody ShareRequestDTO shareRequestDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("shareDLDocument API initiated...");

        if (AppUtility.isEmpty(shareRequestDTO)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        String status = null;
        try {
            status = dlShareService.shareDLDocument(shareRequestDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(status);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    public CustomResponse removeDLDocumentSharing(HttpServletRequest request,
                                                  @RequestBody ShareRequestDTO shareRequestDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("removeDLDocumentSharing API initiated...");

        if (AppUtility.isEmpty(shareRequestDTO)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        String status = null;
        try {
            status = dlShareService.shareDLDocument(shareRequestDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(status);
    }
}
