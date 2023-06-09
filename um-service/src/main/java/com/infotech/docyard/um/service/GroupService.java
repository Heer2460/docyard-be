package com.infotech.docyard.um.service;

import com.infotech.docyard.um.dl.entity.Group;
import com.infotech.docyard.um.dl.repository.AdvSearchRepository;
import com.infotech.docyard.um.dl.repository.GroupRepository;
import com.infotech.docyard.um.dl.repository.GroupRoleRepository;
import com.infotech.docyard.um.dl.repository.UserRepository;
import com.infotech.docyard.um.dto.GroupDTO;
import com.infotech.docyard.um.exceptions.DBConstraintViolationException;
import com.infotech.docyard.um.exceptions.DataValidationException;
import com.infotech.docyard.um.util.AppConstants;
import com.infotech.docyard.um.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DataBindingException;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AdvSearchRepository advSearchRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRoleRepository groupRoleRepository;

    public List<Group> searchGroup(String code, String name, String status, List<Long> role) {
        log.info("searchGroup method called..");

        return advSearchRepository.searchGroup(code, name, status, role);
    }

    public List<Group> getAllGroup() {
        log.info("getAllRole method called..");

        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        log.info("getGroupById method called..");

        Optional<Group> group = groupRepository.findById(id);
        if (group.isPresent()) {
            return group.get();
        }
        return null;
    }

    @Transactional
    public Group saveGroup(GroupDTO groupDTO) {
        log.info("saveAndUpdateGroup method called..");

        Group group = groupDTO.convertToEntity();
        if (AppUtility.isEmpty(group.getGroupRoles())) {
            group.setGroupRoles(groupDTO.groupRoles(group));
        }
        if (groupRepository.existsByCode(groupDTO.getCode())) {
            throw new DBConstraintViolationException("Code Already Exists");
        }
        Group groups = groupRepository.save(group);
        return groups;
    }

    @Transactional
    public Group UpdateGroup(GroupDTO groupDTO) {
        log.info("saveAndUpdateGroup method called..");

        Optional<Group> optionalGroup = groupRepository.findById(groupDTO.getId());
        Group group = groupDTO.convertToEntityUpdate(optionalGroup.get());

        if (groupDTO.getStatus().equalsIgnoreCase(AppConstants.Status.SUSPEND)) {
            if (groupRoleRepository.existsByGroup_IdAndRole_Status(groupDTO.getId(), AppConstants.Status.ACTIVE)
                    || userRepository.existsByGroup_IdAndStatus(groupDTO.getId(), AppConstants.Status.ACTIVE)) {
                throw new DataValidationException(AppUtility.getResourceMessage("record.cannot.be.suspended.dependency"));
            }
        }
        if (AppUtility.isEmpty(group.getGroupRoles())) {
            group.setGroupRoles(groupDTO.groupRoles(group));
        }
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long id) {
        log.info("deleteGroup method called..");

        if (userRepository.existsByGroup_IdAndStatus(id, AppConstants.Status.ACTIVE)) {
            throw new DataValidationException(AppUtility.getResourceMessage("record.cannot.be.deleted.dependency"));
        }

        groupRepository.deleteById(id);
    }
}
