package sqlite;

import org.junit.jupiter.api.*;
import utils.KeyKeeperException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLiteManagerTest {

    private SQLiteManager sqLiteManager;

    @BeforeEach
    void setUp() {
        sqLiteManager = SQLiteManager.getInstance();
    }

    @Test
    void getDbConnection() {
        assertTrue(sqLiteManager.isConnectionEstablished());
    }

    @Test
    void createAndDeleteTable() {
        String tableName = "test_user_table";
        List<String> tableFields = new ArrayList<>();

        tableFields.add("id integer PRIMARY KEY");
        tableFields.add("login text NOT NULL");

        sqLiteManager.createTable(tableName, tableFields);

        assertTrue(sqLiteManager.checkIfTableExists(tableName));

        sqLiteManager.deleteTable(tableName);

        assertFalse(sqLiteManager.checkIfTableExists(tableName));
    }

    @Test
    void createTableNoFields() {
        String tableName = "test_user_table";

        Assertions.assertThrows(KeyKeeperException.class, () -> {
            List<String> tableFields = new ArrayList<>();

            sqLiteManager.createTable(tableName, tableFields);
        });

        assertFalse(sqLiteManager.checkIfTableExists(tableName));
    }

    @Test
    void deleteTableNotExisting() {
        String tableName = "this_table_not_exists";

        Assertions.assertThrows(KeyKeeperException.class, () -> {
            sqLiteManager.deleteTable(tableName);
        });

        assertFalse(sqLiteManager.checkIfTableExists(tableName));
    }
}
