<template>
  <header class="fixed-header flex justify-between mb-2" v-if="isNoteGenerateCompleted">
    <VideoControls/>
    <NoteControls/>
  </header>
  <header class="fixed-header flex" v-else>
    <div class="text-center full-width flex  items-center justify-center">
      <span class="text-weight-medium" style="font-size: 1.4rem">{{noteGenerateStatus}}. . .</span>
      <CommentaryProgressBar v-if="isCommentaryGenerating"/>
    </div>

  </header>
</template>

<script setup lang="ts">
import { NoteGenerateStatus, useNoteStore} from "~/stores/noteStore";

const noteStore = useNoteStore();

const noteGenerateStatus = computed(()=> noteStore.noteGenerateStatus);
const isNoteGenerateCompleted = computed(()=> noteStore.noteGenerateStatus == NoteGenerateStatus.COMPLETE_GENERATED);
const isCommentaryGenerating = computed(() => noteStore.noteGenerateStatus == NoteGenerateStatus.COMMENTARY_GENERATING);

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