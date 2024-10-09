export default defineNuxtConfig({
  compatibilityDate: '2024-04-03',
  devtools: { enabled: true },
  modules: [
     'nuxt-quasar-ui',
     '@pinia/nuxt'
  ],
  css: [
    '@/assets/global.css' // 전역 CSS 파일 추가
  ]
})