import {Theme, useTheme} from "../../contexts/ThemeContext";
import SunsetBackground from "./sunset-background/SunsetBackground";
import NebulaBackground from "./nebula-background/NebulaBackground";
import CafeBackground from "./cafe-background/CafeBackground";
import FujiBackground from "./fuji/FujiBackground";
import React from "react";
import HanokVillageBackground from "./hanok/HanokVillageBackground";


const backgroundMap: Partial<Record<Theme, React.ReactElement>> = {
    sunset: <SunsetBackground/>,
    nebula: <NebulaBackground/>,
    cafe: <CafeBackground/>,
    fuji: <FujiBackground/>,
    hanok: <HanokVillageBackground/>,
};

const Backgrounds: React.FC = () => {
    const { theme } = useTheme();

    const BackgroundComponent = backgroundMap[theme];
    return BackgroundComponent || null;
};

export default Backgrounds;