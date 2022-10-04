package com.infotech.docyard.um.api;

import com.infotech.docyard.um.dto.ModuleDTO;
import com.infotech.docyard.um.exceptions.CustomException;
import com.infotech.docyard.um.exceptions.NoDataFoundException;
import com.infotech.docyard.um.service.ModuleService;
import com.infotech.docyard.um.util.CustomResponse;
import com.infotech.docyard.um.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/module")
@Log4j2
public class ModuleAPI {

    @Autowired
    private ModuleService moduleService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllModuleWithModuleActions(HttpServletRequest request)
            throws CustomException, NoDataFoundException {

        List<ModuleDTO> moduleDTOList = null;
        try {
            moduleDTOList = moduleService.getAllModuleWithModuleActions();
        } catch (Exception e) {
            throw new CustomException(e);
        }
        return ResponseUtility.buildResponseObject(moduleDTOList);
    }


}
