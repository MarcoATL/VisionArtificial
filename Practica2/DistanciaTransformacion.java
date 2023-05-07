/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Distancia;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


class RGB {
    private double r;
    private double g;
    private double b;
    public RGB(double b, double g, double r) {
        this.b = b;
        this.g = g;
        this.r = r;
    }
    public double [] getRGB() {
        double [] rgb = {
            b, g, r
        };
        return rgb;
    }
    public String toString() {
        return "("+b+","+g+","+r+")";
    }
}



/**
 *
 * @author Saul
 */
public class DistanciaTransformacion {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception {
        String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
        String filePath = PATH + "estrellas.jpg";

        Mat newImage = Imgcodecs.imread(filePath);

        if (newImage.dataAddr() == 0) {
            System.out.println("No puedo abrir el archivo " + filePath);
            } 
        else {
            HighGui.namedWindow("input", HighGui.WINDOW_AUTOSIZE);
            HighGui.imshow("input", newImage);
            // cambia el fondo blanco a negro; el propósito es preparar la 
            // transformación posterior
            Mat imgNegro = Imgcodecs.imread(filePath);
            for(int row=0; row<newImage.rows(); row++) {
		for(int col=0; col<newImage.cols(); col++) {
                        double [] pixel = newImage.get(row, col);
                        if( pixel[0]>200 && pixel[1]>200 && pixel[2]>200) {
                            imgNegro.put(row, col, new byte[]{0,0,0});
                            }
                    }
                }
                
            String input = "Cambio fondo de imagen";
            HighGui.namedWindow(input, HighGui.WINDOW_AUTOSIZE);
            
            HighGui.imshow(input, imgNegro);
            // Usa filter2D y Laplacian para mejorar el contraste de la imagen, 
            // nítido
            Mat imgLaplace = new Mat(newImage.width(), newImage.height(), newImage.type());
            Mat sharp = newImage;
            Mat kernel = new Mat(3,3,Imgcodecs.IMREAD_COLOR);
            kernel.put(0,0, 1, 1, 1, 1, -8, 1, 1, 1, 1);
            Imgproc.filter2D(sharp, imgLaplace, -1, kernel, 
                    new Point(-1, -1), 0, Core.BORDER_DEFAULT);
//            newImage.convertTo(sharp, CvType.CV_32F);
            Mat imgResult = Imgcodecs.imread(filePath);//sharp - imgLaplance;
            for(int row=0; row<newImage.rows(); row++) {
		for(int col=0; col<newImage.cols(); col++) {
                    double [] pixelSharp = sharp.get(row, col);
                    double [] pixelLaplace = imgLaplace.get(row, col);
                    double [] byteDouble = {
                        pixelSharp[0] - pixelLaplace[0],
                        pixelSharp[1] - pixelLaplace[1],
                        pixelSharp[2] - pixelLaplace[2]
                        };
                    imgResult.put(row, col, byteDouble);
                    }
                }
                for(int row=0; row<imgResult.rows(); row++) {
		for(int col=0; col<imgResult.cols(); col++) {
                        double [] pixel = imgResult.get(row, col);
                        if( pixel[0]>200 && pixel[1]>200 && pixel[2]>200) {
                            imgResult.put(row, col, new byte[]{0,0,0});
                            }
                    }
                }
            // show
            imgResult.convertTo(imgResult, CvType.CV_8UC3);
            imgLaplace.convertTo(imgLaplace, CvType.CV_8UC3);
            HighGui.namedWindow("Sharp image", HighGui.WINDOW_AUTOSIZE);
            HighGui.imshow("Sharp image", imgResult);
            
            // la imagen binaria se convierte en imagen binaria a través del 
            // umbral
            Mat binaryImag = new Mat();
            Imgproc.cvtColor(newImage, binaryImag, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(binaryImag, binaryImag, 60, 255, 
                    Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY_INV);                      
            HighGui.namedWindow("binary image", HighGui.WINDOW_AUTOSIZE);
            HighGui.imshow("binary image", binaryImag);
            
            // transformación de distancia
            Mat distImg = 
                    new Mat();
            Imgproc.distanceTransform(
                    binaryImag, distImg, Imgproc.DIST_L1, 3);
            // distanceTransform(InputArray  src, OutputArray dst,  
            // OutputArray  labels,  int  distanceType,  int maskSize,  
            // int labelType=DIST_LABEL_CCOMP)
            // distanceType = DIST_L1 / DIST_L2,
            // maskSize = 3x3, el último admite 5x5, se recomienda 3x3,
            // etiqueta salida de diagrama de Veno discreta
            // dst emite un número de coma flotante de 8 o 32 bits, canal único, 
            // el tamaño es el mismo que el de la imagen de entrada
            
            // Normaliza el resultado de la transformación de distancia a [0~1]
            Core.normalize(distImg, distImg, 0.0, 1.0, Core.NORM_MINMAX);
            //HighGui.imshow("distance image", distImg);
            // Usa el umbral y binariza de nuevo para obtener la etiqueta 
            // (binario de nuevo)
            Imgproc.threshold(distImg, distImg, 0.2, 1, Imgproc.THRESH_BINARY);
            // Recorrido para obtener cada erosión máxima
            Mat kernel1 = new Mat(13, 13, CvType.CV_8UC1);
            Imgproc.erode(distImg, distImg, kernel1, new Point(-1, -1));
            //HighGui.imshow("distance binary image", distImg);
            
            // marcadores (buscar contornos - findContours)
            Mat dist_8u = new Mat();
            distImg.convertTo(dist_8u, CvType.CV_8U);
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();;
            Imgproc.findContours(dist_8u, contours, new Mat(), 
                    Imgproc.RETR_EXTERNAL, 
                    Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
            Scalar WHITE = new Scalar(255,255,255);
            Scalar BLACK = new Scalar(0,0,0);
            Mat markers =  new Mat(newImage.size(), CvType.CV_32SC1);
            int thickness = -1;
            for (int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(markers, contours, i, 
                        new Scalar(i+1,i+1,i+1), thickness);
                }
            Imgproc.circle(markers, new Point(5, 5), 3, 
                    new Scalar(255, 255, 255), -1);
//            HighGui.imshow("mis marcadores", markers);

            // realizar cuenca hidrográfica (basada en la teoría de inmersión)
            Imgproc.watershed(newImage, markers);
            Mat mark = new Mat(markers.size(), CvType.CV_8UC1);
            markers.convertTo(mark, CvType.CV_8UC1);
            //src.convertTo(dst, type, scale, shift)
            // dst: propósito matriz;
            // tipo: El tipo de matriz de salida requerido, o más específico, la 
            // profundidad de la matriz de salida. Si el valor es negativo 
            // (común-1), la matriz de salida es la misma que el tipo de matriz 
            // de entrada;
            // escala: factor de escala;
            // shift: el valor agregado después de escalar los elementos de la 
            // matriz de entrada proporcionalmente;
            Core.bitwise_not(mark, mark, new Mat());
            HighGui.imshow("watershed image", mark);
            
            // generate random color
            LinkedList<RGB> colores = new LinkedList<>();
            for(int i=0; i<contours.size(); i++) {
                int b = (int)(Math.random() * 256.0);
                int g = (int)(Math.random() * 256.0);
                int r = (int)(Math.random() * 256.0);
                System.out.println(b + " " + g + " " + r);
                colores.add(new RGB(b,g,r));
                }
            // rellena con color y muestra el resultado final (colorea el 
            // resultado de salida para cada área dividida)
            Mat dst = new Mat(markers.size(), CvType.CV_8UC3);
            for(int row=0; row<markers.rows(); row++) {
                for(int col=0; col<markers.cols(); col++) {
                    double [] indexD = markers.get(row, col);
                    int index = (int)indexD[0];
//                    System.out.println("index: " + index + 
//                            " contorno size: " + contours.size());
                    if( index>0 && index<=contours.size()) {
                        RGB pixel = colores.get(index-1);
//                        System.out.println(pixel);
                        double [] doublePixel = pixel.getRGB();
//                        System.out.println("pix length: " + doublePixel.length);
                        dst.put(row, col, doublePixel);
                        }
                    else {
                        dst.put(row, col, new byte[] {0, 0, 0});
                        }
                    }
            }
            HighGui.imshow("Final Result", dst);
            }
        HighGui.waitKey(0);
        System.exit(0);
    }
}
/*
#include "stdafx.h"
#include<opencv2/opencv.hpp>
#include<iostream>
#include<math.h>
 
using namespace std;
using namespace cv;
 
int main(int argc, char*argv) {
    Mat src;
    src = imread("C:/Users/59235/Desktop/imag/Pentagram.jpg");
    if (!src.data) {
	printf("could not load image...\n");
	return -1;
	}
    namedWindow("input", CV_WINDOW_AUTOSIZE);
    imshow("input", src);
 
    // cambia el fondo blanco a negro; el propósito es preparar la transformación posterior
    for (int row = 0; row < src.rows; row++) {
	for (int col = 0; col<src.cols; col++) {
            if( src.at<Vec3b>(row, col)[0]>200 && 
                src.at<Vec3b>(row, col)[1]>200 && 
                src.at<Vec3b>(row, col)[2]>200) {
		src.at<Vec3b>(row, col)[0] = 0;
		src.at<Vec3b>(row, col)[1] = 0;
		src.at<Vec3b>(row, col)[2] = 0;
		}
            }
	}
    const char*input = "change backgroud image";
    namedWindow(input, CV_WINDOW_AUTOSIZE);
    imshow(input, src);
 
    // Use filter2D y Laplacian para mejorar el contraste de la imagen, nítido
    Mat imgLaplance;
    Mat sharp = src;
    Mat kernel = (Mat_<float>(3, 3) << 1, 1, 1, 1, -8, 1, 1, 1, 1);
    filter2D(sharp, imgLaplance, CV_32F, kernel, Point(-1, -1), 0, BORDER_DEFAULT);
    src.convertTo(sharp, CV_32F);
    Mat imgResult = sharp - imgLaplance;
 
    // show
    imgResult.convertTo(imgResult, CV_8UC3);
    imgLaplance.convertTo(imgLaplance, CV_8UC3);
    namedWindow("Sharp image", CV_WINDOW_AUTOSIZE);
    imshow("Sharp image", imgResult);
    // src = resultImg; // copy back
 
    // la imagen binaria se convierte en imagen binaria a través del umbral
    Mat binaryImag;
    cvtColor(src, binaryImag, CV_BGR2GRAY);
    threshold(binaryImag, binaryImag, 40, 255, THRESH_OTSU | THRESH_BINARY);
    namedWindow("binary image", CV_WINDOW_AUTOSIZE);
    imshow("binary image", binaryImag);
 
    // transformación de distancia
    Mat distImg;
    distanceTransform(binaryImag, distImg, DIST_L1, 3, 5);
    //cv::distanceTransform(InputArray  src, OutputArray dst,  OutputArray  
    //          labels,  int  distanceType,  int maskSize,  
    //                      int labelType=DIST_LABEL_CCOMP)
    //distanceType = DIST_L1 / DIST_L2,
    // maskSize = 3x3, el último admite 5x5, se recomienda 3x3,
    // etiqueta salida de diagrama de Veno discreta
    // dst emite un número de coma flotante de 8 o 32 bits, canal único, el 
    // tamaño es el mismo que el de la imagen de entrada
 
    // Normaliza el resultado de la transformación de distancia a [0 ~ 1]
    normalize(distImg, distImg, 0, 1, NORM_MINMAX);
    imshow("distance image", distImg);
 
    // Usa el umbral y binariza de nuevo para obtener la etiqueta 
    // (binario de nuevo)
    threshold(distImg, distImg, .2, 1, THRESH_BINARY);
 
    // Recorrído para obtener cada erosión máxima
    Mat kernel1 = Mat::ones(13, 13, CV_8UC1);
    erode(distImg, distImg, kernel1, Point(-1, -1));
    imshow("distance binary image", distImg);
 
    // marcadores (buscar contornos - findContours)
    Mat dist_8u;
    distImg.convertTo(dist_8u, CV_8U);
    vector<vector<Point>> contours;
    findContours(dist_8u, contours, RETR_EXTERNAL, 
                    CHAIN_APPROX_SIMPLE, Point(0, 0));
 
    // crear creadores (dibujar contornos-dibujarContours)
    Mat markers = Mat::zeros(src.size(), CV_32SC1);
    for (size_t i = 0; i < contours.size(); i++) {
	drawContours(markers, contours, static_cast<int>(i), 
                    Scalar::all(static_cast<int>(i) + 1), -1);
	}
    circle(markers, Point(5, 5), 3, Scalar(255, 255, 255), -1);
    imshow("my markers", markers * 1000);
 
    // realizar cuenca hidrográfica (basada en la teoría de inmersión)
    watershed(src, markers);
    Mat mark = Mat::zeros(markers.size(), CV_8UC1);
    markers.convertTo(mark, CV_8UC1);
    //src.convertTo(dst, type, scale, shift)
    // dst: propósito matriz;
    // tipo: El tipo de matriz de salida requerido, o más específicamente, 
    // la profundidad de la matriz de salida. Si el valor es negativo (común-1), 
    // la matriz de salida es la misma que el tipo de matriz de entrada;
    // escala: factor de escala;
    // shift: el valor agregado después de escalar los elementos de la matriz 
    // de entrada proporcionalmente;
    bitwise_not(mark, mark, Mat());
    imshow("watershed image", mark);
 
 
    // generate random color
    vector<Vec3b> colors;
    for (size_t i = 0; i < contours.size(); i++) {
        int r = theRNG().uniform(0, 255);
	int g = theRNG().uniform(0, 255);
	int b = theRNG().uniform(0, 255);
	colors.push_back(Vec3b((uchar)b, (uchar)g, (uchar)r));
	}
 
    // rellena con color y muestra el resultado final (colorea el resultado de 
    // salida para cada área dividida)
    Mat dst = Mat::zeros(markers.size(), CV_8UC3);
    for (int row = 0; row < markers.rows; row++) {
	for (int col = 0; col < markers.cols; col++) {
            int index = markers.at<int>(row, col);
            if (index > 0 && index <= static_cast<int>(contours.size())) {
		dst.at<Vec3b>(row, col) = colors[index - 1];
		}
            else {
		dst.at<Vec3b>(row, col) = Vec3b(0, 0, 0);
		}
            }
	}
    imshow("Final Result", dst);
 
    waitKey(0);
    return 0;
}

*/
