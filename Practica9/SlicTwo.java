/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperPixeles;

import java.util.ArrayList;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * class SlicTwo.
 *
 * In this class, an over-segmentation is created of an image, provided by the
 * step-size (distance between initial cluster locations) and the colour
 * distance parameter.
 */
public class SlicTwo {
    /**
     * The number of iterations run by the clustering algorithm. 
     */
    private final int NR_ITERATIONS;
    /**
     * The cluster assignments 
     */
    private double [][] clusters;
    /**
     * The distance values for each pixels
     */
    private double [][] distances;
    /** 
     * The LAB and xy values of the centers. 
     */
    private ArrayList<Centro> centers;
    /** 
     * The number of occurences of each center. 
     */
    private ArrayList<Integer> center_counts;
    /** 
     * The step size per cluster. 
     */
    private int step;
    /**
     * The colour (nc) parameter
     */
    private int nc;
    /**
     * The distance (ns) parameter
     */
    private int ns;
    /**
     * Compute the distance between a center and an individual pixel. 
     * Input : The cluster index (int), the pixel (CvPoint), and the Lab values 
     *         of the pixel (CvScalar).
     * Output: The distance (double).
     * @param ci
     * @param pixel
     * @param colour
     * @return 
     */
    private double computeDistance(int ci, Point pixel, Scalar colour) {
        Centro centro = centers.get(ci);
        double dc = Math.sqrt(
                    Math.pow(centro.getL() - colour.val[0], 2.0) + 
                    Math.pow(centro.getA() - colour.val[1], 2.0) + 
                    Math.pow(centro.getB() - colour.val[2], 2.0)
                    );
        double ds = Math.sqrt(
                    Math.pow(centers.get(ci).getX() - pixel.x, 2) + 
                    Math.pow(centers.get(ci).getY() - pixel.y, 2)
                    );
        return Math.sqrt(Math.pow(dc / nc, 2) + Math.pow(ds / ns, 2));
    }
    /**
     * Find the pixel with the lowest gradient in a 3x3 surrounding. 
     * Find a local gradient minimum of a pixel in a 3x3 neighbourhood. This
     * method is called upon initialization of the cluster centers.
     *
     * Input : The image (Mat*) and the pixel center (Point).
     * Output: The local gradient minimum (Point).
     * @param image
     * @param center
     * @return 
     */
    private Point findLocalMinimum(Mat image, Point center) {
        double min_grad = Double.MAX_VALUE;
        Point loc_min = new Point(center.x, center.y);

        for (int j = (int)center.y-1; j < (int)center.y+2; j++) {
            for (int i = (int)center.x-1; i < (int)center.x+2; i++) {
                double [] c1 = image.get(j+1, i);
                double [] c2 = image.get( j, i+1);
                double [] c3 = image.get( j, i);
                /* Convert colour values to grayscale values. */
                double i1 = c1[0];
                double i2 = c2[0];
                double i3 = c3[0];
                /*
                double i1 = 
                        c1.val[0] * 0.11 + c1.val[1] * 0.59 + c1.val[2] * 0.3;
                double i2 = 
                        c2.val[0] * 0.11 + c2.val[1] * 0.59 + c2.val[2] * 0.3;
                double i3 = 
                        c3.val[0] * 0.11 + c3.val[1] * 0.59 + c3.val[2] * 0.3;
                */
                /* 
                 * Compute horizontal and vertical gradients and keep track of 
                 * the minimum. 
                 */
                if(Math.sqrt(Math.pow(i1 - i3, 2)) + 
                    Math.sqrt(Math.pow(i2 - i3,2)) < min_grad) {
                    min_grad = Math.abs(i1 - i3) + Math.abs(i2 - i3);
                    loc_min.x = i;
                    loc_min.y = j;
                }
            }
        }
        return loc_min;
    }    
    /** 
     * Remove and initialize the 2d vectors. 
     * Clear the data as saved by the algorithm.
     * Input : -
     * Output: -
     */
    private void clearData() {
        clusters = null;
        distances = null;
        if(centers!=null) {
            centers.clear();
            }
        if(center_counts!=null) { 
            center_counts.clear();
            }
        System.gc();
    }
    /**
     * Initialize the cluster centers and initial values of the pixel-wise 
     * cluster
     * assignment and distance values.
     *
     * Input : The image (IplImage*).
     * Output: -
     * @param image 
     */
    private void initData(Mat image) {
        int height  = image.height();
        int width = image.width();
        clusters = new double[height][width];
        distances = new double[height][width];
        centers = new ArrayList<>();
        center_counts = new ArrayList<>();
        /* Initialize the cluster and distance matrices. */
        for(int j=0; j<image.height(); j++) {
            for(int i=0; i<image.width(); i++) { 
                clusters[j][i] = 80;
                distances[j][i] = Double.MAX_VALUE;
                }
            }
        /* Initialize the centers and counters. */
        System.out.println("Paso: " + step);
        int contador = 0;
        for (int j = step; j < image.height() - step/2; j += step) {
            for (int i = step; i < image.width() - step/2; i += step) {
                /* Find the local minimum (gradient-wise). */
                Point nc = findLocalMinimum(image, new Point(i,j));
                double [] colour = image.get((int)nc.y, (int)nc.x);
                /* Generate the center vector. */
                Centro center = new Centro((int)nc.x, (int)nc.y);
                center.setL(colour[0]);
                center.setA(colour[1]);
                center.setB(colour[2]);
                /* Append to vector of centers. */
                centers.add(center);
                center_counts.add(0);
                contador++;
                }
            }
        System.out.println( "K " + contador);
    }
    /** 
     * Class constructors and deconstructors. 
     * @param iteraciones
     */
    public SlicTwo(int iteraciones) {
        NR_ITERATIONS = iteraciones;
    }
    /**
     * Destructor
     */
    public void destructor() {
        clearData();
    }
    /** 
     * Generate an over-segmentation for an image. 
     * @param image
     * @param step
     * @param nc
     */
    public void generateSuperpixels(Mat image, int step, int nc) {
        this.step = step;
        this.nc = nc;
        this.ns = step;

        /* Clear previous data (if any), and re-initialize it. */
        clearData();
        initData(image);

        /* Run EM for 10 iterations (as prescribed by the algorithm). */
        for (int i = 0; i < NR_ITERATIONS; i++) {
            /* Reset distance values. */
            for (int j = 0; j < image.width(); j++) {
                for (int k = 0;k < image.height(); k++) {
                    distances[i][j] = Double.MAX_VALUE;
                    }
                }
        for (int j = 0; j < centers.size(); j++) {
            /* Only compare to pixels in a 2 x step by 2 x step region. */
            Centro centro = centers.get(j);
             
            int yIni = centro.getY()-step;
            int yFin = centro.getY()+step;
            int xIni = centro.getX()-step;
            int xFin = centro.getX()+step;
            for(int l=yIni; l<yFin; l++) {
                for( int k=xIni; k<xFin; k++) {
                    if (k >= 0 && k < image.width() && 
                            l >= 0 && l < image.height()) {
                        Scalar colour = new Scalar( image.get(l, k) );
                        double d = computeDistance(j, new Point(k,l), colour);
                        /* Update cluster allocation if the cluster minimizes 
                         * the distance. 
                         */
                        if (d < distances[l][k]) {
                            distances[l][k] = d;
                            
                            clusters[l][k] = j;
                            //System.out.println(clusters[l][k]);
                            }
                        
                        }
                    }
                }
            }
        /* Clear the center values. */
        for (int j = 0; j < centers.size(); j++) {
            //centers[j][0] = centers[j][1] = centers[j][2] = 
            //centers[j][3] = centers[j][4] = 0;
            centers.get(j).clear();
            center_counts.add(j, 0);
            }
        /* Compute the new cluster centers. */
        for (int k = 0; k < image.height(); k++) {
            for (int j = 0; j < image.width(); j++) {
                int c_id = (int)clusters[k][j];
                if (c_id != -1) {
                    double [] colour = image.get(k, j);
                    Centro center = centers.get(c_id);
                    double L = center.getL();
                    L += colour[0];
                    center.setL(L);
                    double a = center.getA();
                    a += colour[1];
                    center.setA(a);
                    double b = center.getB();
                    b += colour[2];
                    center.setB(b);
                    int y = center.getY();
                    y += j;
                    center.setY(y);
                    int x = center.getX();
                    x += k;
                    center.setX(x);
                    centers.remove(c_id);
                    centers.add(c_id, center);
                    Integer valor = center_counts.get(c_id);
                    valor += 1;
                    center_counts.add(c_id, valor);
                    }
                }
            }
            /* Normalize the clusters. */
            for (int j = 0; j < centers.size(); j++) {
                Centro center = centers.get(j);
                double L = center.getL();
                L /= center_counts.get(j);
                center.setL(L);
                double a = center.getA();
                a /= center_counts.get(j);
                center.setA(a);
                double b = center.getB();
                b /= center_counts.get(j);
                center.setB(b);
                int y = center.getY();
                y /= center_counts.get(j);
                center.setY(y);
                int x = center.getX();
                x /= center_counts.get(j);
                center.setX(x);
                centers.remove(j);
                centers.add(j, center);
            }
        }
    }
    /** 
     * Enforce connectivity for an image. 
     * Enforce connectivity of the superpixels. This part is not actively 
     * discussed in the paper, but forms an active part of the implementation 
     * of the authors of the paper.
     *
     * Input : The image (Mat image).
     * Output: -
     * @param image
     */
    public void createConnectivity(Mat image) {
        int label = 0, adjlabel = 0;
        int lims = (image.width() * image.height()) / (centers.size());
        // conectividad 4
        int [] dx4 = {-1,  0,  1,  0};
        int [] dy4 = { 0, -1,  0,  1};

        /* Initialize the new cluster matrix. */
        int height  = image.height();
        int width = image.width();
        double [][] new_clusters = new double[height][width];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) { 
                new_clusters[j][i] = -1.0;
                }
            }
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (new_clusters[j][i] == -1) {
                    ArrayList<Point> elements = new ArrayList<>();
                    elements.add( new Point(i, j));
                    /* Find an adjacent label, for possible use later. */
                    for (int k = 0; k < 4; k++) {
                        int x = (int)elements.get(0).x + dx4[k];
                        int y = (int)elements.get(0).y + dy4[k];
                        if(x >= 0 && x < width && y >= 0 && y < height) {
                            if (new_clusters[y][x] >= 0) {
                                adjlabel = (int)new_clusters[y][x];
                                }
                            }
                        }
                    int count = 1;
                    for (int c = 0; c < count; c++) {
                        for (int k = 0; k < 4; k++) {
                            int x = (int)elements.get(c).x + dx4[k]; 
                            int y = (int)elements.get(c).y + dy4[k];

                            if (x >= 0 && x < width && y >= 0 && y < height) {
                                if (new_clusters[y][x] == -1 && 
                                        clusters[j][i] == clusters[y][x]) {
                                    elements.add(new Point(x, y));
                                    new_clusters[y][x] = label;
                                    count += 1;
                                    }
                                }
                            }
                        }
                    /* Use the earlier found adjacent label if a segment size is
                       smaller than a limit. */
                    if (count <= lims >> 2) {
                        for (int c = 0; c < count; c++) {
                            new_clusters[(int)elements.get(c).y]
                                        [(int)elements.get(c).x] = adjlabel;
                            }
                        label -= 1;
                        }
                    label += 1;
                    }
                }
            }
    }
        
    /**
     * Draw functions. Resp. displayal of the centers and the contours. 
     * Display the cluster centers.
     *
     * Input : The image to display upon (IplImage*) and the colour (CvScalar).
     * Output: -
     * @param image
     * @param colour
     */
    public void displayCenterGrid(Mat image, Scalar colour) {
        
        for (Centro center : centers) {
            
            Imgproc.circle(image, 
                    new Point(center.getX(), center.getY()), 0, colour, 0);
            }
    }
    /**
     * Display a single pixel wide contour around the clusters.
     *
     * Input : The target image (IplImage*) and contour colour (CvScalar).
     * Output: -
     * @param image
     * @param colour
     */
    public void displayContours(Mat image, double [] colour) {
        int [] dx8 = {-1, -1,  0,  1, 1, 1, 0, -1};
	int [] dy8 = { 0, -1, -1, -1, 0, 1, 1,  1};
	/* Initialize the contour vector and the matrix detailing whether a 
         * pixel is already taken to be a contour. 
         */
	ArrayList<Point> contours = new ArrayList<>();
	boolean [][] istaken = new boolean[image.height()][image.width()];
        for (int j = 0; j < image.height(); j++) {
            for (int i = 0; i < image.width(); i++) { 
                istaken[j][i] = false;
                }
            }
        /* Go through all the pixels. */
        for (int j = 0; j < image.height(); j++) {
            for (int i = 0; i < image.width(); i++) {
                int nr_p = 0;
                /* Compare the pixel to its 8 neighbours. */
                for (int k = 0; k < 8; k++) {
                    int x = i + dx8[k], y = j + dy8[k];
                    if (x >= 0 && x < image.width() && 
                            y >= 0 && y < image.height()) {
                        if (istaken[y][x] == false && 
                                clusters[j][i] != clusters[y][x]) {
                            nr_p += 1;
                            }
                        }
                    } 
                /* Add the pixel to the contour list if desired. */
                if (nr_p >= 2) {
                    contours.add(new Point(i,j));
                    istaken[j][i] = true;
                    }
                }
            }
        /* Draw the contour pixels. */
        for (int i = 0; i < (int)contours.size(); i++) {
            image.put( (int)contours.get(i).y, (int)contours.get(i).x, colour);
            }
        //System.out.println( centers);
        for (Centro center : centers) { 
            image.put(center.getY(), center.getX(), new double[]{0,0,255});
             }
        }
    
        
    /**
     * Give the pixels of each cluster the same colour values. The specified 
     * colour is the mean RGB colour per cluster.
     *
     * Input : The target image (IplImage*).
     * Output: -
     * @param image
     */
    
    public void colourWithClusterMeans(Mat image) {
        
        ArrayList<Scalar> colours = new ArrayList<>(centers.size());
        
        //Cada color se le inserta el valor del centro
        for (Centro center : centers) {
            colours.add(new Scalar(center.getL(),center.getA(),center.getB()));
        }
       
        //se reunen los valores de color por grupo
        for (int j = 0; j < image.height(); j++) {
            for (int i = 0; i < image.width(); i++) {
                
                int index = (int)clusters[j][i];
                double [] colour = image.get(j, i);                
                double a = colours.get(index).val[0];
                a += colour[0];
                double b = colours.get(index).val[1];
                b += colour[1];
                double c = colours.get(index).val[2];
                c += colour[2];
                colours.set(index, new Scalar(a, b, c));
                }
            }
        
        // Divide el número de píxeles por grupo para obtener el color medio.
        for (int i = 0; i <colours.size(); i++) {
            double a = colours.get(i).val[0];
            a /= center_counts.get(i);
            double b = colours.get(i).val[1];
            b /= center_counts.get(i);
            double c = colours.get(i).val[2];
            c /= center_counts.get(i);
            colours.set(i, new Scalar(a, b, c));
            }
        
        //inserta el color a la nueva imagen
        for (int j = 0; j < image.height(); j++) {
            for (int i = 0; i < image.width(); i++) {
                Scalar ncolour = colours.get((int)clusters[j][i]);
                double [] dcolour = {
                    ncolour.val[0],
                    ncolour.val[1],
                    ncolour.val[2]
                    };
                image.put(j, i, dcolour);
                }
            }
    }
}