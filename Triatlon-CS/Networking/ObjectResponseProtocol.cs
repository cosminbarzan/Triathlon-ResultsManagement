using System;
using Triatlon_CSharp.Domain;

namespace Networking
{
    public interface Response 
    {
    }

    [Serializable]
    public class OkResponse : Response
    {
        private UserDTO user;

        public OkResponse() {}
        
        public OkResponse(UserDTO user)
        {
            this.user = user;
        }

        public virtual UserDTO User
        {
            get
            {
                return user;
            }
        }
    }

    [Serializable]
    public class ErrorResponse : Response
    {
        private string message;

        public ErrorResponse(string message)
        {
            this.message = message;
        }

        public virtual string Message
        {
            get
            {
                return message;
            }
        }
    }

    [Serializable]
    public class GetParticipantsByScoreResponse : Response
    {
        private Participant[] participants;

        public GetParticipantsByScoreResponse(Participant[] participants)
        {
            this.participants = participants;
        }

        public virtual Participant[] Participants
        {
            get
            {
                return participants;
            }
        }
    }
    
    [Serializable]
    public class GetParticipantsAndScoreResponse : Response
    {
        private ParticipantDTO[] participants;

        public GetParticipantsAndScoreResponse(ParticipantDTO[] participants)
        {
            this.participants = participants;
        }

        public virtual ParticipantDTO[] Participants
        {
            get
            {
                return participants;
            }
        }
    }
    
    [Serializable]
    public class FindParticipantResponse : Response
    {
        private Participant participant;

        public FindParticipantResponse(Participant participant)
        {
            this.participant = participant;
        }

        public virtual Participant Participant
        {
            get
            {
                return participant;
            }
        }
    }
    
    [Serializable]
    public class FindUserResponse : Response
    {
        private User user;

        public FindUserResponse(User user)
        {
            this.user = user;
        }

        public virtual User User
        {
            get
            {
                return user;
            }
        }
    }
    
    [Serializable]
    public class FindUserByUsernameResponse : Response
    {
        private User user;

        public FindUserByUsernameResponse(User user)
        {
            this.user = user;
        }

        public virtual User User
        {
            get
            {
                return user;
            }
        }
    }
    
    public interface UpdateResponse : Response
    {
    }
    
    
    [Serializable]
    public class NewResultResponse : UpdateResponse
    {
        private ResultDTO result;

        public NewResultResponse(ResultDTO result)
        {
            this.result = result;
        }

        public virtual ResultDTO Result
        {
            get
            {
                return result;
            }
        }
    }
}