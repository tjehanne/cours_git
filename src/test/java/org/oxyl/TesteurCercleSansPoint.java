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

@DisplayName("Tests associés au Cercle")
@TestMethodOrder(OrderAnnotation.class)
public class TesteurCercleSansPoint {

	private static enum Variables {

		X("centreX", double.class), Y("centreY", double.class), RAYON("rayon", double.class);

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

	private Class<?> CercleClass;
	private boolean isCercleImplemented;

	{
		try {
			CercleClass = Class.forName("org.oxyl.Cercle");
			isCercleImplemented = true;
		} catch (Exception e) {
			try {
				CercleClass = Class.forName("Cercle");
				isCercleImplemented = true;
			} catch (Exception e2) {
				isCercleImplemented = false;
			}
		}
	}

	@Test
	@Order(1)
	@DisplayName("Présence de la classe Cercle.java")
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
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Object valueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType()).apply(cercle);
		assertEquals(argsList, Arrays.asList(valueX, valueY, valueRayon), String.format(
				"Le cercle créé (x, y, rayon)=(%s, %s, %s) n'est pas celui attendu : (x, y, rayon)=(%s, %s, %s).",
				toFormat(valueX, valueY, valueRayon, argsList)));
	}

	@Test
	@Order(5)
	@DisplayName("Présence et fonctionnement du constructeur vide")
	public void constructeur_vide_Cercle_doit_modifier_les_variables_dInstance() {
		checkIfCercleIsImplemented();
		checkIfConstructorVideIsCorrectlyDefined();
		Object cercle = getCercleByVide();
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Object valueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType()).apply(cercle);
		List<Object> expected = Arrays.asList(0.0, 0.0, 1.0);
		assertEquals(expected, Arrays.asList(valueX, valueY, valueRayon), String.format(
				"Le cercle par défaut (x, y, rayon)=(%s, %s, %s) obtenu n'est pas celui attendu : (x, y, rayon)=(%s, %s, %s).",
				toFormat(valueX, valueY, valueRayon, expected)));
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
		Object cercle = getCercleByCopie(cercleInit);
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Object valueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType()).apply(cercle);
		assertEquals(argsList, Arrays.asList(valueX, valueY, valueRayon), String.format(
				"Le cercle créé par copie obtenu (x, y, rayon)=(%s, %s, %s) n'est pas celui attendu : (x, y, rayon)=(%s, %s, %s).",
				toFormat(valueX, valueY, valueRayon, argsList)));
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
		Double oldValueX = 1.0;
		Double oldValueY = 1.5;
		Double oldValueRayon = 3.2;
		invokeMethod(cercle, "deplacer", new Class<?>[] { double.class, double.class }, argsList.toArray());
		Double newValueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Double newValueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Double newValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		// TODO: Si cercle pas déplacé, meme spiderman avec le cercle
		assertEquals(Arrays.asList(oldValueX + argsList.get(0), oldValueY + argsList.get(1), oldValueRayon),
				Arrays.asList(newValueX, newValueY, newValueRayon),
				String.format(
						"Le cercle (x, y, rayon)=(%s, %s, %s) a été déplacé à (x, y, rayon)=(%s, %s, %s) après l'appel deplacer(%s, %s).",
						toFormat(1.0, 1.5, 3.2, newValueX, newValueY, newValueRayon, argsList)));
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
		Double oldValueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Double oldValueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Double oldValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		invokeMethod(cercle, "redimensionner", new Class<?>[] { double.class }, new Object[] { f });
		Double newValueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Double newValueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Double newValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		assertEquals(Arrays.asList(oldValueX, oldValueY, oldValueRayon * f),
				Arrays.asList(newValueX, newValueY, newValueRayon),
				String.format(
						"Après un redimensionnement par %s, le cercle (x, y, rayon)=(%s, %s, %s) est devenu (x, y, rayon)=(%s, %s, %s).",
						toFormat(f, argsList, newValueX, newValueY, newValueRayon)));
	}

	@Test
	@Order(10)
	@DisplayName("Fonctionnement de la méthode tourner")
	public void tourner_ne_doit_rien_faire() {
		checkIfCercleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object cercle = getCercle(new Object[] { 2.0, 1.5, 4.6 });
		Double oldValueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Double oldValueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Double oldValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		invokeMethod(cercle, "tourner", new Class<?>[] { double.class }, new Object[] { 85.0 });
		Double newValueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Double newValueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Double newValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		assertEquals(Arrays.asList(oldValueX, oldValueY, oldValueRayon),
				Arrays.asList(newValueX, newValueY, newValueRayon),
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
			Object[] args, List<Double> expectedCercle, String message) {
		checkIfCercleIsImplemented();
		checkIfConstructorIsCorrectlyDefined();
		Object cercle = getCercle(initialCercle.toArray());
		Double valueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercle);
		Double valueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercle);
		Double valueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType())
				.apply(cercle);
		if (methodName == null) {
			assertEquals(expectedCercle, Arrays.asList(valueX, valueY, valueRayon), message);
		} else {
			invokeMethod(cercle, methodName, types, args);
			Double newValueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType())
					.apply(cercle);
			Double newValueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType())
					.apply(cercle);
			Double newValueRayon = (Double) privateValueGetter(Variables.RAYON.getFieldName(),
					Variables.RAYON.getType()).apply(cercle);
			assertEquals(expectedCercle, Arrays.asList(newValueX, newValueY, newValueRayon), message);
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
		return invokeMethod(o, "deplacer", new Class[] {double.class, double.class}, args);
    }
	
	public Object invokeRedimensionner(Object o, Object... args) {
		return invokeMethod(o, "redimensionner", new Class[] {double.class}, args);
    }
	
	public double invokeGetCentreX(Object o) {
		return invokeMethodAndReturn(o, "getCentreX", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetCentreY(Object o) {
		return invokeMethodAndReturn(o, "getCentreY", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetRayon(Object o) {
		return invokeMethodAndReturn(o, "getRayon", new Class[]{}, new Object[]{}, double.class);
    }
	
	public boolean invokeIsGrand(Object o) {
		return invokeMethodAndReturn(o, "isGrand", new Class[]{}, new Object[]{}, boolean.class);
    }

	public Object getCercleByVide() {
		return assertDoesNotThrow(() -> CercleClass.getConstructor().newInstance(),
				"La création du cercle par défaut a levé une exception alors qu'elle n'aurait pas dû.");
	}

	public Object getCercle(Object[] args) {
		Class<?>[] constructorTypes = Variables.valuesList().stream().map(v -> v.getType()).toArray(Class<?>[]::new);
		return assertDoesNotThrow(() -> CercleClass.getConstructor(constructorTypes).newInstance(args), String.format(
				"La création du cercle a levé une exception alors qu'elle n'aurait pas dû.",
				args));
	}
	
	public Object getCercleByCopie(Object cercleInit) {
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(cercleInit);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(cercleInit);
		Object valueRayon = privateValueGetter(Variables.RAYON.getFieldName(), Variables.RAYON.getType()).apply(cercleInit);
		return assertDoesNotThrow(() -> CercleClass.getConstructor(CercleClass).newInstance(cercleInit), String.format(
				"La copie du cercle (x, y, rayon)=(%s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
				valueX, valueY, valueRayon));
	}
	
	private void checkIfConstructorIsCorrectlyDefined() {
		List<String> constructors = getConstructors(CercleClass);
		String expectedTypes = Variables.valuesList().stream().map(v -> v.getType().getSimpleName())
				.collect(Collectors.joining(", "));
		if (!constructors.contains(String.format("Cercle(%s)", expectedTypes))) {
			if (constructors.isEmpty()) {
				fail("Le constructeur principal de Cercle doit être public.");
			}
			fail(String.format("On attend un constructeur Cercle(%s), et ceux présents sont : %s.", expectedTypes,
					listToFrenchString(constructors)));
		}
	}
	
	private void checkIfConstructorVideIsCorrectlyDefined() {
		List<String> constructors = getConstructors(CercleClass);
		if (!constructors.contains("Cercle()")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur vide de Cercle doit être public.");
			}
			fail(String.format("On attend un constructeur Cercle(), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}
	
	private void checkIfConstructorParCopieIsCorrectlyDefined() {
		List<String> constructors = getConstructors(CercleClass);
		if (!constructors.contains("Cercle(Cercle)")) {
			if (constructors.isEmpty()) {
				fail("Le constructeur par copie de Cercle doit être public.");
			}
			fail(String.format("On attend un constructeur Cercle(Cercle), et ceux présents sont : %s.",
					listToFrenchString(constructors)));
		}
	}

	private void checkIfCercleIsImplemented() {
		if (!isCercleImplemented) {
			fail("La classe Cercle.java n'est pas implémentée.");
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
				.map(list -> "Cercle(" + (list.size() == 0 ? "" : list.toString().split("[\\[\\]]")[1]) + ")")
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