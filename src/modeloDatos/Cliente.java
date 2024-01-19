package modeloDatos;

import modeloNegocio.Jugador;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;

public class Cliente {


    public static void main(String[] args) throws IOException {

        Scanner teclado = new Scanner(System.in);

         System.out.println("----MUS ONLINE----");
         System.out.println("Introduce tu nombre: ");

         Jugador j=new Jugador(teclado.nextLine());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusGameUI(j);
            }
        });
         //j.conectar();


    }
}
