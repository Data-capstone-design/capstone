<template>
  <header class="fixed-header flex justify-between mb-2" v-if="isNoteGenerateCompleted">
    <VideoControls/>
    <NoteControls/>
  </header>
  <header class="fixed-header flex" v-else>
    <div class="text-center full-width flex  items-start justify-between q-px-xl">
      <span class="text-weight-medium" style="font-size: 1.4rem">{{noteStatusWord}}. . .</span>
      <CommentaryProgressBar/>
    </div>

  </header>
</template>

<script setup lang="ts">
import {NoteStatus} from "~/stores/noteStore";

const noteStore = useNoteStore();

const noteStatusWord = computed(() => {
  const status = noteStore.noteInfo.status;
  if(status == NoteStatus.OUTLINE_GENERATING) return "목차 생성중"
  if(status == NoteStatus.COMMENTARY_EXPLANATION_GENERATING) return "텍스트로부터 초기 해설 생성중"
  if(status == NoteStatus.COMMENTARY_FEEDBACK_GENERATING) return "피드백 생성중"
  if(status == NoteStatus.COMMENTARY_GENERATING) return "피드백을 반영해 해설 생성중"
})

const isNoteGenerateCompleted = computed(() => noteStore.noteInfo.status == NoteStatus.COMPLETED);
</script>

<style scoped>
.fixed-header {
  background-color: white;
  z-index: 10;
  padding: 1.5rem 0 1rem 0;
  border-bottom: 1px solid #dcd9d9;
}

/*  모바일 */
@media (max-width: 768px) {
  .fixed-header {
    margin: 0;
    padding: .5rem 0 .8rem 0;
  }
}
</style>