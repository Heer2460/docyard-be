package com.infotech.user.service.api;


import com.infotech.user.service.dto.UserDTO;
import com.infotech.user.service.entity.User;
import com.infotech.user.service.service.UMService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/users")
@Log4j2
public class UserAPI {

    @Autowired
    private UMService umService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UserDTO getUserById(HttpServletRequest request,
                               @PathVariable("id") Long id){

        UserDTO userDTO =umService.getUserByID(id);

        return userDTO;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<User> getAllUsers(HttpServletRequest request){

        return umService.getAllUsers();


    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public User creteUser(HttpServletRequest request,
                             @RequestBody User user){

        User serviceUser =umService.createAndUpdateUser(user);

        return serviceUser;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public User updateUser(HttpServletRequest request,
                             @RequestBody User user){

        User serviceUser =umService.createAndUpdateUser(user);

        return serviceUser;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUserById(HttpServletRequest request,
                               @PathVariable("id") Long id){

       umService.deleteUserById(id);

    }
}
