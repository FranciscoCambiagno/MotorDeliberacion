package ar.edu.uns.cs.ed;

import ar.edu.uns.cs.ed.tdas.Entrada;
import ar.edu.uns.cs.ed.tdas.Entry;
import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.TNodo;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyStackException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;
import ar.edu.uns.cs.ed.tdas.tdaarbol.Arbol;
import ar.edu.uns.cs.ed.tdas.tdaarbol.Tree;
import ar.edu.uns.cs.ed.tdas.tdacola.ColaConNodo;
import ar.edu.uns.cs.ed.tdas.tdacola.Queue;
import ar.edu.uns.cs.ed.tdas.tdadiccionario.DiccionarioConHashAbierto;
import ar.edu.uns.cs.ed.tdas.tdadiccionario.Dictionary;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;
import ar.edu.uns.cs.ed.tdas.tdamapeo.Map;
import ar.edu.uns.cs.ed.tdas.tdamapeo.MapeoConHashAbierto;
import ar.edu.uns.cs.ed.tdas.tdapila.PilaEnlazada;
import ar.edu.uns.cs.ed.tdas.tdapila.Stack;

/**
 * Incluir los import necesarios para las clases utilizadas en el código.
 */

/**
 * Clase que representa el motor de deliveración.
 * 
 */
public class MotorDeliberacion{

    /**
     * Incluir los atributos necesarios para el desarrollo.
     */
	private Tree<Opinion> arbol;
	private Stack<Operaciones> historial;
    private Map<String,Position<Opinion>> posicionesOpiniones;
    private Dictionary<String, Position<Opinion>> opinionesPorUsuario;
	private Queue<Operaciones> logs;
	
    /**
     * Constructor de la clase MotorDeliveracion.
     * Inicializa el árbol de debate, y las estructuras correspondientes.  
     * 
     */
    public MotorDeliberacion() {
    //TODO: Inicializar el árbol de debate y las estructuras correspondientes.
    	clearDebate();
    }

    /**
     * Registra una operación en la estructura que almacena las operaciones.
     * Las siguientes operaciones se pueden deshacer: "CREAR_DEBATE", "POSTEAR". El resto de las operaciones no se pueden deshacer.
     * 
     * La operación eliminar posteo no se puede deshacer, y si se intenta deshacer una operación cuando no hay operaciones registradas, se lanza una excepción.
     * 
     * 
     * @param operacion : La operación a registrar. 1: CREAR_DEBATE, 2: POSTEAR
     * @param detalle : Detalle de la operación, como el id del argumento o el id del usuario. El texto de la operación se almacena en mayúsculas para mantener consistencia. Por ejemplo: "CREAR_DEBATE:" + idRaiz.trim()
     * @param idArgumento : Identificador del argumento o de root en caso de ser el tema raíz.
     * @throws IllegalArgumentException si la operación es nula o vacía.
     */
    public void registrarOperacion(int operacion, String detalle, String idArgumento) {
        //TODO: Implementar el registro de la operación en la estructura correspondiente.
    	
    	/*------------------------------------------------------------------------
    	 * operacion : 1: CREAR_DEBATE, 2: POSTEAR, 3: ELIMINAR_POSTEO
    	 * detalle : <nombre operacion> + ":" + ID ususario + ":" + ID argumento + ":" + ID argumento padre (null si es root) + ":" + texto.trim();
    	 * idArgumento : ID argumento
    	//----------------------------------------------------------------------*/
    	
    	if(operacion < 1 || operacion > 3)
    		throw new IllegalArgumentException("Operacion invalida.");
    	
    	if(detalle ==  null || idArgumento == null)
    		throw new IllegalArgumentException("El detalle y el idArgumento no puedens ser nulos.");
    	
    	detalle = detalle.trim();
    	idArgumento = idArgumento.trim();    	
    	
    	if(detalle.equals("") || idArgumento.equals(""))
    		throw new IllegalArgumentException("El detalle y el idArgumento no puedens ser vacios.");
    	
    	
    	historial.push(new Operaciones(operacion, detalle, idArgumento));
    }

/**
 * Deshace la última operación registrada en el motor de deliveración.
 * La operación eliminar posteo no se puede deshacer, y si se intenta deshacer una operación cuando no hay operaciones registradas, se lanza una excepción.
 * @return
 * @throws EmptyStackException
 */
    public String deshacerUltimaOperacion() throws EmptyStackException {
        //TODO: Implementar la funcionalidad de deshacer la última operación registrada.
    	
    	if(historial.isEmpty())
    		throw new EmptyStackException("No se realizo ninguna operacion aun.");
    	
    	Operaciones ultimaOperacion = historial.pop();
    	String texto = null;
    	
    	int operacion = ultimaOperacion.getOperacion();
    	
    	// La operacion es ELIMINAR_POSTEO no se puede deshacer ERROR
    	if(operacion == 3)
    		throw new IllegalStateException("No se puede deshacer la eliminacion de un posteo.");
    	
    	//System.out.println(ultimaOperacion.getOperacion() + "-------");
    	// la operacion es POSTEAR se puede deshacer
    	if(operacion == 2) {
    		String idPosOpinion = ultimaOperacion.getId();
    		//System.out.println(posicionesOpiniones.get(idPosOpinion).element().getUsuario().getId());
    		String idUsuario = posicionesOpiniones.get(idPosOpinion).element().getUsuario().getId(); // getIdUsuarioOperaciones(ultimaOperacion);
    		//String idPosOpinionPadre = arbol.parent(posicionesOpiniones.get(idPosOpinion)).element().getIdArgumento();
    		texto = ultimaOperacion.getDetalle();
    		
    		eliminarPosteo(idUsuario, idPosOpinion);
    	
        // la operacion es CREAR_DEBATE se puede deshacer
    	}else if(operacion == 1) {	    		
    		clearDebate();    		
    	}    	
    	
    	return texto;
    }
    
    private String getIdUsuarioOperaciones(Operaciones operacion) {
    	return operacion.getDetalle().split(":")[1];
    }
    
    
/**
 * Crea un nuevo debate con un argumento raíz.
 * @param idRaiz   : Identificador único del tema raíz.
 * @param texto:    Texto del tema raíz.
 * @param idUsuario: Identificador del usuario.
 * @param nombreUsuario: Nombre del usuario.
 * 
 * El método valida que los parámetros no sean nulos o vacíos, y que el identificador de la raíz sea numérico.
 * Si el árbol ya tiene un debate, lanza una excepción.
 * El método registra la operación en la pila de operaciones para permitir deshacerla posteriormente.
 * @throws IllegalArgumentException si algún parámetro es inválido.
 * @throws IllegalStateException si ya existe un debate en curso.
 * Crea un usuario y un tema raíz, y los agrega al árbol y a las estructuras correspondientes.
 */
    public void crearDebate(String idRaiz, String texto, String idUsuario, String nombreUsuario) {
    //TODO: Implementar la creación del debate con el argumento raíz y el usuario correspondiente.
    	
    	if(!arbol.isEmpty())
    		throw new IllegalStateException("Ya existe un debate en curso.");

    	if(idRaiz == null || texto == null || idUsuario == null || nombreUsuario == null)
    		throw new IllegalArgumentException("No pueden haber parametros nulos.");
    	
    	idRaiz = idRaiz.trim();
    	texto = texto.trim(); 
    	idUsuario = idUsuario.trim(); 
    	nombreUsuario = nombreUsuario.trim(); 
    	
    	if(idRaiz.equals("") || idUsuario.equals("") || nombreUsuario.equals("") || texto.equals(""))
    		throw new IllegalArgumentException("No pueden haber parametros vacios.");
    	
    	Usuario usuarioRoot = new Usuario(idUsuario,nombreUsuario);
    	Opinion opinionRoot = new Opinion(idRaiz,texto,"ROOT",usuarioRoot);
    	arbol.createRoot(opinionRoot);
    	
    	posicionesOpiniones.put(idRaiz, arbol.root());
    	opinionesPorUsuario.insert(idUsuario, arbol.root());
    	
    	String detalle = "CREAR_DEBATE:" + idUsuario + ":" + idRaiz + ":" + "::" + texto;
    	registrarOperacion(1, detalle, idRaiz);    	
    }

    public void Postear(Usuario usr, String idNuevo, String idPadre, String postura, String texto) {
        //TODO: Implementar la funcionalidad de postear un nuevo argumento en el debate.
    	
    	if(arbol.isEmpty())
    		throw new IllegalStateException("No existe un debate en curso.");
    	
    	if(usr == null)
    		throw new IllegalArgumentException("El usuario no puede ser nulo.");
    	
    	if(idNuevo == null || texto == null || idPadre == null)
    		throw new IllegalArgumentException("No pueden haber parametros nulos.");
    	
    	idPadre = idPadre.trim();
    	texto = texto.trim(); 
    	idNuevo = idNuevo.trim(); 
    	postura = postura.trim().toUpperCase();

    	if(idNuevo.equals("") || texto.equals("") || idPadre.equals(""))
    		throw new IllegalArgumentException("No pueden haber parametros vacios.");
    	
    	if(!postura.equals("AFAVOR") && !postura.equals("ENCONTRA"))	// Las posturas estan seteadas asi en el tester
    		throw new IllegalArgumentException("La postura no es valida.");
    	
    	if(posicionesOpiniones.get(idNuevo) != null)
    		throw new IllegalArgumentException("Ya existe un argumento con ese ID.");
    	
    	Position<Opinion> posOpinonPadre = posicionesOpiniones.get(idPadre);
    	if(posOpinonPadre == null)
    		throw new IllegalArgumentException("No existe una opinon con el IDPadre pasado como parametro.");
    	
    	Opinion nuevaOpinon = new Opinion(idNuevo, texto, postura, usr);
    	Position<Opinion> posOpinionNueva = arbol.addLastChild(posOpinonPadre, nuevaOpinon);
    	
    	posicionesOpiniones.put(idNuevo, posOpinionNueva);
    	opinionesPorUsuario.insert(usr.getId(), posOpinionNueva);	
    	
    	String detalle = "POSTEAR:" + usr.getId() + ":" + idNuevo + ":" + idPadre + ":" + texto;
    	registrarOperacion(2, detalle, idNuevo);
    }
    

    // Elimina un posteo y todo el subárbol.
    public void eliminarPosteo(String idUsuario, String idPosteo) {
    //TODO: Implementar la funcionalidad de eliminar un posteo y todo su subárbol en el debate.
    	//Comprobaciones
    	
    	if(idUsuario == null || idPosteo == null)
    		throw new IllegalArgumentException("Los IDs no pueden ser nulos.");
    	
    	idPosteo = idPosteo.trim(); 
    	idUsuario = idUsuario.trim(); 
    	
    	if(idUsuario.equals("") || idPosteo.equals(""))
        	throw new IllegalArgumentException("Los IDs no pueden ser vacios.");
    	
        Position<Opinion> posPosteoAEliminar = posicionesOpiniones.get(idPosteo);
        
        if(posPosteoAEliminar == null)
        	throw new IllegalArgumentException("No existe un posteo con esa ID.");
       
        //System.out.println(posPosteoAEliminar.element().toString());
        System.out.println(posPosteoAEliminar.element().getUsuario().getId() + "--" + idUsuario);
        if(posPosteoAEliminar.element().getUsuario().getId() != idUsuario)
        	throw new IllegalStateException("El usuario pasado por parametro no es propietario del posteo.");
        
        //Obtiene informacion para guardar el registro
        //Position<Opinion> posPosteoPadre = null;
        //String idPosPosteoPadre = null;
        //if(arbol.root() != posPosteoAEliminar) {
        //	posPosteoPadre = arbol.parent(posPosteoAEliminar);
        //	idPosPosteoPadre = posPosteoPadre.element().getIdArgumento();
        //}
        //String texto = posPosteoAEliminar.element().getTexto();
        
        //Elimina el posteo
        elimnarSubarbolPostorden(posPosteoAEliminar,idUsuario,idPosteo);
        
        //Guarda la operacion realizada en el historial ?????
        //String detalle = "ELIMINAR_POSTEO:" + idUsuario + ":" + idPosteo + ":" + idPosPosteoPadre + ":" + texto;
    	//registrarOperacion(3, detalle, idPosteo);
    	
    }
    
    private void elimnarSubarbolPostorden(Position<Opinion> posOpinion, String idUsuario, String idPosteo) {    	
    	//Recorrido
    	for(Position<Opinion> hijo : arbol.children(posOpinion)) {
    		String idPostHijo = hijo.element().getIdArgumento();
    		String idUsuarioHijo = hijo.element().getUsuario().getId();
    		
    		elimnarSubarbolPostorden(hijo, idUsuarioHijo, idPostHijo);
    	}
    	
    	//Visita
    	opinionesPorUsuario.remove(new Entrada(idUsuario, posOpinion));
    	posicionesOpiniones.remove(idPosteo);
    	
    	arbol.removeNode(posOpinion);    	
    }

    //obtiene las posiciones en postorden.
    //private void obtenerIdsYPosicionesSubarbol(Position<Opinion> nodo, PositionList<String> ids, PositionList<Position<Opinion>> posiciones) {
    //TODO: Implementar la funcionalidad de obtener los ids y posiciones de un subárbol en postorden.
    //}

    //limpia todo el debate, dejando la estructura vacía.   
    public void clearDebate() {
    //TODO: Implementar la funcionalidad de limpiar todo el debate, dejando la estructura vacía.
    	arbol = new Arbol<Opinion>();														// Arbol de Opinones
    	historial = new PilaEnlazada<Operaciones>(); 										// Historial de operacioens que se pueden deshacer
        posicionesOpiniones = new MapeoConHashAbierto<String,Position<Opinion>>();  		// Guarda la posicion de las opniones en base a su ID
        opinionesPorUsuario = new DiccionarioConHashAbierto<String,Position<Opinion>>();	// Guarda la posicion de las opiniones por ID de usuario				
        logs = new ColaConNodo<Operaciones>();												// Logs de operaciones. No se pueden deshacer.
    }

    //consultar por usuario, devuelve una lista de argumentos que pertenecen a ese usuario.
    public PositionList<Opinion> consultarArgumentosPorUsuario(String idUsuario) {
    //TODO: Implementar la funcionalidad de consultar los argumentos de un usuario específico en el debate.
    	
    	if(idUsuario == null)
        	throw new IllegalArgumentException("El ID de usuario no puede ser nulo.");
    	
    	idUsuario = idUsuario.trim();
    	
    	if(idUsuario.equals(""))
        	throw new IllegalArgumentException("El ID de usuario no puede ser vacios..");
    	
    	PositionList<Opinion> ArgumentosPorUsuario = new ListaDoblementeEnlazada<Opinion>();
    	for(Entry<String,Position<Opinion>> entrada : opinionesPorUsuario.findAll(idUsuario)) {
    		ArgumentosPorUsuario.addLast(entrada.getValue().element());
    	}
    	
    	return ArgumentosPorUsuario;
    }
    
    /**
     * Calcula la polarización de todos los nodos del debate.
     * La polarización se define como la cantidad de argumentos a favor y en contra que existen en el subárbol que tiene como raíz cada nodo.
     * @throws IllegalStateException si no hay un debate activo.
     * @return Un diccionario cuya clave es el ID del nodo y el valor es un diccionario con la polarización: 
     * "AFAVOR" y "ENCONTRA" y sus respectivos conteos.
     */
    public DiccionarioConHashAbierto<String,DiccionarioConHashAbierto<String,Integer>> calcularPolarizacionDebate(){
        //TODO
    	
    	if(arbol.isEmpty())
    		throw new IllegalStateException("No hay un debate activo");
    	
    	DiccionarioConHashAbierto<String,DiccionarioConHashAbierto<String,Integer>> dictPolarizacion = new DiccionarioConHashAbierto<String,DiccionarioConHashAbierto<String,Integer>>();
    	postordenPolarizacionTotal(arbol.root(), dictPolarizacion);    	
    	
    	return dictPolarizacion;
    }
    
    private void postordenPolarizacionTotal(Position<Opinion> posOpinion, DiccionarioConHashAbierto<String,DiccionarioConHashAbierto<String,Integer>> dictPolarizacion) {
    	int contAFavor = 0;
    	int contEnContra = 0;
    	//Recorrido
    	for(Position<Opinion> hijo : arbol.children(posOpinion)) {
    		postordenPolarizacionTotal(hijo, dictPolarizacion);
    		
    		String idHijo = hijo.element().getIdArgumento();
    		contAFavor += dictPolarizacion.find(idHijo).getValue().find("AFAVOR").getValue();
        	contEnContra += dictPolarizacion.find(idHijo).getValue().find("ENCONTRA").getValue();
    	}
    	
    	//Visita
    	if(posOpinion.element().getPostura() == "AFAVOR") {
    		contAFavor += 1;
    	}else if(posOpinion.element().getPostura() == "ENCONTRA") {
    		contEnContra += 1;
    	}
    	DiccionarioConHashAbierto<String,Integer> dictPolarizacionPosActual = new DiccionarioConHashAbierto<String,Integer>();
    	dictPolarizacionPosActual.insert("AFAVOR",contAFavor);
    	dictPolarizacionPosActual.insert("ENCONTRA",contEnContra);
    	
    	String idPosActual = posOpinion.element().getIdArgumento();
    	dictPolarizacion.insert(idPosActual, dictPolarizacionPosActual);    	
    }

     /**
    * Calcula la polarización de un nodo específico en el árbol de debate.
    * La polarización se define como la cantidad de argumentos a favor y en contra que existen en el subárbol que tiene como raíz el nodo especificado por idNodo.
    * @throws IllegalStateException si no hay un debate activo. 
    * @param idNodo : Identificador del nodo para el cual se desea calcular la polarización.
    * @return Un DiccionarioHashAbierto<String,Integer> que contiene la polarización del nodo, con las claves "AFAVOR" y "ENCONTRA" y sus respectivos conteos.
    */
    public DiccionarioConHashAbierto<String,Integer> calcularPolarizacionNodo(String idNodo){
        //TODO
    	
    	if(arbol.isEmpty())
    		throw new IllegalStateException("No hay un debate activo");
    	
    	if(idNodo == null)
    		throw new IllegalArgumentException("El ID del nodo no puede ser nulo.");
    		
    	idNodo = idNodo.trim();
    		
    	if(idNodo.equals(""))
    		throw new IllegalArgumentException("El ID del nodo no puede estar vacio.");
    	
    	
    	Position<Opinion> posOpinion = posicionesOpiniones.get(idNodo);
    	
    	if(posOpinion == null)
    		throw new IllegalArgumentException("El ID pasado por paramtero no peretenece a ninguna opinion de este debate.");
    	
    	DiccionarioConHashAbierto<String,Integer> dictPolarizacion = new DiccionarioConHashAbierto<String,Integer>();

    	dictPolarizacion.insert("AFAVOR", 0);
    	dictPolarizacion.insert("ENCONTRA", 0);
    	
    	preordenPolarizacion(posOpinion, dictPolarizacion);
    	
    	return dictPolarizacion;
    }
    
    private void preordenPolarizacion(Position<Opinion> posOpinion, DiccionarioConHashAbierto<String,Integer> dictPolarizacion){
    	
    	//Visita
    	int count = 0;
    	Entry<String,Integer> entrada = null;
    	
    	if(posOpinion.element().getPostura().equals("AFAVOR")) {
    		entrada = dictPolarizacion.find("AFAVOR");
    		count = entrada.getValue();
    		dictPolarizacion.remove(entrada);
    		dictPolarizacion.insert("AFAVOR", count+1);
    		
    	}else if(posOpinion.element().getPostura().equals("ENCONTRA")) {
    		entrada = dictPolarizacion.find("ENCONTRA");
    		count = entrada.getValue();
    		dictPolarizacion.remove(entrada);
    		dictPolarizacion.insert("ENCONTRA", count+1);
    	}
    	
    	//Recorrido    	
    	for(Position<Opinion> hijo : arbol.children(posOpinion)) {
    		preordenPolarizacion(hijo, dictPolarizacion);
    	}
    }
    
    public void mostrarOperaciones() {
    	int cantLogs = logs.size();
    	
    	for(int i=0; i<cantLogs; i++) {
    		Operaciones operacion = logs.dequeue();
    		System.out.println(operacion.getDetalle());
    		logs.enqueue(operacion);
    	}
    }
    

 /*** Mostrar árbol por consola ***/
 
    /**
     * Imprime por consola, en modo texto, el contenido del árbol de debate,
     * con indentación jerárquica que muestra la estructura padre-hijo.
     * Cada nodo se muestra con su id, postura, usuario y texto del argumento.
     */
    public void imprimirDebate() {
        if (arbol == null || arbol.isEmpty()) {
            System.out.println("(El árbol de debate está vacío.)");
            return;
        }

        try {
            Position<Opinion> raiz = arbol.root();
            System.out.println(formatearNodo(raiz.element()));
            imprimirHijos(raiz, "");
        } catch (Exception e) {
            System.out.println("No se pudo imprimir el árbol: " + e.getMessage());
        }
    }

    /**
     * Imprime recursivamente los hijos de 'nodo', usando 'prefijo' para
     * mantener la indentación acumulada de los niveles superiores y
     * conectores tipo "├──" / "└──" para el nivel actual.
     */
    private void imprimirHijos(Position<Opinion> nodo, String prefijo) throws InvalidPositionException {
        PositionList<Position<Opinion>> hijos = new ListaDoblementeEnlazada<Position<Opinion>>();
        for (Position<Opinion> hijo : arbol.children(nodo)) {
            hijos.addLast(hijo);
        }


        for (Position<Opinion> hijo : arbol.children(nodo)) {
            boolean esUltimo = (hijo == hijos.last().element());
            
            String conector = esUltimo ? "└── " : "├── ";
            System.out.println(prefijo + conector + formatearNodo(hijo.element()));

            String prefijoHijo = prefijo + (esUltimo ? "    " : "│   ");
            imprimirHijos(hijo, prefijoHijo);
            
        }
    }

/** Arma la línea de texto que representa a un Argumento en la impresión. */
    private String formatearNodo(Opinion a) {
        if (a == null) {
            return "(argumento nulo)";
        }
        return "[" + a.getIdArgumento() + "] (" + a.getPostura() + ") "
                + "usuario=" + a.getUsuario().getId() + " -> " + a.getTexto();
    }
}


