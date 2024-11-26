<template>
  <h1 class="text-h6 text-bold">[{{ noteTitle }}]</h1>
  <h2 class="no-margin q-pb-sm q-pt-sm size text-h5">목차</h2>
</template>

<script setup lang="ts">

import {useNoteStore} from "~/stores/noteStore";

const noteStore = useNoteStore();
const noteTitle = computed(() => noteStore.noteInfo.title);

</script>

<style scoped>

</style>