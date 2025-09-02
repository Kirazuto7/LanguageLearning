import {TtsRequest} from "../../types/dto";
import {apiSlice} from "./apiSlice";

export const ttsApiSlice = apiSlice.injectEndpoints({
    endpoints: builder => ({
        speak: builder.mutation<Blob, TtsRequest>({
            query: (request) => ({
                url: '/tts/speak',
                method: 'POST',
                body: request,
                responseHandler: (response) => response.blob(),
            })
        }),

    })
});

export const { useSpeakMutation } = ttsApiSlice;