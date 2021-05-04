using System.Collections.Generic;
using Triatlon_CSharp.Domain;

namespace Triatlon_CSharp.Repository
{
    public interface ParticipantRepository : IRepository<int, Participant>
    {
        IEnumerable<Participant> GetParticipantsByScore(int idStage);
        
        double getParticipantScore(int id);
    }
}