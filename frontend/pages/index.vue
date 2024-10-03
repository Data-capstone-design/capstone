<template>
  <q-page padding row>
    <section class="flex flex-center row">
      <q-card class="q-pa-lg" style="max-width: 500px; width: 100%">
        <q-form @submit.prevent="handleSubmit">
          <div class="q-mb-md">
            <q-input
              v-model="youtubeUrl"
              label="YouTube 영상 링크를 입력하세요"
              filled
              :rules="[validateYoutubeUrl]"
            />
          </div>
          <q-btn
            type="submit"
            label="완료"
            color="primary"
            class="full-width"
          />
        </q-form>
      </q-card>
    </section>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useVideoStore } from "~/stores/video";

const youtubeUrl = ref("");
const router = useRouter();
const videoStore = useVideoStore();

const validateYoutubeUrl = (val: string) => {
  const pattern = /^https:\/\/www\.youtube\.com\/watch\?v=([\w-]{11})$/;
  return pattern.test(val);
};

const handleSubmit = () => {
  if (validateYoutubeUrl(youtubeUrl.value) === true) {
    videoStore.setVideoURL(youtubeUrl.value);
    router.push("/video");
  }
};
</script>

<style scoped></style>
