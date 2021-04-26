/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidordns;

/**
 *
 * @author usuario
 */
public class Consulta {
    private byte[] paquete;
    private int apuntador;
    private int ID;
    private String nombreHost;
    private int longitud;
    private int banderas;
    private int preguntas;
    private int clase; //¿¿¿¿¿¿¿Vamos a manejar varias clases o solo A??????????
    private int tipo;
    
    public Consulta(byte[] datos)
    {
        this.apuntador = 0;
        this.nombreHost = "";
        this.paquete = datos;
        this.longitud = datos.length;
        this.ID = get2bytes(); //apuntador = 2byte
        this.banderas = get2bytes(); //apuntador = 4byte
        this.preguntas = get2bytes(); //apuntador = 6byte
//se necesitan los otros campos?????

        apuntador += 6; //apuntador = 12byte(query)
        int longitudNombre = getByteActual();
       
        while (!(longitudNombre == 0)) {
            int i = 0;
            while(i < longitudNombre){
               this.nombreHost += (char)getByteActual();
               i++;
            }
            longitudNombre = getByteActual();
            if (0 < longitudNombre) {
                nombreHost = nombreHost + ".";
            }
        }
        
        clase = get2bytes();
        tipo = get2bytes();
    }
    
    ////////////////////////////////////////////////////////////
    //METODOS///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    
    private int get2bytes() {
        byte[] valores = getxBytes(2);
        return ((valores[0]) << 8) + (valores[1]);
    }
    
    private byte getByteActual() {
        return paquete[apuntador++];
    }
    
    private byte[] getxBytes(int x) {
        byte[] arreglo = new byte[x];
        int i = 0;
        while(i < x){
            arreglo[i] = getByteActual();
            i++;
        }
        return arreglo;
    }
    
    ////////////////////////////////////////////////////////////
    //GETTER & SETTER///////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    public byte[] getConsulta() {
        return paquete;
    }

    public void setConsulta(byte[] consulta) {
        this.paquete = consulta;
    }

    public int getId() {
        return ID;
    }

    public void setId(int id) {
        this.ID = id;
    }

    public String getNombreHost() {
        return nombreHost;
    }

    public void setNombreHost(String informacion) {
        this.nombreHost = informacion;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    public int getBanderas() {
        return banderas;
    }

    public void setBanderas(int banderas) {
        this.banderas = banderas;
    }

    public int getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(int preguntas) {
        this.preguntas = preguntas;
    }

    public int getApuntador() {
        return apuntador;
    }

    public void setApuntador(int apuntador) {
        this.apuntador = apuntador;
    }

    public int getClase() {
        return clase;
    }

    public void setClase(int clase) {
        this.clase = clase;
    }
 
}
