/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SegmentacionColor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Saul
 */
public class Region {
    private List<Pixel> R;
    public Region() {
        R = new ArrayList<>();
    }
    public void add(Pixel unPixel) {
        if(!isAdicionado(unPixel)) {
            R.add(unPixel);
		}
    }
    private boolean isAdicionado(Pixel unPixel) {
        for(Pixel pixel: R) {
            if(pixel.equals(unPixel)) {
                return true;
                }
            }
        return false;
    }
    public double[] calcularMedia() {
        double[] media = {0,0,0};
        for(Pixel pixel : R) {
			double[] pVal = pixel.getVal();
			for (int i=0; i<pVal.length; i++)
				media[i] += pVal[i];
            }
		for (int i=0; i<media.length; i++) {
			media[i] /= R.size();
			media[i] *= 1000.0;
			media[i] = Math.round(media[i]);
			media[i] /= 1000.0;
		}
        
        return media;
    }
    public List<Pixel> getR() {
        return R;
    }
    @Override
    public String toString() {
        return R.toString();
    }
}
