<template>
  <div class="flex q-pr-md">
    <q-btn
        unelevated
        label="basic"
        class="toggle-btn BASIC q-mr-sm q-pa-x-sm q-pa-y-xs bg-black-8 text-grey-9 border q-border-grey-3 text-bold no-ripple icon-spacing"
        :class="{'mobile-btn': $q.screen.lt.sm}"
        @click="changeUserLevel(KnowledgeLevel.BASIC)"

    />
    <q-btn
        unelevated
        label="INTERMEDIATE"
        class="toggle-btn INTERMEDIATE q-mr-sm q-pa-x-sm q-pa-y-xs bg-black-8 text-grey-9 border q-border-grey-3 text-bold no-ripple icon-spacing"
        :class="{'mobile-btn': $q.screen.lt.sm}"
        @click="changeUserLevel(KnowledgeLevel.INTERMEDIATE)"
    />
    <q-btn
        unelevated
        label="ADVANCED"
        class="toggle-btn ADVANCED q-mr-sm q-pa-x-sm q-pa-y-xs bg-black-8 text-grey-9 border q-border-grey-3 text-bold no-ripple icon-spacing"
        :class="{'mobile-btn': $q.screen.lt.sm}"
        @click="changeUserLevel(KnowledgeLevel.ADVANCED)"
    />
  </div>
</template>

<script setup lang="ts">
import { useInitNote } from '~/composables/useInitNote';
import {useCommentaryStore} from "~/stores/commentaryStore";
import { useNoteStore} from "~/stores/noteStore";
import {useOutlineStore} from "~/stores/outlineStore";
import { useEventSource } from "~/composables/useEventSource"
import {KnowledgeLevel} from "~/types/commentary";

const { initNote } = useInitNote();
const { disconnectSse } = useEventSource();
const outlineStore = useOutlineStore();
const commentaryStore = useCommentaryStore();
const noteStore = useNoteStore();


const changeUserLevel = async (level: string) => {
  disconnectSse();
  const { videoId, title, userLevel } = noteStore.getNoteInfo();
  if(level == userLevel) return;
  const response = await noteStore.fetchNoteStatus(videoId,level);
  const { noteStatus } = response.data;
  outlineStore.resetStore();
  commentaryStore.resetStore();
  noteStore.resetStore();
  noteStore.setNoteInfo(videoId, level, title, noteStatus);
  await initNote();
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

.toggle-btn {
  position: relative;
  display: flex;
  align-items: center;
}

.toggle-btn::after {
  position:absolute;
  right: .1rem;
  bottom: .6rem;
  content: "";
  display: inline-block;
  width: 12px;
  height: 10px;
}

.toggle-btn.BASIC::after {
  background-color: yellow;
}

/* INTERMEDIATE -> 파란색 박스 */
.toggle-btn.INTERMEDIATE::after {
  background-color: blue;
}

/* ADVANCED -> 빨간색 박스 */
.toggle-btn.ADVANCED::after {
  background-color: red;
}
</style>