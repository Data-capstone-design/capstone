package com.technote.storage.mongo.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.technote.core.enums.NoteStatus;
import com.technote.core.enums.UserLevel;
import com.technote.storage.mongo.DbMongoTestApplication;
import com.technote.storage.mongo.core.Note.Commentary;
import com.technote.storage.mongo.core.Note.Segment;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = DbMongoTestApplication.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NoteRepositoryTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
    }

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }

    @Nested
    class findByVideoIdAndUserLevel_메서드는 {

        final String givenVideoId = "video123";
        final UserLevel givenUserLevel = UserLevel.BASIC;

        @Nested
        class 저장된_Note가_존재하고_videoId와_userLevel이_일치하는_경우 {

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId(givenVideoId)
                        .userLevel(givenUserLevel)
                        .title("스프링 컨퍼런스")
                        .commentaries(List.of(
                                Commentary.builder()
                                        .startTime(0)
                                        .content("첫 번째 코멘트")
                                        .build()
                        ))
                        .outline(List.of(
                                Segment.builder()
                                        .startTime(0)
                                        .title("도입부")
                                        .summary("도입부 요약")
                                        .build()
                        ))
                        .build();
                noteRepository.save(note);
            }

            @Test
            void Note를_포함하는_Optional을_반환한다() {
                Optional<Note> result = noteRepository.findByVideoIdAndUserLevel(givenVideoId, givenUserLevel);

                assertThat(result).isPresent();
                assertThat(result.get()
                        .getVideoId()).isEqualTo(givenVideoId);
                assertThat(result.get()
                        .getUserLevel()).isEqualTo(givenUserLevel);
            }
        }

        @Nested
        class 저장된_Note가_존재하지_않는_경우 {

            @BeforeEach
            void setUpContext() {
                noteRepository.deleteAll();
            }

            @Test
            void 빈_Optional을_반환한다() {
                Optional<Note> result = noteRepository.findByVideoIdAndUserLevel(givenVideoId, givenUserLevel);

                assertThat(result).isNotPresent();
            }
        }

        @Nested
        class 저장된_Note가_존재하고_userLevel이_다른_경우 {

            final UserLevel invalidUserLevel = UserLevel.ADVANCED;

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId(givenVideoId)
                        .userLevel(UserLevel.BASIC)
                        .title("스프링 컨퍼런스")
                        .build();
                noteRepository.save(note);
            }

            @Test
            void 빈_Optional을_반환한다() {
                Optional<Note> result = noteRepository.findByVideoIdAndUserLevel(givenVideoId, invalidUserLevel);

                assertThat(result).isNotPresent();
            }
        }

        @Nested
        class 저장된_Note가_존재하고_videoId가_다른_경우 {

            final String invalidVideoId = "video999";

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId(givenVideoId)
                        .userLevel(givenUserLevel)
                        .title("스프링 컨퍼런스")
                        .build();
                noteRepository.save(note);
            }

            @Test
            void 빈_Optional을_반환한다() {
                Optional<Note> result = noteRepository.findByVideoIdAndUserLevel(invalidVideoId, givenUserLevel);

                assertThat(result).isNotPresent();
            }
        }

        @Nested
        class 저장된_Note가_존재하고_videoId와_userLevel이_모두_다른_경우 {

            final String invalidVideoId = "video999";
            final UserLevel invalidUserLevel = UserLevel.ADVANCED;

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId(givenVideoId)
                        .userLevel(givenUserLevel)
                        .title("스프링 컨퍼런스")
                        .build();
                noteRepository.save(note);
            }

            @Test
            void 빈_Optional을_반환한다() {
                Optional<Note> result = noteRepository.findByVideoIdAndUserLevel(invalidVideoId, invalidUserLevel);

                assertThat(result).isNotPresent();
            }
        }
    }


    @Nested
    class existsByVideoIdAndUserLevel_메서드는 {

        final String givenVideoId = "video123";
        final UserLevel givenUserLevel = UserLevel.BASIC;

        @Nested
        class 저장된_Note가_존재하고_videoId와_userLevel이_일치하는_경우 {

            @BeforeEach
            void setUpContext() {

                Note note = Note.builder()
                        .videoId(givenVideoId)
                        .userLevel(givenUserLevel)
                        .title("스프링 컨퍼런스")
                        .commentaries(List.of(
                                Commentary.builder()
                                        .startTime(0)
                                        .content("첫 번째 코멘트")
                                        .build()
                        ))
                        .outline(List.of(
                                Segment.builder()
                                        .startTime(0)
                                        .title("도입부")
                                        .summary("도입부 요약")
                                        .build()
                        ))
                        .build();
                noteRepository.save(note);
            }

            @Test
            void true를_반환한다() {
                boolean exists = noteRepository.existsByVideoIdAndUserLevel(givenVideoId, givenUserLevel);

                assertThat(exists).isTrue();
            }
        }

        @Nested
        class 저장된_Note가_존재하지_않는_경우 {

            @BeforeEach
            void setUpContext() {
                noteRepository.deleteAll();
            }

            @Test
            void false를_반환한다() {
                boolean exists = noteRepository.existsByVideoIdAndUserLevel(givenVideoId, givenUserLevel);

                assertThat(exists).isFalse();
            }
        }

        @Nested
        class 저장된_Note가_존재하고_userLevel이_다른_경우 {

            final UserLevel invalidUserLevel = UserLevel.ADVANCED;

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId(givenVideoId)
                        .userLevel(UserLevel.BASIC)
                        .title("스프링 컨퍼런스")
                        .build();
                noteRepository.save(note);
            }

            @Test
            void false를_반환한다() {
                boolean exists = noteRepository.existsByVideoIdAndUserLevel(givenVideoId, invalidUserLevel);

                assertThat(exists).isFalse();
            }
        }

        @Nested
        class 저장된_Note가_존재하고_videoId가_다른_경우 {

            final String invalidVideoId = "video3312213";

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId("video123")
                        .userLevel(UserLevel.BASIC)
                        .title("스프링 컨퍼런스")
                        .build();
                noteRepository.save(note);
            }

            @Test
            void false를_반환한다() {
                boolean exists = noteRepository.existsByVideoIdAndUserLevel(invalidVideoId, givenUserLevel);

                assertThat(exists).isFalse();
            }
        }
    }

    @Nested
    class setCommentaries_메서드는 {

        @Nested
        class noteId_에_해당하는_Note가_존재하는_경우 {
            String givenNoteId;

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId("video123")
                        .userLevel(UserLevel.BASIC)
                        .title("Spring Framework Overview")
                        .commentaries(List.of(
                                Commentary.builder()
                                        .startTime(0)
                                        .content("초기 코멘트: 도입부 설명")
                                        .build()
                        ))
                        .outline(List.of(
                                Segment.builder()
                                        .startTime(0)
                                        .title("Introduction")
                                        .summary("스프링 프레임워크의 도입부를 설명")
                                        .build()
                        ))
                        .build();
                noteRepository.save(note);
                givenNoteId = note.getId();
            }

            @Test
            void Commentaries_필드를_업데이트한다() {
                List<Commentary> updatedCommentaries = List.of(
                        Commentary.builder()
                                .startTime(0)
                                .content("스프링 프레임워크는 자바 기반 경량 애플리케이션 개발 프레임워크입니다.")
                                .build(),
                        Commentary.builder()
                                .startTime(15)
                                .content("스프링은 의존성 주입(DI)과 AOP를 지원합니다.")
                                .build()
                );
                noteRepository.setCommentaries(givenNoteId, updatedCommentaries);

                Optional<Note> updatedNote = noteRepository.findById(givenNoteId);
                assertThat(updatedNote).isPresent();
                Note actualNote = updatedNote.get();
                assertThat(actualNote.getCommentaries())
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactlyElementsOf(updatedCommentaries);
            }
        }

        @Nested
        class noteId_에_해당하는_Note가_없는_경우 {

            @Test
            void 아무_변경도_발생하지_않는다() {
                List<Commentary> updatedCommentaries = List.of(
                        Commentary.builder()
                                .startTime(0)
                                .content("스프링은 의존성 주입(DI)과 AOP를 지원합니다.")
                                .build()
                );
                noteRepository.setCommentaries("nonExistentNoteId", updatedCommentaries);

                Optional<Note> result = noteRepository.findById("nonExistentNoteId");
                assertThat(result).isNotPresent();
            }
        }
    }

    @Nested
    class updateCommentaryContentByOrder_메서드는 {

        String givenNoteId;

        @BeforeEach
        void setUpContext() {
            Note note = Note.builder()
                    .videoId("video123")
                    .userLevel(UserLevel.BASIC)
                    .title("Spring Framework Overview")
                    .commentaries(List.of(
                            Commentary.builder()
                                    .startTime(0)
                                    .content("첫 번째 코멘트")
                                    .build(),
                            Commentary.builder()
                                    .startTime(10)
                                    .content("두 번째 코멘트")
                                    .build(),
                            Commentary.builder()
                                    .startTime(20)
                                    .content("세 번째 코멘트")
                                    .build()
                    ))
                    .outline(List.of(
                            Segment.builder()
                                    .startTime(0)
                                    .title("Introduction")
                                    .summary("도입부 설명")
                                    .build()
                    ))
                    .build();
            Note savedNote = noteRepository.save(note);
            givenNoteId = savedNote.getId();
        }

        @Nested
        class orderIndex_가_유효한_경우 {

            @Test
            void 특정_인덱스의_Commentary_내용을_업데이트한다() {
                int orderIndex = 1;
                String newContent = "수정된 두 번째 코멘트";
                noteRepository.updateCommentaryContentByOrder(givenNoteId, orderIndex, newContent);

                Optional<Note> updatedNote = noteRepository.findById(givenNoteId);
                assertThat(updatedNote).isPresent();
                Note actualNote = updatedNote.get();

                assertThat(actualNote.getCommentaries()
                        .get(orderIndex)
                        .getContent()).isEqualTo(newContent);

                assertThat(actualNote.getCommentaries()
                        .get(0)
                        .getContent()).isEqualTo("첫 번째 코멘트");
                assertThat(actualNote.getCommentaries()
                        .get(2)
                        .getContent()).isEqualTo("세 번째 코멘트");
            }
        }
    }

    @Nested
    class setStatusToComplete_메서드는 {
        @Nested
        class 주어진_noteId에_해당하는_노트가_존재할_경우 {

            String givenNoteId;

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId("video123")
                        .userLevel(UserLevel.BASIC)
                        .title("Spring Framework Overview")
                        .status(NoteStatus.IN_PROGRESS)
                        .build();
                Note savedNote = noteRepository.save(note);
                givenNoteId = savedNote.getId();
            }

            @Test
            void status를_완료로_변경한다() {
                noteRepository.setStatusToComplete(givenNoteId);

                Optional<Note> result = noteRepository.findById(givenNoteId);
                assertThat(result).isPresent();
                Note updatedNote = result.get();
                assertThat(updatedNote.getStatus()).isEqualTo(NoteStatus.COMPLETED);
            }
        }
    }

    @Nested
    class setOutline_메서드는 {
        @Nested
        class 주어진_noteId에_해당하는_노트가_존재할_경우 {
            String givenNoteId;
            List<Segment> givenSegments;

            @BeforeEach
            void setUpContext() {
                Note note = Note.builder()
                        .videoId("video123")
                        .userLevel(UserLevel.BASIC)
                        .title("Spring Framework Overview")
                        .status(NoteStatus.IN_PROGRESS)
                        .build();
                Note savedNote = noteRepository.save(note);
                givenNoteId = savedNote.getId();


                givenSegments = List.of(
                        Segment.builder()
                                .startTime(0)
                                .title("Introduction")
                                .summary("도입부 설명")
                                .build(),
                        Segment.builder()
                                .startTime(0)
                                .title("Content")
                                .summary("내용 설명")
                                .build()
                );

            }

            @Test
            void 노트의_목차를_segments로_설정한다() {
                noteRepository.setOutline(givenNoteId, givenSegments);

                Optional<Note> result = noteRepository.findById(givenNoteId);
                assertThat(result).isPresent();
                Note actualNote = result.get();

                assertThat(actualNote.getOutline())
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactlyElementsOf(givenSegments);
            }
        }

        @Nested
        class 주어진_noteId에_해당하는_노트가_존재하지_않을_경우 {
            final String notExistNoteId = "adsfaerwrfadfdagaqwerwer";
            final List<Segment> givenSegments = List.of();

            @Test
            void 예외를_던지지_않고_아무_동작도_하지_않는다(){
                assertThatCode(() -> {
                    noteRepository.setOutline(notExistNoteId,givenSegments);
                }).doesNotThrowAnyException();
            }
        }
    }
}
