package com.infotech.docyard.um.dto;

import com.infotech.docyard.um.dl.entity.ModuleAction;
import lombok.Data;

@Data
public class ModuleActionDTO extends BaseDTO<ModuleActionDTO, ModuleAction> {

    private Long moduleActionId;
    private String title;
    private String slug;
    private Integer seq;
    private ModuleDTO moduleDTO;

    @Override
    public ModuleAction convertToEntity() {
        return null;
    }

    @Override
    public void convertToDTO(ModuleAction entity, boolean partialFill) {
        this.moduleActionId = entity.getId();
        this.title = entity.getTitle();
        this.slug = entity.getSlug();
        this.seq = entity.getSeq();
        this.createdOn = entity.getCreatedOn();
        this.updatedOn = entity.getUpdatedOn();
        if (!partialFill) {
            this.moduleDTO = new ModuleDTO();
            this.moduleDTO.convertToDTO(entity.getModule(), true);
        }
    }

    @Override
    public ModuleActionDTO convertToNewDTO(ModuleAction entity, boolean partialFill) {
        ModuleActionDTO ma = new ModuleActionDTO();
        ma.convertToDTO(entity, partialFill);
        return ma;
    }
}
