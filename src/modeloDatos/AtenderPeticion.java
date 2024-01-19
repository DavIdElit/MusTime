package modeloDatos;

import modeloNegocio.Equipo;
import modeloNegocio.Jugador;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class AtenderPeticion implements Runnable{

    private Socket socket;
    private Equipo equipo1;
    private Equipo equipo2;
    private CountDownLatch c;
    private int njugador;

    public AtenderPeticion(Socket socket, Equipo equipo1, Equipo equipo2, CountDownLatch c,int i){

        this.socket = socket;
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        this.c = c;
        this.njugador=i;
    }


    @Override
    public void run() {

        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
             PrintStream ps = new PrintStream(socket.getOutputStream());

            Jugador j = new Jugador(bfr.readLine());

            if(this.equipo1.getJugadores().size() < 2){

                this.equipo1.getJugadores().add(j);
                ps.println(j.getNombre() + " perteneces al " + this.equipo1.getNombre());
            }else{

                this.equipo2.getJugadores().add(j);
                ps.println(j.getNombre() + " perteneces al " + this.equipo2.getNombre());

            }
            System.out.println("Nº de jugadores unidos: " + this.njugador);
            this.c.await();
            ps.println("Da comienzo el juego");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
