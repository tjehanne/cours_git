package org.oxyl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


public class TesteurPoint {

	private static enum Variables {

		X("x", double.class), Y("y", double.class);

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

	private Class<?> PointClass;
	private boolean isPointImplemented;

	{
		try {
			PointClass = Class.forName("org.oxyl.Point");
			isPointImplemented = true;
		} catch (Exception e) {
			try {
				PointClass = Class.forName("Point");
				isPointImplemented = true;
			} catch (Exception e2) {
				isPointImplemented = false;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void testBeforeMocking() {
		TesteurPoint t = new TesteurPoint();
		try {
			t.classe_Point_doit_exister();
			argumentsForInstanceVariables()
					.forEach(a -> t.Point_doit_avoir_les_variables_dInstances((String) a.get()[0]));
			t.Point_doit_implementer_trois_constructeurs();
			argumentsForConstructorTest().forEach(a -> t
					.constructeur_Point_doit_modifier_les_variables_dInstance(((List<Double>) (a.get()[0])).toArray()));
			t.constructeur_vide_Point_doit_modifier_les_variables_dInstance();
			argumentsForConstructorCopyTest()
					.forEach(a -> t.constructeur_par_copie_Point_doit_modifier_les_variables_dInstance(
							((List<Double>) (a.get()[0])).toArray()));
			argumentsForGetXTest().forEach(a -> t
					.getX_doit_renvoyer_correctement_les_variables_dInstance(((List<Double>) (a.get()[0])).toArray()));
			argumentsForGetYTest().forEach(a -> t
					.getY_doit_renvoyer_correctement_les_variables_dInstance(((List<Double>) (a.get()[0])).toArray()));
			argumentsForSetXTest().forEach(a -> t
					.setX_doit_modifier_correctement_les_variables_dInstance(((List<Double>) (a.get()[0])).toArray()));
			argumentsForSetYTest().forEach(a -> t
					.setY_doit_modifier_correctement_les_variables_dInstance(((List<Double>) (a.get()[0])).toArray()));
			argumentsForEqualsTest()
					.forEach(a -> t.equals_doit_correctement_évaluer_deux_points(a.get()[0], a.get()[1], a.get()[2]));
			argumentsForCalculerDistanceTest()
					.forEach(a -> t.calculerDistance_doit_renvoyer_la_bonne_distances(a.get()[0], a.get()[1]));
		} catch (Error e) {
			fail("La classe Point n'existe pas ou n'est pas terminée");
		}
	}

	@Test
	@Order(1)
	@DisplayName("Présence de la classe Point.java")
	public void classe_Point_doit_exister() {
		checkIfPointIsImplemented();
	}


	private static final Stream<Arguments> argumentsForInstanceVariables() {
		return Variables.valuesList().stream().map(v -> Arguments.of(v.getFieldName()));
	}

	@Order(2)
	@DisplayName("Présence des variables d'instance")
	@ParameterizedTest(name = "Variable : {0}")
	@MethodSource("argumentsForInstanceVariables")
	public void Point_doit_avoir_les_variables_dInstance(String fieldName) {
		checkIfPointIsImplemented();
		privateValueGetter(fieldName, Variables.findByFieldName(fieldName).getType());
	}

	public void Point_doit_avoir_les_variables_dInstances(String fieldName) {
		checkIfPointIsImplemented();
		privateValueGetter(fieldName, Variables.findByFieldName(fieldName).getType());
	}

	@Test
	@Order(3)
	@DisplayName("Présence du constructeur")
	public void Point_doit_implementer_trois_constructeurs() {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
	}

	private static final Stream<Arguments> argumentsForConstructorTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0)),
				Arguments.of(Arrays.asList(5.0, -1.0)), Arguments.of(Arrays.asList(1.0, 4.2)),
				Arguments.of(Arrays.asList(-1.0, -4.2)));
	}

	@Order(4)
	@DisplayName("Fonctionnement du constructeur non vide")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorTest")
	public void constructeur_Point_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
		Object[] args = argsList.toArray();
		Object point = getPoint(args);
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		assertEquals(argsList, Arrays.asList(valueX, valueY),
				String.format("Le point créé (x, y)=(%s, %s) n'est pas celui attendu : (x, y)=(%s, %s).", valueX,
						valueY, args[0], args[1]));
	}

	public void constructeur_Point_doit_modifier_les_variables_dInstance(Object... args) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object point = getPoint(args);
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		assertEquals(Arrays.asList(args), Arrays.asList(valueX, valueY),
				String.format("Le point créé (x, y)=(%s, %s) n'est pas celui attendu : (x, y)=(%s, %s).", valueX,
						valueY, args[0], args[1]));
	}

	@Test
	@Order(5)
	@DisplayName("Fonctionnement du constructeur vide")
	public void constructeur_vide_Point_doit_modifier_les_variables_dInstance() {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
		Object point = getPointVide();
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		List<Object> expectedValues = Arrays.asList(0.0, 0.0);

		assertEquals(expectedValues, Arrays.asList(valueX, valueY),
				String.format("Le point créé (x, y)=(%s, %s) n'est pas celui attendu : (x, y)=(%s, %s).", valueX,
						valueY, expectedValues.get(0), expectedValues.get(1)));
	}

	private static final Stream<Arguments> argumentsForConstructorCopyTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0)),
				Arguments.of(Arrays.asList(5.0, -1.0)), Arguments.of(Arrays.asList(1.0, 4.2)),
				Arguments.of(Arrays.asList(-1.0, -4.2)));
	}

	@Order(6)
	@DisplayName("Fonctionnement du constructeur par copie")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorCopyTest")
	public void constructeur_par_copie_Point_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object[] args = argsList.toArray();
		Object pointACopier = getPoint(args);
		Object point = getPointCopie(pointACopier);
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		assertEquals(argsList, Arrays.asList(valueX, valueY),
				String.format("Le point créé (x, y)=(%s, %s) n'est pas celui attendu : (x, y)=(%s, %s).", valueX,
						valueY, args[0], args[1]));
	}

	public void constructeur_par_copie_Point_doit_modifier_les_variables_dInstance(Object... args) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object pointACopier = getPoint(args);
		List<Object> argsList = Arrays.asList(args);
		Object point = getPointCopie(pointACopier);
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		assertEquals(argsList, Arrays.asList(valueX, valueY),
				String.format("Le point créé (x, y)=(%s, %s) n'est pas celui attendu : (x, y)=(%s, %s).", valueX,
						valueY, args[0], args[1]));
	}

	private static final Stream<Arguments> argumentsForGetXTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0)),
				Arguments.of(Arrays.asList(5.0, -1.0)), Arguments.of(Arrays.asList(1.0, 4.2)),
				Arguments.of(Arrays.asList(-1.0, -4.2)));
	}

	@Order(7)
	@DisplayName("Fonctionnement de la méthode getX")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForGetXTest")
	public void getX_doit_renvoyer_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object[] args = argsList.toArray();
		Object point = getPoint(args);

		Object x = invokeMethodWithReturn(point, "getX", new Class[] {}, new Object[] {}, double.class);

		assertEquals(args[0], x, String.format("L'abscisse du point (x, y)=(%s, %s) n'est pas %s mais %s", args[0],
				args[1], x, args[0]));
	}

	public void getX_doit_renvoyer_correctement_les_variables_dInstance(Object... args) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object point = getPoint(args);

		Object x = invokeMethodWithReturn(point, "getX", new Class[] {}, new Object[] {}, double.class);

		assertEquals(args[0], x, String.format("L'abscisse du point (x, y)=(%s, %s) n'est pas %s mais %s", args[0],
				args[1], x, args[0]));
	}

	private static final Stream<Arguments> argumentsForGetYTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0)),
				Arguments.of(Arrays.asList(5.0, -1.0)), Arguments.of(Arrays.asList(1.0, 4.2)),
				Arguments.of(Arrays.asList(-1.0, -4.2)));
	}

	@Order(8)
	@DisplayName("Fonctionnement de la méthode getY")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForGetYTest")
	public void getY_doit_renvoyer_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object[] args = argsList.toArray();
		Object point = getPoint(args);

		Object y = invokeMethodWithReturn(point, "getY", new Class[] {}, new Object[] {}, double.class);

		assertEquals(args[1], y, String.format("L'ordonnée du point (x, y)=(%s, %s) n'est pas %s mais %s", args[0],
				args[1], y, args[1]));
	}

	public void getY_doit_renvoyer_correctement_les_variables_dInstance(Object... args) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object point = getPoint(args);

		Object y = invokeMethodWithReturn(point, "getY", new Class[] {}, new Object[] {}, double.class);

		assertEquals(args[1], y, String.format("L'ordonnée du point (x, y)=(%s, %s) n'est pas %s mais %s", args[0],
				args[1], y, args[1]));
	}

	private static final Stream<Arguments> argumentsForSetXTest() {
		return Stream.of(Arguments.of(Arrays.asList(0.0)), Arguments.of(Arrays.asList(7.0)),
				Arguments.of(Arrays.asList(-5.5)), Arguments.of(Arrays.asList(2.3)));
	}

	@Order(9)
	@DisplayName("Fonctionnement de la méthode setX")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForSetXTest")
	public void setX_doit_modifier_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		double oldValueX = 1.0;
		double oldValueY = 1.0;
		Object point = getPoint(new Object[] { oldValueX, oldValueY });

		Object[] args = argsList.toArray();
		invokeMethodWithReturn(point, "setX", new Class[] { double.class }, args, void.class);

		double newValueX = (double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		double newValueY = (double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		assertEquals(Arrays.asList(args[0], oldValueY), Arrays.asList(newValueX, newValueY),
				String.format(
						"Le point modifié en abscisse (x, y)=(%s, %s) n'est pas celui attendu :" + " (x, y)=(%s, %s).",
						newValueX, newValueY, args[0], oldValueY));
	}

	public void setX_doit_modifier_correctement_les_variables_dInstance(Object... args) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		double oldValueX = 1.0;
		double oldValueY = 1.0;
		Object point = getPoint(new Object[] { oldValueX, oldValueY });

		invokeMethodWithReturn(point, "setX", new Class[] { double.class }, args, void.class);

		double newValueX = (double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		double newValueY = (double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		assertEquals(Arrays.asList(args[0], oldValueY), Arrays.asList(newValueX, newValueY),
				String.format(
						"Le point modifié en abscisse (x, y)=(%s, %s) n'est pas celui attendu :" + " (x, y)=(%s, %s).",
						newValueX, newValueY, args[0], oldValueY));
	}

	private static final Stream<Arguments> argumentsForSetYTest() {
		return Stream.of(Arguments.of(Arrays.asList(0.0)), Arguments.of(Arrays.asList(7.0)),
				Arguments.of(Arrays.asList(-5.5)), Arguments.of(Arrays.asList(2.3)));
	}

	@Order(10)
	@DisplayName("Fonctionnement de la méthode setY")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForSetYTest")
	public void setY_doit_modifier_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		double oldValueX = 1.0;
		double oldValueY = 1.0;
		Object point = getPoint(new Object[] { oldValueX, oldValueY });

		Object[] args = argsList.toArray();
		invokeMethodWithReturn(point, "setY", new Class[] { double.class }, args, void.class);

		double newValueX = (double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		double newValueY = (double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		assertEquals(Arrays.asList(oldValueX, args[0]), Arrays.asList(newValueX, newValueY),
				String.format("Le point modifié en ordonnée (x, y)=(%s, %s) n'est pas celui attendu : (x, y)=(%s, %s).",
						newValueX, newValueY, oldValueX, args[0]));
	}

	public void setY_doit_modifier_correctement_les_variables_dInstance(Object... args) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		double oldValueX = 1.0;
		double oldValueY = 1.0;
		Object point = getPoint(new Object[] { oldValueX, oldValueY });

		invokeMethodWithReturn(point, "setY", new Class[] { double.class }, args, void.class);

		double newValueX = (double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(point);
		double newValueY = (double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(point);

		assertEquals(Arrays.asList(oldValueX, args[0]), Arrays.asList(newValueX, newValueY),
				String.format("Le point modifié en ordonnée (x, y)=(%s, %s) n'est pas celui attendu : (x, y)=(%s, %s).",
						newValueX, newValueY, oldValueX, args[0]));
	}

	private static final Stream<Arguments> argumentsForEqualsTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0), Arrays.asList(2.0, 0.0), true),
				Arguments.of(Arrays.asList(2.4, 1.1), Arrays.asList(2.4, 1.1), true),
				Arguments.of(Arrays.asList(5.7, -1.0), Arrays.asList(6.0, -1.0), false),
				Arguments.of(Arrays.asList(6.3, -1.0), Arrays.asList(6.0, -1.0), false),
				Arguments.of(Arrays.asList(-1.0, -4.2), Arrays.asList(-4.2, -1.0), false));
	}

	@Order(11)
	@DisplayName("Fonctionnement de la méthode equals")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForEqualsTest")
	public void equals_doit_correctement_évaluer_deux_points(List<Double> args1, List<Double> args2,
			boolean expectedResult) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object point1 = getPoint(args1.toArray());
		Object point2 = getPoint(args2.toArray());
		Object result = invokeMethodWithReturn(point1, "equals", new Class[] { Object.class }, new Object[] { point2 },
				boolean.class);

		assertEquals(expectedResult, result,
				String.format(
						"Le résultat de la méthode equals appliquée aux points (x, y)=(%s, %s) et (x, y)=(%s, %s) "
								+ "n'est pas celui attendu : %s au lieu de %s).",
						args1.get(0), args1.get(1), args2.get(0), args2.get(1), result, expectedResult));
	}

	@SuppressWarnings("unchecked")
	public void equals_doit_correctement_évaluer_deux_points(Object args1, Object args2, Object expectedResult) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		List<Double> argsList1 = (List<Double>) args1;
		List<Double> argsList2 = (List<Double>) args2;

		Object point1 = getPoint(argsList1.toArray());
		Object point2 = getPoint(argsList2.toArray());
		Object result = invokeMethodWithReturn(point1, "equals", new Class[] { Object.class }, new Object[] { point2 },
				boolean.class);

		assertEquals(expectedResult, result,
				String.format(
						"Le résultat de la méthode equals appliquée aux points (x, y)=(%s, %s) et (x, y)=(%s, %s) "
								+ "n'est pas celui attendu : %s au lieu de %s).",
						argsList1.get(0), argsList1.get(1), argsList2.get(0), argsList2.get(1), result,
						expectedResult));
	}

	private static final Stream<Arguments> argumentsForCalculerDistanceTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0), Arrays.asList(2.0, 0.0)),
				Arguments.of(Arrays.asList(2.0, 1.0), Arrays.asList(2.0, 0.0)),
				Arguments.of(Arrays.asList(5.0, -1.0), Arrays.asList(2.0, 6.2)),
				Arguments.of(Arrays.asList(1.0, 4.2), Arrays.asList(-0.5, 3.0)),
				Arguments.of(Arrays.asList(-1.0, -4.2), Arrays.asList(-1.0, 2.0)));
	}

	@Order(12)
	@DisplayName("Fonctionnement de la méthode calculerDistance")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForCalculerDistanceTest")
	public void calculerDistance_doit_renvoyer_la_bonne_distance(List<Double> argsList1, List<Double> argsList2)
			throws ClassNotFoundException {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		Object[] args1 = argsList1.toArray();
		Object[] args2 = argsList2.toArray();

		Object point1 = getPoint(args1);
		Object point2 = getPoint(args2);

		double expectedDistance = Math.sqrt(
				Math.pow(argsList1.get(0) - argsList2.get(0), 2) + Math.pow(argsList1.get(1) - argsList2.get(1), 2));

		Object distance = invokeMethodWithReturn(point1, "calculerDistance", new Class[] { Class.forName("org.oxyl.Point") },
				new Object[] { point2 }, double.class);

		assertEquals(expectedDistance, distance, String.format(
				"Le calcul de distance entre les points (x, y)=(%s, %s) et (x, y)=(%s, %s) a donné %s au lieu de %s",
				args1[0], args1[1], args2[0], args2[1], distance, expectedDistance));
	}

	@SuppressWarnings("unchecked")
	public void calculerDistance_doit_renvoyer_la_bonne_distances(Object argsList1, Object argsList2) {
		checkIfPointIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();

		List<Double> list1 = (List<Double>) argsList1;
		List<Double> list2 = (List<Double>) argsList2;

		Object[] args1 = list1.toArray();
		Object[] args2 = list2.toArray();

		Object point1 = getPoint(args1);
		Object point2 = getPoint(args2);

		double expectedDistance = Math
				.sqrt(Math.pow(list1.get(0) - list2.get(0), 2) + Math.pow(list1.get(1) - list2.get(1), 2));

		Object distance = invokeMethodWithReturn(point1, "calculerDistance", new Class[] { PointClass }, new Object[] { point2 },
				double.class);

		assertEquals(expectedDistance, distance, String.format(
				"Le calcul de distance entre les points (x, y)=(%s, %s) et (x, y)=(%s, %s) a donné %s au lieu de %s",
				list1.get(0), list1.get(1), list2.get(0), list2.get(1), distance, expectedDistance));
	}

	public Object getPoint(Object[] args) {
		Class<?>[] constructorTypes = Variables.valuesList().stream().map(v -> v.getType()).toArray(Class<?>[]::new);
		return assertDoesNotThrow(() -> PointClass.getConstructor(constructorTypes).newInstance(args),
				String.format(
						"La création du point (x, y)=(%s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
						args[0], args[1]));
	}

	public Object getPointVide() {
		return assertDoesNotThrow(() -> PointClass.getConstructor(new Class<?>[] {}).newInstance(),
				"La création du point avec le constructeur vide a levé une exception alors qu'elle n'aurait pas dû.");
	}

	public Object getPointCopie(Object pointACopier) {
		Class<?>[] constructorTypes = new Class[] { PointClass };
		return assertDoesNotThrow(() -> PointClass.getConstructor(constructorTypes).newInstance(pointACopier), String
				.format("La création du point avec le constructeur par copie a levé une exception alors qu'elle n'aurait pas dû."));
	}

	private void checkIfConstructorsAreCorrectlyDefined() {
		List<String> constructors = getConstructors(PointClass);
		if (constructors.isEmpty()) {
			fail("Les constructeurs de Point doivent être publics.");
		}
		String expectedTypes = Variables.valuesList().stream().map(v -> v.getType().getSimpleName())
				.collect(Collectors.joining(", "));

		if (!constructors.contains(String.format("Point(%s)", expectedTypes))) {
			fail(String.format("On attend un constructeur Point(%s), et ceux présents sont : %s. Vérifiez qu'il "
					+ "n'est pas privé.", expectedTypes, listToFrenchString(constructors)));
		}
		if (!constructors.contains("Point()")) {
			fail(String.format("On attend un constructeur Point(), et ceux présents sont : %s. Vérifiez qu'il "
					+ "n'est pas privé.", listToFrenchString(constructors)));
		}
		if (!constructors.contains("Point(Point)")) {
			fail(String.format("On attend un constructeur Point(Point), et ceux présents sont : %s. Vérifiez qu'il "
					+ "n'est pas privé.", listToFrenchString(constructors)));
		}
	}

	private void checkIfPointIsImplemented() {
		if (!isPointImplemented) {
			fail("La classe Point.java n'est pas implémentée.");
		}
	}

	@SuppressWarnings("unchecked")
	private <V> Function<Object, V> privateValueGetter(String fieldName, Class<V> returnType) {
		try {
			Field field = PointClass.getDeclaredField(fieldName);
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

	// Ex : [Point(int, int), Point(String)]
	private List<String> getConstructors(Class<?> classe) {
		return Arrays.asList(classe.getConstructors()).stream()
				.map(c -> Arrays.asList(c.getParameters()).stream().map(p -> p.getType().getSimpleName())
						.collect(Collectors.toList()))
				.map(list -> "Point(" + (list.size() == 0 ? "" : list.toString().split("[\\[\\]]")[1]) + ")")
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

	private Object invokeMethod(Object o, String methodName, Class<?>[] types, Object[] args) {
		try {
			Method method = PointClass.getDeclaredMethod(methodName, types);
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
				fail(String.format("La méthode '%s' avec les types '%s' n'existe pas.", methodName, listToFrenchString(
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
			Method method = PointClass.getDeclaredMethod(methodName, types);
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
	
	public double invokeGetX(Object o) {
		return invokeMethodWithReturn(o, "getX", new Class[] {}, null, double.class);
    }
	
	public double invokeGetY(Object o) {
		return invokeMethodWithReturn(o, "getY", new Class[] {}, null, double.class);
    }
	

}
