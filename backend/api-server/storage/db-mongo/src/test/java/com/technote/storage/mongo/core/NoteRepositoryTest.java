package com.technote.storage.mongo.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.technote.core.enums.UserLevel;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Nested
    class findByVideoIdAndUserLevel_메서드는 {

        private final String givenVideoId = "video123";
        private final UserLevel givenUserLevel = UserLevel.BASIC;

        @Nested
        class 저장된_Note가_존재하고_videoId와_userLevel이_일치하는_경우 {

            @BeforeEach
            void setup() {
                Note note = Note.builder()
                        .videoId(givenVideoId)
                        .userLevel(givenUserLevel)
                        .title("스프링 컨퍼런스")
                        .commentaries(List.of(
                                Note.Commentary.builder().startTime(0).content("첫 번째 코멘트").build()
                        ))
                        .outline(List.of(
                                Note.Segment.builder().startTime(0).title("도입부").summary("도입부 요약").build()
                        ))
                        .build();
                noteRepository.save(note);
            }

            @Test
            void Note를_반환한다() {
                Optional<Note> result = noteRepository.findByVideoIdAndUserLevel(givenVideoId, givenUserLevel);

                assertThat(result).isPresent();
                assertThat(result.get().getVideoId()).isEqualTo(givenVideoId);
                assertThat(result.get().getUserLevel()).isEqualTo(givenUserLevel);
            }
        }

        @Nested
        class 저장된_Note가_존재하지_않는_경우 {

            @BeforeEach
            void setup() {
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

            private final UserLevel invalidUserLevel = UserLevel.ADVANCED;

            @BeforeEach
            void setup() {
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

            private final String invalidVideoId = "video999";

            @BeforeEach
            void setup() {
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

            private final String invalidVideoId = "video999";
            private final UserLevel invalidUserLevel = UserLevel.ADVANCED;

            @BeforeEach
            void setup() {
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

        private final String givenVideoId = "video123";
        private final UserLevel givenUserLevel = UserLevel.BASIC;

        @Nested
        class 저장된_Note가_존재하고_videoId와_userLevel이_일치하는_경우 {

            @BeforeEach
            void setup() {
                // Arrange: Note 저장
                Note note = Note.builder()
                        .videoId(givenVideoId)
                        .userLevel(givenUserLevel)
                        .title("스프링 컨퍼런스")
                        .commentaries(List.of(
                                Note.Commentary.builder().startTime(0).content("첫 번째 코멘트").build()
                        ))
                        .outline(List.of(
                                Note.Segment.builder().startTime(0).title("도입부").summary("도입부 요약").build()
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
            void setup() {
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

            private final UserLevel invalidUserLevel = UserLevel.ADVANCED;

            @BeforeEach
            void setup() {
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

                // Assert
                assertThat(exists).isFalse();
            }
        }

        @Nested
        class 저장된_Note가_존재하고_videoId가_다른_경우 {

            private final String invalidVideoId = "video3312213";

            @BeforeEach
            void setup() {
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
}
