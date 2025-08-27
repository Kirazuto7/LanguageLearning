import React from "react";
import Jinny from "./mascots/Jinny";
import Sakura from "./mascots/Sakura";
import Riku from "./mascots/Riku";
import Yuna from "./mascots/Yuna";
import Jinwoo from "./mascots/Jinwoo";
import {MascotName} from "../../types/types";


interface MascotCharacterProps {
    hop?: boolean;
    celebrate?: boolean;
}

const mascotMap: Record<MascotName, React.FC<MascotCharacterProps>> = {
    jinny: Jinny,
    sakura: Sakura,
    riku: Riku,
    yuna: Yuna,
    jinwoo: Jinwoo
};

interface MascotProps extends MascotCharacterProps {
    character?: MascotName;
}

const Mascot: React.FC<MascotProps> = ({ character = 'jinny', hop, celebrate }) => {
    const MascotComponent = mascotMap[character] || mascotMap.jinny;

    return <MascotComponent hop={hop} celebrate={celebrate} />;
};

export default Mascot;