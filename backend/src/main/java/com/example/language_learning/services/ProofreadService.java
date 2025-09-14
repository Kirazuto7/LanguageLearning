package com.example.language_learning.services;

import com.example.language_learning.entity.user.User;
import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.example.language_learning.services.contexts.ProofreadContext;
import com.example.language_learning.services.states.ProofreadState;
import com.example.language_learning.utils.ReactiveStateMachine;
import com.example.language_learning.utils.ReactiveStateMachineFactory;
import com.example.language_learning.requests.PracticeLessonCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProofreadService {

    private final ReactiveStateMachineFactory<ProofreadState, ProofreadContext> stateMachineFactory;

    public Mono<PracticeLessonCheckResponse> checkSentence(PracticeLessonCheckRequest request, User user) {
        ProofreadContext context = new ProofreadContext(request, user);
        ReactiveStateMachine<ProofreadState, ProofreadContext> sm = stateMachineFactory.createInstance();

        return sm.runToCompletion(context)
                .onCompletion(ProofreadState.COMPLETED.class, ProofreadState.COMPLETED::response)
                .onError(ProofreadState.FAILED.class, failed -> new RuntimeException(failed.reason()))
                .asMono();
    }
}
