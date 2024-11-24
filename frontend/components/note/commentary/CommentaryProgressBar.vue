<template>
  <div class="pr-Bar q-py-sm q-px-xs">
    <q-linear-progress size="18px" style="width: 175px; color:#b1b1f6;" :value="progress">
      <div class="absolute-full flex flex-center">
        <q-badge color="transparent" text-color="black" :label="progressLabel1" />
      </div>
    </q-linear-progress>
  </div>
</template>

<script setup lang="ts">
import { useNoteStore} from "~/stores/noteStore";

const noteStore = useNoteStore();

const progress = computed(() => noteStore.noteCommentaryCount.current / noteStore.noteCommentaryCount.total);
const progressLabel1 = computed(() => (progress.value * 100).toFixed(2) + '%');
</script>

<style lang="scss" scoped>
</style>