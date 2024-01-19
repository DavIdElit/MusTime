package modeloNegocio;

//Descripción: Cada objeto carta esta formado por el palo(bastos, oros, espadas ...) y por el numero(1,3,4,5,6,7,10,11,12)
public class Carta {

    /*Atributos*/
    private int numero;
    private modeloNegocio.Palo palo;


    /*Constructor*/
    public Carta(int numero, modeloNegocio.Palo palo){
        this.numero = numero;
        this.palo = palo;
    }


    /*Metodos*/

    //Devuelve: atributo privado numero
    public int getNumero(){
        return this.numero;
    }

    //Devuelve: atributo privado palo(enum)
    public Palo getPalo(){
        return this.palo;
    }

    public String mostrarCarta(){
        //System.out.print("["+this.numero+","+this.palo+"] ");
        String s="["+this.numero+","+this.palo+"] ";
        return s;
    }
}
