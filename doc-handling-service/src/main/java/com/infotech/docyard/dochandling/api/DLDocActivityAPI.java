package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dto.DLDocumentActivityResponseDTO;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.service.DLDocActivityService;
import com.infotech.docyard.dochandling.util.CustomResponse;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/dl-doc-activity")
@Log4j2
public class DLDocActivityAPI {

    @Autowired
    private DLDocActivityService dlDocActivityService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllActivitiesByUserId(HttpServletRequest request,
                                                   @RequestParam(value = "userId", required = false) Long userId) throws CustomException {
        log.info("getAllActivitiesByUserId API initiated...");

        List<DLDocumentActivityResponseDTO> dlActivityResponseDTOList = null;
        try {
            dlActivityResponseDTOList = dlDocActivityService.getAllActivitiesByUserId(userId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(dlActivityResponseDTOList);
    }

}
