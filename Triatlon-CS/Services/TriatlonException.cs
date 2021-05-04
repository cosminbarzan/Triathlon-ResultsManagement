using System;

namespace Services
{
    public class TriatlonException : Exception
    {
        public TriatlonException():base() { }

        public TriatlonException(String msg) : base(msg) { }

        public TriatlonException(String msg, Exception ex) : base(msg, ex) { }

    }
}