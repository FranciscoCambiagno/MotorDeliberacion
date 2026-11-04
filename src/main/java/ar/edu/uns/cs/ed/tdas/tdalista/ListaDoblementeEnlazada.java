package ar.edu.uns.cs.ed.tdas.tdalista;

import java.util.Iterator;

import ar.edu.uns.cs.ed.tdas.DNodo;
import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.BoundaryViolationException;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyListException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;

public class ListaDoblementeEnlazada<E> implements PositionList<E>{
	
	private DNodo<E> head;
	private DNodo<E> tail;
	private int cant;
	
	public ListaDoblementeEnlazada() {
		head = new DNodo<E>(null);
		tail = new DNodo<E>(null);
		cant = 0;
		head.setNext(tail);;
		tail.setPrev(head);
		
	}

	@Override
	public int size() {
		return cant;
	}

	@Override
	public boolean isEmpty() {
		return cant == 0;
	}

	@Override
	public Position<E> first() {
		Position<E> elem = null;
		
		if(isEmpty()) {
			throw new EmptyListException("La lista esta vacia.");
		}else {
			elem = head.getNext();
		}
		
		return elem;
	}

	@Override
	public Position<E> last() {
		Position<E> elem = null;
		
		if(isEmpty()) {
			throw new EmptyListException("La lista esta vacia.");
		}else {
			elem = tail.getPrev();
		}
		
		return elem;
	}

	@Override
	public Position<E> next(Position<E> p) {
		
		DNodo<E> nodoP = checkPosition(p);
		
		if(nodoP == tail.getPrev()) {
			throw new BoundaryViolationException("No se puede solicitar la posicion siguiente al ultimo elemento.");
		}		
		
		return nodoP.getNext();
	}

	@Override
	public Position<E> prev(Position<E> p) {
		DNodo<E> nodoP = checkPosition(p);
		
		if(nodoP == head.getNext()) {
			throw new BoundaryViolationException("No se puede solicitar la posicion anterior al primer elemento.");
		}		
		
		return nodoP.getPrev();
	}

	@Override
	public void addFirst(E element) {
		DNodo<E> dn = new DNodo<E>(element);
		DNodo<E> nodoSiguiente = head.getNext();
		
		nodoSiguiente.setPrev(dn);
		dn.setNext(nodoSiguiente);
		
		head.setNext(dn);
		dn.setPrev(head);
		cant++;
	}

	@Override
	public void addLast(E element) {
		DNodo<E> dn = new DNodo<E>(element);
		DNodo<E> nodoAnterior = tail.getPrev();
		
		nodoAnterior.setNext(dn);
		dn.setPrev(nodoAnterior);
		
		tail.setPrev(dn);
		dn.setNext(tail);
		cant++;
	}

	@Override
	public void addAfter(Position<E> p, E element) {
		DNodo<E> nodoP, nodoSiguiente, nuevoNodo;
		
		nodoP = checkPosition(p);
		
		nuevoNodo = new DNodo<E>(element);		
		nodoSiguiente = nodoP.getNext();
		
		nodoSiguiente.setPrev(nuevoNodo);
		nuevoNodo.setNext(nodoSiguiente);
		
		nuevoNodo.setPrev(nodoP);
		nodoP.setNext(nuevoNodo);
		cant++;
	}

	@Override
	public void addBefore(Position<E> p, E element) {
		DNodo<E> nodoP, nodoAnterior, nuevoNodo;
		
		nodoP = checkPosition(p);
		
		nuevoNodo = new DNodo<E>(element);		
		nodoAnterior = nodoP.getPrev();
		
		nodoAnterior.setNext(nuevoNodo);
		nuevoNodo.setPrev(nodoAnterior);
		
		nuevoNodo.setNext(nodoP);
		nodoP.setPrev(nuevoNodo);
		cant++;
	}

	@Override
	public E remove(Position<E> p) {
		DNodo<E> nodoP, nodoSiguiente, nodoAnterior;
		E elem = null;
		
		nodoP = checkPosition(p);
		
		elem = nodoP.element();
		nodoAnterior = nodoP.getPrev();
		nodoSiguiente = nodoP.getNext();
		
		nodoAnterior.setNext(nodoSiguiente);
		nodoSiguiente.setPrev(nodoAnterior);
		cant--;
		
		return elem;
	}

	@Override
	public E set(Position<E> p, E element) {
		E elemAnterior = null;
		DNodo<E> nodoP = checkPosition(p);
		
		elemAnterior = nodoP.element();
		nodoP.setElement(element);
		
		return elemAnterior;
	}
	
	private DNodo<E> checkPosition(Position<E> p){
		DNodo<E> nodoP;
		
		try {
			nodoP = (DNodo<E>) p;
		}catch( ClassCastException e) {
			throw new InvalidPositionException("Posicion invalida.");
		}
		
		if( p == null) {
			throw new InvalidPositionException("Posicion invalida.");
		}
		
		if(isEmpty()) {
			throw new InvalidPositionException("Lista vacia.");			
		}
		
		return nodoP;
	}
	
	
	//-------------
	//----ITERADOR
	//-------------

	@Override
	public Iterator<E> iterator() {
		return new ElementIterator<E>(this);
	}

	@Override
	public Iterable<Position<E>> positions() {
		PositionList<Position<E>> pl = new ListaDoblementeEnlazada<Position<E>>();
		if(cant != 0) {
			DNodo<E> n = head.getNext();
			while(n != tail) {
				pl.addLast(n);
				n = n.getNext();
			}
		}
		
		return pl;
	}
	
	
}
