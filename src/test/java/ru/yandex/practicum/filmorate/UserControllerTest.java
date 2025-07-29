package ru.yandex.practicum.filmorate;

public class UserControllerTest {
//    private UserController controller;
//
//    @BeforeEach
//    void setUp() {
//        controller = new UserController();
//    }
//
//    @Test
//    void shouldCreateValidEntity() {
//        User validEntity = User.builder()
//                .login("Chynar")
//                .email("email@mail.ru")
//                .birthday(LocalDate.of(2003, 3, 24))
//                .build();
//        User created = controller.create(validEntity);
//        assertNotNull(created.getId());
//        assertTrue(controller.findAll().contains(created));
//    }
//
//    @Test
//    void shouldThrowWhenCreateInvalidEntity() {
//        User invalidEntity = User.builder()
//                .name("Илья")
//                .email("emailmail.ru")
//                .birthday(LocalDate.of(2026, 3, 24))
//                .build();
//        assertThrows(ValidationException.class, () -> controller.create(invalidEntity));
//    }
//
//
//    @Test
//    void updateUser_ShouldChangeNameAndKeepSameId() {
//        User validEntity = User.builder()
//                .login("Chynar")
//                .email("email@mail.ru")
//                .birthday(LocalDate.of(2003, 3, 24))
//                .build();
//        User created = controller.create(validEntity);
//        created.setName("Новое имя");
//        User updated = controller.update(created);
//
//        assertEquals(created.getId(), updated.getId());
//        assertEquals(1L, controller.findAll().size());
//        assertEquals("Новое имя", updated.getName());
//    }
//
//    @Test
//    void createUser_WhenNameNotProvided_ShouldSetLoginAsName() {
//        User validEntity = User.builder()
//                .login("Chynar")
//                .email("email@mail.ru")
//                .birthday(LocalDate.of(2003, 3, 24))
//                .build();
//        User created = controller.create(validEntity);
//        assertEquals("Chynar", created.getName());
//    }

}
