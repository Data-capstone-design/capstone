import {useVideoStore} from "~/stores/videoStore";
import {useOutlineStore} from "~/stores/outlineStore";
import {useCommentaryStore} from "~/stores/commentaryStore";
import {useNoteStore} from "~/stores/noteStore";

export const useResetStore = () => {
    const resetAllStore = () => {
        const videoStore = useVideoStore();
        const outlineStore = useOutlineStore();
        const commentaryStore = useCommentaryStore();
        const noteStore = useNoteStore();

        videoStore.resetStore();
        commentaryStore.resetStore();
        outlineStore.resetStore();
        noteStore.resetStore();
    }

    return {
        resetAllStore
    }
}