using System.Collections.Generic;
using System.Data;
using log4net;
using Triatlon_CSharp.Domain;
using Triatlon_CSharp.Utils;

namespace Triatlon_CSharp.Repository
{
    public class ResultDBRepository : ResultRepository
    {
        private static readonly ILog Log = LogManager.GetLogger("ResultDBRepository");

        public ResultDBRepository()
        {
            Log.Info("Creating ResultDBRepository");    
        }
        
        public Result FindOne(int id)
        {
            Result searchedResult = null;
            Log.InfoFormat("Entering findOne with value {0}", id);
            IDbConnection connection = DBUtils.getConnection();

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select * from Results where id=@id";
                IDbDataParameter paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = id;
                command.Parameters.Add(paramId);

                using (var dataReader = command.ExecuteReader())
                {
                    if (dataReader.Read())
                    {
                        int idR = dataReader.GetInt32(0);
                        int idParticipant = dataReader.GetInt32(1);
                        int idStage = dataReader.GetInt32(2);
                        double score = dataReader.GetDouble(3);

                        Participant participant = new Participant();
                        participant.Id = idParticipant;
                        Stage stage = new Stage();
                        stage.Id = idStage;

                        Result result = new Result(participant, stage, score);
                        result.Id = idR;

                        searchedResult = result;
                    }
                }
            }

            return searchedResult;
        }

        public IEnumerable<Result> FindAll()
        {
            throw new System.NotImplementedException();
        }

        public Result Save(Result entity)
        {
            var connection = DBUtils.getConnection();

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "insert into Results (idParticipant, idStage, score) values (@idParticipant, @idStage, @score)";

                var paramIdParticipant = command.CreateParameter();
                paramIdParticipant.ParameterName = "@idParticipant";
                paramIdParticipant.Value = entity.Participant.Id;
                command.Parameters.Add(paramIdParticipant);
                
                var paramIdStage = command.CreateParameter();
                paramIdStage.ParameterName = "@idStage";
                paramIdStage.Value = entity.Stage.Id;
                command.Parameters.Add(paramIdStage);
                
                var paramScore = command.CreateParameter();
                paramScore.ParameterName = "@score";
                paramScore.Value = entity.Score;
                command.Parameters.Add(paramScore);

                var result = command.ExecuteNonQuery();
            }

            return null;
        }

        public Result Delete(int id)
        {
            throw new System.NotImplementedException();
        }

        public Result Update(Result entity)
        {
            throw new System.NotImplementedException();
        }
    }
}