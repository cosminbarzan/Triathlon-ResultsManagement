package triathlon.persistence.repository.jdbc.triatlon;

import triathlon.model.Stage;


import triathlon.persistence.StageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class StageDBRepository implements StageRepository {
    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public StageDBRepository(Properties properties) {
        logger.info("Initializing StageDBRepository with properties: {}", properties);
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public Stage findOne(Integer integer) {
        Stage searchedStage = null;
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Stages where id = ?")){
            preparedStatement.setInt(1, integer);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");

                    Stage stage = new Stage(name);
                    stage.setId(id);

                    searchedStage = stage;
                    return stage;
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println(ex);
        }

        return searchedStage;
    }

    @Override
    public Iterable<Stage> findAll() {
        return null;
    }

    @Override
    public Stage save(Stage entity) {
        return null;
    }

    @Override
    public Stage delete(Integer integer) {
        return null;
    }

    @Override
    public Stage update(Stage entity) {
        return null;
    }
}
