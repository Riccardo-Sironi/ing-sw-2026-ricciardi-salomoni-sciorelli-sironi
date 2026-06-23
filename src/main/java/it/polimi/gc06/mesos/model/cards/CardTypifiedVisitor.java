package it.polimi.gc06.mesos.model.cards;

/**
 * A generic variant of the CardVisitor that can store and return a specific result of type T
 * after visiting a card.
 *
 * @param <T> The type of the result produced by the visitor.
 */
public abstract class CardTypifiedVisitor<T> extends CardVisitor{

    private T result;

    /**
     * This method retrieves the result of the visit operation.
     *
     * @return The resulting object of type T.
     */
    public T getResult() {
        return result;
    }

    /**
     * This method sets the result of the visit operation.
     *
     * @param result The resulting object of type T to be stored.
     */
    public void setResult(T result){
        this.result = result;
    }
}