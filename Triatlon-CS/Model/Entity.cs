using System;

namespace Triatlon_CSharp.Domain
{
    [Serializable]
    public class Entity<TID>
    {
        public TID Id { get; set; }
    }
}