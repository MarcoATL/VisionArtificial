/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Kmeans;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author MARCO
 */

public class Kmeans {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
        String filePath = PATH + "/mar.jpg";
        Mat img = Imgcodecs.imread(filePath);
        if (img.dataAddr() == 0) {
            System.out.println("No puedo abrir el archivo\n " + filePath);
            } 
        else {
            List<Mat> clusters = cluster(img, 3);
            mostrarImagen("imagen", img);
            int n = 0;
            for(Mat cluster: clusters) {
                mostrarImagen("clusters "+n, cluster);
                n++;
                }
            }
        HighGui.waitKey(0);
        System.exit(0);
    }
    public static void mostrarImagen(String titulo, Mat imagen) {
        HighGui.namedWindow(titulo, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(titulo, imagen);
    }
    /**
     * Agrupa los pixeles en k clusters
     * @param cutout la imagen a segmentar
     * @param k el numero de clusters
     * @return devuelve el conjunto de clusters agrupados cada uno en una matriz 
     * de imagen
     */
    public static List<Mat> cluster(Mat cutout, int k) {
        // remodela la imagen en una sola dimension todos los pixeles
        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        // convierte samples a samples de 32 bits flotantes
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
        // las etiquetas
        Mat labels = new Mat();
        // el criterio de agrupacion 
        // TermCriteria.COUNT, 
        // 100 numero de iteraciones
        // 1 presicion deseada (epsilon)
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 200, 1);
        // los centroides
        Mat centers = new Mat();
        // Aplicamos el algoritmo de k-means
        // samples32f la imagen flotante 32 bits
        // k el numero de clusters o agrupaciones
        // labels las etiquetas
        // criteria el criterio de ejecucion del algoritmo
        // 1 los intentos
        // KMEANS_PP_CENTERS la bandera de agrupacion
        // centers Matriz de salida de los centros de los grupos, una fila por 
        // cada centro de cada grupo
        Core.kmeans(samples32f, k = 7, labels, criteria, 1, 
                Core.KMEANS_PP_CENTERS, centers);
        // devuelve los grupos, las etiquetas y la imagen original
        return showClusters(cutout, labels, centers);
    }
    /**
     * Muestra los clusters obtenidos del algoritmo de k-means
     * 
     * @param cutout la imagen a segmentar
     * @param labels las etiquetas
     * @param centers los centros
     * @return 
     */
    private static List<Mat> showClusters(Mat cutout, Mat labels, Mat centers) {
        // convierte los centros al dominio 0 - 255
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        // remodela la imagen a sus tres canales
        centers.reshape(3);
        // Lista de grupos
        List<Mat> clusters = new ArrayList<Mat>();
        for (int i = 0; i < centers.rows(); i++) {
            clusters.add(Mat.zeros(cutout.size(), cutout.type()));
            }
        // Mapea los centroides
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < centers.rows(); i++) {
            counts.put(i, 0);
            }
        // llena los clusters con los pixeles 
        int rows = 0;
        for (int y = 0; y < cutout.rows(); y++) {
            for (int x = 0; x < cutout.cols(); x++) {
                int label = (int) labels.get(rows, 0)[0];
                int r = (int) centers.get(label, 2)[0];
                int g = (int) centers.get(label, 1)[0];
                int b = (int) centers.get(label, 0)[0];
                counts.put(label, counts.get(label) + 1);
                clusters.get(label).put(y, x, b, g, r);
                rows++;
                }
            }
        // imprime el conjunto de pixeles encontrados para cada k
        System.out.println(counts);
        //devuelve la lista de clusters
        return clusters;
    }
}