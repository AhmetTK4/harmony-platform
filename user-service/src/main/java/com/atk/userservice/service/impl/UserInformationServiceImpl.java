package com.atk.userservice.service.impl;

import com.atk.userservice.dto.UserInformationDto;
import com.atk.userservice.entity.UserInformation;
import com.atk.userservice.repository.UserInformationRepository;
import com.atk.userservice.service.UserInformationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInformationServiceImpl  implements UserInformationService {

    private final UserInformationRepository repository;

    public UserInformationServiceImpl(UserInformationRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<UserInformationDto> getAllUsers() {
        return repository.findAll().stream()
                .map(user -> new UserInformationDto(user.getId(),user.getUsername(),user.getEmail()))
                .toList();
    }

    @Override
    public UserInformationDto createUser(UserInformationDto userInformationDto) {
        UserInformation user = UserInformation.builder()
                .username(userInformationDto.username())
                .email(userInformationDto.email())
                .build();
        UserInformation saved = repository.save(user);
        return new UserInformationDto(saved.getId(), saved.getUsername(), saved.getEmail());
    }
}
