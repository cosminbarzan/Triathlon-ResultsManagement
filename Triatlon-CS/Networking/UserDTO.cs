using System;

namespace Networking
{
    [Serializable]
    public class UserDTO
    {
        public UserDTO(int id, string firstName, string lastName, string username, string password, int idStage)
        {
            Id = id;
            FirstName = firstName;
            LastName = lastName;
            Username = username;
            Password = password;
            IdStage = idStage;
        }
        
        public int Id { get; set; }
        
        public String FirstName { get; set; }

        public String LastName { get; set; }

        public String Username { get; set; }

        public String Password { get; set; }

        public int IdStage { get; set; }
    }
}