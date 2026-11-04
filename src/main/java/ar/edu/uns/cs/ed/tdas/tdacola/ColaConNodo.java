package ar.edu.uns.cs.ed.tdas.tdacola;

import ar.edu.uns.cs.ed.tdas.Node;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyQueueException;

public class ColaConNodo<E> implements Queue<E>{
	protected Node<E> frente;
	protected Node<E> fondo;
	protected int cantidad;
	
	
	public ColaConNodo() {
		frente = null;
		fondo = null;
		cantidad = 0;
	}


	@Override
	public int size() {
		return cantidad;
	}


	@Override
	public boolean isEmpty() {
		return cantidad == 0;
	}


	@Override
	public E front() {
		E element = null;
		
		if (frente == null) {
			throw new EmptyQueueException("La cola esta vacia.");
		}else {
			element = frente.getElement();
		}
		
		return element;
	}


	@Override
	public void enqueue(E element) {
		Node<E> nuevoNodo = new Node<E>(element);
		
		if(cantidad == 0) {
			frente = nuevoNodo;
			fondo = nuevoNodo;
		}else {
			fondo.setNext(nuevoNodo);
			fondo = nuevoNodo;
		}
		
		cantidad++;
		
	}


	@Override
	public E dequeue() {
		E element = null;
		
		if(cantidad == 0) {
			throw new EmptyQueueException("La cola esta vacia.");
		}else {
			element = frente.getElement();
			frente = frente.getNext();
			cantidad--;
		}
		
		return element;
	}
}
