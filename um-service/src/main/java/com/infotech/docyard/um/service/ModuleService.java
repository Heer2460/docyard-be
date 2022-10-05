package com.infotech.docyard.um.service;

import com.infotech.docyard.um.dl.entity.Module;
import com.infotech.docyard.um.dl.repository.ModuleRepository;
import com.infotech.docyard.um.dto.ModuleDTO;
import com.infotech.docyard.um.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    public List<ModuleDTO> getAllModuleWithModuleActions() {
        log.info("getAllModuleWithModuleActions method called..");
        List<Module> moduleList = moduleRepository.findAllModules();

        return this.getMenuList(moduleList);
    }

    private List<ModuleDTO> getMenuList(List<Module> moduleList) {
        List<Long> specificModuleIds = new ArrayList<>();
        Map<Long, ModuleDTO> map = new HashMap<>();
        for (Module m : moduleList) {
            if (m.getSlug().equalsIgnoreCase("com.infotech.docyard.gateway.config")
                    || m.getSlug().equalsIgnoreCase("report")) {
                specificModuleIds.add(m.getId());
            }
            if (AppUtility.isEmpty(m.getParentId())) {
                if (!map.containsKey(m.getParentId())) {
                    map.put(m.getId(), new ModuleDTO().convertToNewDTO(m, false));
                }
            } else {
                ModuleDTO parent = map.get(m.getParentId());
                if (AppUtility.isEmpty(parent)) {
                    parent = new ModuleDTO().convertToNewDTO(moduleRepository.findById(m.getParentId()).get(), true);
                    map.put(m.getParentId(), parent);
                }
                if (AppUtility.isEmpty(parent.getChildren())) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(new ModuleDTO().convertToNewDTO(m, false));
            }
        }
        for (Long moduleId : specificModuleIds) {
            ModuleDTO refModuleDTO = map.get(moduleId);
            if (!AppUtility.isEmpty(refModuleDTO)) {
                refModuleDTO.setChildren(new ArrayList<>());
                List<Module> refChildModuleList = moduleRepository.findAllByParentId(moduleId);
                for (Module m : refChildModuleList) {
                    ModuleDTO firstParent = new ModuleDTO().convertToNewDTO(m, false);
                    refModuleDTO.getChildren().add(firstParent);
                    List<Module> refChildModList = moduleRepository.findAllByParentId(m.getId());
                    for (Module mod : refChildModList) {
                        ModuleDTO child = new ModuleDTO();
                        child.convertToDTO(mod, false);
                        if (AppUtility.isEmpty(child.getChildren())) {
                            child.setChildren(new ArrayList<>());
                        }
                        child.getChildren().add(new ModuleDTO().convertToNewDTO(mod, false));
                        if (AppUtility.isEmpty(firstParent.getChildren())) {
                            firstParent.setChildren(new ArrayList<>());
                        }
                        firstParent.getChildren().add(child);
                    }
                }
            }
            List<ModuleDTO> sortedList = refModuleDTO.getChildren().stream().sorted(Comparator.comparing(ModuleDTO::getSeq)).collect(Collectors.toList());
            refModuleDTO.setChildren(sortedList);
        }
        return new ArrayList<>(map.values());
    }
}
