package Descriptores;

public class Complejo {
    private double real;
    private double imaginario;

    public Complejo() {
        this(0.0, 0.0);
    }

    public Complejo(double real, double imaginario) {
        this.real = real;
        this.imaginario = imaginario;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public double getImaginario() {
        return imaginario;
    }

    public void setImaginario(double imaginario) {
        this.imaginario = imaginario;
    }
    
    public Complejo obtenerConjugado() {
        return new Complejo(real, (-1.0)*imaginario);
    }
    
    public double obtenerMagnitud() {
        return Math.sqrt(real*real + imaginario*imaginario);
    }
    //Fase
    public double obtenerFase() {
        if (real == 0) {
            if (imaginario == 0) {
                return 0;
                } 
            else if (imaginario > 0) {
                return (Math.PI / 2);
                } 
            else {
                return (-Math.PI / 2);
                }
            }
        return Math.atan(imaginario / real);
    }
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.real) ^ 
                (Double.doubleToLongBits(this.real) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.imaginario) ^ 
                (Double.doubleToLongBits(this.imaginario) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
            }
        if (getClass() != obj.getClass()) {
            return false;
            }
        final Complejo other = (Complejo) obj;
        if (Double.doubleToLongBits(this.real) != 
                Double.doubleToLongBits(other.real)) {
            return false;
            }
        if (Double.doubleToLongBits(this.imaginario) != 
                Double.doubleToLongBits(other.imaginario)) {
            return false;
            }
        return true;
    }

    @Override
    public String toString() {
        return "(" + real + ",j " + imaginario + ')';
    }
     
}