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
    	arbol = new Arbol<Opinion>();														// Arbol de Opinones
    	historial = new PilaEnlazada<Operaciones>(); 										// Historial de operacioens que se pueden deshacer
        posicionesOpiniones = new MapeoConHashAbierto<String,Position<Opinion>>();  		// Guarda la posicion de las opniones en base a su ID
        opinionesPorUsuario = new DiccionarioConHashAbierto<String,Position<Opinion>>();	// Guarda la posicion de las opiniones por ID de usuario				
        logs = new ColaConNodo<Operaciones>();												// Logs de operaciones. No se pueden deshacer.
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
		
		if(operacion < 1 || operacion > 8)
			throw new IllegalArgumentException("Operacion invalida.");
		
		if(detalle ==  null || idArgumento == null)
			throw new IllegalArgumentException("El detalle y el idArgumento no puedens ser nulos.");
		
		detalle = detalle.trim();
		idArgumento = idArgumento.trim();    	
		
		if(detalle.equals("") || idArgumento.equals(""))
			throw new IllegalArgumentException("El detalle y el idArgumento no puedens ser vacios.");
		
		//En el historial solo se guardan crear_debate, postear y eiminar posteo
		if(operacion < 4) {
			historial.push(new Operaciones(operacion, detalle, idArgumento));
		}
		
		//Siempre se guardan los logs
		logs.enqueue(new Operaciones(operacion,detalle,idArgumento));
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

    	// La operacion es ELIMINAR_POSTEO no se puede deshacer ERROR
    	if(historial.top().getOperacion() == 3)
    		throw new IllegalStateException("No se pudo deshacer la operación POSTEAR.");
    	
    	Operaciones ultimaOperacion = historial.pop();
    	String texto = null;
    	
    	int operacion = ultimaOperacion.getOperacion();
    	
    	// la operacion es POSTEAR se puede deshacer
    	if(operacion == 2) {
    		String idPosOpinion = ultimaOperacion.getId();
    		String idUsuario = posicionesOpiniones.get(idPosOpinion).element().getUsuario().getId(); // getIdUsuarioOperaciones(ultimaOperacion);
    		texto = ultimaOperacion.getDetalle();
    		
            eliminarPosteo(idUsuario, idPosOpinion);
            historial.pop();	//Elimina el ultimo registro de posteo eliminado para pasar el tester
    	
        // la operacion es CREAR_DEBATE se puede deshacer
    	}else if(operacion == 1) {	    		
    		clearDebate();    		
    	}    	
    	
    	return texto;
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
    	
    	if(arbol.isEmpty())//c1
    		throw new IllegalStateException("No existe un debate en curso.");
    	
    	if(usr == null)//c2
    		throw new IllegalArgumentException("El usuario no puede ser nulo.");
    	
    	if(idNuevo == null || texto == null || idPadre == null)//c3
    		throw new IllegalArgumentException("No pueden haber parametros nulos.");
    	
    	idPadre = idPadre.trim();//c4
    	texto = texto.trim(); //c5
    	idNuevo = idNuevo.trim(); //c6
    	postura = postura.trim().toUpperCase();//c7

    	if(idNuevo.equals("") || texto.equals("") || idPadre.equals(""))//c8
    		throw new IllegalArgumentException("No pueden haber parametros vacios.");
    	
    	if(!postura.equals("AFAVOR") && !postura.equals("ENCONTRA"))//c9	// Las posturas estan seteadas asi en el tester
    		throw new IllegalArgumentException("La postura no es valida.");
    	
    	if(posicionesOpiniones.get(idNuevo) != null)//c10
    		throw new IllegalArgumentException("Ya existe un argumento con ese ID.");
    	
    	Position<Opinion> posOpinonPadre = posicionesOpiniones.get(idPadre);//c11
    	if(posOpinonPadre == null)
    		throw new IllegalArgumentException("No existe una opinon con el IDPadre pasado como parametro.");
    	
    	Opinion nuevaOpinon = new Opinion(idNuevo, texto, postura, usr);//c12
    	Position<Opinion> posOpinionNueva = arbol.addLastChild(posOpinonPadre, nuevaOpinon);//c13
    	
    	posicionesOpiniones.put(idNuevo, posOpinionNueva);//c14
    	opinionesPorUsuario.insert(usr.getId(), posOpinionNueva);	//c15
    	
    	String detalle = "POSTEAR:" + usr.getId() + ":" + idNuevo + ":" + idPadre + ":" + texto;//c16
    	registrarOperacion(2, detalle, idNuevo);//c17
    	
    }//T(c)=c1+c2+c3+c4+c5+c6+c7+c8+c9+c10+c11+c12+c13+c14+c15+c16+c17=0(T(c))=O(1)
    

    // Elimina un posteo y todo el subárbol.
    public void eliminarPosteo(String idUsuario, String idPosteo) {
    //TODO: Implementar la funcionalidad de eliminar un posteo y todo su subárbol en el debate.
    	//Comprobaciones
    	
    	if(idUsuario == null || idPosteo == null)//c1
    		throw new IllegalArgumentException("Los IDs no pueden ser nulos.");
    	
    	idPosteo = idPosteo.trim(); //c2
    	idUsuario = idUsuario.trim(); //c3
    	
    	if(idUsuario.equals("") || idPosteo.equals(""))//c4
        	throw new IllegalArgumentException("Los IDs no pueden ser vacios.");
    	
        Position<Opinion> posPosteoAEliminar = posicionesOpiniones.get(idPosteo);//c5
        
        if(posPosteoAEliminar == null)//c6
        	throw new IllegalArgumentException("No existe un posteo con esa ID.");
        
        if(posPosteoAEliminar.element().getUsuario().getId() != idUsuario)//c8
        	throw new IllegalStateException("El usuario pasado por parametro no es propietario del posteo.");
        
        //Obtiene informacion para guardar el registro
        Position<Opinion> posPosteoPadre = null;//c9
        String idPosPosteoPadre = null;//c10
        if(arbol.root() != posPosteoAEliminar){//c11 
        	posPosteoPadre = arbol.parent(posPosteoAEliminar);//c12
        	idPosPosteoPadre = posPosteoPadre.element().getIdArgumento();//c13
        }
        String texto = posPosteoAEliminar.element().getTexto();//c14
        
        //Elimina el posteo
        elimnarSubarbolPostorden(posPosteoAEliminar,idUsuario,idPosteo);//m
        
        //Guarda la operacion realizada en el historial
        String detalle = "ELIMINAR_POSTEO:" + idUsuario + ":" + idPosteo + ":" + idPosPosteoPadre + ":" + texto;//c15
    	registrarOperacion(3, detalle, idPosteo);//c16
    	
    }//T(m)=c1+c2+c3+c4+c5+c6+c7+c8+c9+c10+c11+c12+c13+c14+m+c15+c16=O(T(m))=O(m)
    
    private void elimnarSubarbolPostorden(Position<Opinion> posOpinion, String idUsuario, String idPosteo) {    	
    	//Recorrido
    	for(Position<Opinion> hijo : arbol.children(posOpinion)) {//m*(c1+c2+c3)
    		String idPostHijo = hijo.element().getIdArgumento();//c1
    		String idUsuarioHijo = hijo.element().getUsuario().getId();//c2
    		
    		elimnarSubarbolPostorden(hijo, idUsuarioHijo, idPostHijo);//c3
    	}
    	
    	//Visita
    	opinionesPorUsuario.remove(new Entrada(idUsuario, posOpinion));//c4
    	posicionesOpiniones.remove(idPosteo);//c5
    	
    	arbol.removeNode(posOpinion);//c6	
    	
    }//T(m)=m*(c1+c2+c3)+c5+c6)=O(T(m))=O(m)

    //obtiene las posiciones en postorden.
    //private void obtenerIdsYPosicionesSubarbol(Position<Opinion> nodo, PositionList<String> ids, PositionList<Position<Opinion>> posiciones) {
    //TODO: Implementar la funcionalidad de obtener los ids y posiciones de un subárbol en postorden.
    //}

    //limpia todo el debate, dejando la estructura vacía.   
    public void clearDebate() {
    //TODO: Implementar la funcionalidad de limpiar todo el debate, dejando la estructura vacía.
    	arbol = new Arbol<Opinion>();														
    	historial = new PilaEnlazada<Operaciones>(); 										
        posicionesOpiniones = new MapeoConHashAbierto<String,Position<Opinion>>();  		
        opinionesPorUsuario = new DiccionarioConHashAbierto<String,Position<Opinion>>();					
        logs = new ColaConNodo<Operaciones>();												
       
        String texto = "CLEAR_DEBATE:" + "::::" + "Limpiado de debate";
        registrarOperacion(4,texto,"-");
    }

    //consultar por usuario, devuelve una lista de argumentos que pertenecen a ese usuario.
    public PositionList<Opinion> consultarArgumentosPorUsuario(String idUsuario) {
    //TODO: Implementar la funcionalidad de consultar los argumentos de un usuario específico en el debate.
    	
    	if(idUsuario == null)//c1
        	throw new IllegalArgumentException("El ID de usuario no puede ser nulo.");
    	
    	idUsuario = idUsuario.trim();//c2
    	
    	if(idUsuario.equals(""))//c3
        	throw new IllegalArgumentException("El ID de usuario no puede ser vacios..");
    	
    	PositionList<Opinion> ArgumentosPorUsuario = new ListaDoblementeEnlazada<Opinion>();//c4
    	for(Entry<String,Position<Opinion>> entrada : opinionesPorUsuario.findAll(idUsuario)) {//c5
    		ArgumentosPorUsuario.addLast(entrada.getValue().element());//c6
    	}
    	
    	 String texto = "CONSULTA_ARGUMENTO_POR_USUARIO:" + "::::" + "Consulta de argumento Por usuario";//c7
         registrarOperacion(5,texto,"-");//c8
    	
    	return ArgumentosPorUsuario;//c9
    	
    }//T(c)=c1+c2+c3+c4+c5+c6+c7+c8+c9=O(T(c))=1
    
    /**
     * Calcula la polarización de todos los nodos del debate.
     * La polarización se define como la cantidad de argumentos a favor y en contra que existen en el subárbol que tiene como raíz cada nodo.
     * @throws IllegalStateException si no hay un debate activo.
     * @return Un diccionario cuya clave es el ID del nodo y el valor es un diccionario con la polarización: 
     * "AFAVOR" y "ENCONTRA" y sus respectivos conteos.
     */
    public DiccionarioConHashAbierto<String,DiccionarioConHashAbierto<String,Integer>> calcularPolarizacionDebate(){
        //TODO
    	
    	if(arbol.isEmpty())//c1
    		throw new IllegalStateException("No hay un debate activo");
    	
    	DiccionarioConHashAbierto<String,DiccionarioConHashAbierto<String,Integer>> dictPolarizacion = new DiccionarioConHashAbierto<String,DiccionarioConHashAbierto<String,Integer>>();//c2
    	postordenPolarizacionTotal(arbol.root(), dictPolarizacion); //m
    	
    	 String texto = "CONSULTA_POLARIZACION_DEBATE:" + "::::" + "Consulta de polarizacion de debate general";//c3
         registrarOperacion(6,texto,"-");//c4
    	
    	return dictPolarizacion;//c5
    	
    }//T(m)=c1+c2+m+c3+c4+c5=O(T(m))=0(m)
    
    private void postordenPolarizacionTotal(Position<Opinion> posOpinion, DiccionarioConHashAbierto<String,DiccionarioConHashAbierto<String,Integer>> dictPolarizacion) {
    	int contAFavor = 0;//c1
    	int contEnContra = 0;//c2
    	//Recorrido
    	for(Position<Opinion> hijo : arbol.children(posOpinion)) {//m
    		postordenPolarizacionTotal(hijo, dictPolarizacion);//c3
    		
    		String idHijo = hijo.element().getIdArgumento();//c4
    		contAFavor += dictPolarizacion.find(idHijo).getValue().find("AFAVOR").getValue();//c5
        	contEnContra += dictPolarizacion.find(idHijo).getValue().find("ENCONTRA").getValue();//c6
    	}
    	
    	//Visita
    	if(posOpinion.element().getPostura() == "AFAVOR") {//c7
    		contAFavor += 1;//c8
    	}else if(posOpinion.element().getPostura() == "ENCONTRA") {
    		contEnContra += 1;
    	}
    	DiccionarioConHashAbierto<String,Integer> dictPolarizacionPosActual = new DiccionarioConHashAbierto<String,Integer>();//c9
    	dictPolarizacionPosActual.insert("AFAVOR",contAFavor);//c10
    	dictPolarizacionPosActual.insert("ENCONTRA",contEnContra);//c11
    	
    	String idPosActual = posOpinion.element().getIdArgumento();//c12
    	dictPolarizacion.insert(idPosActual, dictPolarizacionPosActual);//c13	
    	
    }//T(m)=c1+C2+m*(C3+C4+C5+C6)+C7+C8+C9+C10+C11+C12+C13=O(T(m))=O(m)	

     /**
    * Calcula la polarización de un nodo específico en el árbol de debate.
    * La polarización se define como la cantidad de argumentos a favor y en contra que existen en el subárbol que tiene como raíz el nodo especificado por idNodo.
    * @throws IllegalStateException si no hay un debate activo. 
    * @param idNodo : Identificador del nodo para el cual se desea calcular la polarización.
    * @return Un DiccionarioHashAbierto<String,Integer> que contiene la polarización del nodo, con las claves "AFAVOR" y "ENCONTRA" y sus respectivos conteos.
    */
    public DiccionarioConHashAbierto<String,Integer> calcularPolarizacionNodo(String idNodo){
        //TODO
    	
    	if(arbol.isEmpty())//c1
    		throw new IllegalStateException("No hay un debate activo");
    	
    	if(idNodo == null)//c2
    		throw new IllegalArgumentException("El ID del nodo no puede ser nulo.");
    		
    	idNodo = idNodo.trim();//c3
    		
    	if(idNodo.equals(""))//c4
    		throw new IllegalArgumentException("El ID del nodo no puede estar vacio.");
    	
    	
    	Position<Opinion> posOpinion = posicionesOpiniones.get(idNodo);//c5
    	
    	if(posOpinion == null)//c6
    		throw new IllegalArgumentException("El ID pasado por paramtero no peretenece a ninguna opinion de este debate.");
    	
    	DiccionarioConHashAbierto<String,Integer> dictPolarizacion = new DiccionarioConHashAbierto<String,Integer>();//c7

    	dictPolarizacion.insert("AFAVOR", 0);//c8
    	dictPolarizacion.insert("ENCONTRA", 0);//c9
    	
    	preordenPolarizacion(posOpinion, dictPolarizacion);//n
    	
    	 String texto = "CONSULTA_POLARIZACION_NODO:" + ":::::" + "Consulta de polarizacion de Nodo";//c10
         registrarOperacion(7,texto,"-");//c11
    	
    	return dictPolarizacion;//c12
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
    		logs.enqueue(operacion);
    	}
    	 String texto = "MOSTRAR_OPERACIONES:" + ":::::" + "Mostrar Operaciones Realizadas";
         registrarOperacion(8,texto,"-");    	
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

