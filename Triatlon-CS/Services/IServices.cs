using Triatlon_CSharp.Domain;

namespace Services
{
    public interface IServices
    {
        User login(User user, IObserver client);
        void addResult(Result result);
        void logout(User user, IObserver client);
        Participant[] getParticipantsByScore(int idStage);
        ParticipantDTO[] getParticipantsAndScore();
        Participant findParticipant(int id);
        User findUser(int id);
        User findUserByUsername(User user);
    }
}