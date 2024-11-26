<template>
  <div class="pr-Bar q-py-sm q-px-xs">
    <q-linear-progress size="18px" style="width: 175px; color:#b1b1f6;" :value="progress">
      <div class="absolute-full flex flex-center">
        <q-badge color="transparent" text-color="black" :label="progressLabel" />
      </div>
    </q-linear-progress>
  </div>
</template>

<script setup lang="ts">
import { useNoteStore} from "~/stores/noteStore";

const noteStore = useNoteStore();
const progress = computed(() => {
  const current = noteStore.noteCommentaryCount.current;
  const total = noteStore.noteCommentaryCount.total;
  console.log(`진행상황 업데이트: total ${total}, current ${current}`)
  return total == 0  ? 0 : current / total;
});
const progressLabel = computed(() => (progress.value * 100).toFixed(2) + '%');


</script>

<style lang="scss" scoped>
</style>