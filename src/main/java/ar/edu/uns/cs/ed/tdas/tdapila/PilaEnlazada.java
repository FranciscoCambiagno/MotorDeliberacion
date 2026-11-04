package ar.edu.uns.cs.ed.tdas.tdapila;

import ar.edu.uns.cs.ed.tdas.Node;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyStackException;

public class PilaEnlazada<E> implements Stack<E>{

	private Node<E> front;
	private int tamanio;
	
	public PilaEnlazada() {
		front = null;
		tamanio = 0;
	}
	
	public void push(E e){
		Node<E> nuevoNodo = new Node<E>(e);
		
		nuevoNodo.setNext(front);
		front = nuevoNodo;
		tamanio++;
	}

	public E pop(){
		E element = null;
		
		if(isEmpty()) {
			throw new EmptyStackException("La pila esta vacia.");
		}else {
			element = front.getElement();
			front = front.getNext();
			tamanio--;
		}

		return element;
	}

	public boolean isEmpty(){
		return front == null;
	}

	public E top(){
		E element;
		
		if(isEmpty()) {
			throw new EmptyStackException("La pila esta vacia.");
		}else {
			element = front.getElement();
		}
		
		return element;
	}

	public int size(){
		return tamanio;
	}

}
