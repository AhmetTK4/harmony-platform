package com.atk.userservice.controller;

import com.atk.userservice.dto.UserInformationDto;
import com.atk.userservice.service.impl.UserInformationServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserInformationController {

    private final UserInformationServiceImpl service;

    public UserInformationController(UserInformationServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public List<UserInformationDto> getAllUsers(){
        return service.getAllUsers();
    }

    @PostMapping
    public UserInformationDto createUser(@RequestBody UserInformationDto userInformationDto){
        return service.createUser(userInformationDto);
    }
}
