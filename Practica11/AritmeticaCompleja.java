package Descriptores;

public class AritmeticaCompleja {
    /**
     * z1 = (a,b), z2 = (c,d)
     * z3 = z1 + z2 = (a,b)+(c,d)=(a+c,b+d) = z3
     * @param z1
     * @param z2
     * @return 
     */
    public Complejo sumar(Complejo z1, Complejo z2) {
        return new Complejo(z1.getReal()+z2.getReal(),
                            z1.getImaginario()+z2.getImaginario());
    }
    /**
     * z1 = (a,b), z2 = (c,d)
     * z3 = z1 - z2 = (a,b)-(c,d)=(a-c,b-d) = z3
     * @param z1
     * @param z2
     * @return 
     */
    public Complejo restar(Complejo z1, Complejo z2) {
        return new Complejo(z1.getReal()-z2.getReal(),
                            z1.getImaginario()-z2.getImaginario());
    }
    /**
     * z1 = (a,b), z2 = (c,d)
     * z3 = z1*z2 = (a,b)*(c,d)=(ac-bd,ad+bc) = z3
     * @param z1
     * @param z2
     * @return 
     */
    public Complejo multiplicar(Complejo z1, Complejo z2) {
        double real = z1.getReal()*z2.getReal()-
                        z1.getImaginario()*z2.getImaginario();
        double imaginario = z1.getReal()*z2.getImaginario()+
                            z1.getImaginario()*z2.getReal();
        return new Complejo(real, imaginario);
    }
    /**
     * z1 = (a,b)
     * z2 = r*z1 = r(a,b)=(ra,rb)
     * @param escalar
     * @param z
     * @return 
     */
    public Complejo multiplicarEscalar(double escalar, Complejo z) {
        return new Complejo(escalar*z.getReal(), escalar*z.getImaginario());
    }
    /**
     * z1 = (a,b), z2 = (c,d)
     *       (a,b)     (ac+bd, bc-ad)      ac + bd       bc - ad
     * z3 = ------- = ---------------- = ----------- , ----------- 
     *       (c,d)       c*c + d*d        c*c + d*d     c*c + d*d
     * @param z1
     * @param z2
     * @return 
     */
    public Complejo dividir(Complejo z1, Complejo z2) {
        double divisor = Math.pow(z2.getReal(), 2.0) + 
                        Math.pow(z2.getImaginario(), 2.0);
        double real = z1.getReal() * z2.getReal() + 
                        z1.getImaginario() * z2.getImaginario();
        double imaginario = z1.getImaginario() * z2.getReal() - 
                            z1.getReal() * z2.getImaginario();
        return new Complejo(real/divisor, imaginario/divisor);
    }
    /**
     * z1 = (a,b)
     * z2 = z1/r = (a,b)/r=(a/r,b/r)
     * @param escalar
     * @param z
     * @return 
     */
    public Complejo dividirEscalar(double escalar, Complejo z) {
        return new Complejo(z.getReal()/escalar, z.getImaginario()/escalar);
    }
}