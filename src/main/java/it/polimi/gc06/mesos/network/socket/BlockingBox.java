package it.polimi.gc06.mesos.network.socket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/** A thread-safe and resettable class to handle {@link CompletableFuture} in thread environment*/
public class BlockingBox<T> {

    private volatile CompletableFuture<T> future;

    public BlockingBox(){
        future = new CompletableFuture<>();
    }

    /**
     * Store a value if possible.
     *
     * @param value the value that needs to be stored.
     * @return true if the box was empty.
     * @throws IllegalArgumentException if value is null
     */
    public synchronized boolean store(T value) throws IllegalArgumentException{
        if(value == null) throw new IllegalArgumentException("Value cannot be null.");
        return future.complete(value);
    }

    /**
     * Stores an exception that will be thrown when it is accessed.
     *
     * @param e the e that needs to be stored.
     * @return true if the box was empty.
     * @throws IllegalArgumentException if e is null.
     */
    public synchronized boolean storeException(Exception e){
        if(e == null) throw new IllegalArgumentException("Exception cannot be null.");
        return future.completeExceptionally(e);
    }

    /**
     * Waits until the value is given and the return it.
     *
     * @return the value stored.
     * @throws CompletionException if the value was an exception.
     */
    public T look() throws CompletionException{
        return future.join();
    }

    /**
     * Corresponds to look() + empty() calls.
     *
     * @return the value stored.
     * @throws CompletionException if the value was an exception.
     */
    public T take() throws CompletionException{
        try {
            return look();
        }
        finally {
            empty();
        }
    }

    /**
     * Discards the value stored in the box.
     *
     * @return true if a value was stored.
     */
    public synchronized boolean empty(){
        boolean r = future.isDone();
        future = new CompletableFuture<>();
        return r;
    }

    /**
     * If the box is empty.
     *
     * @return false if the box has a value stored.
     */
    public synchronized boolean isEmpty(){
        return !future.isDone();
    }

}
