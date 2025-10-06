import React from "react";
import {StoryBookLibraryItemDTO} from "../../../shared/types/dto";

interface StoryBookItemProps {
    book: StoryBookLibraryItemDTO;
}

const StoryBookItem: React.FC<StoryBookItemProps> = ({ book }) => {
    return(
        <div>
            <p>{book.title}</p>
        </div>
    )
};
export default StoryBookItem;