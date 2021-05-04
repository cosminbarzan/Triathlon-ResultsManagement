package triathlon.persistence.repository.jdbc.triatlon;

import triathlon.model.Stage;
import triathlon.model.User;


import triathlon.persistence.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class UserDBRepository implements UserRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public UserDBRepository(Properties properties) {
        logger.info("Initializing UserDBRepository with properties: {}", properties);
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public User findOne(Integer integer) {
        User searchedUser = null;
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Users where id = ?")){
            preparedStatement.setInt(1, integer);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int idStage = resultSet.getInt("idStage");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    Stage stage = new Stage();
                    stage.setId(idStage);

                    User user = new User(firstName, lastName, username, password, stage);
                    user.setId(id);
                    searchedUser = user;
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println(ex);
        }

        return searchedUser;
    }

    @Override
    public Iterable<User> findAll() {
        return null;
    }

    @Override
    public User save(User entity) {
        return null;
    }

    @Override
    public User delete(Integer integer) {
        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public Integer verifyAccount(String username, String password) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Integer id = -1;

        try (PreparedStatement preparedStatement = connection.prepareStatement("select id from Users where username=? and password=?")){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    id = resultSet.getInt("id");
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }

        return id;
    }
}
