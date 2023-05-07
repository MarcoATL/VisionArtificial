package Practica4;

import java.io.Serializable;

/**
 * @author MATL
 */
public class ObjectFound implements Serializable {
	private final int centroideX, centroideY;
	private final double perimetro, area;
	public String name;

	public ObjectFound(int centroideX, int centroideY, double perimetro, double area, String name) {
		this.centroideX = centroideX;
		this.centroideY = centroideY;
		this.perimetro	= perimetro;
		this.area		= area;
		this.name		= name;
	}
	
	public ObjectFound(int centroideX, int centroideY, double perimetro, double area) {
		this.centroideX = centroideX;
		this.centroideY = centroideY;
		this.perimetro	= perimetro;
		this.area		= area;
		this.name		= "";
	}
	
	public boolean match(int centroideX, int centroideY, double perimetro, double area) {
		return centroideX==this.centroideX 
				&& centroideY==this.centroideY
				&& perimetro==this.perimetro
				&& area==this.area;
	}
}
