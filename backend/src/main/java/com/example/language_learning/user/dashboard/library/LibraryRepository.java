package com.example.language_learning.user.dashboard.library;

import java.util.List;

public interface LibraryRepository {
    List<LibraryItem> findLibraryItemsByUserId(Long userId);
}
