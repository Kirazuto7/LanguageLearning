package com.example.language_learning.config;

import com.example.language_learning.ai.actions.ChapterPrepActions;
import com.example.language_learning.ai.inputs.ChapterPrepInput;
import com.example.language_learning.ai.outputs.ChapterPrepOutput;
import com.example.language_learning.shared.utils.SyncWorkflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PipelineConfig {

    @Bean
    public SyncWorkflow<ChapterPrepInput, ChapterPrepOutput> chapterPrepWorkflow(ChapterPrepActions actions) {
        return new SyncWorkflow.Builder<ChapterPrepInput, ChapterPrepOutput>()
                .addTask(actions::generateTaskId)
                .addTask(actions::findOrCreateBook)
                .addTask(actions::createInitialChapter)
                .addTask(actions::calculateStartingPage)
                .build();
    }
}
