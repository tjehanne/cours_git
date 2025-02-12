package org.oxyl;

import static java.lang.Math.sqrt;

public class Point {
    private double x;
    private double y;

    // Constructeur initialisant les coordonnées
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Constructeur par défaut
    public Point() {
        x = 0;
        y = 0;
    }

    // Constructeur par copie
    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    // Getters
    public double getX() {
        return x;
    }

    // Getters
    public double getY() {
        return y;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
    }

    // Setters
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals (Object o) {
        if (o instanceof Point) {
            return ((Point)o).x == this.x && ((Point)o).y == this.y;
        }
        else{
            return false;
        }
    }

    // Calcule de distance entre deux points 
    public double calculerDistance(Point p) {
        return Math.sqrt(Math.pow(p.x - this.x, 2) + Math.pow(p.y - this.y, 2));    }
}
