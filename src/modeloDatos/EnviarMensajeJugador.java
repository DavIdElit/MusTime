package modeloDatos;

import javax.swing.*;
import java.io.PrintStream;
import java.util.Scanner;

public class EnviarMensajeJugador implements Runnable{
    private PrintStream ps;
    private JButton musYesButton, musNoButton, nextRoundButton, envidarButton,enviarApuestaButton;
    private JSlider apuestaSlider;

    public EnviarMensajeJugador(PrintStream ps, JButton musYesButton, JButton musNoButton, JButton nextRoundButton, JButton envidarButton, JButton enviarApuestaButton, JSlider apuestaSlider) {
        this.ps = ps;
        this.musYesButton = musYesButton;
        this.musNoButton = musNoButton;
        this.nextRoundButton = nextRoundButton;
        this.envidarButton = envidarButton;
        this.enviarApuestaButton = enviarApuestaButton;
        this.apuestaSlider = apuestaSlider;
    }

    @Override
    public void run() {
        Scanner sc=new Scanner(System.in);
        while (true){
            ps.println(sc.nextLine());
        }
    }
}
