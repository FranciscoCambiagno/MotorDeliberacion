package ar.edu.uns.cs.ed.tdas;

public class Entrada<K,V> implements Entry<K,V>{	
	private K clave;
	private V valor;
	
	public Entrada(K clave, V valor) {
		this.clave = clave;
		this.valor = valor;
	}

	@Override
	public K getKey() {
		return clave;
	}

	@Override
	public V getValue() {
		return valor;
	}
	
	public void setKey(K clave) {
		this.clave = clave;
	}

	public void setValue(V value) {
		valor = value;
	}
	
	public String toString() {
		return "(" + clave + "," + valor + ")";
	}
}
