<template>
  <q-header
      bordered
      class="bg-white text-dark row items-center flex justify-start items-center shadow-none q-border"
      :style="{ height: 'var(--q-header-height)' }"
  >
    <div class="cursor-pointer q-pa-md flex full-height"  @click="toHome">
      <q-img
          class="logo"
          src="/logo.png"
          fit="contain"
          alt="Logo"
      />
    </div>

  </q-header>
</template>

<script setup lang="ts">
import {navigateTo} from "#app";

const toHome = () => {
  navigateTo("/")
}
</script>
<style scoped>
.logo {
  width:230px;
}

@media (max-width: 768px) {
  .logo {
    width: 100px;
  }
}

</style>