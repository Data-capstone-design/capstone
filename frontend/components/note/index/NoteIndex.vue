<template>
  <section class="index-container q-pl-md">
    <IndexTitle/>
    <IndexContent/>
  </section>
</template>

<script setup lang="ts">
import IndexTitle from "~/components/note/index/IndexTitle.vue";
import IndexContent from "~/components/note/index/IndexContent.vue";
</script>

<style scoped>
.index-container {
  height: inherit;
}
</style>