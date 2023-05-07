package SegmentacionColor;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Segmentacion {
        static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }
	public static void main(String[] args) {
                String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
                String filePath = PATH + "fig.jpg";
		Mat img = Imgcodecs.imread(filePath),newImg = img.clone();
		
		Segmentacion.mostrarImagen("Original", img);
                //Point semillaAleatoria = new Point(430,350);
                //Point semillaAleatoria = new Point(430,150);
                Point semillaAleatoria = new Point(980,110);
//		Point semillaAleatoria = new Point(
//				(int) (Math.random() * (img.width()-1)), 
//				(int) (Math.random() * (img.height()-1)));
//		
		System.out.println("Dimens: "+img.size());
		
		System.out.println("Semilla aleatoria: "+semillaAleatoria);
		
		Region R = segmentacionPorCrecimiento(newImg.clone(), semillaAleatoria, 50);
		List<Pixel> RTemporal = R.getR();
		
		System.out.println(R.getR().size() + " " + R);
		
		for(Pixel unPixel : RTemporal) {
			int cX = unPixel.getX(),
				cY = unPixel.getY();
			Imgproc.circle(newImg, new Point(cX,cY), 1, new Scalar(250,0,0),-1);
		}
		mostrarImagen("Regionalizada", newImg, true);
		System.exit(0);
            
	}
	
	public static Region segmentacionPorCrecimiento(Mat src, Point randSeedPos, int umbral) {
		boolean crecimineto = true;
		int width  = src.width(),
			height = src.height();
		Region R = new Region();
		List<Pixel> Vecinos = new ArrayList<>();
                List<Pixel> tmpR;
		
		
		// Toma la semilla de la imagen
		double[] pixelD = src.get((int) randSeedPos.y, (int) randSeedPos.x);
		double[] pixValue = pixelD;
		double[] r = pixValue;			//	Referencia r -> pixColor
		
		// 1. Iniciamos la region R con la semilla p 
		Pixel pixel = new Pixel(pixValue, randSeedPos);
		pixel.adicionado = true;
		R.add(pixel);
				
		
		while (crecimineto) {		
			crecimineto = false;
			
			// 2. tomamos los vecinos de R
			tmpR = R.getR();
			for (Pixel tmpPixel : tmpR) {
				if (tmpPixel.adicionado) {
					int x = tmpPixel.getX(),
						y = tmpPixel.getY();
					Point nbr;

					// Pixel de arriba
					nbr = new Point(incrementVector(new double[]{x, y}, new double[]{0, -1}, new double[]{0, 0}, new double[]{width-1, height-1}));
					if (!tmpPixel.equals(nbr))
						Vecinos.add(new Pixel(src.get((int) nbr.y, (int) nbr.x), nbr));
					
					// Pixel de abajo
					nbr = new Point(incrementVector(new double[]{x, y}, new double[]{0, 1}, new double[]{0, 0}, new double[]{width-1, height-1}));
					if (!tmpPixel.equals(nbr))
						Vecinos.add(new Pixel(src.get((int) nbr.y, (int) nbr.x), nbr));
					
					// Pixel Derecho
					nbr = new Point(incrementVector(new double[]{x, y}, new double[]{1, 0}, new double[]{0, 0}, new double[]{width-1, height-1}));
					if (!tmpPixel.equals(nbr))
						Vecinos.add(new Pixel(src.get((int) nbr.y, (int) nbr.x), nbr));
					
					// Pixel Izquierdo
					nbr = new Point(incrementVector(new double[]{x, y}, new double[]{-1, 0}, new double[]{0, 0}, new double[]{width-1, height-1}));
					if (!tmpPixel.equals(nbr))
						Vecinos.add(new Pixel(src.get((int) nbr.y, (int) nbr.x), nbr));
					
					tmpPixel.adicionado = false;
				}
			}
				
			// 3. Aumentar la region con los pixeles que cumplan
			// f(pi)-r < fth
			for(Pixel internPixel : Vecinos) {
				boolean underUmbral = true;
				double[] pixelVal = internPixel.getVal();
				//System.out.print("Value "+Arrays.toString(pixelVal)+" & r "+Arrays.toString(r)+" -> Diference ");
				for (int i = 0; i < pixelVal.length; i++)
					if (pixelVal[i]-r[i]>umbral || pixelVal[i]-r[i]<-umbral) 
						underUmbral = false;
				
				if(underUmbral) {
					internPixel.adicionado = true;
					//System.out.println("Pixel Added: "+internPixel);
					R.add(internPixel);
					crecimineto = true;
				}
			}
			Vecinos.clear();//Limpia vecionos

			// 4. Actualizar el valor de referencia r
			r = R.calcularMedia(); 
		}
		return R;
	}
	
	public static Mat cargarImagen(String filename) {
		String PATH = "c:/Users/MARCO/Documents/NetBeansProjects/";
                String filePath = PATH + "rio.jpg";
		Mat img = Imgcodecs.imread(filePath);
		if (img.dataAddr()==0)
			throw new Error("No puedo abrir el archivo " + filePath);
		else 
			return img;
	}
    
    public static void mostrarImagen(String titulo, Mat imagen) {
        HighGui.namedWindow(titulo, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(titulo, imagen);
    }
    
    public static void mostrarImagen(String titulo, Mat imagen, boolean wait) {
        char k = '\0';
        while (k!=' ') {
            Segmentacion.mostrarImagen(titulo, imagen);
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
