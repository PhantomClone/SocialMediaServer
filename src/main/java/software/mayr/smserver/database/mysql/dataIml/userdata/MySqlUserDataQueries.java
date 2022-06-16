package software.mayr.smserver.database.mysql.dataIml.userdata;

import de.chojo.sqlutil.conversion.UUIDConverter;
import de.chojo.sqlutil.wrapper.QueryBuilder;
import de.chojo.sqlutil.wrapper.QueryBuilderConfig;
import de.chojo.sqlutil.wrapper.stage.ResultStage;
import de.chojo.sqlutil.wrapper.stage.StatementStage;
import de.chojo.sqlutil.wrapper.stage.UpdateStage;
import software.mayr.smserver.data.userdata.UserData;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class MySqlUserDataQueries extends UserData {

        private final StatementStage<?> insertUserQuery;
        private final StatementStage<UserData> userExist, selectUserByUuidQuery, selectUserByUserNameQuery;

        public MySqlUserDataQueries(DataSource dataSource) {
            this.insertUserQuery = QueryBuilder.builder(dataSource).defaultConfig()
                    .query("INSERT INTO User (userUuid, username, password, email) VALUES(?, ?, ?, ?)");
            this.userExist = QueryBuilder.builder(dataSource, UserData.class).defaultConfig()
                    .query("SELECT userUuid FROM User WHERE username=?");
            this.selectUserByUuidQuery = QueryBuilder.builder(dataSource, UserData.class).defaultConfig()
                    .query("SELECT * FROM User WHERE userUuid LIKE BINARY ? and password LIKE BINARY ?");
            this.selectUserByUserNameQuery = QueryBuilder.builder(dataSource, UserData.class).defaultConfig()
                    .query("SELECT * FROM User WHERE username LIKE BINARY ? and password LIKE BINARY ?");
        }

        public String createTableSqlString() {
            return "CREATE TABLE IF NOT EXISTS User(userUuid BINARY(16) PRIMARY KEY NOT NULL, username VARCHAR(32) NOT NULL, password VARCHAR(32) NOT NULL, email VARCHAR(32) NOT NULL)";
        }

        public CompletableFuture<Integer> insertUser(UserData userdata) {
            return this.insertUserQuery.params(preparedStatement -> {
                preparedStatement.setBytes(1, UUIDConverter.convert(userdata.getUserUuid()));
                preparedStatement.setString(2, userdata.getUserName());
                preparedStatement.setString(3, userdata.getPassword());
                preparedStatement.setString(4, userdata.getEmail());
            }).insert().execute();
        }

        public CompletableFuture<Optional<UserData>> doesUserExist(String username) {
            return this.userExist.params(preparedStatement -> preparedStatement.setString(1, username))
                    .readRow(resultSet -> {
                        setUserUuid(UUIDConverter.convert(resultSet.getBytes("userUuid")));
                        setUserName(username);
                        setPassword(null);
                        setEmail(null);
                        return this;
                    }).first();
        }

        public CompletableFuture<Optional<UserData>> getUser(String username, String password) {
            return this.selectUserByUserNameQuery.params(preparedStatement -> {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
            }).readRow(resultSet -> {
                setUserUuid(UUIDConverter.convert(resultSet.getBytes("userUuid")));
                setUserName(resultSet.getString("username"));
                setPassword(resultSet.getString("password"));
                setEmail(resultSet.getString("email"));
                return this;
            }).first();
        }

        public CompletableFuture<Optional<UserData>> getUser(UUID userUuid, String password) {
            return this.selectUserByUuidQuery.params(preparedStatement -> {
                preparedStatement.setBytes(1, UUIDConverter.convert(userUuid));
                preparedStatement.setString(2, password);
            }).readRow(resultSet -> {
                setUserUuid(UUIDConverter.convert(resultSet.getBytes("userUuid")));
                setUserName(resultSet.getString("username"));
                setPassword(resultSet.getString("password"));
                setEmail(resultSet.getString("email"));
                return this;
            }).first();
        }

}
