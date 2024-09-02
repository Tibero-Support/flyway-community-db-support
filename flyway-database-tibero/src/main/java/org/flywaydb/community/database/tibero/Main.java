package org.flywaydb.community.database.tibero;

import org.flywaydb.core.Flyway;

public class Main {

    public static void main(String[] args) {
        Flyway flyway = Flyway.configure()
                .cleanDisabled(false)
            .dataSource("jdbc:tibero:thin:@localhost:8629:tibero", "tibero", "tibero").load();

        flyway.clean();
        flyway.migrate();
    }
}
