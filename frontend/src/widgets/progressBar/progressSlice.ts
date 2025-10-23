import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";
import { ProgressUpdateDTO } from "../../shared/types/dto";
import {RootState} from "../../app/store";
import { logOut } from "../../features/authentication/authSlice";
import {GenerationType} from "../../shared/types/types";

export interface  ProgressEntry {
    generationType: GenerationType;
    userId: number;
    parentId: string;
    progressData: ProgressUpdateDTO;
    language: string;
    difficulty: string;
}

interface ProgressState {
    [taskId: string]: ProgressEntry;
}

const initialState: ProgressState = {};

const progressSlice = createSlice({
    name: "progress",
    initialState,
    reducers: {
        startGenerationTracking(state, action: PayloadAction<{
            taskId: string;
            language: string;
            difficulty: string;
            userId: number;
            generationType: GenerationType;
            parentId: string; }>
        ) {
            const { taskId, language, difficulty, generationType, parentId, userId } = action.payload;
            state[taskId] = {
                progressData: {
                    taskId,
                    progress: 0,
                    message: 'Initializing...',
                    isComplete: false,
                    data: undefined,
                    isError: false,
                },
                language,
                difficulty,
                generationType,
                parentId,
                userId,
            };
        },

        updateProgress(state, action: PayloadAction<ProgressUpdateDTO>) {
            const taskId = action.payload.taskId;
            if (state[taskId]) {
                state[taskId]!.progressData = action.payload;
            }
        },

        clearProgressTask(state, action: PayloadAction<string>) {
            const taskId = action.payload;
            delete state[taskId];
        }
    },
    extraReducers: (builder) => {
        builder.addCase(logOut, () => {
            return initialState; // Reset to initial empty state on logout
        });
    }
});

/*//////////////////////////////////////////////////////////////*/
/*                          SELECTORS                           */
/*//////////////////////////////////////////////////////////////*/
export const { startGenerationTracking, updateProgress, clearProgressTask } = progressSlice.actions;
export const selectProgressByTaskId = (state: RootState, taskId: string): ProgressUpdateDTO | undefined => state.progress[taskId]?.progressData;
export const selectIsAnyGenerationLoading = (state: RootState): boolean => {
    return Object.values(state.progress || {}).some(task => task && task.progressData && !task.progressData.isComplete && !task.progressData.isError);
};

export const selectActiveTaskIdForContext = (
    state: RootState,
    { language, difficulty, generationType }: { language: string, difficulty: string, generationType: GenerationType }
): string | undefined => {
    return Object.values(state.progress || {})
        .find(task =>
            task &&
            task.language === language &&
            task.difficulty === difficulty &&
            task.generationType === generationType &&
            !task.progressData.isComplete &&
            !task.progressData.isError
        )
        ?.progressData.taskId;
};

export const selectTaskIdForContext = (
    state: RootState,
    { language, difficulty, generationType }: { language: string, difficulty: string, generationType: GenerationType }
): string | undefined => {
    return Object.values(state.progress || {})
        .find(task =>
            task &&
            task.language === language &&
            task.difficulty === difficulty &&
            task.generationType === generationType
        )
        ?.progressData.taskId;
};
export default progressSlice.reducer;