package com.infotech.docyard.dochandling.api;

import com.infotech.docyard.dochandling.dto.DashboardDTO;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DataValidationException;
import com.infotech.docyard.dochandling.service.DLDocumentService;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dashboard")
@Log4j2
public class DashboardAPI {

    @Autowired
    private DLDocumentService dlDocumentService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseUtility.APIResponse getDashboardStats(HttpServletRequest request,
                                                         @PathVariable(value = "userId") Long userId) throws CustomException {
        log.info("getDashboardStats API initiated...");

        if (AppUtility.isEmpty(userId)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        DashboardDTO dashboardDTO = null;
        try {
            dashboardDTO = dlDocumentService.getDashboardStats(userId);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return new ResponseUtility.APIResponse(dashboardDTO, AppUtility.getResourceMessage("dashboard.stats.found"));
    }

}
