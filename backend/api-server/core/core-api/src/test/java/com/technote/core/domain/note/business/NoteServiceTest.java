package com.technote.core.domain.note.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.technote.core.domain.note.implement.NoteEventPublisher;
import com.technote.core.domain.note.implement.NoteStorageHandler;
import com.technote.core.domain.note.implement.NoteVO;
import com.technote.core.enums.NoteStatus;
import com.technote.core.enums.UserLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteEventPublisher noteEventPublisher;

    @Mock
    private NoteStorageHandler noteStorageHandler;

    @InjectMocks
    private NoteService noteService;

    private NoteServiceTest() {
    }

    static NoteServiceTest createNoteServiceTest() {
        return new NoteServiceTest();
    }

    @Nested
    class createNote_메서드는 {

        @Nested
        class 새로운_노트를_생성할_때 {

            final String givenVideoId = "video123";
            final UserLevel givenUserLevel = UserLevel.BASIC;
            final String givenNoteId = "note123";

            @BeforeEach
            void setUpContext() {
                when(noteStorageHandler.saveNote(givenVideoId, givenUserLevel)).thenReturn(givenNoteId);
            }

            @Test
            void 노트를_저장하고_이벤트를_발행한다() {
                String noteId = noteService.createNote(givenVideoId, givenUserLevel);

                assertEquals(givenNoteId, noteId);
                verify(noteStorageHandler).saveNote(givenVideoId, givenUserLevel);
                verify(noteEventPublisher).publishCreateNoteEvent(givenVideoId, givenUserLevel, givenNoteId);
            }
        }
    }

    @Nested
    class getNoteStatus_메서드는 {

        @Nested
        class 특정_노트의_상태를_조회할_때 {

            final String givenVideoId = "video123";
            final UserLevel givenUserLevel = UserLevel.BASIC;
            final NoteStatus givenNoteStatus = NoteStatus.IN_PROGRESS;

            @BeforeEach
            void setUpContext() {
                when(noteStorageHandler.getNoteStatus(givenVideoId, givenUserLevel)).thenReturn(givenNoteStatus);
            }

            @Test
            void 노트의_상태를_반환한다() {
                NoteStatus noteStatus = noteService.getNoteStatus(givenVideoId, givenUserLevel);

                assertEquals(givenNoteStatus, noteStatus);
                verify(noteStorageHandler).getNoteStatus(givenVideoId, givenUserLevel);
            }
        }
    }

    @Nested
    class getNote_메서드는 {

        @Nested
        class 특정_노트를_조회할_때 {

            final String givenVideoId = "video123";
            final UserLevel givenUserLevel = UserLevel.BASIC;
            final NoteVO givenNote = NoteVO.builder()
                    .id("note123")
                    .videoId(givenVideoId)
                    .status(NoteStatus.COMPLETED)
                    .userLevel(givenUserLevel)
                    .build();

            @BeforeEach
            void setUpContext() {
                when(noteStorageHandler.getNote(givenVideoId, givenUserLevel)).thenReturn(givenNote);
            }

            @Test
            void 노트를_반환한다() {
                NoteVO note = noteService.getNote(givenVideoId, givenUserLevel);
                assertEquals(givenNote, note);
            }
        }
    }
}
