<template>
  <section class="index-container q-pl-md">
    <IndexTitle/>
    <IndexContent/>
  </section>
</template>

<script setup lang="ts">
import IndexTitle from "~/components/note/outline/OutlineTitle.vue";
import IndexContent from "~/components/note/outline/OutlineContent.vue";
</script>

<style scoped>
.index-container {
  height: inherit;
}
</style>