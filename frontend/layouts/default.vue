<template>
  <q-layout view="hHh lpR fFf">
    <q-header
      elevated
      class="bg-white text-dark row items-center justify-center custom-header"
    >
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar>
            <img src="#" />
          </q-avatar>
          로고 넣을 것
        </q-toolbar-title>
      </q-toolbar>
    </q-header>
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts"></script>

<style scoped>
.custom-header {
  height: 70px;
}

@media (max-width: 600px) {
  .custom-header {
    height: 50px;
  }
}

@media (min-width: 601px) and (max-width: 1199px) {
  .custom-header {
    height: 60px;
  }
}

@media (min-width: 1200px) {
  .custom-header {
    height: 80px;
  }
}
</style>
