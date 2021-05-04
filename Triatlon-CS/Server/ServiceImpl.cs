using System;
using System.Collections.Generic;
using System.Linq;
using Services;
using Triatlon_CSharp.Domain;
using Triatlon_CSharp.Repository;
using System.Threading.Tasks;

namespace Triatlon_CS
{
    public class ServiceImpl :MarshalByRefObject, IServices
    {

        private UserRepository userRepo;
        private ResultRepository resultRepo;
        private ParticipantRepository participantRepo;
        private StageRepository stageRepo;
        private readonly IDictionary <int, IObserver> loggedClients;

        public ServiceImpl(UserRepository userRepo, ResultRepository resultRepo, ParticipantRepository participantRepo, StageRepository stageRepo)
        {
            this.userRepo = userRepo;
            this.resultRepo = resultRepo;
            this.participantRepo = participantRepo;
            this.stageRepo = stageRepo;
            loggedClients = new Dictionary<int, IObserver>();
        }

        public User login(User user, IObserver client)
        {
            int idUser = userRepo.verifyAccount(user.Username, user.Password);
            User userR = userRepo.FindOne(idUser);

            if (userR != null)
            {
                Console.WriteLine(userR.Id);
                if(loggedClients.ContainsKey(userR.Id))
                    throw new TriatlonException("User already logged in.");
                loggedClients[userR.Id]= client;
            }
            else
            {
                throw new TriatlonException("Authentication failed.");
            }
            return userR;
        }

        private void notifyResultAdded(Result result){
            Console.WriteLine("Notifying result added...");
            
            foreach(KeyValuePair<int, IObserver> entry in loggedClients)
            {
                // do something with entry.Value or entry.Key
                IObserver client = entry.Value;
                if (client != null)
                {
                    Task.Run(() => client.resultAdded(result));
                }
            }
        }

        public void addResult(Result result)
        {
            resultRepo.Save(result);
            Console.WriteLine("Added result. Now going to notify logged users...");

            notifyResultAdded(result);
        }

        public void logout(User user, IObserver client)
        {
            IObserver localClient = loggedClients[user.Id];
            if (localClient == null)
                throw new TriatlonException("User " + user.Id + " is not logged in.");
            loggedClients.Remove(user.Id);
        }

        public Participant[] getParticipantsByScore(int idStage)
        {
            Console.WriteLine("Get participants by score from stage : " + idStage);
            IList<Participant> participants = (List<Participant>)participantRepo.GetParticipantsByScore(idStage);

            return participants.ToArray();
        }

        public ParticipantDTO[] getParticipantsAndScore()
        {
            Console.WriteLine("Get participants and score");
            
            IList<ParticipantDTO> participants = new List<ParticipantDTO>();

            foreach (var participant in participantRepo.FindAll())
            {
                Double score = participantRepo.getParticipantScore(participant.Id);
                ParticipantDTO p = new ParticipantDTO(participant.Id, participant.FirstName, participant.LastName, score);
                participants.Add(p);
            }

            return participants.ToArray();
        }

        public Participant findParticipant(int id)
        {
            return participantRepo.FindOne(id);
        }

        public User findUser(int id)
        {
            return userRepo.FindOne(id);
        }

        public User findUserByUsername(User user)
        {
            return userRepo.FindByUsername(user.Username);
        }

        public override object InitializeLifetimeService()
        {
            return null;
        }
    }
}