using System.Collections.Generic;
using System.Data;
using log4net;
using Triatlon_CSharp.Domain;
using Triatlon_CSharp.Utils;

namespace Triatlon_CSharp.Repository
{
    public class UserDBRepository : UserRepository
    {
        private static readonly ILog Log = LogManager.GetLogger("UserDBRepository");

        public UserDBRepository()
        {
            Log.Info("Creating UserDBRepository");
        }
        
        public User FindOne(int id)
        {
            Log.InfoFormat("Entering FindOne with value {0}", id);
            IDbConnection connection = DBUtils.getConnection();

            User searchedUser = null;

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select * from Users where id=@id";
                var paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = id;
                command.Parameters.Add(paramId);

                using (var dataReader = command.ExecuteReader())
                {
                    if (dataReader.Read())
                    {
                        int idU = dataReader.GetInt32(0);
                        int idStage = dataReader.GetInt32(1);
                        string firstName = dataReader.GetString(2);
                        string lastName = dataReader.GetString(3);
                        string username = dataReader.GetString(4);
                        string password = dataReader.GetString(5);

                        Stage stage = new Stage();
                        stage.Id = idStage;

                        User user = new User(firstName, lastName, username, password, stage);
                        user.Id = idU;

                        searchedUser = user;
                    }
                }
            }

            return searchedUser;
        }
        
        public User FindByUsername(string username)
        {
            Log.InfoFormat("Entering FindOne with value {0}", username);
            IDbConnection connection = DBUtils.getConnection();

            User searchedUser = null;

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select * from Users where username=@username";
                var paramUsername = command.CreateParameter();
                paramUsername.ParameterName = "@username";
                paramUsername.Value = username;
                command.Parameters.Add(paramUsername);

                using (var dataReader = command.ExecuteReader())
                {
                    if (dataReader.Read())
                    {
                        int idU = dataReader.GetInt32(0);
                        int idStage = dataReader.GetInt32(1);
                        string firstName = dataReader.GetString(2);
                        string lastName = dataReader.GetString(3);
                        string username2 = dataReader.GetString(4);
                        string password = dataReader.GetString(5);

                        Stage stage = new Stage();
                        stage.Id = idStage;

                        User user = new User(firstName, lastName, username2, password, stage);
                        user.Id = idU;

                        searchedUser = user;
                    }
                }
            }

            return searchedUser;
        }

        public IEnumerable<User> FindAll()
        {
            throw new System.NotImplementedException();
        }

        public User Save(User entity)
        {
            var connection = DBUtils.getConnection();

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "insert into Users (idStage, firstName, lastName, username, password) values (@idStage, @firstName, @lastName, @username, @password)";

                var paramIdStage = command.CreateParameter();
                paramIdStage.ParameterName = "@idStage";
                paramIdStage.Value = entity.Stage.Id;
                command.Parameters.Add(paramIdStage);
                
                var paramFirstName = command.CreateParameter();
                paramFirstName.ParameterName = "@firstName";
                paramFirstName.Value = entity.FirstName;
                command.Parameters.Add(paramFirstName);
                
                var paramLastName = command.CreateParameter();
                paramLastName.ParameterName = "@lastName";
                paramLastName.Value = entity.LastName;
                command.Parameters.Add(paramLastName);
                
                var paramUsername = command.CreateParameter();
                paramUsername.ParameterName = "@username";
                paramUsername.Value = entity.Username;
                command.Parameters.Add(paramUsername);
                
                var paramPassword = command.CreateParameter();
                paramPassword.ParameterName = "@password";
                paramPassword.Value = entity.Password;
                command.Parameters.Add(paramPassword);

                var result = command.ExecuteNonQuery();
            }

            return null;
        }

        public int verifyAccount(string username, string password)
        {
            Log.InfoFormat("Entering verifyAccount with value {0}", username);
            IDbConnection connection = DBUtils.getConnection();

            int id = -1;

            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id from Users where username=@username and password=@password";
                var paramUsername = command.CreateParameter();
                paramUsername.ParameterName = "@username";
                paramUsername.Value = username;
                command.Parameters.Add(paramUsername);

                var paramPassword = command.CreateParameter();
                paramPassword.ParameterName = "@password";
                paramPassword.Value = password;
                command.Parameters.Add(paramPassword);

                using (var dataReader = command.ExecuteReader())
                {
                    if (dataReader.Read())
                    {
                        id = dataReader.GetInt32(0);
                    }
                }
            }

            return id;
        }

        public User Delete(int id)
        {
            throw new System.NotImplementedException();
        }

        public User Update(User entity)
        {
            throw new System.NotImplementedException();
        }
    }
}