package ar.edu.uns.cs.ed.tdas;

public class DNodo<E> implements Position<E> {
	
	private E elem;
	private DNodo<E> prev;
	private DNodo<E> next;
	
	public DNodo(E e) {
		elem = e;
		prev = null;
		next = null;
	}

	@Override
	public E element() {
		return elem;
	}
	
	public DNodo<E> getNext(){
		return next;
	}
	
	public DNodo<E> getPrev(){
		return prev;
	}
	
	
	public void setElement(E e) {
		elem = e;
	}
	
	public void setPrev(DNodo<E> dn) {
		prev = dn;
	}
	
	public void setNext(DNodo<E> dn) {
		next = dn;
	}
	
	public boolean equals(DNodo<E> d) {
		return prev == d.getPrev() && next == d.getNext() && elem == d.element();		
		
	}
	
	
	
}
