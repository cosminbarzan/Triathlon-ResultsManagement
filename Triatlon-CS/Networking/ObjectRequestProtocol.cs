using System;

namespace Networking
{
    // using UserDTO = chat.network.dto.UserDTO;
    // using MessageDTO = chat.network.dto.MessageDTO;


    public interface Request 
    {
    }


    [Serializable]
    public class LoginRequest : Request
    {
        private UserDTO user;

        public LoginRequest(UserDTO user)
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
    public class LogoutRequest : Request
    {
        private UserDTO user;

        public LogoutRequest(UserDTO user)
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
    public class AddResultRequest : Request
    {
        private ResultDTO result;

        public AddResultRequest(ResultDTO result)
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

    [Serializable]
    public class GetParticipantsByScoreRequest : Request
    {
        private int idStage;

        public GetParticipantsByScoreRequest(int idStage)
        {
            this.idStage = idStage;
        }

        public virtual int IdStage
        {
            get
            {
                return idStage;
            }
        }
    }
    
    [Serializable]
    public class GetParticipantsAndScoreRequest : Request
    {

        public GetParticipantsAndScoreRequest()
        {
        }
    }
    
    
    [Serializable]
    public class FindParticipantRequest : Request
    {
        private int id;

        public FindParticipantRequest(int id)
        {
            this.id = id;
        }

        public virtual int Id
        {
            get
            {
                return id;
            }
        }
    }
    
    [Serializable]
    public class FindUserRequest : Request
    {
        private int id;

        public FindUserRequest(int id)
        {
            this.id = id;
        }

        public virtual int Id
        {
            get
            {
                return id;
            }
        }
    }
    
    [Serializable]
    public class FindUserByUsernameRequest : Request
    {
        private UserDTO user;

        public FindUserByUsernameRequest(UserDTO user)
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
}