package modeloNegocio;

import java.util.ArrayList;
import java.util.List;

public class Mesa {

    /*Atributos*/

    private modeloNegocio.Baraja baraja;
    private List<modeloNegocio.Carta> monton;
    private modeloNegocio.Equipo equipo1;
    private modeloNegocio.Equipo equipo2;
    private List<modeloNegocio.Jugador> ordenMesa;


    /*Constructor*/

    public Mesa(modeloNegocio.Equipo e1, modeloNegocio.Equipo e2){
        this.baraja=new modeloNegocio.Baraja();   //Baraja con la que se va a jugar la partida
        this.monton = new ArrayList<modeloNegocio.Carta>();
        this.ordenMesa = new ArrayList<modeloNegocio.Jugador>();  //Orden que van a seguir los turnos de la partida
        this.equipo1 = e1;  //Equipos de dos jugadores
        this.equipo2 = e2;


    }

    /*Metodos*/

    public modeloNegocio.Baraja getBaraja(){
        return this.baraja;
    }
    public void nuevaBaraja() {
    	this.baraja=new Baraja();
    }
    public void cambiarPosicionesJugadores(){
        modeloNegocio.Jugador aux = this.ordenMesa.get(0);
        for(int i=0;i<this.ordenMesa.size()-1;i++){
            this.ordenMesa.set(i, this.ordenMesa.get(i + 1));
        }
        this.ordenMesa.set(3, aux);
    }
    public modeloNegocio.Equipo getEquipo1(){
        return this.equipo1;
    }

    public modeloNegocio.Equipo getEquipo2() {
        return this.equipo2;
    }

    public List<modeloNegocio.Jugador> getOrdenMesa(){return this.ordenMesa;}

    public int getOrdenJugador(modeloNegocio.Jugador j){
        return this.getOrdenMesa().indexOf(j);
    }
    public List<modeloNegocio.Carta> getMonton(){return  this.monton;}
    
    public void nuevoMonton() {
    	this.monton=new ArrayList<Carta>();
    }
    //Obtiene el equipo al que pertenece el jugador j
    public modeloNegocio.Equipo equipoJugador(modeloNegocio.Jugador jugador){

        modeloNegocio.Equipo e = new Equipo(null);

        for(modeloNegocio.Jugador j: this.equipo1.getJugadores()){

            if(j.equals(jugador)){
                e = this.equipo1;
                break;
            }
        }

        for(Jugador j: this.equipo2.getJugadores()){
            if(j.equals(jugador)){
                e= this.equipo2;
                break;
            }
        }

        return e;
    }

}
