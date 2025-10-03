import {StoryVocabularyItemDTO, StoryVocabularyPageDTO} from "../../../../shared/types/dto";
import React from "react";
import {renderWord} from "../../../../shared/utils/renderUtils";

interface StoryVocabularyPageProps {
    page: StoryVocabularyPageDTO;
}

/**
 * Renders the content for a story vocabulary page, displaying words and their translations.
 */
const StoryVocabularyPage: React.FC<StoryVocabularyPageProps> = ({ page }) => {
    return(
        <div>
            <h5 className="text-center mb-4">Vocabulary</h5>
            <table className="table table-striped">
                <tbody>
                    {page.vocabulary.map((vocabItem: StoryVocabularyItemDTO, index) =>(
                        // Use the item's ID if it exists, otherwise fall back to the array index.
                        <tr key={vocabItem.id ?? index}>
                            <td><strong>{vocabItem.word}</strong></td>
                            <td>{vocabItem.translation}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default StoryVocabularyPage;