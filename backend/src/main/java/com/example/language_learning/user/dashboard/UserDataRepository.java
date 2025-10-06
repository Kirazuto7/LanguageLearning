package com.example.language_learning.user.dashboard;

public interface UserDataRepository {
    UserDataDTO findUserDataByUserId(Long userId);
}
