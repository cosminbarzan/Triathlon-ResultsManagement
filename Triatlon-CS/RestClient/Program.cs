using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace RestClient
{
    class MainClass
    {
        static HttpClient client = new HttpClient();

        public static void Main(string[] args)
        {
            Console.WriteLine("Hello World!");
            RunAsync().Wait();
        }


        static async Task RunAsync()
        {
            client.BaseAddress = new Uri("http://localhost:8080/triathlon/greeting");
            client.DefaultRequestHeaders.Accept.Clear();
            //client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("text/plain"));
            client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
            // Get the string
            //String text = await GetTextAsync("http://localhost:8080/chat/greeting");
            //Console.WriteLine("am obtinut {0}", text);
            //Get one user
            Console.WriteLine("Get stage {0}", 1);
            Stage result = await GetStageAsync("http://localhost:8080/triathlon/stages/" + 1);
            Console.WriteLine("Am primit {0}", result);
            Console.ReadLine();
        }

        static async Task<String> GetTextAsync(string path)
        {
            String product = null;
            HttpResponseMessage response = await client.GetAsync(path);
            if (response.IsSuccessStatusCode)
            {
                product = await response.Content.ReadAsStringAsync();
            }
            return product;
        }


        static async Task<Stage> GetStageAsync(string path)
        {
            Stage product = null;
            HttpResponseMessage response = await client.GetAsync(path);
            if (response.IsSuccessStatusCode)
            {
                product = await response.Content.ReadAsAsync<Stage>();
            }
            return product;
        }

    }
    public class Stage
    {
        public int Id { get; set; }
        public string Name { get; set; }

        public override string ToString()
        {
            return string.Format("[Stage: Id={0}, Name={1}]", Id, Name);
        }
    }
}