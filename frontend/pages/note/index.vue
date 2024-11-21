<template>
  <!-- 모바일 레이아웃 -->
  <div v-if="$q.screen.lt.sm" class="note-layout">
    <div class="note-layout__video">
      <NoteVideo/>
    </div>
    <div class="note-layout__outline">
      <NoteOutline/>
    </div>
    <div class="note-layout__commentary">
      <NoteCommentary/>
    </div>
  </div>

  <div v-else class="note-layout">
    <div class="note-layout__content">
      <div class="note-layout__video">
        <NoteVideo/>
      </div>
      <div class="note-layout__outline"
           :style="{
          height: outlineHeight + 'px'
        }"
      >
        <NoteOutline/>
      </div>
    </div>
    <div class="note-layout__commentary">
      <NoteCommentary/>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useTabSync} from "~/composables/useTabSync";
import {useVideoStore} from "~/stores/videoStore";

const {syncTabs} = useTabSync();

definePageMeta({
  middleware: "check-note-info"
});

const videoStore = useVideoStore();
const videoHeight = computed(() => videoStore.videoHeight);

// `computed`로 `videoHeight` 가져오기
const outlineHeight = ref<number>(0);

// `100vh - videoHeight - headerHeight`을 계산하여 outlineHeight에 반영
const updateOutlineHeight = () => {
  const viewportHeight = window.innerHeight;
  outlineHeight.value = viewportHeight - videoHeight.value - 150;
};

// 비디오 높이가 변경될 때마다 outlineHeight 업데이트
watch(videoHeight, updateOutlineHeight);

const handleBeforeUnload = () => {
  if (window.location.pathname.includes('/note')) {
    localStorage.setItem('videoInProgress', 'false');
  }
};

onMounted(() => {
  updateOutlineHeight();
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
.note-layout {
  display: flex;
}

.note-layout__content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.note-layout__video {
}

.note-layout__outline {
  height: 100%;
}

.note-layout__commentary {
  flex: 1;
}

@media (max-width: 768px) {
  /* 모바일 스타일 */

}
</style>