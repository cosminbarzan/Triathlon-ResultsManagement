using System;
using Networking;

using domain = Triatlon_CSharp.Domain;

namespace Protobuff
{
    static class ProtoUtils
    {
        public static Request createLoginRequest(Networking.UserDTO user)
        {
            //new proto.User {Id = user.Id, Passwd = user.Password};
            Protobuff.UserDTO userDto = new UserDTO
            {
                Id = user.Id, FirstName = user.FirstName, LastName = user.LastName, Username = user.Username,
                Password = user.Password, IdStage = user.IdStage
            };
            
            Request request = new Request{Type = Request.Types.RequestType.Login, User = userDto};
              
            return request;
        }

        public static Request createLogoutRequest(Networking.UserDTO user)
        {
            Protobuff.UserDTO userDto = new UserDTO {Id = user.Id};

            Request request = new Request{Type = Request.Types.RequestType.Logout, User = userDto};
            
            return request;
        }
        
        public static Request createAddResultRequest(Networking.ResultDTO result)
        {
            Protobuff.ResultDTO resultDto = new ResultDTO
            {
                ParticipantId = result.IdParticipant,
                StageId = result.IdStage,
                Score = result.Score
            };
            
            Request request = new Request{  Type = Request.Types.RequestType.AddResult, Result = resultDto};
            
            return request;
        }

       
        public static Request createGetParticipantsByScoreRequest(int idStage)
        {
            Request request = new Request
            {
                Type = Request.Types.RequestType.GetParticipantsByScore,
                IdStage = idStage
            };
            
            return request;
        }
        
        public static Request createGetParticipantsAndScoreRequest()
        {
            Request request = new Request
            {
                Type = Request.Types.RequestType.GetParticipantsAndScore
            };
            
            return request;
        }
        
        public static Response createOkResponse()
        {
            Response response = new Response{ Type = Response.Types.ResponseType.Ok};
            return response;
        }

        
        public static Response createErrorResponse(String text)
        {
            Response response = new Response{
                Type = Response.Types.ResponseType.Error, Error = text};
            return response;
        }
        
        public static Response createNewResultResponse(Networking.ResultDTO result)
        {
            Protobuff.ResultDTO resultDto = new ResultDTO
            {
                ParticipantId = result.IdParticipant,
                StageId = result.IdStage,
                Score = result.Score
            };

            Response response = new Response { Type = Response.Types.ResponseType.NewResult,Result = resultDto};
            
            return response;
        }


        public static Response createGetParticipantsByScoreResponse(domain.Participant[] participants)
        {
            Response response = new Response { 
                Type = Response.Types.ResponseType.GetParticipantsByScore
            };
            foreach (domain.Participant participant in participants)
            {
                Protobuff.Participant participantP = new Protobuff.Participant
                {
                    FirstName = participant.FirstName,
                    LastName = participant.LastName
                };

                response.Participants.Add(participantP);
            }

            return response;
        }
        
        public static Response createGetParticipantsAndScoreResponse(domain.ParticipantDTO[] participants)
        {
            Response response = new Response { 
                Type = Response.Types.ResponseType.GetParticipantsAndScore
            };
            foreach (domain.ParticipantDTO participantDTO in participants)
            {
                Protobuff.ParticipantDTO participantDTOP = new Protobuff.ParticipantDTO
                {
                    Id = participantDTO.Id,
                    FirstName = participantDTO.FirstName,
                    LastName = participantDTO.LastName,
                    Score = participantDTO.Score
                };

                response.ParticipantsDTO.Add(participantDTOP);
            }

            return response;
        }
        
        public static domain.User getUser(Request request)
        {
            domain.Stage stage = new domain.Stage();
            stage.Id = Convert.ToInt32(request.User.IdStage);

            domain.User user = new domain.User(request.User.FirstName, request.User.LastName, request.User.Username,
                request.User.Password, stage);
                
            return user;
        }
        
        public static domain.Result getResult(Request request)
        {
            domain.Participant participant = new domain.Participant();
            participant.Id = Convert.ToInt32(request.Result.ParticipantId);
            
            domain.Stage stage = new domain.Stage();
            stage.Id = Convert.ToInt32(request.Result.StageId);

            domain.Result result = new domain.Result(participant, stage, request.Result.Score);
            
            return result;
        }

        public static int getIdStage(Request request)
        {
            return Convert.ToInt32(request.IdStage);
        }
    }
}