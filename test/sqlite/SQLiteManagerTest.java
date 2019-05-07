package sqlite;

import org.junit.jupiter.api.*;
import utils.KeyKeeperException;

import java.util.ArrayList;
import java.util.List;

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
    void createTable() {
        String tableName = "test_user_table";
        List<String> tableFields = new ArrayList<>();

        tableFields.add("id integer PRIMARY KEY");
        tableFields.add("login text NOT NULL");

        sqLiteManager.createTable(tableName, tableFields);
    }

    @Test
    void createTableNoFields() {
        Assertions.assertThrows(KeyKeeperException.class, () -> {
            String tableName = "test_user_table";
            List<String> tableFields = new ArrayList<>();

            sqLiteManager.createTable(tableName, tableFields);
        });
    }

    @Test
    void deleteTable() {
        String tableName = "test_user_table";

        sqLiteManager.deleteTable(tableName);
    }

    @Test
    void deleteTableNotExisting() {
        Assertions.assertThrows(KeyKeeperException.class, () -> {
            String tableName = "this_table_not_exists";

            sqLiteManager.deleteTable(tableName);
        });
    }

}
