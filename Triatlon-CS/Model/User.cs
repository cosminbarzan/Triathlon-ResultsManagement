using System;

namespace Triatlon_CSharp.Domain
{
    [Serializable]
    public class User : Entity<int>
    {
        public User(string firstName, string lastName, string username, string password, Stage stage)
        {
            FirstName = firstName;
            LastName = lastName;
            Username = username;
            Password = password;
            Stage = stage;
        }

        public String FirstName { get; set; }

        public String LastName { get; set; }

        public String Username { get; set; }

        public String Password { get; set; }

        public Stage Stage { get; set; }
    }
}