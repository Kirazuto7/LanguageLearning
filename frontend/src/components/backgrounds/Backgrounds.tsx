import {useTheme} from "../../contexts/ThemeContext";
import SunsetBackground from "./sunset-background/SunsetBackground";
import NebulaBackground from "./nebula-background/NebulaBackground";

const Backgrounds: React.FC = () => {
    const { theme } = useTheme();

    switch (theme) {
        case 'sunset':
            return <SunsetBackground/>;
        case 'nebula':
            return <NebulaBackground />;
        default:
            return null;
    }
};

export default Backgrounds;