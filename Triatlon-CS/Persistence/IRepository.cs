using System.Collections.Generic;
using Triatlon_CSharp.Domain;

namespace Triatlon_CSharp.Repository
{
    public interface IRepository<ID, E> where E : Entity<ID>
    {
        E FindOne(ID id);

        IEnumerable<E> FindAll();

        E Save(E entity);

        E Delete(ID id);

        E Update(E entity);
    }
}