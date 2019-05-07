package sqlite;

import org.apache.log4j.Logger;
import utils.KeyKeeperException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void createTable(String tableName, List<String> tableFields) {
        String formattedTableFields = tableFields.toString().replace("[", "(").replace("]", ")");
        String createTableSql = "CREATE TABLE " + tableName + formattedTableFields;

        executeStatement(createTableSql);
    }

    public void deleteTable(String tableName) {
        String deleteTableSql = "DROP TABLE " + tableName;

        executeStatement(deleteTableSql);
    }

    boolean isConnectionEstablished() {
        return (connection != null);
    }

    boolean checkIfTableExists(String tableName) {
        String query = "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = '" + tableName + "'";
        List<String> result = executeQuery(query);

        return Integer.valueOf(result.get(0)) > 0;
    }

    private List<String> executeQuery(String query) {
        logger.info("Trying to execute query...");
        logger.info("Query is: " + query);

        try {
            return tryToExecuteQuery(query);
        } catch (SQLException ex) {
            throw new KeyKeeperException("An error has occurred while executing query!", ex);
        }

    }

    private List<String> tryToExecuteQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<String> result = convertResultSetToList(resultSet);

        statement.close();

        logger.info("Query executed!");

        return result;
    }

    private List<String> convertResultSetToList(ResultSet resultSet) {
        List<String> result = new ArrayList<>();

        try {
            tryToConvertResultSetToList(resultSet, result);
        } catch (SQLException ex) {
            throw new KeyKeeperException("An error has occurred while converting result!", ex);
        }

        return result;
    }

    private void tryToConvertResultSetToList(ResultSet resultSet, List<String> result) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; ++i) {
                logger.error("Result: " + resultSet.getString(i));
                result.add(resultSet.getString(i));

            }
        }

        logger.info("Result size: " + result.size());
    }

    private void executeStatement(String sql) {
        logger.info("Trying to execute statement...");
        logger.info("Statement is: " + sql);

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

    private void getDbConnection() {
        logger.info("Trying to get connection...");

        try {
            tryToGetDbConnection();
        } catch (SQLException ex) {
            logger.error("An error has occurred while trying to get connection");
            ex.printStackTrace();
        }
    }

    private void tryToGetDbConnection() throws SQLException {
        String connectionString = "jdbc:sqlite:db/key-keeper";
        connection = DriverManager.getConnection(connectionString);

        if (connection != null) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            logger.info("Connection to database established! Driver name is: " + databaseMetaData.getDriverName());
        }
    }
}
