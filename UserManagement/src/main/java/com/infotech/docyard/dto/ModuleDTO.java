package com.infotech.docyard.dto;

import com.infotech.docyard.dl.entity.Module;
import com.infotech.docyard.dl.entity.ModuleAction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleDTO extends BaseDTO<ModuleDTO, Module> {

    private Long moduleId;
    private String name;
    private String slug;
    private String route;
    private String icon;
    private Integer seq;
    private String status;
    private String catSlug;
    private String catName;
    private List<ModuleDTO> children;
    private List<ModuleActionDTO> moduleActionDTOList = new ArrayList<>();

    @Override
    public Module convertToEntity() {
        return null;
    }

    @Override
    public void convertToDTO(Module entity, boolean partialFill) {
        this.moduleId = entity.getId();
        this.name = entity.getName();
        this.slug = entity.getSlug();
        this.route = entity.getRoute();
        this.icon = entity.getIcon();
        this.seq = entity.getSeq();
        this.status = entity.getStatus();
        this.catSlug = entity.getCatSlug();
        this.catName = entity.getCatName();
        if (!partialFill) {
            this.fillModuleActions(entity.getModuleActions());
        }
        this.createdOn = entity.getCreatedOn();
        this.updatedOn = entity.getUpdatedOn();
    }

    @Override
    public ModuleDTO convertToNewDTO(Module entity, boolean partialFill) {
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.convertToDTO(entity, partialFill);
        return moduleDTO;
    }

    public void fillModuleActions(List<ModuleAction> moduleActionList) {
        for (ModuleAction ma : moduleActionList) {
            this.moduleActionDTOList.add(new ModuleActionDTO().convertToNewDTO(ma, true));
        }
    }
}
