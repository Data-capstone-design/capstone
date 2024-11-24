<template>
  <section class="q-pa-md q-mt-xl" style="max-width: 70%">
    <h1 class="ns-Title">준비된 노트들<q-icon name="book" class="q-mb-xs q-pl-xs" style="color:#0e0e0e" /></h1>
    <div class="row q-col-gutter-md justify-start full-width">
      <q-card
          v-for="(item, index) in youtubeItems"
          :key="index"
          class="fixed-card col-12 col-sm-6 col-md-4 column"
          style="padding: 0; margin: 10px;"
      >
        <img
            :src="item.thumbnail"
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
              @click="note"
          />
        </q-card-actions>

        <div
            :class="item.userLevel"
            class="q-ml-xs u-Level"
        ></div>
      </q-card>


    </div>
  </section>
</template>

<script setup lang="ts">
import {ref} from "vue";



const youtubeItems = ref([
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "INTERMEDIATE"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "BASIC"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "INTERMEDIATE"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "BASIC"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"
  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "INTERMEDIATE"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "BASIC"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"
  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "INTERMEDIATE"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "BASIC"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"
  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "INTERMEDIATE"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "BASIC"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"
  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "INTERMEDIATE"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "BASIC"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"
  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "INTERMEDIATE"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "BASIC"

  },
  {
    thumbnail: "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
    title: "Never Gonna Give You Up",
    userLevel: "ADVANCED"
  }


]);
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
