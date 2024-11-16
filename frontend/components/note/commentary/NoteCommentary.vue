<template>
  <section class="commentary-container bl">
    <q-card class="no-shadow">
      <CommentaryToolbar/>
      <CommentaryContent/>
    </q-card>
  </section>
</template>

<script setup lang="ts">
</script>

<style lang="scss" scoped>
.commentary-container {
  background-color: white;
  border-left: 1px solid #dddddd;
}

/*  모바일 */
@media (max-width: 768px) {
  .commentary-container {
    position: inherit;
    max-width: 100vw;
    border-left: none;
  }
}
</style>
