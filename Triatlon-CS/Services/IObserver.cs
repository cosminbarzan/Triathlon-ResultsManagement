using Triatlon_CSharp.Domain;

namespace Services
{
    public interface IObserver
    {
        void resultAdded(Result result);
    }
}