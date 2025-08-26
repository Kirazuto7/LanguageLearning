import {useProofreadMutation} from "../features/api/bookApiSlice";
import {useSettingsManager} from "./useSettingsManager";
import {PracticeLessonCheckResponse} from "../types/dto";

type ProofreadHookRequest = {
    questionId: number;
    userSentence: string;
}

export const useProofread = () => {
    const [proofread, { isLoading, error: proofreadError }] = useProofreadMutation();
    const { settings } = useSettingsManager();

    const checkSentence = async ({ questionId, userSentence}: ProofreadHookRequest): Promise<PracticeLessonCheckResponse> => {
        const language = settings?.language;
        if (!language) {
            throw new Error("Cannot proofread: Language setting is unknown.");
        }

        return await proofread({
            language,
            questionId,
            userSentence
        }).unwrap();
    };

    return { checkSentence, isLoading, proofreadError };
};