export const useVideoStore = defineStore('auth', () => {
    const videoURL = ref<string>();

    const setVideoURL = (url: string) => {
        videoURL.value = url;
    }

    const getVideoURL = (): string => videoURL.value as string
    return {
        setVideoURL,
        getVideoURL
    }
})