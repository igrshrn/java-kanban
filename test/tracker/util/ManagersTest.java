package tracker.util;

import org.junit.jupiter.api.Test;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void inMemoryTaskManagersInitialization() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Менеджер не инициализирован.");
    }

    @Test
    void inMemoryHistoryTaskManagersInitialization() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер не инициализирован.");
    }
}