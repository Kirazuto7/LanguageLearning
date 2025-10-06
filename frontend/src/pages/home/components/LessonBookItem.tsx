import React from "react";
import {LessonBookLibraryItemDTO} from "../../../shared/types/dto";

interface LessonBookItemProps {
    book: LessonBookLibraryItemDTO;
}

const LessonBookItem: React.FC<LessonBookItemProps> = ({ book }) => {
    return(
        <div>
            <p>{book.title}</p>
        </div>
    )
};
export default LessonBookItem;