<template>
  <div :id="`c-ST-${commentary.startTime}`" class="c-CT-root content-section  q-shadow-xs">

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

</style>