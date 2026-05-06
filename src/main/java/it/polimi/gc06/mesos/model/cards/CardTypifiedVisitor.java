package it.polimi.gc06.mesos.model.cards;

public abstract class CardTypifiedVisitor<T> extends CardVisitor{

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result){
        this.result = result;
    }
}