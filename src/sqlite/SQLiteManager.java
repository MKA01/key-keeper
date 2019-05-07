package sqlite;

import org.apache.log4j.Logger;
import utils.KeyKeeperException;

import java.sql.*;
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

    boolean isConnectionEstablished() {
        return (connection != null);
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

    private void executeStatement(String sql) {
        logger.info("Trying to execute statement...");
        logger.info("Statement is: " + sql);

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);

            logger.info("Statement executed!");
        } catch (SQLException ex) {
            throw new KeyKeeperException("An error has occurred while executing statement!", ex);
        }
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
        String connectionString = "jdbc:sqlite:key-keeper";
        connection = DriverManager.getConnection(connectionString);

        if (connection != null) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            logger.info("Connection to database established! Driver name is: " + databaseMetaData.getDriverName());
        }
    }
}
