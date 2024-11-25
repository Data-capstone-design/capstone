package com.technote.core.web.controller;

import com.technote.core.domain.note.business.NoteService;
import com.technote.core.domain.note.business.NoteSseService;
import com.technote.core.enums.UserLevel;
import com.technote.core.support.response.ApiResponse;
import com.technote.core.web.dto.CreateNoteHttpRequest;
import com.technote.core.web.dto.CreateNoteHttpResponse;
import com.technote.core.web.dto.NoteStatusHttpResponse;
import com.technote.core.web.dto.PagedNotePreviewsHttpResponse;
import com.technote.core.web.mapper.NoteHttpMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    private final NoteSseService noteSseService;

    @PostMapping("")
    public ApiResponse<CreateNoteHttpResponse> createNote(@RequestBody CreateNoteHttpRequest request) {
        var result = noteService.createNote(request.videoId(), UserLevel.fromValue(request.userLevel()));
        return ApiResponse.success(NoteHttpMapper.toCreateNoteHttpResponse(result));
    }

    @GetMapping("")
    public ApiResponse<?> getNote(
            @RequestParam String videoId,
            @RequestParam String userLevel
    ) {
        log.info("videoId={} userLevel={} 노트 조회 요청 들어옴", videoId, userLevel);
        var result = noteService.getNote(videoId, UserLevel.fromValue(userLevel));
        return ApiResponse.success(NoteHttpMapper.toNoteHttpResponse(result));
    }

    @GetMapping("/status")
    public ApiResponse<NoteStatusHttpResponse> getNoteStatus(
            @RequestParam String videoId,
            @RequestParam String userLevel
    ) {
        var result = noteService.getNoteStatus(videoId, UserLevel.fromValue(userLevel));
        return ApiResponse.success(NoteHttpMapper.toGetNoteStatusHttpResponse(result));
    }

    @GetMapping("/sse/{noteId}")
    public SseEmitter connect(
            @PathVariable("noteId") String noteId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(true);
        log.info("Connect to sse: noteId={}, sessionId={}", noteId, session.getId());
        return noteSseService.connect(noteId, session.getId());
    }

    @GetMapping
    public ApiResponse<PagedNotePreviewsHttpResponse> getMainPageNotes(
            @RequestParam(required = false) String lastId,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        var result = noteService.getMainPageNotes(lastId, pageSize);
        return ApiResponse.success(NoteHttpMapper.toPagedNotePreviewsHttpResponse(result));
    }
}
