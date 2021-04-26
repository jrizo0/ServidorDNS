/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidordns;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author usuario
 */
public class ControladorMasterFile {

    private ArrayList<Dominio> dominios;

    public static final String RUTAMASTERFILE = "./MasterFile.txt";
    
    public ControladorMasterFile() throws IOException {
        dominios = new ArrayList<>();
        leerArchivo();
    }
    
    private void leerArchivo() throws IOException {
        BufferedReader br = nuevoBufferLectura(ControladorMasterFile.RUTAMASTERFILE);
        String linea = null;
        String token = null;
        String nombreHost = null;
        String hostIP = null;
        String tipo = null;
        boolean estaLeyendo = false;
        while (!((linea = br.readLine()) == null)) {
            //linea = linea.trim();
            StringTokenizer lineaTokenizada = new StringTokenizer(linea, " ");
            if (linea.startsWith("$ORIGEN")) {
                estaLeyendo = false;
                lineaTokenizada.nextToken();
                token = lineaTokenizada.nextToken();
            } 
            else if (linea.startsWith("hosts")) {
                estaLeyendo = true;
            } 
            else if (estaLeyendo && (lineaTokenizada.countTokens() == 4)) {
                nombreHost = lineaTokenizada.nextToken().trim() + "." + token;
                tipo = lineaTokenizada.nextToken().trim();
                hostIP = lineaTokenizada.nextToken().trim();

                if (tipo.equals("A")) {
                    InetAddress ip = InetAddress.getByName(hostIP);
                    adicionarHost(nombreHost, ip);
                }
            } 
            else {
                estaLeyendo = false;
            }
        }
        cerrarBufferLectura(br);
    }
    
    public static void cerrarBufferLectura(BufferedReader reader) throws IOException {
        try {
            reader.close();
        } catch (IOException e) {
            throw new IOException("Error: No se pudo cerrar el buffer de lectura [" + e.getMessage() + "]");
        }
    }
    
    public void adicionarHost(String nombreHost, InetAddress direccion) {
        Dominio nuevoDominio = new Dominio();
        nuevoDominio.setDireccion(direccion);
        nuevoDominio.setNombreHost(nombreHost);
        dominios.add(nuevoDominio);
        
    }
    
    public static BufferedReader nuevoBufferLectura(String filename) throws FileNotFoundException {
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fileReader);
            return reader;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Error: No se encontro el archivo '" + filename + "' [" + e.getMessage() + "]");
        }
    }
    
    public void mostrarDominios()
    {
        for(Dominio actual : dominios)
        {
            System.out.println(actual.getDireccion() + " -> " + actual.getNombreHost());
        }
    }
    
    public boolean existeNombreHost(String nombreHost) {
        return dominios.stream().anyMatch((a) -> (a.getNombreHost().equals(nombreHost)));
    }
    
    public InetAddress obtenerDireccion(String nombreHost) {
        for (Dominio a : dominios) {
            if (a.getNombreHost().equals(nombreHost)) {
                return a.getDireccion();
            }
        }
        return null;
    }
}
