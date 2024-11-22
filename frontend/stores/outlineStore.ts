export interface NoteOutlineItem {
    startTime: number,
    title: string,
    summary: string
}

export const useOutlineStore = defineStore('index', () => {
    const noteOutline = ref<NoteOutlineItem[]>([]);

    const setNoteOutline = (indices: NoteOutlineItem[]) => {
        noteOutline.value = indices;
    }

    const getNoteOutline = () => noteOutline.value;

    const resetStore = () => {
        noteOutline.value = [];
    }

    return {
        noteOutline,
        setNoteOutline,
        getNoteOutline,
        resetStore
    };
})