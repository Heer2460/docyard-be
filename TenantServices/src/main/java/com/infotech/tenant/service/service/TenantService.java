package com.infotech.tenant.service.service;

import com.infotech.tenant.service.entity.Tenant;
import com.infotech.tenant.service.repository.TenantRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;

    public Tenant getTenantById(Long id) {
    log.info("getTenantById method called...");
        return tenantRepository.findById(id).get();

    }

    public Tenant createAndUpdateTenant(Tenant requestTenant) {
        log.info("createTenant method called...");

        return tenantRepository.save(requestTenant);
    }

    public void deleteTenantById(Long id) {
        log.info("deleteTenantById method called...");

        tenantRepository.deleteById(id);
    }

    public List<Tenant> getAllTenantsByStatus(String status) {
        log.info("getAllTenantsByStatus method called...");

    return tenantRepository.findAllByStatus(status);
    }
}
