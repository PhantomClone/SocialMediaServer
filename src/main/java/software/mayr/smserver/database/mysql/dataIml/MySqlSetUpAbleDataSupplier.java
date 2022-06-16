package software.mayr.smserver.database.mysql.dataIml;

import javax.sql.DataSource;

public interface MySqlSetUpAbleDataSupplier {

    DataSource dataSource();
    String createTableSqlString();

}
