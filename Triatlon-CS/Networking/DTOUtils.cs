using System;
using Triatlon_CSharp.Domain;

namespace Networking
{
    public class DTOUtils
    {
        public static User getFromDTO(UserDTO usdto)
        {
            string firstName = usdto.FirstName;
            string lastName = usdto.LastName;
            string username = usdto.Username;
            string password = usdto.Password;
            int idStage = usdto.IdStage;
            int id = usdto.Id;

            Stage stage = new Stage();
            stage.Id = idStage;

            User user = new User(firstName, lastName, username, password, stage);
            user.Id = id;

            return user;

        }
        public static ResultDTO getDTO(Result result)
        {
            int idParticipant = result.Participant.Id;
            int idStage = result.Stage.Id;
            Double score = result.Score;

            return new ResultDTO(result.Id, idParticipant, idStage, score);
        }
        
        public static Result getFromDTO(ResultDTO rdto)
        {
            Participant participant = new Participant();
            participant.Id = rdto.IdParticipant;

            Stage stage = new Stage();
            stage.Id = rdto.IdStage;

            Double score = rdto.Score;

            return new Result(participant, stage, score);

        }
        public static UserDTO getDTO(User user)
        {
            string firstName = user.FirstName;
            string lastName = user.LastName;
            string username = user.Username;
            string password = user.Password;
            int idStage = user.Stage.Id;

            return new UserDTO(user.Id, firstName, lastName, username, password, idStage);
        }
    }
}