package modeloDatos;

import modeloNegocio.Carta;
import modeloNegocio.Jugador;
import modeloNegocio.Mano;
import modeloNegocio.Palo;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RecibirMensajeJugador implements Runnable{
    private BufferedReader bfr;
    private JTextPane chat;
    private JButton musYesButton, musNoButton, nextRoundButton, envidarButton,enviarApuestaButton;
    private JSlider apuestaSlider;

    private JLabel apuestaLab,apuestaLabel;

    private JPanel messagePanel;
    private Jugador jugador;
    public RecibirMensajeJugador(BufferedReader bfr,JTextPane chat,JButton musYesButton, JButton musNoButton, JButton nextRoundButton, JButton envidarButton, JButton enviarApuestaButton, JSlider apuestaSlider,JLabel apuestaLab,JLabel apuestaLabel,JPanel messagePanel,Jugador jugador){
        this.bfr=bfr;
        this.chat=chat;
        this.musYesButton = musYesButton;
        this.musNoButton = musNoButton;
        this.nextRoundButton = nextRoundButton;
        this.envidarButton = envidarButton;
        this.enviarApuestaButton = enviarApuestaButton;
        this.apuestaSlider = apuestaSlider;
        this.apuestaLab=apuestaLab;
        this.apuestaLabel=apuestaLabel;
        this.jugador=jugador;
        this.messagePanel=messagePanel;
    }
    @Override
    public void run() {
        while (true){
            try {
                String mensaje= bfr.readLine();
                chat.setText(chat.getText()+"\n"+mensaje);
                if(mensaje.contains("Mano de "+jugador.getNombre())){
                    String cartasSubstring = mensaje.substring(mensaje.indexOf(":") + 2);
                    System.out.println(cartasSubstring);
                    // Dividir las cartas basadas en el espacio entre cada carta
                    String[] cartasArray = cartasSubstring.split("\\s+");
                    System.out.println(Arrays.toString(cartasArray));
                    // Lista para almacenar las cartas
                    ArrayList<Carta> listaCartas = new ArrayList<>();

                    // Procesar cada carta y agregarla a la lista
                    for (String cartaStr : cartasArray) {
                        // Eliminar corchetes y dividir la cadena en número y palo
                        String[] partes = cartaStr.replaceAll("[\\[\\]]", "").split(",");
                        int numero = Integer.parseInt(partes[0].split(":")[1]);  // El número ahora está en la posición 1
                        Palo palo = Palo.valueOf(partes[1].toLowerCase()); // El palo ahora está en la posición 2

                        // Crear instancia de Carta y agregarla a la lista
                        Carta carta = new Carta(numero, palo);
                        listaCartas.add(carta);
                    }

                    // Imprimir la lista de cartas
                    for (Carta carta : listaCartas) {
                        System.out.println(carta.mostrarCarta());
                    }
                    Mano mano=new Mano();
                    mano.setMano(listaCartas);
                    jugador.setMano(mano);
                    System.out.println(jugador.mostrarMano());
                }
                if(mensaje.contains(jugador.getNombre())&&mensaje.contains("quieres hacer mus")){
                    this.musYesButton.setVisible(true);
                    this.musNoButton.setVisible(true);
                    this.messagePanel.setVisible(false);
                }else if(mensaje.contains("elige una carta")){
                    this.musYesButton.setVisible(false);
                    this.musNoButton.setVisible(false);
                    this.messagePanel.setVisible(true);
                }else if(mensaje.contains("Fase de Grandes")){
                    this.musYesButton.setVisible(false);
                    this.musNoButton.setVisible(false);
                    this.messagePanel.setVisible(false);
                    this.nextRoundButton.setVisible(true);
                    this.envidarButton.setVisible(true);
                    this.enviarApuestaButton.setVisible(true);
                    this.apuestaSlider.setVisible(true);
                    this.apuestaLab.setVisible(true);
                    this.apuestaLabel.setVisible(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
