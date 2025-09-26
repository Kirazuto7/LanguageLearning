import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { ProgressUpdateDTO } from "../../shared/types/dto";
import {RootState} from "../../app/store";

interface  ProgressEntry {
    progressData: ProgressUpdateDTO;
    language: string;
    difficulty: string;
    isStale?: boolean;
}

interface ProgressState {
    [taskId: string]: ProgressEntry | undefined;
}

const initialState: ProgressState = {};

const progressSlice = createSlice({
    name: "progress",
    initialState,
    reducers: {
        startGenerationTracking(state, action: PayloadAction<{ taskId: string; language: string; difficulty: string }>) {
            const { taskId, language, difficulty } = action.payload;
            state[taskId] = {
                progressData: {
                    taskId,
                    progress: 0,
                    message: 'Initializing...',
                    isComplete: false,
                    data: undefined,
                    error: undefined,
                },
                language,
                difficulty
            };
        },

        updateProgress(state, action: PayloadAction<ProgressUpdateDTO>) {
            const taskId = action.payload.taskId;
            if (state[taskId]) {
                state[taskId]!.progressData = action.payload;
            }
        },

        markProgressAsStale(state, action: PayloadAction<string>) {
            const taskId = action.payload;
            if (state[taskId]) {
                state[taskId]!.isStale = true;
            }
        }
    }
});

export const { startGenerationTracking, updateProgress, markProgressAsStale } = progressSlice.actions;
export const selectProgressByTaskId = (state: RootState, taskId: string): ProgressUpdateDTO | undefined => state.progress[taskId]?.progressData;
export const selectIsAnyGenerationLoading = (state: RootState): boolean => {
    return Object.values(state.progress).some(task => task && !task.progressData.isComplete && !task.progressData.error && !task.isStale);
};

export const selectActiveTaskIdForContext = (state: RootState, { language, difficulty }: { language: string, difficulty: string }): string | undefined => {
    return Object.values(state.progress)
        .find(task => task && task.language === language && task.difficulty === difficulty && !task.progressData.isComplete && !task.progressData.error && !task.isStale)
        ?.progressData.taskId;
};

export default progressSlice.reducer;