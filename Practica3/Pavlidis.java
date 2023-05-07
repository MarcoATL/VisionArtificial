/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Esqueletizacion;

import org.opencv.core.Core;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
/**
 *
 * @author MARCO
 */
public class Pavlidis {
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

            Mat tresh = new Mat(newImage.rows(), newImage.cols(), newImage.type());
            //Aplica umbralizado a la imagen
            Imgproc.threshold(newImage, tresh, 173, 255,Imgproc.THRESH_BINARY );
            HighGui.namedWindow("tresh_1", HighGui.WINDOW_AUTOSIZE);
            HighGui.imshow("tresh_1", tresh);
            HighGui.waitKey();
            
            Mat skeleton = new Mat();
            skeleton = esqueletoPavlidis(tresh);//Aplica esqueletizacion
            HighGui.imshow("Imagen esqueletizada", skeleton);
            HighGui.waitKey();
        }
        System.exit(0);
    }
    public static Mat esqueletoPavlidis(Mat tresh) {
       
       for (int row = 1; row < tresh.rows()-1; row++)
        {
            for (int col = 1; col < tresh.cols()-1; col++)
            {   
                double [] valor = tresh.get(row,col);
                int valorT = (int)valor[0] / 255;//Deja una matriz de ceros y unos
                tresh.put(row, col, valorT);        
            } 
        }
        
        Mat prev = new Mat(tresh.size(),CvType.CV_8UC1);
        Mat diff = new Mat();
        do {
            tresh = thinningIteracion(tresh);//Aplica Pavlidis
            Core.absdiff(tresh, prev, diff);//Verifica si aun quedan pixeles por eliminar
            tresh.copyTo(prev);
        } 
        while (Core.countNonZero(diff) > 0);
        
        for (int row = 1; row < tresh.rows()-1; row++)
        {
            for (int col = 1; col < tresh.cols()-1; col++)
            {   
                double [] valor = tresh.get(row,col);
                int valorT = (int)valor[0] * 255;//Deja la matriz con valores de 0 y 255
                tresh.put(row, col, valorT);    
            } 
        }
        return tresh;
    }
    public static Mat thinningIteracion(Mat im){
        Mat marker = new Mat(im.rows(),im.cols(),CvType.CV_8UC1);
        boolean validation;
        for (int i = 1; i < im.rows()-1; i++)
        {
            for (int j = 1; j < im.cols()-1; j++)
            {
                validation = false;
                
                //uchar p2 = im.at<uchar>(i-1, j);
                double [] P1 = im.get(i-1, j-1);
                int p1 = (int)P1[0];
                //uchar p3 = im.at<uchar>(i-1, j+1);
                double [] P2 = im.get(i, j-1);
                int p2 = (int)P2[0];
                //uchar p4 = im.at<uchar>(i, j+1);
                double [] P3 = im.get(i+1, j-1);
                int p3 = (int)P3[0];
                //uchar p5 = im.at<uchar>(i+1, j+1);
                double [] P4 = im.get(i-1, j);
                int p4 = (int)P4[0];
                //Posicion inicial
                double [] P5 = im.get(i, j);
                int p5 = (int)P5[0];
                //uchar p6 = im.at<uchar>(i+1, j);
                double [] P6 = im.get(i+1, j);
                int p6 = (int)P6[0];
                //uchar p7 = im.at<uchar>(i+1, j-1);
                double [] P7 = im.get(i-1, j+1);
                int p7 = (int)P7[0];
                //uchar p8 = im.at<uchar>(i, j-1);
                double [] P8 = im.get(i, j+1);
                int p8 = (int)P8[0];
                //uchar p9 = im.at<uchar>(i-1, j-1);
                double [] P9 = im.get(i+1, j+1);
                int p9 = (int)P9[0];
                
                if (p5 == 1 && (p2 == 0 || p4 == 0 || p6 == 0 || p8 == 0)){
                    //Los siguientes 4 else if corresponden a la plantilla 1
                    if ((p1 == 1 || p2 == 1 || p3 == 1) && (p7 == 1 || p8 == 1 || p9 == 1) && p4 == p6 && p6 == p6 ){
                        validation = true;
                    }else if((p3 == 1 || p6 == 1 || p9 == 1) && (p1 == 1 || p4 == 1 || p7 == 1) && p2 == p6 && p8 == p6){
                        validation = true;
                    }else if((p7 == 1 || p8 == 1 || p9 == 1) && (p1 == 1 || p2 == 1 || p3 == 1) && p4 == p6 && p6 == p6){
                        validation = true;
                    }else if((p1 == 1 || p4 == 1 || p7 == 1) && (p3 == 1 || p6 == 1 || p9 == 1) && p2 == p6 && p8 == p6){
                        validation = true;
                    //Los siguientes 4 else if corresponden a la plantilla 2
                    }else if((p1 == 1 || p2 == 1 || p3 == 1 || p4 == 1 || p7 == 1) && p8 == p6 && p6 == p6 && p9 == p2){
                        validation = true;
                    }else if((p1 == 1 || p2 == 1 || p3 == 1 || p6 == 1 || p9 == 1) && p7 == p2 && p8 == p6 && p4 == p6){
                        validation = true;
                    }else if((p3 == 1 || p6 == 1 || p9 == 1 || p8 == 1 || p7 == 1) && p1 == p2 && p2 == p6 && p4 == p6){
                        validation = true;
                    }else if((p1 == 1 || p4 == 1 || p7 == 1 || p8 == 1 || p9 == 1) && p3 == p2 && p2 == p6 && p6 == p6){
                        validation = true;
                    
                    //Los siguientes 4 else if corresponden a la plantilla 3
                    }else if((p1 == 1 || p2 == 1) && (p7 == 1 || p8 == 1) && (p3 == 1 || p9 == 1) && p4 == p6 && p5 == p2 && p6 == p2){
                        validation = true;
                    }else if((p3 == 1 || p6 == 1) && (p1 == 1 || p4 == 1) && (p7 == 1 || p9 == 1) && p2 == p6 && p5 == p2 && p8 == p2){
                        validation = true;
                    }else if((p8 == 1 || p9 == 1) && (p2 == 1 || p3 == 1) && (p1 == 1 || p7 == 1) && p6 == p6 && p4 == p2 && p5 == p2){
                        validation = true;
                    }else if((p4 == 1 || p7 == 1) && (p6 == 1 || p9 == 1) && (p1 == 1 || p3 == 1) && p8 == p6 && p2 == p2 && p5 == p2){
                        validation = true;
                    }
                }
                if (validation == false){
                    marker.put(i, j, new byte[]{0});//No se elimina pixel
                }else if(validation == true){
                    marker.put(i, j, new byte[]{1});//Se elimina pixel
                }
            }
        }
        for (int i = 1; i < im.rows()-1; i++)
        {
            for (int j = 1; j < im.cols()-1; j++)
            {
                double [] marcador = marker.get(i, j);
                if (marcador[0] == 1){
                    im.put(i, j, 0);//Limpieza de pixeles
                }
            }
        }
        return im;
    }
}
