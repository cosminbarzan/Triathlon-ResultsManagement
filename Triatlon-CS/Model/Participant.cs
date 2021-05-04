using System;

namespace Triatlon_CSharp.Domain
{
    [Serializable]
    public class Participant : Entity<int>
    {
        public Participant() {}
        
        public Participant(string firstName, string lastName)
        {
            FirstName = firstName;
            LastName = lastName;
        }

        public String FirstName { get; set; }

        public String LastName { get; set; }

        public override string ToString()
        {
            return string.Format("Id = {0} | {1} {2}", Id, FirstName, LastName);
        }
    }
}