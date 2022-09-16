package com.infotech.docyard.service;

import com.infotech.docyard.dl.entity.Group;
import com.infotech.docyard.dl.entity.Role;
import com.infotech.docyard.dl.repository.AdvSearchRepository;
import com.infotech.docyard.dl.repository.GroupRepository;
import com.infotech.docyard.dl.repository.RoleRepository;
import com.infotech.docyard.dto.GroupDTO;
import com.infotech.docyard.dto.RoleDTO;
import com.infotech.docyard.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public List<Group> searchGroup(String code, String name, String status, List<Long>role) {
        log.info("searchGroup method called..");

        return advSearchRepository.searchGroup(code, name, status,role);
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
    public Group saveAndUpdateGroup(GroupDTO groupDTO) {
        log.info("saveAndUpdateGroup method called..");

        Group group = groupDTO.convertToEntity();
        if(AppUtility.isEmpty(group.getGroupRoles())){
            group.setGroupRoles(groupDTO.groupRoles(group));
        }
        Group groups = groupRepository.save(group);
        return groups;
    }

    @Transactional
    public void deleteGroup(Long id) {
        log.info("deleteGroup method called..");

        groupRepository.deleteById(id);
    }
}
