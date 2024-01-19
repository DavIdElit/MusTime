package modeloNegocio;

import java.io.*;
import java.net.Socket;

public class Jugador{

    /*Atributos*/
    private String nombre;
    protected modeloNegocio.Mano mano;
    private boolean tienePares;
    private boolean tieneJuego;
    private int juego;
    private Socket socket;
    /*Constructor*/
    public Jugador(String nombre) throws IOException {
        this.nombre = nombre;
        this.mano = new modeloNegocio.Mano();
        this.tienePares=false;
        this.tieneJuego=false;
        this.juego = 0;
    }

    /*Metodos*/
    public modeloNegocio.Mano getMano(){
        return this.mano;
    }
    public void setMano() {
    	this.mano=new modeloNegocio.Mano();
    }

    public void setMano(Mano mano) {
        this.mano=mano;
    }
    public String getNombre(){return this.nombre;}

    public boolean getTienePares(){
        return this.tienePares;
    }
    public boolean getTieneJuego(){
        return this.tieneJuego;
    }
    public void setTienePares(boolean b){
        this.tienePares=b;
    }
    public void setTieneJuego(boolean b){
        this.tieneJuego=b;
    }


    //Muestra las cartas de la mano de un jugador
    public String mostrarMano(){
        int numCarta=0;
        //System.out.println("-Mano de " + this.nombre + ":" + "\r\n");
        String s= "-Mano de " + this.nombre + ": ";
        for(int i=0; i<this.mano.getMano().size(); i++){
            //System.out.print(i+1+":");
            numCarta=numCarta+1;
            s=s+numCarta+":"+this.mano.getMano().get(i).mostrarCarta();
            //this.mano.getMano().get(i).mostrarCarta();
        }
        //System.out.println("");
        s=s+"";
        return s;
    }


    public void setJuego(){
        int juegoActual = 0;
        for(int i=0;i<4;i++){

            if(this.mano.getMano().get(i).getNumero()>=10){
                juegoActual = juegoActual + 10;
            }else{
                juegoActual = juegoActual + this.mano.getMano().get(i).getNumero();
            }
        }

        this.juego=juegoActual;
    }
    public int getJuego(){
        return this.juego;
    }
    
    

   /* public void conectar() {



        try {
             socket = new Socket("localhost", 6666);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
             PrintStream ps=new PrintStream(socket.getOutputStream());

            ps.println(this.getNombre());     //Lo enviamos al servidor para que forme los equipos
            Thread tEnviar=new Thread(new EnviarMensajeJugador(ps));
            Thread tRecibir=new Thread(new RecibirMensajeJugador(bfr));
            tEnviar.start();
            tRecibir.start();
            while (true){

            }


        } catch (IOException e) {
            System.out.println("Te has desconectado de la partida");
        }

    }*/
    public String IPConectado(){
        return socket.getInetAddress().toString();
    }

    public String PuertoConectado(){
        return String.valueOf(socket.getPort());
    }

    public Socket getSocket(){
        return this.socket;
    }


    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
