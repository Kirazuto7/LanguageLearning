package com.example.language_learning.ai.config;

import com.example.language_learning.ai.config.model.StableDiffusionImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StableDiffusionImageModel implements ImageModel {
    private final StableDiffusionClient client;

    @Override
    public ImageResponse call(ImagePrompt prompt) {
        List<ImageGeneration> allImageGenerations = new ArrayList<>();
        for (ImageMessage message : prompt.getInstructions()) {
            StableDiffusionImageResponse response = client.promptTextToImage(message.getText());

            if (!response.getImages().isEmpty()) {
                Image image = new Image(response.getImages().getFirst(), "data:image/png;base64," + response.getImages().getFirst());
                allImageGenerations.add(new ImageGeneration(image));
            }
        }
        return new ImageResponse(allImageGenerations);
    }
}
