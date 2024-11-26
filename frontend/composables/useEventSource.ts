import {useCommentaryStore} from '~/stores/commentaryStore';
import {useOutlineStore} from '~/stores/outlineStore';
import {NoteGenerateStatus, useNoteStore} from "~/stores/noteStore";

export const useEventSource = () => {
    const sseStore = useSseStore();
    const commentaryStore = useCommentaryStore();
    const outlineStore = useOutlineStore();
    const noteStore = useNoteStore();

    const setupEventHandlers = (eventSource: EventSource) => {
        eventSource.addEventListener('connect', () => {
            console.log('서버와 연결');
            const noteGenerateStatus = noteStore.getNoteGenerateStatus();
            if (noteGenerateStatus == NoteGenerateStatus.WAITING) {
                noteStore.setNoteGenerateStatus(NoteGenerateStatus.OUTLINE_GENERATING);
            }
        });

        eventSource.addEventListener('commentary', async (e: any) => {
            noteStore.setNoteGenerateStatus(NoteGenerateStatus.COMMENTARY_GENERATING);
            const data = JSON.parse(e.data);
            const { startTime, content } = data;
            console.log(`새로운 해설 생성: startTime ${startTime}, content: ${content}`);
            await commentaryStore.addCommentary(startTime, content);
            noteStore.addCurrentCommentaryCount();
        });

        eventSource.addEventListener('outline', (e: any) => {
            const data = JSON.parse(e.data);
            const { segments } = data;
            outlineStore.setNoteOutline(segments);
            noteStore.setNoteGenerateStatus(NoteGenerateStatus.COMMENTARY_GENERATING);
            noteStore.setTotalCommentaryCount(segments.length);
        });

        eventSource.addEventListener('complete', () => {
            console.log('해설 생성 완료');
            noteStore.setNoteGenerateStatus(NoteGenerateStatus.COMPLETE_GENERATED);
            sseStore.disconnect();
        });

        eventSource.onerror = (error) => {
            console.error('EventSource 에러:', error);
            sseStore.disconnect();
        };
    };

    const connectSse = (noteId: string) => {
        console.log(`연결할 noteId: ${noteId}`)
        const url = `http://localhost:8080/notes/sse/${noteId}`;
        const eventSource = sseStore.connect(url);

        if (eventSource) {
            setupEventHandlers(eventSource);
        }
    };

    const disconnectSse = () => {
        sseStore.disconnect();
    };

    return {
        connectSse,
        disconnectSse,
    };
};
