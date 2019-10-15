package cscholtz.android.nutrievaluator;
import android.database.Cursor;



public class Evaluator {
    private static final String SEX1 = "Masculino";
    private static final String BIG = "grande";
    private static final String MED = "mediana";
    private static final String SMALL = "pequeña";
    private static final String NORM = "Normal";
    private static final String RISK1 = "Riesgo Moderado";
    private static final String RISK2 = "Riesgo Alto";
    private int[] percentiles = {5,10,25,50,75,90,96};
    private String sexo;
    private String nombre;
    private int edad;
    private int tricipital;
    private int bicipital;
    private int suprailiaco;
    private int subescapular;
    private float peso;
    private float talla;
    private float cintura;
    private float cadera;
    private float braquial;
    private float carpo;
    private double imc;
    private double pesoIdeal;
    private double ipt;
    private double cmb;
    private double amb;
    private double agb;
    private double relCinCad;
    private double contextura;

    public Evaluator( String nom, String sex, int ed, Float[] medidas, Integer[] pliegues, Float[] circunferencias){
        this.nombre = nom;
        this.sexo = sex;
        this.edad = ed;
        this.peso = medidas[0];
        this.talla = medidas[1];
        this.tricipital = pliegues[0];
        this.bicipital = pliegues[1];
        this.suprailiaco = pliegues[2];
        this.subescapular = pliegues[3];
        this.cintura = circunferencias[0];
        this.cadera = circunferencias[1];
        this.braquial = circunferencias[2];
        this.carpo = circunferencias[3];
        calcular();
    }

    private void calcular(){
        this.imc = peso/Math.pow(talla,2);
        if(sexo.equals(SEX1)){
            this.pesoIdeal = Math.pow(talla,2) * 22;
        }else{
            this.pesoIdeal = Math.pow(talla,2) * 21.5;
        }
        this.ipt = peso*100/ pesoIdeal;
        this.cmb = 10*braquial - tricipital * Math.PI;
        this.amb = Math.pow(cmb,2)/(4*Math.PI);
        this.agb = (Math.pow(braquial*10,2)/(4*Math.PI)) - amb;
        this.relCinCad = cintura/cadera;
        this.contextura = 100*talla/carpo;
    }

    public double getIMC(){
        return imc;
    }
    public double getIPT(){
        return ipt;
    }
    public double getPesoIdeal(){
        return pesoIdeal;
    }
    public double getCMB(){
        return cmb;
    }
    public double getAMB(){
        return amb;
    }
    public double getAGB(){
        return agb;
    }
    public double getPT(){
        return tricipital;
    }
    public double getCin(){
        return cintura;
    }
    public double getRelCinCad(){
        return relCinCad;
    }
    public double getContextura(){
        return contextura;
    }

    public int percentilMin(double value,Integer[] p){
        double min;
        min = 100000;
        int aux=0;
        for(int i=0;i<7;i++){
            if(value-p[i]<0){
                break;
            }else{
                if(value - p[i] < min){
                    min = value-p[i];
                    aux = i;
                }
            }
        }
        return percentiles[aux];
    }

    public int percentilMax(double value,Integer[] p){
        double max;
        max = 100000;
        int aux = 6;
        for(int i=6;i>=0;i--){
            if(p[i]-value<0){
                break;
            }else{
                if(p[i]- value < max){
                    max = p[i] - value;
                    aux = i;
                }
            }
        }
        return percentiles[aux];
    }

    public String evaluarIMC(){
        if(imc <18.5){
            return "Enflaquecido";
        }else if(imc >= 18.5 && imc <25){
            return NORM;
        }else if(imc >=25 && imc <30){
            return "Sobrepeso";
        }else{
            return "Obesidad";
        }
    }

    public String evaluarIPT(){
        if(ipt <70){
            return "Desnutricion Severa";
        }else if(ipt >= 70 && ipt <80){
            return "Desnutricion Moderada";
        }else if(ipt >=80 && ipt <90){
            return "Desnutricion Leve";
        }else if(ipt >=90 && ipt <110){
            return NORM;
        }else if(ipt >=110 && ipt <120){
            return "Sobrepeso";
        }else{
            return "Obesidad";
        }
    }

   public String evaluarPercentilesCMB(int pmin, int pmax){
        float prom = (pmin+pmax)/2f;
        if(prom<=5){
            return "Déficit moderado a severo";
        }else if(prom>5 && prom <=25){
            return "Déficit leve";
        }else if(prom>25 && prom<=75){
            return "Reserva normal";
        }else{
            return "Reserva normal alta";
        }
   }

   public String evaluarPercentiles(int pmin, int pmax){
        float prom = (pmin+pmax)/2f;
        if(prom<=5){
            return "Déficit moderado a severo";
        }else if(prom>5 && prom <=10){
            return "Déficit leve";
        }else if(prom>10 && prom<=90){
            return "Reserva normal";
        }else if(prom>90 && prom<=95){
            return "Reserva alta";
        }else{
            return "Reserva muy alta";
        }
    }

    public String evaluarCintura(){
        if(sexo.equals(SEX1)){
            if(cintura<94){
                return NORM;
            }else if(cintura>=94 && cintura<102){
                return RISK1;
            }else{
                return RISK2;
            }
        }else{
            if(cintura<80){
                return NORM;
            }else if(cintura>=80 && cintura<88){
                return RISK1;
            }else{
                return RISK2;
            }
        }
    }

    public String evaluarRelCinCad(){
        if(sexo.equals(SEX1)){
            if(relCinCad <0.9){
                return NORM;
            }else if(relCinCad >=0.9 && relCinCad <=1){
                return RISK1;
            }else{
                return RISK2;
            }
        }else{
            if(relCinCad <0.75){
                return NORM;
            }else if(relCinCad >=0.75 && relCinCad <=0.85){
                return RISK1;
            }else{
                return RISK2;
            }
        }
    }

    public String evaluarContextura(){
        if(sexo.equals(SEX1)){
            if(contextura<9.6){
                return BIG;
            }else if(contextura>=9.6 && contextura<=10.4){
                return MED;
            }else{
                return SMALL;
            }
        }else{
            if(contextura<10.1){
                return BIG;
            }else if(contextura>=10.1 && contextura<=11){
                return MED;
            }else{
                return SMALL;
            }
        }
    }

    public Integer[] percentiles(Cursor c){
        c.moveToFirst();
        Integer[] pe = new Integer[7];
        for(int i=0;i<7;i++){
            pe[i]= Integer.valueOf(c.getString(i+1));
        }
        return pe;
    }

    public Integer[] rangoPercentiles(double a, Integer[] p){
        Integer[] rango = new Integer[2];
        rango[0] = percentilMin(a,p);
        rango[1] = percentilMax(a,p);
        return rango;
    }
}
