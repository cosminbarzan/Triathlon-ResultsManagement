using System;
using Triatlon_CSharp.Domain;

namespace Triatlon_CSharp.Repository
{
    public interface UserRepository : IRepository<int, User>
    {
        int verifyAccount(String username, String password);

        User FindByUsername(string username);
    }
}