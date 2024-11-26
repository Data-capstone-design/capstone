import {$fetch} from "ofetch";
import type {Reactive } from "vue";

export enum NoteStatus {
    NOT_EXIST = "NOT_EXIST",
    OUTLINE_GENERATING    = "OUTLINE_GENERATING",
    COMMENTARY_EXPLANATION_GENERATING = "COMMENTARY_EXPLANATION_GENERATING",
    COMMENTARY_FEEDBACK_GENERATING = "COMMENTARY_FEEDBACK_GENERATING",
    COMMENTARY_GENERATING = "COMMENTARY_GENERATING",
    COMPLETED = "COMPLETED"
}

export interface NoteStore {
    noteCommentaryCount: Reactive<NoteCommentaryCount>;
    noteInfo: Reactive<NoteInfo>;
    fetchCreateNote: (videoId: string, userLevel: string) => Promise<any>;
    fetchNoteStatus: (videoId: string, userLevel: string) => Promise<any>;
    fetchNote: (videoId: string, userLevel: string) => Promise<any>;
    getNoteInfo: () => NoteInfo;
    resetStore: () => void;
    setNoteInfoWithoutTitle: (videoId: string, userLevel: string, status: NoteStatus) => void;
    setNoteInfo: (videoId: string, userLevel: string, title: string, status: NoteStatus) => void;
    setNoteStatus: (generateStatus: NoteStatus) => void;
    setTotalCommentaryCount: (totalCount:number) => void;
    addCurrentCommentaryCount: (count?:number) => void;
    setNoteTitle: (title: string) => void;
}

export interface NoteInfo {
    videoId: string;
    userLevel: string;
    status: NoteStatus;
    title: string;
}

export interface NoteCommentaryCount {
    total: number;
    current: number;
}

export const useNoteStore = defineStore('note', (): NoteStore => {
    const noteInfo = reactive<NoteInfo>({
        videoId: '',
        userLevel: '',
        status: NoteStatus.NOT_EXIST,
        title: ''
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

    const setNoteStatus = (status: NoteStatus) => {
        noteInfo.status = status;
    }

    const setNoteInfo = (videoId: string, userLevel: string, title: string, status: NoteStatus): void => {
        noteInfo.videoId = videoId;
        noteInfo.status = status;
        noteInfo.userLevel = userLevel;
    }

    const setNoteInfoWithoutTitle = (videoId: string, userLevel: string, status: NoteStatus): void => {
        noteInfo.videoId = videoId;
        noteInfo.status = status;
        noteInfo.userLevel = userLevel;
    }

    const setNoteTitle = (title: string) => {
        noteInfo.title = title;
    }

    const getNoteInfo = () => noteInfo;

    const setTotalCommentaryCount = (totalCount: number) => {
        noteCommentaryCount.total = totalCount;
    }

    const addCurrentCommentaryCount = (count?: number) => {
        if (count == 0) return;
        if(count) {
            noteCommentaryCount.current += count;
        } else {
            noteCommentaryCount.current += 1;
        }
    }

    const resetStore = () => {
        noteInfo.videoId = '';
        noteInfo.userLevel = '';
        noteInfo.status = NoteStatus.NOT_EXIST;
        noteCommentaryCount.total = 0;
        noteCommentaryCount.current = 0;

    }

    return {
        noteCommentaryCount,
        noteInfo,
        fetchCreateNote,
        fetchNoteStatus,
        setNoteInfo,
        setNoteInfoWithoutTitle,
        getNoteInfo,
        fetchNote,
        setNoteStatus,
        setTotalCommentaryCount,
        addCurrentCommentaryCount,
        resetStore,
        setNoteTitle
    }
});

