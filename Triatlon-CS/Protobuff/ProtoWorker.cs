using System;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Threading;
using Google.Protobuf;
using Networking;
using Services;
using Triatlon_CSharp.Domain;

namespace Protobuff
{
    public class ProtoWorker :  IObserver
    {
		private IServices server;
		private TcpClient connection;

		private NetworkStream stream;
		private volatile bool connected;
		
		public ProtoWorker(IServices server, TcpClient connection)
		{
			this.server = server;
			this.connection = connection;
			try
			{
				
				stream = connection.GetStream();
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
					Request request = Request.Parser.ParseDelimitedFrom(stream);
					Response response = handleRequest(request);
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
			Networking.ResultDTO resultDto = DTOUtils.getDTO(result);
			Console.WriteLine("Result added  " + result);
			try
			{
				sendResponse(ProtoUtils.createNewResultResponse(resultDto));
			}
			catch (Exception e)
			{
				throw new TriatlonException("Sending error: " + e);
			}
		}

		private Response handleRequest(Request request)
		{
			Response response = null;
			Request.Types.RequestType reqType = request.Type;

			switch (reqType)
			{
				case Request.Types.RequestType.Login:
				{
					Console.WriteLine("Login request ...");
					User user = ProtoUtils.getUser(request);
					try
					{
						lock (server)
						{
							server.login(user, this);
						}

						return ProtoUtils.createOkResponse();
					}
					catch (TriatlonException e)
					{
						connected = false;
						return ProtoUtils.createErrorResponse(e.Message);
					}
				}

				case Request.Types.RequestType.Logout:
				{
					Console.WriteLine("Logout request ...");
					User user = ProtoUtils.getUser(request);
					try
					{
						lock (server)
						{

							server.logout(user, this);
						}

						connected = false;
						return ProtoUtils.createOkResponse();

					}
					catch (TriatlonException e)
					{
						return ProtoUtils.createErrorResponse(e.Message);
					}
				}

				case Request.Types.RequestType.AddResult:
				{
					Console.WriteLine("AddResult Request ...");
					Result result = ProtoUtils.getResult(request);
					try
					{
						lock (server)
						{
							server.addResult(result);
						}

						return ProtoUtils.createOkResponse();
					}
					catch (TriatlonException e)
					{
						return ProtoUtils.createErrorResponse(e.Message);
					}
				}

				case Request.Types.RequestType.GetParticipantsByScore:
				{
					Console.WriteLine("GetParticipantsByScore Request ...");
					int idStage = ProtoUtils.getIdStage(request);
					try
					{
						Triatlon_CSharp.Domain.Participant[] participants;
						lock (server)
						{

							participants = server.getParticipantsByScore(idStage);
						}

						return ProtoUtils.createGetParticipantsByScoreResponse(participants);
					}
					catch (TriatlonException e)
					{
						return ProtoUtils.createErrorResponse(e.Message);
					}
				}

				case Request.Types.RequestType.GetParticipantsAndScore:
				{
					Console.WriteLine("GetParticipantsAndScore Request ...");
					try
					{
						Triatlon_CSharp.Domain.ParticipantDTO[] participants;
						lock (server)
						{

							participants = server.getParticipantsAndScore();
						}

						return ProtoUtils.createGetParticipantsAndScoreResponse(participants);
					}
					catch (TriatlonException e)
					{
						return ProtoUtils.createErrorResponse(e.Message);
					}
				}
			}

			return response;
		}

		
		private void sendResponse(Response response)
		{
			Console.WriteLine("sending response " + response);
			lock (stream)
			{
				response.WriteDelimitedTo(stream);
				stream.Flush();
			}
			
		}
	}
}