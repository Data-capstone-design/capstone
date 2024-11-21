import {type Ref, ref} from "vue";

export interface VideoStore {
    videoPlayer: Ref<any>;
    videoHeight: Ref<number>;
    videoId: Ref<string>;
    setPlayer: (player: any) => void;
    getPlayer: () => any;
    getVideoId: () => string;
    getCurrentVideoTime: () => number;
    setPlayerSize: (windowWidth: number) => void;
    setVideoId: (videoId: string) => void;
    getPlayerHeight: () => number;
    seekToTime: (seconds: number) => void;
    resetStore: () => void;
}

export const useVideoStore = defineStore('video', (): VideoStore => {
    const videoPlayer = ref<any>(null);
    const videoId = ref<string>('')
    const videoHeight = ref<number>(0)

    const setPlayer = (player: any): void => {
        videoPlayer.value = player;
    }

    const getPlayer = () => videoPlayer.value;

    const setVideoId = (vId: string): void => {
        videoId.value = vId;
    }

    const getVideoId = (): string => videoId.value;

    const getCurrentVideoTime = (): number => {
        return videoPlayer.value.getCurrentTime();
    }

    const setPlayerSize = (windowWidth: number): void => {
        const videoRatio: number = windowWidth > 768 ? 0.5 : 1;
        const width = windowWidth * videoRatio;
        const height = width * (9 / 16);
        videoHeight.value = height;
        videoPlayer.value.setSize(width, height);
    }

    const seekToTime = (seconds: number): void => {
        if (videoPlayer && typeof videoPlayer.value.seekTo === "function") {
            videoPlayer.value.seekTo(seconds, true); // 두 번째 인자는 autoplay 여부 (true: 재생)
        }
    };

    const getPlayerHeight = () => videoHeight.value;

    const resetStore = () => {
        videoPlayer.value = null;
        videoId.value = '';
        videoHeight.value = 0;
    }

    return {
        videoPlayer,
        videoId,
        videoHeight,
        getVideoId,
        setPlayer,
        setVideoId,
        getPlayer,
        getCurrentVideoTime,
        setPlayerSize,
        getPlayerHeight,
        seekToTime,
        resetStore
    }
})