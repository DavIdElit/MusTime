package modeloNegocio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*Descripción: Baraja española que va a contener 36 cartas(objeto) ya que se descartan los doses*/
public class Baraja {

    /*Atributos*/
    private ArrayList<modeloNegocio.Carta> baraja;
    private int numero_cartas;
    private Random random = new Random();

    /*Constructor*/
    public Baraja(){
        this.baraja = new ArrayList<modeloNegocio.Carta>();
        modeloNegocio.Palo palo[]={modeloNegocio.Palo.bastos, modeloNegocio.Palo.copas, modeloNegocio.Palo.espadas, Palo.oros};

        for(int i=0;i<4;i++){
            modeloNegocio.Carta carta_excepcion=new modeloNegocio.Carta(1,palo[i]);
            this.baraja.add(carta_excepcion);

            for(int n=3;n<8;n++){
                modeloNegocio.Carta carta=new modeloNegocio.Carta(n,palo[i]);
                this.baraja.add(carta);
            }

            for(int n=10;n<13;n++){
                modeloNegocio.Carta carta=new modeloNegocio.Carta(n,palo[i]);
                this.baraja.add(carta);
            }
        }

        this.numero_cartas = 36;
    }

    /*Metodos*/

    //Revuelve las cartas de la baraja para que estén dispuestas de forma aleatoria
    public void barajar(){

        for(int i=0;i<this.baraja.size();i++){
            int r = random.nextInt(this.baraja.size() -1);
            modeloNegocio.Carta c1 = this.baraja.get(i);
            modeloNegocio.Carta c2 = this.baraja.get(r);

            this.baraja.set(r, c1);
            this.baraja.set(i, c2);
        }
    }

    //Devuelve el numero de cartas restantes en la baraja
    public int cartasRestantes(){
        return this.baraja.size();
    }

    public modeloNegocio.Carta sacarCarta(){
        modeloNegocio.Carta c;
        c=this.baraja.get(this.baraja.size()-1);
        this.baraja.remove(this.baraja.size()-1);
        return c;
    }

    public List<Carta> getBaraja(){return this.baraja;}

}
