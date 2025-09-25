import {StoryContentPageDTO} from "../../../../shared/types/dto";
import React from "react";

interface StoryContentPageProps {
    page: StoryContentPageDTO;
}

const StoryContentPage: React.FC<StoryContentPageProps> = ({ page }) => {
    return(
        <div>
            {page.imageUrl && <img src={page.imageUrl} alt="Story illustration" style={{ maxWidth: '100%', marginBottom: '1rem' }} />}
            {page.paragraphs.map(p => (
                <p key={p.id}>
                    {p.content}
                </p>
            ))}
        </div>
    );
};

export default StoryContentPage;