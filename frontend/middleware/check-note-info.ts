import {useNoteStore} from "~/stores/noteStore";

export default defineNuxtRouteMiddleware((to, from) => {
    const noteStore = useNoteStore();
    const noteInfo = noteStore.getNoteInfo();
    if (noteInfo.videoId == '') {
        return navigateTo('/');
    }
});