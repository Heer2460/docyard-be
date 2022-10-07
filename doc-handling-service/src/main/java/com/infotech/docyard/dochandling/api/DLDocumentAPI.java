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

        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = documentService.getDocumentsByFolderIdAndArchive(folderId, archived);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility
                .buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/favourite", method = RequestMethod.GET)
    public CustomResponse getAllFavouriteDLDocumentsByFolder(HttpServletRequest request,
                                                             @RequestParam(value = "folderId", required = false) Long folderId) throws CustomException {
        log.info("getAllFavouriteDLDocumentsByFolderAndArchive API initiated...");

        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = documentService.getAllFavouriteDLDocumentsByFolder(folderId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility
                .buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/recent/owner/{ownerId}", method = RequestMethod.GET)
    public CustomResponse getAllRecentDLDocumentByOwnerId(HttpServletRequest request,
                                                          @PathVariable(value = "ownerId") Long ownerId) throws CustomException {
        log.info("getAllRecentDLDocumentByOwnerId API initiated...");

        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = documentService.getAllRecentDLDocumentByOwnerId(ownerId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/{dlDocumentId}", method = RequestMethod.GET)
    public CustomResponse getDLDocumentById(HttpServletRequest request,
                                            @PathVariable(value = "dlDocumentId") Long dlDocumentId) throws CustomException {
        log.info("getDLDocumentById API initiated...");
        DLDocumentDTO dlDocumentDTO = null;
        if (AppUtility.isEmpty(dlDocumentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            dlDocumentDTO = documentService.getDLDocumentById(dlDocumentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }

        return ResponseUtility.successResponseForPut(dlDocumentDTO, "Document Meta");
    }

    @RequestMapping(value = "/download/{dlDocumentId}", method = RequestMethod.GET)
    public CustomResponse downloadDLDocumentById(HttpServletRequest request,
                                                 @PathVariable(value = "dlDocumentId") Long dlDocumentId) throws CustomException {
        log.info("downloadDLDocumentById API initiated...");
        DLDocument dlDocument = null;
        if (AppUtility.isEmpty(dlDocumentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            dlDocument = documentService.downloadDLDocumentById(dlDocumentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }

        return ResponseUtility.buildResponseObject(dlDocument, new DLDocumentDTO(), true);
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

    @RequestMapping(value = "/{dlDocumentId}/", method = RequestMethod.PUT)
    public CustomResponse updateFavorite(HttpServletRequest request,
                                         @PathVariable(value = "dlDocumentId") Long dlDocumentId,
                                         @RequestParam(name = "favourite") Boolean favourite)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("updateFavorite API initiated...");

        DLDocument dlDocument = null;
        try {
            dlDocument = documentService.updateFavourite(dlDocumentId, favourite);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocument, new DLDocumentDTO(), true);
    }

    @RequestMapping(value = "/rename/{dlDocumentId}", method = RequestMethod.PUT)
    public CustomResponse renameDLDocument(HttpServletRequest request,
                                           @PathVariable(value = "dlDocumentId") Long dlDocumentId,
                                           @RequestParam(name = "newName") String name,
                                           @RequestParam(name = "userId") Long userId)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("renameDLDocument API initiated...");

        DLDocument dlDocument = null;
        try {
            dlDocument = documentService.renameDLDocument(dlDocumentId, name, userId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocument, new DLDocumentDTO(), true);
    }

    @RequestMapping(value = "/{dlDocumentId}", method = RequestMethod.DELETE)
    public CustomResponse deleteDLDocument(HttpServletRequest request,
                                           @PathVariable(value = "dlDocumentId") Long dlDocumentId)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("deleteDLDocument API initiated...");

        try {
            documentService.deleteDLDocument(dlDocumentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.deleteSuccessResponse(null, AppUtility.getResourceMessage("document.delete.success"));
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
