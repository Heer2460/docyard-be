package com.infotech.docyard.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public abstract class BaseDTO<D, E> implements Serializable {

    public Long id;
    public ZonedDateTime createdOn;
    public ZonedDateTime updatedOn;
    public Long createdBy;
    public Long updatedBy;

    public abstract E convertToEntity();

    public abstract void convertToDTO(E entity, boolean partialFill);

    public abstract D convertToNewDTO(E entity, boolean partialFill);

}

