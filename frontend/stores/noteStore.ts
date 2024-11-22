import {$fetch} from "ofetch";
import type {Reactive} from "vue";

export enum NoteStatus {
    COMPLETE = 'COMPLETE',
    IN_PROGRESS = 'IN_PROGRESS',
    NOT_EXIST = 'NOT_EXIST'
}

export interface NoteStore {
    noteInfo: Reactive<{ videoId: string, userLevel: string }>;
    fetchCreateNote: (videoId: string, userLevel: string) => Promise<any>;
    fetchNoteStatus: (videoId: string, userLevel: string) => Promise<any>;
    fetchNote: (videoId: string, userLevel: string) => Promise<any>;
    setNoteInfo: (videoId: string, userLevel: string, status: string) => void;
    getNoteInfo: () => NoteInfo;
    resetStore: () => void;
}

export interface NoteInfo {
    videoId: string;
    userLevel: string;
    status: string;
}

export const useNoteStore = defineStore('note', (): NoteStore => {
    const noteInfo = reactive<NoteInfo>({
        videoId: '',
        userLevel: '',
        status: ''
    });
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

    const setNoteInfo = (videoId: string, userLevel: string, status: string): void => {
        noteInfo.videoId = videoId;
        noteInfo.status = status;
        noteInfo.userLevel = userLevel;
    }

    const getNoteInfo = () => noteInfo;

    const resetStore = () => {
        noteInfo.videoId = '';
        noteInfo.userLevel = '';
        noteInfo.status = '';
    }

    return {
        noteInfo,
        fetchCreateNote,
        fetchNoteStatus,
        setNoteInfo,
        getNoteInfo,
        fetchNote,
        resetStore
    }
});

