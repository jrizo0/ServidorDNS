/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidordns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author usuario
 */
public class ServidorDNS {
    ////////////////////////////////////////////////////////////
    //Atributos/////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    private static final int PUERTO = 53;
    private DatagramSocket socket;
    private ControladorMasterFile masterFile;
    ////////////////////////////////////////////////////////////
    //Constructor///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    public ServidorDNS(){
        try {
            System.out.println("Leyendo archivo");
            masterFile = new ControladorMasterFile();
            masterFile.mostrarDominios();
        } catch (IOException event) {
            System.out.println("Error: [" + event.getMessage() + "]");
            System.exit(1);
        }
        
        byte[] mensajeRecibido = new byte[1024];
        byte[] mensajeRespuesta = new byte[1024];        
        try {
            socket = new DatagramSocket(PUERTO);
            System.out.println("Puerto 53 en escucha");
            while(true){
                DatagramPacket solicitud = new DatagramPacket(mensajeRecibido, 1024);
                socket.receive(solicitud);
                System.out.print("Mensaje recibido desde: ");
                System.out.print(solicitud.getAddress().getHostAddress()+": " +solicitud.getPort());
                System.out.println("\nTamaño del mensaje: " + solicitud.getLength());
                
                Consulta consulta = new Consulta(solicitud.getData());
                System.out.println("Nombre del host consulta: " + consulta.getNombreHost());
//                DatagramPacket respuesta = construirRespuesta(solicitud);
//                socket.send(respuesta)
// Prueba con foreignSolver -> sirve
                InetAddress direccion = null;
                String sentencia = "";
//                InetAddress foreignSolver = foreignSolver(direccion, sentencia, consulta.getNombreHost());
//                System.out.println("Direccion IP por foreignsolver: " + foreignSolver.getHostAddress() + "\n");
                InetAddress ipSolucion = busquedaMasterFile(consulta.getNombreHost());
                System.out.println("Direccion IP por masterfile: " + ipSolucion.getHostAddress() + "\n");
            }
        }
        catch (SocketException ex)
        {
            Logger.getLogger(ServidorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ServidorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ////////////////////////////////////////////////////////////
    //METODOS///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    private InetAddress busquedaMasterFile(String nombreHost) {
        String sentencia = "";
        InetAddress direccion = null;
        if (masterFile.existeNombreHost(nombreHost)) {
            direccion = masterFile.obtenerDireccion(nombreHost);
            sentencia = nombreHost + " : " + direccion.getHostAddress();
            System.out.println(sentencia);
        } else {
            /**
             * Conectar con DNS de Google 8.8.8.8
             */
            direccion = foreignSolver(direccion, sentencia, nombreHost);
        }
        return direccion;
    } 
    
    private InetAddress foreignSolver(InetAddress direccion, String sentencia, String nombreHost) { 
        try {
            direccion = InetAddress.getByName(nombreHost);
            sentencia = nombreHost + " : " + direccion.getHostAddress();
            masterFile.adicionarHost(nombreHost, direccion);
            //masterFile.escribirNuevoHost(nombreHost, direccion);
        } catch (UnknownHostException event) { // No se encontro
            sentencia = "Nombre de host: [" + nombreHost + "] no se encuentra";
        }
        //System.out.println(sentencia);
        return direccion;
    }
    
    

//    private DatagramPacket construirRespuesta(DatagramPacket solicitud)
//    {
//        DatagramPacket respuesta = new DatagramPacket();
//        System.out.println(solicitud.getData());
//        return respuesta;
//    }
    
       
//este es para crear la respuesta el de python
//    public byte crearBanderas(byte flags[]){
//        byte byte1 = flags[0];
//        byte byte2 = flags[1];
//        
//        String rflags = "";
//        
//        String QR = "1";
//        
//        String OPCODE = "";
//        for(int bit = 0; bit < 5; bit++){
//            OPCODE += String.valueOf((int)(byte1)&(1<<bit));
//        }
//        
//        String AA = "1";
//        String TC = "0";
//        String RD = "0";
//        String RA = "0";
//        String Z = "000";
//        String RCODE = "0000";
//        
//        return (byte)(Integer.parseInt(QR+OPCODE+AA+TC+RD))+(byte)(Integer.parseInt(RA+Z+RCODE));
//        
//    }
    
    public static void main(String[] args) {
        ServidorDNS nameServer = new ServidorDNS();
        
    }
    
}
