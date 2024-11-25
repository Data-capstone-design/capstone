package com.technote.core.domain.note.business;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technote.client.kafka.event.CreateNoteCommentaryEvent;
import com.technote.client.kafka.event.CreateNoteOutlineEvent;
import com.technote.client.kafka.event.CreateNoteOutlineEvent.CreateNoteOutlineEventSegment;
import com.technote.core.domain.note.implement.NoteStorageHandler;
import com.technote.core.domain.note.vo.NoteVO.SegmentVO;
import com.technote.core.domain.note.implement.SseEventSender;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteEventListenerTest {

    @Mock
    private SseEventSender sseEventSender;

    @Mock
    private NoteStorageHandler noteStorageHandler;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private NoteEventListener noteEventListener;

    @Nested
    class handleCreateNoteCommentaryEvent_메서드는 {

        @Nested
        class 새로운_해설_하나가_생성되었다는_이벤트를_받는_경우 {
            final String givenNoteId = "adfasdcwq223";
            final int givenStartTime = 34;
            final String givenContent = "레디스(Redis)는 데이터베이스의 한 종류로..";
            final int givenOrderIndex = 8;

            @BeforeEach
            void setUpContext() {
                var event = CreateNoteCommentaryEvent.builder()
                        .noteId(givenNoteId)
                        .startTime(givenStartTime)
                        .content(givenContent)
                        .orderIndex(givenOrderIndex)
                        .build();

                noteEventListener.handleCreateNoteCommentaryEvent(event);
            }

            @Test
            void 받은_해설을_저장하고_해설_생성에_대한_SSE를_발행한다() throws JsonProcessingException {
                String expectedJson = objectMapper.writeValueAsString(
                        Map.of(
                                "content", givenContent,
                                "startTime", givenStartTime
                        )
                );

                verify(noteStorageHandler, never()).setStatusToCompleted(anyString());
                verify(sseEventSender, never()).broadcastCompleteEvent(anyString());

                verify(noteStorageHandler).updateCommentaryByOrder(givenNoteId,givenOrderIndex,givenContent);
                verify(sseEventSender).broadcastCommentaryEvent(givenNoteId, expectedJson);
            }
        }

        @Nested
        class 모든_해설이_생성_완료되었다는_이벤트를_받는_경우 {
            final String givenNoteId = "adfasdcwq223";
            final int givenStartTime = -1;
            final String givenContent =  "END";

            @BeforeEach
            void setUpContext() {
                var event = CreateNoteCommentaryEvent.builder()
                        .noteId(givenNoteId)
                        .startTime(givenStartTime)
                        .content(givenContent)
                        .build();

                noteEventListener.handleCreateNoteCommentaryEvent(event);
            }

            @Test
            void 노트의_상태를_완료로_변경하고_해설_생성완료에_대한_SSE를_발행한다() {
                verify(noteStorageHandler).setStatusToCompleted(givenNoteId);
                verify(sseEventSender).broadcastCompleteEvent(givenNoteId);
            }
        }
    }

    @Nested
    class handleCreateNoteOutlineEvent_메서드는 {

        @Nested
        class 목차_생성_이벤트를_받는_경우 {
            final String givenNoteId = "note123";
            final List<CreateNoteOutlineEventSegment> givenSegments = List.of(
                    new CreateNoteOutlineEventSegment(0, "Redis 소개", "Redis는 오픈 소스 인메모리 데이터 저장소로 빠른 데이터 처리와 다양한 데이터 구조를 지원합니다."),
                    new CreateNoteOutlineEventSegment(10, "Redis 기능들", "Redis는 캐싱, Pub/Sub 메시징, 스트리밍 처리, 분산 데이터 저장 등 강력한 기능을 제공합니다.")
            );

            @BeforeEach
            void setUpContext() {
                var event = CreateNoteOutlineEvent.builder()
                        .noteId(givenNoteId)
                        .segments(givenSegments)
                        .build();

                noteEventListener.handleCreateNoteOutlineEvent(event);
            }

            @Test
            void 목차를_저장하고_SSE_이벤트를_발행한다() throws JsonProcessingException {
                var expectedSegments = givenSegments.stream()
                        .map(segment -> SegmentVO.builder()
                                .startTime(segment.startTime())
                                .summary(segment.summary())
                                .title(segment.title())
                                .build())
                        .toList();

                String expectedJson = objectMapper.writeValueAsString(
                        Map.of("segments", expectedSegments)
                );

                verify(noteStorageHandler).setNoteOutline(givenNoteId, expectedSegments);
                verify(noteStorageHandler).setNoteCommentariesBasedOnOutline(givenNoteId, expectedSegments);

                verify(sseEventSender).broadcastOutlineEvent(givenNoteId, expectedJson);
            }
        }
    }
}