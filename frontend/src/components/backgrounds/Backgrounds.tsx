import {useTheme} from "../../contexts/ThemeContext";
import StarryBackground from "./starry-background/StarryBackground";

const Backgrounds: React.FC = () => {
    const { theme } = useTheme();

    switch (theme) {
        case 'starry':
            return <StarryBackground/>;
        default:
            return null;
    }
};

export default Backgrounds;