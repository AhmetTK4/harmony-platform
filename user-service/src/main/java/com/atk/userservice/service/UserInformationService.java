package com.atk.userservice.service;

import com.atk.userservice.dto.UserInformationDto;

import java.util.List;

public interface UserInformationService {
    List<UserInformationDto> getAllUsers();
    UserInformationDto createUser(UserInformationDto userInformationDto);
}
