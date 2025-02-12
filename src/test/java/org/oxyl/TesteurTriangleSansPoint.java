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
public class TesteurTriangleSansPoint {

	private static enum Variables {

		X1("x1", double.class), Y1("y1", double.class),
		X2("x2", double.class), Y2("y2", double.class),
		X3("x3", double.class), Y3("y3", double.class);

		private String fieldName;
		private Class<?> type;

		Variables(String fieldName, Class<?> type) {
			this.fieldName = fieldName;
			this.type = type;
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

		public static List<Variables> valuesList() {
			return Arrays.asList(Variables.values());
		}

	}

	private static final boolean CHECK_PRIVATE_FIELDS = true;

	private Class<?> TriangleClass;
	private boolean isTriangleImplemented;

	{
		try {
			TriangleClass = Class.forName("org.oxyl.Triangle");
			isTriangleImplemented = true;
		} catch (ClassNotFoundException e) {
			try {
				TriangleClass = Class.forName("Triangle");
				isTriangleImplemented = true;
			} catch (Exception e2) {
				isTriangleImplemented = false;
			}
		}
	}

	@Test
	@Order(1)
	@DisplayName("Présence de la classe Triangle.java")
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
		List<Double> values = this.getValuesAsList(triangle);
		List<Double> expected = Arrays.asList(0.0, 0.0, 0.0, 1.0, 0.5, 0.5);
    	assertEquals(expected, values, String.format(
				"""	
					Le triangle par défaut obtenu (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s)
					n'est pas celui attendu: (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s)
    			"""
    			, this.toFormat(values, expected)));
	}

	@Order(5)
	@DisplayName("Fonctionnement du constructeur principal")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorTest")
	public void constructeur_Triangle_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfTriangleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object triangle = getTriangle(argsList.toArray());
		List<Double> values = this.getValuesAsList(triangle);
    	assertEquals(argsList, values, String.format(
				"""	
					Création d'un triangle de coordonnées (%s, %s, %s, %s, %s, %s)
	    			avec l'entrée (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s).
    			"""
    			, this.toFormat(values, argsList)));
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
		List<Double> values = this.getValuesAsList(triangle);
    	assertEquals(argsList, values, String.format(
				"""	
						Le triangle créé par copie (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s)
						n'est pas celui attendu (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s).
    			"""
    			, this.toFormat(values, argsList)));
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
		
		List<Double> wantedValues = this.getValuesForTranslationAsList(triangle, argsList.get(6), argsList.get(7));
				
		invokeMethod(triangle, "deplacer",
				new Class<?>[] {double.class, double.class},
				argsList.get(6),
				argsList.get(7)
				);
		
		List<Double> newValues = this.getValuesAsList(triangle);
		
		assertEquals(wantedValues, newValues, String.format(
				"""
					Le triangle (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s) 
					a été déplacé à (%s, %s, %s, %s, %s, %s) après l'appel deplacer(%s, %s).
				""",
				this.toFormat(argsList.subList(0, 6), newValues, argsList.get(6), argsList.get(7))
				));
		
	}
	
	private static final Stream<Arguments> argumentsForTournerTest() {
		return Stream.of(
				Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 1.0, 0.0, 3.0, 30.0)), 
				Arguments.of(Arrays.asList(2.0, 1.0, 1.0, 1.0, 0.0, 3.0, 60.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 1.0, 1.0, 0.0, 3.0, 180.0)), 
				Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 1.0, 0.0, 3.0, 360.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 1.0, 0.0, 3.0, -180.0)),
				Arguments.of(Arrays.asList(0.0, 0.0, 2.0, 0.0, 1.0, 1.0, 180.0))
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
		
		List<Double> wantedValues = this.getValuesForRotationAsList(triangle, argsList.get(6));
		
		invokeMethod(triangle, "tourner",
				new Class<?>[] {double.class},
				argsList.get(6)
				);
		
		List<Double> newValues = this.getValuesAsList(triangle);
		
		assertEquals(wantedValues, newValues, String.format(
				"""
					Le triangle (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s) 
					a été tourné à (%s, %s, %s, %s, %s, %s) après l'appel tourner(%s).
				""",
				this.toFormat(argsList.subList(0, 6), newValues, argsList.get(6))
				));
		
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
					Le triangle (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s) 
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
					Le triangle (x1, y1, x2, y2, x3, y3) = (%s, %s, %s, %s, %s, %s) 
					n'est pas équilatéral alors que la fonction isEquilateral renvoie %s
				""",
				this.toFormat(argsList, givenValue)
				));
		
	}
	
	private List<Double> getValuesAsList(Object triangle) {
		List<Double> values = new ArrayList<Double>();
		values.add((Double) privateValueGetter(Variables.X1.getFieldName(), Variables.X1.getType()).apply(triangle));
		values.add((Double) privateValueGetter(Variables.Y1.getFieldName(), Variables.Y1.getType()).apply(triangle));
		values.add((Double) privateValueGetter(Variables.X2.getFieldName(), Variables.X2.getType()).apply(triangle));
		values.add((Double) privateValueGetter(Variables.Y2.getFieldName(), Variables.Y2.getType()).apply(triangle));
		values.add((Double) privateValueGetter(Variables.X3.getFieldName(), Variables.X3.getType()).apply(triangle));
		values.add((Double) privateValueGetter(Variables.Y3.getFieldName(), Variables.Y3.getType()).apply(triangle));
		return values;
	}
	
	private List<Double> getValuesForTranslationAsList(Object triangle, double x, double y) {
		List<Double> values = new ArrayList<Double>();
		values.add((Double) privateValueGetter(Variables.X1.getFieldName(), Variables.X1.getType()).apply(triangle)+x);
		values.add((Double) privateValueGetter(Variables.Y1.getFieldName(), Variables.Y1.getType()).apply(triangle)+y);
		values.add((Double) privateValueGetter(Variables.X2.getFieldName(), Variables.X2.getType()).apply(triangle)+x);
		values.add((Double) privateValueGetter(Variables.Y2.getFieldName(), Variables.Y2.getType()).apply(triangle)+y);
		values.add((Double) privateValueGetter(Variables.X3.getFieldName(), Variables.X3.getType()).apply(triangle)+x);
		values.add((Double) privateValueGetter(Variables.Y3.getFieldName(), Variables.Y3.getType()).apply(triangle)+y);
		return values;
	}
	
	private List<Double> getValuesForRotationAsList(Object triangle, double theta) {
		List<Double> values = new ArrayList<Double>();
		double x1 = (Double) privateValueGetter(Variables.X1.getFieldName(), Variables.X1.getType()).apply(triangle);
		double y1 = (Double) privateValueGetter(Variables.Y1.getFieldName(), Variables.Y1.getType()).apply(triangle);
		double x2 = (Double) privateValueGetter(Variables.X2.getFieldName(), Variables.X2.getType()).apply(triangle);
		double y2 = (Double) privateValueGetter(Variables.Y2.getFieldName(), Variables.Y2.getType()).apply(triangle);
		double x3 = (Double) privateValueGetter(Variables.X3.getFieldName(), Variables.X3.getType()).apply(triangle);
		double y3 = (Double) privateValueGetter(Variables.Y3.getFieldName(), Variables.Y3.getType()).apply(triangle);
		
		double barycentreX = (x1+x2+x3)/3;
		double barycentreY = (y1+y2+y3)/3;
		
		values.add(Math.round((Math.cos(theta*Math.PI/180)*(x1-barycentreX) - Math.sin(theta*Math.PI/180)*(y1-barycentreY)+barycentreX)*100)/100.0);
		values.add(Math.round((Math.sin(theta*Math.PI/180)*(x1-barycentreX) + Math.cos(theta*Math.PI/180)*(y1-barycentreY)+barycentreY)*100)/100.0);
		values.add(Math.round((Math.cos(theta*Math.PI/180)*(x2-barycentreX) - Math.sin(theta*Math.PI/180)*(y2-barycentreY)+barycentreX)*100)/100.0);
		values.add(Math.round((Math.sin(theta*Math.PI/180)*(x2-barycentreX) + Math.cos(theta*Math.PI/180)*(y2-barycentreY)+barycentreY)*100)/100.0);
		values.add(Math.round((Math.cos(theta*Math.PI/180)*(x3-barycentreX) - Math.sin(theta*Math.PI/180)*(y3-barycentreY)+barycentreX)*100)/100.0);
		values.add(Math.round((Math.sin(theta*Math.PI/180)*(x3-barycentreX) + Math.cos(theta*Math.PI/180)*(y3-barycentreY)+barycentreY)*100)/100.0);

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
				fail("Les paramètres donnés à la méthode dans les tests ne sont pas du bon type.");
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
	
	public Object invokeDeplacer(Object o, Object... args) {
		return invokeMethod(o, "deplacer", new Class[] {double.class, double.class}, args);
    }
	
	public Object invokeTourner(Object o, Object... args) {
		return invokeMethod(o, "tourner", new Class[] {double.class}, args);
    }
	
	public Object invokeRedimensionner(Object o, Object... args) {
		return invokeMethod(o, "redimensionner", new Class[] {double.class}, args);
    }
	
	public double invokeGetX1(Object o) {
		return invokeMethodWithReturn(o, "getX1", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetY1(Object o) {
		return invokeMethodWithReturn(o, "getY1", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetX2(Object o) {
		return invokeMethodWithReturn(o, "getX2", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetY2(Object o) {
		return invokeMethodWithReturn(o, "getY2", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetX3(Object o) {
		return invokeMethodWithReturn(o, "getX3", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetY3(Object o) {
		return invokeMethodWithReturn(o, "getY3", new Class[]{}, new Object[]{}, double.class);
    }
	
	public boolean invokeIsEquilateral(Object o) {
		return invokeMethodWithReturn(o, "isEquilateral", new Class[]{}, new Object[]{}, boolean.class);
    }


	public Object getEmptyTriangle() {
		return assertDoesNotThrow(() -> TriangleClass.getConstructor().newInstance(),
				"La création du triangle par défaut a levé une exception alors qu'elle n'aurait pas dû.");
	}
	
	public Object getTriangle(Object[] args) {
		Class<?>[] constructorTypes = Variables.valuesList().stream().map(v -> v.getType()).toArray(Class<?>[]::new);
		return assertDoesNotThrow(() -> TriangleClass.getConstructor(constructorTypes).newInstance(args), String.format(
				"La création du triangle (x1, y1, x2, y2, x3, y3)=(%s, %s, %s, %s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
				args));
	}
	
	public Object getTriangleByCopy(Object triangleInit) {
		List<Double> values = this.getValuesAsList(triangleInit);
		return assertDoesNotThrow(() -> TriangleClass.getConstructor(TriangleClass).newInstance(triangleInit), String.format(
				"La copie du triangle (x1, y1, x2, y2, x3, y3)=(%s, %s, %s, %s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
				this.toFormat(values)));
	}

	private void checkIfConstructorIsCorrectlyDefined() {
		List<String> constructors = getConstructors(TriangleClass);
		String expectedTypes = Variables.valuesList().stream().map(v -> v.getType().getSimpleName())
				.collect(Collectors.joining(", "));
		if (!constructors.contains(String.format("Triangle(%s)", expectedTypes))) {
			if (constructors.isEmpty()) {
				fail("Le constructeur de Triangle doit être public.");
			}
			fail(String.format("On attend un constructeur Triangle(%s), et ceux présents sont : %s.", expectedTypes,
					listToFrenchString(constructors)));
		}
	}
	
	private void checkIfEmptyConstructorIsCorrectlyDefined() {
		List<String> constructors = getConstructors(TriangleClass);
		if (!constructors.contains("Triangle()")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur vide de Triangle doit être public.");
			}
			fail(String.format("On attend un constructeur Triangle(), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}
	
	private void checkIfConstructorByCopyIsCorrectlyDefined() {
		List<String> constructors = getConstructors(TriangleClass);
		if (!constructors.contains("Triangle(Triangle)")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur par copie de Triangle doit être public.");
			}
			fail(String.format("On attend un constructeur Triangle(Triangle), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}

	private void checkIfTriangleIsImplemented() {
		if (!isTriangleImplemented) {
			fail("La classe Triangle.java n'est pas implémentée.");
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
				.map(list -> "Triangle(" + (list.size() == 0 ? "" : list.toString().split("[\\[\\]]")[1]) + ")")
				.collect(Collectors.toList());
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