import {useTheme} from "../../contexts/ThemeContext";
import StarryBackground from "./starry-background/StarryBackground";
import SunsetBackground from "./sunset-background/SunsetBackground";

const Backgrounds: React.FC = () => {
    const { theme } = useTheme();

    switch (theme) {
        case 'starry':
            return <StarryBackground/>;
        case 'sunset':
            return <SunsetBackground/>;
        default:
            return null;
    }
};

export default Backgrounds;