<template>
  <div class="video-page">
    <div class="video-container">
      <div id="player"></div>
    </div>
    <ExplanationSidebar />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref } from "vue";
import { useVideoStore } from "~/stores/video";

definePageMeta({
  middleware: "check-video-url",
});

const videoStore = useVideoStore();
const player = ref<any>(null);
const intervalId = ref<number | null>(null);

const loadYouTubeAPI = () => {
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

const initializePlayer = () => {
  const videoId = getVideoIdFromUrl(videoStore.getVideoURL());
  if (!videoId) return;

  player.value = new window.YT.Player("player", {
    height: "100%",
    width: "100%",
    videoId: videoId,
    events: {
      onReady: onPlayerReady,
    },
  });
};

const onPlayerReady = (event: any) => {
  event.target.playVideo();
  startLoggingCurrentTime();
};

const startLoggingCurrentTime = () => {
  intervalId.value = window.setInterval(() => {
    const currentTime = player.value.getCurrentTime();
    console.log("현재 재생 시간:", currentTime);
  }, 1000);
};

const stopLoggingCurrentTime = () => {
  if (intervalId.value !== null) {
    clearInterval(intervalId.value);
    intervalId.value = null;
  }
};

const getVideoIdFromUrl = (url: string): string | null => {
  const match = url.match(
    /^https:\/\/www\.youtube\.com\/watch\?v=([\w-]{11})$/
  );
  return match ? match[1] : null;
};

onMounted(async () => {
  await loadYouTubeAPI();
  initializePlayer();
});

onBeforeUnmount(() => {
  stopLoggingCurrentTime();
  if (player.value && player.value.destroy) {
    player.value.destroy();
  }
});
</script>

<style scoped>
.video-page {
  display: flex;
  height: 100vh;
}

.video-container {
  flex: 3;
  background-color: black;
}

#player {
  width: 100%;
  height: 100%;
}
</style>
