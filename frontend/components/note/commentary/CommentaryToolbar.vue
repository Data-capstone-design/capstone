<template>
  <header class="fixed-header flex justify-between mb-2">
    <VideoControls v-if="isNoteGenerateCompleted"/>
    <NoteControls v-if="isNoteGenerateCompleted"/>
    <div class="text-center full-width" v-else>
      {{noteGenerateStatus}} . . .
    </div>
  </header>
</template>

<script setup lang="ts">
import { NoteGenerateStatus, useNoteStore} from "~/stores/noteStore";

const noteStore = useNoteStore();

const noteGenerateStatus = computed(()=> noteStore.noteGenerateStatus);
const isNoteGenerateCompleted = computed(()=> noteStore.noteGenerateStatus == NoteGenerateStatus.COMPLETE_GENERATED)
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