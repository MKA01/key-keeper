package sqlite;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;
import utils.KeyKeeperException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteManagerTest {

    private SQLiteManager sqLiteManager;
    private Logger logger = Logger.getLogger(SQLiteManagerTest.class);

    @BeforeEach
    void setUp() {
        sqLiteManager = SQLiteManager.getInstance();
    }

    @AfterEach
    void tearDown() {
        try {
            sqLiteManager.dropTable("test_user_table");
        } catch (KeyKeeperException ex) {
            logger.info("Test tearDown failed because table was already deleted!");
        }
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

        sqLiteManager.dropTable(tableName);

        assertFalse(sqLiteManager.checkIfTableExists(tableName));
    }

    @Test
    void createTableWithoutFields() {
        String tableName = "test_user_table";

        Assertions.assertThrows(KeyKeeperException.class, () -> {
            List<String> tableFields = new ArrayList<>();

            sqLiteManager.createTable(tableName, tableFields);
        });

        assertFalse(sqLiteManager.checkIfTableExists(tableName));
    }

    @Test
    void deleteNotExistingTable() {
        String tableName = "this_table_not_exists";

        Assertions.assertThrows(KeyKeeperException.class, () -> sqLiteManager.dropTable(tableName));

        assertFalse(sqLiteManager.checkIfTableExists(tableName));
    }

    @Test
    void insertIntoAndSelectAllFromTable() {
        String tableName = "test_user_table";
        String tableColumns = "(id, login)";
        String[] values = {"1", "test1"};
        String[] values2 = {"2", "test2"};
        String[] values3 = {"3", "test3"};

        createTestUserTable();

        sqLiteManager.insertIntoTable(tableName, tableColumns, values);
        sqLiteManager.insertIntoTable(tableName, tableColumns, values2);
        sqLiteManager.insertIntoTable(tableName, tableColumns, values3);

        Object[][] result = sqLiteManager.selectFromTable(tableName);

        assertEquals(3, result.length);
        assertEquals(1, result[0][0]);
        assertEquals("test1", result[0][1]);
        assertEquals(2, result[1][0]);
        assertEquals("test2", result[1][1]);
        assertEquals(3, result[2][0]);
        assertEquals("test3", result[2][1]);
    }

    @Test
    void selectFromTableWithCondition() {
        String tableName = "test_user_table";
        String tableColumns = "(id, login)";
        String[] values = {"1", "test1"};
        String[] values2 = {"2", "test2"};
        String[] values3 = {"3", "test3"};

        createTestUserTable();

        sqLiteManager.insertIntoTable(tableName, tableColumns, values);
        sqLiteManager.insertIntoTable(tableName, tableColumns, values2);
        sqLiteManager.insertIntoTable(tableName, tableColumns, values3);

        Object[][] result = sqLiteManager.selectFromTableWithCondition(tableName, "id = 2");

        assertEquals(1, result.length);
        assertEquals(2, result[0][0]);
        assertEquals("test2", result[0][1]);

        logger.info(result);
    }

    @Test
    void updateDataFromTable() {
        String tableName = "test_user_table";
        String tableColumns = "(id, login)";
        String[] values = {"1", "test1"};
        Map<String, Object> updateValues = new HashMap<>();

        updateValues.put("login", "updateTest1");

        createTestUserTable();

        sqLiteManager.insertIntoTable(tableName, tableColumns, values);

        Object[][] result = sqLiteManager.selectFromTable(tableName);

        assertEquals(1, result.length);
        assertEquals(1, result[0][0]);
        assertEquals("test1", result[0][1]);

        sqLiteManager.updateTable(tableName, "id", 1, updateValues);

        result = sqLiteManager.selectFromTable(tableName);

        assertEquals(1, result.length);
        assertEquals(1, result[0][0]);
        assertEquals("updateTest1", result[0][1]);
    }

    @Test
    void deleteFromTable() {
        String tableName = "test_user_table";
        String tableColumns = "(id, login)";
        String[] values = {"1", "test1"};

        createTestUserTable();

        sqLiteManager.insertIntoTable(tableName, tableColumns, values);

        Object[][] result = sqLiteManager.selectFromTable(tableName);

        assertEquals(1, result.length);
        assertEquals(1, result[0][0]);
        assertEquals("test1", result[0][1]);

        sqLiteManager.deleteFromTable(tableName, "id", "1");

        result = sqLiteManager.selectFromTable(tableName);

        assertEquals(0, result.length);
    }

    private void createTestUserTable() {
        String tableName = "test_user_table";
        List<String> tableFields = new ArrayList<>();

        tableFields.add("id integer PRIMARY KEY");
        tableFields.add("login text NOT NULL");

        sqLiteManager.createTable(tableName, tableFields);

        assertTrue(sqLiteManager.checkIfTableExists(tableName));
    }
}
