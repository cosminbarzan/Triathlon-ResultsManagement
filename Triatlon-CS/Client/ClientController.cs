using System;
using System.Collections.Generic;
using System.Windows.Forms;
using Services;
using Triatlon_CSharp.Domain;

namespace Client
{
    public class ClientController:MarshalByRefObject, IObserver
    {
        public event EventHandler<UserEventArgs> updateEvent; //ctrl calls it when it has received an update
        private readonly IServices server;
        private User currentUser;
        
        public ClientController(IServices server)
        {
            this.server = server;
            currentUser = null;
        }

        public User getCurrentUser()
        {
            return currentUser;
        }
        
        public void resultAdded(Result result)
        {
            UserEventArgs userArgs = new UserEventArgs(UserEvent.NewResult, result);
            Console.WriteLine("Result added");
            OnUserEvent(userArgs);
        }

        public void login(string username, string password)
        {
            Stage stage = new Stage();
            Console.WriteLine("aaaaaaaaaaaaaaaaaaa");
            Console.WriteLine("aaaaaaaaaaaaaaaaaaa");
            Console.WriteLine("aaaaaaaaaaaaaaaaaaa");
            currentUser = server.findUserByUsername(new User("", "", username, password, stage));
            // User user = null;
            // if (username == "vladpopescu")
            // {
            //     stage.Id = 2;
            //     user = new User("", "", username, password, stage);
            //     user.Id = 1;
            // }
            //
            // if (username == "sergiudavid")
            // {
            //     stage.Id = 1;
            //     user = new User("", "", username, password, stage);
            //     user.Id = 2;
            // }
            //
            // if (username == "andreiionescu")
            // {
            //     stage.Id = 3;
            //     user = new User("", "", username, password, stage);
            //     user.Id = 3;
            // }
            //
            // currentUser = user;
            currentUser = server.login(currentUser, this);
            // stage.Id = 2;
        }

        public IList<Participant> getParticipantsByScore(int idStage)
        {
            IList<Participant> participants = new List<Participant>();

            participants = server.getParticipantsByScore(idStage);

            return participants;
        }

        public IList<ParticipantDTO> getParticipantsAndScore()
        {
            IList<ParticipantDTO> participantsDTO = new List<ParticipantDTO>();

            participantsDTO = server.getParticipantsAndScore();

            return participantsDTO;
        }

        protected virtual void OnUserEvent(UserEventArgs e)
        {
            if (updateEvent == null) return;
            updateEvent(this, e);
            Console.WriteLine("Update Event called");
        }
        
        public void addResult(double score, int idParticipant)
        {
            Participant participant = server.findParticipant(idParticipant);

            Stage stage = currentUser.Stage;

            Result result = new Result(participant, stage, score);
            
            UserEventArgs userArgs = new UserEventArgs(UserEvent.NewResult, result);
            OnUserEvent(userArgs);

            server.addResult(result);
        }

        // public void login(String userId, String pass)
        // {
        //     User user=new User(userId,pass);
        //     server.login(user,this);
        //     Console.WriteLine("Login succeeded ....");
        //     currentUser = user;
        //     Console.WriteLine("Current user {0}", user);
        // }
        // public void messageReceived(Message message)
        // {
        //     String mess = "[" + message.Sender.Id + "]: " + message.Text;
        //     ChatUserEventArgs userArgs=new ChatUserEventArgs(ChatUserEvent.NewMessage,mess);
        //     Console.WriteLine("Message received");
        //     OnUserEvent(userArgs);
        // }
        //
        // public void friendLoggedIn(User friend)
        // {
        //     Console.WriteLine("Friend logged in "+friend);
        //     ChatUserEventArgs userArgs = new ChatUserEventArgs(ChatUserEvent.FriendLoggedIn, friend.Id);
        //     OnUserEvent(userArgs);
        // }
        //
        // public void friendLoggedOut(User friend)
        // {
        //     Console.WriteLine("Friend logged out"+friend);
        //     ChatUserEventArgs userArgs = new ChatUserEventArgs(ChatUserEvent.FriendLoggedOut, friend.Id);
        //     OnUserEvent(userArgs);
        // }
        //
        // public void logout()
        // {
        //     Console.WriteLine("Ctrl logout");
        //     server.logout(currentUser, this);
        //     currentUser = null;
        // }
        //
        // protected virtual void OnUserEvent(ChatUserEventArgs e)
        // {
        //     if (updateEvent == null) return;
        //     updateEvent(this, e);
        //     Console.WriteLine("Update Event called");
        // }
        // public IList<String> getLoggedFriends()
        // {
        //     IList<String> loggedFriends=new List<string>();
        //     User[] friends = server.getLoggedFriends(currentUser);
        //     foreach (var user in friends)
        //     {
        //         loggedFriends.Add(user.Id);
        //     }
        //     return loggedFriends;
        // }
        //
        // public void sendMessage(string id, string txt)
        // {
        //     //display the sent message on the user window
        //     String mess = "[" + currentUser.Id + "-->"+id+"]: " + txt;
        //     ChatUserEventArgs userArgs = new ChatUserEventArgs(ChatUserEvent.NewMessage, mess);
        //     OnUserEvent(userArgs);
        //     //sends the message to the server
        //     User receiver=new User(id);
        //     Message message=new Message(currentUser,receiver, txt);
        //     server.sendMessage(message);
        // }
    }
}