import {useCommentaryStore} from '~/stores/commentaryStore';
import {useOutlineStore} from '~/stores/outlineStore';
import {NoteGenerateStatus, useNoteStore} from "~/stores/noteStore";

export const useEventSource = () => {

    const connectSse = (noteId: string): EventSource => {
        const commentaryStore = useCommentaryStore();
        const outlineStore = useOutlineStore();
        const noteStore = useNoteStore();

        const url = `http://localhost:8080/notes/sse/${noteId}`;
        const eventSource = new EventSource(url);

        eventSource.addEventListener('connect', () => {
            console.log('서버와 연결');
            noteStore.setNoteGenerateStatus(NoteGenerateStatus.OUTLINE_GENERATING);
        });

        eventSource.addEventListener('commentary', async (e: any) => {
            const data = JSON.parse(e.data);
            const { startTime, content } = data;
            await commentaryStore.addCommentary(startTime, content);
        });

        eventSource.addEventListener('outline', (e: any) => {
            const data = JSON.parse(e.data);
            const { segments } = data;
            outlineStore.setNoteOutline(segments);
            noteStore.setNoteGenerateStatus(NoteGenerateStatus.COMMENTARY_GENERATING);
        });

        eventSource.addEventListener('complete', (e: any) => {
            noteStore.setNoteGenerateStatus(NoteGenerateStatus.COMPLETE_GENERATED);
        })

        eventSource.onerror = (error) => {
            console.error('Error receiving SSE:', error);
        };

        eventSource.addEventListener('close', () => {
            eventSource?.close();
        });

        return eventSource;
    };

    return {
        connectSse,
    };
};
