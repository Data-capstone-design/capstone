import {NoteStatus, useNoteStore} from "~/stores/noteStore";
import {useCommentaryStore} from "~/stores/commentaryStore";
import {useOutlineStore} from "~/stores/outlineStore";
import {useEventSource} from "~/composables/useEventSource";
const {connectSse } = useEventSource();



export const useInitNote = () => {
    const initNote = async () => {
        const noteStore = useNoteStore();
        const commentaryStore = useCommentaryStore();
        const outlineStore = useOutlineStore();

        const {videoId, userLevel, status} = noteStore.getNoteInfo()
        console.log("노트 생성 상태", status);

        if (status == NoteStatus.COMPLETED) {
            noteStore.setNoteStatus(NoteStatus.COMPLETED);
            const response = await noteStore.fetchNote(videoId, userLevel);
            const { outline, title, commentaries} = response.data;
            noteStore.setNoteTitle(title);
            outlineStore.setNoteOutline(outline);
            for (const commentary of commentaries) {
                if (commentary.content) {
                    await commentaryStore.appendCommentary(commentary.startTime, commentary.content);
                }
            }
            return;
        }

        if (status == NoteStatus.NOT_EXIST) {
            noteStore.setNoteStatus(NoteStatus.OUTLINE_GENERATING);
            await noteStore.fetchCreateNote(videoId, userLevel);
        }

        const response = await noteStore.fetchNote(videoId, userLevel);
        const {noteId, title, outline, commentaries} = response.data;
        noteStore.setNoteTitle(title);

        if (status == NoteStatus.OUTLINE_GENERATING) {
            noteStore.setNoteStatus(NoteStatus.OUTLINE_GENERATING);
        }

        if (status == NoteStatus.COMMENTARY_EXPLANATION_GENERATING) {
            noteStore.setNoteStatus(NoteStatus.COMMENTARY_EXPLANATION_GENERATING)
        }

        if (status == NoteStatus.COMMENTARY_FEEDBACK_GENERATING) {
            noteStore.setNoteStatus(NoteStatus.COMMENTARY_FEEDBACK_GENERATING)
        }

        if (status == NoteStatus.COMMENTARY_GENERATING) {
            noteStore.setNoteStatus(NoteStatus.COMMENTARY_GENERATING);

            let currentCommentaryCount = 0;

            noteStore.setNoteStatus(NoteStatus.COMMENTARY_GENERATING);
            outlineStore.setNoteOutline(outline);

            for (const commentary of commentaries) {
                if (commentary.content) {
                    currentCommentaryCount += 1;
                    await commentaryStore.appendCommentary(commentary.startTime, commentary.htmlContent);
                }
            }

            const totalCommentaryCount = outline.length;
            noteStore.setTotalCommentaryCount(totalCommentaryCount);
            noteStore.addCurrentCommentaryCount(currentCommentaryCount);
        }
        connectSse(noteId);
    }
    return {
        initNote
    }
}