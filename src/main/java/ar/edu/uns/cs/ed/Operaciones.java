package ar.edu.uns.cs.ed;

public class Operaciones {
    int operacion; // 1: CREAR_DEBATE, 2: POSTEAR
    String detalle; // Detalle de la operación, como el id del argumento o el id del usuario. El texto de la operación se almacena en mayúsculas para mantener consistencia. Por ejemplo: "CREAR_DEBATE:" + idRaiz.trim()
    String id; // Identificador del argumento o de root en caso de ser el tema raíz. 

    Operaciones() {    	
    }
    
    Operaciones(int operacion, String detalle, String id){
    	this.operacion= operacion;
    	this.detalle = detalle;
    	this.id = id;
    }
    
    public void setOperacion(int operacion){
        this.operacion = operacion;
    }

    public void setDetalle(String detalle){
        this.detalle = detalle;
    }

    public void setId(String id){
        this.id = id;
    }

    public int getOperacion(){
        return this.operacion;
    }

    public String getDetalle(){
        return this.detalle;
    }

    public String getId(){
        return this.id;
    }


   
    
}
