export interface NoteIndexItem {
    startTime: number,
    title: string,
    summary: string
}

export const useIndexStore = defineStore('index', () => {
    const noteIndex = ref<NoteIndexItem[]>([]);

    const setNoteIndices = (idxs: NoteIndexItem[]) => {
        noteIndex.value = idxs;
    }

    const getNoteIndex = () => noteIndex.value;

    return {
        noteIndex,
        setNoteIndices,
        getNoteIndex
    };
})