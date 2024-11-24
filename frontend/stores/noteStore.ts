import {$fetch} from "ofetch";
import type {Reactive, Ref} from "vue";

export enum NoteStatus {
    COMPLETED = 'COMPLETED',
    IN_PROGRESS = 'IN_PROGRESS',
    NOT_EXIST = 'NOT_EXIST'
}

// IN_PROGRESS 상태일 때 목차가 생성중인지, 해설이 생성중인지 렌더링하는 함수
export enum NoteGenerateStatus {
    WAITING = '대기중 입니다',
    OUTLINE_GENERATING = '목차 생성중 입니다',
    COMMENTARY_GENERATING = '해설 생성중 입니다',
    COMPLETE_GENERATED = '생성 완료'
}

export interface NoteStore {
    noteGenerateStatus: Ref<NoteGenerateStatus>;
    noteCommentaryCount: Reactive<NoteCommentaryCount>;
    noteInfo: Reactive<NoteInfo>;
    fetchCreateNote: (videoId: string, userLevel: string) => Promise<any>;
    fetchNoteStatus: (videoId: string, userLevel: string) => Promise<any>;
    fetchNote: (videoId: string, userLevel: string) => Promise<any>;
    setNoteInfo: (videoId: string, userLevel: string, status: string) => void;
    getNoteInfo: () => NoteInfo;
    resetStore: () => void;
    setNoteGenerateStatus: (generateStatus: NoteGenerateStatus) => void;
    setTotalCommentaryCount: (totalCount:number) => void;
    addCurrentCommentaryCount: (count?:number) => void;
}

export interface NoteInfo {
    videoId: string;
    userLevel: string;
    status: string;
}

export interface NoteCommentaryCount {
    total: number;
    current: number;
}

export const useNoteStore = defineStore('note', (): NoteStore => {
    const noteGenerateStatus = ref<NoteGenerateStatus>(NoteGenerateStatus.WAITING);
    const noteInfo = reactive<NoteInfo>({
        videoId: '',
        userLevel: '',
        status: ''
    });
    const noteCommentaryCount = reactive<NoteCommentaryCount>({
        total: 0,
        current: 0
    })

    const config = useRuntimeConfig();

    const fetchCreateNote = async (videoId: string, userLevel: string): Promise<any> => {
        return $fetch<void>('/notes', {
            method: 'POST',
            baseURL: config.public.apiBaseUrl,
            body: {
                videoId,
                userLevel
            },
            onResponse: ({request, response, options}) => {
                const statusCode = response.status;
                console.log(statusCode);
            }
        })
    }

    const fetchNoteStatus = async (videoId: string, userLevel: string): Promise<any> => {
        return $fetch<any>('/notes/status', {
            method: 'GET',
            baseURL: config.public.apiBaseUrl,
            params: {
                videoId,
                userLevel
            }
        })
    }

    const fetchNote = async (videoId: string, userLevel: string): Promise<any> => {
        return $fetch<any>('/notes', {
            method: 'GET',
            baseURL: config.public.apiBaseUrl,
            params: {
                videoId,
                userLevel
            }
        });
    }

    const setNoteGenerateStatus = (generateStatus: NoteGenerateStatus) => {
        noteGenerateStatus.value = generateStatus;
    }

    const setNoteInfo = (videoId: string, userLevel: string, status: string): void => {
        noteInfo.videoId = videoId;
        noteInfo.status = status;
        noteInfo.userLevel = userLevel;
    }

    const getNoteInfo = () => noteInfo;

    const setTotalCommentaryCount = (totalCount: number) => {
        noteCommentaryCount.total = totalCount;
    }

    const addCurrentCommentaryCount = (count?: number) => {
        if(count) {
            noteCommentaryCount.current += count;
        } else {
            noteCommentaryCount.current += 1;
        }
    }

    const resetStore = () => {
        noteInfo.videoId = '';
        noteInfo.userLevel = '';
        noteInfo.status = '';
        noteCommentaryCount.total = 0;
        noteCommentaryCount.current = 0;

    }



    return {
        noteCommentaryCount,
        noteGenerateStatus,
        noteInfo,
        fetchCreateNote,
        fetchNoteStatus,
        setNoteInfo,
        getNoteInfo,
        fetchNote,
        setNoteGenerateStatus,
        setTotalCommentaryCount,
        addCurrentCommentaryCount,
        resetStore
    }
});

