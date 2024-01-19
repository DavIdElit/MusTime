package modeloDatos;

import modeloNegocio.Equipo;
import modeloNegocio.Partida;

import java.io.IOException;

public class Servidor {

    public static void main(String[] args) throws IOException {
        Equipo e1 = new Equipo("Equipo1");
        Equipo e2 = new Equipo("Equipo2");
        Partida p = new Partida(e1,e2);
        p.iniciarServidor();
      
    }




}
