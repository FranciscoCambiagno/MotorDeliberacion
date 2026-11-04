package ar.edu.uns.cs.ed.tdas;

import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;

public class TNodo<E> implements Position<E>{
	private E element;
	private TNodo<E> padre;
	private PositionList<TNodo<E>> hijos;
	
	public TNodo(E e) {
		element = e;
		hijos = new ListaDoblementeEnlazada<TNodo<E>>();
		padre = null;
	}

	@Override
	public E element() {
		return element;
	}
	
	public TNodo<E> getPadre(){
		return padre;
	}
	
	public PositionList<TNodo<E>> getHijos(){
		return hijos;
	}
	
	public void setPadre(TNodo<E> p) {
		padre = p;
	}
	
	public void setElement(E e) {
		element = e;
	}

}
