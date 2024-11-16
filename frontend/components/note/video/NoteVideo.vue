<template>
  <section class="video-container">
    <div v-if="loading" class="v-Loading q-pa-md">
      <q-card flat class="column full-width full-height" >
        <q-skeleton square style="flex:1"/>

        <q-card-section>
          <q-skeleton type="text" height="35px"  class="text-subtitle1" />
          <q-skeleton type="text" height="35px"  width="80%" class="text-subtitle1" />
          <q-skeleton type="text" height="35px" class="text-caption" />
        </q-card-section>
      </q-card>
    </div>
    <div id="player" class="v-Player"></div>
  </section>

</template>

<script setup lang="ts">
import {onBeforeUnmount, onMounted} from "vue";
import {useVideoStore} from "~/stores/videoStore";
import {useCommentaryStore} from "~/stores/commentaryStore";
import {useIndexStore} from "~/stores/indexStore";

import { v4 as uuidv4 } from "uuid";

definePageMeta({
  middleware: "check-video-url",
});

const videoStore = useVideoStore();
const commentaryStore = useCommentaryStore();
const indexStore = useIndexStore();

const loading = ref<boolean>(true);
const {startAutoDisplayCommentary, stopAutoDisplayCommentary} = useAutoDisplayCommentary();

const loadYouTubeAPI = (): Promise<void> => {
  return new Promise<void>((resolve) => {
    if (window.YT && window.YT.Player) {
      resolve();
    } else {
      const tag = document.createElement("script");
      tag.src = "https://www.youtube.com/iframe_api";
      const firstScriptTag = document.getElementsByTagName("script")[0];
      firstScriptTag.parentNode?.insertBefore(tag, firstScriptTag);

      window.onYouTubeIframeAPIReady = () => {
        resolve();
      };
    }
  });
};

const initializePlayer = (): void => {
  const videoId = videoStore.getVideoId();
  const videoPlayer = new window.YT.Player("player", {
    videoId,
    events: {
      onReady: onPlayerReady,
    },
  });

  videoStore.setPlayer(videoPlayer);
  videoStore.setPlayerSize(window.innerWidth);
};

const onPlayerReady = async (event: any) => {
  event.target.playVideo();
  loading.value = false;
};

let eventSource: EventSource;


onMounted(async () => {
  await loadYouTubeAPI();
  initializePlayer();

  const videoId = videoStore.getVideoId();
  const clientId = uuidv4();
  const url = '/api/sse'
  // `http://localhost:8080/sse/connect/${videoId}?clientId=${clientId}`
  eventSource = new EventSource(url);
  eventSource.addEventListener("connect", () => {
    console.log("서버와 연결")
  })

  eventSource.addEventListener("commentary", async (e: any) => {
    const data = JSON.parse(e.data);
    const { startTime, content } = data;
    await commentaryStore.addCommentary(startTime, content);
  });

  eventSource.addEventListener("index", (e: any) => {
    const data = JSON.parse(e.data);
    const { noteIndex } = data;
    indexStore.setNoteIndices(noteIndex);
  });

  eventSource.onerror = (error) => {
    console.error("Error receiving SSE:", error);
  };

  eventSource.addEventListener("close", ()=> {
    eventSource.close();
  })
});

onBeforeUnmount(() => {
  stopAutoDisplayCommentary();
  if (eventSource) {
    console.log("eventSource close");
    eventSource.close()
  }
});

window.addEventListener('resize', () => videoStore.setPlayerSize(window.innerWidth));

</script>

<style scoped>
.video-container {
  position: relative;
  aspect-ratio: 16 / 9;
}
.v-Loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: white;
  z-index: 1001;
}


@media (max-width: 768px) {
  .video-container {
    display: flex;
    position: relative;
  }

  #player {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
  }
}
</style>
