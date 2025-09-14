import { useTranslateMutation } from "../../../shared/api/translationApiSlice";
import { TranslationResponse } from "../../../shared/types/dto";
import { useCallback } from "react";
import { logToServer } from "../../../shared/utils/loggingService";
import {useSettingsManager} from "../../../features/userSettings/hooks/useSettingsManager";

/**
 * Custom hook to provide a simple interface for the text translation feature.
 * It encapsulates the RTK Query mutation hook and provides a clean `translate` function.
 *
 * @returns An object containing the `translate` function and the mutation's state (isLoading, isError, data).
 */
export const useTranslate = () => {
    const [translateMutation, { isLoading, isError, data, reset }] = useTranslateMutation();
    const { settings } = useSettingsManager();

    const translate = useCallback(async (textToTranslate: string): Promise<TranslationResponse> => {
        const sourceLanguage = settings?.language;

        if (!sourceLanguage) {
            const error = new Error("Cannot translate: Source language setting is not available.");
            logToServer('warn', error.message);
            throw error;
        }
        try {
            return await translateMutation({ textToTranslate, sourceLanguage }).unwrap();
        }
        catch (err) {
            logToServer('error', 'Failed to translate text:', { error: err });
            throw err;
        }
    }, [translateMutation, settings?.language]);

    return { translate, isLoading, isError, data, reset };
}