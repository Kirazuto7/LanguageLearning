import {ProgressEntry, updateProgress} from "../../widgets/progressBar/progressSlice";
import {logToServer} from "../../shared/utils/loggingService";
import {store} from "../store";
import {progressApiSlice} from "../../shared/api/progressApiSlice";
import {GenerationType} from "../../shared/types/types";
import {lessonBookApiSlice} from "../../shared/api/lessonBookApiSlice";
import {storyBookApiSlice} from "../../shared/api/storyBookApiSlice";


const POLLING_INTERVAL = 20000; // Poll every 20 seconds;
const POLLING_STORAGE_KEY = 'polling_tasks';

const activePolls = new Map<string, NodeJS.Timeout>();

function getPollingTasksFromStorage(): Record<string, boolean> {
    try {
        const tasks = localStorage.getItem(POLLING_STORAGE_KEY);
        return tasks ? JSON.parse(tasks) : {};
    }
    catch (e) {
        return {};
    }
}

function setPollingTasksInStorage(tasks: Record<string, boolean>): void {
    localStorage.setItem(POLLING_STORAGE_KEY, JSON.stringify(tasks));
}

/**
 * Starts polling the status of a task via a REST endpoint.
 * This is used as a fallback when a WebSocket connection closes unexpectedly.
 *
 * @param task The ProgressEntry object for the task to poll.
 */
async function pollTask(task: ProgressEntry) {
    const { progressData, language, difficulty, generationType } = task;
    const { taskId } = progressData;

    try {
        const result = await store.dispatch(
            progressApiSlice.endpoints.getTaskStatus.initiate(taskId, {
                forceRefetch: true,
            })
        ).unwrap();

        if (result) {
            const update = result;

            store.dispatch(updateProgress(update));

            if (update.isComplete || update.isError) {
                logToServer('info', `Polling for task ${taskId} detected completion. Stopping poll and cleaning up.`);
                stopPolling(taskId);

                // Trigger the final book refetch
                if (generationType === GenerationType.CHAPTER) {
                    store.dispatch(lessonBookApiSlice.endpoints.getLessonBook.initiate({ language, difficulty }, { forceRefetch: true }));
                }
                else if (generationType === GenerationType.STORY) {
                    store.dispatch(storyBookApiSlice.endpoints.getStoryBook.initiate({ language, difficulty }, { forceRefetch: true }));
                }
            }
        }
    }
    catch (err) {
        logToServer('error', `Polling for task ${taskId} failed. Stopping poll.`, { error: err });
        stopPolling(taskId);
    }
 }

/**
 * Starts the polling loop for a single task if it's not already running.
 */
function startPollingForTask(taskId: string) {
    const task = store.getState().progress[taskId];

    if (!task || activePolls.has(taskId)) {
        return;
    }

    if (task.progressData.isComplete || task.progressData.isError) {
        stopPolling(taskId);
        return;
    }

    logToServer('info', `WebSocket disconnected for ${taskId}. Starting REST polling fallback.`);
    const intervalId = setInterval(() => pollTask(task), POLLING_INTERVAL);
    activePolls.set(taskId, intervalId);
}

/**
 * Public function to add a task to be polled.
 * This is called from the onComplete handler in the API slice.
 */
export function addTaskToStorage(taskId: string): void {
    const tasks = getPollingTasksFromStorage();
    tasks[taskId] = true;
    setPollingTasksInStorage(tasks);
}

/**
 * Stops polling for a specific task.
 */
export function stopPolling(taskId: string) {
    const intervalId = activePolls.get(taskId);
    if (intervalId) {
        clearInterval(intervalId);
        activePolls.delete(taskId);
    }
    const tasks = getPollingTasksFromStorage();
    delete tasks[taskId];
    setPollingTasksInStorage(tasks);
    logToServer('info', `Stopped polling for task ${taskId}.`);
}

/**
 * Initializes the polling manager on app startup.
 * Reads from localStorage and starts polling for any tasks found.
 */
export function initializePolling(): void {
    logToServer('info', 'Initializing Polling Manager...');
    const tasksToPoll = getPollingTasksFromStorage();
    const taskIds = Object.keys(tasksToPoll);

    if (taskIds.length > 0) {
        logToServer('info', `Found ${taskIds.length} tasks to poll from previous session.`);
        taskIds.forEach(startPollingForTask);
    }
}