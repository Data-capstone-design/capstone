<template>
  <section class="column items-center full-width">
    <div class="q-gutter-md text-center q-pr-none full-width" style="max-width: 900px;">
      <q-input
          filled
          append
          v-model.trim="youtubeUrl"
          label="YouTube 영상 링크를 입력하세요"
          class="q-mt-md q-pr-none"
          color="black"
          :rules="[validateYoutubeUrl]"
          :error-message="'영상 링크가 올바르지 않아요'"
          no-error-icon
          bg-color="white"
      >
        <template v-slot:append>
          <q-btn class="bg-black text-white full-height " flat icon="arrow_forward" @click="handleSubmit"/>
        </template>
      </q-input>
    </div>
    <div class="q-mt-md text-center q-mt-md">
      <div class="q-gutter-sm flex justify-center">
        <q-checkbox
            v-for="level in knowledgeLevels"
            :key="level.value"
            v-model="checkboxStates[level.value]"
            :label="level.label"
            :class="`checkbox-with-box ${level.value}`"
            class="q-mx-md"
            @update:model-value="selectCheckbox(level.value)"
            color="black"
        />
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import {useVideoStore} from "~/stores/videoStore";
import {NoteStatus, useNoteStore} from "~/stores/noteStore";
import {KnowledgeLevel} from "~/types/commentary";
import {navigateTo} from "#app";

const youtubeUrl = ref("");
const noteStore = useNoteStore();

const selectedLevel = ref<KnowledgeLevel>(KnowledgeLevel.BASIC);
const checkboxStates = reactive({
  [KnowledgeLevel.BASIC]: true,
  [KnowledgeLevel.INTERMEDIATE]: false,
  [KnowledgeLevel.ADVANCED]: false
});

const knowledgeLevels = [
  {value: KnowledgeLevel.BASIC, label: "전혀 몰라요"},
  {value: KnowledgeLevel.INTERMEDIATE, label: "어느정도 지식이 있어요"},
  {value: KnowledgeLevel.ADVANCED, label: "전문가에요"}
];

const selectCheckbox = (checkedLevel: KnowledgeLevel): void => {
  selectedLevel.value = checkedLevel;
  for (let i = 0; i < knowledgeLevels.length; i++) {
    checkboxStates[knowledgeLevels[i].value] = knowledgeLevels[i].value == checkedLevel;
  }
};

const validateYoutubeUrl = (val: string): boolean => {
  const pattern = /^https:\/\/www\.youtube\.com\/watch\?v=/;
  return pattern.test(val);
};

const extractVideoId = (url: string): string | null => {
  const urlParams = new URLSearchParams(url.split('?')[1]);
  return urlParams.get('v');
}

const handleSubmit = async (): Promise<void> => {
  if (selectedLevel.value && validateYoutubeUrl(youtubeUrl.value)) {
    const trimmedUrl = youtubeUrl.value.split("&")[0];
    const videoId = extractVideoId(trimmedUrl) as string;
    const userLevel = selectedLevel.value;
    const response = await noteStore.fetchNoteStatus(videoId,userLevel);
    const { noteStatus } = response.data;
    noteStore.setNoteInfo(videoId, userLevel, noteStatus);
    navigateTo("/note");
  }
};
</script>

<style scoped>
.checkbox-with-box {
  position: relative;
  display: flex;
  align-items: center;

}


.checkbox-with-box::after {
  position:absolute;
  right: -1.1rem;
  bottom: .6rem;
  content: "";
  display: inline-block;
  width: 12px;
  height: 10px;
}

.checkbox-with-box.BASIC::after {
  background-color: yellow;
}

/* INTERMEDIATE -> 파란색 박스 */
.checkbox-with-box.INTERMEDIATE::after {
  background-color: blue;
}

/* ADVANCED -> 빨간색 박스 */
.checkbox-with-box.ADVANCED::after {
  background-color: red;
}


</style>