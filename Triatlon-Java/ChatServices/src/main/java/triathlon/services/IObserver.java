package triathlon.services;

import triathlon.model.Result;


public interface IObserver {
    void resultAdded(Result result) throws TriatlonException;

}