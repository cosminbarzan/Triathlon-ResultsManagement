using System;

namespace Triatlon_CSharp.Domain
{
    [Serializable]
    public class Stage : Entity<int>
    {
        public Stage() {}
        
        public Stage(string name)
        {
            Name = name;
        }

        public String Name { get; set; }

        public override string ToString()
        {
            return string.Format("Id = {0} | {1}", Id, Name);
        }
    }
}