<template>
  <div class="summary-container">
    <q-card class="q-pa-md">
      <h3>영상 해설</h3>
      <div v-html="videoSummary" class="markdown-content"></div>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import hljs from "highlight.js";
import "highlight.js/styles/github-dark.css";
import { marked, Renderer } from "marked";
import DOMPurify from "dompurify";

const videoSummary = ref("");

const response = `
# 마크다운 테스트

이건 **굵은 글씨** 입니다.

## 코드 블록 예시

### JavaScript 코드 블록
\`\`\`javascript
// 간단한 JavaScript 코드 예시
const message = 'Hello, world!';
console.log(message);
\`\`\`

### Python 코드 블록
\`\`\`python
# 간단한 Python 코드 예시
def greet():
    return "Hello, Python!"

print(greet())
\`\`\`
`;

const renderer = new Renderer();
renderer.code = ({ text, lang }: { text: string; lang?: string }) => {
  const language = lang && hljs.getLanguage(lang) ? lang : "plaintext";
  const highlightedCode = hljs.highlight(text, { language }).value;
  return `<pre><code class="hljs ${language}">${highlightedCode}</code></pre>`;
};

onMounted(async () => {
  const rawHtml = await marked.parse(response, { renderer });
  videoSummary.value = DOMPurify.sanitize(rawHtml); // DOMPurify로 정화
});
</script>

<style scoped>
.summary-container {
  flex: 1;
  min-width: 400px;
  overflow-y: auto;
}

/* 기본 스타일 설정 */
.markdown-content h1 {
  font-size: 2rem;
}

.markdown-content h2 {
  font-size: 1.75rem;
}

.markdown-content h3 {
  font-size: 1.5rem;
}

.markdown-content p {
  font-size: 1rem;
}

.markdown-content code,
.markdown-content pre {
  font-size: 0.9rem;
}

/* 작은 화면 (모바일)에서의 글자 크기 조정 */
@media (max-width: 600px) {
  .markdown-content h1 {
    font-size: 1.5rem;
  }

  .markdown-content h2 {
    font-size: 1.3rem;
  }

  .markdown-content h3 {
    font-size: 1.2rem;
  }

  .markdown-content p {
    font-size: 0.9rem;
  }

  .markdown-content code,
  .markdown-content pre {
    font-size: 0.8rem;
  }
}

/* 큰 화면 (데스크탑)에서의 글자 크기 조정 */
@media (min-width: 1200px) {
  .markdown-content h1 {
    font-size: 2.5rem;
  }

  .markdown-content h2 {
    font-size: 2rem;
  }

  .markdown-content h3 {
    font-size: 1.75rem;
  }

  .markdown-content p {
    font-size: 1.1rem;
  }

  .markdown-content code,
  .markdown-content pre {
    font-size: 1rem;
  }
}
</style>
