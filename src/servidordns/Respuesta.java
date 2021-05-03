/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidordns;

import java.net.InetAddress;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 *
 * @author usuario
 */
public class Respuesta {
    
    private ArrayList<Byte> datos;
    
    public Respuesta(int id, int aa, String nHost, int tipo, int clase, InetAddress direccion){
        datos = new ArrayList<>();
        agregarHeader(id, aa);
        agregarMensajeR(nHost, tipo, clase, direccion);
    }
    
    public void agregarEntero(int valor) {
        agregarBytes(new byte[]{(byte) ((valor >> 8)), (byte) (valor & 0xff)});
    }
    
    public void agregarEntero2(int valor) {
        agregarBytes(new byte[]{(byte) ((valor >> 16)), (byte) ((valor >> 8) & 0xff), (byte) (valor & 0xff)});
    }
    
    public void agregarByte(int numeroBytes) {
        agregarByte((byte) numeroBytes);
    }
    public void agregarByte(byte bait) {
        datos.add(bait);
    }
    
    public void agregarBytes(byte[] bytes){
        int i = 0;
        while(i < bytes.length){
            agregarByte(bytes[i]);
            i++;
        }
    }
    
    public void agregarHeader(int id, int aa){
        
        agregarEntero(id); //transaction id

        //flags primer byte
        if (aa!=0){
            agregarByte(0x84); //si es autoritativa
        }
        else{
            agregarByte(0x80); //si es no autoritativa
        }
        //flags segundo byte
        agregarByte(0x00);
        
        //qdcount
        agregarEntero(1);
        
        //ancount
        agregarEntero(1);
        
        //nscount
        agregarEntero(1);
        
        //arcount
        agregarEntero(0);
        
    }
    
    public void agregarMensajeR (String nHost, int tipo, int clase, InetAddress direccion){
        
        //Domain Name
        StringTokenizer delimitador = new StringTokenizer(nHost, ".");
        while (delimitador.hasMoreTokens()) {
            String capaAct = delimitador.nextToken();
            agregarByte(capaAct.length());
            agregarBytes(capaAct.getBytes());
        }
        agregarByte(0x00);
        agregarEntero(tipo); //tipo
        agregarEntero(clase); //clase
        
        agregarByte(0xc0);
        agregarByte(0x0c);
        
        agregarEntero(tipo); //tipo
        agregarEntero(clase); //clase
        
        agregarByte(0x00);
        agregarByte(0x00);
        agregarByte(0x00);
        agregarByte(0x1e); //TTL - 30 segundos
        
        agregarEntero(4); //Largo de los datos, 4 porque solo se hacen de tipo A
        agregarBytes(direccion.getAddress()); //IP del servidor
        
    }
  
    public void setDatos(ArrayList<Byte> datos) {
        this.datos = datos;
    }
    
    public int getLongitud() {
        int longitud = datos.size();
        return longitud;
    }
    public byte[] getDatos() {
        int longitdDatos = getLongitud();
        byte[] arregloBytes = new byte[longitdDatos];
        int i = 0;
        while(i < longitdDatos){
            arregloBytes[i] = datos.get(i);
            i++;
        }
        return arregloBytes;
    }
    
}
