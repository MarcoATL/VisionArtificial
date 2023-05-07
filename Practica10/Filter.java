
package FiltradoTam;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class Filter {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
        String filePath = PATH + "coin2.jpg";
        // # 1. Cargamos la imagen
        Mat newImage = Imgcodecs.imread(filePath);
        if (newImage.dataAddr() == 0) {
            System.out.println("No puedo abrir el archivo " + filePath);
            } 
        else {
            //mostrarImagen("input", newImage);
            int perimetroMaximo = 2000;
            int perimetroMinimo = 200;
            int areaMax = 7500;
            int areaMin = 3000;
            FilterBySize filter = new FilterBySize(perimetroMaximo, perimetroMinimo, areaMax, areaMin);
            Mat segmentada = filter.contarObjetos(newImage.clone());
            mostrarImagen("contornos", segmentada);
            }
        HighGui.waitKey(0);
        System.exit(0);
    }
    public static void mostrarImagen(String titulo, Mat imagen) {
        HighGui.namedWindow(titulo, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(titulo, imagen);
    }
}
