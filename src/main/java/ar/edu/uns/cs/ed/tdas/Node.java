package ar.edu.uns.cs.ed.tdas;

public class Node<E> {

    protected E element;
    protected Node<E> next;
    
    public Node(E e){
        element = e;
        next = null;
    }

    public E getElement(){
        return element;
    }

    public void setElement( E element ){
        this.element = element;
    }

    public Node<E> getNext(){
        return next;
    }

    public void setNext(Node<E> e){
        next = e;
    }

}
