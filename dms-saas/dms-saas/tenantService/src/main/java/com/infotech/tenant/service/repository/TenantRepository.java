package com.infotech.tenant.service.repository;

import com.infotech.tenant.service.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantRepository  extends JpaRepository<Tenant,Long> {

    List<Tenant> findAllByStatus(String status);
}
