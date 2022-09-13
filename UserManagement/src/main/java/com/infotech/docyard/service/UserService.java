package com.infotech.docyard.service;

import com.infotech.docyard.dl.entity.User;
import com.infotech.docyard.dl.repository.AdvSearchRepository;
import com.infotech.docyard.dl.repository.UserRepository;
import com.infotech.docyard.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdvSearchRepository advSearchRepository;

    public List<User> searchUser(String username, String email, String name, String phoneNumber) {
        log.info("searchUser method called..");

        return advSearchRepository.searchUser(username, email, name, phoneNumber);
    }

    public List<User> getAllUsers() {
        log.info("getAllUsers method called..");

        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        log.info("getUserById method called..");

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Transactional
    public User saveAndUpdateUser(UserDTO userDTO) {
        log.info("saveAndUpdateUser method called..");

        return userRepository.save(userDTO.convertToEntity());
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("deleteUser method called..");

        userRepository.deleteById(id);
    }

}