package org.flywaydb.community.database.tibero;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.AfterEach;

abstract class TiberoFlywayBaseTest {

    public static final String TIBERO_URL = "jdbc:tibero:thin:@localhost:8629:tibero";
    public static final String TIBERO_USER = "tibero";
    public static final String TIBERO_PASSWORD = "tibero";

    private static final Set<String> EXPECTED_ALL_SCRIPTS = Set.of(
        "V1__create_series.sql", "V2__create_seasons.sql",
        "V3__create_episodes.sql", "V4__load_data.sql",
        "V5__create_series_title_index.sql", "V6__rename_series_title_index.sql"
    );

    protected static void assertCountTable(int expectedSize, String sql, Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery(sql);
        rs.next();

        assertEquals(expectedSize, rs.getLong(1));
    }

    protected static FluentConfiguration createFlyway(String migrationsDir) {
        return Flyway.configure()
            .locations(migrationsDir)
            .dataSource(TIBERO_URL, TIBERO_USER, TIBERO_PASSWORD);
    }

    protected void verifyTest() throws SQLException {
        try (Connection connection = DriverManager
            .getConnection("jdbc:tibero:thin:@localhost:8629:tibero", "tibero", "tibero")) {

            try (Statement statement = connection.createStatement()) {
                verifyCountTables(statement);

                ResultSet rs = statement.executeQuery("SELECT script FROM flyway_schema_history;");
                HashSet<String> scripts = new HashSet<>();

                while (rs.next()) {
                    scripts.add(rs.getString(1));
                }

//                assertEquals(expectedScripts(), scripts);
            }
        }
    }

    protected void verifyCountTables(Statement statement) throws SQLException {
        assertCountTable(2, "SELECT COUNT(*) FROM series", statement);
        assertCountTable(9, "SELECT COUNT(*) FROM seasons", statement);
        assertCountTable(70, "SELECT COUNT(*) FROM episodes", statement);
    }

    protected Set<String> expectedScripts() {
        return EXPECTED_ALL_SCRIPTS;
    }

//    @AfterEach
    void checkAfterTest() throws SQLException {
        verifyTest();

        try (Connection connection = DriverManager
            .getConnection("jdbc:tibero:thin:@localhost:8629:tibero", "tibero", "tibero")) {

            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "DROP TABLE TIBERO.\"flyway_schema_history\";"
                );
            }
        }
    }
}
