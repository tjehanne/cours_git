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

@DisplayName("Tests associés au Cercle avec Point")
@TestMethodOrder(OrderAnnotation.class)
public class TesteurCercleAvecPoint {

	private static enum Variables {

		POINT("centre"), RAYON("rayon", double.class);

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

	private Class<?> CercleClass;
	private Class<?> PointClass;
	private boolean isCercleImplemented;

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
			CercleClass = Class.forName("org.oxyl.CercleAvecPoint");
			isCercleImplemented = true;
		} catch (Exception e) {
			try {
				CercleClass = Class.forName("CercleAvecPoint");
				isCercleImplemented = true;
			} catch (Exception e2) {
				isCercleImplemented = false;
			}
		}
	}

	@Test
	@Order(1)
	@DisplayName("Présence de la classe CercleAvecPoint.java")
	public void classe_Cercle_doit_exister() {
		checkIfCercleIsImplemented();
	}

	private static final Stream<Arguments> argumentsForInstanceVariables() {
		return Variables.valuesList().stream().map(v -> Arguments.of(v.getFieldName()));
	}

	@Order(2)
	@DisplayName("Présence des variables d'instance")
	@ParameterizedTest(name = "Variable : {0}")
	@MethodSource("argumentsForInstanceVariables")
	public void Cercle_doit_avoir_les_variables_dInstance(String fieldName) {
		checkIfCercleIsImplemented();
		privateValueGetter(fieldName, Variables.findByFieldName(fieldName).getType());
	}

	@Test
	@Order(3)
	@DisplayName("Présence du constructeur principal")
	public void Cercle_doit_implementer_le_constructeur_principal() {
		checkIfCercleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
	}

	private static final Stream<Arguments> argumentsForConstructorTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0, 1.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 1.0)), Arguments.of(Arrays.asList(1.0, 4.2, 5.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5)));
	}

	@Order(4)
	@DisplayName("Fonctionnement du constructeur principal")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorTest")
	public void constructeur_principal_Cercle_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfCercleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object cercle = getCercle(argsList.toArray());
		Object point = newPoint(argsList.get(0), argsList.get(1));
		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
		Object valueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType()).apply(cercle);
		List<Object> expected = Arrays.asList(point, argsList.get(2));
		assertEquals(expected, Arrays.asList(valuePoint, valueRayon),
				String.format(
						"Le cercle créé (centre, rayon)=(%s, %s) n'est pas celui attendu : (centre, rayon)=(%s, %s).",
						toFormat(pointToString(valuePoint), valueRayon, pointToString(point), argsList.get(2))));
	}

	@Test
	@Order(5)
	@DisplayName("Présence et fonctionnement du constructeur vide")
	public void constructeur_vide_Cercle_doit_modifier_les_variables_dInstance() {
		checkIfCercleIsImplemented();
		checkIfConstructorVideIsCorrectlyDefined();
		Object cercle = getCercleByVide();
		Object point = newPoint(0.0, 0.0);
		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
		Object valueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType()).apply(cercle);
		List<Object> expected = Arrays.asList(point, 1.0);
		assertEquals(expected, Arrays.asList(valuePoint, valueRayon), String.format(
				"Le cercle par défaut (centre, rayon)=(%s, %s) obtenu n'est pas celui attendu : (centre, rayon)=(%s, %s).",
				toFormat(pointToString(valuePoint), valueRayon, pointToString(point), 1.0)));
	}

	private static final Stream<Arguments> argumentsForConstructorByCopieTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0, 1.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 1.0)), Arguments.of(Arrays.asList(1.0, 4.2, 5.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5)));
	}

	@Order(6)
	@DisplayName("Présence et fonctionnement du constructeur par copie")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorByCopieTest")
	public void constructeur_par_copie_Cercle_doit_modifier_les_variables_dInstance(List<Object> argsList) {
		checkIfCercleIsImplemented();
		checkIfConstructorParCopieIsCorrectlyDefined();
		Object[] args = argsList.toArray();
		Object cercleInit = getCercle(args);
		Object point = newPoint(argsList.get(0), argsList.get(1));
		Object cercle = getCercleByCopie(cercleInit);
		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
		Object valueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType()).apply(cercle);
		List<Object> expected = Arrays.asList(point, argsList.get(2));
		assertEquals(expected, Arrays.asList(valuePoint, valueRayon), String.format(
				"Le cercle créé par copie obtenu (centre, rayon)=(%s, %s) n'est pas celui attendu : (x, y, rayon)=(%s, %s).",
				toFormat(pointToString(valuePoint), valueRayon, pointToString(point), argsList.get(2))));
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
		checkIfCercleIsImplemented();
		Object cercle = getCercle(new Object[] { 1.0, 1.5, 3.2 });
		Object oldValuePoint = newPoint(1.0, 1.5);
		Object oldValueRayon = 3.2;
		Object pointToAdd = newPoint(argsList.get(0), argsList.get(1));
		invokeMethod(cercle, "deplacer", new Class<?>[] { double.class, double.class }, argsList.toArray());
		Object newValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType())
				.apply(cercle);
		Object newValueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		// TODO: Si cercle pas déplacé, meme spiderman avec le cercle
		assertEquals(Arrays.asList(addPoint(oldValuePoint, pointToAdd), oldValueRayon),
				Arrays.asList(newValuePoint, newValueRayon),
				String.format(
						"Le cercle (centre, rayon)=((%s, %s), %s) a été déplacé à (centre, rayon)=(%s, %s) après l'appel deplacer(%s, %s).",
						toFormat(1.0, 1.5, 3.2, pointToString(newValuePoint), newValueRayon, argsList)));
	}

	private static final Stream<Arguments> argumentsForIsGrandTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 110.0), true),
				Arguments.of(Arrays.asList(2.0, 1.0, 100.0), false), Arguments.of(Arrays.asList(2.0, 1.0, 90.0), false),
				Arguments.of(Arrays.asList(2.0, 1.0, 99.99), false), Arguments.of(Arrays.asList(2.0, 1.0, 100.01), true));
	}

	@Order(8)
	@DisplayName("Fonctionnement de la méthode isGrand")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForIsGrandTest")
	public void isGrand_doit_retourner_le_bon_boolean(List<Double> argsList, boolean expectedResult) {
		checkIfCercleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object cercle = getCercle(argsList.toArray());
		boolean result = invokeMethodAndReturn(cercle, "isGrand", null, null, boolean.class);
		assertEquals(expectedResult, result, String.format("Le cercle (x, y, rayon)=(%s, %s, %s) %s être grand.",
				toFormat(argsList, expectedResult ? "devrait" : "ne devrait pas")));
	}

	private static final Stream<Arguments> argumentsForRedimensionnerTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 11.0), 2.0),
				Arguments.of(Arrays.asList(2.0, 1.0, 10.0), 0), Arguments.of(Arrays.asList(2.0, 1.0, 9.0), 1.5));
	}

	@Order(9)
	@DisplayName("Fonctionnement de la méthode redimensionner")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForRedimensionnerTest")
	public void redimensionner_doit_actualiser_le_cercle_correctement(List<Double> argsList, double f) {
		checkIfCercleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object cercle = getCercle(argsList.toArray());
		Object oldValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
		Double oldValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		invokeMethod(cercle, "redimensionner", new Class<?>[] { double.class }, new Object[] { f });
		Object newValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
		Double newValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		assertEquals(Arrays.asList(oldValuePoint, oldValueRayon * f),
				Arrays.asList(newValuePoint, newValueRayon),
				String.format(
						"Après un redimensionnement par %s, le cercle (centre, rayon)=(%s, %s) est devenu (centre, rayon)=(%s, %s).",
						toFormat(f, pointToString(oldValuePoint), argsList.get(2), pointToString(newValuePoint), newValueRayon)));
	}

	@Test
	@Order(10)
	@DisplayName("Fonctionnement de la méthode tourner")
	public void tourner_ne_doit_rien_faire() {
		checkIfCercleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object cercle = getCercle(new Object[] { 2.0, 1.5, 4.6 });
		Object oldValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
		Double oldValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		invokeMethod(cercle, "tourner", new Class<?>[] { double.class }, new Object[] { 85.0 });
		Object newValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
		Double newValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		assertEquals(Arrays.asList(oldValuePoint, oldValueRayon),
				Arrays.asList(newValuePoint, newValueRayon),
				"A quoi correspond une rotation d'un cercle autour de son centre ?");
	}

	private static final Stream<Arguments> argumentsForBonusTest() {
		return Stream.of(
				Arguments.of("Constructeur intelligent", Arrays.asList(2.0, 0.0, -1.0), null, null, null,
						Arrays.asList(2.0, 0.0, 0.0), "Un rayon négatif doit être ramené à un rayon de 0."),
				Arguments.of("Redimensionnement intelligent", Arrays.asList(2.0, 1.0, 5.0), "redimensionner",
						new Class<?>[] { double.class }, new Object[] { -1.0 }, Arrays.asList(2.0, 1.0, 0.0),
						"Un redimensionnement avec un facteur négatif doit créer un cercle de rayon nul."));
	}

	@Order(12)
	@DisplayName("[BONUS]")
	@ParameterizedTest(name = "{0}")
	@MethodSource("argumentsForBonusTest")
	public void test_sur_les_bonus(String testName, List<Double> initialCercle, String methodName, Class<?>[] types,
			Object[] args, List<Double> expectedCercleInfo, String message) {
		List<Object> expectedCercle = Arrays.asList(newPoint(expectedCercleInfo.get(0), expectedCercleInfo.get(1)), expectedCercleInfo.get(2));
		checkIfCercleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object cercle = getCercle(initialCercle.toArray());
		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
		Double valueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		if (methodName == null) {
			assertEquals(expectedCercle, Arrays.asList(valuePoint, valueRayon), message);
		} else {
			invokeMethod(cercle, methodName, types, args);
			Object newValuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType()).apply(cercle);
			Double newValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(),
					Variables.RAYON.getType()).apply(cercle);
			assertEquals(expectedCercle, Arrays.asList(newValuePoint, newValueRayon), message);
		}
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
	private <T> T invokeMethodAndReturn(Object o, String methodName, Class<?>[] types, Object[] args,
			Class<T> returnType) {
		Object objectRes = invokeMethod(o, methodName, types, args);
		try {
			Method method = CercleClass.getDeclaredMethod(methodName, types);
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
			Method method = CercleClass.getDeclaredMethod(methodName, types);
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
				fail(String.format("La méthode '%s' avec les paramètres de types '%s' n'existe pas.", methodName,
						listToFrenchString(Arrays.asList(types).stream().map(c -> c.getSimpleName())
								.collect(Collectors.toList()))));
			}
		}
		return null;
	}
	
	public Object invokeRedimensionner(Object o, Object... args) {
		return invokeMethodAndReturn(o, "redimensionner", new Class[] {double.class}, args, void.class);
    }
	
	public Object invokeDeplacer(Object o, Object... args) {
		return invokeMethodAndReturn(o, "deplacer", new Class[] {double.class, double.class}, args, void.class);
    }
	
	public Object invokeGetCentre(Object o) {
		return invokeMethodAndReturn(o, "getCentre", new Class[] {}, null, PointClass);
    }
	
	public double invokeGetRayon(Object o) {
		return invokeMethodAndReturn(o, "getRayon", new Class[] {}, null, double.class);
    }
	
	public boolean invokeIsGrand(Object o) {
		return invokeMethodAndReturn(o, "isGrand", new Class[]{}, new Object[]{}, boolean.class);
    }


	public Object getCercle(Object[] args) {
		try {
			Object point = newPoint(args[0], args[1]);
			Class<?>[] constructorTypes = Variables.valuesList().stream().map(v -> v.getType())
					.toArray(Class<?>[]::new);
			return assertDoesNotThrow(() -> CercleClass.getConstructor(constructorTypes).newInstance(point, args[2]),
					String.format(
							"La création du cercle (x, y, rayon)=(%s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
							args));
		} catch (Exception e) {
			// can never happen
			return null;
		}
	}

	public Object getCercleByCopie(Object cercleInit) {
		Object valuePoint = privateValueGetter(Variables.POINT.getFieldName(), Variables.POINT.getType())
				.apply(cercleInit);
		Object valueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercleInit);
		return assertDoesNotThrow(() -> CercleClass.getConstructor(CercleClass).newInstance(cercleInit), String.format(
				"La copie du cercle (centre, rayon)=(%s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
				pointToString(valuePoint), valueRayon));
	}

	public Object getCercleByVide() {
		return assertDoesNotThrow(() -> CercleClass.getConstructor().newInstance(),
				"La création du cercle par défaut a levé une exception alors qu'elle n'aurait pas dû.");
	}

	private void checkIfConstructorIsCorrectlyDefined() {
		List<String> constructors = getConstructors(CercleClass);
		String expectedTypes = Variables.valuesList().stream().map(v -> v.getType().getSimpleName())
				.collect(Collectors.joining(", "));
		if (!constructors.contains(String.format("CercleAvecPoint(%s)", expectedTypes))) {
			if (constructors.isEmpty()) {
				fail("Le constructeur de CercleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur CercleAvecPoint(%s), et ceux présents sont : %s.", expectedTypes,
					listToFrenchString(constructors)));
		}
	}

	private void checkIfConstructorVideIsCorrectlyDefined() {
		List<String> constructors = getConstructors(CercleClass);
		if (!constructors.contains("CercleAvecPoint()")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur vide de CercleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur CercleAvecPoint(), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}

	private void checkIfConstructorParCopieIsCorrectlyDefined() {
		List<String> constructors = getConstructors(CercleClass);
		if (!constructors.contains("CercleAvecPoint(CercleAvecPoint)")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur par copie de CercleAvecPoint doit être public.");
			}
			fail(String.format("On attend un constructeur CercleAvecPoint(CercleAvecPoint), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}

	private void checkIfCercleIsImplemented() {
		if (!isCercleImplemented) {
			fail("La classe CercleAvecPoint.java n'est pas implémentée.");
		}
	}

	@SuppressWarnings("unchecked")
	private <V> Function<Object, V> privateValueGetter(String fieldName, Class<V> returnType) {
		try {
			Field field = CercleClass.getDeclaredField(fieldName);
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

	// Ex : [Cercle(int, int), Cercle(String)]
	private List<String> getConstructors(Class<?> classe) {
		return Arrays.asList(classe.getConstructors()).stream()
				.map(c -> Arrays.asList(c.getParameters()).stream().map(p -> p.getType().getSimpleName())
						.collect(Collectors.toList()))
				.map(list -> "CercleAvecPoint(" + (list.size() == 0 ? "" : list.toString().split("[\\[\\]]")[1]) + ")")
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