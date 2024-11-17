package com.technote.core.web.controller;

import com.technote.core.domain.note.business.NoteService;
import com.technote.core.domain.note.business.NoteSseService;
import com.technote.core.web.dto.CreateNoteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<Void> start(@RequestBody CreateNoteRequest request) {
        log.info("Received request to start note with video id {}", request.videoId());
        noteService.createNote(request.videoId(), request.userLevel());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/sse/{connectId}")
    public SseEmitter connect(@PathVariable("connectId") String connectId){
        log.info("Connect to sse: {}", connectId);
        return noteSseService.connect(connectId);
    }
}
