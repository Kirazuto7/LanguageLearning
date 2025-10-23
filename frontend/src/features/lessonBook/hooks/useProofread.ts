import { useProofreadMutation } from "../../../shared/api/practiceLessonApiSlice";
import {useSettingsManager} from "../../userSettings/hooks/useSettingsManager";
import {PracticeLessonCheckResponse} from "../../../shared/types/dto";

type ProofreadHookRequest = {
    questionId: string;
    userSentence: string;
}

export const useProofread = () => {
    const [proofread, { isLoading, error: proofreadError }] = useProofreadMutation();
    const { settings } = useSettingsManager();

    const checkSentence = async ({ questionId, userSentence}: ProofreadHookRequest): Promise<PracticeLessonCheckResponse> => {
        const language = settings?.language;
        const difficulty = settings?.difficulty;

        if (!difficulty || !language) {
            throw new Error("Cannot proofread: Language or difficulty setting is unknown.");
        }

        return await proofread({
            language,
            difficulty,
            questionId,
            userSentence
        }).unwrap();
    };

    return { checkSentence, isLoading, proofreadError };
};