package org.oxyl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Tests associés au Rectangle avec Point")
@TestMethodOrder(OrderAnnotation.class)
public class TesteurRectangleAvecPoint {

	private static enum Variables {

		POINT("centre"), LONGUEUR("longueur", double.class), LARGEUR ("largeur", double.class), ANGLE("angle", double.class);

		private String fieldName;
		private Class<?> type;

		Variables(String fieldName, Class<?> type) {
			this.fieldName = fieldName;
			this.type = type;
		}

		Variables(String fieldName) {
			this.fieldName = fieldName;
			this.type = Object.class;
		}

		public static Variables findByFieldName(String fieldName) {
			return valuesList().stream().filter(v -> v.getFieldName().equals(fieldName)).findAny().orElse(null);
		}

		public String getFieldName() {
			return fieldName;
		}

		public Class<?> getType() {
			return type;
		}

		public void setType(Class<?> type) {
			this.type = type;
		}

		public static List<Variables> valuesList() {
			return Arrays.asList(Variables.values());
		}

	}

	private static final boolean CHECK_PRIVATE_FIELDS = true;

	private Class<?> RectangleClass;
	private Class<?> PointClass;
	private boolean isRectangleImplemented;

	{
		TesteurPoint.testBeforeMocking();
		try {
			PointClass = Class.forName("org.oxyl.Point");
			Variables.POINT.setType(PointClass);
		} catch (Exception e) {
			try {
				PointClass = Class.forName("Point");
				Variables.POINT.setType(PointClass);
			} catch (Exception e2) {
			}
		}
		try {
			RectangleClass = Class.forName("org.oxyl.RectangleAvecPoint");
			isRectangleImplemented = true;
		} catch (Exception e) {
			try {
				RectangleClass = Class.forName("RectangleAvecPoint");
				isRectangleImplemented = true;
			} catch (Exception e2) {
				isRectangleImplemented = false;
			}
		}
	}

	@Test
	@Order(1)
	@DisplayName("Présence de la classe RectangleAvecPoint.java")
	public void classe_Rectangle_doit_exister() {
		checkIfRectangleIsImplemented();
	}

	private static final Stream<Arguments> argumentsForInstanceVariables() {
		return Variables.valuesList().stream().map(v -> Arguments.of(v.getFieldName()));
	}

	@Order(2)
	@DisplayName("Présence des variables d'instance")
	@ParameterizedTest(name = "Variable : {0}")
	@MethodSource("argumentsForInstanceVariables")
	public void Rectangle_doit_avoir_les_variables_dInstance(String fieldName) {
		checkIfRectangleIsImplemented();
		privateValueGetter(fieldName, Variables.findByFieldName(fieldName).getType());
	}

	@Test
	@Order(3)
	@DisplayName("Présence du constructeur principal")
	public void Rectangle_doit_implementer_le_constructeur_principal() {
		checkIfRectangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
	}

	private static final Stream<Arguments> argumentsForConstructorTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 0.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0, 2.0, 1.2, 90.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 2.5, 1.0, 60.0)), Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 5.0, 270.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 10.0, 45.0)));
	}

	@Order(4)
	@DisplayName("Fonctionnement du constructeur principal")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorTest")
	public void constructeur_principal_Rectangle_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfRectangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		
		Object point = newPoint(argsList.get(0), argsList.get(1));
		Object[] args = new Object[] {argsList.get(0), argsList.get(1), argsList.get(2), argsList.get(3), argsList.get(4)};
		Object rectangle = getRectangle(args);

		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(rectangle);
		Object valueLongueur = privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		Object valueLargeur = privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		Object valueAngle = privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		List<Object> expected = Arrays.asList(point, argsList.get(2), argsList.get(3), argsList.get(4));
		assertEquals(expected, Arrays.asList(valuePoint, valueLongueur, valueLargeur, valueAngle),
				String.format(
						"Le rectangle créé (centre, longueur, largeur, angle)=(%s, %s, %s, %s) n'est pas celui attendu : "
						+ "(centre, longueur, largeur, angle)=(%s, %s, %s, %s).",
						toFormat(pointToString(valuePoint), valueLongueur, valueLargeur, valueAngle, pointToString(point), 
								argsList.get(2), argsList.get(3), argsList.get(4))));
	}

	@Test
	@Order(5)
	@DisplayName("Présence et fonctionnement du constructeur vide")
	public void constructeur_vide_Rectangle_doit_modifier_les_variables_dInstance() {
		checkIfRectangleIsImplemented();
		checkIfConstructorVideIsCorrectlyDefined();
		Object rectangle = getRectangleVide();
		Object point = newPoint(0.0, 0.0);
		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(rectangle);
		Object valueLongueur = privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		Object valueLargeur = privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		Object valueAngle = privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		List<Object> expected = Arrays.asList(point, 1.0, 1.0, 0.0);
		assertEquals(expected, Arrays.asList(valuePoint, valueLongueur, valueLargeur, valueAngle), String.format(
				"Le rectangle par défaut (centre, longueur, largeur, angle)=(%s, %s, %s ,%s) obtenu n'est pas celui attendu : "
				+ "(centre, longueur, largeur, angle)=(%s, %s, %s, %s).",
				toFormat(pointToString(valuePoint), valueLongueur, valueLargeur, valueAngle, pointToString(point), 1.0, 1.0, 0.0)));
	}

	private static final Stream<Arguments> argumentsForConstructorByCopieTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 0.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0, 2.0, 1.2, 90.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 2.5, 1.0, 60.5)), Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 5.0, 270.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 10.0, 45.0)));
	}

	@Order(6)
	@DisplayName("Présence et fonctionnement du constructeur par copie")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorByCopieTest")
	public void constructeur_par_copie_Rectangle_doit_modifier_les_variables_dInstance(List<Object> argsList) {
		checkIfRectangleIsImplemented();
		checkIfConstructorParCopieIsCorrectlyDefined();
		
		Object point = newPoint(argsList.get(0), argsList.get(1));
		Object[] args = new Object[] {argsList.get(0), argsList.get(1), argsList.get(2), argsList.get(3), argsList.get(4)};
		
		Object rectangleACopier = getRectangle(args);
		Object rectangle = getRectangleCopie(rectangleACopier);

		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(rectangle);
		Object valueLongueur = privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		Object valueLargeur = privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		Object valueAngle = privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		List<Object> expected = Arrays.asList(point, argsList.get(2), argsList.get(3), argsList.get(4));
		
		assertEquals(expected, Arrays.asList(valuePoint, valueLongueur, valueLargeur, valueAngle),
				String.format(
						"Le rectangle créé par copie (centre, longueur, largeur, angle)=(%s, %s, %s, %s) n'est pas celui attendu : "
						+ "(centre, longueur, largeur, angle)=(%s, %s, %s, %s).",
						toFormat(pointToString(valuePoint), valueLongueur, valueLargeur, valueAngle, pointToString(point), 
								argsList.get(2), argsList.get(3), argsList.get(4))));
	}

	private static final Stream<Arguments> argumentsForDeplacerTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0)),
				Arguments.of(Arrays.asList(5.0, -1.0)), Arguments.of(Arrays.asList(1.0, 4.2)),
				Arguments.of(Arrays.asList(-1.0, 4.0)));
	}

	@Order(7)
	@DisplayName("Fonctionnement de la méthode deplacer")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForDeplacerTest")
	public void deplacer_doit_modifier_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfRectangleIsImplemented();
		Object oldValuePoint = newPoint(1.0, 1.5);
		Object oldValueLongueur = 6.2;
		Object oldValueLargeur = 4.0;
		Object oldValueAngle = 90.0;
		Object rectangle = getRectangle(new Object[] { 1.0, 1.5, oldValueLongueur, oldValueLargeur, oldValueAngle });
		Object pointToAdd = newPoint(argsList.get(0), argsList.get(1));
		
		invokeMethodWithReturn(rectangle, "deplacer", new Class<?>[] { double.class, double.class }, new Object[] { argsList.get(0), argsList.get(1) }, void.class);
		Object newValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType())
				.apply(rectangle);
		Object newValueLongueur = privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType())
				.apply(rectangle);
		Object newValueLargeur = privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType())
				.apply(rectangle);
		Object newValueAngle = privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType())
				.apply(rectangle);
		assertEquals(Arrays.asList(addPoint(oldValuePoint, pointToAdd), oldValueLongueur, oldValueLargeur, oldValueAngle),
				Arrays.asList(newValuePoint, newValueLongueur, newValueLargeur, newValueAngle),
				String.format(
						"Le rectangle (centre, longueur, largeur, angle)=(%s, %s, %s, %s) est devenu "
						+ "(centre, longueur, largeur, angle)=(%s, %s, %s, %s) après translation de %s.",
						toFormat(pointToString(oldValuePoint), oldValueLongueur, oldValueLargeur, oldValueAngle, 
						pointToString(newValuePoint), newValueLongueur, newValueLargeur, newValueAngle, pointToString(pointToAdd))));
	}

	private static final Stream<Arguments> argumentsForIsCarreTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 2.0, 0.0, 0.0, 0.0), true), Arguments.of(Arrays.asList(2.0, 1.0, 2.0, 1.2, 90.0), false),
				Arguments.of(Arrays.asList(5.0, -1.0, 2.0, 1.8, 60.5), false), Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 5.0, 270.0), true),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.3, 5.0, 45.0), false));
	}

	@Order(8)
	@DisplayName("Fonctionnement de la méthode isCarre")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForIsCarreTest")
	public void isCarre_doit_retourner_le_bon_boolean(List<Double> argsList, boolean expectedResult) {
		checkIfRectangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object point = newPoint(argsList.get(0), argsList.get(1));
		Object[] args = new Object[] {argsList.get(0), argsList.get(1), argsList.get(2), argsList.get(3), argsList.get(4)};
		Object rectangle = getRectangle(args);
		boolean result = invokeMethodWithReturn(rectangle, "isCarre", null, null, boolean.class);
		assertEquals(expectedResult, result, String.format("Le rectangle (centre, longueur, largeur, angle)="
				+ "(%s, %s, %s, %s) %s être un carré.",
				toFormat(pointToString(point), args[1], args[2], args[3], expectedResult ? "devrait" : "ne devrait pas")));
	}

	private static final Stream<Arguments> argumentsForRedimensionnerTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 2.0, 0.0, 0.0, 0.0), 1.0), Arguments.of(Arrays.asList(2.0, 1.0, 2.0, 1.2, 90.0), 2.0),
				Arguments.of(Arrays.asList(5.0, -1.0, 2.0, 1.8, 60.5), 2.5), Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 5.0, 270.0), 0.3),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.3, 5.0, 45.0), 9.99));
	}

	@Order(9)
	@DisplayName("Fonctionnement de la méthode redimensionner")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForRedimensionnerTest")
	public void redimensionner_doit_actualiser_le_rectangle_correctement(List<Double> argsList, double f) {
		checkIfRectangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		
		Object oldValuePoint = newPoint(argsList.get(0), argsList.get(1));
		Double oldValueLongueur = argsList.get(2);
		Double oldValueLargeur = argsList.get(3);
		Object oldValueAngle = argsList.get(4);
		Object[] args = new Object[] {argsList.get(0), argsList.get(1), oldValueLongueur, oldValueLargeur, oldValueAngle};
		Object rectangle = getRectangle(args);
	
		invokeMethodWithReturn(rectangle, "redimensionner", new Class<?>[] { double.class }, new Object[] { f } , void.class);
		
		Object newValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(rectangle);
		Double newValueLongueur = (Double) privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType())
				.apply(rectangle);
		Double newValueLargeur = (Double) privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType())
				.apply(rectangle);
		Double newValueAngle = (Double) privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType())
				.apply(rectangle);
		
		assertEquals(Arrays.asList(oldValuePoint, oldValueLongueur * f, oldValueLargeur * f, oldValueAngle),
				Arrays.asList(newValuePoint, newValueLongueur, newValueLargeur, newValueAngle),
				String.format(
						"Après un redimensionnement de facteur %s, le rectangle (centre, longueur,  largeur, angle)="
						+ "(%s, %s, %s, %s) est devenu (centre, longueur, largeur, angle)=(%s, %s %s, %s).",
						toFormat(f, pointToString(oldValuePoint), argsList.get(2), argsList.get(3), argsList.get(4),
								pointToString(newValuePoint), newValueLongueur, newValueLargeur, newValueAngle)));
	}
	
	private static final Stream<Arguments> argumentsForTournerTest() {
		return Stream.of(Arguments.of(0.0), Arguments.of(100.0),
				Arguments.of(-15.5), Arguments.of(45.2),
				Arguments.of(180.0));
	}

	@Order(10)
	@DisplayName("Fonctionnement de la méthode tourner")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForTournerTest")
	public void tourner_doit_actualiser_le_rectangle_correctement(double f) {
		checkIfRectangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		
		Object oldValuePoint = newPoint(1.0, 1.5);
		Object oldValueLongueur = 4.2;
		Object oldValueLargeur = 2.0;
		Double oldValueAngle = 90.0;
		Object rectangle = getRectangle(new Object[] { 1.0, 1.5, oldValueLongueur, oldValueLargeur, oldValueAngle });
		invokeMethodWithReturn(rectangle, "tourner", new Class<?>[] { double.class }, new Object[] {f}, void.class);
		
		Object newValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(rectangle);
		Double newValueLongueur = (Double) privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType())
				.apply(rectangle);
		Double newValueLargeur = (Double) privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType())
				.apply(rectangle);
		Double newValueAngle = (Double) privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType())
				.apply(rectangle);
		
		assertEquals(Arrays.asList(oldValuePoint, oldValueLongueur, oldValueLargeur, oldValueAngle + f),
				Arrays.asList(newValuePoint, newValueLongueur, newValueLargeur, newValueAngle),
				String.format(
						"Après une rotation d'un angle %s, le rectangle (centre, longueur,  largeur, angle)="
						+ "(%s, %s, %s, %s) est devenu (centre, longueur, largeur, angle)=(%s, %s, %s, %s).",
						toFormat(f, pointToString(oldValuePoint), oldValueLongueur, oldValueLargeur, oldValueAngle,
								pointToString(newValuePoint), newValueLongueur, newValueLargeur, newValueAngle)));
	}


	private Object addPoint(Object p1, Object p2) {
		try {
			Field fx = PointClass.getDeclaredField("x");
			fx.setAccessible(true);
			Double x1 = (Double) fx.get(p1);
			Double x2 = (Double) fx.get(p2);
			Field fy = PointClass.getDeclaredField("y");
			fy.setAccessible(true);
			Double y1 = (Double) fy.get(p1);
			Double y2 = (Double) fy.get(p2);
			return newPoint(x1 + x2, y1 + y2);
		} catch (Exception e) {
			return null;
		}
	}

	private String pointToString(Object point) {
		try {
			Field fx = PointClass.getDeclaredField("x");
			fx.setAccessible(true);
			String x = String.valueOf(fx.get(point));
			Field fy = PointClass.getDeclaredField("y");
			fy.setAccessible(true);
			String y = String.valueOf(fy.get(point));
			return String.format("(%s, %s)", x, y);
		} catch (Exception e) {
			return "null";
		}
	}

	
	private Object newPoint(Object x, Object y) {
		try {
			return PointClass.getConstructor(double.class, double.class).newInstance(x, y);
		} catch (Exception e) {
			return null;
		}
	}

	private Object[] toFormat(Object... args) {
		List<Object> res = new ArrayList<>();
		for (Object arg : args) {
			if (arg instanceof List) {
				for (Object elem : (List<?>) arg) {
					res.add(elem);
				}
			} else if (arg instanceof Object[]) {
				for (Object elem : (Object[]) arg) {
					res.add(elem);
				}
			} else {
				res.add(arg);
			}
		}
		return res.toArray();
	}

	@SuppressWarnings("unchecked")
	public <T> T invokeMethodWithReturn(Object o, String methodName, Class<?>[] types, Object[] args,
			Class<T> returnType) {
		Object objectRes = invokeMethod(o, methodName, types, args);
		try {
			Method method = RectangleClass.getDeclaredMethod(methodName, types);
			if (!method.getReturnType().equals(returnType))
				fail(String.format("La méthode '%s' devrait retourner un %s.", methodName, returnType.getSimpleName()));
			if (returnType.isPrimitive()) {
				return (T) objectRes;
			} else {
				return returnType.cast(objectRes);
			}
		} catch (Exception e) {
			fail("Etrange... Impossible de vérifier le type de retour de la méthode " + methodName + ".");
		}
		return null;
	}

	private Object invokeMethod(Object o, String methodName, Class<?>[] types, Object[] args) {
		try {
			Method method = RectangleClass.getDeclaredMethod(methodName, types);
			if (method.getModifiers() != Modifier.PUBLIC)
				fail(String.format("La méthode '%s' doit être public.", methodName));
			try {
				return method.invoke(o, args);
			} catch (Exception e) {
				fail("Les paramètres ou le retour de la méthode appelée ne sont pas du bon type.");
			}
		} catch (Exception e) {
			if (types == null || types.length == 0) {
				fail(String.format("La méthode '%s' n'existe pas.", methodName));
			} else {
				fail(String.format("La méthode '%s' avec les paramètres de types '%s' n'existe pas.", methodName,
						listToFrenchString(Arrays.asList(types).stream().map(c -> c.getSimpleName())
								.collect(Collectors.toList()))));
			}
		}
		return null;
	}
	
	public Object invokeDeplacer(Object o, Object... args) {
		return invokeMethodWithReturn(o, "deplacer", new Class[] {double.class, double.class}, args, void.class);
    }
	
	public Object invokeRedimensionner(Object o, Object... args) {
		return invokeMethodWithReturn(o, "redimensionner", new Class[] {double.class}, args, void.class);
    }
	
	public Object invokeTourner(Object o, Object... args) {
		return invokeMethodWithReturn(o, "tourner", new Class[] {double.class}, args, void.class);
	}
	
	public double invokeGetLargeur(Object o) {
		return invokeMethodWithReturn(o, "getLargeur", new Class[] {}, null, double.class);
    }
	
	public double invokeGetLongueur(Object o) {
		return invokeMethodWithReturn(o, "getLongueur", new Class[] {}, null, double.class);
    }
	
	public double invokeGetAngle(Object o) {
		return invokeMethodWithReturn(o, "getAngle", new Class[] {}, null, double.class);
    }
	
	public Object invokeGetCentre(Object o) {
		return invokeMethodWithReturn(o, "getCentre", new Class[] {}, null, PointClass);
    }
	
	public boolean invokeIsCarre(Object o) {
		return invokeMethodWithReturn(o, "isCarre", new Class[]{}, new Object[]{}, boolean.class);
    }
	
	public Object getRectangle(Object[] args) {
		try {
			Object point = newPoint(args[0], args[1]);
			Class<?>[] constructorTypes = Variables.valuesList().stream().map(v -> v.getType())
					.toArray(Class<?>[]::new);
			return assertDoesNotThrow(() -> RectangleClass.getConstructor(constructorTypes).newInstance(point, args[2], args[3], args[4]),
					String.format(
							"La création du rectangle (centre, longueur, largeur, angle)=(%s, %s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
							pointToString(point), args[2], args[3], args[4]));
		} catch (Exception e) {
			// can never happen
			return null;
		}
	}

	public Object getRectangleCopie(Object rectangleACopier) {
		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType())
				.apply(rectangleACopier);
		Object valueLongueur = privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType())
				.apply(rectangleACopier);
		Object valueLargeur = privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType())
				.apply(rectangleACopier);
		Object valueAngle = privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType())
				.apply(rectangleACopier);
		return assertDoesNotThrow(() -> RectangleClass.getConstructor(RectangleClass).newInstance(rectangleACopier), String.format(
				"La copie du rectangle (centre, longueur, largeur, angle)=(%s, %s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
				pointToString(valuePoint), valueLongueur, valueLargeur, valueAngle));
	}

	public Object getRectangleVide() {
		return assertDoesNotThrow(() -> RectangleClass.getConstructor().newInstance(),
				"La création du rectangle par défaut a levé une exception alors qu'elle n'aurait pas dû.");
	}

	private void checkIfConstructorIsCorrectlyDefined() {
		List<String> constructors = getConstructors(RectangleClass);
		String expectedTypes = Variables.valuesList().stream().map(v -> v.getType().getSimpleName())
				.collect(Collectors.joining(", "));
		if (!constructors.contains(String.format("RectangleAvecPoint(%s)", expectedTypes))) {
			if (constructors.isEmpty()) {
				fail("Le constructeur de RectangleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur RectangleAvecPoint(%s), et ceux présents sont : %s.", expectedTypes,
					listToFrenchString(constructors)));
		}
	}

	private void checkIfConstructorVideIsCorrectlyDefined() {
		List<String> constructors = getConstructors(RectangleClass);
		if (!constructors.contains("RectangleAvecPoint()")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur vide de RectangleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur RectangleAvecPoint(), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}

	private void checkIfConstructorParCopieIsCorrectlyDefined() {
		List<String> constructors = getConstructors(RectangleClass);
		if (!constructors.contains("RectangleAvecPoint(RectangleAvecPoint)")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur par copie de RectangleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur RectangleAvecPoint(RectangleAvecPoin), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}

	private void checkIfRectangleIsImplemented() {
		if (!isRectangleImplemented) {
			fail("La classe RectangleAvecPoint.java n'est pas implémentée.");
		}
	}

	@SuppressWarnings("unchecked")
	private <V> Function<Object, V> privateValueGetter(String fieldName, Class<V> returnType) {
		try {
			Field field = RectangleClass.getDeclaredField(fieldName);
			if (CHECK_PRIVATE_FIELDS && field.getModifiers() != Modifier.PRIVATE)
				fail(String.format("La variable d'instance '%s' devrait être privée.", fieldName));
			field.setAccessible(true);
			if (!field.getType().equals(returnType))
				fail(String.format("La variable d'instance '%s' devrait avoir pour type de retour %s.", fieldName,
						returnType.getSimpleName()));
			return o -> {
				try {
					return returnType.isPrimitive() ? (V) field.get(o) : returnType.cast(field.get(o));
				} catch (Exception e) {
					fail(String.format("La variable d'instance '%s' est illisible. Peut-être du mauvais type ?",
							fieldName));
				}
				return null;
			};
		} catch (Exception e) {
			fail(String.format("La variable d'instance '%s' est introuvable.", fieldName));
		}
		return null;
	}

	// Ex : [RectangleAvecPoint(int, int), RectangleAvecPoint(String)]
	private List<String> getConstructors(Class<?> classe) {
		return Arrays.asList(classe.getConstructors()).stream()
				.map(c -> Arrays.asList(c.getParameters()).stream().map(p -> p.getType().getSimpleName())
						.collect(Collectors.toList()))
				.map(list -> "RectangleAvecPoint(" + (list.size() == 0 ? "" : list.toString().split("[\\[\\]]")[1]) + ")")
				.collect(Collectors.toList());
	}

	// Ex : [a, b, c] devient "a, b et c"
	private String listToFrenchString(List<String> liste) {
		int n = liste.size();
		if (n == 0)
			return "";
		if (n == 1)
			return liste.get(0);
		return String.join(", ", liste.subList(0, n - 1)) + " et " + liste.get(n - 1);
	}

}
