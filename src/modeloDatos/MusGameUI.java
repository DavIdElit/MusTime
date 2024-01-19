package modeloDatos;

import modeloNegocio.Jugador;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class MusGameUI extends JFrame {
    private JTextPane chatPane;
    private JTextField messageField;
    private JButton musYesButton, musNoButton, nextRoundButton, envidarButton;
    private JSlider apuestaSlider;
    private JLabel apuestaLabel=new JLabel();
    public MusGameUI(Jugador jugador) {
        try {
            Socket socket = new Socket("localhost", 6666);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            PrintStream ps = new PrintStream(socket.getOutputStream());
            jugador.setSocket(socket);
            ps.println(jugador.getNombre());     //Lo enviamos al servidor para que forme los equipos

            setTitle("Mus Game");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // Chat Panel
            chatPane = new JTextPane();
            chatPane.setEditable(false);
            JScrollPane chatScrollPane = new JScrollPane(chatPane);
            add(chatScrollPane, BorderLayout.CENTER);

            // Message Input Panel
            JPanel messagePanel = new JPanel(new BorderLayout());
            messageField = new JTextField();
            JButton sendMessageButton = new JButton("Enviar carta descartada");
            sendMessageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String carta=messageField.getText();
                    try{
                        if(Integer.parseInt(carta)<1 ||Integer.parseInt(carta)>5){
                            chatPane.setText(chatPane.getText()+"\n"+"Numero invalido");
                        }else {
                            if(Integer.parseInt(carta)==jugador.getMano().getMano().size()+1){
                                chatPane.setText(chatPane.getText()+"\n"+"No tienes esa carta");
                            }else {
                                ps.println(messageField.getText());
                            }
                        }
                    }catch (NumberFormatException nfe){
                        chatPane.setText(chatPane.getText()+"\n"+"Numero invalido");
                    }
                }
            });
            messagePanel.add(messageField, BorderLayout.CENTER);
            messagePanel.add(sendMessageButton, BorderLayout.EAST);

            messagePanel.setVisible(false);
            add(messagePanel, BorderLayout.SOUTH);

            // Game Controls Panel
            JPanel gameControlsPanel = new JPanel(new FlowLayout());
            musYesButton = new JButton("Mus Sí");
            musNoButton = new JButton("Mus No");
            nextRoundButton = new JButton("Pasar/Rechazar (0)");
            envidarButton = new JButton("Envidar/Aceptar (2)");

            apuestaSlider = new JSlider(JSlider.HORIZONTAL, 3, 25, 3);
            apuestaSlider.setMajorTickSpacing(20);
            apuestaSlider.setPaintTicks(true);
            apuestaSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int apuestaValue = apuestaSlider.getValue();
                    apuestaLabel.setText("Apuesta actual: " + apuestaValue);
                    // Puedes hacer algo con el valor de la apuesta, como mostrarlo en un label
                }
            });

            // Botón para enviar la apuesta al servidor
            JButton enviarApuestaButton = new JButton("Subir Apuesta");
            musYesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ps.println("si");

                }
            });
            musNoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ps.println("no");

                }
            });
            nextRoundButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ps.println("0");

                }
            });
            envidarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int apuestaValue = apuestaSlider.getValue();
                    ps.println("2");

                }
            });
            enviarApuestaButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int apuestaValue = apuestaSlider.getValue();
                    ps.println(apuestaValue);

                }
            });
            JLabel apuestaLab=new JLabel("Apuesta");

            this.musYesButton.setVisible(false);
            this.musNoButton.setVisible(false);
            this.nextRoundButton.setVisible(false);
            this.envidarButton.setVisible(false);
            enviarApuestaButton.setVisible(false);
            this.apuestaSlider.setVisible(false);
            apuestaLab.setVisible(false);
            this.apuestaLabel.setVisible(false);

            gameControlsPanel.add(musYesButton);
            gameControlsPanel.add(musNoButton);
            gameControlsPanel.add(nextRoundButton);
            gameControlsPanel.add(envidarButton);
            gameControlsPanel.add(apuestaLab);
            gameControlsPanel.add(apuestaSlider);
            gameControlsPanel.add(apuestaLabel);
            gameControlsPanel.add(enviarApuestaButton);
            add(gameControlsPanel, BorderLayout.NORTH);

            pack();
            setLocationRelativeTo(null); // Center the frame
            setVisible(true);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    cerrarSocket(ps,br,socket);
                }
            });

            //Thread tEnviar = new Thread(new EnviarMensajeJugador(ps,musYesButton,musNoButton,nextRoundButton,envidarButton,enviarApuestaButton,apuestaSlider));
            Thread tRecibir = new Thread(new RecibirMensajeJugador(br,chatPane,musYesButton,musNoButton,nextRoundButton,envidarButton,enviarApuestaButton,apuestaSlider,apuestaLab,apuestaLabel,messagePanel,jugador));
            //tEnviar.start();
            tRecibir.start();

        }catch (IOException e){
            System.out.println("Te has desconectado de la partida");
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        // Implement the logic to send the message to the server
        // You may want to append the message to the chatPane as well
        messageField.setText("");
    }
    private void cerrarSocket(PrintStream ps,BufferedReader br,Socket socket) {
        try {
            if (socket != null) {
                ps.println("desconectar");
                ps.close();
                br.close();
                socket.close();
                JOptionPane.showMessageDialog(this, "Socket cerrado correctamente.");
                System.out.println("Socket cerrado correctamente.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}