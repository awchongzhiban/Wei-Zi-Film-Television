export default Play;
declare class Play extends IconPlugin {
    static get defaultConfig(): {
        position: string;
        index: number;
        disable: boolean;
    };
    btnClick(e: any): boolean;
    registerIcons(): {
        play: {
            icon: any;
            class: string;
        };
        pause: {
            icon: any;
            class: string;
        };
    };
    initIcons(): void;
    animate(paused: any): void;
    render(): string;
}
import IconPlugin from "../common/iconPlugin";
