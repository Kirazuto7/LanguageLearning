import { apiSlice } from "./apiSlice";
import { ProgressUpdateDTO } from "../types/dto";
import {HttpMethod} from "../types/types";

export const progressApiSlice = apiSlice.injectEndpoints({
    endpoints: builder => ({
        getTaskStatus: builder.query<ProgressUpdateDTO, string>({
            query: (taskId) => ({
                url: `/progress/tasks/${taskId}/status`,
                method: HttpMethod.GET
            })
        })
    })
});

export const { useLazyGetTaskStatusQuery, useGetTaskStatusQuery } = progressApiSlice;