
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.CvType;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class procesamiento{
    static Mat convolveMat(Mat src, Mat kernel) {
        return filter2DHandle(src,kernel);
    }
    static Mat filter2DHandle(Mat src,Mat kernel) {
        Mat destini = new Mat(src.size(),src.type());
        Imgproc.filter2D(src, destini, -1, kernel, new Point(-1,-1), 0,Core.BORDER_REFLECT);
        return destini;
    }
}
/**
 *
 * @author MARCO
 */
public class Main {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        // El acceso donde se encuentran sus imágenes almacenadas
        String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
        String filePath = PATH + "marble.jpg";
        // carga la imagen en niveles de gris
        Mat newImage = Imgcodecs.imread(filePath,Imgcodecs.IMREAD_GRAYSCALE);
        if (newImage.dataAddr() == 0) {
            System.out.println("No puedo abrir el archivo " + filePath);
        }
        else {
            System.out.println("Imagen encontrada");
            HighGui.imshow("Imagen a gris", newImage);
            HighGui.waitKey();
            
            //FILTRADO Pasa bajos suave
            
//            Mat kernel_PB = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
//            kernel_PB.put(0,0,1,1,1,1,1,1,1,1,1);
//            newImage = procesamiento.convolveMat(newImage,kernel_PB);
//            HighGui.imshow("Pasa bajos suave", newImage);
//            HighGui.waitKey();
            
            //FILTRADO Pasa bajos medio
//            Mat kernel_PBM = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
//            kernel_PBM.put(0,0,1,1,1,1,2,1,1,1,1);
//            newImage = procesamiento.convolveMat(newImage,kernel_PBM);
//            HighGui.imshow("Pasa bajos medio", newImage);
//            HighGui.waitKey();
            
            Mat kernel = new Mat(3, 3, Imgcodecs.IMREAD_COLOR);
            kernel.put(0, 0, 0, 1, 0, 1, -4, 1, 0, 1, 0 );
            Mat result = procesamiento.convolveMat(newImage,kernel);
            HighGui.imshow("Imagen con kernel 1",result);
            HighGui.waitKey();
            
            
            Mat kernel_2 = new Mat(3, 3, Imgcodecs.IMREAD_COLOR);
            kernel_2.put(0, 0, 1, 1, 1, 1, -8, 1, 1, 1, 1 );
            Mat result_2 = procesamiento.convolveMat(newImage,kernel_2);
            HighGui.imshow("Imagen con kernel 2",result_2);
            HighGui.waitKey();
            
            //KERNELS ROBERTS
            Mat kernel_robert1 = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_robert1.put(0,0,0,0,0,0,-1,0,0,0,1);
            Mat result_R = procesamiento.convolveMat(newImage,kernel_robert1);
            HighGui.imshow("Robert 1", result_R);
            HighGui.waitKey();
            
            Mat kernel_robert2 = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_robert2.put(0,0,0,0,0,0,0,-1,0,1,0);
            Mat result_R2 = procesamiento.convolveMat(newImage,kernel_robert2);
            HighGui.imshow("Robert 2", result_R2);
            HighGui.waitKey();
            
            Mat ImageRobert = new Mat();
            Core.add(result_R, result_R2, ImageRobert);
            HighGui.imshow("Image Robert", ImageRobert);
            HighGui.waitKey();
            
            //KERNELS PREWITT
            Mat kernel_prewitt = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_prewitt.put(0,0,-1,-1,-1,0,0,0,1,1,1);
            Mat result_P = procesamiento.convolveMat(newImage,kernel_prewitt);
            HighGui.imshow("Prewitt Horizontal", result_P);
            HighGui.waitKey();
            
            Mat kernel_prewitt2 = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_prewitt2.put(0,0,-1,0,1,-1,0,1,-1,0,1);
            Mat result_P2 = procesamiento.convolveMat(newImage,kernel_prewitt2);
            HighGui.imshow("Prewitt vertical", result_P2);
            HighGui.waitKey();
            
            Mat kernel_prewitt3 = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_prewitt3.put(0,0,-1,-1,0,-1,0,1,0,1,1);
            Mat result_P3 = procesamiento.convolveMat(newImage,kernel_prewitt3);
            HighGui.imshow("Prewitt diagonal positiva", result_P3);
            HighGui.waitKey();
            
            
            Mat kernel_prewitt4 = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_prewitt4.put(0,0,0,1,1,-1,0,1,-1,-1,0);
            Mat result_P4 = procesamiento.convolveMat(newImage,kernel_prewitt4);
            HighGui.imshow("Prewitt diagonal negativa", result_P4);
            HighGui.waitKey();
            
            Mat ImagePrewitt = new Mat();
            Mat ImagePrewitt2 = new Mat();
            Mat ImagePrewittResult = new Mat();
            Core.add(result_P, result_P2, ImagePrewitt);
            Core.add(result_P3,result_P4, ImagePrewitt2);
            Core.add(ImagePrewitt,ImagePrewitt2,ImagePrewittResult);
            HighGui.imshow("Image Prewitt", ImagePrewitt);
            HighGui.waitKey();
            
            //KERNELS SOBEL
            
            Mat kernel_sobel = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_sobel.put(0,0,-1,-2,-1,0,0,0,1,2,1);
            Mat result_S = procesamiento.convolveMat(newImage,kernel_sobel);
            HighGui.imshow("Sobel Horizontal", result_S);
            HighGui.waitKey();
            
            Mat kernel_sobel2 = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_sobel2.put(0,0,-1,0,1,-2,0,2,-1,0,1);
            Mat result_S2 = procesamiento.convolveMat(newImage,kernel_sobel2);
            HighGui.imshow("Sobel vertical", result_S2);
            HighGui.waitKey();
            
            Mat kernel_sobel3 = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_sobel3.put(0,0,-2,-1,0,-1,0,1,0,1,2);
            Mat result_S3 = procesamiento.convolveMat(newImage,kernel_sobel3);
            HighGui.imshow("Sobel diagonal positiva", result_S3);
            HighGui.waitKey();
            
            
            Mat kernel_sobel4 = new Mat(3,3, Imgcodecs.IMREAD_COLOR);
            kernel_sobel4.put(0,0,0,1,2,-2,0,1,-2,-1,0);
            Mat result_S4 = procesamiento.convolveMat(newImage,kernel_sobel4);
            HighGui.imshow("Sobel diagonal negativa", result_S4);
            HighGui.waitKey();
            
            Mat ImageSobel = new Mat();
            Mat ImageSobel2 = new Mat();
            Mat ImageSobelResult = new Mat();
            Core.add(result_S, result_S2, ImageSobel);
            Core.add(result_S3, result_S4, ImageSobel2);
            Core.add(ImageSobel,ImageSobel2,ImageSobelResult);
            HighGui.imshow("Image Sobel", ImageSobelResult);
            HighGui.waitKey();
            
            //Umbralizado
            Mat Thresh = new Mat(newImage.rows(), newImage.cols(), newImage.type());
            Imgproc.threshold(newImage, Thresh, 172, 255, Imgproc.THRESH_BINARY);
            HighGui.imshow("Image threshold", Thresh);
            HighGui.waitKey();
            

        }
        System.exit(0);
    }
}
    
