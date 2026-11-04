package ar.edu.uns.cs.ed.tdas;

public class BTNodo<E> implements Position<E>{

	private E element;
	private BTNodo<E> parent;
	private BTNodo<E> left;
	private BTNodo<E> right;
	
	public BTNodo(E element, BTNodo<E> left, BTNodo<E> right, BTNodo<E> parent){
		this.element = element;
		this.parent = parent;
		this.left = left;
		this.right = right;
	}

	public BTNodo(E element){
		this.element = element;
		this.parent = null;
		this.left = null;
		this.right = null;
	}
	
	public BTNodo(E element, BTNodo<E> parent){
		this.element = element;
		this.parent = parent;
		this.left = null;
		this.right = null;
	}
	
	@Override
	public E element() {
		return element;
	}

	public BTNodo<E> getParent(){
		return parent; 
	}

	public BTNodo<E> getLeft(){
		return left; 
	}

	public BTNodo<E> getRight(){
		return right; 
	}

	public void setElement(E element){
		this.element = element;
	}

	public void setParent(BTNodo<E> parent){
		this.parent = parent;
	}

	public void setLeft(BTNodo<E> left){
		this.left = left;
	}

	public void setRight(BTNodo<E> right){
		this.right = right;
	}



}
