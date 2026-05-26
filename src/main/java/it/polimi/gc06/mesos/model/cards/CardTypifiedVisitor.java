package it.polimi.gc06.mesos.model.cards;

public abstract class CardTypifiedVisitor<T> extends CardVisitor{

    private T result;

    /**
     * This method retrieves the result of the visit operation.
     *
     * @return the resulting object of type T.
     */
    public T getResult() {
        return result;
    }

    /**
     * This method sets the result of the visit operation.
     *
     * @param result the resulting object of type T to be stored.
     */
    public void setResult(T result){
        this.result = result;
    }
}