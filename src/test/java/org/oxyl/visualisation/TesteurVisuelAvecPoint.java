package org.oxyl.visualisation;

import org.oxyl.TesteurCercleAvecPoint;
import org.oxyl.TesteurPoint;
import org.oxyl.TesteurRectangleAvecPoint;
import org.oxyl.TesteurTriangleAvecPoint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.lang.Thread.UncaughtExceptionHandler;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;


public class TesteurVisuelAvecPoint extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private Box toolbars;
	private static DrawingPanel drawingPanelTemoin;
	private static DrawingPanel drawingPanelEleve;
	private JComboBox<Figure> figuresComboBox;
	private JCheckBox triangleEqCheckBox;
	private JCheckBox rectangleCarreCheckBox;
	private ShapeConverter converter = ShapeConverter.getInstance();
	
	private TesteurRectangleAvecPoint testeurRectangle = new TesteurRectangleAvecPoint();
	private TesteurCercleAvecPoint testeurCercle = new TesteurCercleAvecPoint();
	private TesteurTriangleAvecPoint testeurTriangle = new TesteurTriangleAvecPoint();
	private TesteurPoint testeurPoint = new TesteurPoint();
	
	private Object[] cercleParams = new Object[] { 160, 150, 90 };
	private Shape cercle = new Ellipse2D.Double(70, 60, 180, 180);
	
	private double angleRectangleEleve = 0;
	private double angleRectangleTemoin = 0;
	private Object[] rectangleCarreParams = new Object[] {125, 145, 150, 150, angleRectangleEleve};
	private Shape rectangleCarre = new Rectangle2D.Double(50, 70, 150, 150);
	private Object[] rectanglePasCarreParams = new Object[] {100, 170, 200, 100, angleRectangleEleve};
	private Shape rectanglePasCarre = new Rectangle2D.Double(50, 70, 100, 200);
	
	private Object[] triangleEqParams = new Object[] { 20.0*10, 10*35.773502691896255 - 200, 10.0*10, 10*35.773502691896255 - 200, 10*15.0, 10*27.11324865405187 - 200 };
	private Shape triangleEq;
	{ 
		double[] X = new double[] {200, 100, 150};
		double[] Y = new double[] {10*35.773502691896255 - 200, 10*35.773502691896255 - 200, 10*27.11324865405187 - 200 };
		Path2D path = new Path2D.Double();
		path.moveTo(X[0], Y[0]);
		for (int i=1; i<X.length; i++) {
			path.lineTo(X[i], Y[i]);
		}
		path.closePath();
		triangleEq = path;
	}
	private Object[] trianglePasEqParams = new Object[] { 30, 60, 200, 90, 100, 130 };
	private Shape trianglePasEq = new Polygon(new int[] {30, 200, 100}, new int[] {60, 90, 130}, 3);
	
	public TesteurVisuelAvecPoint() {
		
		setTitle("Test de mouvement - avec point");
		setLayout(new FlowLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		startExceptionHandler();
	
		initFields();
		
		initDrawingPanels();
		
		makeToolbars();
		
		Box verticalContainer = new Box(BoxLayout.Y_AXIS);
		Box horizontalContainer = new Box(BoxLayout.X_AXIS);
		verticalContainer.add(Box.createVerticalGlue());
		verticalContainer.setFocusable(false);
		verticalContainer.add(toolbars);
		setResizable(false);
		horizontalContainer.add(drawingPanelTemoin);
		horizontalContainer.add(drawingPanelEleve);
		verticalContainer.add(horizontalContainer);
		add(verticalContainer);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void startExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				if (e.getClass().equals(org.opentest4j.AssertionFailedError.class)) {
					JOptionPane.showMessageDialog(TesteurVisuelAvecPoint.this,
							e.getLocalizedMessage(),
							"Une erreur est survenue.",
							JOptionPane.ERROR_MESSAGE);
				} else {
					e.printStackTrace();
					TesteurVisuelAvecPoint.this.close();
				}
			}
			
		});
	}
	
	private void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	private void initDrawingPanels() {
		Shape cercleTemoin = this.cercle;
		Shape rectangleTemoin = this.rectanglePasCarre;
		Shape triangleTemoin = this.trianglePasEq;
		
		Shape cercleEleve = initEleveCercle(this.cercleParams);
		Shape rectangleEleve = initEleveRectangle(this.rectanglePasCarreParams);
		Shape triangleEleve = initEleveTriangle(this.trianglePasEqParams);
		
		drawingPanelTemoin = new DrawingPanel(cercleTemoin, rectangleTemoin, triangleTemoin, false);
		drawingPanelEleve = new DrawingPanel(cercleEleve, rectangleEleve, triangleEleve, true);
		
		Border borderTemoin = BorderFactory.createTitledBorder("Ce que vous devriez obtenir : ");
		Border borderEleve = BorderFactory.createTitledBorder("Ce que vous avez obtenu : ");
		drawingPanelTemoin.setBorder(borderTemoin);
		drawingPanelEleve.setBorder(borderEleve);
		
		drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getCercle(), drawingPanelTemoin.getCercle()));
		drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getCercle(), drawingPanelTemoin.getCercle()));
	}
	
	private Shape initEleveRectangle(Object[] params) {
		return converter.convertRectangleAvecPointToShape(testeurRectangle.getRectangle(params));
	}
	
	private Shape initEleveCercle(Object[] params) {
		return converter.convertCercleAvecPointToShape(testeurCercle.getCercle(params));
	}
	
	private Shape initEleveTriangle(Object[] params) {
		return converter.convertTriangleAvecPointToShape(testeurTriangle.getTriangle(params));
	}
	
	
	private void initFields() {
		initFiguresComboBox();
	}
	
	private JButton initRecommencerButton() {
		JButton bouton = new JButton("Remettre à zéro");
		bouton.setFocusable(false); // A mettre partout, sinon le bouton est dégueulasse
		bouton.addActionListener(e -> {
						
			switch(getCurrentFigure()) {
			
			case RECTANGLE :
				drawingPanelEleve.repaint();
				drawingPanelTemoin.repaint();

				angleRectangleEleve = 0;
				angleRectangleTemoin = 0;
				Object [] newRectangleParams = rectangleCarreCheckBox.isSelected() ? rectangleCarreParams : rectanglePasCarreParams;
				Shape newRectangle = rectangleCarreCheckBox.isSelected() ? rectangleCarre : rectanglePasCarre;
						
				drawingPanelEleve.setRectangle(initEleveRectangle(newRectangleParams));
				drawingPanelEleve.setPreviousRectangle(initEleveRectangle(newRectangleParams));
				drawingPanelTemoin.setRectangle(newRectangle);
				drawingPanelTemoin.setPreviousRectangle(newRectangle);
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getRectangle(), drawingPanelTemoin.getRectangle()));
				drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getRectangle(), drawingPanelTemoin.getRectangle()));

				pack();
				break;
			
			case TRIANGLE : 
				drawingPanelEleve.repaint();
				drawingPanelTemoin.repaint();
				
				Object [] newTriangleParams = triangleEqCheckBox.isSelected() ? triangleEqParams : trianglePasEqParams;
				Shape newTriangle = triangleEqCheckBox.isSelected() ? triangleEq : trianglePasEq;

				drawingPanelEleve.setTriangle(initEleveTriangle(newTriangleParams));
				drawingPanelEleve.setPreviousTriangle(initEleveTriangle(newTriangleParams));
				drawingPanelTemoin.setTriangle(newTriangle);
				drawingPanelTemoin.setPreviousTriangle(newTriangle);
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getTriangle(), drawingPanelTemoin.getTriangle()));
				drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getTriangle(), drawingPanelTemoin.getTriangle()));

				pack();
				break;
			
			case CERCLE : 
				drawingPanelEleve.repaint();
				drawingPanelTemoin.repaint();

				drawingPanelEleve.setCercle(initEleveCercle(this.cercleParams));
				drawingPanelTemoin.setCercle(this.cercle);
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getCercle(), drawingPanelTemoin.getCercle()));
				drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getCercle(), drawingPanelTemoin.getCercle()));

				pack();
				break;
		}
		});
		return bouton;
	}
	
	
	
	private JButton initDeplacerButton() {
		JButton bouton = new JButton("Déplacer");
		bouton.setFocusable(false); // A mettre partout, sinon le bouton est dégueulasse
		bouton.addActionListener(e -> {
			
			Graphics2D gT = (Graphics2D) drawingPanelTemoin.getGraphics();
			Graphics2D gE = (Graphics2D) drawingPanelEleve.getGraphics();
			double dx = Math.random()*100;
			double dy = Math.random()*100;
			
			switch(getCurrentFigure()) {
			
			case RECTANGLE:
				Shape re = translateAndDrawEleve(drawingPanelEleve.getRectangle(), gE, dx, dy); 
				drawingPanelEleve.setRectangle(re);	
				Shape rt = translateAndDrawTemoin(drawingPanelTemoin.getRectangle(), gT, dx, dy); 
				drawingPanelTemoin.setRectangle(rt);	
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(re, rt));
				drawingPanelEleve.setBothSpecial(areBothSpecial(re, rt));

				break;
			
			case TRIANGLE : 
				Shape te = translateAndDrawEleve(drawingPanelEleve.getTriangle(), gE, dx, dy); 
				drawingPanelEleve.setTriangle(te);	
				Shape tt = translateAndDrawTemoin(drawingPanelTemoin.getTriangle(), gT, dx, dy); 
				drawingPanelTemoin.setTriangle(tt);	
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(te, tt));
				drawingPanelEleve.setBothSpecial(areBothSpecial(te, tt));

				break;
			
			case CERCLE : 
				Shape ce = translateAndDrawEleve(drawingPanelEleve.getCercle(), gE, dx, dy); 
				drawingPanelEleve.setCercle(ce);
				Shape ct = translateAndDrawTemoin(drawingPanelTemoin.getCercle(), gT, dx, dy); 
				drawingPanelTemoin.setCercle(ct);	
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(ce, ct));
				drawingPanelEleve.setBothSpecial(areBothSpecial(ce, ct));

				break;
		}
		});
		
		return bouton;
	}
	
	private Shape translateAndDrawTemoin(Shape s, Graphics2D g, double dx, double dy) {
		AffineTransform f = new AffineTransform();
		f.setToTranslation(dx, dy);
		Shape shape = f.createTransformedShape(s);
		drawingPanelTemoin.repaint();
		g.draw(shape);
		pack();
		return shape;
	}
	
	private Shape translateAndDrawEleve(Shape s, Graphics2D g, double dx, double dy) {

		switch(getCurrentFigure()) {
		case RECTANGLE:
			Object r = converter.convertShapeToRectangleAvecPoint(s, 0);
			testeurRectangle.invokeDeplacer(r, dx, dy);
			Shape rectangle = converter.convertRectangleAvecPointToShape(r);
			double x = rectangle.getBounds2D().getCenterX();
			double y = rectangle.getBounds2D().getCenterY();
			AffineTransform f = new AffineTransform();		
			f.setToRotation(2*Math.PI*angleRectangleEleve/360, x, y);
			rectangle = f.createTransformedShape(rectangle);
			drawingPanelEleve.repaint();
			g.draw(rectangle);
			pack();
			return rectangle;
		case CERCLE:
			Object c = converter.convertShapeToCercleAvecPoint(s);
			testeurCercle.invokeDeplacer(c, dx, dy);
			Shape cercle = converter.convertCercleAvecPointToShape(c);
			drawingPanelEleve.repaint();
			g.draw(cercle);
			pack();
			return cercle;	
		case TRIANGLE:
			Object t = converter.convertShapeToTriangleAvecPoint(s);
			testeurTriangle.invokeDeplacer(t, dx, dy);
			Shape triangle = converter.convertTriangleAvecPointToShape(t);
			drawingPanelEleve.repaint();
			g.draw(triangle);
			pack();
			return triangle;
		default:
			return null;
		}
	}
	
	private JButton initRedimensionnerButton() {
		JButton bouton = new JButton("Redimensionner");
		bouton.setFocusable(false); // A mettre partout, sinon le bouton est dégueulasse
		if (getCurrentFigure().getName().equals("Triangle")) {
			bouton.setVisible(false);
		}
		bouton.addActionListener(e -> {
			
			Graphics2D gT = (Graphics2D) drawingPanelTemoin.getGraphics();
			Graphics2D gE = (Graphics2D) drawingPanelEleve.getGraphics();
			double f = Math.random()*3;
			
			switch(getCurrentFigure()) {
			
			case RECTANGLE :
				Shape re = scaleAndDrawEleve(drawingPanelEleve.getRectangle(), gE, f); 
				drawingPanelEleve.setRectangle(re);	
				Shape rt = scaleAndDrawTemoin(drawingPanelTemoin.getRectangle(), gT, f); 
				drawingPanelTemoin.setRectangle(rt);	
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(re, rt));
				drawingPanelEleve.setBothSpecial(areBothSpecial(re, rt));

				break;
				
			case CERCLE : 
				Shape ce = scaleAndDrawEleve(drawingPanelEleve.getCercle(), gE, f); 
				drawingPanelEleve.setCercle(ce);	
				Shape ct = scaleAndDrawTemoin(drawingPanelTemoin.getCercle(), gT, f);
				drawingPanelTemoin.setCercle(ct);
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(ce, ct));
				drawingPanelEleve.setBothSpecial(areBothSpecial(ce, ct));

				break;
				
			default:
		}
		});
		
		return bouton;
	}
	
	private Shape scaleAndDrawTemoin(Shape s, Graphics2D g, double facteur) {
		AffineTransform f = new AffineTransform();	
		
		f.setToScale(facteur, facteur);
		s = f.createTransformedShape(s);
		
		if (getCurrentFigure().getName().equals("Cercle") || getCurrentFigure().getName().equals("Rectangle") ) {
			f.setToTranslation((1/facteur - 1)*(s.getBounds2D().getCenterX()), 
					(1/facteur - 1)*(s.getBounds2D().getCenterY()));
			s = f.createTransformedShape(s);

		}
		
		drawingPanelTemoin.repaint();
		g.draw(s);
		pack();
		return s;
	}
	
	private Shape scaleAndDrawEleve(Shape s, Graphics2D g, double facteur) {
		
		switch(getCurrentFigure()) {
		case RECTANGLE:
			Object r = converter.convertShapeToRectangleAvecPoint(s, 0);
			testeurRectangle.invokeRedimensionner(r, new Object[]{facteur});
			Shape rectangle = converter.convertRectangleAvecPointToShape(r);
			double x = rectangle.getBounds2D().getCenterX();
			double y = rectangle.getBounds2D().getCenterY();
			AffineTransform f = new AffineTransform();		
			f.setToRotation(2*Math.PI*angleRectangleEleve/360, x, y);
			rectangle = f.createTransformedShape(rectangle);
			drawingPanelEleve.repaint();
			g.draw(rectangle);
			pack();
			return rectangle;
		case CERCLE:
			Object c = converter.convertShapeToCercleAvecPoint(s);
			testeurCercle.invokeRedimensionner(c, facteur);
			Shape cercle = converter.convertCercleAvecPointToShape(c);
			drawingPanelEleve.repaint();
			g.draw(cercle);
			pack();
			return cercle;
		default:
			return null;
		}

	}
	
	private JButton initTournerButton() {
		JButton bouton = new JButton("Tourner");
		bouton.setFocusable(false); // A mettre partout, sinon le bouton est dégueulasse
		if (getCurrentFigure().getName().equals("Cercle")) {
			bouton.setVisible(false);
		}

		bouton.addActionListener(e -> {

			Graphics2D gT = (Graphics2D) drawingPanelTemoin.getGraphics();
			Graphics2D gE = (Graphics2D) drawingPanelEleve.getGraphics();
			double theta = Math.random()*100;	
			
			switch(getCurrentFigure()) {
			
			case RECTANGLE :
				Shape re = rotateAndDrawEleve(drawingPanelEleve.getRectangle(), gE, theta); 
				drawingPanelEleve.setRectangle(re);	
				Shape rt = rotateAndDrawTemoin(drawingPanelTemoin.getRectangle(), gT, theta); 
				drawingPanelTemoin.setRectangle(rt);
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(re, rt));
				drawingPanelEleve.setBothSpecial(areBothSpecial(re, rt));
				
				break;
			
			case TRIANGLE : 
				Shape te = rotateAndDrawEleve(drawingPanelEleve.getTriangle(), gE, theta); 
				drawingPanelEleve.setTriangle(te);
				Shape tt = rotateAndDrawTemoin(drawingPanelTemoin.getTriangle(), gT, theta); 
				drawingPanelTemoin.setTriangle(tt);
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(te, tt));
				drawingPanelEleve.setBothSpecial(areBothSpecial(te, tt));
				
				break;
				
			default :
			
		}
		});
	
		return bouton;
	}
	
	
	private Shape rotateAndDrawTemoin(Shape s, Graphics2D g, double theta) {
		AffineTransform f = new AffineTransform();
		drawingPanelTemoin.repaint();
		double x;
		double y;
		
		if (getCurrentFigure().getName().equals("Triangle")) {
			x = getCenterTriangle(s)[0];
			y = getCenterTriangle(s)[1];
		} else {
			x = s.getBounds2D().getCenterX();
			y = s.getBounds2D().getCenterY();
		}
		
		if (getCurrentFigure().getName().equals("Rectangle")) {
			angleRectangleTemoin += theta;
		} 
		
		f.setToRotation(Math.PI*2*theta/360,x,y);
		Shape shape = f.createTransformedShape(s);
		g.draw(shape);
		pack();
		return shape;
	}
	
	private double[] getCenterTriangle(Shape t) {
		PathIterator path = t.getPathIterator(null);
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
		
		double centreX = (x1 + x2 + x3)/3.0;
		double centreY = (y1 + y2 + y3)/3.0;
		
		return new double[] { centreX, centreY };
	}
	
	private Shape rotateAndDrawEleve(Shape s, Graphics2D g, double theta) {
		switch(getCurrentFigure()) {
		case RECTANGLE:
			AffineTransform f = new AffineTransform();
			Object r = converter.convertShapeToRectangleAvecPoint(s, angleRectangleEleve);
			testeurRectangle.invokeTourner(r, new Object[] {theta});
			double angle = testeurRectangle.invokeGetAngle(r);
			angleRectangleEleve = angle;

			double x = s.getBounds2D().getCenterX();
			double y = s.getBounds2D().getCenterY();
			f.setToRotation(Math.PI*2*angleRectangleEleve/360,x,y);
			Shape rectangle = f.createTransformedShape(converter.convertRectangleAvecPointToShape(r));
			drawingPanelEleve.repaint();
			g.draw(rectangle);
			pack();
			return rectangle;
		case TRIANGLE:
			Object t = converter.convertShapeToTriangleAvecPoint(s);
			testeurTriangle.invokeTourner(t, theta);
			Shape triangle = converter.convertTriangleAvecPointToShape(t);
			drawingPanelEleve.repaint();
			g.draw(triangle);
			pack();
			return triangle;
			
		default:
			return null;
		}
	}
	
	private void initFiguresComboBox() {
		figuresComboBox = new JComboBox<Figure>(Figure.values());
		figuresComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
			JLabel renderer = (JLabel) new DefaultListCellRenderer()
					.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			renderer.setText(value.getName());
			float[] hsbFields = Color.RGBtoHSB(235, 235, 235, null);
			renderer.setBackground(
					isSelected ? Color.getHSBColor(hsbFields[0], hsbFields[1], hsbFields[2]) : Color.WHITE);
			return renderer;
		});
		figuresComboBox.setSelectedIndex(getCurrentFigure().ordinal());
		figuresComboBox.addActionListener(e -> {
			setFigure((Figure) figuresComboBox.getSelectedItem());
			switch(getCurrentFigure()) {
			case CERCLE :
				drawingPanelEleve.repaint();
				drawingPanelTemoin.repaint();
				
				drawingPanelEleve.setCercle(initEleveCercle(cercleParams));
				drawingPanelEleve.setPreviousCercle(initEleveCercle(cercleParams));
				drawingPanelTemoin.setCercle(cercle);
				drawingPanelTemoin.setPreviousCercle(cercle);

				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getCercle(), drawingPanelTemoin.getCercle()));
				drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getCercle(), drawingPanelTemoin.getCercle()));
				pack();
				break;
			case RECTANGLE :
				drawingPanelEleve.repaint();
				drawingPanelTemoin.repaint();
				
				angleRectangleEleve = 0;
				angleRectangleTemoin = 0;

				drawingPanelEleve.setRectangle(initEleveRectangle(rectanglePasCarreParams));
				drawingPanelEleve.setPreviousCercle(initEleveRectangle(rectanglePasCarreParams));
				drawingPanelTemoin.setCercle(rectanglePasCarre);
				drawingPanelTemoin.setPreviousCercle(rectanglePasCarre);
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getRectangle(), drawingPanelTemoin.getRectangle()));
				drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getRectangle(), drawingPanelTemoin.getRectangle()));
				pack();
				break;
			case TRIANGLE :
				drawingPanelEleve.repaint();
				drawingPanelTemoin.repaint();
				
				drawingPanelEleve.setTriangle(initEleveTriangle(trianglePasEqParams));
				drawingPanelEleve.setPreviousCercle(initEleveTriangle(trianglePasEqParams));
				drawingPanelTemoin.setTriangle(trianglePasEq);
				drawingPanelTemoin.setPreviousTriangle(trianglePasEq);
				
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getTriangle(), drawingPanelTemoin.getTriangle()));
				drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getTriangle(), drawingPanelTemoin.getTriangle()));
				pack();
				break;
			}
		});
		figuresComboBox.setFocusable(false);
		
	}
	

	
	private JCheckBox initEqCheckBox() {
		triangleEqCheckBox = new JCheckBox("Equilatéral");
		triangleEqCheckBox.setFocusable(false);
		
		if (!getCurrentFigure().getName().equals("Triangle")) {
			triangleEqCheckBox.setVisible(false);
		}
		
		triangleEqCheckBox.addActionListener(e -> {
			switch (getCurrentFigure()) {
			case TRIANGLE :
				Shape triangleTemoin = triangleEqCheckBox.isSelected() ? triangleEq : trianglePasEq ; 
				Object[] triangleEleveParams = triangleEqCheckBox.isSelected() ? triangleEqParams : trianglePasEqParams;
				
				drawingPanelEleve.setTriangle(triangleTemoin);
				drawingPanelEleve.setPreviousTriangle(triangleTemoin);
				drawingPanelTemoin.setTriangle(initEleveTriangle(triangleEleveParams));
				drawingPanelTemoin.setPreviousTriangle(initEleveTriangle(triangleEleveParams));
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getTriangle(), drawingPanelTemoin.getTriangle()));
				drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getTriangle(), drawingPanelTemoin.getTriangle()));

			default :
			}
			
		});
		return triangleEqCheckBox;
	}
	
	private JCheckBox initCarreCheckBox() {
		rectangleCarreCheckBox = new JCheckBox("Carré");
		rectangleCarreCheckBox.setFocusable(false);
		
		if (!getCurrentFigure().getName().equals("Rectangle")) {
			rectangleCarreCheckBox.setVisible(false);
		}
		
		rectangleCarreCheckBox.addActionListener(e -> {
			switch (getCurrentFigure()) {
			case RECTANGLE :
				Shape rectangleTemoin = rectangleCarreCheckBox.isSelected() ? rectangleCarre : rectanglePasCarre ; 
				Object[] rectangleEleveParams = rectangleCarreCheckBox.isSelected() ? rectangleCarreParams : rectanglePasCarreParams;
				
				drawingPanelEleve.setRectangle(rectangleTemoin);
				drawingPanelEleve.setPreviousRectangle(rectangleTemoin);
				drawingPanelTemoin.setRectangle(initEleveRectangle(rectangleEleveParams));
				drawingPanelTemoin.setPreviousRectangle(initEleveRectangle(rectangleEleveParams));
				drawingPanelEleve.setCoordinatesEqual(areCoordinatesEqual(drawingPanelEleve.getRectangle(), drawingPanelTemoin.getRectangle()));
				drawingPanelEleve.setBothSpecial(areBothSpecial(drawingPanelEleve.getRectangle(), drawingPanelTemoin.getRectangle()));

			default :
			}
			
		});
		return rectangleCarreCheckBox;
	}
	
	private void setFigure(Figure selectedFigure) {
		makeToolbars();
		if (drawingPanelTemoin != null)
			drawingPanelTemoin.repaint();
		pack();
	}
			
	
	private Figure getCurrentFigure() {
		return figuresComboBox == null || (Figure) figuresComboBox.getSelectedItem() == null ? Figure.CERCLE : (Figure) figuresComboBox.getSelectedItem();
	}
	
	private void makeToolbars() {
		JComponent[][] lignes = new JComponent[][] { new JComponent[] { figuresComboBox, initCarreCheckBox(), initEqCheckBox() },
				new JComponent[] { initDeplacerButton(), initTournerButton(), initRedimensionnerButton(), initRecommencerButton() }};
		if (toolbars == null) {
			toolbars = new Box(BoxLayout.Y_AXIS);
			toolbars.setFocusable(false);
		} else {
			toolbars.removeAll();
		}
		for (JComponent[] ligne : lignes) {
			JPanel toolbar = new JPanel();
			toolbar.setLayout(new FlowLayout());
			for (JComponent c : ligne) {
				toolbar.add(c);
			}
			toolbars.add(toolbar);
		}
		toolbars.revalidate();
	}
	
	public boolean areCoordinatesEqual(Shape eleve, Shape temoin) {
		Figure f = getCurrentFigure();
		switch (f) {
		case CERCLE :
			Rectangle2D cercleTemoin = temoin.getBounds2D();
			double centreCercleXTemoin = cercleTemoin.getCenterX();
			double centreCercleYTemoin = cercleTemoin.getCenterY();
			double rayonTemoin = cercleTemoin.getHeight()/2.0;
			Object cercleEleve = converter.convertShapeToCercleAvecPoint(eleve);
			Object centreCercleEleve = testeurCercle.invokeGetCentre(cercleEleve);
			double centreCercleXEleve = testeurPoint.invokeGetX(centreCercleEleve);
			double centreCercleYEleve = testeurPoint.invokeGetY(centreCercleEleve);
			double rayonEleve = testeurCercle.invokeGetRayon(cercleEleve);
			if (Math.abs(centreCercleXTemoin - centreCercleXEleve) > 0.02 || Math.abs(centreCercleYTemoin - centreCercleYEleve) > 0.02 ||
					Math.abs(rayonEleve - rayonTemoin) > 0.02) {
						return false;
					}
			return true;
			
		case RECTANGLE :
			Object rectangleEleve = converter.convertShapeToRectangleAvecPoint(eleve, angleRectangleEleve);
			Object centreRectangleEleve = testeurRectangle.invokeGetCentre(rectangleEleve);
			double centreRectangleXEleve = testeurPoint.invokeGetX(centreRectangleEleve);
			double centreRectangleYEleve = testeurPoint.invokeGetY(centreRectangleEleve);
			double longueurEleve = testeurRectangle.invokeGetLongueur(rectangleEleve);
			double largeurEleve = testeurRectangle.invokeGetLargeur(rectangleEleve);
			double angleEleve = testeurRectangle.invokeGetAngle(rectangleEleve);
			
			double[] coordinates = converter.convertShapeRectangleToCoordinates(temoin);
			double centreRectangleXTemoin = (coordinates[0]+coordinates[2]+coordinates[4]+coordinates[6])/4.0;
			double centreRectangleYTemoin = (coordinates[1]+coordinates[3]+coordinates[5]+coordinates[7])/4.0;
			double longueurTemoin = Math.max(this.calculateDistance(coordinates[0], coordinates[1], coordinates[2], coordinates[3]), this.calculateDistance(coordinates[2], coordinates[3], coordinates[4], coordinates[5]));
			double largeurTemoin =  Math.min(this.calculateDistance(coordinates[0], coordinates[1], coordinates[2], coordinates[3]), this.calculateDistance(coordinates[2], coordinates[3], coordinates[4], coordinates[5]));
			double angleTemoin = angleRectangleTemoin;
			
			if (Math.abs(centreRectangleXTemoin - centreRectangleXEleve) > 0.02 || Math.abs(centreRectangleYTemoin - centreRectangleYEleve) > 0.02 ||
					Math.abs(longueurEleve - longueurTemoin) > 0.02 || Math.abs(largeurEleve - largeurTemoin) > 0.02 ||
					Math.abs(angleEleve - angleTemoin) > 0.02) {
						return false;
				}
			return true;
			
		case TRIANGLE :
			double[] coordinatesTriangle = converter.convertShapeTriangleToCoordinates(temoin);
			double x1Temoin = coordinatesTriangle[0];
			double x2Temoin = coordinatesTriangle[2];
			double x3Temoin = coordinatesTriangle[4];
			double y1Temoin = coordinatesTriangle[1];
			double y2Temoin = coordinatesTriangle[3];
			double y3Temoin = coordinatesTriangle[5];
			
			Object triangleEleve = converter.convertShapeToTriangleAvecPoint(eleve);
			Object point1Eleve = testeurTriangle.invokeGetPoint1(triangleEleve);
			Object point2Eleve = testeurTriangle.invokeGetPoint2(triangleEleve);
			Object point3Eleve = testeurTriangle.invokeGetPoint3(triangleEleve);
			double x1Eleve = testeurPoint.invokeGetX(point1Eleve);
			double x2Eleve = testeurPoint.invokeGetX(point2Eleve);
			double x3Eleve = testeurPoint.invokeGetX(point3Eleve);
			double y1Eleve = testeurPoint.invokeGetY(point1Eleve);
			double y2Eleve = testeurPoint.invokeGetY(point2Eleve);
			double y3Eleve = testeurPoint.invokeGetY(point3Eleve);
			if (Math.abs(x1Temoin - x1Eleve) > 0.05 || Math.abs(x2Temoin - x2Eleve) > 0.05 ||
					Math.abs(x3Temoin - x3Eleve) > 0.05 || Math.abs(y1Temoin - y1Eleve) > 0.05 || 
					Math.abs(y2Temoin - y2Eleve) > 0.05 || Math.abs(y3Temoin - y3Eleve) > 0.05) {
				return false;
				}
			return true;
			
		default : 
			return false;
		}
	}
	
	private boolean areBothSpecial(Shape eleve, Shape temoin) {
		Figure f = getCurrentFigure();
		switch(f) {
		case CERCLE :
			Object cercleEleve = converter.convertShapeToCercleAvecPoint(eleve);
			boolean isGrandEleve = testeurCercle.invokeIsGrand(cercleEleve);
			boolean isGrandTemoin = Math.round((temoin.getBounds2D().getHeight()*100/2.0)/100.0) > 100;
			return isGrandEleve == isGrandTemoin;
		case RECTANGLE :
			Object rectangleEleve = converter.convertShapeToRectangleAvecPoint(eleve, angleRectangleEleve);
			boolean isCarreEleve = testeurRectangle.invokeIsCarre(rectangleEleve);
			boolean isCarreTemoin = Math.round(temoin.getBounds2D().getWidth()*100)/100.0 == Math.round(temoin.getBounds2D().getHeight()*100)/100.0;
			return isCarreTemoin == isCarreEleve;
		case TRIANGLE : 
			Object triangleEleve = converter.convertShapeToTriangleAvecPoint(eleve);
			boolean isEquilateralEleve = testeurTriangle.invokeIsEquilateral(triangleEleve);
			double[] coordinatesTemoin = converter.convertShapeTriangleToCoordinates(temoin);
			double x1 = coordinatesTemoin[0];
			double y1 = coordinatesTemoin[1];
			double x2 = coordinatesTemoin[2];
			double y2 = coordinatesTemoin[3];
			double x3 = coordinatesTemoin[4];
			double y3 = coordinatesTemoin[5];
			double longueur1 = calculateDistance(x1, y1, x2, y2);
			double longueur2 = calculateDistance(x2, y2, x3, y3);
			double longueur3 = calculateDistance(x1, y1, x3, y3);
			boolean isEquilateralTemoin = (Math.round(longueur1*100)/100. == Math.round(longueur2*100)/100.) 
					&& (Math.round(longueur1*100)/100. == Math.round(longueur3*100)/100.) 
					&& (Math.round(longueur2*100)/100. == Math.round(longueur3*100)/100.);
			return isEquilateralTemoin == isEquilateralEleve;
		default :
			return false;
		}
		
	}
	
	private double calculateDistance(double x1, double y1, double x2, double y2) {
		return 	Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	private class DrawingPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		private Shape rectangle, cercle, triangle;
		private Shape previousRectangle, previousCercle, previousTriangle;
		private JTextPane coordinates;
		private boolean areCoordinatesEqual;
		private boolean isEleve;
		private boolean areBothSpecial;
		private JTextPane figureBoolean;
		
		public DrawingPanel(Shape c, Shape r, Shape t, boolean b) {
			super(new BorderLayout());
			this.rectangle = r;
			this.cercle = c;
			this.triangle = t;
			this.previousRectangle = r;
			this.previousTriangle = t;
			this.previousCercle = c;
			this.coordinates = new JTextPane();
			this.coordinates.setEditable(false);
			this.coordinates.setOpaque(false);
			this.figureBoolean = new JTextPane();
			this.figureBoolean.setEditable(false);
			this.figureBoolean.setOpaque(false);
			this.isEleve = b;
			this.add(coordinates, BorderLayout.EAST);
			this.add(figureBoolean, BorderLayout.WEST);
			setPreferredSize(new Dimension(600, 500));
			setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			setBackground(Color.WHITE);
			setFocusable(false);
		}
		
		public void setCoordinatesEqual(boolean b) {
			this.areCoordinatesEqual = b;
		}
		
		public void setBothSpecial(boolean b) {
			this.areBothSpecial = b;
		}
		
		public Shape getRectangle() {
			return rectangle;
		}
		
		public void setRectangle(Shape r) {
			this.previousRectangle = this.rectangle;
			this.rectangle = r;
		}
		
		public void setPreviousRectangle(Shape r) {
			this.previousRectangle = r;
		}
		
		public Shape getTriangle() {
			return triangle;
		}
		
		public void setTriangle(Shape t) {
			this.previousTriangle = this.triangle;
			this.triangle = t;
		}
		
		public void setPreviousTriangle(Shape t) {
			this.previousTriangle = t;
		}
		
		public Shape getCercle() {
			return cercle;
		}
		
		public void setPreviousCercle(Shape c) {
			this.previousCercle =  c;
		}
		
		public void setCercle(Shape c) {
			this.previousCercle = this.cercle;
			this.cercle = c;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Figure figure = getCurrentFigure();
			
			if (this.isEleve) {
				if (this.areCoordinatesEqual) {
					this.coordinates.setForeground(Color.GREEN);
				} else {
					this.coordinates.setForeground(Color.RED);
				} 
			}
			
			if (this.isEleve) {
				if (this.areBothSpecial) {
					this.figureBoolean.setForeground(Color.BLUE);
					this.figureBoolean.setForeground(Color.GREEN);
				} else {
					this.figureBoolean.setForeground(Color.RED);
				}
			}

			switch(figure) {

				case TRIANGLE :
					this.figureBoolean.setText(formatTextBoolean(this.triangle));
					this.coordinates.setText(formatTextCoordinates(this.triangle));
					((Graphics2D)g).setColor(Color.LIGHT_GRAY);
					((Graphics2D)g).draw(this.previousTriangle);
					((Graphics2D)g).setColor(Color.BLACK);
					((Graphics2D)g).draw(this.triangle);
					pack();
					break;
				
				case CERCLE : 
					
					this.figureBoolean.setText(formatTextBoolean(this.cercle));
					this.coordinates.setText(formatTextCoordinates(this.cercle));
					((Graphics2D)g).setColor(Color.LIGHT_GRAY);
					((Graphics2D)g).draw(this.previousCercle);
					((Graphics2D)g).setColor(Color.BLACK);
					((Graphics2D)g).draw(this.cercle);
					pack();
					break;
				
				case RECTANGLE : 
					this.figureBoolean.setText(formatTextBoolean(this.rectangle));
					this.coordinates.setText(formatTextCoordinates(this.rectangle));
					((Graphics2D)g).setColor(Color.LIGHT_GRAY);
					((Graphics2D)g).draw(this.previousRectangle);
					((Graphics2D)g).setColor(Color.BLACK);
					((Graphics2D)g).draw(this.rectangle);
					pack();
					break;
			}
		}

		private String formatTextCoordinates(Shape s) {
			Figure figure = getCurrentFigure();
			switch (figure) {
			case CERCLE:
				if (this.isEleve) {
				Object c = converter.convertShapeToCercleAvecPoint(s);
				Object centre = testeurCercle.invokeGetCentre(c);
				double centreX = testeurPoint.invokeGetX(centre);
				double centreY = testeurPoint.invokeGetY(centre);
				double rayon = testeurCercle.invokeGetRayon(c);
				return new String("""
						Coordonnées du cercle : 
						centreX : %s
						centreY : %s
						rayon : %s""").formatted(Math.round(centreX*1000)/1000., 
								Math.round(centreY*1000)/1000., 
								Math.round(rayon*1000)/1000.)					
						;
				} else {
					double centreX = s.getBounds2D().getCenterX();
					double centreY = s.getBounds2D().getCenterY();
					double rayon = s.getBounds2D().getHeight()/2.0;
					return new String("""
							Coordonnées du cercle : 
							centreX : %s
							centreY : %s
							rayon : %s""").formatted(Math.round(centreX*1000)/1000., 
									Math.round(centreY*1000)/1000., 
									Math.round(rayon*1000)/1000.)					
							;
				}

			case RECTANGLE:
				
				double[] coordinatesRectangle = converter.convertShapeRectangleToCoordinates(s);
				double x1Rectangle = coordinatesRectangle[0];
				double y1Rectangle = coordinatesRectangle[1];
				double x2Rectangle = coordinatesRectangle[2];
				double y2Rectangle = coordinatesRectangle[3];
				double x3Rectangle = coordinatesRectangle[4];
				double y3Rectangle = coordinatesRectangle[5];
				double longueur1 = calculateDistance(x1Rectangle, y1Rectangle, x2Rectangle, y2Rectangle);
				double longueur2 = calculateDistance(x2Rectangle, y2Rectangle, x3Rectangle, y3Rectangle);
				double longueur = Math.max(longueur1, longueur2);
				double largeur = Math.min(longueur1, longueur2);
				double centreX = s.getBounds2D().getCenterX();
				double centreY = s.getBounds2D().getCenterY();
				double angleRectangle = this.isEleve ? angleRectangleEleve : angleRectangleTemoin;
				double angleModulo = Math.IEEEremainder(angleRectangle, 360);
				return new String("""
						Coordonnées du rectangle :
						centreX : %s
						centreY : %s
						longueur : %s
						largeur : %s
						angle : %s°""").formatted(
								Math.round(centreX*100)/100.,
								Math.round(centreY*100)/100.,
								Math.round(longueur*100)/100.,
								Math.round(largeur*100)/100.,
								Math.round(angleModulo*100)/100.
								);
				
				
			case TRIANGLE:
				
				double[] coordinatesTriangle = converter.convertShapeTriangleToCoordinates(s);
				double x1Triangle = coordinatesTriangle[0];
				double y1Triangle = coordinatesTriangle[1];
				double x2Triangle = coordinatesTriangle[2];
				double y2Triangle = coordinatesTriangle[3];
				double x3Triangle = coordinatesTriangle[4];
				double y3Triangle = coordinatesTriangle[5];
				return new String("""
						Coordonnées du triangle :
						(x1, y1) : (%s, %s)
						(x2, y2) : (%s, %s)
						(x3, y3) : (%s, %s)""").formatted(Math.round(x1Triangle *100)/100.,
								Math.round(y1Triangle *100)/100.,
								Math.round(x2Triangle *100)/100.,
								Math.round(y2Triangle *100)/100.,
								Math.round(x3Triangle *100)/100.,
								Math.round(y3Triangle *100)/100.);
				
			default:
				return "";
			}
		}

	
	private String formatTextBoolean(Shape s) {
		Figure figure = getCurrentFigure();
		switch (figure) {
		case CERCLE:
			if (this.isEleve) {
				Object c = converter.convertShapeToCercleAvecPoint(s);
				boolean isGrand = testeurCercle.invokeIsGrand(c);
				return new String("Le cercle est %s d'après votre code.").formatted(isGrand ? "grand" : "petit");
			} else {
				boolean isGrand = Math.round((s.getBounds2D().getHeight()*100/2.0)/100.0) > 100;
				return new String("Le cercle est %s.").formatted(isGrand ? "grand" : "petit");
			}
			
			case RECTANGLE :
				if (this.isEleve) {
					Object rectangle = converter.convertShapeToRectangleAvecPoint(s, angleRectangleEleve);
					boolean isCarre = testeurRectangle.invokeIsCarre(rectangle);
					return new String("Le rectangle %s d'après votre code.").formatted(isCarre ? "est un carré" : "n'est pas un carré");
				} else {
					boolean isCarre = Math.round(s.getBounds2D().getWidth()*100)/100.0 == Math.round(s.getBounds2D().getHeight()*100)/100.0;
					return new String("Le rectangle %s.").formatted(isCarre ? "est un carré" : "n'est pas un carré");
				}
			
				
			case TRIANGLE : 
				if (this.isEleve) {
					Object triangle = converter.convertShapeToTriangleAvecPoint(s);
					boolean isEquilateral = testeurTriangle.invokeIsEquilateral(triangle);
					return new String("Le triangle %s d'après votre code.").formatted(isEquilateral ? "est équilatéral" : "n'est pas équilatéral");

				} else {
				
				double[] coordinates = converter.convertShapeTriangleToCoordinates(s);
				double x1 = coordinates[0];
				double y1 = coordinates[1];
				double x2 = coordinates[2];
				double y2 = coordinates[3];
				double x3 = coordinates[4];
				double y3 = coordinates[5];
				double longueur1 = calculateDistance(x1, y1, x2, y2);
				double longueur2 = calculateDistance(x2, y2, x3, y3);
				double longueur3 = calculateDistance(x1, y1, x3, y3);
				boolean isEquilateral = (Math.round(longueur1*100)/100. == Math.round(longueur2*100)/100.) 
						&& (Math.round(longueur1*100)/100. == Math.round(longueur3*100)/100.) 
						&& (Math.round(longueur2*100)/100. == Math.round(longueur3*100)/100.);
				return new String("Le triangle %s.").formatted(isEquilateral ? "est équilatéral" : "n'est pas équilatéral");

				}
			default : 
				return "";
		}
	}
	}
	
	enum Figure {
		
		CERCLE("Cercle", new JComponent[] {  }), RECTANGLE("Rectangle", new JComponent[] {  }),
		TRIANGLE("Triangle", new JComponent[] {  });
		
		private String name;
		private JComponent[] toolbar;
		
		Figure(String name, JComponent[] toolbar) {
			this.name = name;
			this.toolbar = toolbar;
		}
		
		public String getName() {
			return name;
		}
		
		public JComponent[] getToolbar() {
			return toolbar;
		}
		
	}
	
	public static void main(String[] args) {
		new TesteurVisuelAvecPoint();
	}
	
}
