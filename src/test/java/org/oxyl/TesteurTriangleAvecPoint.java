package org.oxyl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

@DisplayName("Tests associés au Triangle")
@TestMethodOrder(OrderAnnotation.class)
public class TesteurTriangleAvecPoint {

	private static enum Variables {

		POINT1("point1"), POINT2("point2"), POINT3("point3");
		
		private String fieldName;
		private Class<?> type;

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

	private Class<?> TriangleClass;
	private Class<?> PointClass;
	private boolean isTriangleImplemented;

	{
		TesteurPoint.testBeforeMocking();
		try {
			PointClass = Class.forName("org.oxyl.Point");
			Variables.POINT1.setType(PointClass);
			Variables.POINT2.setType(PointClass);
			Variables.POINT3.setType(PointClass);
		} catch (Exception e) {
			try {
				PointClass = Class.forName("Point");
				Variables.POINT1.setType(PointClass);
				Variables.POINT2.setType(PointClass);
				Variables.POINT3.setType(PointClass);
			} catch (Exception e2) {
			}
		}
		try {
			TriangleClass = Class.forName("org.oxyl.TriangleAvecPoint");
			isTriangleImplemented = true;
		} catch (Exception e) {
			try {
				TriangleClass = Class.forName("TriangleAvecPoint");
				isTriangleImplemented = true;
			} catch (Exception e2) {
				isTriangleImplemented = false;
			}
		}
	}


	@Test
	@Order(1)
	@DisplayName("Présence de la classe TriangleAvecPoint.java")
	public void classe_Triangle_doit_exister() {
		checkIfTriangleIsImplemented();
	}

	private static final Stream<Arguments> argumentsForInstanceVariables() {
		return Variables.valuesList().stream().map(v -> Arguments.of(v.getFieldName()));
	}

	@Order(2)
	@DisplayName("Présence des variables d'instance")
	@ParameterizedTest(name = "Variable : {0}")
	@MethodSource("argumentsForInstanceVariables")
	public void Triangle_doit_avoir_les_variables_dInstance(String fieldName) {
		checkIfTriangleIsImplemented();
		privateValueGetter(fieldName, Variables.findByFieldName(fieldName).getType());
	}

	@Test
	@Order(3)
	@DisplayName("Présence du constructeur principal")
	public void Triangle_doit_implementer_un_constructeur() {
		checkIfTriangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
	}

	private static final Stream<Arguments> argumentsForConstructorTest() {
		return Stream.of(
				Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 1.0, 0.0, 3.0)), 
				Arguments.of(Arrays.asList(2.0, 1.0, 1.0, 1.0, 0.0, 3.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 1.0, 1.0, 0.0, 3.0)), 
				Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 1.0, 0.0, 3.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 1.0, 0.0, 3.0))
				);
	}
	
	@Test
	@Order(4)
	@DisplayName("Fonctionnement du constructeur vide")
	public void constructeur_Triangle_vide_doit_modifier_les_variables_dInstance() {
		checkIfTriangleIsImplemented();
		checkIfEmptyConstructorIsCorrectlyDefined();
		Object triangle = getEmptyTriangle();
		List<Object> values = this.getValuesAsList(triangle);
		List<Object> expected = Arrays.asList(
				this.newPoint(0.0, 0.0), 
				this.newPoint(0.0, 1.0), 
				this.newPoint(0.5, 0.5));
    	assertEquals(expected, values, String.format(
				"""	
					Le triangle par défaut obtenu 
					((x1, y1), (x2, y2), (x3, y3))=(%s, %s, %s)
					n'est pas celui attendu: 
					((x1, y1), (x2, y2), (x3, y3))=(%s, %s, %s)
    			"""
    			, this.toFormat(
    					this.pointToString(values.get(0)), 
    					this.pointToString(values.get(1)), 
    					this.pointToString(values.get(2)),
    					this.pointToString(expected.get(0)), 
    					this.pointToString(expected.get(1)), 
    					this.pointToString(expected.get(2))
    					)));
	}

	@Order(5)
	@DisplayName("Fonctionnement du constructeur principal")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorTest")
	public void constructeur_Triangle_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfTriangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object triangle = getTriangle(argsList.toArray());
		List<Object> values = this.getValuesAsList(triangle);
    	assertEquals(this.argsListToPointList(argsList), values, String.format(
				"""	
					Création d'un triangle de coordonnées (%s, %s, %s)
	    			avec l'entrée ((x1, y1), (x2, y2), (x3, y3)) =  ((%s, %s), (%s, %s), (%s, %s))
    			"""
    			, this.toFormat(
    					this.pointToString(values.get(0)), 
    					this.pointToString(values.get(1)), 
    					this.pointToString(values.get(2)),
    					argsList)));
	}
	
	private static final Stream<Arguments> argumentsForConstructorByCopyTest() {
		return Stream.of(
				Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 1.0, 0.0, 3.0)), 
				Arguments.of(Arrays.asList(2.0, 1.0, 1.0, 1.0, 0.0, 3.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 1.0, 1.0, 0.0, 3.0)), 
				Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 1.0, 0.0, 3.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 1.0, 0.0, 3.0))
				);
	}
	
	@Order(6)
	@DisplayName("Fonctionnement du constructeur par copie")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorByCopyTest")
	public void constructeur_Triangle_par_copie_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfTriangleIsImplemented();
		checkIfConstructorByCopyIsCorrectlyDefined();
		Object triangleInit = getTriangle(argsList.toArray());
		Object triangle = getTriangleByCopy(triangleInit);
		List<Object> values = this.getValuesAsList(triangle);
    	assertEquals(this.argsListToPointList(argsList), values, String.format(
				"""	
						Le triangle créé par copie 
						((x1, y1), (x2, y2), (x3, y3)) = (%s, %s, %s)
						n'est pas celui attendu 
						((x1, y1), (x2, y2), (x3, y3)) = ((%s, %s), (%s, %s), (%s, %s))
    			"""
    			, this.toFormat(
    					this.pointToString(values.get(0)), 
    					this.pointToString(values.get(1)), 
    					this.pointToString(values.get(2)), 
    					argsList)));
	}

	private static final Stream<Arguments> argumentsForDeplacerTest() {
		return Stream.of(
				Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 1.0, 0.0, 3.0, -1.0, 1.0)), 
				Arguments.of(Arrays.asList(2.0, 1.0, 1.0, 1.0, 0.0, 3.0, 2.5, 7.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 1.0, 1.0, 0.0, 3.0, -3.84, 3.24)), 
				Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 1.0, 0.0, 3.0, -100.0, 0.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 1.0, 0.0, 3.0, 1.0001, 2.0002))
				);
	}

	@Order(7)
	@DisplayName("Fonctionnement de la méthode deplacer")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForDeplacerTest")
	public void deplacer_doit_modifier_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfTriangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object triangle = getTriangle(argsList.subList(0,6).toArray());
		
		List<Object> wantedValues = this.getValuesForTranslationAsList(triangle, argsList.get(6), argsList.get(7));
				
		invokeMethod(triangle, "deplacer",
				new Class<?>[] {double.class, double.class},
				argsList.get(6),
				argsList.get(7)
				);
		
		List<Object> newValues = this.getValuesAsList(triangle);
		
		assertEquals(wantedValues, newValues, String.format(
				"""
					Le triangle ((x1, y1), (x2, y2), (x3, y3)) = ((%s, %s), (%s, %s), (%s, %s))
					a été déplacé à (%s, %s, %s) après l'appel deplacer(%s, %s) alors qu'on attendait
					les coordonnées (%s, %s, %s).
				""",
				this.toFormat(argsList.subList(0, 6), 
						this.pointToString(newValues.get(0)), 
    					this.pointToString(newValues.get(1)), 
    					this.pointToString(newValues.get(2)),
    					argsList.get(6), argsList.get(7),
						this.pointToString(wantedValues.get(0)), 
						this.pointToString(wantedValues.get(1)), 
						this.pointToString(wantedValues.get(2)))
				));
		
	}
	
	private static final Stream<Arguments> argumentsForTournerTest() {
		return Stream.of(
				Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 1.0, 0.0, 3.0, 30.0)), 
				Arguments.of(Arrays.asList(2.0, 1.0, 1.0, 1.0, 0.0, 3.0, 60.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 1.0, 1.0, 0.0, 3.0, 180.0)), 
				Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 1.0, 0.0, 3.0, 360.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 1.0, 0.0, 3.0, 180.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 1.0, 0.0, 3.0, -180.0))
				);
	}
	
	@Order(8)
	@DisplayName("Fonctionnement de la méthode tourner")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForTournerTest")
	public void tourner_doit_modifier_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfTriangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object triangle = getTriangle(argsList.subList(0,6).toArray());
		
		List<Object> wantedValues = this.getValuesForRotationAsList(triangle, argsList.get(6));
		System.out.println(wantedValues.get(0));
		invokeMethod(triangle, "tourner",
				new Class<?>[] {double.class},
				argsList.get(6)
				);
		
		List<Object> newValues = this.getValuesAsList(triangle);
		
		assertEquals(wantedValues, newValues, String.format(
				"""
					Le triangle ((x1, y1), (x2, y2), (x3, y3)) = ((%s, %s), (%s, %s), (%s, %s))
					a été tourné à (%s, %s, %s) après l'appel tourner(%s) alors qu'on attendait
					les coordonnées (%s, %s, %s).
				""",
				this.toFormat(argsList.subList(0, 6), 
						this.pointToString(newValues.get(0)), 
						this.pointToString(newValues.get(1)), 
						this.pointToString(newValues.get(2)), 
						argsList.get(6),
						this.pointToString(wantedValues.get(0)), 
						this.pointToString(wantedValues.get(1)), 
						this.pointToString(wantedValues.get(2))
				)));
		
	}
	
	private static final Stream<Arguments> argumentsForIsEquilateralEstTrueTest() {
		return Stream.of(
				Arguments.of(Arrays.asList(0.0, 0.0, 1.0, 0.0, 1.0/2, Math.sqrt(3)/2)),
				Arguments.of(Arrays.asList(-15.52935477163287, 15.733802057519968, -12.311968110533162, 18.43351001820343, -16.258677117833976, 19.86999462017124)), 
				Arguments.of(Arrays.asList(20.0, 35.773502691896255, 10.0, 35.773502691896255, 15.0, 27.11324865405187)),
				Arguments.of(Arrays.asList(52.49999999999999, 498.55662432702593, 54.99999999999999, 502.88675134594814, 50.000000000000014, 502.88675134594814)) 
				);
	}
	
	@Order(9)
	@DisplayName("Fonctionnement de la méthode isEquilateral pour des triangles équilatéraux")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForIsEquilateralEstTrueTest")
	public void isEquilateral_doit_renvoyer_true(List<Double> argsList) {
		checkIfTriangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object triangle = getTriangle(argsList.toArray());

		boolean givenValue = (Boolean) invokeMethodWithReturn(triangle, "isEquilateral",
				new Class<?>[] {},
				new Object[] {},
				boolean.class
				);
		
		assertTrue(givenValue, String.format(
				"""
					Le triangle ((x1, y1), (x2, y2), (x3, y3)) = ((%s, %s), (%s, %s), (%s, %s))
					est équilatéral alors que la fonction isEquilateral renvoie %s
				""",
				this.toFormat(argsList, givenValue)
				));
		
	}
	
	private static final Stream<Arguments> argumentsForIsEquilateralEstFalseTest() {
		return Stream.of(
				Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 1.0, 0.0, 3.0, 30.0)), 
				Arguments.of(Arrays.asList(2.0, 1.0, 1.0, 1.0, 0.0, 3.0, 60.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 1.0, 1.0, 0.0, 3.0, 180.0)), 
				Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 1.0, 0.0, 3.0, 360.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 1.0, 0.0, 3.0, -180.0)),
				Arguments.of(Arrays.asList(0.0, 0.0, 2.0, 0.0, 1.0, 1.0, 180.0))
				);	
		}
	
	@Order(10)
	@DisplayName("Fonctionnement de la méthode isEquilateral pour des triangles non équilatéraux")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForIsEquilateralEstFalseTest")
	public void isEquilateral_doit_renvoyer_false(List<Double> argsList) {
		checkIfTriangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object triangle = getTriangle(argsList.subList(0,6).toArray());

		boolean givenValue = (Boolean) invokeMethodWithReturn(triangle, "isEquilateral",
				new Class<?>[] {},
				new Object[] {},
				boolean.class
				);
		
		assertFalse(givenValue, String.format(
				"""
					Le triangle ((x1, y1), (x2, y2), (x3, y3)) = ((%s, %s), (%s, %s), (%s, %s))
					n'est pas équilatéral alors que la fonction isEquilateral renvoie %s
				""",
				this.toFormat(argsList, givenValue)
				));
		
	}
	
	private List<Object> getValuesAsList(Object triangle) {
		List<Object> values = new ArrayList<Object>();
		
		Object point1 = privateValueGetter(Variables.POINT1.getFieldName(), Variables.POINT1.getType()).apply(triangle);
		Object point2 = privateValueGetter(Variables.POINT2.getFieldName(), Variables.POINT2.getType()).apply(triangle);
		Object point3 = privateValueGetter(Variables.POINT3.getFieldName(), Variables.POINT3.getType()).apply(triangle);
		
		values.add(point1);
		values.add(point2);
		values.add(point3);
		
		return values;
	}
	
	private List<Object> getValuesForTranslationAsList(Object triangle, double x, double y) {
		List<Object> values = new ArrayList<Object>();
		
		Object point1 = privateValueGetter(Variables.POINT1.getFieldName(), Variables.POINT1.getType()).apply(triangle);
		Object point2 = privateValueGetter(Variables.POINT2.getFieldName(), Variables.POINT2.getType()).apply(triangle);
		Object point3 = privateValueGetter(Variables.POINT3.getFieldName(), Variables.POINT3.getType()).apply(triangle);
		
		Object point = this.newPoint(x, y);
		
		values.add(this.addPoint(point1, point));
		values.add(this.addPoint(point2, point));
		values.add(this.addPoint(point3, point));

		return values;
	}
	
	private List<Object> getValuesForRotationAsList(Object triangle, double theta) {
		List<Object> values = new ArrayList<Object>();

		Object point1 = privateValueGetter(Variables.POINT1.getFieldName(), Variables.POINT1.getType()).apply(triangle);
		Object point2 = privateValueGetter(Variables.POINT2.getFieldName(), Variables.POINT2.getType()).apply(triangle);
		Object point3 = privateValueGetter(Variables.POINT3.getFieldName(), Variables.POINT3.getType()).apply(triangle);
		
		Object barycentre = this.scalePoint(
				this.addPoint(point1, this.addPoint(point2, point3)),
				1./3);
		
		values.add(this.rotatePoint(point1, barycentre, theta));
		values.add(this.rotatePoint(point2, barycentre, theta));
		values.add(this.rotatePoint(point3, barycentre, theta));

		return values;
	}

	private Object invokeMethod(Object o, String methodName, Class<?>[] types, Object... args) {
		try {
			Method method = TriangleClass.getDeclaredMethod(methodName, types);
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
				fail(String.format("La méthode '%s' avec les paramètres de types '%s' n'existe pas.", methodName, listToFrenchString(
						Arrays.asList(types).stream().map(c -> c.getSimpleName()).collect(Collectors.toList()))));
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
    private <T> T invokeMethodWithReturn(Object o, String methodName, Class<?>[] types, Object[] args,
            Class<T> returnType) {
        Object objectRes = invokeMethod(o, methodName, types, args);
        try {
            Method method = TriangleClass.getDeclaredMethod(methodName, types);
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
	
	public Object invokeTourner(Object o, Object... args) {
		return invokeMethodWithReturn(o, "tourner", new Class[] {double.class}, args, void.class);
    }
	
	public Object invokeDeplacer(Object o, Object... args) {
		return invokeMethodWithReturn(o, "deplacer", new Class[] {double.class, double.class}, args, void.class);
    }

	public Object invokeGetPoint1(Object o) {
		return invokeMethodWithReturn(o, "getPoint1", new Class[] {}, null, PointClass);
    }
	
	public Object invokeGetPoint2(Object o) {
		return invokeMethodWithReturn(o, "getPoint2", new Class[] {}, null, PointClass);
    }
	
	public Object invokeGetPoint3(Object o) {
		return invokeMethodWithReturn(o, "getPoint3", new Class[] {}, null, PointClass);
    }
	
	public boolean invokeIsEquilateral(Object o) {
		return invokeMethodWithReturn(o, "isEquilateral", new Class[]{}, new Object[]{}, boolean.class);
    }
	
	public Object getEmptyTriangle() {
		return assertDoesNotThrow(() -> TriangleClass.getConstructor().newInstance(),
				"La création du triangle par défaut a levé une exception alors qu'elle n'aurait pas dû.");
	}
	
	public Object getTriangle(Object[] args) {
		Object point1 = newPoint(args[0], args[1]);
		Object point2 = newPoint(args[2], args[3]);
		Object point3 = newPoint(args[4], args[5]);
		Class<?>[] constructorTypes = Variables.valuesList().stream().map(v -> v.getType()).toArray(Class<?>[]::new);
		return assertDoesNotThrow(() -> TriangleClass.getConstructor(constructorTypes).newInstance(point1, point2, point3), String.format(
				"La création du triangle ((x1, y1), (x2, y2), (x3, y3)) = (%s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
				args));
	}
	
	public Object getTriangleByCopy(Object triangleInit) {
		Object point1 = privateValueGetter(Variables.POINT1.getFieldName(), Variables.POINT1.getType())
				.apply(triangleInit);
		Object point2 = privateValueGetter(Variables.POINT2.getFieldName(), Variables.POINT2.getType())
				.apply(triangleInit);
		Object point3 = privateValueGetter(Variables.POINT3.getFieldName(), Variables.POINT3.getType())
				.apply(triangleInit);

		return assertDoesNotThrow(() -> TriangleClass.getConstructor(TriangleClass).newInstance(triangleInit), String.format(
				"La copie du triangle ((x1, y1), (x2, y2), (x3, y3)) = (%s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
				this.toFormat(
						this.pointToString(point1), 
						this.pointToString(point2), 
						this.pointToString(point3)
						)));
	}

	private void checkIfConstructorIsCorrectlyDefined() {
		List<String> constructors = getConstructors(TriangleClass);
		String expectedTypes = Variables.valuesList().stream().map(v -> v.getType().getSimpleName())
				.collect(Collectors.joining(", "));
		if (!constructors.contains(String.format("TriangleAvecPoint(%s)", expectedTypes))) {
			if (constructors.isEmpty()) {
				fail("Le constructeur de TriangleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur TriangleAvecPoint(%s), et ceux présents sont : %s.", expectedTypes,
					listToFrenchString(constructors)));
		}
	}
	
	private void checkIfEmptyConstructorIsCorrectlyDefined() {
		List<String> constructors = getConstructors(TriangleClass);
		if (!constructors.contains("TriangleAvecPoint()")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur vide de TriangleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur TriangleAvecPoint(), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}
	
	private void checkIfConstructorByCopyIsCorrectlyDefined() {
		List<String> constructors = getConstructors(TriangleClass);
		if (!constructors.contains("TriangleAvecPoint(TriangleAvecPoint)")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur par copie de TriangleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur TriangleAvecPoint(TriangleAvecPoint), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}



	private void checkIfTriangleIsImplemented() {
		if (!isTriangleImplemented) {
			fail("La classe TriangleAvecPoint.java n'est pas implémentée.");
		}
	}

	@SuppressWarnings("unchecked")
	private <V> Function<Object, V> privateValueGetter(String fieldName, Class<V> returnType) {
		try {
			Field field = TriangleClass.getDeclaredField(fieldName);
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


	private List<String> getConstructors(Class<?> classe) {
		return Arrays.asList(classe.getConstructors()).stream()
				.map(c -> Arrays.asList(c.getParameters()).stream().map(p -> p.getType().getSimpleName())
						.collect(Collectors.toList()))
				.map(list -> "TriangleAvecPoint(" + (list.size() == 0 ? "" : list.toString().split("[\\[\\]]")[1]) + ")")
				.collect(Collectors.toList());
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
	
	private Object scalePoint(Object p1, double scale) {
		try {
			Field fx = PointClass.getDeclaredField("x");
			fx.setAccessible(true);
			Double x1 = (Double) fx.get(p1);
			Field fy = PointClass.getDeclaredField("y");
			fy.setAccessible(true);
			Double y1 = (Double) fy.get(p1);
			return newPoint(x1*scale, y1*scale);
		} catch (Exception e) {
			return null;
		}
	}
	
	private Object rotatePoint(Object point, Object barycentre, double theta) {
		try {
			Field fx = PointClass.getDeclaredField("x");
			fx.setAccessible(true);
			Double x1 = (Double) fx.get(point);
			Double xb = (Double) fx.get(barycentre);
			Field fy = PointClass.getDeclaredField("y");
			fy.setAccessible(true);
			Double y1 = (Double) fy.get(point);
			Double yb = (Double) fy.get(barycentre);
			
			Double x = Math.cos(theta*Math.PI/180)*(x1-xb) - Math.sin(theta*Math.PI/180)*(y1-yb)+xb;
			Double y = Math.sin(theta*Math.PI/180)*(x1-xb) + Math.cos(theta*Math.PI/180)*(y1-yb)+yb;
			return newPoint(Math.round(x*100)/100.0, Math.round(y*100)/100.0);
		} catch (Exception e) {
			return null;
		}
	}
	

	
	private List<Object> argsListToPointList(List<Double> argsList) {
		List<Object> values = new ArrayList<Object>();
		if (argsList.size() == 6 ) {
			values.add(this.newPoint(argsList.get(0), argsList.get(1)));
			values.add(this.newPoint(argsList.get(2), argsList.get(3)));
			values.add(this.newPoint(argsList.get(4), argsList.get(5)));
		}
		return values;
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
	
	private String listToFrenchString(List<String> liste) {
		int n = liste.size();
		if (n == 0)
			return "";
		if (n == 1)
			return liste.get(0);
		return String.join(", ", liste.subList(0, n - 1)) + " et " + liste.get(n - 1);
	}

}