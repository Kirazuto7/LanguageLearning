package com.example.language_learning.storybook;

import com.example.language_learning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StoryBookGraphQlController {
    private final StoryBookService storyBookService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public void getReadingBook(@AuthenticationPrincipal User user) {

    }
}
