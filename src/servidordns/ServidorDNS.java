/*
 ESTE ES PAPI
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
import java.io.*;

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
    private int aa;

    ////////////////////////////////////////////////////////////
    //Constructor///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    public ServidorDNS() {
        try {
            System.out.println("Leyendo archivo");
            masterFile = new ControladorMasterFile();
            masterFile.mostrarDominios();
        } catch (IOException event) {
            System.out.println("Error: [" + event.getMessage() + "]");
            System.exit(1);
        }

        byte[] mensajeRecibido = new byte[1024];
        try {
            socket = new DatagramSocket(PUERTO);
            System.out.println("Puerto 53 en escucha");

            while (true) {
                DatagramPacket solicitud = new DatagramPacket(mensajeRecibido, 1024);
                socket.receive(solicitud);
                System.out.print("Mensaje recibido desde: ");
                System.out.print(solicitud.getAddress().getHostAddress() + ": " + solicitud.getPort());
                System.out.println("\nTamaño del mensaje: " + solicitud.getLength());

                Consulta consulta = new Consulta(solicitud.getData());
                System.out.println("id consulta: " + consulta.getId());
                System.out.println("Nombre del host consulta: " + consulta.getNombreHost());
                //PONER EL IF E IPRIMIR LA DIRECCIÓN
                //IMPRIMIR DIRECCIÓN
                //SI LA RESPUESTA VIENE DEL MASTERFILE ES AUTORITATIVA 
                InetAddress ipSolucion = busquedaMasterFile(consulta.getNombreHost(), solicitud);
                Respuesta respuesta = new Respuesta(consulta.getId(), this.aa, consulta.getNombreHost(), consulta.getTipo(), consulta.getClase(), ipSolucion);

                DatagramPacket paqueteRespuesta;
                paqueteRespuesta = new DatagramPacket(respuesta.getDatos(), respuesta.getLongitud(), solicitud.getAddress(), solicitud.getPort());
//                paqueteRespuesta = new DatagramPacket(new byte[0], 0, solicitud.getAddress(), solicitud.getPort());
                socket.send(paqueteRespuesta);
                System.out.println("Mandando paquete....");
            }
        } catch (SocketException ex) {
            Logger.getLogger(ServidorDNS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServidorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ////////////////////////////////////////////////////////////
    //METODOS///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    private InetAddress busquedaMasterFile(String nombreHost, DatagramPacket solicitud) {
        boolean verdadero = false;
        InetAddress direccion = null;
        String sentencia = "";
        String buscaAux = "";
        String busca = nombreHost;
        final String Nombre_Archivo = "MasterFile.txt";
        File aFind = new File(Nombre_Archivo);
        String texto = "";

        String buscav[] = busca.split("www.");

        for (String buscav1 : buscav) {
            buscaAux = buscaAux + buscav1;
        }
        if (aFind.exists()) {
            try {
                BufferedReader bf = new BufferedReader(new FileReader(aFind));
                String temp = " ";
                String bfread;

                while ((bfread = bf.readLine()) != null) {
                    temp = temp + bfread;
                }
                texto = temp;

                String separador[] = texto.split(" ");
                int i = 0;
                while (verdadero == false) {

                    i++;
                    if (separador[i].equals(buscaAux)) {
                        verdadero = true;
                        System.out.print("Mensaje recibido desde: ");
                        System.out.print(solicitud.getAddress().getHostAddress() + ": " + solicitud.getPort());
                        System.out.println("\nTamaÃ±o del mensaje: " + solicitud.getLength());
                        System.out.println("Respuesta autoritativa para " + separador[i] + " : " + separador[i + 9]);
                     
                        String ipd = separador[i + 9];
                        String ip[] = ipd.split("\\.");
                        int ip1 = Integer.parseInt(ip[0]);
                        int ip2 = Integer.parseInt(ip[1]);
                        int ip3 = Integer.parseInt(ip[2]);
                        int ip4 = Integer.parseInt(ip[3]);
                        direccion = InetAddress.getByAddress(new byte[]{
                            (byte) ip1, (byte) ip2, (byte) ip3, (byte) ip4}
                        );

                    }

                }
            } catch (Exception e) {
                System.err.println(" ");
            }
            this.aa = 1;
        }

        if (verdadero == false) {

            /// Conectar con DNS de Google 8.8.8.8
            direccion = foreignSolver(direccion, sentencia, nombreHost);
            this.aa = 0;
        }
        return direccion;
    }

    private InetAddress foreignSolver(InetAddress direccion, String sentencia, String nombreHost) {
        try {
            direccion = InetAddress.getByName(nombreHost);
            sentencia = nombreHost + " : " + direccion.getHostAddress();
            masterFile.adicionarHost(nombreHost, direccion);
        } catch (UnknownHostException event) { // No se encontro
            sentencia = "Nombre de host: [" + nombreHost + "] no se encuentra";
        }
        return direccion;
    }

    public static void main(String[] args) {
        ServidorDNS nameServer = new ServidorDNS();

    }

}
