/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Esqueletizacion;

import org.opencv.core.Core;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.CvType;

/*
/**
 *
 * @author MARCO
 */
public class ZhangSuen {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        // El acceso donde se encuentran sus imágenes almacenadas
        String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
        String filePath = PATH + "star.jpg";
        // carga la imagen en niveles de gris
        Mat newImage = Imgcodecs.imread(filePath,Imgcodecs.IMREAD_GRAYSCALE);
        if (newImage.dataAddr() == 0) {
            System.out.println("No puedo abrir el archivo " + filePath);
        }
        else {
            System.out.println("Imagen encontrada");
            HighGui.imshow("Imagen a gris", newImage);
            HighGui.waitKey();
            
            Mat tresh = new Mat();
            //Umbraliza imagen
            Imgproc.threshold(newImage, tresh, 172, 255,Imgproc.THRESH_BINARY );
            HighGui.namedWindow("tresh_1", HighGui.WINDOW_AUTOSIZE);
            HighGui.imshow("tresh_1", tresh);
            HighGui.waitKey();

            Mat skeleton = new Mat();
            //Llama funcion de esqueletizar
            skeleton = esqueletizar(tresh);
            HighGui.imshow("Imagen esqueletizada", skeleton);
            HighGui.waitKey();
            
        }
        System.exit(0);
    }
    
    public static Mat thinningIteration(Mat im, int iter){
        Mat marker = new Mat(im.rows(),im.cols(),CvType.CV_8UC1);
        int A = 0;//Contador para la primera condicion
        
        for (int i = 1; i < im.rows()-1; i++)
        {
            for (int j = 1; j < im.cols()-1; j++)
            {
                A = 0;
                //uchar p2 = im.at<uchar>(i-1, j);
                double [] P2 = im.get(i-1, j);
                int p2 = (int)P2[0];
                //uchar p3 = im.at<uchar>(i-1, j+1);
                double [] P3 = im.get(i-1, j+1);
                int p3 = (int)P3[0];
                //uchar p4 = im.at<uchar>(i, j+1);
                double [] P4 = im.get(i, j+1);
                int p4 = (int)P4[0];
                //uchar p5 = im.at<uchar>(i+1, j+1);
                double [] P5 = im.get(i+1, j+1);
                int p5 = (int)P5[0];
                //uchar p6 = im.at<uchar>(i+1, j);
                double [] P6 = im.get(i+1, j);
                int p6 = (int)P6[0];
                //uchar p7 = im.at<uchar>(i+1, j-1);
                double [] P7 = im.get(i+1, j-1);
                int p7 = (int)P7[0];
                //uchar p8 = im.at<uchar>(i, j-1);
                double [] P8 = im.get(i, j-1);
                int p8 = (int)P8[0];
                //uchar p9 = im.at<uchar>(i-1, j-1);
                double [] P9 = im.get(i-1, j-1);
                int p9 = (int)P9[0];
                
                if(p2 == 0 && p3 == 1)
                    A++;
                if(p3 == 0 && p4 == 1)
                    A++;
                if(p4 == 0 && p5 == 1)
                    A++;
                if(p5 == 0 && p6 == 1)
                    A++;
                if(p6 == 0 && p7 == 1)
                    A++;
                if(p7 == 0 && p8 == 1)
                    A++;
                if(p8 == 0 && p9 == 1)
                    A++;
                if(p9 == 0 && p2 == 1)
                    A++;
                
                int B  = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
                int m1 = iter == 0 ? (p2 * p4 * p6) : (p2 * p4 * p8);
                int m2 = iter == 0 ? (p4 * p6 * p8) : (p2 * p6 * p8);
                //Evalua condiciones
                if (A == 1 && (B >= 2 && B <= 6) && m1 == 0 && m2 == 0){
                    //Si se cumple las condiciones asigna etiqueta para eliminar
                    marker.put(i, j, 1);                    
                }
            }
        }
        //Ciclo de eliminacion de pixeles
        for (int i = 1; i < im.rows()-1; i++)
        {
            for (int j = 1; j < im.cols()-1; j++)
            {
                double [] marcador = marker.get(i, j);
                if (marcador[0] == 1){
                    im.put(i, j, 0);//limpia pixel
                }
            }
        }
        return im;
    }
    public static Mat esqueletizar(Mat tresh) {
       
       Mat prev = new Mat(tresh.size(),CvType.CV_8UC1);
       Mat diff = new Mat();
       
       for (int row = 1; row < tresh.rows()-1; row++)
        {
            for (int col = 1; col < tresh.cols()-1; col++)
            {   
                double [] valor = tresh.get(row,col);
                int valorT = (int)valor[0] / 255;//Deja una matriz de 0's y 1's
                tresh.put(row, col, valorT);        
            } 
        }
        
        do {
            tresh = thinningIteration(tresh, 0);//primer bucle
            tresh = thinningIteration(tresh, 1);//segundo bucle
            Core.absdiff(tresh, prev, diff);
            tresh.copyTo(prev);
        } 
        while (Core.countNonZero(diff) > 0);
        
        for (int row = 1; row < tresh.rows()-1; row++)
        {
            for (int col = 1; col < tresh.cols()-1; col++)
            {   
                double [] valor = tresh.get(row,col);
                int valorT = (int)valor[0] * 255;//Deja la matriz con valores de ceros y 255
                tresh.put(row, col, valorT);    
            } 
        }
        return tresh;
    }
}
