using System;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Threading;
using Services;
using Triatlon_CSharp.Domain;

namespace Networking
{
    public class ClientWorker :  IObserver
	{
		private IServices server;
		private TcpClient connection;

		private NetworkStream stream;
		private IFormatter formatter;
		private volatile bool connected;
		public ClientWorker(IServices server, TcpClient connection)
		{
			this.server = server;
			this.connection = connection;
			try
			{
				
				stream = connection.GetStream();
                formatter = new BinaryFormatter();
				connected = true;
			}
			catch (Exception e)
			{
                Console.WriteLine(e.StackTrace);
			}
		}

		public virtual void run()
		{
			while(connected)
			{
				try
				{
                    object request = formatter.Deserialize(stream);
					object response = handleRequest((Request)request);
					if (response != null)
					{
					   sendResponse((Response) response);
					}
				}
				catch (Exception e)
				{
                    Console.WriteLine(e.StackTrace);
				}
				
				try
				{
					Thread.Sleep(1000);
				}
				catch (Exception e)
				{
                    Console.WriteLine(e.StackTrace);
				}
			}
			try
			{
				stream.Close();
				connection.Close();
			}
			catch (Exception e)
			{
				Console.WriteLine("Error " + e);
			}
		}
		
		public virtual void resultAdded(Result result)
		{
			ResultDTO resultDto = DTOUtils.getDTO(result);
			Console.WriteLine("Result added  " + result);
			try
			{
				sendResponse(new NewResultResponse(resultDto));
			}
			catch (Exception e)
			{
				throw new TriatlonException("Sending error: " + e);
			}
		}

		private Response handleRequest(Request request)
		{
			Response response = null;
			
			if (request is LoginRequest)
			{
				Console.WriteLine("Login request ...");
				LoginRequest logReq = (LoginRequest)request;
				
				UserDTO udto = logReq.User;
				User user = DTOUtils.getFromDTO(udto);

				User loggedUser = null;
				UserDTO userDto = null;
				try
                {
                    lock (server)
                    {
                        loggedUser = server.login(user, this);
                        userDto = DTOUtils.getDTO(loggedUser);
                    }
					return new OkResponse(userDto);
				}
				catch (TriatlonException e)
				{
					connected = false;
					return new ErrorResponse(e.Message);
				}
			}
			if (request is LogoutRequest)
			{
				Console.WriteLine("Logout request");
				LogoutRequest logReq = (LogoutRequest)request;
				
				UserDTO udto = logReq.User;
				User user = DTOUtils.getFromDTO(udto);
				
				try
				{
                    lock (server)
                    {
	                    server.logout(user, this);
                    }
					connected = false;
					return new OkResponse();

				}
				catch (TriatlonException e)
				{
				   return new ErrorResponse(e.Message);
				}
			}
			if (request is AddResultRequest)
			{
				Console.WriteLine("SendMessageRequest ...");
				AddResultRequest addReq = (AddResultRequest)request;
				
				ResultDTO resultDto = addReq.Result;
				Result result = DTOUtils.getFromDTO(resultDto);
				
				try
				{
                    lock (server)
                    {
                        server.addResult(result);
                    }
					return new OkResponse();
				}
				catch (TriatlonException e)
				{
					return new ErrorResponse(e.Message);
				}
			}

			if (request is GetParticipantsByScoreRequest)
			{
				Console.WriteLine("GetParticipantsByScore Request ...");
				GetParticipantsByScoreRequest getReq = (GetParticipantsByScoreRequest)request;
				
				int idStage =getReq.IdStage;
				
				try
				{
					Participant[] participants;
                    lock (server)
                    {
	                    participants = server.getParticipantsByScore(idStage);
                    }
					// UserDTO[] frDTO =DTOUtils.getDTO(friends);
					return new GetParticipantsByScoreResponse(participants);
				}
				catch (TriatlonException e)
				{
					return new ErrorResponse(e.Message);
				}
			}
			
			if (request is GetParticipantsAndScoreRequest)
			{
				Console.WriteLine("GetParticipantsAndScore Request ...");
				GetParticipantsAndScoreRequest getReq = (GetParticipantsAndScoreRequest)request;
				
				try
				{
					ParticipantDTO[] participants;
					lock (server)
					{
						participants = server.getParticipantsAndScore();
					}
					// UserDTO[] frDTO =DTOUtils.getDTO(friends);
					return new GetParticipantsAndScoreResponse(participants);
				}
				catch (TriatlonException e)
				{
					return new ErrorResponse(e.Message);
				}
			}
			
			if (request is FindParticipantRequest)
			{
				Console.WriteLine("FindParticipant Request ...");
				FindParticipantRequest getReq = (FindParticipantRequest)request;
				
				int id = getReq.Id;
				
				try
				{
					Participant participant;
					lock (server)
					{
						participant = server.findParticipant(id);
					}
					return new FindParticipantResponse(participant);
				}
				catch (TriatlonException e)
				{
					return new ErrorResponse(e.Message);
				}
			}
			
			if (request is FindUserRequest)
			{
				Console.WriteLine("FindUser Request ...");
				FindUserRequest getReq = (FindUserRequest)request;
				
				int id = getReq.Id;
				
				try
				{
					User user;
					lock (server)
					{
						user = server.findUser(id);
					}
					return new FindUserResponse(user);
				}
				catch (TriatlonException e)
				{
					return new ErrorResponse(e.Message);
				}
			}
			
			if (request is FindUserByUsernameRequest)
			{
				Console.WriteLine("FindUser Request ...");
				FindUserByUsernameRequest getReq = (FindUserByUsernameRequest)request;

				UserDTO udto = getReq.User;
				User user = DTOUtils.getFromDTO(udto);
				
				try
				{
					User user1;
					lock (server)
					{
						user1 = server.findUserByUsername(user);
					}
					return new FindUserByUsernameResponse(user1);
				}
				catch (TriatlonException e)
				{
					return new ErrorResponse(e.Message);
				}
			}
			
			return response;
		}

		private void sendResponse(Response response)
		{
			Console.WriteLine("sending response "+response);
            formatter.Serialize(stream, response);
            stream.Flush();
			
		}
	}
}