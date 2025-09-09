import sakura from "../components/mascot/common/Sakura";

export type MascotName = 'jinny' | 'sakura' | 'riku' | 'yuna' | 'jinwoo';
export type MascotGender = 'female' | 'male';

export const mascotGenders: Record<MascotName, MascotGender> = {
    jinny: 'female',
    sakura: 'female',
    riku: 'male',
    yuna: 'female',
    jinwoo: 'male'
};