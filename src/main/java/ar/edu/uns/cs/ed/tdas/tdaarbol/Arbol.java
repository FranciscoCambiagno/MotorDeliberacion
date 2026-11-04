package ar.edu.uns.cs.ed.tdas.tdaarbol;

import java.util.Iterator;

import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.TNodo;
import ar.edu.uns.cs.ed.tdas.excepciones.BoundaryViolationException;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyTreeException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidOperationException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;
import ar.edu.uns.cs.ed.tdas.tdamapeo.Map;
import ar.edu.uns.cs.ed.tdas.tdamapeo.MapeoConHashAbierto;

public class Arbol<E> implements Tree<E>{
	
	private TNodo<E> root;
	private int size;
	
	public Arbol() {
		root = null;
		size = 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<E> iterator() {
		PositionList<E> l = new ListaDoblementeEnlazada<E>();
		
		for(Position<E> p : positions()) {
			l.addLast(p.element());
		}
		
		return l.iterator();
	}

	@Override
	public Iterable<Position<E>> positions() {
		PositionList<Position<E>> lista = new ListaDoblementeEnlazada<Position<E>>();
		
		if(!isEmpty()) {
			preordenPosition(lista, root);
		}		
		
		return lista;
	}
	

	@Override
	public E replace(Position<E> v, E e) {
		TNodo<E> u = checkPosition(v);
		
		E element = u.element();
		u.setElement(e);
		
		
		return element;
	}

	@Override
	public Position<E> root() {
		if(isEmpty())
			throw new EmptyTreeException("El arbol esta vacio.");
		
		return root;
	}

	@Override
	public Position<E> parent(Position<E> v) {
		TNodo<E> nodo = checkPosition(v);
		
		if(nodo == root)
			throw new BoundaryViolationException("El nodo raiz no puede tener padre."); 
		
		return nodo.getPadre();
	}

	@Override
	public Iterable<Position<E>> children(Position<E> v) {
		TNodo<E> nodo = checkPosition(v);
		
		PositionList<Position<E>> hijos = new ListaDoblementeEnlazada<Position<E>>();
		for(Position<E> hijo : nodo.getHijos()) {
			hijos.addLast(hijo);
		}		
		
		return hijos;
	}

	
	@Override
	public boolean isInternal(Position<E> v) {
		return !isExternal(v);		
	}

	@Override
	public boolean isExternal(Position<E> v) {
		TNodo<E> nodo = checkPosition(v);
		return nodo.getHijos().isEmpty();
	}

	@Override
	public boolean isRoot(Position<E> v) {
		TNodo<E> nodo = checkPosition(v);
		return nodo == root;
	}

	@Override
	public void createRoot(E e) {
		if(!isEmpty())
			throw new InvalidOperationException("Ya existe una raiz en el arbol.");
		
		root = new TNodo<E>(e);
		size++;
		
	}

	@Override
	public Position<E> addFirstChild(Position<E> p, E e) {
		TNodo<E> nodoPadre = checkPosition(p);
		
		TNodo<E> nuevoNodoHijo = new TNodo<E>(e);
		nuevoNodoHijo.setPadre(nodoPadre);
		nodoPadre.getHijos().addFirst(nuevoNodoHijo);
		size++;
		
		return nuevoNodoHijo;
	}

	@Override
	public Position<E> addLastChild(Position<E> p, E e) {
		TNodo<E> nodoPadre = checkPosition(p);
		
		TNodo<E> nuevoNodoHijo = new TNodo<E>(e);
		nuevoNodoHijo.setPadre(nodoPadre);
		nodoPadre.getHijos().addLast(nuevoNodoHijo);
		size++;
		
		return nuevoNodoHijo;
	}

	@Override
	public Position<E> addBefore(Position<E> p, Position<E> rb, E e) {
		TNodo<E> nodoNuevoHijo = null;
		TNodo<E> nodoPadre = checkPosition(p);
		TNodo<E> nodoRB = checkPosition(rb);
		
		if(isExternal(p)) {
			throw new InvalidPositionException("El nodo padre no tiene hijos.");
		}
		
		Iterator<Position<TNodo<E>>> hijosIT = nodoPadre.getHijos().positions().iterator();
		Position<TNodo<E>> pos = null;
		
		while(hijosIT.hasNext() && nodoNuevoHijo == null) {
			pos = hijosIT.next();
			if(pos.element() == nodoRB) {
				nodoNuevoHijo = new TNodo<E>(e);
				nodoNuevoHijo.setPadre(nodoPadre);
				nodoPadre.getHijos().addBefore(pos,nodoNuevoHijo);
				size++;
			}			
		}
		
		if(nodoNuevoHijo == null)
			throw new InvalidPositionException("El la posicion pasada por parametro no es hijo del padre pasado por parametro.");
		
		return nodoNuevoHijo;
	}

	@Override
	public Position<E> addAfter(Position<E> p, Position<E> lb, E e) {
		TNodo<E> nodoPadre = checkPosition(p);
		TNodo<E> nodoLB = checkPosition(lb);
		
		if(isExternal(p))
			throw new InvalidPositionException("El padre no tiene hijos.");
		
		Iterator<Position<TNodo<E>>> posHijosIT = nodoPadre.getHijos().positions().iterator();
		Position<TNodo<E>> posHijo = null;
		boolean encontrado = false;
		
		while(posHijosIT.hasNext() && !encontrado) {
			posHijo = posHijosIT.next();
			if(posHijo.element() == nodoLB)
				encontrado = true;
		}
		
		TNodo<E> nuevoNodoHijo = null;
		if(encontrado) {
			nuevoNodoHijo = new TNodo<E>(e);
			nuevoNodoHijo.setPadre(nodoPadre);
			nodoPadre.getHijos().addAfter(posHijo, nuevoNodoHijo);
			size++;
		}else {
			throw new InvalidPositionException("El la posicion pasada por parametro no es hijo del padre pasado por parametro.");
		}
		
		
		return nuevoNodoHijo;
	}

	@Override
	public void removeExternalNode(Position<E> p) {
		TNodo<E> nodo = checkPosition(p);
		
		if(!isExternal(nodo))
			throw new InvalidPositionException("La posicion no es de un nodo externo del arbol.");
		
		if(isRoot(nodo)) {
			root = null;
			size--;
		}else {
			TNodo<E> nodoPadre = nodo.getPadre();
			Iterator<Position<TNodo<E>>> hijosIT = nodoPadre.getHijos().positions().iterator();
			Position<TNodo<E>> hijo = null;
			boolean encontrado = false;
			
			while(hijosIT.hasNext() && !encontrado) {
				hijo = hijosIT.next();
				if(hijo.element() == nodo) {
					encontrado = true;
				}
			}
			
			if(encontrado) {
				nodoPadre.getHijos().remove(hijo);
				size--;
			}else {
				throw new InvalidPositionException("Posicion invalida.");
			}
			
		}		
	}

	@Override
	public void removeInternalNode(Position<E> p) {
		TNodo<E> nodo = checkPosition(p);
		
		if(!isInternal(nodo))
			throw new InvalidPositionException("No es un nodo interno.");
		
		if(isRoot(nodo)) {
			if(nodo.getHijos().size() == 1) {
				root = nodo.getHijos().first().element();
				root.setPadre(null);
				size--;
			}else {
				throw new InvalidPositionException("El nodo raiz solo se peude eliminar si tiene un hijo.");
			}
		}else {
			TNodo<E> nodoPadre = nodo.getPadre();
			PositionList<TNodo<E>> hijosNodo = nodo.getHijos();
			Iterator<Position<TNodo<E>>> hijosNodoPadreIT = nodoPadre.getHijos().positions().iterator();
			Position<TNodo<E>> posNodoHijo = null;
			
			Position<TNodo<E>> pNodo = null;
			
			while(hijosNodoPadreIT.hasNext() && pNodo == null) {
				posNodoHijo = hijosNodoPadreIT.next();
				if(posNodoHijo.element() == nodo) {
					pNodo = posNodoHijo;
					for(TNodo<E> hijoNodo : hijosNodo) {
						hijoNodo.setPadre(nodoPadre);
						nodoPadre.getHijos().addBefore(pNodo, hijoNodo);
					}
					nodoPadre.getHijos().remove(pNodo);
					size--;
				}
			}			
		}
		
	}

	@Override
	public void removeNode(Position<E> p) {
		if(isExternal(p)) {
			removeExternalNode(p);
		}else {
			removeInternalNode(p);
		}		
	}
	
	
	/*
	 * ***************************************************
	 * ---------------METODOS PRIVADOS--------------------
	 * ***************************************************
	 * */
	
	private void preordenPosition(PositionList<Position<E>> lista, TNodo<E> v) {
		lista.addLast(v);
		for(TNodo<E> h : v.getHijos()) {
			preordenPosition(lista, h);
		}
	}
	
	
	private TNodo<E> checkPosition(Position<E> v){
		TNodo<E> nodo = null;
		
		if(v == null)
			throw new InvalidPositionException("Posicion vacia: Posicion invalida.");
		
		if(isEmpty())
			throw new InvalidPositionException("Arbol vacio: Posicion invalida.");
		
		try {
			nodo = (TNodo<E>) v;
		}catch (ClassCastException e) {
			throw new InvalidPositionException("Posicion invalida.");
		}		
		
		return nodo;
	}
	
	
	/*
	 * ***************************************************
	 * -----------------------TP6-------------------------
	 * ***************************************************
	 * */
	
	public void eliminarUltimoHijo(Position<E> p) { //O(c)
	/*
		Este método deberá eliminar del árbol receptor del mensaje a la posición p siempre que p sea el último hijo
		(de izq a der) de su padre. 
		La raíz no se considera último hijo, en este caso el método deberá lanzarInvalidOperationException.
		Si la posición p es inválida el método deberá lanzar InvalidPositionException
	*/
		
		TNodo<E> nodo = checkPosition(p);	//O(c)
		
		if(isRoot(nodo)) {	//O(c)
			throw new InvalidOperationException("La raiz no se considera ultimo hijo.");	//O(c)
		}
		
		TNodo<E> nodoPadre = nodo.getPadre();	//O(c)
		
		if(nodoPadre != null && !nodoPadre.getHijos().isEmpty()) { //O(c)
			
			Position<TNodo<E>> positionUltimoHijoDePadre = nodoPadre.getHijos().last(); //O(c)
			if(positionUltimoHijoDePadre.element() == nodo) {	//O(c)
				nodoPadre.getHijos().remove(positionUltimoHijoDePadre); //O(c)
				nodo.setPadre(null);	//O(c)
				size--;	//O(c)
			}
			
		}
	}
	
	
	
	
}