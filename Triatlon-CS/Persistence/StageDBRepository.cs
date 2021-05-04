using System.Collections.Generic;
using System.Data;
using log4net;
using Triatlon_CSharp.Domain;
using Triatlon_CSharp.Utils;

namespace Triatlon_CSharp.Repository
{
    public class StageDBRepository : StageRepository
    {
        private static readonly ILog Log = LogManager.GetLogger("StageDBRepository");

        public StageDBRepository()
        {
            Log.Info("Creating StageDBRepository");
        }
        
        public Stage FindOne(int id)
        {
            Log.InfoFormat("Entering FindOne with value {0}", id);
            IDbConnection connection = DBUtils.getConnection();

            Stage searchedStage = null;

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select * from Stages where id=@id";
                var paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = id;
                command.Parameters.Add(paramId);

                using (var dataReader = command.ExecuteReader())
                {
                    if (dataReader.Read())
                    {
                        int idS = dataReader.GetInt32(0);
                        string name = dataReader.GetString(1);

                        Stage stage = new Stage(name);
                        stage.Id = idS;
                        
                        searchedStage = stage;
                    }
                }
            }

            return searchedStage;
        }

        public IEnumerable<Stage> FindAll()
        {
            throw new System.NotImplementedException();
        }

        public Stage Save(Stage entity)
        {
            throw new System.NotImplementedException();
        }

        public Stage Delete(int id)
        {
            throw new System.NotImplementedException();
        }

        public Stage Update(Stage entity)
        {
            throw new System.NotImplementedException();
        }
    }
}