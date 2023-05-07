/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperPixeles;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Saul
 */
public class SuperPixelCuatro {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        /* Load the image */
        String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
        String filePath = PATH + "rio.jpg";
        Mat img = Imgcodecs.imread(filePath);
        if (img.dataAddr() == 0) {
            System.out.println("No puedo abrir el archivo\n " + filePath);
            } 
        else {
            mostrarImagen("imagen", img);
             // # 1. convertimos el espacio RGB a Lab
            Mat lab = new Mat(); 
            Imgproc.cvtColor(img, lab, Imgproc.COLOR_BGR2Lab);
            mostrarImagen("lab", lab);
            /* Yield the number of superpixels and weight-factors from the user. 
             */
            int w = img.width();
            int h = img.height();
            int nr_superpixels = 200;
            int nc = 10;
            int itera = 2;

            double step = Math.sqrt((w * h) / (double) nr_superpixels);
            /* Perform the SLIC superpixel algorithm. */
            SlicTwo slic = new SlicTwo(itera);
            slic.generateSuperpixels(lab, (int)step, nc);
            slic.createConnectivity(lab);
            

            /* Display the contours and show the result. */
            Mat segmentada = img.clone();
            slic.displayContours(segmentada, new double[]{255,0,0});
            mostrarImagen("result1", segmentada);
            
            Mat segc = img.clone();
            slic.colourWithClusterMeans(segc);
            mostrarImagen("result2", segc);
            }
        HighGui.waitKey(0);
        System.exit(0);
    }
    public static void mostrarImagen(String titulo, Mat imagen) {
        HighGui.namedWindow(titulo, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(titulo, imagen);
    }
}
