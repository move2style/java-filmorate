package ru.yandex.practicum.filmorate;

public class FilmControllerTest {
//    private FilmController controller;
//
//    @BeforeEach
//    void setUp() {
//        controller = new FilmController();
//    }
//
//    @Test
//    void shouldCreateValidEntity() {
//        Film validEntity = Film.builder()
//                .name("Крутой фильм")
//                .description("С крутым описанием")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(120L)
//                .build();
//        Film created = controller.create(validEntity);
//        assertNotNull(created.getId());
//        assertTrue(controller.findAll().contains(created));
//    }
//
//    @Test
//    void shouldThrowWhenCreateInvalidEntity() {
//        Film invalidEntity = Film.builder()
//                .name(" ")
//                .description("А".repeat(201))
//                .releaseDate(LocalDate.of(1800, 1, 1))
//                .duration(-10L)
//                .build();
//        assertThrows(ValidationException.class, () -> controller.create(invalidEntity));
//    }
//
//    @Test
//    void shouldNotUpdateWithInvalidDuration() {
//        Film validEntity = Film.builder()
//                .name("Крутой фильм")
//                .description("С крутым описанием")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(120L)
//                .build();
//        Film film = controller.create(validEntity);
//        film.setDuration(-100L);
//
//        assertThrows(ValidationException.class, () -> controller.update(film));
//    }
//
//    @Test
//    void shouldUpdateEntity() {
//        Film validEntity = Film.builder()
//                .name("Крутой фильм")
//                .description("С крутым описанием")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(120L)
//                .build();
//        Film created = controller.create(validEntity);
//        created.setName("Новое имя");
//        Film updated = controller.update(created);
//
//        assertEquals(created.getId(), updated.getId());
//        assertEquals(1L, controller.findAll().size());
//        assertEquals("Новое имя", updated.getName());
//    }
}
