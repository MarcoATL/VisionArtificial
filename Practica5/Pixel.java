/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica5;

import org.opencv.core.Point;

public class Pixel {
    private double val;
    private int x;
    private int y;
    public boolean adicionado;
	
    public Pixel(){ 
        this(0,0,0); 
    }
	
    public Pixel(double val, Point point){ 
        this(val, (int) point.x, (int) point.y);
    }
	
    public Pixel(double valor, int x, int y) {
        adicionado = false;
		
        this.val = valor;
        this.x = x;
        this.y = y;
    }
	
    public double getVal() 
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
		
        Pixel pixel = (Pixel) objeto;
        return x==pixel.x && y==pixel.y && val==pixel.val;
    }
	public boolean equals(Point point) 
		{ return point.x==this.x&&point.y==this.y; }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (int) this.val;
        hash = 17 * hash + this.x;
        hash = 17 * hash + this.y;
        hash = 17 * hash + (this.adicionado ? 1 : 0);
        return hash;
    }
    @Override
    public String toString() {
        return val + ":(X:" + x + ", Y:" + y + ")";
    }
}
