import {useCommentaryStore} from '~/stores/commentaryStore';
import {useOutlineStore} from '~/stores/outlineStore';
import {NoteStatus, useNoteStore} from "~/stores/noteStore";
import {useSseStore} from "~/stores/sseStore";

export const useEventSource = () => {

    const connectSse = (noteId: string): EventSource => {
        const commentaryStore = useCommentaryStore();
        const outlineStore = useOutlineStore();
        const noteStore = useNoteStore();

        const url = `http://localhost:8080/notes/sse/${noteId}`;
        const eventSource = new EventSource(url);

        eventSource.addEventListener('connect', () => {
            console.log('서버와 연결');
        });

        eventSource.addEventListener('outline', (e: any) => {
            noteStore.setNoteStatus(NoteStatus.COMMENTARY_EXPLANATION_GENERATING);
            const data = JSON.parse(e.data);
            const { segments } = data;
            outlineStore.setNoteOutline(segments);
            noteStore.setTotalCommentaryCount(segments.length);
        });

        eventSource.addEventListener('explanation-end', async (e: any) => {
            noteStore.setNoteStatus(NoteStatus.COMMENTARY_FEEDBACK_GENERATING);
            console.log('새로운 해설 생성');
            const data = JSON.parse(e.data);
            const { startTime, content } = data;
            await commentaryStore.addCommentary(startTime, content);
            noteStore.addCurrentCommentaryCount();
        });

        eventSource.addEventListener('feedback-end', async (e: any) => {
            noteStore.setNoteStatus(NoteStatus.COMMENTARY_GENERATING)
            console.log('해설 생성 시작');
        });

        eventSource.addEventListener('commentary', async (e: any) => {
            console.log('새로운 해설 생성');
            const data = JSON.parse(e.data);
            const { startTime, content } = data;
            await commentaryStore.addCommentary(startTime, content);
            noteStore.addCurrentCommentaryCount();
        });

        eventSource.addEventListener('complete', (e: any) => {
            noteStore.setNoteStatus(NoteStatus.COMPLETED);
            console.log("해설 생성 완료")
        })

        eventSource.onerror = (error) => {
            console.error('Error receiving SSE:', error);
        };

        eventSource.addEventListener('close', () => {
            eventSource?.close();
        });

        return eventSource;
    };

    const disconnectSse = () => {
        const sseStore = useSseStore();
        sseStore.disconnect();
    };

    return {
        connectSse,
        disconnectSse
    };
};
