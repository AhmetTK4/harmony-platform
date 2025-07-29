package com.atk.userservice.repository;

import com.atk.userservice.entity.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {

        Optional<UserInformation> findByUsername(String username);
}
