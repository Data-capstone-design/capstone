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
import {NoteStatus, useNoteStore} from "~/stores/noteStore";
import {useEventSource} from "~/composables/useEventSource";

const videoStore = useVideoStore();
const noteStore = useNoteStore();
const {startAutoDisplayCommentary, stopAutoDisplayCommentary} = useAutoDisplayCommentary();
const { connectSse } = useEventSource();

const loading = ref<boolean>(true);
let eventSource: EventSource | null = null;

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
  const videoId = noteStore.getNoteInfo().videoId;
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

onMounted(async () => {
  await loadYouTubeAPI();
  initializePlayer();
  const { videoId, userLevel, status } = noteStore.getNoteInfo()

  if(status == NoteStatus.NOT_EXIST) {
    const response = await noteStore.fetchCreateNote(videoId,userLevel);
    const { noteId } = response.data;
    eventSource = connectSse(noteId);
  }
  else if(status == NoteStatus.IN_PROGRESS) {
    const response = await noteStore.fetchNote(videoId, userLevel);
    console.log(response);
    // eventSource = connectSse(noteId);
  }
  else if (status == NoteStatus.COMPLETE) {
    const response = await noteStore.fetchNote(videoId, userLevel);
    console.log(response);
    // 결과를 렌더링해야함
  }
});

onBeforeUnmount(() => {
  stopAutoDisplayCommentary();
  if (eventSource) {
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
