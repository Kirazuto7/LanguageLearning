import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { ProgressUpdateDTO } from "../../types/dto";

interface ProgressState {
    [taskId: string]: ProgressUpdateDTO | undefined;
}

const initialState: ProgressState = {};

const progressSlice = createSlice({
    name: "progress",
    initialState,
    reducers: {
        updateProgress(state, action: PayloadAction<ProgressUpdateDTO>) {
            state[action.payload.taskId] = action.payload;
        },
        clearProgress(state, action: PayloadAction<string>) {
            state[action.payload] = undefined;
        }
    }
});

export const { updateProgress, clearProgress } = progressSlice.actions;
export const selectProgressByTaskId = (state: { progress: ProgressState }, taskId: string) => state.progress[taskId];

export default progressSlice.reducer;