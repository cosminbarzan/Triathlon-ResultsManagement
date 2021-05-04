using System;

namespace Triatlon_CSharp.Domain
{
    [Serializable]
    public class ParticipantDTO
    {
        public ParticipantDTO() {}
        
        public ParticipantDTO(int id, string firstName, string lastName, Double score)
        {
            Id = id;
            FirstName = firstName;
            LastName = lastName;
            Score = score;
        }

        public int Id { get; set; }
        
        public String FirstName { get; set; }

        public String LastName { get; set; }
        
        public Double Score { get; set; }

        public override string ToString()
        {
            return string.Format("Id = {0} | {1} {2} {3}", Id, FirstName, LastName, Score);
        }
    }
}