import { useSelector } from "react-redux";
import { useUpdateSettingsMutation } from "../../../shared/api/userApiSlice";
import { SettingsDTO } from "../../../shared/types/dto";
import { useCallback, useMemo } from "react";
import {selectCurrentSettings} from "../settingsSlice";
import {logToServer} from "../../../shared/utils/loggingService";

/* ------------------------------------------------------------- */
/* ---   Hook to Handle Fetching & Updating User Settings    --- */
/* ------------------------------------------------------------- */

interface SettingsManagerResult {
    settings: SettingsDTO | null,
    updateSettings: (newSettings: Partial<Omit<SettingsDTO, 'id'>>) => Promise<SettingsDTO | undefined>;
    isLoading: boolean,
    error: any,
}

export function useSettingsManager(): SettingsManagerResult {
    const settings = useSelector(selectCurrentSettings);
    const [updateSettingsMutation, {isLoading, error}] = useUpdateSettingsMutation();

    const updateSettings = useCallback(async(newSettings: Partial<Omit<SettingsDTO, 'id'>>) => {
        if(!settings) {
            logToServer('error', "Cannot update settings: user is not logged in.");
            return undefined;
        }

        // Merge the new settings with the existing ones to prevent overwriting.
        const mergedSettings = { ...settings, ...newSettings };

        try{
            // Send the complete, merged settings object to the backend.
            return await updateSettingsMutation(mergedSettings).unwrap();
        }
        catch (err) {
            console.error("Failed to update settings:", err);
            return undefined;
        }
    }, [settings, updateSettingsMutation])

    return useMemo(() => ({
        settings,
        updateSettings,
        isLoading,
        error
    }), [settings, updateSettings, isLoading, error]);
}