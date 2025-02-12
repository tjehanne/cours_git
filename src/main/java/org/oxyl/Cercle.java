package org.oxyl;

public class Cercle {
    private double centreX;
    private double centreY;
    private double rayon;

    public Cercle() {
        centreX = 0.0;
        centreY = 0.0;
        rayon = 1.0;
    }
    public Cercle (double centreX, double centreY, double rayon) {
        this.centreX = centreX;
        this.centreY = centreY;
        this.rayon = rayon;
    }

    public Cercle (Cercle c) {
        this.centreX = c.centreX;
        this.centreY = c.centreY;
        this.rayon = c.rayon;
    }

    public void deplacer(double distanceX, double distanceY) {
        centreX += distanceX;
        centreY += distanceY;
    }

    public boolean isGrand(){
        return rayon > 100;
    }

    public void redimensionner(double f){
        rayon *= f;
    }

    public void tourner(double theta){
        theta += theta;
    }
}
