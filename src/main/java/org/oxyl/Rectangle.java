package org.oxyl;

public class Rectangle {
    private double centreX;
    private double centreY;
    private double longueur;
    private double largeur;
    private double angle;

    public Rectangle () {
        centreX = 0.0;
        centreY = 0.0;
        longueur = 1.0;
        largeur = 1.0;
        angle = 0.0;
    }

    public Rectangle (double centreX, double centreY, double longueur, double largeur, double angle) {
        this.centreX = centreX;
        this.centreY = centreY;
        this.longueur = longueur;
        this.largeur = largeur;
        this.angle = angle;
    }

    public Rectangle (Rectangle R) {
        this.centreX = R.centreX;
        this.centreY = R.centreY;
        this.longueur = R.longueur;
        this.largeur = R.largeur;
        this.angle = R.angle;
    }

    public void deplacer (double distanceX, double distanceY) {
        centreX += distanceX;
        centreY += distanceY;
    }

    // Vérifie si le rectangle est un carré
    public boolean isCarre (){
        return longueur == largeur;
    }

    // redimensionne le rectangle
    public void redimensionner(double f){
        longueur *= f;
        largeur *= f;

    }
    public void tourner(double theta){
        angle += theta;
    }

}

