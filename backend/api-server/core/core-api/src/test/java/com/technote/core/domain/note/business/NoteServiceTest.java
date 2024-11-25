package com.technote.core.domain.note.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.technote.core.domain.note.dto.PagedNotePreviewsDto;
import com.technote.core.domain.note.implement.NoteEventPublisher;
import com.technote.core.domain.note.implement.NoteStorageHandler;
import com.technote.core.domain.note.vo.NotePreviewVO;
import com.technote.core.domain.note.vo.NoteVO;
import com.technote.core.enums.NoteStatus;
import com.technote.core.enums.UserLevel;
import java.util.stream.IntStream;
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

    @Nested
    class createNote_메서드는 {

        @Nested
        class videoId와_userLevel에_해당하는_노트가_존재하지_않을_경우 {

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
        class videoId와_userLevel에_해당하는_노트가_존재하는_경우 {

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
        class videoId와_userLevel에_해당하는_노트가_존재하는_경우 {

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

    @Nested
    class getMainPageNotes_메서드는 {

        @Nested
        class 다음_페이지가_있는_경우 {

            final String givenLastId = "note3";
            final int givenPageSize = 4;
            final String expectedNextCursor = "note7";

            @BeforeEach
            void setUpContext() {
                // 마지막 커서가 3 이므로,  4,5,6,7 반환
                var notePreviews = IntStream.range(4, 4 + givenPageSize)
                        .mapToObj(i -> NotePreviewVO.builder()
                                .id("note" + i)
                                .videoId("video101" + i)
                                .userLevel(UserLevel.BASIC)
                                .title("Spring Boot Basics")
                                .build())
                        .toList();
                when(noteStorageHandler.getPagedNotes(givenLastId, givenPageSize)).thenReturn(notePreviews);
                when(noteStorageHandler.hasMoreNotes("note7")).thenReturn(true);
            }

            @Test
            void 주어진_pageSize_만큼의_데이터와_다음_페이지를_요청하기_위해_필요한_정보를_반환한다() {
                PagedNotePreviewsDto result = noteService.getMainPageNotes(givenLastId, givenPageSize);
                String nextCursor = result.nextCursor();

                assertThat(result).isNotNull();
                assertThat(result.notePreviews()
                        .size()).isEqualTo(givenPageSize);
                assertThat(nextCursor).isEqualTo(expectedNextCursor);
                assertThat(result.hasMore()).isTrue();

                verify(noteStorageHandler).getPagedNotes(givenLastId, givenPageSize);
                verify(noteStorageHandler).hasMoreNotes(nextCursor);
            }
        }

        @Nested
        class 다음_페이지가_없는_경우 {

            final String givenLastId = "note20";
            final int givenCurrentPageNotePreviewsSize = 2;
            final int givenPageSize = 3;
            final String expectedNextCursor = "note22";

            @BeforeEach
            void setUpContext() {
                var notePreviews = IntStream.rangeClosed(21, 22)
                        .mapToObj(i -> NotePreviewVO.builder()
                                .id("note" + i)
                                .videoId("video101" + i)
                                .userLevel(UserLevel.BASIC)
                                .title("Spring Boot Basics")
                                .build())
                        .toList();

                when(noteStorageHandler.getPagedNotes(givenLastId, givenPageSize)).thenReturn(notePreviews);
                when(noteStorageHandler.hasMoreNotes("note22")).thenReturn(false);
            }

            @Test
            void 현재_페이지의_데이터를_반환하고_hasMore은_false_여야_한다() {
                PagedNotePreviewsDto result = noteService.getMainPageNotes(givenLastId, givenPageSize);
                String nextCursor = result.nextCursor();

                assertThat(result).isNotNull();
                assertThat(result.notePreviews()
                        .size()).isEqualTo(givenCurrentPageNotePreviewsSize);
                assertThat(nextCursor).isEqualTo(expectedNextCursor);
                assertThat(result.hasMore()).isFalse();

                verify(noteStorageHandler).getPagedNotes(givenLastId, givenPageSize);
                verify(noteStorageHandler).hasMoreNotes(nextCursor);
            }
        }
    }


}
