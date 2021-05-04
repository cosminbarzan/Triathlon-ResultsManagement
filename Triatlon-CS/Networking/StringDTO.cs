using System;

namespace Networking
{
    [Serializable]
    public class StringDTO
    {
        public StringDTO(string username)
        {
            Username = username;
        }

        public String Username { get; set; }
    }
}