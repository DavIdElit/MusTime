package modeloNegocio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Mano implements Serializable {
    private ArrayList<modeloNegocio.Carta> mano;
    private List<modeloNegocio.Carta> pares;
    private boolean existeDuplex;
    private boolean existeTrio;
    private boolean existePar;
    private int trio;

    public Mano(){
        this.mano=new ArrayList<modeloNegocio.Carta>(4);
        this.pares=new ArrayList<modeloNegocio.Carta>();
        this.existeDuplex=false;
        this.existeTrio=false;
        this.existePar=false;
        this.trio=0;
    }
    public ArrayList<modeloNegocio.Carta> getMano(){
        return this.mano;
    }
    public void setMano(ArrayList<modeloNegocio.Carta> mano){
        this.mano=mano;
    }

    public boolean isExisteDuplex() {
        return this.existeDuplex;
    }

    public boolean isExisteTrio() {
        return existeTrio;
    }

    public boolean isExistePar() {
        return existePar;
    }

    public int getTrio() {
        return trio;
    }
    public void setTrio() {
    	this.trio=0;
    }
    public List<modeloNegocio.Carta> getPares() {
        return pares;
    }

    public void setExisteDuplex(boolean existeDuplex) {
        this.existeDuplex = existeDuplex;
    }

    public void setExisteTrio(boolean existeTrio) {
        this.existeTrio = existeTrio;
    }

    public void setExistePar(boolean existePar) {
        this.existePar = existePar;
    }


    public void setPares(){
        if(this.mano.get(0).getNumero()==this.mano.get(1).getNumero() && this.mano.get(2).getNumero()==this.mano.get(3).getNumero()){
            this.pares.add(this.mano.get(0));
            this.pares.add(this.mano.get(2));
            this.existeDuplex=true;
        }else if(((this.mano.get(0).getNumero()==this.mano.get(1).getNumero())&&(this.mano.get(1).getNumero()==this.mano.get(2).getNumero()))
                || ((this.mano.get(1).getNumero()==this.mano.get(2).getNumero())&&(this.mano.get(2).getNumero()==this.mano.get(3).getNumero()))){
            this.trio=this.mano.get(2).getNumero();
            this.existeTrio=true;
        }else if(this.mano.get(0).getNumero()==this.mano.get(1).getNumero()){
            this.pares.add(this.mano.get(0));
            this.existePar=true;
        }else if(this.mano.get(1).getNumero()==this.mano.get(2).getNumero()){
            this.pares.add(this.mano.get(1));
            this.existePar=true;
        }else if(this.mano.get(2).getNumero()==this.mano.get(3).getNumero()){
            this.pares.add(this.mano.get(2));
            this.existePar=true;
        }

    }

    public void ordenarMano(){
        Collections.sort(this.mano, new Comparator<modeloNegocio.Carta>() {
            @Override
            public int compare(modeloNegocio.Carta p1, Carta p2) {
                return Integer.valueOf(p2.getNumero()).compareTo(Integer.valueOf(p1.getNumero()));
            }
        });
    }

}
