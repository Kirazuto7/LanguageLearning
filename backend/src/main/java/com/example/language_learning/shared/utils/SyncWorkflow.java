package com.example.language_learning.shared.utils;

import com.example.language_learning.shared.exceptions.SyncWorkflowException;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple tool for executing a linear sequence of synchronous operations.
 * Each task in the workflow is executed in order, operating on a shared context object.
 *
 * @param <I> The type of the immutable input object for the workflow.
 * @param <O> The type of the mutable output object that accumulates results.
 */
public class SyncWorkflow<I, O> {
    private final List<SyncTask<I, O>> tasks;

    private SyncWorkflow(List<SyncTask<I, O>> tasks) {
        this.tasks = tasks;
    }

    /**
     * Represents a single, executable task in a {@link SyncWorkflow}.
     * This is a nested interface to encapsulate it within the workflow's API.
     */
    @FunctionalInterface
    public interface SyncTask<I, O> {
        void execute(I input, O output) throws Exception;
    }

    /**
     * Executes all tasks in the workflow sequentially.
     * If any step throws an exception, the workflow is halted and the exception is re-thrown.
     *
     * @param input The immutable input data for the workflow.
     * @param output The mutable output object to be populated by the tasks.
     */
     public void execute(I input, O output) {
        try {
            for (SyncTask<I, O> task : tasks) {
                task.execute(input, output);
            }
        }
        catch (Exception e) {
            throw new SyncWorkflowException("Workflow execution failed at task: " + e.getClass().getSimpleName(), e);
        }
     }

    public static class Builder<I, O> {
        private final List<SyncTask<I, O>> tasks = new ArrayList<>();

        public Builder<I, O> addTask(SyncTask<I, O> task) {
            this.tasks.add(task);
            return this;
        }

        public SyncWorkflow<I, O> build() {
            return new SyncWorkflow<>(List.copyOf(tasks));
        }
    }
}
