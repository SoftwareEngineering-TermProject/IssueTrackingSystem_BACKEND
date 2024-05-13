package com.example.IssueTrackingSystem.converter;

import com.example.IssueTrackingSystem.domain.entity.User;
import com.example.IssueTrackingSystem.web.dto.UserRequestDTO;
import com.example.IssueTrackingSystem.web.dto.UserResponseDTO;

public class UserConverter {

    public static User toUser(UserRequestDTO.CreateUserRequestDTO request){
        return User.builder()
                .name(request.getName())
                .userRole(request.getUserRole())
                .userName(request.getUserName())
                .password(request.getPassword())
                .build();
    }

    public static UserResponseDTO.CreateUserResultDTO toCreateResultDTO(User user){
        return UserResponseDTO.CreateUserResultDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserRequestDTO.SignInRequestDTO request){
        return User.builder()
                .userName(request.getUserName())
                .password(request.getPassword())
                .build();
    }

    public static UserResponseDTO.SignInResultDTO toSignInResultDTO(User user){
        return UserResponseDTO.SignInResultDTO.builder()
                .userId(user.getUserId())
                .build();
    }
}