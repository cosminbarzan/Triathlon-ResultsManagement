using System.Data;
using System.Reflection;
using System;
using System.Data.SQLite;
using System.Configuration;

namespace Triatlon_CSharp.Utils
{
    public class DBUtils
    {
        private static IDbConnection instance = null;

        private static string GetConnectionStringByName(string name)
        {
            string returnValue = null;

            ConnectionStringSettings settings = ConfigurationManager.ConnectionStrings[name];

            if (settings != null)
                returnValue = settings.ConnectionString;

            return returnValue;
        }
        
        public static IDbConnection getConnection()
        {
            if (instance == null || instance.State == System.Data.ConnectionState.Closed)
            {
                instance = getNewConnection();
                instance.Open();
            }
            return instance;
        }

        private static IDbConnection getNewConnection()
        {
            return ConnectionFactory.getInstance().createConnection(GetConnectionStringByName("triathlon.db"));
        }
    }
    
    public abstract class ConnectionFactory
    {
        protected ConnectionFactory()
        {
        }

        private static ConnectionFactory instance;

        public static ConnectionFactory getInstance()
        {
            if (instance == null)
            {

                Assembly assembly = Assembly.GetExecutingAssembly();
                Type[] types = assembly.GetTypes();
                foreach (var type in types)
                {
                    if (type.IsSubclassOf(typeof(ConnectionFactory)))
                        instance = (ConnectionFactory)Activator.CreateInstance(type);
                }
            }
            return instance;
        }

        public abstract  IDbConnection createConnection(string connectionString);
    }
    
    public class SqliteConnectionFactory : ConnectionFactory
    {
        public override IDbConnection createConnection(string connectionString)
        {
            connectionString = "Data Source=triathlon.db;Version=3";
            
            //connectionString = "Data Source=C:\\Users\\barza\\source\\repos\\An2-Sem2\\MPP\\Lab\\Triatlon-CS\\Server\\bin\\Debug\\triathlon.db;Version=3";
            // Windows Sqlite Connection, fisierul .db ar trebuie sa fie in directorul debug/bin
            return new SQLiteConnection(connectionString);
        }
    }
}   