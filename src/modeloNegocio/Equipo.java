package modeloNegocio;

import java.util.ArrayList;
import java.util.List;

public class Equipo {

    //Atributos
    private String nombre;
    private List<modeloNegocio.Jugador> jugadores;
    private int puntos;
    //Constructores
    public Equipo(String s){
        this.jugadores = new ArrayList<modeloNegocio.Jugador>(2);
        this.puntos=0;
        this.nombre=s;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos(){
        return this.puntos;
    }

    public void setPuntos(int puntos){
        this.puntos=this.puntos+puntos;
    }

    public List<Jugador> getJugadores(){
        return this.jugadores;
    }

}
