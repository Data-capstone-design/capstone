<template>
  <!-- 모바일 레이아웃 -->
  <div v-if="$q.screen.lt.sm" class="note-layout">
    <div class="note-layout__video">
      <NoteVideo />
    </div>
    <div class="note-layout__index">
      <NoteIndex />
    </div>
    <div class="note-layout__commentary">
      <NoteCommentary />
    </div>
  </div>

  <div v-else class="note-layout">
    <div class="note-layout__content">
      <div class="note-layout__video">
        <NoteVideo />
      </div>
      <div class="note-layout__index"
        :style="{
          height: indexHeight + 'px'
        }"
      >
        <NoteIndex />
      </div>
    </div>
    <div class="note-layout__commentary">
      <NoteCommentary />
    </div>
  </div>
</template>

<script setup lang="ts">
import {useTabSync} from "~/composables/useTabSync";
import {useVideoStore} from "~/stores/videoStore";

const {syncTabs} = useTabSync();

definePageMeta({
  middleware: "check-video-url"
});

const videoStore = useVideoStore();
const videoHeight = computed(() => videoStore.videoHeight);

// `computed`로 `videoHeight` 가져오기
const indexHeight = ref<number>(0);

// `100vh - videoHeight - headerHeight`을 계산하여 indexHeight에 반영하는 함수
const updateIndexHeight = () => {
  console.log("호출됌")
  const viewportHeight = window.innerHeight;
  indexHeight.value = viewportHeight - videoHeight.value - 150;
};

// 비디오 높이가 변경될 때마다 indexHeight를 업데이트
watch(videoHeight, updateIndexHeight);

const handleBeforeUnload = () => {
  if (window.location.pathname.includes('/note')) {
    localStorage.setItem('videoInProgress', 'false');
    videoStore.setVideoId('');
  }
};

onMounted(() => {
  updateIndexHeight();
  localStorage.setItem('videoInProgress', 'true');
  syncTabs();
  window.addEventListener('beforeunload', handleBeforeUnload);
});

onBeforeUnmount(() => {
  localStorage.setItem('videoInProgress', 'false');
  window.removeEventListener('beforeunload', handleBeforeUnload);
});

</script>


<style scoped>
/* 최상위 레이아웃 스타일 */
.note-layout {
  display: flex;
}

/* 데스크톱에서 가로 배치 */
.note-layout__content {
  flex:1;
  display: flex;
  flex-direction: column;
}

/* 각 컴포넌트를 감싸는 Wrapper 스타일 */
.note-layout__video {
}

.note-layout__index {
  height: 100%;
}

.note-layout__commentary {
  flex: 1;
}

@media (max-width: 768px) {
  /* 모바일 스타일 */

}
</style>