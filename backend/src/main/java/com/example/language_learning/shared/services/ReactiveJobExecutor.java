package com.example.language_learning.shared.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class ReactiveJobExecutor {

    private final Scheduler scheduler;

    /**
     * Creates a dedicated, bounded thread pool for executing reactive jobs.
     * This provides centralized concurrency control for all non-blocking, asynchronous tasks.
     *
     * @param workerCount The maximum number of threads in the pool, configured via application properties.
     */
    public ReactiveJobExecutor(@Value("${app.reactive-job-queue.workers:10}") int workerCount) {
        log.info("Initializing ReactiveJobExecutor with a bounded elastic scheduler of size {}", workerCount);
        this.scheduler = Schedulers.newBoundedElastic(workerCount, Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE, "reactive-jobs");
    }

    /**
     * Submits a true background reactive job (not tied to an HTTP request) to be
     * executed on the dedicated, concurrency-limited scheduler.
     */
    public <T> Mono<T> submitBackgroundJob(Mono<T> job) {
        log.debug("Submitting background reactive job to 'reactive-jobs' scheduler.");
        return Mono.defer(() -> job.subscribeOn(scheduler));
    }
}