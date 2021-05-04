using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Threading;
using Services;
using Triatlon_CSharp.Domain;

namespace Networking
{
    public class ServerProxy : IServices
	{
		private string host;
		private int port;

		private IObserver client;

		private NetworkStream stream;
		
        private IFormatter formatter;
		private TcpClient connection;

		private Queue<Response> responses;
		private volatile bool finished;
        private EventWaitHandle _waitHandle;
        
		public ServerProxy(string host, int port)
		{
			this.host = host;
			this.port = port;
			responses = new Queue<Response>();
		}

		public virtual User login(User user, IObserver client)
		{
			initializeConnection();
			UserDTO udto = DTOUtils.getDTO(user);
			sendRequest(new LoginRequest(udto));
			Response response = readResponse();
			
			if (response is OkResponse)
			{
				this.client = client;
				OkResponse resp = (OkResponse)response;
				User user1 = DTOUtils.getFromDTO(resp.User);
				return user1;
			}
			if (response is ErrorResponse)
			{
				ErrorResponse err = (ErrorResponse)response;
				closeConnection();
				throw new TriatlonException(err.Message);
			}

			return null;
		}

		public virtual void addResult(Result result)
		{
			ResultDTO rdto = DTOUtils.getDTO(result);
			sendRequest(new AddResultRequest(rdto)); 
			
			Response response = readResponse();
			
			if (response is ErrorResponse)
			{
				ErrorResponse err = (ErrorResponse)response;
				throw new TriatlonException(err.Message);
			}
		}

		public virtual void logout(User user, IObserver client)
		{
			UserDTO udto = DTOUtils.getDTO(user);
			sendRequest(new LogoutRequest(udto));
			
			Response response = readResponse();
			closeConnection();
			
			if (response is ErrorResponse)
			{
				ErrorResponse err = (ErrorResponse)response;
				throw new TriatlonException(err.Message);
			}
		}

		
		public virtual Participant[] getParticipantsByScore(int idStage)
		{
			sendRequest(new GetParticipantsByScoreRequest(idStage));
			
			Response response = readResponse();
			
			if (response is ErrorResponse)
			{
				ErrorResponse err = (ErrorResponse)response;
				throw new TriatlonException(err.Message);
			}
			
			GetParticipantsByScoreResponse resp = (GetParticipantsByScoreResponse)response;
			Participant[] participants = resp.Participants;
			return participants;
		}
		
		public virtual ParticipantDTO[] getParticipantsAndScore()
		{
			sendRequest(new GetParticipantsAndScoreRequest());
			
			Response response = readResponse();
			
			if (response is ErrorResponse)
			{
				ErrorResponse err = (ErrorResponse)response;
				throw new TriatlonException(err.Message);
			}
			
			GetParticipantsAndScoreResponse resp = (GetParticipantsAndScoreResponse)response;
			ParticipantDTO[] participants = resp.Participants;
			return participants;
		}

		public Participant findParticipant(int id)
		{
			sendRequest(new FindParticipantRequest(id));
			
			Response response = readResponse();
			
			if (response is ErrorResponse)
			{
				ErrorResponse err = (ErrorResponse)response;
				throw new TriatlonException(err.Message);
			}
			
			FindParticipantResponse resp = (FindParticipantResponse)response;
			Participant participant = resp.Participant;
			return participant;
		}

		public User findUser(int id)
		{
			sendRequest(new FindUserRequest(id));
			
			Response response = readResponse();
			
			if (response is ErrorResponse)
			{
				ErrorResponse err = (ErrorResponse)response;
				throw new TriatlonException(err.Message);
			}
			
			FindUserResponse resp = (FindUserResponse)response;
			User user = resp.User;
			return user;
		}

		public User findUserByUsername(User user)
		{
			UserDTO udto = DTOUtils.getDTO(user);
			//sendRequest(new LoginRequest(udto));
			
			sendRequest(new FindUserByUsernameRequest(udto));
			
			Response response = readResponse();
			
			if (response is ErrorResponse)
			{
				ErrorResponse err = (ErrorResponse)response;
				throw new TriatlonException(err.Message);
			}
			
			FindUserByUsernameResponse resp = (FindUserByUsernameResponse)response;
			User user1 = resp.User;
			return user1;
		}

		private void closeConnection()
		{
			finished = true;
			try
			{
				stream.Close();
				//output.close();
				connection.Close();
                _waitHandle.Close();
				client = null;
			}
			catch (Exception e)
			{
				Console.WriteLine(e.StackTrace);
			}

		}

		private void sendRequest(Request request)
		{
			try
			{
                formatter.Serialize(stream, request);
                stream.Flush();
			}
			catch (Exception e)
			{
				throw new TriatlonException("Error sending object "+e);
			}

		}

		private Response readResponse()
		{
			Response response = null;
			try
			{
                _waitHandle.WaitOne();
				lock (responses)
				{
                    //Monitor.Wait(responses); 
                    response = responses.Dequeue();
                
				}
				
			}
			catch (Exception e)
			{
				Console.WriteLine(e.StackTrace);
			}
			return response;
		}
		private void initializeConnection()
		{
			 try
			 {
				connection = new TcpClient(host,port);
				stream = connection.GetStream();
                formatter = new BinaryFormatter();
				finished = false;
                _waitHandle = new AutoResetEvent(false);
				startReader();
			 }
			 catch (Exception e)
			 {
                Console.WriteLine(e.StackTrace);
			 }
		}
		private void startReader()
		{
			Thread tw = new Thread(run);
			tw.Start();
		}


		private void handleUpdate(UpdateResponse update)
		{
			if (update is NewResultResponse)
			{
				NewResultResponse resultRes = (NewResultResponse)update;
				Result result = DTOUtils.getFromDTO(resultRes.Result);
				try
				{
					client.resultAdded(result);
				}
				catch (TriatlonException e)
				{
                    Console.WriteLine(e.StackTrace);
				}
			}
		}
		public virtual void run()
			{
				while(!finished)
				{
					try
					{
                        object response = formatter.Deserialize(stream);
						Console.WriteLine("response received " + response);
						if (response is UpdateResponse)
						{
							 handleUpdate((UpdateResponse)response);
						}
						else
						{
							
							lock (responses)
							{
								responses.Enqueue((Response)response);
                               
							}
                            _waitHandle.Set();
						}
					}
					catch (Exception e)
					{
						Console.WriteLine("Reading error " + e);
					}
					
				}
			}
	}
}