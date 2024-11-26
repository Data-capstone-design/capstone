package com.technote.core.domain.note.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technote.client.kafka.event.CreateNoteCommentaryEvent;
import com.technote.client.kafka.event.CreateNoteOutlineEvent;
import com.technote.core.domain.note.implement.NoteStorageHandler;
import com.technote.core.domain.note.implement.SseEventSender;
import com.technote.core.domain.note.vo.NoteVO.SegmentVO;
import com.technote.core.enums.NoteStatus;
import com.technote.core.enums.SseName;
import com.technote.core.support.error.CustomException;
import com.technote.core.support.error.ErrorType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteEventListener {
    private static final int END_FLAG = -1;
    private static final String TOTAL_END = "END";
    private static final String FEEDBACK_END = "FEEDBACK END";
    private static final String EXPLANATION_END = "EXPLANATION END";

    private final ObjectMapper objectMapper;
    private final SseEventSender sseEventSender;
    private final NoteStorageHandler noteStorageHandler;

    @EventListener
    public void handleCreateNoteCommentaryEvent(CreateNoteCommentaryEvent event) {
        try {
            log.info("Received create note commentary event: {}", event);
            String noteId = event.noteId();

            int orderIndex = event.orderIndex();
            String content = event.content();

            if(orderIndex == END_FLAG) {
                if(content.equals(EXPLANATION_END)) {
                    log.info("기본 설명문 생성 완료");
                    noteStorageHandler.setStatus(noteId, NoteStatus.COMMENTARY_FEEDBACK_GENERATING);
                    sseEventSender.broadcastEvent(noteId, SseName.EXPLANATION_END);
                }

                if(content.equals(FEEDBACK_END)) {
                    log.info("피드백 생성 완료");
                    noteStorageHandler.setStatus(noteId, NoteStatus.COMMENTARY_GENERATING);
                    sseEventSender.broadcastEvent(noteId, SseName.FEEDBACK_END);
                }

                if(content.equals(TOTAL_END)) {
                    log.info("모든 해설 생성 완료");
                    noteStorageHandler.setStatus(noteId, NoteStatus.COMPLETED);
                    sseEventSender.broadcastEvent(noteId, SseName.COMPLETE);
                }
                return;
            }
            noteStorageHandler.updateCommentaryByOrder(noteId, orderIndex, content);

            Map<String, Object> eventData = Map.of(
                    "startTime", event.startTime(),
                    "content", content
            );
            String jsonData = objectMapper.writeValueAsString(eventData);
            sseEventSender.broadcastEvent(noteId,SseName.COMMENTARY,jsonData);
        } catch (IOException e) {
            log.error("CreateNoteCommentaryEvent 직렬화 과정에서 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(
                    ErrorType.IO_ERROR,
                    event.toString()
            );
        }
    }

    @EventListener
    public void handleCreateNoteOutlineEvent(CreateNoteOutlineEvent event) {
        try {
            String noteId = event.noteId();
            log.info("Received create note outline event: {}", event);
            List<SegmentVO> segments = event.segments().stream().map(segment -> SegmentVO.builder()
                    .startTime(segment.startTime())
                    .summary(segment.summary())
                    .title(segment.title())
                    .build()
            ).toList();

            noteStorageHandler.setNoteOutline(noteId, segments);
            noteStorageHandler.setNoteCommentariesBasedOnOutline(noteId, segments);

            Map<String, Object> eventData = Map.of(
                    "segments", segments
            );
            String jsonData = objectMapper.writeValueAsString(eventData);
            sseEventSender.broadcastEvent(noteId,SseName.OUTLINE, jsonData);
        } catch (IOException e) {
            log.error("CreateNoteOutlineEvent 직렬화 과정에서 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(
                    ErrorType.IO_ERROR,
                    event.toString()
            );
        }
    }
}