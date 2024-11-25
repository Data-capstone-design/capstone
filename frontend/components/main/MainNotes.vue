<template>
  <section class="q-pa-md q-mt-xl" style="max-width: 70%">
    <h1 class="ns-Title">
      <q-icon name="book" class="q-mb-xs q-pl-xs" style="color:#0e0e0e" />
      <span>전체 노트</span>
      <span class="text-grey-6 q-pl-sm" style="font-size: .8rem">더 보려면 스크롤 하세요</span>
    </h1>
    <div
        class="row q-col-gutter-md justify-start full-width"
        @scroll="onScroll"
        ref="scrollContainer"
        style="overflow-y: auto; max-height: 500px;"
    >
      <q-card
          v-for="(item, index) in notePreviewItems"
          :key="index"
          class="fixed-card col-12 col-sm-6 col-md-4 column"
          style="padding: 0; margin: 10px;"
      >
        <img
            :src="`https://img.youtube.com/vi/${item.thumbnail}/default.jpg`"
            class="q-card-image"
            alt="노트 이미지"
        />

        <q-card-section class="no-padding full-width">
          <div
              class="text-h6 q-px-sm ellipsis-title"
              style="font-size: 1rem;"
          >
            {{ item.title }}
          </div>
        </q-card-section>

        <q-card-actions class="no-padding  row justify-between">
          <q-btn
              flat
              color="grey-7"
              target="_blank"
              label="노트 보기"
              icon="book"
              @click="goToNote(item.videoId,item.userLevel)"
          />
        </q-card-actions>

        <div
            :class="item.userLevel"
            class="q-ml-xs u-Level"
        ></div>
      </q-card>
    </div>
    <div v-if="loading" class="text-center q-my-md text-bold">로딩중 . . .</div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import {useNoteStore} from "~/stores/noteStore";

const noteStore = useNoteStore();

interface NotePreview {
  noteId: string;
  videoId: string;
  userLevel: string;
  title: string;
  thumbnail: string;
}

interface PagedNotePreviews {
  notePreviews: NotePreview[];
  nextCursor: string;
  hasMore: boolean;
}

const notePreviewItems = ref<NotePreview[]>([]);
const nextCursor = ref<string | null>(null);
const hasMore = ref(true);
const loading = ref(false);

const config = useRuntimeConfig();

const fetchNotes = async () => {
  if (loading.value || !hasMore.value) return;

  loading.value = true;

  try {
    let params;
    if(notePreviewItems.value.length > 0) {
      params = {
        lastId: nextCursor.value,
        pageSize: 20,
      }
    } else {
      params = {
        pageSize: 20
      }
    }

    const response = await $fetch<PagedNotePreviews>('/notes/preview', {
      method: 'GET',
      baseURL: config.public.apiBaseUrl,
      params
    });

    notePreviewItems.value.push(...response.data.notePreviews);
    nextCursor.value = response.data.nextCursor;
    hasMore.value = response.data.hasMore;
  } catch (error) {
    console.error("Failed to fetch notes:", error);
  } finally {
    loading.value = false;
  }
};

const onScroll = (event: Event) => {
  const target = event.target as HTMLElement;

  if (target.scrollHeight - target.scrollTop <= target.clientHeight + 100) {
    fetchNotes();
  }
};

const goToNote = async (videoId: string, userLevel: string): Promise<void> => {
    const response = await noteStore.fetchNoteStatus(videoId,userLevel);
    const { noteStatus } = response.data;
    noteStore.setNoteInfo(videoId, userLevel, noteStatus);
    navigateTo("/note");
};

onMounted(() => {
  fetchNotes();
});
</script>


<style scoped>

.ns-Title {
  font-size: 1.5rem;
  padding-left: .7rem;
  margin-bottom: 1.3rem;
}


.fixed-card {
  min-width: 200px;
  max-width: 200px;
  margin: auto;
}


.q-card-image {
  object-fit: cover;
  max-height: 110px;
  min-height: 110px;
}


.row {
  display: flex;
  flex-wrap: wrap;
}

.ellipsis-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.q-btn :deep(.q-icon) {
  margin-right: .1em;
  font-size: 1rem;
}

.u-Level {
  width: 20px;
  height: 20px;
  position: absolute;
  top: 5px;
  right: 5px;
}

.BASIC {
  background-color: yellow;
}

.INTERMEDIATE {
  background-color: blue;
}

.ADVANCED {
  background-color: red;
}
</style>
