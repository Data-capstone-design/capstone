import {useCreateHtml} from '~/composables/useCreateHtml'
import type {Ref} from "vue";
import type {HtmlCommentary} from "~/types/commentary";
import {useVideoStore} from "~/stores/videoStore";

interface CommentaryStore {
    isGenerateCommentariesCompleted: Ref<boolean>;
    isCommentaryFollowingVideo: Ref<boolean>;
    commentaries: Ref<HtmlCommentary[]>;
    totalCommentaries: Ref<HtmlCommentary[]>;
    getIsGenerateCommentariesCompleted: () => boolean;
    getIsCommentaryFollowingVideo: () => boolean;
    getCommentaries: () => HtmlCommentary[];
    updateCommentaries: (currentTime: number) => void;
    setIsCommentaryFollowingVideo: (followingVideo: boolean) => void;
    setScrollableCommentaries: () => void;
    getCurrentCommentaryTime: () => number;
    appendCommentary: (startTime: number, content: string) => Promise<void>;
    addCommentary: (startTime: number, content: string) => Promise<void>;
    resetStore: () => void;
}

export const useCommentaryStore = defineStore('commentary', (): CommentaryStore => {
    const AUTO_DISPLAY_COMMENTARY_SIZE = 1;

    const {createHtmlFromCommentary} = useCreateHtml();
    const isGenerateCommentariesCompleted: Ref<boolean> = ref<boolean>(false);
    const isCommentaryFollowingVideo: Ref<boolean> = ref<boolean>(false);
    const commentaries: Ref<HtmlCommentary[]> = ref<HtmlCommentary[]>([]);
    const totalCommentaries: Ref<HtmlCommentary[]> = ref<HtmlCommentary[]>([]);
    /*

     */
    const appendCommentary = async (startTime: number, content: string):Promise<void> => {
        const htmlContent = await createHtmlFromCommentary(content);
        const commentary = createCommentary(startTime, htmlContent);

        totalCommentaries.value.push(commentary);
        commentaries.value.push(commentary);
    }

    const addCommentary = async (startTime: number, content: string):Promise<void> => {
        const htmlContent = await createHtmlFromCommentary(content);
        const commentary = createCommentary(startTime, htmlContent);

        const comparator = (a: HtmlCommentary, b: HtmlCommentary) => a.startTime - b.startTime

        totalCommentaries.value = [...totalCommentaries.value, commentary].sort(comparator);
        commentaries.value = [...totalCommentaries.value];
    }

    const createCommentary = (startTime:number, htmlContent: string) : HtmlCommentary => {
        return {
            startTime,
            htmlContent
        }
    }

    const setScrollableCommentaries = () => {
        if(commentaries.value.length == AUTO_DISPLAY_COMMENTARY_SIZE) {
            commentaries.value = totalCommentaries.value;
        }
    }

    const updateCommentaries = (currentTime: number): void => {
        console.log(`해설 업데이트: currentTime ${currentTime}`)
        const startIndex = getTargetCommentaryIndex(currentTime);
        const commentary: HtmlCommentary = totalCommentaries.value[startIndex];
        commentaries.value = [commentary]
    }

    const getTargetCommentaryIndex = (currentTime: number): number => {
        let start: number = 0;
        let end: number = totalCommentaries.value.length - 1;
        let index: number = -1;
        while (start <= end) {
            const mid = (start + end) >> 1;
            if (totalCommentaries.value[mid].startTime <= currentTime) {
                index = mid;
                start = mid + 1
            } else {
                end = mid - 1;
            }
        }
        return index;
    }

    const setIsCommentaryFollowingVideo = (isFollowing: boolean) => {
        isCommentaryFollowingVideo.value = isFollowing;
    }

    const getIsCommentaryFollowingVideo = () => isCommentaryFollowingVideo.value;

    const getIsGenerateCommentariesCompleted = () => isGenerateCommentariesCompleted.value;

    const getCommentaries = () => commentaries.value;

    const getCurrentCommentaryTime = () : number=> {
        const videoStore = useVideoStore();
        const currentTime = videoStore.getCurrentVideoTime();
        const targetCommentaryIndex = getTargetCommentaryIndex(currentTime);
        return totalCommentaries.value[targetCommentaryIndex].startTime
    }

    const resetStore = (): void => {
        isGenerateCommentariesCompleted.value = false;
        isCommentaryFollowingVideo.value = false;
        commentaries.value = [];
        totalCommentaries.value = [];
    }

    return {
        isCommentaryFollowingVideo,
        isGenerateCommentariesCompleted,
        commentaries,
        totalCommentaries,
        addCommentary,
        getIsCommentaryFollowingVideo,
        getIsGenerateCommentariesCompleted,
        getCommentaries,
        updateCommentaries,
        setIsCommentaryFollowingVideo,
        setScrollableCommentaries,
        getCurrentCommentaryTime,
        appendCommentary,
        resetStore
    };
})