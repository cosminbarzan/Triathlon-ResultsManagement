using System;

namespace Networking
{
    [Serializable]
    public class ResultDTO
    {
        public ResultDTO(int id, int idParticipant, int idStage, double score)
        {
            Id = id;
            IdParticipant = idParticipant;
            IdStage = idStage;
            Score = score;
        }

        public int Id { get; set; }

        public int IdParticipant { get; set; }

        public int IdStage { get; set; }

        public double Score { get; set; }

        public override string ToString()
        {
            return string.Format("Id = {0} | participant = {1} | stage = {2} | score = {3}", Id, IdParticipant, IdStage, Score);
        }
    }
}