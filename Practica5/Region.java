
package Practica5;
import java.util.ArrayList;
import java.util.List;


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
    public double calcularMedia() {
        double media = 0.0;
        for(Pixel pixel : R) {
            media += pixel.getVal();
            }
        media /= R.size();
        media *= 1000.0;
        media = Math.round(media);
        media /= 1000.0;
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
