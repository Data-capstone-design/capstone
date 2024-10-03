

export const useVideoStore = defineStore('auth', () => {
    const videoURL = ref<String>();

    const setVideoURL = (url: string) => {
        videoURL.value = url;
    }

    const getVideoURL = () => videoURL.value

    return {
        setVideoURL,
        getVideoURL
    }
})