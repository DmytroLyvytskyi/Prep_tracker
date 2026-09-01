package com.example.Prep_tracker.mapper;

import com.example.Prep_tracker.dto.UserResponse;
import com.example.Prep_tracker.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail()
        );
    }
}