using System;
using System.Collections;
using System.Net.Sockets;
using System.Runtime.Remoting;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Tcp;
using System.Runtime.Remoting.Services;
using System.Threading;
using Networking;
using Protobuff;
using Services;
using Triatlon_CSharp.Repository;

namespace Triatlon_CS
{
    class StartServer
    {
        static void Main(string[] args)
        {
            
            ParticipantRepository participantRepo = new ParticipantDBRepository();

            ResultRepository resultRepo = new ResultDBRepository();
    
            StageRepository stageRepo = new StageDBRepository();

            UserRepository userRepo = new UserDBRepository();

            var serviceImpl = new ServiceImpl(userRepo, resultRepo, participantRepo, stageRepo);

            //      VARIANTA 1
            // // IChatServer serviceImpl = new ChatServerImpl();
            // SerialServer server = new SerialServer("127.0.0.1", 55555, serviceImpl);
            // server.Start();
            // Console.WriteLine("Server started ...");
            // //Console.WriteLine("Press <enter> to exit...");
            // Console.ReadLine();
            
            
            //      VARIANTA 2 - .NET Remoting
            // BinaryServerFormatterSinkProvider serverProvider = new BinaryServerFormatterSinkProvider();
            // serverProvider.TypeFilterLevel = System.Runtime.Serialization.Formatters.TypeFilterLevel.Full;
            // BinaryClientFormatterSinkProvider clientProvider = new BinaryClientFormatterSinkProvider();
            //
            // IDictionary properties = new Hashtable();
            // properties["port"] = 55555;
            // TcpChannel tcpChannel = new TcpChannel(properties, clientProvider, serverProvider);
            //
            // ChannelServices.RegisterChannel(tcpChannel, false);
            //
            // RemotingServices.Marshal(serviceImpl, "Triathlon");


            ProtoServer server = new ProtoServer("127.0.0.1", 55556, serviceImpl);
            server.Start();
            
            Console.WriteLine("Server started ...");
            //Console.WriteLine("Press <enter> to exit...");
            Console.ReadLine();
        }
    }

    public class SerialServer: ConcurrentServer 
    {
        private IServices server;
        private ClientWorker worker;
        public SerialServer(string host, int port, IServices server) : base(host, port)
        {
            this.server = server;
            Console.WriteLine("SerialServer...");
        }
        protected override Thread createWorker(TcpClient client)
        {
            worker = new ClientWorker(server, client);
            return new Thread(new ThreadStart(worker.run));
        }
    }
    
    
    public class ProtoServer : ConcurrentServer
    {
        private IServices server;
        private ProtoWorker worker;
        public ProtoServer(string host, int port, IServices server)
            : base(host, port)
        {
            this.server = server;
            Console.WriteLine("ProtoServer...");
        }
        protected override Thread createWorker(TcpClient client)
        {
            worker = new ProtoWorker(server, client);
            return new Thread(new ThreadStart(worker.run));
        }
    }
}