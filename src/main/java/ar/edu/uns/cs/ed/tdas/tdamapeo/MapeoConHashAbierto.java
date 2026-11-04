package ar.edu.uns.cs.ed.tdas.tdamapeo;

import java.util.Iterator;

import ar.edu.uns.cs.ed.tdas.Entrada;
import ar.edu.uns.cs.ed.tdas.Entry;
import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidKeyException;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;

public class MapeoConHashAbierto<K,V> implements Map<K,V>{
	
	private ListaDoblementeEnlazada<Entrada<K,V>>[] arr;
	private int cantElementos;
	private int N;	// Cantidad de buckets
	private static double lambda = 0.7; // cantElementos / N
	
	public MapeoConHashAbierto() {										// O(c)
		N = 13;
		//table = (MapEntry<K,V>[ ]) new MapEntry[capacity]; 	//-----EJEMPLO LIBRO------
		arr = (ListaDoblementeEnlazada<Entrada<K,V>>[]) new ListaDoblementeEnlazada[N]; 	//c1
		for(int i = 0; i<arr.length; i++) {								//c3
			arr[i] = new ListaDoblementeEnlazada<Entrada<K,V>>();					//c2
		}
		cantElementos = 0;												//c4
	}																	// c1 + c2 + c3 + c4 -> T(c)

	@Override
	public int size() {				// T(c)
		return cantElementos;		//c1
	}

	@Override
	public boolean isEmpty() {			// T(c)
		return cantElementos == 0;		//c1
	}

	@Override
	public V get(K key) {													// O(c) 
		if(key == null) {													//c1
			throw new InvalidKeyException("La key no puede ser nula.");		//c2
		}
		
		V value = null;														//c3
		int bucket = hash(key);												//c4
		
		for(Position<Entrada<K,V>> entrada : arr[bucket].positions()) {		//13*c7
			if(entrada.element().getKey().equals(key)) {					//c5
				value = entrada.element().getValue();						//c6
			}
		}
		
		return value;														//c8
	}																		//c1 + c3 + c4 + 13 * c7 + c8 -> T(c)

	@Override
	public V put(K key, V value) {										// O(c)
		if(key == null) {												//c1
			throw new InvalidKeyException("La key no puede ser nula.");	//c2
		}
		
		V valueToRet = null;											//c3
		int bucket = hash(key);											//c4
		
		Iterator<Entrada<K,V>> it = arr[bucket].iterator();				//c5
		Entrada<K,V> entrada = null;									//c6
		
		while(it.hasNext() &&  valueToRet == null) {					//13 * c11
			entrada = it.next();										//c7
			
			if(entrada.getKey().equals(key)) {							//c8
				valueToRet = entrada.getValue();						//c9
				entrada.setValue(value);								//c10
			}
		}
		
		if(valueToRet == null) {										//c12
			//System.out.println("Buckets: " + N + " | Elementos: " + cantElementos + " | Lambda: " + (float)cantElementos / N);
			if((float)cantElementos/N > lambda) {
				rehash();
			}
			cantElementos++;											//c14
			arr[bucket].addLast(new Entrada(key, value));				//c13
		}
		
		
		return valueToRet;												//c15
	}																	// c1 + c3 + c4 + c5 + c6 + 13*c11 + c12 + c13 + c14 + c15 -> T(c)

	@Override
	public V remove(K key) {													// O()
		if(key == null) {														//c
			throw new InvalidKeyException("La key no puede ser nula.");			//c
		}
		
		V value = null;															//c
		int bucket = hash(key);													//c
		
		for(Position<Entrada<K,V>> posicionEntrada : arr[bucket].positions())	//13 * c
			if(posicionEntrada.element().getKey().equals(key)) {				//c
				value = posicionEntrada.element().getValue();					//c
				arr[bucket].remove(posicionEntrada);							//c
				cantElementos--;												//c
				break;															//c
			}
			
		return value;															//c
	}

	@Override
	public Iterable<K> keys() {
		ListaDoblementeEnlazada<K> keys = new ListaDoblementeEnlazada<K>();
		
		for(int i = 0; i < arr.length; i++) {
			for(Entrada<K,V> entrada : arr[i]) {
				keys.addLast(entrada.getKey());
			}
		}
		
		return keys;
	}

	@Override
	public Iterable<V> values() {
		ListaDoblementeEnlazada<V> value = new ListaDoblementeEnlazada<V>();
		
		for(int i = 0; i < arr.length; i++) {
			for(Entrada<K,V> entrada : arr[i]) {
				value.addLast(entrada.getValue());
			}
		}
		
		return value;
	}

	@Override
	public Iterable<Entry<K, V>> entries() {
		PositionList<Entry<K,V>> entradas = new ListaDoblementeEnlazada<Entry<K,V>>();
		
		for(int i=0; i<arr.length; i++) {
			for(Entrada<K,V> entrada : arr[i]) {
				entradas.addLast(entrada);
			}
		}
		
		return entradas;
	}
	
	//------------------------------------------------
	
	private int hash(K key) {;
		return key.hashCode() % N;
	}

	
	private int buscarProximoPrimo() {
		int num = N * 2;
		boolean primo = true;
		boolean encontrado = false;
		
		while(!encontrado) {
			
			for(int i=2; i*i<=num && primo; i++) {
				if(num % i == 0) {
					primo = false;
				}				
			}
			
			if(primo) {
				encontrado = true;
			}else {
				primo = true;
				num++;
			}
		}
		
		
		return num;
	}
	
	private void rehash() {
		N = buscarProximoPrimo();
		cantElementos = 0; 
		Iterable<Entry<K,V>> entradas = entries();
		arr = new ListaDoblementeEnlazada[N];
		
		for(int i=0; i<arr.length; i++) {
			arr[i] = new ListaDoblementeEnlazada<Entrada<K,V>>();
		}
		
		try {
			for(Entry<K,V> entrada : entradas) {
				this.put(entrada.getKey(), entrada.getValue());
			}
		}
		catch(InvalidKeyException error) {
			System.out.println("Error en reHash: " + error.getMessage());
			error.printStackTrace();
		}
	}
}
