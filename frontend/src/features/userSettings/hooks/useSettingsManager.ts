import { useSelector } from "react-redux";
import { useUpdateSettingsMutation } from "../../../shared/api/userApiSlice";
import { SettingsDTO } from "../../../shared/types/dto";
import { useCallback } from "react";
import {selectCurrentSettings} from "../settingsSlice";

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
            console.error("Cannot update settings: user is not logged in.");
            return undefined;
        }

        try{
            return await updateSettingsMutation(newSettings).unwrap();
        }
        catch (err) {
            console.error("Failed to update settings:", err);
            return undefined;
        }
    }, [settings, updateSettingsMutation])

    return { settings, updateSettings, isLoading, error};
}