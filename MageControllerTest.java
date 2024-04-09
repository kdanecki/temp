package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MageControllerTest {
    @Mock
    private MageRepository repository;
    private MageController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new MageController(repository);
    }

    @Test
    void find_MageFound_ReturnsStringRepresentation() {
        Mage mage = new Mage("Merlin", 10);
        when(repository.find("Merlin")).thenReturn(Optional.of(mage));
        assertEquals("Merlin 10", controller.find("Merlin"));
    }

    @Test
    void find_MageNotFound_ReturnsNotFound() {
        when(repository.find("Gandalf")).thenReturn(Optional.empty());
        assertEquals("not found", controller.find("Gandalf"));
    }

    @Test
    void delete_MageFound_DeletesAndReturnsDone() {
        assertEquals("done", controller.delete("Gandalf"));
        verify(repository).delete("Gandalf");
    }

    @Test
    void delete_MageNotFound_ReturnsNotFound() {
        doThrow(new IllegalArgumentException()).when(repository).delete("Gandalf");
        assertEquals("not found", controller.delete("Gandalf"));
    }

    @Test
    void save_ValidMage_SavesAndReturnsDone() {
        Mage gandalf = new Mage("Gandalf", 20);
        assertEquals("done", controller.save("Gandalf", 20));
        verify(repository).save(eq(gandalf));
    }

    @Test
    void save_DuplicateMage_ReturnsBadRequest() {

        doThrow(new IllegalArgumentException()).when(repository).save(new Mage("Gandalf", 20));
        assertEquals("bad request", controller.save("Gandalf", 20));
    }
}
