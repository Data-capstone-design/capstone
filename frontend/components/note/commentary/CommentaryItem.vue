<template>
  <div :id="`c-ST-${commentary.startTime}`" class="c-CT-root">

    <div class="flex justify-between q-pr-xs">
      <div class="time-Badge-root">
        <span class="time-Badge-inner">{{ formatTime(commentary.startTime) }}</span>
      </div>
      <div class="text-subtitle2">{{index+1}}.{{ noteOutline[index].title }}</div>
    </div>
    <div v-html="commentary.htmlContent"></div>
  </div>
</template>

<script setup lang="ts">
import {type HtmlCommentary} from "~/types/commentary";
import type {PropType} from "vue";
import {useFormatTime} from "~/composables/useFormatTime";
import { useOutlineStore } from "~/stores/outlineStore";

const {formatTime} = useFormatTime();

const outlineStore = useOutlineStore();

defineProps({
  commentary: {
    type: Object as PropType<HtmlCommentary>,
    required: true,
  },
  index: {
    type: Number,
    required: true
  }
});

const noteOutline = outlineStore.getNoteOutline();
</script>

<style scoped>
.c-CT-root {
  padding: 1rem;
  margin-bottom: 2rem;
  box-shadow: inset -2px -7px 2px rgb(180 180 180 / 50%), 0 4px 8px rgb(33 33 33 / 20%)
}
</style>