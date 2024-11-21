<template>
  <div class="i-Scroller">
    <div v-for="(noteIndex, i) in outline" :key="i" class="q-mb-lg q-pl-xs">
      <div class="q-mb-xs text-h6" >
      </div>
      <div class="flex items-center">
        <h2 class="cursor-pointer q-mb-xs text-h6 text-weight-bold no-margin q-pr-sm"
          style="text-decoration: underline; text-underline-position:under;"
            @click="videoStore.seekToTime(parseInt(noteIndex.startTime.toString()))"
        >
          {{i+1}}.{{ noteIndex.title }}
        </h2>
        <div class="flex items-center column">
          <span class="font-medium text-grey-9" style="padding-bottom: 3px">{{ formatTime(noteIndex.startTime) }}</span>
        </div>
      </div>
      <q-card flat>
        <q-card-section class="q-pa-none">
          <p class="q-mb-none">
            {{ noteIndex.summary }}
          </p>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useOutlineStore} from "~/stores/outlineStore";
import { useFormatTime } from "~/composables/useFormatTime"
import {useVideoStore} from "~/stores/videoStore";

const videoStore = useVideoStore();
const outlineStore = useOutlineStore();
const { formatTime } = useFormatTime();

const outline = computed(() => outlineStore.getNoteOutline());
</script>

<style scoped>
.i-Scroller {
  height: 100%;
  overflow-y: auto;
}
</style>