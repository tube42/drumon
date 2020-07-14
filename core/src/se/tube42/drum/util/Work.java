
package se.tube42.drum.util;


// Work is a job for future, it may be called with the given input or fail
// This is mainly used to abstract away the Android async stuff
public interface Work<T>
{
    void success(T t);
    void failure(String msg);
}
