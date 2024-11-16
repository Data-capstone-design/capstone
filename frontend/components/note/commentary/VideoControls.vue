<template>
  <div class="q-ml-sm">
    <q-btn v-if="isCommentaryFollowingVideo"
           unelevated
           @click="debouncedStopAutoDisplayCommentary"
           label="영상 따라가기"
           icon="adjust"
           class="toggle-btn q-mr-sm q-pa-x-sm q-pa-y-xs bg-grey-3 text-grey-9 border q-border-grey-3 text-bold no-ripple icon-spacing"
           :class="{'mobile-btn': $q.screen.lt.sm }"
    />
    <q-btn v-else
           unelevated
           @click="debouncedStartAutoDisplayCommentary"
           label="자유롭게 보기"
           icon="sticky_note_2"
           class="toggle-btn q-mr-sm q-pa-x-sm q-pa-y-xs bg-grey-3 text-grey-9 border q-border-grey-3 text-bold no-ripple icon-spacing"
           :class="{'mobile-btn': $q.screen.lt.sm }"
    />
    <q-btn
        unelevated
        @click="scrollTo"
        label="재생위치로 이동"
        icon="filter_center_focus"
        class="toggle-btn q-mr-sm q-pa-x-sm q-pa-y-xs bg-grey-3 text-grey-9 border q-border-grey-3 text-bold no-ripple icon-spacing"
        :disable="isCommentaryFollowingVideo"
        :class="{'mobile-btn': $q.screen.lt.sm }"
    />
  </div>
</template>

<script setup lang="ts">
import {useCommentaryStore} from "~/stores/commentaryStore"
import {useAutoDisplayCommentary} from "~/composables/useAutoDisplayCommentary";
import {useDebounce} from "~/composables/useDebounce";


const commentaryStore = useCommentaryStore();
const {startAutoDisplayCommentary, stopAutoDisplayCommentary} = useAutoDisplayCommentary();

const {debounce} = useDebounce();
const debouncedStartAutoDisplayCommentary = debounce(startAutoDisplayCommentary, 100);
const debouncedStopAutoDisplayCommentary = debounce(stopAutoDisplayCommentary, 100);
const isCommentaryFollowingVideo = computed(() => commentaryStore.getIsCommentaryFollowingVideo());

const scrollTo = () => {
  const startTime = commentaryStore.getCurrentCommentaryTime();
  console.log(startTime);
  const elementId = `c-ST-${startTime}`;
  const element = document.getElementById(elementId);

  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
}
</script>

<style scoped>
.q-btn :deep(.q-icon) {
  margin-right: .3em;
  font-size: 1.2rem;
  margin-top: .125rem;
}

.mobile-btn {
  font-size: 12px;
  padding: 5px 10px;
}
</style>