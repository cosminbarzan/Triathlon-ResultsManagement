using System;

namespace Triatlon_CSharp.Domain
{
    [Serializable]
    public class Result : Entity<int>
    {
        public Result(Participant participant, Stage stage, double score)
        {
            Participant = participant;
            Stage = stage;
            Score = score;
        }

        public Participant Participant { get; set; }

        public Stage Stage { get; set; }

        public double Score { get; set; }

        public override string ToString()
        {
            return string.Format("Id = {0} | participant = {1} | stage = {2} | score = {3}", Id, Participant.Id, Stage.Id, Score);
        }
    }
}