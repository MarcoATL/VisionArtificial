/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SegmentacionColor;

import java.util.Arrays;
import org.opencv.core.Point;

/**
 *
 * @author Saul
 */
public class Pixel {
    private double[] val;
    private int x;
    private int y;
    public boolean adicionado;
	
    public Pixel() 
		{ this(new double[]{0,0,0},0,0); }
	
	public Pixel(double[] val, Point point) 
		{ this(val, (int) point.x, (int) point.y); }
	
    public Pixel(double[] valor, int x, int y) {
        adicionado = false;
        this.val = valor;
        this.x = x;
        this.y = y;
    }
	
    public double[] getVal() 
		{ return val; }
    public int getX() 
		{ return x; }
    public int getY() 
		{ return y; }
	
    @Override
    public boolean equals(Object objeto) {
        if(objeto==null)
            return false;
        if(!(objeto instanceof Pixel))
			return false;
		
		boolean sameVal = true;
		
        Pixel pixel = (Pixel) objeto;
		for (int i = 0; i < val.length; i++)
			if (val[i]!=pixel.val[i])
				sameVal = false;
			
        return x==pixel.x && y==pixel.y && sameVal;
    }
	public boolean equals(Point point) 
		{ return point.x==this.x&&point.y==this.y; }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (int) this.val[0];
		hash = 17 * hash + (int) this.val[1];
		hash = 17 * hash + (int) this.val[2];
        hash = 17 * hash + this.x;
        hash = 17 * hash + this.y;
        hash = 17 * hash + (this.adicionado ? 1 : 0);
        return hash;
    }
    @Override
    public String toString() {
        return Arrays.toString(val) + ":(X:" + x + ", Y:" + y + ")";
    }
}
