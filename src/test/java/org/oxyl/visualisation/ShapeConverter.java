package org.oxyl.visualisation;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import org.oxyl.TesteurCercleAvecPoint;
import org.oxyl.TesteurCercleSansPoint;
import org.oxyl.TesteurPoint;
import org.oxyl.TesteurRectangleAvecPoint;
import org.oxyl.TesteurRectangleSansPoint;
import org.oxyl.TesteurTriangleAvecPoint;
import org.oxyl.TesteurTriangleSansPoint;

public class ShapeConverter {
	
	private static ShapeConverter instance;
	
	private TesteurRectangleSansPoint testeurRectangleSansPoint = new TesteurRectangleSansPoint();
	private TesteurCercleSansPoint testeurCercleSansPoint = new TesteurCercleSansPoint();
	private TesteurTriangleSansPoint testeurTriangleSansPoint = new TesteurTriangleSansPoint();
	
	private TesteurPoint testeurPoint = new TesteurPoint();
	private TesteurRectangleAvecPoint testeurRectangleAvecPoint = new TesteurRectangleAvecPoint();
	private TesteurCercleAvecPoint testeurCercleAvecPoint = new TesteurCercleAvecPoint();
	private TesteurTriangleAvecPoint testeurTriangleAvecPoint = new TesteurTriangleAvecPoint();
	
	public static ShapeConverter getInstance() {
		if (instance == null) {
			instance = new ShapeConverter();
		}
		return instance;
	}

	public Shape convertRectangleSansPointToShape(Object r) {
		return new Rectangle2D.Double(testeurRectangleSansPoint.invokeGetCentreX(r) - testeurRectangleSansPoint.invokeGetLargeur(r)/2, 
				testeurRectangleSansPoint.invokeGetCentreY(r) - testeurRectangleSansPoint.invokeGetLongueur(r)/2, 
				testeurRectangleSansPoint.invokeGetLargeur(r), 
				testeurRectangleSansPoint.invokeGetLongueur(r));
	}
	
	public Shape convertCercleSansPointToShape(Object c) {
		return new Ellipse2D.Double(testeurCercleSansPoint.invokeGetCentreX(c) - testeurCercleSansPoint.invokeGetRayon(c), 
				testeurCercleSansPoint.invokeGetCentreY(c) - testeurCercleSansPoint.invokeGetRayon(c), 
				testeurCercleSansPoint.invokeGetRayon(c)*2, 
				testeurCercleSansPoint.invokeGetRayon(c)*2);
	}
	
	public Shape convertTriangleSansPointToShape(Object t) {
		Path2D shape = new Path2D.Double();
		shape.moveTo(testeurTriangleSansPoint.invokeGetX1(t), testeurTriangleSansPoint.invokeGetY1(t));
		shape.lineTo(testeurTriangleSansPoint.invokeGetX2(t), testeurTriangleSansPoint.invokeGetY2(t));
		shape.lineTo(testeurTriangleSansPoint.invokeGetX3(t), testeurTriangleSansPoint.invokeGetY3(t));
		shape.lineTo(testeurTriangleSansPoint.invokeGetX1(t), testeurTriangleSansPoint.invokeGetY1(t));

		return shape;
	}

	
	public Object convertShapeToRectangleSansPoint(Shape s, double theta) {
		PathIterator path = s.getPathIterator(null);
		double[] coord = new double[2];
		path.currentSegment(coord);
		double x1 = coord[0];
		double y1 = coord[1];
			
		path.next();
		path.currentSegment(coord);

		double x2 = coord[0];
		double y2 = coord[1];
		
		path.next();
		path.currentSegment(coord);

		double x3 = coord[0];
		double y3 = coord[1];

		path.next();
		path.currentSegment(coord);
		
		double x4 = coord[0];
		double y4 = coord[1];
			
		path.next();
		path.currentSegment(coord);

		double centreX = (x1 + x3)/2;
		double centreY = (y1 + y3)/2;
		double largeur = Math.sqrt(Math.pow(x1 - x2, 2) + (Math.pow(y1 - y2, 2)));
		double longueur = Math.sqrt(Math.pow(x1 - x4, 2) + (Math.pow(y1 - y4, 2)));
		
		return testeurRectangleSansPoint.getRectangle(
				new Object[]{ centreX, 
						centreY, 
						longueur, 
						largeur, 
						theta
						});

	}
	
	public Object convertShapeToCercleSansPoint(Shape s) {
		return testeurCercleSansPoint.getCercle(
				new Object[] {
				s.getBounds2D().getCenterX(), 
				s.getBounds2D().getCenterY(), 
				s.getBounds2D().getHeight()/2.0
				});
	}

	public Object convertShapeToTriangleSansPoint(Shape s) {
		PathIterator path = s.getPathIterator(null);
		double[] coord = new double[2];
		path.currentSegment(coord);
		double x1 = coord[0];
		double y1 = coord[1];
			
		path.next();
		path.currentSegment(coord);

		double x2 = coord[0];
		double y2 = coord[1];
		
		path.next();
		path.currentSegment(coord);

		double x3 = coord[0];
		double y3 = coord[1];

		path.next();
		path.currentSegment(coord);

		return testeurTriangleSansPoint.getTriangle(
				new Object[] {x1, y1, x2, y2, x3, y3});
		}
	
 //////////// AVEC POINT
	
	public Shape convertRectangleAvecPointToShape(Object r) {
		Object centre = testeurRectangleAvecPoint.invokeGetCentre(r);
		return new Rectangle2D.Double(testeurPoint.invokeGetX(centre) - testeurRectangleAvecPoint.invokeGetLargeur(r)/2, 
				testeurPoint.invokeGetY(centre) - testeurRectangleAvecPoint.invokeGetLongueur(r)/2, 
				testeurRectangleAvecPoint.invokeGetLargeur(r), 
				testeurRectangleAvecPoint.invokeGetLongueur(r));
	}
	
	public Shape convertCercleAvecPointToShape(Object c) {
		Object centre = testeurCercleAvecPoint.invokeGetCentre(c);
		return new Ellipse2D.Double(testeurPoint.invokeGetX(centre) - testeurCercleAvecPoint.invokeGetRayon(c), 
				testeurPoint.invokeGetY(centre) - testeurCercleAvecPoint.invokeGetRayon(c), 
				testeurCercleAvecPoint.invokeGetRayon(c)*2, 
				testeurCercleAvecPoint.invokeGetRayon(c)*2);
	}
	
	public Shape convertTriangleAvecPointToShape(Object t) {
		Object point1 = testeurTriangleAvecPoint.invokeGetPoint1(t);
		Object point2 = testeurTriangleAvecPoint.invokeGetPoint2(t);
		Object point3 = testeurTriangleAvecPoint.invokeGetPoint3(t);
		Path2D shape = new Path2D.Double();
		shape.moveTo(testeurPoint.invokeGetX(point1), testeurPoint.invokeGetY(point1));
		shape.lineTo(testeurPoint.invokeGetX(point2), testeurPoint.invokeGetY(point2));
		shape.lineTo(testeurPoint.invokeGetX(point3), testeurPoint.invokeGetY(point3));
		shape.lineTo(testeurPoint.invokeGetX(point1), testeurPoint.invokeGetY(point1));

		return shape;
	}

	
	public Object convertShapeToRectangleAvecPoint(Shape s, double theta) {
		PathIterator path = s.getPathIterator(null);
		double[] coord = new double[2];
		path.currentSegment(coord);
		double x1 = coord[0];
		double y1 = coord[1];
			
		path.next();
		path.currentSegment(coord);

		double x2 = coord[0];
		double y2 = coord[1];
		
		path.next();
		path.currentSegment(coord);

		double x3 = coord[0];
		double y3 = coord[1];

		path.next();
		path.currentSegment(coord);
		
		double x4 = coord[0];
		double y4 = coord[1];
			
		path.next();
		path.currentSegment(coord);

		double centreX = (x1 + x3)/2;
		double centreY = (y1 + y3)/2;
		double largeur = Math.sqrt(Math.pow(x1 - x2, 2) + (Math.pow(y1 - y2, 2)));
		double longueur = Math.sqrt(Math.pow(x1 - x4, 2) + (Math.pow(y1 - y4, 2)));
		
		return testeurRectangleAvecPoint.getRectangle(
				new Object[]{ centreX, 
						centreY, 
						longueur, 
						largeur, 
						theta
						});
	}
	
	public Object convertShapeToCercleAvecPoint(Shape s) {
		return testeurCercleAvecPoint.getCercle(
				new Object[] {
				s.getBounds2D().getCenterX(), 
				s.getBounds2D().getCenterY(), 
				s.getBounds2D().getHeight()/2.0
				});
	}

	public Object convertShapeToTriangleAvecPoint(Shape s) {
		PathIterator path = s.getPathIterator(null);
		double[] coord = new double[2];
		path.currentSegment(coord);
		double x1 = coord[0];
		double y1 = coord[1];
			
		path.next();
		path.currentSegment(coord);

		double x2 = coord[0];
		double y2 = coord[1];
		
		path.next();
		path.currentSegment(coord);

		double x3 = coord[0];
		double y3 = coord[1];

		path.next();
		path.currentSegment(coord);

		return testeurTriangleAvecPoint.getTriangle(
				new Object[] {x1, y1, x2, y2, x3, y3});
		}
	
	//////////// EXTRAIRE LES COORDONNEES
	
	public double[] convertShapeTriangleToCoordinates(Shape s) {
		PathIterator path = s.getPathIterator(null);
		double[] coord = new double[2];
		path.currentSegment(coord);
		double x1 = coord[0];
		double y1 = coord[1];
			
		path.next();
		path.currentSegment(coord);

		double x2 = coord[0];
		double y2 = coord[1];
		
		path.next();
		path.currentSegment(coord);

		double x3 = coord[0];
		double y3 = coord[1];

		path.next();
		path.currentSegment(coord);
		
		return new double[] { x1, y1, x2, y2, x3, y3 };
	}
	public double[] convertShapeRectangleToCoordinates(Shape s) {
		PathIterator path = s.getPathIterator(null);
		double[] coord = new double[2];
		path.currentSegment(coord);
		double x1 = coord[0];
		double y1 = coord[1];
			
		path.next();
		path.currentSegment(coord);

		double x2 = coord[0];
		double y2 = coord[1];
		
		path.next();
		path.currentSegment(coord);

		double x3 = coord[0];
		double y3 = coord[1];

		path.next();
		path.currentSegment(coord);
		
		double x4 = coord[0];
		double y4 = coord[1];

		path.next();
		path.currentSegment(coord);
		
		return new double[] { x1, y1, x2, y2, x3, y3, x4, y4 };
	}
	
}
