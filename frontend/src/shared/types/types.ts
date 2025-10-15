export type MascotName = 'jinny' | 'sakura' | 'riku' | 'yuna' | 'jinwoo';
export type MascotGender = 'female' | 'male';

export const mascotGenders: Record<MascotName, MascotGender> = {
    jinny: 'female',
    sakura: 'female',
    riku: 'male',
    yuna: 'female',
    jinwoo: 'male'
};

export type Theme = 'default' | 'light' | 'sunset' | 'nebula' | 'cafe' | 'hanok' | 'fuji' | 'school';

export enum GenerationType {
    CHAPTER = 'chapter',
    STORY = 'story'
}

export enum HttpMethod {
    GET = 'GET',
    POST = 'POST',
    PATCH = 'PATCH',
    DELETE = 'DELETE',
    PUT = 'PUT'
}

export enum BookType {
    LESSON = 'lesson',
    STORY = 'story'
}

export enum AlertLevel {
    INFO = 'info',
    WARN = 'warn',
    ERROR = 'error'
}

export enum ActionCardDesign {
    LESSON = 'lesson',
    STORY = 'story'
}