package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dto.DLDocumentDTO;
import com.infotech.docyard.dochandling.dto.DLDocumentListDTO;
import com.infotech.docyard.dochandling.dto.DashboardDTO;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/dl-document")
@Log4j2
public class DLDocumentAPI {

    @Autowired
    private DLDocumentService dlDocumentService;

    @RequestMapping(value = "/search/{userId}", method = RequestMethod.GET)
    public CustomResponse searchDLDocuments(HttpServletRequest request,
                                            @RequestParam(value = "searchKey") String searchKey,
                                            @PathVariable(value = "userId") Long userId) throws CustomException {
        log.info("searchDLDocuments API initiated...");

        List<DLDocument> dlDocumentList = null;
        try {
            dlDocumentList = dlDocumentService.searchDLDocuments(searchKey, userId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(dlDocumentList, new DLDocumentDTO(), true);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllDLDocumentsByFolderAndArchive(HttpServletRequest request,
                                                              @RequestParam(value = "folderId", required = false) Long folderId,
                                                              @RequestParam(value = "archived") Boolean archived) throws CustomException {
        log.info("getAllDLDocumentsByFolderAndArchive API initiated...");

        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getDLDocumentsByFolderIdAndArchive(folderId, archived);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/owner/{ownerId}", method = RequestMethod.GET)
    public CustomResponse getAllDLDocumentsByOwnerIdFolderAndArchive(HttpServletRequest request,
                                                                     @PathVariable("ownerId") Long ownerId,
                                                                     @RequestParam(value = "folderId", required = false) Long folderId,
                                                                     @RequestParam(value = "archived") Boolean archived) throws CustomException {
        log.info("getAllDLDocumentsByOwnerIdFolderAndArchive API initiated...");

        if (AppUtility.isEmpty(ownerId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getDLDocumentsByOwnerIdFolderIdAndArchive(ownerId, folderId, archived);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/folder/{folderId}", method = RequestMethod.GET)
    public CustomResponse getAllDocumentsByFolderAndArchive(HttpServletRequest request,
                                                            @PathVariable(name = "folderId") Long folderId,
                                                            @RequestParam(value = "archived") Boolean archived) throws CustomException {
        log.info("getAllDocumentsByFolderAndArchive API initiated...");

        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getAllDocumentsByFolderAndArchive(folderId, archived);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/document/owner/{ownerId}", method = RequestMethod.GET)
    public CustomResponse getAllDocumentsByOwnerIdFolderAndArchive(HttpServletRequest request,
                                                                   @PathVariable("ownerId") Long ownerId,
                                                                   @RequestParam(value = "folderId", required = false) Long folderId,
                                                                   @RequestParam(value = "archived") Boolean archived) throws CustomException {
        log.info("getAllDocumentsByOwnerIdFolderAndArchive API initiated...");

        if (AppUtility.isEmpty(ownerId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getDocumentsByOwnerIdFolderIdAndArchive(ownerId, folderId, archived);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/used-space/user/{userId}", method = RequestMethod.GET)
    public CustomResponse getUsedSpaceByUserId(HttpServletRequest request,
                                               @PathVariable("userId") Long ownerId) throws CustomException {
        log.info("getUsedSpaceByUserId API initiated...");

        if (AppUtility.isEmpty(ownerId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        String usedSpace = null;
        try {
            usedSpace = dlDocumentService.getUsedSpaceByUserId(ownerId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(usedSpace);
    }

    @RequestMapping(value = "/favourite/owner/{ownerId}", method = RequestMethod.GET)
    public CustomResponse getAllFavouriteDLDocumentsByOwnerIdFolderAndArchive(HttpServletRequest request,
                                                                              @PathVariable("ownerId") Long ownerId,
                                                                              @RequestParam(value = "folderId", required = false) Long folderId,
                                                                              @RequestParam(value = "archived") Boolean archived) throws CustomException {
        log.info("getAllFavouriteDLDocumentsByOwnerIdFolderAndArchive API initiated...");

        if (AppUtility.isEmpty(ownerId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getAllFavouriteDLDocumentsByOwnerIdFolderAndArchive(ownerId, folderId, archived);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/favourite", method = RequestMethod.GET)
    public CustomResponse getAllFavouriteDLDocumentsByFolder(HttpServletRequest request,
                                                             @RequestParam(value = "folderId", required = false) Long folderId) throws CustomException {
        log.info("getAllFavouriteDLDocumentsByFolderAndArchive API initiated...");

        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getAllFavouriteDLDocumentsByFolder(folderId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/recent/owner/{ownerId}", method = RequestMethod.GET)
    public CustomResponse getAllRecentDLDocumentByOwnerId(HttpServletRequest request,
                                                          @PathVariable(value = "ownerId") Long ownerId) throws CustomException {
        log.info("getAllRecentDLDocumentByOwnerId API initiated...");

        if (AppUtility.isEmpty(ownerId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getAllRecentDLDocumentByOwnerId(ownerId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/{dlDocumentId}", method = RequestMethod.GET)
    public CustomResponse getDLDocumentById(HttpServletRequest request,
                                            @PathVariable(value = "dlDocumentId") Long dlDocumentId) throws CustomException {
        log.info("getDLDocumentById API initiated...");

        if (AppUtility.isEmpty(dlDocumentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocumentDTO dlDocumentDTO = null;
        try {
            dlDocumentDTO = dlDocumentService.getDLDocumentById(dlDocumentId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponseForPut(dlDocumentDTO, "Document Meta");
    }

    @RequestMapping(value = "/shared-by-me/user/{userId}", method = RequestMethod.GET)
    public CustomResponse getSharedByMeDLDocumentsByFolder(HttpServletRequest request,
                                                           @PathVariable(value = "userId") Long userId,
                                                           @RequestParam(value = "folderId") Long folderId) throws CustomException {
        log.info("getSharedByMeDLDocuments API initiated...");

        if (AppUtility.isEmpty(userId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getSharedByMeDLDocumentsByFolder(userId, folderId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/shared-with-me/user/{userId}", method = RequestMethod.GET)
    public CustomResponse getSharedWithMeDLDocuments(HttpServletRequest request,
                                                     @PathVariable(value = "userId") Long userId,
                                                     @RequestParam(value = "folderId") Long folderId) throws CustomException {
        log.info("getSharedWithMeDLDocuments API initiated...");

        if (AppUtility.isEmpty(userId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getSharedWithMeDLDocuments(userId, folderId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }

        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public CustomResponse uploadDocuments(HttpServletRequest request,
                                          @RequestPart(name = "reqObj") UploadDocumentDTO uploadDocumentDTO,
                                          @RequestPart(name = "doc") MultipartFile[] files)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("uploadDocuments API initiated...");

        if (AppUtility.isEmpty(uploadDocumentDTO) || AppUtility.isEmpty(files)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocument dlDocument = null;
        try {
            dlDocument = dlDocumentService.uploadDocuments(uploadDocumentDTO, files);
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

        if (AppUtility.isEmpty(folderRequestDTO) || !AppUtility.isEmpty(folderRequestDTO.getId())) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocument dlDocument = null;
        try {
            dlDocument = dlDocumentService.createFolder(folderRequestDTO);
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

        if (AppUtility.isEmpty(dlDocumentId) || AppUtility.isEmpty(favourite)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocument dlDocument = null;
        try {
            dlDocument = dlDocumentService.updateFavourite(dlDocumentId, favourite);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocument, new DLDocumentDTO(), true);
    }

    @RequestMapping(value = "/rename", method = RequestMethod.PUT)
    public CustomResponse renameDLDocument(HttpServletRequest request,
                                           @RequestBody DLDocumentDTO dlDocumentDTO)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("renameDLDocument API initiated...");

        if (AppUtility.isEmpty(dlDocumentDTO) || AppUtility.isEmpty(dlDocumentDTO.getId()) || AppUtility.isEmpty(dlDocumentDTO.getName())) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocument dlDocument = null;
        try {
            dlDocument = dlDocumentService.renameDLDocument(dlDocumentDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(dlDocument, new DLDocumentDTO(), true);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CustomResponse deleteDLDocument(HttpServletRequest request,
                                           @RequestBody DLDocumentListDTO dlDocumentIds)
            throws CustomException, DataValidationException, NoDataFoundException {
        log.info("deleteDLDocument API initiated...");

        if (AppUtility.isEmpty(dlDocumentIds)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        try {
            dlDocumentService.deleteDLDocument(dlDocumentIds);
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

        if (AppUtility.isEmpty(dlDocumentId) || AppUtility.isEmpty(archive)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DLDocument dlDocument = null;
        if (AppUtility.isEmpty(dlDocumentId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            dlDocument = dlDocumentService.archiveDlDocument(dlDocumentId, archive);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponseForPut(dlDocument, "Document Archived");
    }

    @RequestMapping(value = "/restore-archived", method = RequestMethod.PUT)
    public CustomResponse restoreArchivedDlDocument(HttpServletRequest request,
                                                    @RequestBody DLDocumentListDTO dlDocumentIds)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("restoreArchivedDlDocument API initiated...");


        if (AppUtility.isEmpty(dlDocumentIds)) {
            throw new DataValidationException(AppUtility.getResourceMessage("ids.not.found"));
        }
        try {
            dlDocumentService.restoreArchivedDlDocument(dlDocumentIds);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponseForPut(null, "Documents Archived");
    }

    @RequestMapping(value = "/trash/owner/{ownerId}", method = RequestMethod.GET)
    public CustomResponse getAllTrashDLDocumentByOwnerId(HttpServletRequest request,
                                                         @PathVariable(value = "ownerId") Long ownerId) throws CustomException {
        log.info("getAllTrashDLDocumentByOwnerId API initiated...");

        if (AppUtility.isEmpty(ownerId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        List<DLDocumentDTO> documentDTOList = null;
        try {
            documentDTOList = dlDocumentService.getAllTrashDLDocumentByOwnerId(ownerId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(documentDTOList);
    }

    @RequestMapping(value = "/download/{dlDocumentId}", method = RequestMethod.GET)
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
}
