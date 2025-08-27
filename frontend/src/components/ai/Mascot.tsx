import React, { LazyExoticComponent } from "react";
import {MascotName} from "../../types/types";

const Jinny = React.lazy(() => import("./mascots/Jinny"));
const Sakura = React.lazy(() => import("./mascots/Sakura"));
const Riku = React.lazy(() => import("./mascots/Riku"));
const Yuna = React.lazy(() => import("./mascots/Yuna"));
const Jinwoo = React.lazy(() => import("./mascots/Jinwoo"));


interface MascotCharacterProps {
    hop?: boolean;
    celebrate?: boolean;
}

const mascotMap: Record<MascotName, LazyExoticComponent<React.FC<MascotCharacterProps>>> = {
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