package com.infotech.tenant.service.api;

import com.infotech.tenant.service.entity.Tenant;
import com.infotech.tenant.service.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/tenants")
public class TenantApi {
    @Autowired
    TenantService tenantService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Tenant getTenantById(HttpServletRequest request,
                                @PathVariable("id") Long id) {
        Tenant tenant;
        tenant = tenantService.getTenantById(id);
        return tenant;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Tenant> getAllTenantsByStatus(HttpServletRequest request,
                                              @RequestParam(name = "status") String status ) {
        List<Tenant> tenant;
        tenant = tenantService.getAllTenantsByStatus(status);
        return tenant;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Tenant creteTenant(HttpServletRequest request,
                          @RequestBody Tenant requestTenant){

        Tenant tenant =tenantService.createAndUpdateTenant(requestTenant);

        return tenant;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Tenant UpdateTenant(HttpServletRequest request,
                          @RequestBody Tenant requestTenant){

        Tenant tenant =tenantService.createAndUpdateTenant(requestTenant);

        return tenant;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteTenantById(HttpServletRequest request,
                                @PathVariable("id") Long id) {

        tenantService.deleteTenantById(id);

    }
}
