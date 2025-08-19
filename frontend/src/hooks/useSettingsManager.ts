import { useSelector } from "react-redux";
import { RootState } from "../app/store";
import { useUpdateSettingsMutation } from "../features/api/userApiSlice";
import { SettingsDTO } from "../types/dto";
import { useCallback } from "react";

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
    const { user } = useSelector((state: RootState) => state.auth);
    const settings = user?.settings ?? null;
    const [updateSettingsMutation, {isLoading, error}] = useUpdateSettingsMutation();

    const updateSettings = useCallback(async(newSettings: Partial<Omit<SettingsDTO, 'id'>>) => {
        if(!user) {
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
    }, [user, updateSettingsMutation])

    return { settings, updateSettings, isLoading, error};
}