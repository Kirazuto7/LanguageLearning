import React, { LazyExoticComponent, FC } from "react";
import {MascotName} from "../../types/types";
import Jinny from "./common/Jinny"; // Eagerly load the default mascot

// Lazy load only the non-default mascots
const Sakura = React.lazy(() => import("./common/Sakura"));
const Riku = React.lazy(() => import("./common/Riku"));
const Yuna = React.lazy(() => import("./common/Yuna"));
const Jinwoo = React.lazy(() => import("./common/Jinwoo"));


interface MascotCharacterProps {
    hop?: boolean;
    celebrate?: boolean;
}

// The map now holds only the lazy-loaded components
const lazyMascotMap: Record<Exclude<MascotName, 'jinny'>, LazyExoticComponent<FC<MascotCharacterProps>>> = {
    sakura: Sakura,
    riku: Riku,
    yuna: Yuna,
    jinwoo: Jinwoo
};

interface MascotProps extends MascotCharacterProps {
    character?: MascotName;
}

const Mascot: React.FC<MascotProps> = ({ character = 'jinny', hop, celebrate }) => {
    // Handle the default, non-lazy mascot separately for instant loading on critical pages
    if (character === 'jinny') {
        return <Jinny hop={hop} celebrate={celebrate} />;
    }

    const MascotComponent = lazyMascotMap[character];

    // Fallback to the default mascot if an invalid character name is passed
    if (!MascotComponent) {
        return <Jinny hop={hop} celebrate={celebrate} />;
    }

    return <MascotComponent hop={hop} celebrate={celebrate} />;
};

export default Mascot;