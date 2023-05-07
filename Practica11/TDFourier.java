package Descriptores;


public class TDFourier {
    private AritmeticaCompleja aritmetica;
    private Complejo [] au;
    private Complejo [] sk;
    private int K;
    private boolean directa;
    private boolean usoP;
    private int P;
    public TDFourier(int K) {
        this.K = K;
        au = new Complejo[this.K];
        aritmetica = new AritmeticaCompleja();
        directa = true;
        usoP = true;
        P = 0;
    }
    /**
     * cos( θ ) + i sen( θ ) = e i θ
     * @param sk 
     */
    public void transformarDirecto(Complejo [] sk) {
        double dosPI = 2.0 * Math.PI;
        Complejo suma;
        for(int u=0; u<K; u++) {
            suma = new Complejo();
            for(int k=0; k<K; k++) {
                double argumento = (-dosPI*(double)u*(double)k)/(double)K;
                double real = Math.cos(argumento);
                double imaginario = Math.sin(argumento);
                Complejo euler = new Complejo(real, imaginario);
                Complejo z = aritmetica.multiplicar(sk[k], euler);
                suma = aritmetica.sumar(suma, z);
                // DEBUG
//                System.out.println(suma);
                }
            au[u] = suma;
            }
        directa = true;
    }
    public void transformarInverso(Complejo [] au) {
        double dosPI = 2.0 * Math.PI;
        Complejo suma;
        sk = new Complejo[K];
        for(int k=0; k<K; k++) {
            suma = new Complejo();
            for(int u=0; u<K; u++) {
                double argumento = (dosPI*(double)u*(double)k)/(double)K;
                double real = Math.cos(argumento);
                double imaginario = Math.sin(argumento);
                Complejo euler = new Complejo(real, imaginario);
                Complejo z = aritmetica.multiplicar(au[u], euler);
                suma = aritmetica.sumar(suma, z);
                // DEBUG
//                System.out.println(suma);
                }
            sk[k] = aritmetica.dividirEscalar((double)K, suma);
            }
        directa = false;
        usoP = false;
    }
    public void transformarInverso(Complejo [] au, int P) {
        this.P = P;
        double dosPI = 2.0 * Math.PI;
        Complejo suma;
        sk = new Complejo[K];
        for(int k=P; k<K; k++) {
            au[k] = new Complejo();
            }
        for(int k=0; k<K; k++) {
            suma = new Complejo();
            for(int u=0; u<K; u++) {
                double argumento = (dosPI*(double)u*(double)k)/(double)K;
                double real = Math.cos(argumento);
                double imaginario = Math.sin(argumento);
                Complejo euler = new Complejo(real, imaginario);
                Complejo z = aritmetica.multiplicar(au[u], euler);
                suma = aritmetica.sumar(suma, z);
                // DEBUG
//                System.out.println(suma);
                }
            sk[k] = aritmetica.dividirEscalar((double)K, suma);
            }
        
        directa = false;
        usoP = false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int longitud = 0;
        builder.append(" K=").append(K).append("\n");
        builder.append(" P=").append(P).append("\n");
        if(directa) {
            builder.append(" au=[ ");
            for(int n=0; n<K; n++) {
                builder.append(au[n]).append(" ");
                }
            builder.append(" ]").append("\n");
            }
        else {
            builder.append(" sk=[ ");
            if(usoP) {
                longitud = P;
                }
            else {
                longitud = K;
                }
            for(int n=0; n<longitud; n++) {
                builder.append(sk[n]).append(" ");
                }
            builder.append(" ]").append("\n");
            }
        return builder.toString();
    }

    public Complejo [] getAu() {
        return au;
    }

    public Complejo [] getSk() {
        return sk;
    }
    
}