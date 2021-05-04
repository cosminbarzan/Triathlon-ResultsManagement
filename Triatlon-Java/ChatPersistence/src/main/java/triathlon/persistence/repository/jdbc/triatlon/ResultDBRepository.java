package triathlon.persistence.repository.jdbc.triatlon;

import triathlon.model.Result;
import triathlon.model.Participant;
import triathlon.model.Stage;


import triathlon.persistence.ResultRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class ResultDBRepository implements ResultRepository {
    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public ResultDBRepository(Properties properties) {
        logger.info("Initializing ResultDBRepository with properties: {}", properties);
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public Result findOne(Integer integer) {
        Result searchedResult = null;
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Results where id = ?")){
            preparedStatement.setInt(1, integer);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int idParticipant = resultSet.getInt("idParticipant");
                    int idStage = resultSet.getInt("idStage");
                    double score = resultSet.getDouble("score");

                    Participant participant = new Participant();
                    participant.setId(idParticipant);

                    Stage stage = new Stage();
                    stage.setId(idStage);

                    Result result = new Result(participant, stage, score);

                    searchedResult = result;
                    return result;
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println(ex);
        }

        return searchedResult;
    }

    @Override
    public Iterable<Result> findAll() {
        return null;
    }

    @Override
    public Result save(Result entity) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into Results (idParticipant, idStage, score) values(?,?,?)")){
            preparedStatement.setInt(1, entity.getParticipant().getId());
            preparedStatement.setInt(2, entity.getStage().getId());
            preparedStatement.setDouble(3, entity.getScore());

            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {} results", result);
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println(ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Result delete(Integer integer) {
        return null;
    }

    @Override
    public Result update(Result entity) {
        return null;
    }
}

