package software.mayr.smserver.database.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sqlutil.wrapper.QueryBuilder;
import software.mayr.smserver.data.Data;
import software.mayr.smserver.database.DataRegistry;
import software.mayr.smserver.database.DataSupplier;
import software.mayr.smserver.database.mysql.dataIml.messagedata.MySqlListMessageDataSupplier;
import software.mayr.smserver.database.mysql.dataIml.messagedata.MySqlMessageDataSupplier;
import software.mayr.smserver.database.mysql.dataIml.userchatdata.MySqlListUserChatDataSupplier;
import software.mayr.smserver.database.mysql.dataIml.userchatdata.MySqlUserChatDataSupplier;
import software.mayr.smserver.database.mysql.dataIml.userdata.MySqlUserDataSupplier;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public record MySqlDataRegistry(DataSource dataSource, List<DataSupplier<?>> registeredDataSupplier,
                                List<DataSupplier<?>> registeredListDataSupplier) implements DataRegistry {

    public static MySqlDataRegistry create(String address, String database, int port, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", address, port, database));
        config.setConnectionTimeout(5000);
        config.setMaximumPoolSize(20);
        return new MySqlDataRegistry(new HikariDataSource(config));
    }

    public MySqlDataRegistry(DataSource dataSource) {
        this(dataSource, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public CompletableFuture<Integer> setUpDataRegistry() {
        MySqlUserDataSupplier mySqlUserDataSupplier = new MySqlUserDataSupplier(dataSource());
        MySqlUserChatDataSupplier mySqlUserChatDataSupplier = new MySqlUserChatDataSupplier(dataSource());
        MySqlMessageDataSupplier mySqlMessageDataSupplier = new MySqlMessageDataSupplier(dataSource());

        MySqlListUserChatDataSupplier mySqlListUserChatDataSupplier = new MySqlListUserChatDataSupplier(dataSource());
        MySqlListMessageDataSupplier mySqlListMessageDataSupplier = new MySqlListMessageDataSupplier(dataSource());

        this.registeredDataSupplier.add(mySqlUserDataSupplier);
        this.registeredDataSupplier.add(mySqlUserChatDataSupplier);
        this.registeredDataSupplier.add(mySqlMessageDataSupplier);

        this.registeredListDataSupplier.add(mySqlListUserChatDataSupplier);
        this.registeredListDataSupplier.add(mySqlListMessageDataSupplier);

        return QueryBuilder.builder(dataSource()).defaultConfig()
                        .queryWithoutParams(mySqlUserDataSupplier.createTableSqlString())
                                .append()
                                        .queryWithoutParams(mySqlUserChatDataSupplier.createTableSqlString())
                .append().queryWithoutParams(mySqlMessageDataSupplier.createTableSqlString()).update().execute();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T extends Data> DataSupplier<T> getDataSupplier(Class<T> dataClazz) {
        return (DataSupplier<T>) this.registeredDataSupplier.stream()
                .filter(dataSupplier -> dataSupplier.getGenericClass().equals(dataClazz))
                .findFirst().orElseThrow(() -> new UnsupportedOperationException(String.format("%s is not suppported!",
                        dataClazz.getSimpleName())));
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T extends Data> DataSupplier<List<T>> getListDataSupplier(Class<T> dataClazz) {
        return (DataSupplier<List<T>>) this.registeredListDataSupplier.stream()
                .filter(dataSupplier -> dataSupplier.getGenericClass().equals(dataClazz))
                .findFirst().orElseThrow(() -> new UnsupportedOperationException(String.format("%s is not suppported!",
                        dataClazz.getSimpleName())));
    }

    @Override
    public void close() {
        if (dataSource instanceof HikariDataSource hikariDataSource)
            hikariDataSource.close();
    }
}
