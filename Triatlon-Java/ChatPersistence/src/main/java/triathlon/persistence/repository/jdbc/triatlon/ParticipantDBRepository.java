package triathlon.persistence.repository.jdbc.triatlon;

import triathlon.model.Participant;

import triathlon.persistence.ParticipantRepository;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParticipantDBRepository implements ParticipantRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public ParticipantDBRepository(Properties properties) {
        logger.info("Initializing ParticipantDBRepository with properties: {}", properties);
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public Iterable<Participant> getParticipantsByScore(Integer idStage) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();

        ArrayList<Participant> participants = new ArrayList<Participant>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Participants inner join Results R on Participants.id = R.idParticipant where R.idStage = ? order by R.score desc")){
            preparedStatement.setInt(1, idStage);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");

                    Participant participant = new Participant(firstName, lastName);
                    participant.setId(id);

                    participants.add(participant);
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }

        return participants;
    }

    @Override
    public Double getParticipantScore(Integer id) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Double score = 0d;

        try (PreparedStatement preparedStatement = connection.prepareStatement("select sum(score) as val from Results where idParticipant=?")){
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    score = resultSet.getDouble("val");
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }

        return score;
    }

    @Override
    public Participant findOne(Integer integer) {
        Participant searchedParticipant = null;
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Participants where id = ?")){
            preparedStatement.setInt(1, integer);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");

                    Participant participant = new Participant(firstName, lastName);
                    participant.setId(id);

                    searchedParticipant = participant;
                    return participant;
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println(ex);
        }

        return searchedParticipant;
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Participant> participants = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Participants")){
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");

                    Participant participant = new Participant(firstName, lastName);
                    participant.setId(id);

                    participants.add(participant);
                }
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }

        return participants;
    }

    @Override
    public Participant save(Participant entity) {
        logger.traceEntry("saving participant {}", entity);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into Participants (firstName, lastName) values(?,?)")){
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());

            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {} participants", result);
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Participant delete(Integer id) {
        logger.traceEntry("delete participant with id {}", id);
        if(id == null)
            throw new IllegalArgumentException("id must be not null");

        Participant participant = findOne(id);
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from Participants where id=?")) {
            preparedStatement.setInt(1, id);

            int result = preparedStatement.executeUpdate();
            logger.trace("Deleted {} participants", result);
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println(ex);
        }
        return participant;
    }

    @Override
    public Participant update(Participant entity) {
        logger.traceEntry();
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("update Participants set firstName=?, lastname=? where id=?")){
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setInt(3, entity.getId());

            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} participants", result);

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
