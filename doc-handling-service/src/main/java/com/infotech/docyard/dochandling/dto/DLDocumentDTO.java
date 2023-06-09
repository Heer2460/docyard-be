package com.infotech.docyard.dochandling.dto;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import com.infotech.docyard.dochandling.util.AppUtility;
import com.infotech.docyard.dochandling.util.DateTimeUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DLDocumentDTO extends BaseDTO<DLDocumentDTO, DLDocument> implements Serializable {

    private String title;
    private String size;
    private Long sizeBytes;
    private String mimeType;
    private String location;
    private String extension;
    private Double currentVersion;
    private String content;
    private boolean ocrDone;
    private boolean ocrSupported;

    private String subject;
    private Boolean shared;
    private Long dlShareId;
    private String shareType;
    private String name;
    private Boolean isLeafNode;
    private String versionGUId;
    private String guId;
    private Double version;
    private Boolean favourite;
    private String description;

    private Integer daysArchived;
    private ZonedDateTime archivedOn;
    private Boolean archived;
    private Boolean folder;
    private Boolean checkedIn;
    private Long parentId;
    private Long checkedInBy;
    private List<Long> documentActivityIds;
    private List<Long> documentCommentIds;
    private List<Long> documentTagIds;
    private String createdByName;
    private String updatedByName;
    private String updatedOnDetail;
    private List<DLDocumentCommentDTO> dlDocumentCommentDTOList;

    public DLDocumentDTO() {

    }

    @Override
    public DLDocument convertToEntity() {
        DLDocument dlDocument = new DLDocument();
        dlDocument.setId(this.id);
        dlDocument.setParentId(this.parentId);
        dlDocument.setFolder(this.folder);
        dlDocument.setArchived(this.archived);
        dlDocument.setArchivedOn(this.archivedOn);
        dlDocument.setDescription(this.description);
        dlDocument.setFavourite(this.favourite);
        dlDocument.setVersion(this.version);
        dlDocument.setVersionGUId(this.versionGUId);
        dlDocument.setLeafNode(this.isLeafNode);
        dlDocument.setName(this.name);
        dlDocument.setShareType(this.shareType);
        dlDocument.setShared(this.shared);
        dlDocument.setSubject(this.subject);
        dlDocument.setContent(this.content);
        dlDocument.setOcrDone(this.ocrDone);
        dlDocument.setOcrSupported(this.ocrSupported);
        dlDocument.setCurrentVersion(this.currentVersion);
        dlDocument.setExtension(this.extension);
        dlDocument.setLocation(this.location);
        dlDocument.setMimeType(this.mimeType);
        dlDocument.setDaysArchived((this.daysArchived));
        dlDocument.setSize(this.size);
        dlDocument.setSizeBytes(this.sizeBytes);
        dlDocument.setTitle(this.title);
        dlDocument.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        dlDocument.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        dlDocument.setCreatedBy(this.getCreatedBy());
        dlDocument.setUpdatedBy(this.getUpdatedBy());
        dlDocument.setCheckedIn(this.checkedIn);
        dlDocument.setCheckedInBy(this.checkedInBy);
        return dlDocument;
    }

    @Override
    public void convertToDTO(DLDocument entity, boolean partialFill) {
        this.id = entity.getId();
        this.parentId = entity.getParentId();
        this.folder = entity.getFolder();
        this.archived = entity.getArchived();
        this.archivedOn = entity.getArchivedOn();
        this.description = entity.getDescription();
        this.favourite = entity.getFavourite();
        this.version = entity.getVersion();
        this.versionGUId = entity.getVersionGUId();
        this.isLeafNode = entity.getLeafNode();
        this.name = entity.getName();
        this.shareType = entity.getShareType();
        this.shared = entity.getShared();
        this.dlShareId = entity.getDlShareId();
        this.subject = entity.getSubject();
        this.content = entity.getContent();
        this.ocrDone = entity.getOcrDone();
        this.ocrSupported = entity.getOcrSupported();
        this.currentVersion = entity.getCurrentVersion();
        this.extension = entity.getExtension();
        this.location = entity.getLocation();
        this.mimeType = entity.getMimeType();
        this.daysArchived = entity.getDaysArchived();
        this.size = entity.getSize();
        this.sizeBytes = entity.getSizeBytes();
        this.title = entity.getTitle();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
        this.checkedIn = entity.getCheckedIn();
        this.checkedInBy=entity.getCheckedInBy();
        this.updatedOnDetail = DateTimeUtil.convertDateToUFDateFormat(entity.getUpdatedOn());
        if (!partialFill) {
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
        if (AppUtility.isEmpty(this.dlDocumentCommentDTOList)) {
            this.dlDocumentCommentDTOList = new ArrayList<>();
        } else {
            this.dlDocumentCommentDTOList.clear();
        }
        for (DLDocumentComment cm : dlDocumentCommentList) {
            this.dlDocumentCommentDTOList.add(new DLDocumentCommentDTO().convertToNewDTO(cm, true));
        }
    }

}
