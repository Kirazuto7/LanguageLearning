import { Theme } from "../../types/types";
import SunsetBackground from "./sunset-background/SunsetBackground";
import NebulaBackground from "./nebula-background/NebulaBackground";
import CafeBackground from "./cafe-background/CafeBackground";
import FujiBackground from "./fuji/FujiBackground";
import React from "react";
import HanokVillageBackground from "./hanok/HanokVillageBackground";
import {useSelector} from "react-redux";
import {selectCurrentTheme} from "../../../features/userSettings/settingsSlice";


const backgroundMap: Partial<Record<Theme, React.ReactElement>> = {
    sunset: <SunsetBackground/>,
    nebula: <NebulaBackground/>,
    cafe: <CafeBackground/>,
    fuji: <FujiBackground/>,
    hanok: <HanokVillageBackground/>,
};

const Backgrounds: React.FC = () => {
    const theme = useSelector(selectCurrentTheme);

    const BackgroundComponent = backgroundMap[theme as Theme];
    return BackgroundComponent || null;
};

export default Backgrounds;