import { useCommentaryStore } from '~/stores/commentaryStore';
import { useOutlineStore } from '~/stores/outlineStore';

export const useEventSource = () => {

    const connectSse = (noteId: string): EventSource => {
        const commentaryStore = useCommentaryStore();
        const indexStore = useOutlineStore();

        const url = `http://localhost:8080/notes/sse/${noteId}`;
        const eventSource = new EventSource(url);

        eventSource.addEventListener('connect', () => {
            console.log('서버와 연결');
        });

        eventSource.addEventListener('commentary', async (e: any) => {
            const data = JSON.parse(e.data);
            const { startTime, content } = data;
            await commentaryStore.addCommentary(startTime, content);
        });

        eventSource.addEventListener('outline', (e: any) => {
            const data = JSON.parse(e.data);
            const { segments } = data;
            indexStore.setNoteIndices(segments);
        });

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
