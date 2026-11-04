package ar.edu.uns.cs.ed.tdas.tdalista;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.BoundaryViolationException;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyListException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;

public class ElementIterator<E> implements Iterator<E>{		// ELEMENT ITERATOR DE POSITIONLIST

	protected PositionList<E> list;
	protected Position<E> cursor;
	
	public ElementIterator(PositionList<E> l) {
		list = l;
		if(list.isEmpty()) {
			cursor = null;
		}else {
			try {
				cursor = list.first();
			}catch( EmptyListException e ) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean hasNext() {
		return cursor != null;
	}

	@Override
	public E next() throws NoSuchElementException {
		
		if(cursor == null) {
			throw new NoSuchElementException("No existe un siguiente elemento.");
		}
		
		E toReturn = cursor.element();
		
		try {
			cursor = (cursor == list.last()) ? null : list.next(cursor);
		}catch(InvalidPositionException | BoundaryViolationException | EmptyListException e) {
			e.printStackTrace();  
		}
		
		return toReturn;
	}
}
