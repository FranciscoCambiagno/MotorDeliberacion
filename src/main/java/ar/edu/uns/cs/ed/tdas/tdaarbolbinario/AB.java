package ar.edu.uns.cs.ed.tdas.tdaarbolbinario;

import java.util.Iterator;

import ar.edu.uns.cs.ed.tdas.BTNodo;
import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.BoundaryViolationException;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyTreeException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidOperationException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;

public class AB<E> implements BinaryTree<E>{
	
	private BTNodo<E> root;
	private int size;
	
	public AB(){
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
		PositionList<E> list = new ListaDoblementeEnlazada<E>();
		
		if(!isEmpty())
			preordenDeIterator(list, this, root);	
		
		return list.iterator();
	}
	
	private void preordenDeIterator(PositionList<E> l, AB<E> t, BTNodo<E> v) {
		//Visita
		l.addLast(v.element());
		
		//Recorrido
		if(t.hasLeft(v))
			preordenDeIterator(l, t, v.getLeft());
		if(t.hasRight(v))
			preordenDeIterator(l, t, v.getRight());	
	}	

	@Override
	public Iterable<Position<E>> positions() {
		PositionList<Position<E>> list = new ListaDoblementeEnlazada<Position<E>>();
		
		if(!isEmpty())
			preordenDePositions(list, this, root);	
		
		return list;
	}
	
	private void preordenDePositions(PositionList<Position<E>> l, AB<E> t, BTNodo<E> v) {
		//Visita
		l.addLast(v);
		
		//Recorrido
		if(t.hasLeft(v))
			preordenDePositions(l, t, v.getLeft());
		if(t.hasRight(v))
			preordenDePositions(l, t, v.getRight());	
	}

	@Override
	public E replace(Position<E> v, E e) {
		BTNodo<E> nodo = checkPosition(v);
		E element = nodo.element();
		
		nodo.setElement(e);
		
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
		BTNodo<E> nodo = checkPosition(v);	
		
		if(isRoot(v))
			throw new BoundaryViolationException("El nodo raiz no tiene padre.");
		
		return nodo.getParent();
	}

	@Override
	public Iterable<Position<E>> children(Position<E> v) {
		BTNodo<E> nodo = checkPosition(v);
		
		PositionList<Position<E>> list = new ListaDoblementeEnlazada<Position<E>>();
		
		if(hasLeft(v))
			list.addLast(nodo.getLeft());
		
		if(hasRight(v))
			list.addLast(nodo.getRight());
		
		return list;
	}

	@Override
	public boolean isInternal(Position<E> v) {
		return hasLeft(v) || hasRight(v);
	}

	@Override
	public boolean isExternal(Position<E> v) {
		return !isInternal(v);
	}

	@Override
	public boolean isRoot(Position<E> v) {
		BTNodo<E> nodo = checkPosition(v);		
		return nodo == root;
	}

	@Override
	public void createRoot(E e) {
		if(root != null)
			throw new InvalidOperationException("El arbo la posee una raiz.");
		
		root = new BTNodo<E>(e);
		size++;
	}

	@Override
	public Position<E> addFirstChild(Position<E> p, E e) {
		return addLeft(p, e);
	}

	@Override
	public Position<E> addLastChild(Position<E> p, E e) {
		return addRight(p, e);
	}

	@Override
	public Position<E> addBefore(Position<E> p, Position<E> rb, E e) {
		BTNodo<E> nodo = checkPosition(p);
		BTNodo<E> nodoRB = checkPosition(rb);
		
		if(nodo.getRight() != nodoRB)
			throw new InvalidPositionException("El nodo derecho no es el pasado por parametro.");		
		
		return addLeft(p, e);
	}

	@Override
	public Position<E> addAfter(Position<E> p, Position<E> lb, E e) {
		BTNodo<E> nodo = checkPosition(p);
		BTNodo<E> nodoLB = checkPosition(lb);
		
		if(nodo.getLeft() != nodoLB)
			throw new InvalidPositionException("El nodo izquierdo no es el pasado por parametro.");		
		
		return addRight(p, e);
	}

	@Override
	public void removeExternalNode(Position<E> p) {
		BTNodo<E> nodo = checkPosition(p);
		
		if(isExternal(p)) {
		
			if(isRoot(p)) {
				root = null;
				size--;
			}else {
				BTNodo<E> nodoPadre = nodo.getParent();
				if(nodoPadre.getLeft() == nodo) {
					nodoPadre.setLeft(null);
				}else {
					nodoPadre.setRight(null);
				}
				nodo.setParent(null);
				size--;
			}
			
		}
	}

	@Override
	public void removeInternalNode(Position<E> p) {
		BTNodo<E> nodo = checkPosition(p);
		
		if(isInternal(p)) {
			
			if(isRoot(p)) {
				if(hasLeft(p) && !hasRight(p) ||  !hasLeft(p) && hasRight(p)) {
					//Se puede hacer
					BTNodo<E> nodoHijo = null;
					if(hasLeft(p)) {
						nodoHijo = nodo.getLeft();
					}else {
						nodoHijo = nodo.getRight();
					}
					
					nodoHijo.setParent(null);
					root = nodoHijo;
					size--;					
				}else {
					throw new InvalidPositionException("Solo se puede eliminar la raiz si tiene un solo hijo.");
				}
			}else {
				BTNodo<E> nodoPadre = nodo.getParent();
				
				if(hasLeft(p) && !hasRight(p) ||  !hasLeft(p) && hasRight(p)) {
					//Se peude hacer
					BTNodo<E> nodoHijo = null;
					if(hasLeft(p)) {
						nodoHijo = nodo.getLeft();
					}else {
						nodoHijo = nodo.getRight();
					}
					
					if(hasLeft(nodoPadre) && nodoPadre.getLeft() == nodo) {
						nodoPadre.setLeft(nodoHijo);						
					}else {
						nodoPadre.setRight(nodoHijo);						
					}
					
					nodoHijo.setParent(nodoPadre);
					size--;
					
				}else if(hasLeft(nodoPadre) && !hasRight(nodoPadre) ||  !hasLeft(nodoPadre) && hasRight(nodoPadre)){
					//Se puede hacer
					BTNodo<E> nodoHijoIzquierdo = nodo.getLeft();
					BTNodo<E> nodoHijoDerecho = nodo.getRight();
					
					nodoHijoIzquierdo.setParent(nodoPadre);
					nodoHijoDerecho.setParent(nodoPadre);
					
					nodoPadre.setLeft(nodoHijoIzquierdo);
					nodoPadre.setRight(nodoHijoDerecho);
					
					size--;
				}else {
					throw new InvalidPositionException("El nodo debe tener un solo hijo o el padre debe tener un solo hijo.");
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

	@Override
	public Position<E> left(Position<E> v) {
		BTNodo<E> nodo = checkPosition(v);
		
		if(!hasLeft(v))
			throw new BoundaryViolationException("El nodo no posee hijo izquierdo");
		
		return nodo.getLeft();
	}

	@Override
	public Position<E> right(Position<E> v) {
		BTNodo<E> nodo = checkPosition(v);
		
		if(!hasRight(v))
			throw new BoundaryViolationException("El nodo no posee hijo derecho");
		
		return nodo.getRight();
	}

	@Override
	public boolean hasLeft(Position<E> v) {
		BTNodo<E> nodo = checkPosition(v);
		return nodo.getLeft() != null;
	}

	@Override
	public boolean hasRight(Position<E> v) {
		BTNodo<E> nodo = checkPosition(v);
		return nodo.getRight() != null;
	}

	@Override
	public Position<E> addLeft(Position<E> v, E r) {
		BTNodo<E> nodo = checkPosition(v);
		
		if(hasLeft(v)) {
			throw new InvalidOperationException("El nodo ya posee un hijo izquierdo.");
		}
		
		BTNodo<E> nodoIzquierdo = new BTNodo<E>(r);
		nodoIzquierdo.setParent(nodo);
		nodo.setLeft(nodoIzquierdo);
		size++;
		
		return nodoIzquierdo;
	}

	@Override
	public Position<E> addRight(Position<E> v, E r) {
		BTNodo<E> nodo = checkPosition(v);
				
		if(hasRight(v)) {
			throw new InvalidOperationException("El nodo ya posee un hijo derecho.");
		}
		
		BTNodo<E> nodoDerecho= new BTNodo<E>(r);
		nodoDerecho.setParent(nodo);
		nodo.setRight(nodoDerecho);
		size++;
		
		return nodoDerecho;
	}
	
	//--------------------------------------------------------------------------------------
	
	public void attach(Position<E> p, BinaryTree<E> t1, BinaryTree<E> t2) throws InvalidPositionException {
		BTNodo<E> raiz_local = checkPosition(p);
		if (raiz_local.getLeft() != null || raiz_local.getRight() != null)
			throw new InvalidPositionException("La posicion no corresponde a un nodo hoja");
		try {
		//Clonación de T1 como subárbol izquierdo
		if (!t1.isEmpty()) {
			Position<E> raiz_t1 = t1.root();
			BTNodo<E> hi_raiz_local = new BTNodo<E>(raiz_t1.element(), raiz_local);
			raiz_local.setLeft(hi_raiz_local);
			clonar(raiz_local.getLeft(), raiz_t1, t1);
		}
		//Clonación de T2 como subárbol derecho
		if (!t2.isEmpty()) {
			Position<E> raiz_t2 = t2.root();
			BTNodo<E> hd_raiz_local = new BTNodo<E>(raiz_t2.element(), raiz_local);
			raiz_local.setRight(hd_raiz_local);
			clonar(raiz_local.getRight(), raiz_t2, t2);
		}
		size += t1.size() + t2.size();
		}catch(EmptyTreeException e) { raiz_local.setLeft(null); raiz_local.setRight(null); }
	}

	protected void clonar(BTNodo<E> padre_local, Position<E> padre_t, BinaryTree<E> t) {
		try {
			//Si existe hijo izquierdo en T de padre_t, se clona este y el subárbol a partir del hijo izquierdo de padre_t.
			if (t.hasLeft(padre_t)) {
				Position<E> hi_padre_t = t.left(padre_t);
				BTNodo<E> hi_padre_local = new BTNodo<E>(hi_padre_t.element(), padre_local);
				padre_local.setLeft(hi_padre_local);
				clonar(hi_padre_local, hi_padre_t, t);
			}
			//Si existe hijo derecho en T de padre_t, se clona este y el subárbol a partir del hijo derecho de padre_t.
			if (t.hasRight(padre_t)) {
				Position<E> hd_padre_t = t.right(padre_t);
				BTNodo<E> hd_padre_local = new BTNodo<E>(hd_padre_t.element(), padre_local);
				padre_local.setRight(hd_padre_local);
				clonar(hd_padre_local, hd_padre_t, t);
			}
			}catch(InvalidPositionException | BoundaryViolationException e) {
			padre_local.setLeft(null);
			padre_local.setRight(null);
		}
	}
	
	
	

	/*@Override
	public void attach(Position<E> v, BinaryTree<E> T1, BinaryTree<E> T2) {
		BTNodo<E> nodo = checkPosition(v);
		
		if(!isExternal(v))
			throw new InvalidPositionException("El nodo pasado por parametros no corresponde a una hija.");
		
		if(!T1.isEmpty()) {
			addLeft(nodo, T1.root().element());
						// padre, hijo,    arbolParametro, arbolEste
			preordenAttach(nodo, T1.root(), 	T1, 		this);
		}
		
		if(!T2.isEmpty()) {
			addRight(nodo, T1.root().element());
						// padre, hijo,    arbolParametro, arbolEste
			preordenAttach(nodo, T1.root(), 	T1, 		this);
		}
	}
	
	private void preordenAttach(Position<E> nPadre, Position<E> nHijo, BinaryTree<E> tRecorrer, BinaryTree<E> tUnir) {
		
		if(tRecorrer.hasLeft(nHijo))
			tUnir.addLeft(nPadre, tRecorrer.left(nHijo).element());
			preordenAttach(nHijo, tRecorrer.left(nHijo), tRecorrer, tUnir);
		if(tRecorrer.hasRight(nHijo))
			tUnir.addRight(nPadre, tRecorrer.right(nHijo).element());
			preordenAttach(nHijo, tRecorrer.right(nHijo), tRecorrer, tUnir);
	}*/
	
	private BTNodo<E> checkPosition(Position<E> p){
		
		if(isEmpty()) {
			throw new InvalidPositionException("El arbol binario esta vacio.");
		}
		
		if(p == null) {
			throw new InvalidPositionException("La posicion es nula.");
		}
		
		BTNodo<E> nodo = null;
		
		try {
			nodo = (BTNodo<E>) p;
		}catch(ClassCastException e) {
			throw new InvalidPositionException("La position no pertenece a un nodo de arbol binario.");
		}
		
		return nodo;
	}

}
