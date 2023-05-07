/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FiltradoTam;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 *
 * @author Saul
 */
public class FilterBySize {
    private final int perimetroMaximo;
    private final int perimetroMinimo;
    private final int areaMaxima;
    private final int areaMinima;

    public FilterBySize(int perimetroMaximo, int perimetroMin, int areaMax, int areaMin) {
        this.perimetroMaximo = perimetroMaximo;
        this.perimetroMinimo = perimetroMin;
        this.areaMaxima = areaMax;
        this.areaMinima = areaMin;
    }
    public Mat contarObjetos(Mat fuente) {
        // # 2. Convertimos a escala de grises
        Mat gris = new Mat();
        Imgproc.cvtColor(fuente, gris, Imgproc.COLOR_BGR2GRAY);
        // # 3. Aplicar suavizado Gaussiano
        Mat gauss = aberrarGaussiano(gris, 11);
        Filter.mostrarImagen("suavisado", gauss);
        // # 4. Detectamos los contornos con Canny
        Mat canny = new Mat();
        Imgproc.Canny(gauss, canny, 60, 180);
        Filter.mostrarImagen("canny", canny);
        // # 5. Buscamos los contornos
        List<MatOfPoint> contornos = new ArrayList<>();
        Mat jerarquia = new Mat();
        Imgproc.findContours(canny.clone(), contornos, jerarquia, 
                Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE);
        // # 6. Dibujar los contornos encontrados
        Imgproc.drawContours(fuente, contornos, -1, new Scalar(0,0,255), 2);
        // # 7. Mostramos el número de monedas por consola
        System.out.println("He encontrado " + contornos.size() + " objetos ");
        // # etiquetado de objetos
        FiltrarObjetos(contornos, fuente);
        return fuente;
    }
    public Mat aberrarGaussiano(Mat gris, int size) {
        Mat gauss = new Mat();
        Imgproc.GaussianBlur(gris, gauss, new Size(size, size), 0);
        return gauss;
    }
    public void FiltrarObjetos(List<MatOfPoint> contornos, Mat image) {
        for(int i=0; i<contornos.size(); i++) {
            MatOfPoint momento = contornos.get(i);
            Moments M = Imgproc.moments(momento);
            System.out.println(M);
            double centroideDoubleEnX = M.get_m10() / M.get_m00();
            double centroideDoubleEnY = M.get_m01() / M.get_m00();
            int cX = (int)centroideDoubleEnX;
            int cY = (int)centroideDoubleEnY;
            System.out.println("centroide(" + cX + ", " + cY + ")");
            double area = Imgproc.contourArea(momento);
            System.out.println("Area: " + area+"Area maxima: " + this.areaMaxima);
            MatOfPoint2f momento2f = new MatOfPoint2f(momento.toArray());
            double perimetro = Imgproc.arcLength(momento2f, true);
            System.out.println("Perimetro: " + perimetro + 
                            " Perimetro maximo: " + this.perimetroMaximo);

            //Filtrado por area
            //if(area < areaMaxima){
            //Filtrado por perimetro
            //if(perimetro < perimetroMaximo){
            //Filtrado por perimetro y area
            //if((perimetro < perimetroMaximo) && (area < areaMaxima)) { 
            //Filtrado por perimetro y area con limites
            if((perimetro < perimetroMaximo) && (perimetro > perimetroMinimo) && (area > areaMinima) && (area < areaMaxima)) { 
                
                Imgproc.circle(image, new Point(cX,cY), 5, 
                        new Scalar(0,255,0),-1);
                Imgproc.putText(image, "x:"+ cX +",y:"+ cY, 
                        new Point(cX, cY) , 1, 1, new Scalar(0,0,0));
                // etiqueta
                Imgproc.putText(image, "" +(i+1), 
                        new Point(cX, cY-20) , 1, 1, new Scalar(255,0,0));
                Rect aRectangle = Imgproc.boundingRect(momento);
                Imgproc.rectangle(image, new Point(aRectangle.x, aRectangle.y), 
                        new Point(aRectangle.x+aRectangle.width, 
                                  aRectangle.y+aRectangle.height), 
                        new Scalar(0,255,0), 2);
                Filter.mostrarImagen("input", image);
                HighGui.waitKey(0);
            }
        }
    }
}
