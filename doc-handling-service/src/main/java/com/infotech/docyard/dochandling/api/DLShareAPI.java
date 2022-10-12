package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
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

@RestController
@RequestMapping("/share")
@Log4j2
public class DLShareAPI {

    @Autowired
    private DLShareService dlShareService;

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

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
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
