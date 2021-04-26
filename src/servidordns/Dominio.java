/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidordns;

import java.net.InetAddress;

/**
 *
 * @author usuario
 */
public class Dominio {
    
    private String  nombre;
    private InetAddress direccion;
    

    public String getNombreHost() {
        return nombre;
    }

    public void setNombreHost(String nombre) {
        this.nombre = nombre;
    }

    public InetAddress getDireccion() {
        return direccion;
    }

    public void setDireccion(InetAddress direccion) {
        this.direccion = direccion;
    }
}
