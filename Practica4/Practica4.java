package Practica4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 * @author MATL
 */
public class Practica4 {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
	static String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
	static String filePath = "euros.jpg";
        
    public static void main(String[] args) {
        //~ 1. Cargamos la imagen
        Mat newImage = cargarImagen(Practica4.filePath);
        
        mostrarImagen("Original", newImage);
        Mat segmentada = contarObjetos(newImage.clone());
        showImg("Contornos", segmentada, true);
        
        
        System.exit(0);
    }
    
    public static Mat contarObjetos(Mat fuente) {
        // # 2. Convertimos a escala de grises
        Mat gris = new Mat();
        Imgproc.cvtColor(fuente, gris, Imgproc.COLOR_BGR2GRAY);
        // # 3. Aplicar suavizado Gaussiano
        Mat gauss = aberrarGaussiano(gris, 11);
        mostrarImagen("Suavisado", gauss);
        // # 4. Detectamos los contornos con Canny
        Mat canny = new Mat();
        Imgproc.Canny(gauss, canny, 60, 180);
        mostrarImagen("Canny", canny);
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
        etiquetarObjetos(contornos, fuente);
        return fuente;
    }
    public static void etiquetarObjetos(List<MatOfPoint> contornos, Mat image) {
		Scanner scanner = new Scanner(System.in);
		ObjectFound[] objects = new ObjectFound[contornos.size()];
		
		try 
			{ objects = readObjectsForImg(Practica4.filePath); } 
		catch (IOException | ClassNotFoundException e) 
			{ System.out.println("Archivo de objetos no existe"); }
		
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
            System.out.println("Area: " + area);
            MatOfPoint2f momento2f = new MatOfPoint2f(momento.toArray());
            double perimetro = Imgproc.arcLength(momento2f, true);
            System.out.println("Perimetro: " + perimetro);
			
			ObjectFound obj = null;
			for (ObjectFound object : objects)
				if (object!=null && object.match(cX, cY, perimetro, area))
					obj = object;
			
            Imgproc.circle(image, new Point(cX,cY), 5, new Scalar(0,255,0),-1);
            Imgproc.putText(image, "x:"+ cX +",y:"+ cY, 
            new Point(cX, cY) , 1, 1, new Scalar(0,0,0));
			
			if (obj==null)
				obj = new ObjectFound(cX, cY, perimetro, area);
			
			while (obj.name.isBlank()) {
				System.out.println("Nombre del objeto: ");
				obj.name = scanner.nextLine();
			}
			objects[i] = obj;
			
            // etiqueta
            Imgproc.putText(image, obj.name+"-"+(i+1), 
                    new Point(cX, cY-20) , 1, 1, new Scalar(255,0,0));
            Rect aRectangle = Imgproc.boundingRect(momento);
            Imgproc.rectangle(image, new Point(aRectangle.x, aRectangle.y), 
                    new Point(aRectangle.x+aRectangle.width, 
                              aRectangle.y+aRectangle.height), 
                    new Scalar(0,255,0), 2);
            showImg("Input", image, true);
		}
		try {
			writeObjectsForImg(Practica4.filePath, objects);
		} catch (IOException ex) {
			System.out.println("No fue posible guardar archivo");
			Logger.getLogger(Practica4.class.getName()).log(Level.SEVERE, null, ex);
		}
    }
	
	
	public static Mat cargarImagen(String filename) {
		filename = PATH + filename;
		Mat img = Imgcodecs.imread(filename);
		if (img.dataAddr()==0)
			throw new Error("No puedo abrir el archivo " + filename);
		else 
			return img;
	}
    
    public static void mostrarImagen(String titulo, Mat imagen) {
        HighGui.namedWindow(titulo, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(titulo, imagen);
    }
    
    public static void showImg(String titulo, Mat imagen, boolean wait) {
        char k = '\0';
        while (k!=' ') {
            mostrarImagen(titulo, imagen);
            if (wait) {
                k = (char) HighGui.waitKey(0);
            } else k = ' ';
        }
    }
	
	public static ObjectFound[] readObjectsForImg(String filename) throws ClassNotFoundException, IOException {
		System.out.println("reading on"+PATH+filename.replaceAll("\\.\\w+$", "")+".obj");
		FileInputStream fis = new FileInputStream(PATH+filename.replaceAll("\\.\\w+$", "")+".obj");
				
		ObjectInputStream ois = new ObjectInputStream(fis);
			
		ObjectFound[] objects = (ObjectFound[]) ois.readObject();
		System.out.println("i read "+objects.length);
		return objects;
	}
	
	public static void writeObjectsForImg(String filename, ObjectFound[] objects) throws FileNotFoundException, IOException {
		System.out.println("saving on:"+PATH+filename.replaceAll("\\.\\w+$", "")+".obj");
		FileOutputStream fis = new FileOutputStream(PATH+filename.replaceAll("\\.\\w+$", "")+".obj");
				
		ObjectOutputStream ois = new ObjectOutputStream(fis);
		
		ois.writeObject(objects);
		System.out.println("saved");
	}
	
    
    public static Mat aberrarGaussiano(Mat gris, int size) {
        Mat gauss = new Mat();
        Imgproc.GaussianBlur(gris, gauss, new Size(size, size), 0);
        return gauss;
    }
}
