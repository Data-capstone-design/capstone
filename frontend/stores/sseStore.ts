
export const useSseStore = defineStore('sse', () => {
    const eventSource: Ref<EventSource | null>  = ref(null);
    const isConnected: Ref<boolean> = ref(false);

    const connect = (url: string) => {
        if (eventSource.value) {
            console.warn('이미 EventSource가 연결되어 있습니다.');
            return eventSource.value;
        }

        eventSource.value = new EventSource(url);
        isConnected.value = true;

        console.log('EventSource 연결 생성:', url);
        return eventSource.value;
    };

    const disconnect = () => {
        if (eventSource.value) {
            console.log('EventSource 연결 닫기');
            eventSource.value.close();
            eventSource.value = null;
            isConnected.value = false;
        }
    };

    return {
        eventSource,
        isConnected,
        connect,
        disconnect,
    };

})