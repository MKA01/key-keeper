package sqlite;

import org.apache.log4j.Logger;
import utils.KeyKeeperException;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class SQLiteManager {

    private final static Logger logger = Logger.getLogger(SQLiteManager.class);
    private static SQLiteManager ourInstance = new SQLiteManager();
    private Connection connection;

    private SQLiteManager() {
        getDbConnection();
    }

    public static SQLiteManager getInstance() {
        return ourInstance;
    }

    public void createAppTables() {
        String createUsersTableSql = "CREATE TABLE USERS(ID INTEGER PRIMARY KEY NOT NULL, LOGIN TEXT NOT NULL, PASSWORD TEXT NOT NULL)";
        String createKeysTableSql = "CREATE TABLE KEYS(ID INTEGER PRIMARY KEY NOT NULL, USER_ID INTEGER NOT NULL, SERVICE TEXT NOT NULL, LOGIN TEXT NOT NULL, PASSWORD TEXT NOT NULL)";

        if (!checkIfTableExists("USERS")) {
            executeStatement(createUsersTableSql);
        }

        if (!checkIfTableExists("KEYS")) {
            executeStatement(createKeysTableSql);
        }
    }

    public void createTable(String tableName, List<String> tableFields) {
        String formattedTableFields = tableFields.stream().map(String::valueOf).collect(Collectors.joining(",", "(", ")"));
        String createTableSql = "CREATE TABLE " + tableName + formattedTableFields;

        executeStatement(createTableSql);
    }

    public void dropTable(String tableName) {
        String dropTableSql = "DROP TABLE " + tableName;

        executeStatement(dropTableSql);
    }

    public void insertIntoTable(String tableName, String tableColumns, String[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = "'" + values[i] + "'";
        }

        String formattedValues = Arrays.stream(values).map(String::valueOf).collect(Collectors.joining(",", "(", ")"));
        String insertIntoSql = "INSERT INTO " + tableName + tableColumns + " VALUES" + formattedValues;

        executeStatement(insertIntoSql);
    }

    public Object[][] selectFromTable(String tableName) {
        String selectFromSql = "SELECT * FROM " + tableName;

        return executeQuery(selectFromSql);
    }

    public Object[][] selectFromTableWithCondition(String tableName, String condition) {
        String selectFromWithConditionSql = tableName + " WHERE " + condition;

        return selectFromTable(selectFromWithConditionSql);
    }

    public void updateTable(String tableName, String keyName, Object keyValue, Map<String, Object> values) {
        StringBuilder updateSql = new StringBuilder("UPDATE " + tableName + " SET ");

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            updateSql.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(",");
        }

        updateSql.deleteCharAt(updateSql.length() - 1);

        updateSql.append(" WHERE ").append(keyName).append(" = '").append(keyValue).append("'");

        executeStatement(updateSql.toString());
    }

    public void deleteFromTable(String tableName, String keyName, Object keyValue) {
        String deleteFromSql = "DELETE FROM " + tableName + " WHERE " + keyName + " = '" + keyValue + "'";

        executeStatement(deleteFromSql);
    }

    public boolean isConnectionEstablished() {
        return (connection != null);
    }

    public boolean checkIfTableExists(String tableName) {
        String query = "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = '" + tableName + "'";
        Object[][] result = executeQuery(query);

        return Integer.valueOf(result[0][0].toString()) > 0;
    }

    private void getDbConnection() {
        logger.info("Trying to get connection...");

        try {
            tryToGetDbConnection();
        } catch (SQLException ex) {
            logger.error("An error has occurred while trying to get connection!", ex);
        }
    }

    private void tryToGetDbConnection() throws SQLException {
        String connectionString = "jdbc:sqlite:db/key-keeper";
        connection = DriverManager.getConnection(connectionString);

        if (connection != null) {
            logger.info("Connection to database established! Driver name is: " + connection.getMetaData().getDriverName());
        }
    }

    private void executeStatement(String sql) {
        logger.info("Trying to execute statement...\nStatement is: " + sql);

        try {
            tryToExecuteStatement(sql);
        } catch (SQLException ex) {
            throw new KeyKeeperException("An error has occurred while executing statement!", ex);
        }
    }

    private void tryToExecuteStatement(String sql) throws SQLException {
        Statement statement = connection.createStatement();

        statement.execute(sql);
        statement.close();

        logger.info("Statement executed!");
    }

    private Object[][] executeQuery(String sql) {
        logger.info("Trying to execute query... \nQuery is: " + sql);

        try {
            return tryToExecuteQuery(sql);
        } catch (SQLException ex) {
            throw new KeyKeeperException("An error has occurred while executing query!", ex);
        }
    }

    private Object[][] tryToExecuteQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Object[][] result = convertResultSetToList(resultSet);

        statement.close();

        logger.info("Query executed!");

        return result;
    }

    private Object[][] convertResultSetToList(ResultSet resultSet) {
        try {
            return tryToConvertResultSetToList(resultSet);
        } catch (SQLException ex) {
            throw new KeyKeeperException("An error has occurred while converting result to list!", ex);
        }
    }

    private Object[][] tryToConvertResultSetToList(ResultSet resultSet) throws SQLException {
        List<List<Object>> tempResult = new ArrayList<>();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        while (resultSet.next()) {
            List<Object> row = new ArrayList<>();

            for (int i = 1; i <= columnCount; ++i) {
                row.add(resultSet.getObject(i));
            }

            tempResult.add(row);
        }

        Object[][] result = new Object[tempResult.size()][columnCount];

        for (int i = 0; i < tempResult.size(); i++) {
            for (int j = 0; j < columnCount; j++) {
                result[i][j] = tempResult.get(i).get(j);
            }
        }

        return result;
    }
}
