package com.example.language_learning.shared.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class JobQueueService {
    private final BlockingQueue<Runnable> jobQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService;
    private final int workerCount;

    public JobQueueService(@Value("${app.job-queue.workers:5}") int workerCount) {
        this.workerCount = workerCount;
        this.executorService = Executors.newFixedThreadPool(workerCount);
    }

    @PostConstruct
    public void startWorkers() {
        log.info("Starting {} job queue workers.", workerCount);
        for (int i = 0; i < workerCount; i++) {
            executorService.submit(this::work);
        }
    }

    private void work() {
        while( !Thread.currentThread().isInterrupted()) {
            try {
                Runnable job = jobQueue.take();
                log.info("Worker thread [{}] picked up a job. Queue size: {}", Thread.currentThread().getName(), jobQueue.size());
                job.run();
            }
            catch (InterruptedException e) {
                log.warn("Job queue worker thread [{}] was interrupted.", Thread.currentThread().getName());
                Thread.currentThread().interrupt();
                break;
            }
            catch (Exception e) {
                log.error("Uncaught exception during job execution in worker [{}].", Thread.currentThread().getName(), e);
            }
        }
        log.info("Worker thread [{}] is shutting down.", Thread.currentThread().getName());
    }

    public void submitJob(Runnable job) {
        try {
            jobQueue.put(job);
            log.info("Submitted a new job to the queue. Current queue size: {}", jobQueue.size());
        }
        catch (InterruptedException e) {
            log.error("Failed to submit job to the queue as the thread was interrupted.", e);
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down job queue executor service...");
        executorService.shutdownNow();
    }

 }
