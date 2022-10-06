package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import com.infotech.docyard.dochandling.util.AppUtility;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class DLDocumentDTO extends BaseDTO<DLDocumentDTO, DLDocument> implements Serializable {

    private String title;
    private String size;
    private String mimeType;
    private String location;
    private String extension;
    private Double currentVersion;
    private String content;
    private String subject;
    private Boolean isShared;
    private String shareType;
    private String name;
    private Boolean isLeafNode;
    private String versionGUId;
    private Double version;
    private Boolean isFavourite;
    private String description;
    private ZonedDateTime archivedOn;
    private Boolean isArchived;
    private Boolean isFolder;
    private Long parentId;
    private List<Long> documentActivityIds;
    private List<Long> documentCommentIds;
    private List<Long> documentTagIds;
    private String createdByName;
    private String updatedByName;
    private List<DLDocumentCommentDTO> dlDocumentCommentDTOList;

    public DLDocumentDTO() {

    }

    @Override
    public DLDocument convertToEntity() {
        DLDocument dlDocument = new DLDocument();
        dlDocument.setId(this.id);
        dlDocument.setParentId(this.parentId);
        dlDocument.setFolder(this.isFolder);
        dlDocument.setArchived(this.isArchived);
        dlDocument.setArchivedOn(this.archivedOn);
        dlDocument.setDescription(this.description);
        dlDocument.setFavourite(this.isFavourite);
        dlDocument.setVersion(this.version);
        dlDocument.setVersionGUId(this.versionGUId);
        dlDocument.setLeafNode(this.isLeafNode);
        dlDocument.setName(this.name);
        dlDocument.setShareType(this.shareType);
        dlDocument.setShared(this.isShared);
        dlDocument.setSubject(this.subject);
        dlDocument.setContent(this.content);
        dlDocument.setCurrentVersion(this.currentVersion);
        dlDocument.setExtension(this.extension);
        dlDocument.setLocation(this.location);
        dlDocument.setMimeType(this.mimeType);
        dlDocument.setSize(this.size);
        dlDocument.setTitle(this.title);
        dlDocument.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        dlDocument.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        dlDocument.setCreatedBy(this.getCreatedBy());
        dlDocument.setUpdatedBy(this.getUpdatedBy());
        return dlDocument;
    }

    @Override
    public void convertToDTO(DLDocument entity, boolean partialFill) {
        this.id = entity.getId();
        this.parentId = entity.getParentId();
        this.isFolder = entity.getFolder();
        this.isArchived = entity.getArchived();
        this.archivedOn = entity.getArchivedOn();
        this.description = entity.getDescription();
        this.isFavourite = entity.getFavourite();
        this.version = entity.getVersion();
        this.versionGUId = entity.getVersionGUId();
        this.isLeafNode = entity.getLeafNode();
        this.name = entity.getName();
        this.shareType = entity.getShareType();
        this.isShared = entity.getShared();
        this.subject = entity.getSubject();
        this.content = entity.getContent();
        this.currentVersion = entity.getCurrentVersion();
        this.extension = entity.getExtension();
        this.location = entity.getLocation();
        this.mimeType = entity.getMimeType();
        this.size = entity.getSize();
        this.title = entity.getTitle();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
        if(!partialFill){
            fillDlDocumentComments(entity.getDocumentComments());
        }
    }

    @Override
    public DLDocumentDTO convertToNewDTO(DLDocument entity, boolean partialFill) {
        DLDocumentDTO dlDocumentDTO = new DLDocumentDTO();
        dlDocumentDTO.convertToDTO(entity, partialFill);
        return dlDocumentDTO;
    }


    public void fillDlDocumentComments(List<DLDocumentComment> dlDocumentCommentList) {
        for (DLDocumentComment cm : dlDocumentCommentList) {
            this.dlDocumentCommentDTOList.add(new DLDocumentCommentDTO().convertToNewDTO(cm, true));
        }
    }

}
