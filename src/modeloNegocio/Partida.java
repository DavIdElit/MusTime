package modeloNegocio;

import modeloDatos.AtenderPeticion;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Partida {
    private modeloNegocio.Mesa mesa;
    private int pGrandes = 1;
    private int pPequeñas = 1;
    private int pPares = 1;
    private int pJuego = 1;
    private int pPunto = 0;
    private ArrayList<Socket> sockets = new ArrayList();
    private ArrayList<PrintStream> printers = new ArrayList();
    private ArrayList<BufferedReader> readers = new ArrayList();

    /*Constructor*/
    public Partida(modeloNegocio.Equipo e1, modeloNegocio.Equipo e2){
        this.mesa = new modeloNegocio.Mesa(e1,e2);
    }

    //repartes 4 cartas a cada jugador al principio de la partida
    public void repartir(){
        int contador = 0;
        while(contador<4){

            for(int i=0; i<4; i++){

                modeloNegocio.Jugador j = this.mesa.getOrdenMesa().get(i);
                j.getMano().getMano().add(this.getMesa().getBaraja().sacarCarta());

            }
            contador++;
        }
        this.ordenarCartasJugadores();
    }

    public void repartirMus(modeloNegocio.Jugador jugador){

        int contador = 0;
        if(this.getMesa().getBaraja().cartasRestantes()<4-jugador.getMano().getMano().size()) {
            this.getMesa().getBaraja().getBaraja().addAll(this.getMesa().getMonton());
            this.getMesa().getBaraja().barajar();
        }


        while(true) {
            if (jugador.getMano().getMano().size() < 4) {
                jugador.getMano().getMano().add(this.getMesa().getBaraja().sacarCarta());
            } else {
                System.out.println(this.getMesa().getBaraja().cartasRestantes());
                break;
            }
        }
        this.ordenarCartasJugadores();

    }

    public void ordenarCartasJugadores(){
        for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()){
            j.getMano().ordenarMano();
        }
    }

    public void hacerMus() throws IOException {
        boolean decision = true;
        while(decision){
            for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
            	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                }
                printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " quieres hacer mus?: si/no");
                String respuesta = readers.get(this.getMesa().getOrdenJugador(j)).readLine();
                if(respuesta.equals("si")){
                    printers.get(this.getMesa().getOrdenJugador(j)).println("Quiero mus");
                    decision = true;
                }else if(respuesta.equals("no")){
                    printers.get(this.getMesa().getOrdenJugador(j)).println("No quiero mus");
                    decision = false;
                    break;
                }
            }
            if(decision){
                ArrayList<Carta> descarte = null;
                for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
                	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                    }
                    int carta = 0;
                    descarte = new ArrayList<>();
                    while(descarte.isEmpty() || carta!=5){
                        printers.get(this.getMesa().getOrdenJugador(j)).println(j.mostrarMano());
                        printers.get(this.getMesa().getOrdenJugador(j)).println("-----------------------");
                        printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " elige una carta a descartar, 5 para no descartar");
                        carta=Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                        if(carta !=5){
                            descarte.add(j.getMano().getMano().get(carta-1));
                            j.getMano().getMano().remove(j.getMano().getMano().get(carta-1));
                        }
                    }
                    this.getMesa().getMonton().addAll(descarte);
                }
                //Repartir nuevas cartas
                for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
                    this.repartirMus(j);
                }
            }else{
                for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()){
                    printers.get(this.getMesa().getOrdenJugador(j)).println("No hay mus");
                }
            }
        }
    }

    public int faseGrandes() throws IOException{
    	
        boolean envite = false;
        boolean subirApuesta=true;
        int cantidadJugador = 0;
        int cantidadTotal = 0;
        int apuestaSiguiente=0;
        int numJ=0;
        int puntosAcumulados=1;
        modeloNegocio.Equipo empiezaEnvite = new modeloNegocio.Equipo(null);
        
        for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()) {
        	printers.get(this.getMesa().getOrdenJugador(j)).println("--------------------Fase de Grandes------------------------");
        }

        for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
        	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
            }
            printers.get(this.getMesa().getOrdenJugador(j)).println(" -introduce cantidad a envidar(2) o subir apuesta(+2), 0 para pasar: ");
            cantidadJugador =  Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
            cantidadTotal = cantidadTotal + cantidadJugador;

            if(cantidadJugador != 0){
            	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " ha envidado " + cantidadJugador + " puntos");
                }
                empiezaEnvite = this.getMesa().equipoJugador(j);
                envite = true;
                break;

            }else{
            	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " pasa el turno");
                }
            }
        }
        if(envite){
            while(subirApuesta==true) {
                for (modeloNegocio.Jugador j : this.getMesa().getOrdenMesa()) {

                    if (!this.getMesa().equipoJugador(j).equals(empiezaEnvite)) {

                        if (numJ == 0) {//Primer jugador del equipo en decidir
                            printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Quieres rechazar el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                            apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                            switch (apuestaSiguiente) {
                                case 0:
                                    numJ++;
                                    break;
                                case 2:
                                    numJ++;
                                    break;
                                default:
                                    empiezaEnvite = this.getMesa().equipoJugador(j);
                                    cantidadTotal=cantidadTotal+apuestaSiguiente;
                                    puntosAcumulados++;
                                    numJ++;
                                    break;
                            }
                        } else if (numJ == 1) { //Segundo jugador del equipo en decidir
                            switch (apuestaSiguiente) {
                                case 0:
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero a rechazado el envite. Quieres rechazar tambien el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                    apuestaSiguiente =  Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                    if(apuestaSiguiente>2){
                                        empiezaEnvite = this.getMesa().equipoJugador(j);
                                        cantidadTotal=cantidadTotal+apuestaSiguiente;
                                        puntosAcumulados++;
                                    }
                                    break;
                                case 2:
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero a aceptado el envite. Quieres aceptarlo tambien(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                    apuestaSiguiente =Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                    if(apuestaSiguiente>2){
                                        empiezaEnvite = this.getMesa().equipoJugador(j);
                                        cantidadTotal=cantidadTotal+apuestaSiguiente;
                                        puntosAcumulados++;
                                    }
                                    break;
                                default:
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero a subido la apuesta");
                                    break;
                            }
                        }
                    }
                }
                if (apuestaSiguiente == 0) {
                    subirApuesta=false;
                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha rechazado el envite. El " + empiezaEnvite.getNombre() + " se llevan "+puntosAcumulados + " puntos");
                    }
                    empiezaEnvite.setPuntos(puntosAcumulados);
                    cantidadTotal=0;
                } else if (apuestaSiguiente == 2) {
                    subirApuesta=false;
                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha aceptado el envite. El equipo que gane se llevara "+ cantidadTotal);
                    }
                }
                else{
                   numJ=0;
                }
            }
        }else{
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Fase de grandes ha terminado: todo el mundo ha pasado");
            }
            cantidadTotal=1;
        }
        return cantidadTotal;

    }

    public int fasePequeñas() throws NumberFormatException, IOException{

    	boolean envite = false;
        boolean subirApuesta=true;
        int cantidadJugador = 0;
        int cantidadTotal = 0;
        int apuestaSiguiente=0;
        int numJ=0;
        int puntosAcumulados=1;
        modeloNegocio.Equipo empiezaEnvite = new modeloNegocio.Equipo(null);

        for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()) {
        	printers.get(this.getMesa().getOrdenJugador(j)).println("--------------------Fase de Pequeñas------------------------");
        }
        for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
        	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
            }
            printers.get(this.getMesa().getOrdenJugador(j)).println(" -introduce cantidad a envidar(2) o subir apuesta(+2), 0 para pasar: ");
            cantidadJugador =  Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
            cantidadTotal = cantidadTotal + cantidadJugador;
            if(cantidadJugador != 0){
            	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " ha envidado " + cantidadJugador + " puntos");
                }
                empiezaEnvite = this.getMesa().equipoJugador(j);
                envite = true;
                break;

            }else{
            	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " pasa el turno");
                }
            }
        }
        if(envite){
            while(subirApuesta==true) {
                for (modeloNegocio.Jugador j : this.getMesa().getOrdenMesa()) {
                    if (!this.getMesa().equipoJugador(j).equals(empiezaEnvite)) {
                        if (numJ == 0) {//Primer jugador del equipo en decidir
                        	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                            }
                            printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Quieres rechazar el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                            apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                            switch (apuestaSiguiente) {
                                case 0:
                                    numJ++;
                                    break;
                                case 2:
                                    numJ++;
                                    break;
                                default:
                                    empiezaEnvite = this.getMesa().equipoJugador(j);
                                    cantidadTotal=cantidadTotal+apuestaSiguiente;
                                    puntosAcumulados++;
                                    numJ++;
                                    break;
                            }
                        } else if (numJ == 1) { //Segundo jugador del equipo en decidir
                            switch (apuestaSiguiente) {
                                case 0:
                                	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                                    }
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha rechazado el envite. Quieres rechazar tambien el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                    apuestaSiguiente =  Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                    if(apuestaSiguiente>2){
                                        empiezaEnvite = this.getMesa().equipoJugador(j);
                                        cantidadTotal=cantidadTotal+apuestaSiguiente;
                                        puntosAcumulados++;
                                    }
                                    break;
                                case 2:
                                	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                                    }
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha aceptado el envite. Quieres aceptarlo tambien(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                    apuestaSiguiente =Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                    if(apuestaSiguiente>2){
                                        empiezaEnvite = this.getMesa().equipoJugador(j);
                                        cantidadTotal=cantidadTotal+apuestaSiguiente;
                                        puntosAcumulados++;
                                    }
                                    break;
                                default:
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha subido la apuesta");
                                    break;
                            }
                        }
                    }
                }
                if (apuestaSiguiente == 0) {
                    subirApuesta=false;
                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha rechazado el envite. El " + empiezaEnvite.getNombre() + " se llevan "+puntosAcumulados + " puntos");
                    }
                    empiezaEnvite.setPuntos(puntosAcumulados);
                    cantidadTotal=0;
                } else if (apuestaSiguiente == 2) {
                    subirApuesta=false;
                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha aceptado el envite. El equipo que gane se llevara "+ cantidadTotal);
                    }
                }
                else{
                   numJ=0;
                }
            }
        }else{
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Fase de grandes ha terminado: todo el mundo ha pasado");
            }
            cantidadTotal=1;
        }
        return cantidadTotal;
    }

    public int fasePares() throws NumberFormatException, IOException{

        boolean envite = false;
        boolean subirApuesta=true;
        int cantidadJugador = 0;
        int cantidadTotal = 0;
        int apuestaSiguiente=0;
        int numJ=0;
        int puntosAcumulados=1;
        List<modeloNegocio.Jugador> pares = new ArrayList<modeloNegocio.Jugador>();
        modeloNegocio.Equipo empiezaEnvite = new modeloNegocio.Equipo(null);

        for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()) {
        	printers.get(this.getMesa().getOrdenJugador(j)).println("--------------------Fase de Pares------------------------");
        }
        for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
        	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("¿Jugador " + j.getNombre() + " tiene pares?");
            }
            j.getMano().setPares();
            if(j.getMano().isExisteDuplex()){
                printers.get(this.getMesa().getOrdenJugador(j)).println("Tiene duplex");
            }
            if(j.getMano().isExisteTrio()){
                printers.get(this.getMesa().getOrdenJugador(j)).println("Tiene trio");
            }
            if(j.getMano().isExistePar()){
                printers.get(this.getMesa().getOrdenJugador(j)).println("Tiene par");
            }
            if(j.getMano().isExisteDuplex() || j.getMano().isExisteTrio() || j.getMano().isExistePar()){
                j.setTienePares(true);
                pares.add(j);
                for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " tiene pares.");
                }
            }else {
                j.setTienePares(false);
                for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " no tiene pares.");
                }
            }
        }
        if(!pares.isEmpty()) {
            if (!(pares.size() == 2 && this.getMesa().equipoJugador(pares.get(0)) == this.getMesa().equipoJugador(pares.get(1))) && !(pares.size() == 1)) {

                for (modeloNegocio.Jugador j : pares) {
                	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                    }
                	printers.get(this.getMesa().getOrdenJugador(j)).println(" -introduce cantidad a envidar(2) o subir apuesta(+2), 0 para pasar: ");
                    cantidadJugador = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                    cantidadTotal = cantidadTotal + cantidadJugador;
                    if (cantidadJugador != 0) {
                    	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                        	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " ha envidado " + cantidadJugador + " puntos");
                        }
                        empiezaEnvite = this.getMesa().equipoJugador(j);
                        envite = true;
                        break;
                    } else {
                        for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                        	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " pasa el turno");
                        }
                    }
                }
                if (envite) {
                    while (subirApuesta == true) {
                        for (modeloNegocio.Jugador j : pares) {
                            if (!this.getMesa().equipoJugador(j).equals(empiezaEnvite)) {
                                if (numJ == 0) {//Primer jugador del equipo en decidir
                                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                                    }
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Quieres rechazar el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                    apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                    switch (apuestaSiguiente) {
                                        case 0:
                                            numJ++;
                                            break;
                                        case 2:
                                            numJ++;
                                            break;
                                        default:
                                            empiezaEnvite = this.getMesa().equipoJugador(j);
                                            cantidadTotal = cantidadTotal + apuestaSiguiente;
                                            puntosAcumulados++;
                                            numJ++;
                                            break;
                                    }
                                } else if (numJ == 1) { //Segundo jugador del equipo en decidir
                                    switch (apuestaSiguiente) {
                                        case 0:
                                            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                                            }
                                            printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha rechazado el envite. Quieres rechazar tambien el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                            apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                            if (apuestaSiguiente > 2) {
                                                empiezaEnvite = this.getMesa().equipoJugador(j);
                                                cantidadTotal = cantidadTotal + apuestaSiguiente;
                                                puntosAcumulados++;
                                            }
                                            break;
                                        case 2:
                                            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                                            }
                                            printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha aceptado el envite. Quieres aceptarlo tambien(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                            apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                            if (apuestaSiguiente > 2) {
                                                empiezaEnvite = this.getMesa().equipoJugador(j);
                                                cantidadTotal = cantidadTotal + apuestaSiguiente;
                                                puntosAcumulados++;
                                            }
                                            break;
                                        default:
                                        	printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha subido la apuesta");
                                            break;
                                    }
                                }
                            }
                        }
                        if (apuestaSiguiente == 0) {
                            subirApuesta = false;
                            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha rechazado el envite. El " + empiezaEnvite.getNombre() + " se llevan " + puntosAcumulados + " puntos");
                            }
                            empiezaEnvite.setPuntos(puntosAcumulados);
                            cantidadTotal = 0;
                        } else if (apuestaSiguiente == 2) {
                            subirApuesta = false;
                            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha aceptado el envite. El equipo que gane se llevara "+ cantidadTotal);
                            }
                        } else {
                            numJ = 0;
                        }
                    }
                } else {
                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Fase de pares ha terminado: todo el mundo ha pasado");
                    }
                    cantidadTotal = 1;
                }
            } else {
                for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println("Solo un equipo tiene pares");
                }
            }
        } else{
            cantidadTotal=-1;
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Ningun jugador tiene pares.");
            }
        }
        return cantidadTotal;

    }


    public int faseJuego() throws NumberFormatException, IOException{

        boolean envite = false;
        boolean subirApuesta=true;
        int cantidadJugador = 0;
        int cantidadTotal = 0;
        int apuestaSiguiente=0;
        int numJ=0;
        int puntosAcumulados=1;
        List<modeloNegocio.Jugador> juego = new ArrayList<modeloNegocio.Jugador>();
        modeloNegocio.Equipo empiezaEnvite = new modeloNegocio.Equipo(null);

        for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()) {
        	printers.get(this.getMesa().getOrdenJugador(j)).println("--------------------Fase de Juego------------------------");
        }
        for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("¿Jugador " + j.getNombre() + " tiene juego? ");
            }
            j.setJuego();
            if(j.getJuego()>=31){
                j.setTieneJuego(true);
                juego.add(j);
                for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " tiene juego.");
                }
            }else{
                j.setTieneJuego(false);
                for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " no tiene juego.");
                }
            }
        }
        if(!juego.isEmpty()) {
            //Comprobamos que al menos uno de cada equipo tiene pjuego
            if (!(juego.size() == 2 && this.getMesa().equipoJugador(juego.get(0)) == this.getMesa().equipoJugador(juego.get(1))) && !(juego.size() == 1)) {
                for (modeloNegocio.Jugador j : juego) {
                	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                    }
                	printers.get(this.getMesa().getOrdenJugador(j)).println(" -introduce cantidad a envidar(2) o subir apuesta(+2), 0 para pasar: ");
                    cantidadJugador = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                    cantidadTotal = cantidadTotal + cantidadJugador;
                    if (cantidadJugador != 0) {
                    	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                        	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " ha envidado " + cantidadJugador + " puntos");
                        }
                        empiezaEnvite = this.getMesa().equipoJugador(j);
                        envite = true;
                        break;

                    } else {
                        for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                        	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " ha envidado " + cantidadJugador + " puntos");
                        }
                    }
                }

                if (envite) {
                    while (subirApuesta == true) {
                        for (modeloNegocio.Jugador j : juego) {
                        	if (!this.getMesa().equipoJugador(j).equals(empiezaEnvite)) {
                                if (numJ == 0) {//Primer jugador del equipo en decidir
                                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                                    }
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Quieres rechazar el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                    apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                    switch (apuestaSiguiente) {
                                        case 0:
                                            numJ++;
                                            break;
                                        case 2:
                                            numJ++;
                                            break;
                                        default:
                                            empiezaEnvite = this.getMesa().equipoJugador(j);
                                            cantidadTotal = cantidadTotal + apuestaSiguiente;
                                            puntosAcumulados++;
                                            numJ++;
                                            break;
                                    }
                                } else if (numJ == 1) { //Segundo jugador del equipo en decidir
                                    switch (apuestaSiguiente) {
                                        case 0:
                                            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                                            }
                                            printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha rechazado el envite. Quieres rechazar tambien el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                            apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                            if (apuestaSiguiente > 2) {
                                                empiezaEnvite = this.getMesa().equipoJugador(j);
                                                cantidadTotal = cantidadTotal + apuestaSiguiente;
                                                puntosAcumulados++;
                                            }
                                            break;
                                        case 2:
                                            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
                                            }
                                            printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha aceptado el envite. Quieres aceptarlo tambien(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                            apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                            if (apuestaSiguiente > 2) {
                                                empiezaEnvite = this.getMesa().equipoJugador(j);
                                                cantidadTotal = cantidadTotal + apuestaSiguiente;
                                                puntosAcumulados++;
                                            }
                                            break;
                                        default:
                                        	printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha subido la apuesta");
                                            break;
                                    }
                                }
                            }
                        }
                        if (apuestaSiguiente == 0) {
                            subirApuesta = false;
                            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha rechazado el envite. El " + empiezaEnvite.getNombre() + " se llevan " + puntosAcumulados + " puntos");
                            }
                            empiezaEnvite.setPuntos(puntosAcumulados);
                            cantidadTotal = 0;
                        } else if (apuestaSiguiente == 2) {
                            subirApuesta = false;
                            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                            	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha aceptado el envite. El equipo que gane se llevara "+ cantidadTotal);
                            }
                        } else {
                            numJ = 0;
                        }
                    }
                } else {
                	 for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                     	printers.get(this.getMesa().getOrdenJugador(t)).println("Fase de juego ha terminado: todo el mundo ha pasado");
                     }
                    cantidadTotal = 1;
                }
            } else {
            	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println("Solo un equipo tiene juego");
                }
            }
        }else{
            cantidadTotal=-1;
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Nadie tiene juego. Se pasa a la fase de Punto");
            }
        }
        return cantidadTotal;
    }

    public int fasePunto() throws NumberFormatException, IOException{

        boolean envite = false;
        boolean subirApuesta=true;
        int cantidadJugador = 0;
        int cantidadTotal = 0;
        int apuestaSiguiente=0;
        int numJ=0;
        int puntosAcumulados=1;
        modeloNegocio.Equipo empiezaEnvite = new modeloNegocio.Equipo(null);

        for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()) {
        	printers.get(this.getMesa().getOrdenJugador(j)).println("--------------------Fase de Punto------------------------");
        }

        for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
        	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Turno de " + j.getNombre());
            }
            printers.get(this.getMesa().getOrdenJugador(j)).println(" -introduce cantidad a envidar(2) o subir apuesta(+2), 0 para pasar: ");
            cantidadJugador =  Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
            cantidadTotal = cantidadTotal + cantidadJugador;


            if(cantidadJugador != 0){
            	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " ha envidado " + cantidadJugador + " puntos");
                }
                empiezaEnvite = this.getMesa().equipoJugador(j);
                envite = true;
                break;

            }else{
            	for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                	printers.get(this.getMesa().getOrdenJugador(t)).println(j.getNombre() + " pasa el turno");
                }
            }
        }
        if(envite){
            while(subirApuesta==true) {
                for (modeloNegocio.Jugador j : this.getMesa().getOrdenMesa()) {
                	if (!this.getMesa().equipoJugador(j).equals(empiezaEnvite)) {

                        if (numJ == 0) {//Primer jugador del equipo en decidir
                            printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                            printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Quieres rechazar el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                            apuestaSiguiente = Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                            switch (apuestaSiguiente) {
                                case 0:
                                    numJ++;
                                    break;
                                case 2:
                                    numJ++;
                                    break;
                                default:
                                    empiezaEnvite = this.getMesa().equipoJugador(j);
                                    cantidadTotal=cantidadTotal+apuestaSiguiente;
                                    puntosAcumulados++;
                                    numJ++;
                                    break;
                            }
                        } else if (numJ == 1) { //Segundo jugador del equipo en decidir
                            switch (apuestaSiguiente) {
                                case 0:
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha rechazado el envite. Quieres rechazar tambien el envite(0), aceptarlo(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                    apuestaSiguiente =  Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                    if(apuestaSiguiente>2){
                                        empiezaEnvite = this.getMesa().equipoJugador(j);
                                        cantidadTotal=cantidadTotal+apuestaSiguiente;
                                        puntosAcumulados++;
                                    }
                                    break;
                                case 2:
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha aceptado el envite. Quieres aceptarlo tambien(2) o subir apuesta(+2 Se sumara a la apuesta actual)");
                                    apuestaSiguiente =Integer.valueOf(readers.get(this.getMesa().getOrdenJugador(j)).readLine());
                                    if(apuestaSiguiente>2){
                                        empiezaEnvite = this.getMesa().equipoJugador(j);
                                        cantidadTotal=cantidadTotal+apuestaSiguiente;
                                        puntosAcumulados++;
                                    }
                                    break;
                                default:
                                    printers.get(this.getMesa().getOrdenJugador(j)).println("Apuesta actual: " + cantidadTotal);
                                    printers.get(this.getMesa().getOrdenJugador(j)).println(j.getNombre() + " Tu compañero ha subido la apuesta");
                                    break;
                            }
                        }
                    }
                }
                if (apuestaSiguiente == 0) {
                    subirApuesta=false;
                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha rechazado el envite. El " + empiezaEnvite.getNombre() + " se llevan "+puntosAcumulados + " puntos");
                    }
                    empiezaEnvite.setPuntos(puntosAcumulados);
                    cantidadTotal=0;
                } else if (apuestaSiguiente == 2) {
                    subirApuesta=false;
                    for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
                    	printers.get(this.getMesa().getOrdenJugador(t)).println("Se ha aceptado el envite. El equipo que gane se llevara "+ cantidadTotal);
                    }
                }
                else{
                   numJ=0;
                }
            }
        }else{
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Fase de Punto ha terminado: todo el mundo ha pasado");
            }
            cantidadTotal=1;
        }
        return cantidadTotal;

    }

    public void recuentoPuntos(int pGrandes,int pPequeñas,int pPares,int pJuego,int pPunto){
        //Recuento Grandes
        if(pGrandes!=0){
            modeloNegocio.Jugador mayor=null;
            for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()){
                if(mayor==null){
                    mayor=j;
                }else{
                    for(int i=0;i<4;i++){
                        if(j.getMano().getMano().get(i).getNumero()>mayor.getMano().getMano().get(i).getNumero()){
                            mayor=j;
                            break;
                        }else if(j.getMano().getMano().get(i).getNumero()<mayor.getMano().getMano().get(i).getNumero()){
                            break;
                        }
                    }
                }
            }
            this.getMesa().equipoJugador(mayor).setPuntos(pGrandes);
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Recuento de Grandes: "+this.getMesa().getEquipo1().getPuntos() + " : " + this.getMesa().getEquipo2().getPuntos());
            } 
        }
        if(pPequeñas!=0){
            modeloNegocio.Jugador menor=null;
            for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()){
                if(menor==null){
                    menor=j;
                }else{
                    for(int i=3;i>-1;i--){
                        if(j.getMano().getMano().get(i).getNumero()<menor.getMano().getMano().get(i).getNumero()){
                            menor=j;
                            break;
                        }else if(j.getMano().getMano().get(i).getNumero()>menor.getMano().getMano().get(i).getNumero()){
                            break;
                        }
                    }
                }
            }
            this.getMesa().equipoJugador(menor).setPuntos(pPequeñas);
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Recuento de Pequeñas: "+this.getMesa().getEquipo1().getPuntos() + " : " + this.getMesa().getEquipo2().getPuntos());
            }
        }
        if(pPares!=-1){
            modeloNegocio.Jugador pares=null;
            for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()){
                if(j.getTienePares()==true){
                    if(pares==null){
                        pares=j;
                    }else{
                        if((j.getMano().isExisteDuplex()==true && (pares.getMano().isExisteTrio()==true || pares.getMano().isExistePar()==true))
                            || (j.getMano().isExisteTrio()==true && pares.getMano().isExistePar()==true)){
                            System.out.println("tiene trio");
                            pares=j;
                        }else if(j.getMano().isExisteDuplex()==true && pares.getMano().isExisteDuplex()==true){
                            for(int i=0;i<2;i++){
                                if(j.getMano().getPares().get(i).getNumero()>pares.getMano().getPares().get(i).getNumero()){
                                    pares=j;
                                    break;
                                }else if(j.getMano().getPares().get(i).getNumero()>pares.getMano().getPares().get(i).getNumero()){
                                    break;
                                }
                            }
                        }else if(j.getMano().isExisteTrio()==true && pares.getMano().isExisteTrio()==true){
                            if(j.getMano().getTrio()>pares.getMano().getTrio()){
                                pares=j;
                            }
                        }else if(j.getMano().isExistePar()==true && pares.getMano().isExistePar()==true){
                            if(j.getMano().getPares().get(0).getNumero()>pares.getMano().getPares().get(0).getNumero()){
                                pares=j;
                            }
                        }
                    }
                }
            }
            this.getMesa().equipoJugador(pares).setPuntos(pPares);
            for(modeloNegocio.Jugador j:this.getMesa().equipoJugador(pares).getJugadores()){
                if(j.getMano().isExisteDuplex()){
                    this.getMesa().equipoJugador(pares).setPuntos(3);
                }else if(j.getMano().isExisteTrio()){
                    this.getMesa().equipoJugador(pares).setPuntos(2);
                }else if(j.getMano().isExistePar()){
                    this.getMesa().equipoJugador(pares).setPuntos(1);
                }
            }
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Recuento de Pares: "+this.getMesa().getEquipo1().getPuntos() + " : " + this.getMesa().getEquipo2().getPuntos());
            }
        }
        if(pJuego!=-1){//si nadie tiene juego se pasa al recuento de la fase de punto

            modeloNegocio.Jugador mejor = null;
            for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
                if(j.getTieneJuego()==true){
                    if(mejor==null){
                        mejor=j;
                    }else{
                        if(mejor.getJuego()==31){
                            break;
                        }else if(j.getJuego()==31){
                            mejor=j;
                            break;
                        }else if((j.getJuego()==32 && mejor.getJuego()!=32)||(j.getJuego()>mejor.getJuego() && mejor.getJuego()!=32)){
                            mejor=j;
                        }
                    }
                }

            }
            this.getMesa().equipoJugador(mejor).setPuntos(pJuego);
            for(modeloNegocio.Jugador j: this.getMesa().equipoJugador(mejor).getJugadores()) {
                if (j.getJuego() == 31) {
                    this.getMesa().equipoJugador(mejor).setPuntos(3);
                } else if(j.getTieneJuego()==true) {
                    this.getMesa().equipoJugador(mejor).setPuntos(2);
                }
            }
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Recuento de Juego: "+this.getMesa().getEquipo1().getPuntos() + " : " + this.getMesa().getEquipo2().getPuntos());
            }
        }else if(pPunto!=0){
            modeloNegocio.Jugador jpun = null;
            for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
                    if(jpun==null){
                        jpun=j;
                    }else{
                        if(j.getJuego()>jpun.getJuego()){
                            jpun=j;
                        }
                    }

            }
            this.getMesa().equipoJugador(jpun).setPuntos(pPunto);
            for(modeloNegocio.Jugador t:this.getMesa().getOrdenMesa()) {
            	printers.get(this.getMesa().getOrdenJugador(t)).println("Recuento de Punto: "+this.getMesa().getEquipo1().getPuntos() + " : " + this.getMesa().getEquipo2().getPuntos());
            }
        }
         
    }

    public modeloNegocio.Mesa getMesa(){
        return this.mesa;
    }
    public void nuevaMesa(){
        modeloNegocio.Equipo e1 = new modeloNegocio.Equipo("Equipo1");
        modeloNegocio.Equipo e2 = new Equipo("Equipo2");
        this.mesa=new Mesa(e1,e2);
    }


    public void iniciarPartida() throws IOException {
        //Orden inicial al empezar la partida
        this.getMesa().getOrdenMesa().add(this.getMesa().getEquipo1().getJugadores().get(0));
        this.getMesa().getOrdenMesa().add(this.getMesa().getEquipo2().getJugadores().get(0));
        this.getMesa().getOrdenMesa().add(this.getMesa().getEquipo1().getJugadores().get(1));
        this.getMesa().getOrdenMesa().add(this.getMesa().getEquipo2().getJugadores().get(1));
        

        while((this.getMesa().getEquipo1().getPuntos()<25 && this.getMesa().getEquipo2().getPuntos()<25) ||  this.getMesa().getEquipo1().getPuntos()==this.getMesa().getEquipo2().getPuntos()) {
        	//Barajar
        	for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
                printers.get(this.getMesa().getOrdenJugador(j)).println("Equipo 1: "+this.getMesa().getEquipo1().getJugadores().get(0).getNombre()+"-"+this.getMesa().getEquipo1().getJugadores().get(1).getNombre());
                printers.get(this.getMesa().getOrdenJugador(j)).println("Equipo 2: "+this.getMesa().getEquipo2().getJugadores().get(0).getNombre()+"-"+this.getMesa().getEquipo2().getJugadores().get(1).getNombre());
        		printers.get(this.getMesa().getOrdenJugador(j)).println("Orden Mesa: "+this.getMesa().getOrdenMesa().get(0).getNombre()+"->"+this.getMesa().getOrdenMesa().get(1).getNombre()+"->"+this.getMesa().getOrdenMesa().get(2).getNombre()+"->"+this.getMesa().getOrdenMesa().get(3).getNombre());
        		printers.get(this.getMesa().getOrdenJugador(j)).println("----------------------------------------------------");
        	}
        	this.getMesa().getBaraja().barajar();

            //Repartir cartas
            this.repartir();
            
        	for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
        		printers.get(this.getMesa().getOrdenJugador(j)).println(j.mostrarMano());
        		printers.get(this.getMesa().getOrdenJugador(j)).println("-----------------------");
        	}


        	//Fase1: Mus
	        this.hacerMus();
	
	        for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
	            printers.get(this.getMesa().getOrdenJugador(j)).println(j.mostrarMano());
	            printers.get(this.getMesa().getOrdenJugador(j)).println("-----------------------");
	        }

	        //Fase2: Grandes
	         this.pGrandes = this.faseGrandes();
	         for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
	             printers.get(this.getMesa().getOrdenJugador(j)).println(j.mostrarMano());
	             printers.get(this.getMesa().getOrdenJugador(j)).println("-----------------------");
	         }
	
	        //Fase3: Pequeñas
	         this.pPequeñas = this.fasePequeñas();
	         for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
	             printers.get(this.getMesa().getOrdenJugador(j)).println(j.mostrarMano());
	             printers.get(this.getMesa().getOrdenJugador(j)).println("-----------------------");
	         }
	
	         //Fase4: Pares
	         this.pPares = this.fasePares();
	         for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
	             printers.get(this.getMesa().getOrdenJugador(j)).println(j.mostrarMano());
	             printers.get(this.getMesa().getOrdenJugador(j)).println("-----------------------");
	         }
	
	         //Fase5: Juego
	         this.pJuego = this.faseJuego();
	
	        if(pJuego==-1) {
	            //Fase5.1: Punto
	        	for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
	                printers.get(this.getMesa().getOrdenJugador(j)).println(j.mostrarMano());
	                printers.get(this.getMesa().getOrdenJugador(j)).println("-----------------------");
	            }
	            this.pPunto = this.fasePunto();
	        }
	
	        for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
	        	for(modeloNegocio.Jugador t: this.getMesa().getOrdenMesa()){
	                printers.get(this.getMesa().getOrdenJugador(j)).println(t.getNombre() + "  " +  t.mostrarMano());
	                printers.get(this.getMesa().getOrdenJugador(j)).println("-----------------------");
	            }
	        }
	        //Fase6: Recuento de puntos
	        this.recuentoPuntos(this.pGrandes,this.pPequeñas,this.pPares,this.pJuego,this.pPunto);
	        
	        if((this.getMesa().getEquipo1().getPuntos()<=25 && this.getMesa().getEquipo2().getPuntos()<=25)||  this.getMesa().getEquipo1().getPuntos()==this.getMesa().getEquipo2().getPuntos()) {
	        	//Cambiamos de orden los jugadores y los sockets
	        	this.getMesa().cambiarPosicionesJugadores();
	        	
	        	for(modeloNegocio.Jugador j:this.getMesa().getOrdenMesa()) {
	            	j.setMano();
	            	
	            }
	        	
	        	PrintStream auxP = this.printers.get(0);
	            for(int i=0;i<this.printers.size()-1;i++){
	                this.printers.set(i, this.printers.get(i + 1));
	            }
	            this.printers.set(3, auxP);
	            
	            BufferedReader auxR = this.readers.get(0);
	            for(int i=0;i<this.readers.size()-1;i++){
	                this.readers.set(i, this.readers.get(i + 1));
	            }
	            this.readers.set(3, auxR);
	            
	            this.getMesa().nuevaBaraja();
	            this.getMesa().nuevoMonton();

                for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
                    printers.get(this.getMesa().getOrdenJugador(j)).println("-----------SIGUIENTE RONDA---------------");
                }
	        }
        }
        if(this.getMesa().getEquipo1().getPuntos()>this.getMesa().getEquipo2().getPuntos()) {
        	for(modeloNegocio.Jugador j: this.getMesa().getOrdenMesa()){
                printers.get(this.getMesa().getOrdenJugador(j)).println("¡¡¡¡¡Ha ganado el equipo 1!!!!!");
            }
        }else {
        	for(Jugador j: this.getMesa().getOrdenMesa()){
                printers.get(this.getMesa().getOrdenJugador(j)).println("¡¡¡¡¡Ha ganado el equipo 2!!!!!");
            }
        }
    }

    public void enviar(String s, Socket socket) {


        try {
            BufferedWriter bufferDeSalida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bufferDeSalida.write(s);
            bufferDeSalida.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void iniciarServidor() throws IOException{
    	ServerSocket serverSocket = new ServerSocket(6666);
        try{
            ExecutorService pool = Executors.newFixedThreadPool(4);
            CountDownLatch c = new CountDownLatch(1);
                for (int i = 0; i < 4; i++) {
                    final Socket conexion = serverSocket.accept();  //espera conexion
                    pool.execute(new AtenderPeticion(conexion, this.mesa.getEquipo1(), this.mesa.getEquipo2(), c, i + 1));
                    sockets.add(conexion);
                    printers.add(new PrintStream(conexion.getOutputStream()));
                    readers.add(new BufferedReader(new InputStreamReader(conexion.getInputStream())));
                }
                //Ordenar arrays para que tengan el mismo orden que tienen los jugadores en la mesa
                PrintStream psaux = printers.get(2);
                printers.set(2, printers.get(1));
                printers.set(1, psaux);
                BufferedReader braux = readers.get(2);
                readers.set(2, readers.get(1));
                readers.set(1, braux);

                while (this.getMesa().getEquipo2().getJugadores().size() < 2) {

                }
                c.countDown();
                iniciarPartida();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            serverSocket.close();
        }
    }



}
