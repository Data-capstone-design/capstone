<template>
  <div class="title">
      <img class="title__image" src="/main-title.png"/>
  </div>
</template>
<script setup lang="ts">
</script>

<style scoped>
.title {
  text-align: center; /* 가운데 정렬 */
  margin: 20px 0;
}

/* 이미지 스타일 */
.title__image {
  width: 100%;          /* 부모 요소에 맞게 너비 조정 */
  max-width: 600px;     /* 최대 너비 */
  height: auto;         /* 비율을 유지하며 높이 자동 조절 */
}

/* 모바일에서 이미지 크기 조정 */
@media (max-width: 768px) {
  .title__image {
    max-width: 80%;     /* 모바일에서는 너비를 더 작게 설정 */
  }
}
</style>