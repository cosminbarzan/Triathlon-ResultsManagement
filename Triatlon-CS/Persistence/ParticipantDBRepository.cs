using System.Collections.Generic;
using System.Data;
using log4net;
using Triatlon_CSharp.Domain;
using Triatlon_CSharp.Utils;
using System;

namespace Triatlon_CSharp.Repository
{
    public class ParticipantDBRepository : ParticipantRepository
    {
        private static readonly ILog Log = LogManager.GetLogger("ParticipantDBRepository");

        public ParticipantDBRepository()
        {
            Log.Info("Creating ParticipantDBRepository");
        }

        public IEnumerable<Participant> GetParticipantsByScore(int idStage)
        {
            IDbConnection con = DBUtils.getConnection();
            IList<Participant> participants = new List<Participant>();
            
            using (var comm = con.CreateCommand())
            {
                comm.CommandText = "select * from Participants inner join Results R on Participants.id = R.idParticipant where R.idStage=@idStage order by R.score desc";
                var paramIdStage = comm.CreateParameter();
                paramIdStage.ParameterName = "@idStage";
                paramIdStage.Value = idStage;
                comm.Parameters.Add(paramIdStage);

                using (var dataR = comm.ExecuteReader())
                {
                    while (dataR.Read())
                    {
                        int idP = dataR.GetInt32(0);
                        string firstName = dataR.GetString(1);
                        string lastName = dataR.GetString(2);
                        
                        Participant participant = new Participant(firstName, lastName);
                        participant.Id = idP;

                        participants.Add(participant);
                    }
                }
            }

            return participants;
        }

        public double getParticipantScore(int id)
        {
            Log.InfoFormat("Entering getParticipantScore with value {0}", id);
            IDbConnection connection = DBUtils.getConnection();

            double score = 0;

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select sum(score) as val from Results where idParticipant=@idParticipant";
                var paramIdParticipant = command.CreateParameter();
                paramIdParticipant.ParameterName = "@idParticipant";
                paramIdParticipant.Value = id;
                command.Parameters.Add(paramIdParticipant);

                using (var dataReader = command.ExecuteReader())
                {
                    if (dataReader.Read())
                    {

                        object s = dataReader.GetValue(0);
                        if (s.Equals(System.DBNull.Value))
                        {

                        }
                        else
                            score = dataReader.GetDouble(0);
                    }
                }
            }

            return score;
        }

        public Participant FindOne(int id)
        {
            Log.InfoFormat("Entering FindOne with value {0}", id);
            IDbConnection connection = DBUtils.getConnection();

            Participant searchedParticipant = null;

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select * from Participants where id=@id";
                var paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = id;
                command.Parameters.Add(paramId);

                using (var dataReader = command.ExecuteReader())
                {
                    if (dataReader.Read())
                    {
                        int idP = dataReader.GetInt32(0);
                        string firstName = dataReader.GetString(1);
                        string lastName = dataReader.GetString(2);

                        Participant participant = new Participant(firstName, lastName);
                        participant.Id = idP;
                        searchedParticipant = participant;
                    }
                }
            }

            return searchedParticipant;
        }

        public IEnumerable<Participant> FindAll()
        {

            IDbConnection con = DBUtils.getConnection();
            IList<Participant> participants = new List<Participant>();

            using (var comm = con.CreateCommand())
            {
                comm.CommandText = "select * from Participants";

                using (var dataR = comm.ExecuteReader())
                {
                    while (dataR.Read())
                    {
                        int idP = dataR.GetInt32(0);
                        string firstName = dataR.GetString(1);
                        string lastName = dataR.GetString(2);

                        Participant participant = new Participant(firstName, lastName);
                        participant.Id = idP;

                        participants.Add(participant);
                    }
                }
            }

            return participants;
        }

        public Participant Save(Participant entity)
        {
            var connection = DBUtils.getConnection();

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "insert into Participants (firstName, lastName) values (@firstName, @lastName)";

                var paramFirstName = command.CreateParameter();
                paramFirstName.ParameterName = "@firstName";
                paramFirstName.Value = entity.FirstName;
                command.Parameters.Add(paramFirstName);
                
                var paramLastName = command.CreateParameter();
                paramLastName.ParameterName = "@lastName";
                paramLastName.Value = entity.LastName;
                command.Parameters.Add(paramLastName);

                var result = command.ExecuteNonQuery();
            }

            return null;
        }

        public Participant Delete(int id)
        {
            throw new System.NotImplementedException();
        }

        public Participant Update(Participant entity)
        {
            throw new System.NotImplementedException();
        }
    }
}