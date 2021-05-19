package triathlon.persistence.repository.jdbc.triatlon;

import org.springframework.stereotype.Component;
import triathlon.model.Participant;
import triathlon.model.Stage;


import triathlon.persistence.StageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
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
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Stage> stages = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Stages")){
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");

                    Stage stage = new Stage(name);
                    stage.setId(id);

                    stages.add(stage);
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }

        return stages;
    }

    @Override
    public Stage save(Stage entity) {
        logger.traceEntry("saving stage {}", entity);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into Stages (name) values(?)", Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, entity.getName());

            int result = preparedStatement.executeUpdate();
            if (result > 0){
                //obtinem ID-ul generat de baza de date
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entity.setId(id);
                    logger.trace("Generated id {} ", id);
                }

            }
            logger.trace("Saved {} stages", result);
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Stage delete(Integer id) {
        logger.traceEntry("delete stage with id {}", id);
        if(id == null)
            throw new IllegalArgumentException("id must be not null");

        Stage stage = findOne(id);
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from Stages where id=?")) {
            preparedStatement.setInt(1, id);

            int result = preparedStatement.executeUpdate();
            logger.trace("Deleted {} stages", result);
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println(ex);
        }
        return stage;
    }

    @Override
    public Stage update(Stage entity) {
        logger.traceEntry();
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("update Stages set name=? where id=?")){
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setInt(2, entity.getId());

            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} stages", result);

            if(result == 0)
                entity = null;
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println(ex);
        }

        return entity;
    }
}
