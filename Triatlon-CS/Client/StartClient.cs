using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Tcp;
using System.Threading.Tasks;
using System.Windows.Forms;
using Networking;
using Services;

namespace Client
{
    static class StartClient
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            BinaryServerFormatterSinkProvider serverProvider = new BinaryServerFormatterSinkProvider();
            serverProvider.TypeFilterLevel = System.Runtime.Serialization.Formatters.TypeFilterLevel.Full;
            BinaryClientFormatterSinkProvider clientProvider = new BinaryClientFormatterSinkProvider();

            IDictionary properties = new Hashtable();
            properties["port"] = 0;
            TcpChannel tcpChannel = new TcpChannel(properties, clientProvider, serverProvider);
            ChannelServices.RegisterChannel(tcpChannel, false);

            IServices server = (IServices) Activator.GetObject(typeof(IServices), "tcp://localhost:55555/Triathlon");
            //IServices server = new ServerProxy("127.0.0.1", 55555);
            ClientController ctrl = new ClientController(server);
            LoginWindow window = new LoginWindow(ctrl);
            Application.Run(window);
        }
    }
}