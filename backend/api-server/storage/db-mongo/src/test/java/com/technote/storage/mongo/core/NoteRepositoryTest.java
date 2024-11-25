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
import java.util.stream.IntStream;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
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
                                        .content("첫 번째 해설")
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
                                        .content("첫 번째 해설")
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
                                        .content("초기 해설: 도입부 설명")
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
                                    .content("첫 번째 해설")
                                    .build(),
                            Commentary.builder()
                                    .startTime(10)
                                    .content("두 번째 해설")
                                    .build(),
                            Commentary.builder()
                                    .startTime(20)
                                    .content("세 번째 해설")
                                    .build()
                    ))
                    .outline(List.of(
                            Segment.builder()
                                    .startTime(0)
                                    .title("Introduction")
                                    .summary("스프링 소개")
                                    .build(),
                            Segment.builder()
                                    .startTime(10)
                                    .title("DI & IoC")
                                    .summary("DI IoC 설명")
                                    .build(),
                            Segment.builder()
                                    .startTime(20)
                                    .title("Servlet")
                                    .summary("서블릿 설명")
                                    .build(),
                            Segment.builder()
                                    .startTime(30)
                                    .title("Spring data")
                                    .summary("Spring data 설명")
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
                String newContent = "수정된 두 번째 해설";
                noteRepository.updateCommentaryContentByOrder(givenNoteId, orderIndex, newContent);

                Optional<Note> updatedNote = noteRepository.findById(givenNoteId);
                assertThat(updatedNote).isPresent();
                Note actualNote = updatedNote.get();

                assertThat(actualNote.getCommentaries()
                        .get(orderIndex)
                        .getContent()).isEqualTo(newContent);

                assertThat(actualNote.getCommentaries()
                        .get(0)
                        .getContent()).isEqualTo("첫 번째 해설");
                assertThat(actualNote.getCommentaries()
                        .get(2)
                        .getContent()).isEqualTo("세 번째 해설");
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
                                .startTime(10)
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

    @Nested
    class GetPagedNotes_메서드는 {

        @Nested
        class 페이징할_데이터가_1페이지보다_많은_경우 {

            final int givenPageSize = 5;
            final int givenTotalNoteCount = 7;

            @BeforeEach
            void setUpContext() {

                IntStream.rangeClosed(1, givenTotalNoteCount)
                        .mapToObj(i -> Note.builder()
                                .videoId("video" + i)
                                .userLevel(UserLevel.BASIC)
                                .title("테크영상 " + i)
                                .commentaries(List.of(
                                        Commentary.builder()
                                                .startTime(i)
                                                .content("해설 " + i)
                                                .build()
                                ))
                                .outline(List.of(
                                        Segment.builder()
                                                .startTime(i)
                                                .title("제목 " + i)
                                                .summary("요약 " + i)
                                                .build()
                                ))
                                .build())
                        .forEach(noteRepository::save);
            }

            @Test
            void 첫_페이지를_요청하면_지정된_givenPageSize만큼의_모든_노트를_반환한다() {

                List<Note> result = noteRepository.getPagedNotes(null, givenPageSize);


                assertThat(result).hasSize(givenPageSize);
                assertThat(result)
                        .extracting(Note::getTitle)
                        .containsExactly("테크영상 1", "테크영상 2", "테크영상 3", "테크영상 4", "테크영상 5");
            }

            @Test
            void lastId를_기준으로_다음_페이지를_요청하면_페이지에_해당하는_모든_노트를_반환한다() {

                List<Note> firstPage = noteRepository.getPagedNotes(null, givenPageSize);
                String lastId = firstPage.get(firstPage.size() - 1).getId();

                List<Note> result = noteRepository.getPagedNotes(lastId, givenPageSize);

                assertThat(result).hasSize(givenTotalNoteCount - givenPageSize);
                assertThat(result)
                        .extracting(Note::getTitle)
                        .containsExactly("테크영상 6", "테크영상 7");
            }
        }

        @Nested
        class 페이징할_데이터가_1페이지보다_적은_경우 {

            final int givenPageSize = 5;
            final int givenTotalNoteCount = 3;

            @BeforeEach
            void setUpContext() {

                IntStream.rangeClosed(1, 3)
                        .mapToObj(i -> Note.builder()
                                .videoId("video" + i)
                                .userLevel(UserLevel.BASIC)
                                .title("테크영상 " + i)
                                .commentaries(List.of(
                                        Commentary.builder()
                                                .startTime(0)
                                                .content("해설 " + i)
                                                .build()
                                ))
                                .outline(List.of(
                                        Segment.builder()
                                                .startTime(0)
                                                .title("제목 " + i)
                                                .summary("요약 " + i)
                                                .build()
                                ))
                                .build())
                        .forEach(noteRepository::save);
            }

            @Test
            void 저장된_모든_데이터를_반환한다() {

                List<Note> result = noteRepository.getPagedNotes(null, givenPageSize);

                assertThat(result).hasSize(givenTotalNoteCount);
                assertThat(result)
                        .extracting(Note::getTitle)
                        .containsExactly("테크영상 1", "테크영상 2", "테크영상 3");
            }
        }

        @Nested
        class 저장된_데이터가_없는_경우 {

            final int givenPageSize = 5;

            @BeforeEach
            void clearContext() {
                noteRepository.deleteAll();
            }

            @Test
            void 빈_List를_반환한다() {

                List<Note> result =  noteRepository.getPagedNotes(null, givenPageSize);


                assertThat(result).isEmpty();
            }
        }
    }

    @Nested
    class hasMoreNotes_메서드는 {

        @Nested
        class lastId_이후에_데이터가_존재하는_경우 {

            final int givenTotalNoteCount = 10;
            List<String> givenNoteIds;

            @BeforeEach
            void setUpContext() {

                IntStream.rangeClosed(1, givenTotalNoteCount)
                        .mapToObj(i -> Note.builder()
                                .title("테크영상 " + i)
                                .videoId("video" + i)
                                .userLevel(UserLevel.BASIC)
                                .build())
                        .forEach(noteRepository::save);

                var allNotes = noteRepository.findAll(Sort.by(Sort.Direction.ASC, "_id"));
                givenNoteIds = allNotes.stream().map(Note::getId)
                        .toList();
            }

            @Test
            void true를_반환한다() {
                String lastId = givenNoteIds.get(4);

                boolean result = noteRepository.hasMoreNotes(lastId);

                assertThat(result).isTrue();
            }
        }

        @Nested
        class lastId_이후에_데이터가_존재하지_않는_경우 {

            final int givenTotalNoteCount = 5;
            List<String> givenNoteIds;


            @BeforeEach
            void setUpContext() {

                IntStream.rangeClosed(1, givenTotalNoteCount)
                        .mapToObj(i -> Note.builder()
                                .title("테크영상 " + i)
                                .videoId("video" + i)
                                .userLevel(UserLevel.BASIC)
                                .build())
                        .forEach(noteRepository::save);

                var allNotes = noteRepository.findAll(Sort.by(Sort.Direction.ASC, "_id"));
                givenNoteIds = allNotes.stream().map(Note::getId)
                        .toList();
            }

            @Test
            void false를_반환한다() {
                String lastId = givenNoteIds.get(givenTotalNoteCount - 1);

                boolean result = noteRepository.hasMoreNotes(lastId);

                assertThat(result).isFalse();
            }
        }
    }


}
