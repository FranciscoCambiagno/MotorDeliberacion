package ar.edu.uns.cs.ed.tdas.tdadiccionario;

import java.util.Iterator;

import ar.edu.uns.cs.ed.tdas.Entrada;
import ar.edu.uns.cs.ed.tdas.Entry;
import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidEntryException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidKeyException;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;

public class DiccionarioConHashAbierto<K,V> implements Dictionary<K,V>{
	
	private ListaDoblementeEnlazada<Entrada<K,V>>[] D;
	private int N;
	private int n;
	private static double lambda = 0.7;
	
	public DiccionarioConHashAbierto() {
		N = 13;
		n = 0;
		D = (ListaDoblementeEnlazada<Entrada<K,V>>[]) new ListaDoblementeEnlazada[N];
		
		for(int i=0; i<D.length; i++) {
			D[i] = new ListaDoblementeEnlazada<Entrada<K,V>>();
		}
	}

	@Override
	public int size() {
		return n;
	}

	@Override
	public boolean isEmpty() {
		return n == 0;
	}

	@Override
	public Entry<K, V> find(K key) {
		if(key == null) {
			throw new InvalidKeyException("La key no pued ser nula.");
		}
		
		int bucket = hash(key);
		Iterator<Entrada<K,V>> it = D[bucket].iterator();
		Entrada<K,V> entrada = null;
		boolean encontrado = false;
		
		while(it.hasNext() && !encontrado) {
			entrada = it.next();
			if(entrada.getKey().equals(key)) {
				encontrado = true;
			}
		}
		
		return entrada;
	}

	@Override
	public Iterable<Entry<K, V>> findAll(K key) {
		if(key == null)
			throw new InvalidKeyException("La key no puede ser nula.");
			
		ListaDoblementeEnlazada<Entry<K,V>> entradasK = new ListaDoblementeEnlazada<Entry<K,V>>();
		int bucket = hash(key);
		
		Iterator<Entrada<K,V>> it = D[bucket].iterator();
		Entrada<K,V> entrada = null;
		
		while(it.hasNext()) {
			entrada = it.next();
			if(entrada.getKey().equals(key)) {
				entradasK.addLast(entrada);
			}
		}
		
		return entradasK;
	}

	@Override
	public Entry<K, V> insert(K key, V value) {
		if(key == null)
			throw new InvalidKeyException("La key no puede ser invalida.");
		
		if((float)n/N > lambda) {
			rehash();
		}
		int bucket = hash(key);
		Entrada<K,V> entrada = new Entrada<K,V>(key, value);
		D[bucket].addLast(entrada);
		n++;
		
		return entrada;
	}

	@Override
	public Entry<K, V> remove(Entry<K, V> e) {
		if(e == null)
			throw new InvalidEntryException("La entrada no puede ser nula.");
		
		Entrada<K,V> entradaToRet = null;
		int bucket = hash(e.getKey());
		
		Iterator<Position<Entrada<K,V>>> it = D[bucket].positions().iterator(); 
		Position<Entrada<K,V>> posicionEntrada;
		
		while(it.hasNext() && entradaToRet == null){
			posicionEntrada = it.next();
			Entrada<K,V> entrada = posicionEntrada.element();
			
			if(entrada.getKey().equals(e.getKey()) && entrada.getValue().equals(e.getValue())){
				entradaToRet = entrada;
				D[bucket].remove(posicionEntrada);
				n--;
			}
		}
		
		if(entradaToRet == null)
			throw new InvalidEntryException("La entrada no se encunetra en el diccionario.");
		
		return entradaToRet;
	}

	@Override
	public Iterable<Entry<K, V>> entries() {
		ListaDoblementeEnlazada<Entry<K,V>> entradas = new ListaDoblementeEnlazada<Entry<K,V>>();
		for(int i=0; i<D.length; i++) {
			for(Entrada<K,V> entrada: D[i]) {
				entradas.addLast(entrada);
			}
			
		}
		
		return entradas;
	}
	
	//-------------------------------------
	
	private int hash(K key) {
		return key.hashCode() % N;
	}
	
	private int buscarProximoPrimo() {
		int num = N * 2;
		boolean primo = true;
		boolean encontrado = false;
		
		while(!encontrado) {
			for(int i=2; i*i <= num && primo; i++) {
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
		n = 0;
		Iterable<Entry<K,V>> entradas = entries();
		D = new ListaDoblementeEnlazada[N];
		
		for(int i=0; i<D.length; i++) {
			D[i] = new ListaDoblementeEnlazada<Entrada<K,V>>();
		}
		
		try {
			for(Entry<K,V> entrada : entradas) {
				this.insert(entrada.getKey(), entrada.getValue());
			}
		}
		catch(InvalidKeyException error) {
			System.out.println("Error en reHash: " + error.getMessage());
			error.printStackTrace();
		}
	}
}
