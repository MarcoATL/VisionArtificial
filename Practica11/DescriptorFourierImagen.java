package Descriptores;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class DescriptorFourierImagen {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
        String filePath = PATH + "sinFunction.png";
        // # 1. Cargamos la imagen
        Mat newImage = Imgcodecs.imread(filePath);
        if (newImage.dataAddr() == 0) {
            System.out.println("No puedo abrir el archivo " + filePath);
            } 
        else {
            mostrarImagen("input", newImage);
            // # 2. Convertimos a escala de grises
            Mat gris = new Mat();
            Imgproc.cvtColor(newImage.clone(), gris, Imgproc.COLOR_BGR2GRAY);
            // # 3. Aplicar suavizado Gaussiano
            Mat gauss = aberrarGaussiano(gris, 11);
            mostrarImagen("suavisado", gauss);
            System.out.println(gauss.height() + " " + gauss.width() + " " + gauss.type());
            // # 4. Detectamos los contornos con Canny
            Mat canny = new Mat();
            Imgproc.Canny(gauss, canny, 60, 180);
            mostrarImagen("canny", canny);
            
            
            //# 5. Buscamos los contornos
            List<MatOfPoint> contornos = new ArrayList<>();
            Mat jerarquia = new Mat();
            Imgproc.findContours(canny.clone(), contornos, jerarquia, 
                Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE);
            // # 6. Dibujar los contornos encontrados
            Imgproc.drawContours(newImage, contornos, -1, new Scalar(0,0,255), 2);
            System.out.println("Largo de la imagen: " + canny.width());
            System.out.println("Ancho de la imagen: " + canny.height());
            int longitud = canny.width()*canny.height();
            TDFourier fourier = new TDFourier(longitud);
            Complejo [] senial = new Complejo[longitud];
            
            for (int i = 0; i < senial.length; i++)
                senial[i] = new Complejo(0, 0);
            
            for (MatOfPoint contorno : contornos) {
                for (Point punto : contorno.toList()) {
                    int x = (int) punto.x;
                    int y = (int) punto.y;
                    senial[(x*canny.width())+y] = new Complejo(canny.get(y, x)[0],0.0);
                }
            }
            
            

            int P = longitud/1;
            System.out.println( "P:" + P );
            System.out.println("Numero de contornos encontrados: " + contornos.size());
            // directa
            fourier.transformarDirecto(senial);
            // inversa
            fourier.transformarInverso(fourier.getAu(), P);
            Complejo [] sk = fourier.getSk();
            int contador = 0;
            System.out.println(canny.height() + " " + canny.width() + " " + canny.type());
            Mat cannyInv = new Mat(canny.height(), canny.width(), canny.type());
            for(int y=0; y<canny.height(); y++) {
                for(int x=0; x<canny.width(); x++) {
                    byte [] pixel = {
                        (byte)sk[contador].getReal()
                        };
                    //invertimos y,x a x,y para voltear la imagen
                    cannyInv.put(x, y, pixel); 
                    contador++;
                    }
                }
            mostrarImagen("cannyInv", cannyInv);
            }
        HighGui.waitKey(0);
        System.exit(0);
    }
    public static void mostrarImagen(String titulo, Mat imagen) {
        HighGui.namedWindow(titulo, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(titulo, imagen);
    }
    public static Mat aberrarGaussiano(Mat gris, int size) {
        Mat gauss = new Mat();
        Imgproc.GaussianBlur(gris, gauss, new Size(size, size), 0);
        return gauss;
    }
}