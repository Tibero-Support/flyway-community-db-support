/*-
 * ========================LICENSE_START=================================
 * flyway-database-tibero
 * ========================================================================
 * Copyright (C) 2010 - 2024 Red Gate Software Ltd
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.flywaydb.community.database.tibero;

import java.sql.Connection;
import java.sql.Types;
import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.base.BaseDatabaseType;
import org.flywaydb.core.internal.database.base.CommunityDatabaseType;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;
import org.flywaydb.core.internal.parser.Parser;
import org.flywaydb.core.internal.parser.ParsingContext;

public class TiberoDatabaseType extends BaseDatabaseType implements CommunityDatabaseType {

    @Override
    public String getName() {
        return "Tibero";
    }

    @Override
    public int getNullType() {
        return Types.VARCHAR;
    }

    @Override
    public boolean handlesJDBCUrl(String url) {
        return url.startsWith("jdbc:tibero");
    }

    @Override
    public String getDriverClass(String url, ClassLoader classLoader) {
        return "com.tmax.tibero.jdbc.TbDriver";
    }

    @Override
    public boolean handlesDatabaseProductNameAndVersion(String databaseProductName,
        String databaseProductVersion, Connection connection) {
        return true;
    }

    @Override
    public Database createDatabase(Configuration configuration,
        JdbcConnectionFactory jdbcConnectionFactory, StatementInterceptor statementInterceptor) {
        TiberoDatabase.enableTiberoTNSNameSupport();

        return new TiberoDatabase(configuration, jdbcConnectionFactory, statementInterceptor);
    }

    @Override
    public Parser createParser(Configuration configuration, ResourceProvider resourceProvider,
        ParsingContext parsingContext) {
        return new TiberoParser(configuration, parsingContext, 3);
    }
}
