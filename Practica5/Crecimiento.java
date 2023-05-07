package Practica5;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

public class Crecimiento {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    public static void main(String[] args) {
        
        String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
        String filePath = PATH + "senales2.jpg";
        Mat newImg = Imgcodecs.imread(filePath);

        motrarImagen("Original", newImg);
        
        if (newImg.dataAddr() == 0) {
            System.out.println("No puedo abrir el archivo " + filePath);
        } 
        else {
            Point semillaAleatoria = new Point((int) (Math.random() * (newImg.width()-1)), 
                                                (int) (Math.random() * (newImg.height()-1)));

            System.out.println("Semilla aleatoria: "+semillaAleatoria);

            Region R = SegmentarPorCrecimientoDeRegion(newImg.clone(), semillaAleatoria, 20);
            List<Pixel> RTemporal = R.getR();

            System.out.println(R.getR().size() + " " + R);

            for(Pixel unPixel : RTemporal) {
                    int cX = unPixel.getX(),
                            cY = unPixel.getY();
                    Imgproc.circle(newImg, new Point(cX,cY), 1, new Scalar(0,0,255),-1);
            }
            mostrarImagen("Regionalizada", newImg, true);
            //HighGui.waitKey(0);
        }
    }
	
	public static Region SegmentarPorCrecimientoDeRegion(Mat src, Point randSeedPos, int umbral) {
		boolean growing = true;
		int width  = src.width(),
			height = src.height();
		Region R = new Region();
		List<Pixel> neighborhood = new ArrayList<>();
                List<Pixel> tmpR;
		
		Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
		motrarImagen("Grises", src);
		
		// Toma la semilla de la imagen
		double[] pixelD = src.get((int) randSeedPos.y, (int) randSeedPos.x);
		int pixValue = (int) pixelD[0];
		double r = pixValue;			//	Referencia r -> pixColor
		
		// 1. Iniciamos la region R con la semilla p 
		Pixel pixel = new Pixel(pixValue, randSeedPos);
		pixel.adicionado = true;
		R.add(pixel);
		
		//															  y	  x
		//System.out.println("Corner Pixel: "+Arrays.toString(src.get(395,703)));
		System.out.println("width: "+width+" height: "+height);
				
		
		while (growing) {		
			growing = false;
			
			// 2. tomamos los vecinos de R
			tmpR = R.getR();
			for (Pixel tmpPixel : tmpR) {
				if (tmpPixel.adicionado) {
					int x = tmpPixel.getX(),
						y = tmpPixel.getY();
					Point nbr;

					//Pixel de arriba
                                        nbr = new Point(incrementVector(new double[]{x, y}, new double[]{0, -1}, new double[]{0, 0}, new double[]{width-1, height-1}));
                                        if (!tmpPixel.equals(nbr))
                                            neighborhood.add(new Pixel(src.get((int) nbr.y, (int) nbr.x)[0], nbr));
                                        // Pixel de abajo
                                        nbr = new Point(incrementVector(new double[]{x, y}, new double[]{0, 1}, new double[]{0, 0}, new double[]{width-1, height-1}));
                                        if (!tmpPixel.equals(nbr))
                                            neighborhood.add(new Pixel(src.get((int) nbr.y, (int) nbr.x)[0], nbr));
                                        
                                        // Pixel Derecho
                                        nbr = new Point(incrementVector(new double[]{x, y}, new double[]{1, 0}, new double[]{0, 0}, new double[]{width-1, height-1}));
                                        if (!tmpPixel.equals(nbr))
                                            neighborhood.add(new Pixel(src.get((int) nbr.y, (int) nbr.x)[0], nbr));
                                        
                                        // Pixel izquierdo
                                        nbr = new Point(incrementVector(new double[]{x, y}, new double[]{-1, 0}, new double[]{0, 0}, new double[]{width-1, height-1}));
                                        if (!tmpPixel.equals(nbr))
                                            neighborhood.add(new Pixel(src.get((int) nbr.y, (int) nbr.x)[0], nbr));
                                        
                                        tmpPixel.adicionado = false;
				}
			}
				
			// 3. Aumentar la region con los pixeles que cumplan
			// f(pi)-r < fth
			for(Pixel internPixel : neighborhood) {
				double pixelVal = internPixel.getVal();
				//System.out.println("Value "+pixelVal+" & r "+r+" -> Diference "+(pixelVal-r));
				if(pixelVal-r<umbral && pixelVal-r>-umbral) {
					internPixel.adicionado = true;
					//System.out.println("Pixel Added: "+internPixel);
					R.add(internPixel);
					growing = true;
				}
			}
			
			//System.out.println("***************");
			neighborhood.clear();

			// 4. Actualizar el valor de referencia r
			r = R.calcularMedia(); 
		}
		return R;
	}
    
    public static void motrarImagen(String titulo, Mat imagen) {
        HighGui.namedWindow(titulo, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(titulo, imagen);
    }
    
    public static void mostrarImagen(String titulo, Mat imagen, boolean wait) {
        char k = '\0';
        while (k!=' ') {
            motrarImagen(titulo, imagen);
            if (wait) {
                k = (char) HighGui.waitKey(0);
            } else k = ' ';
        }
    }
	
	public static double[] incrementVector(double[] vector, double[] increment, double[] lowerBound, double[] upperBound) throws ArrayIndexOutOfBoundsException, ArithmeticException {
		if (vector.length<increment.length) 
			throw new ArrayIndexOutOfBoundsException("increment bigger than vector");
		
		for (int i=0; i<vector.length; i++) {
			vector[i] += i<increment.length
					? increment[i]
					: 0;
			if (vector[i]<lowerBound[i])
				vector[i] = lowerBound[i];
			else if (vector[i]>upperBound[i])
				vector[i] = upperBound[i];
		}
		
		return vector;
	}
}
