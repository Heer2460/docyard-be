package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dl.entity.DLShare;
import com.infotech.docyard.dochandling.dl.entity.DLShareCollaborator;
import com.infotech.docyard.dochandling.dto.DLDocumentShareDTO;
import com.infotech.docyard.dochandling.dto.DLShareDTO;
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

    @RequestMapping(value = "/{dlShareId}", method = RequestMethod.GET)
    public CustomResponse getDLShareById(HttpServletRequest request,
                                         @PathVariable(name = "dlShareId") Long dlShareId) throws CustomException {
        log.info("getAllSharingDetailsByDLDocId API initiated...");
        if (AppUtility.isEmpty(dlShareId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }

        DLShareDTO dlShareDTO = null;
        try {
            dlShareDTO = dlShareService.getDLShareById(dlShareId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlShareDTO);
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

    @RequestMapping(value = "/update-access-permission", method = RequestMethod.PUT)
    public CustomResponse updateShareCollaboratorAccessPermission(HttpServletRequest request,
                                                                  @RequestParam(value = "dlDocId") Long dlDocId,
                                                                  @RequestParam(value = "collId") Long collId,
                                                                  @RequestParam(value = "accessRight") String accessRight)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("updateShareCollaboratorAccessPermission API initiated...");

        DLShareCollaborator shareCollaborator = null;
        try {
            shareCollaborator = dlShareService.updateShareCollaboratorAccessPermission(dlDocId, collId, accessRight);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(shareCollaborator);
    }

    @RequestMapping(value = "/dl-document/{dlDocId}/collaborator/{colId}", method = RequestMethod.DELETE)
    public CustomResponse removeShareCollaborator(HttpServletRequest request,
                                                  @PathVariable(name = "dlDocId") Long dlDocId,
                                                  @PathVariable(name = "colId") Long colId)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("removeShareCollaborator API initiated...");

        if (AppUtility.isEmpty(dlDocId) || AppUtility.isEmpty(colId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        try {
            dlShareService.removeShareCollaborator(dlDocId, colId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponse(null, AppUtility.getResourceMessage("collaborator.remove.success"));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    public CustomResponse removeDLDocumentSharing(HttpServletRequest request,
                                                  @RequestBody ShareRequestDTO shareRequestDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("removeDLDocumentSharing API initiated...");

        if (AppUtility.isEmpty(shareRequestDTO) || AppUtility.isEmpty(shareRequestDTO.getDlDocId())) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        try {
            dlShareService.removeSharing(shareRequestDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponse(null, AppUtility.getResourceMessage("document.share.remove.success"));
    }
}
