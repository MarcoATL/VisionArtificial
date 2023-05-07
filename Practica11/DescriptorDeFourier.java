package Descriptores;

public class DescriptorDeFourier {
    public static void main(String[] args) {
        Complejo [] escalon = {
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0)
            };
        Complejo [] delta = {
            new Complejo(1.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0),
            new Complejo(0.0, 0.0)
            };
        Complejo [] constante = {
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0),
            new Complejo(1.0, 0.0)
            };
        double dosPi = 2.0*Math.PI;
        Complejo [] seno = {
            new Complejo(Math.sin((dosPi*0.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*1.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*2.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*3.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*4.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*5.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*6.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*7.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*8.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*9.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*10.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*11.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*12.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*13.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*14.0)/16), 0.0),
            new Complejo(Math.sin((dosPi*15.0)/16), 0.0)
            };
        TDFourier fourier = new TDFourier(16);
        imprimirSenial(escalon);
        fourier.transformarDirecto(escalon);
        //imprimirSenial(delta);
        //fourier.transformarDirecto(delta);
        //imprimirSenial(constante);
        //fourier.transformarDirecto(constante);
//        imprimirSenial(seno);
//        fourier.transformarDirecto(seno);
        System.out.println(fourier);
        fourier.transformarInverso(fourier.getAu());
        System.out.println(fourier);
        fourier.transformarInverso(fourier.getAu(), 12);
        System.out.println(fourier);
    }
    public static void imprimirSenial(Complejo [] zetas) {
        System.out.print(" sk=[ ");
        for(int n=0; n<zetas.length; n++) {
            System.out.print(zetas[n] + " ");
                }
        System.out.println(" ]\n");
    }
}