import {useCommentaryStore} from "~/stores/commentaryStore";
import {useVideoStore} from "~/stores/videoStore";

interface UseAutoDisplayCommentary {
    startAutoDisplayCommentary: () => void
    stopAutoDisplayCommentary: () => void
}

export const useAutoDisplayCommentary = (): UseAutoDisplayCommentary => {
    const commentaryStore = useCommentaryStore();
    const videoStore = useVideoStore();
    const INTERVAL_DURATION_MS = 1000;

    // 1초 간격으로 현재 비디오 재생시간에 맞는 해설을 렌더링
    const startAutoDisplayCommentary = (): void => {
        commentaryStore.setIsCommentaryFollowingVideo(true);
        const executeDisplay = ():void => {
            const isCommentaryFollowingVideo: boolean = commentaryStore.getIsCommentaryFollowingVideo();
            if (isCommentaryFollowingVideo) {
                const currentTime = videoStore.getPlayer().getCurrentTime();
                console.log(`영상 재생 시간: ${currentTime}`);
                commentaryStore.updateCommentaries(currentTime);
                setTimeout(executeDisplay, INTERVAL_DURATION_MS);
            }
        }
        executeDisplay();
    };

    const stopAutoDisplayCommentary = () => {
        commentaryStore.setIsCommentaryFollowingVideo(false);
        commentaryStore.setScrollableCommentaries();
    };

    return {startAutoDisplayCommentary, stopAutoDisplayCommentary};
};

