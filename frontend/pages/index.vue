<template>
  <div class="q-pa-md column justify-center items-center" style="margin-top: 20vh">
    <MainTitle/>
    <MainInput/>
  </div>
</template>

<script setup lang="ts">
import { useResetStore } from '~/composables/useResetStore';

const { resetAllStore } = useResetStore();

onMounted(() => {
  resetAllStore();
})
</script>

<style>

</style>
