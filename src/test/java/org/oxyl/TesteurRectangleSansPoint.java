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

public class TesteurRectangleSansPoint {

	private static enum Variables {

		X("centreX", double.class), Y("centreY", double.class), LONGUEUR("longueur", double.class), 
			LARGEUR("largeur", double.class), ANGLE("angle", double.class);

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

	private Class<?> RectangleClass;
	private boolean isRectangleImplemented;

	{
		try {
			RectangleClass = Class.forName("org.oxyl.Rectangle");
			isRectangleImplemented = true;
		} catch (Exception e) {
			try {
				RectangleClass = Class.forName("Rectangle");
				isRectangleImplemented = true;
			} catch (Exception e2) {
				isRectangleImplemented = false;
			}
		}
	}

	@Test
	@Order(1)
	@DisplayName("Présence de la classe Rectangle.java")
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
	@DisplayName("Présence du constructeur")
	public void Rectangle_doit_implementer_trois_constructeurs() {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
	}

	private static final Stream<Arguments> argumentsForConstructorTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 0.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0, 3.0, 2.0, 90.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 6.0, 2.0, 35.0)), Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 0.5, 120.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 5.5, 270.0)));
	}

	@Order(4)
	@DisplayName("Fonctionnement du constructeur non vide")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorTest")
	public void constructeur_Rectangle_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
		
		Object[] args = argsList.toArray();
		Object rectangle = getRectangle(args);
		
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(rectangle);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(rectangle);
		Object valueLongueur = privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		Object valueLargeur = privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		Object valueAngle = privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		

		assertEquals(argsList, Arrays.asList(valueX, valueY, valueLongueur, valueLargeur, valueAngle), String.format( 
				"Le rectangle créé (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s) n'est pas celui attendu :"
				+ " (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s).",
				valueX, valueY, valueLongueur, valueLargeur, valueAngle, args[0], args[1], args[2], args[3], args[4]));
	
	}
	
	@Test
	@Order(5)
	@DisplayName("Fonctionnement du constructeur vide")
	public void constructeur_vide_Rectangle_doit_modifier_les_variables_dInstance() {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
		Object rectangle = getRectangleVide();
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(rectangle);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(rectangle);
		Object valueLongueur = privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		Object valueLargeur = privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		Object valueAngle = privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		
		List<Object> expectedValues = Arrays.asList(0.0, 0.0, 1.0, 1.0, 0.0);
		

		assertEquals(expectedValues, Arrays.asList(valueX, valueY, valueLongueur, valueLargeur, valueAngle), String.format( 
				"Le rectangle créé (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s) n'est pas celui attendu :"
				+ " (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s).",
				valueX, valueY, valueLongueur, valueLargeur, valueAngle, expectedValues.get(0), expectedValues.get(1), expectedValues.get(2),
				expectedValues.get(3), expectedValues.get(4)));
	}


	private static final Stream<Arguments> argumentsForConstructorCopyTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 0.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0, 3.0, 2.0, 90.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 6.0, 2.0, 35.0)), Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 0.5, 120.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 5.5, 270.0)));
	}

	@Order(6)
	@DisplayName("Fonctionnement du constructeur par copie")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForConstructorCopyTest")
	public void constructeur_par_copie_Rectangle_doit_modifier_les_variables_dInstance(List<Double> argsList) {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
		
		Object[] args = argsList.toArray();
		Object rectangleACopier = getRectangle(args);
		Object rectangle = getRectangleCopie(rectangleACopier);
		
		Object valueX = privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(rectangle);
		Object valueY = privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(rectangle);
		Object valueLongueur = privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		Object valueLargeur = privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		Object valueAngle = privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		

		assertEquals(argsList, Arrays.asList(valueX, valueY, valueLongueur, valueLargeur, valueAngle), String.format( 
				"Le rectangle créé (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s) n'est pas celui attendu :"
				+ " (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s).",
				valueX, valueY, valueLongueur, valueLargeur, valueAngle, args[0], args[1], args[2], args[3], args[4]));
	}
	

	private static final Stream<Arguments> argumentsForDeplacerTest() {
		return Stream.of(Arguments.of(Arrays.asList(0.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0)),
				Arguments.of(Arrays.asList(-5.0, -1.0)), Arguments.of(Arrays.asList(1.0, 4.2)),
				Arguments.of(Arrays.asList(-1.0, 4.0)));
	}
	
	
	@Order(7)
	@DisplayName("Fonctionnement de la méthode deplacer")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForDeplacerTest")
	public void deplacer_doit_modifier_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
		
		double oldValueX = 1.0;
		double oldValueY = 1.0;
		double oldValueLongueur = 5.0;
		double oldValueLargeur = 3.0;
		double oldValueAngle = 90.0;

		Object rectangle = getRectangle(new Object[] {oldValueX, oldValueY, oldValueLongueur, oldValueLargeur, oldValueAngle});
			
		Object[] args = argsList.toArray();
		callMethodAndReturn(rectangle, "deplacer", new Class[] {double.class, double.class}, args, void.class);
		
		double newValueX = (double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(rectangle);
		double newValueY = (double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(rectangle);
		double newValueLongueur = (double) privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		double newValueLargeur = (double) privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		double newValueAngle = (double) privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		
		assertEquals(Arrays.asList(oldValueX + (double) argsList.get(0), oldValueY + argsList.get(1), oldValueLongueur, oldValueLargeur, oldValueAngle), 
				Arrays.asList(newValueX, newValueY, newValueLongueur, newValueLargeur, newValueAngle), String.format(
				"Le rectangle déplacé (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s) n'est pas celui attendu :"
						+ " (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s).",
						newValueX, newValueY, newValueLongueur, newValueLargeur, newValueAngle,
						oldValueX + (double) argsList.get(0), oldValueY + argsList.get(1), oldValueLongueur, oldValueLargeur, oldValueAngle));
	}
	
	private static final Stream<Arguments> argumentsForIsCarreTest() {
		return Stream.of(Arguments.of(Arrays.asList(2.0, 0.0, 0.0, 0.0, 0.0)), Arguments.of(Arrays.asList(2.0, 1.0, 3.0, 2.0, 90.0)),
				Arguments.of(Arrays.asList(5.0, -1.0, 6.0, 6.0, 35.0)), Arguments.of(Arrays.asList(1.0, 4.2, 5.0, 5.1, 120.0)),
				Arguments.of(Arrays.asList(-1.0, 4.0, 5.5, 5.5, 270.0)));
	}
	
	
	@Order(8)
	@DisplayName("Fonctionnement de la méthode isCarre")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForIsCarreTest")
	public void isCarre_doit_renvoyer_true_si_carre(List<Double> argsList) {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
		
		Object[] args = argsList.toArray();
		Object rectangle = getRectangle(args);
		
		double valueX = (double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(rectangle);
		double valueY = (double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(rectangle);
		double valueLongueur = (double) privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		double valueLargeur = (double) privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		double valueAngle = (double) privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		
		boolean isCarre = callMethodAndReturn(rectangle, "isCarre", null, null, boolean.class);
		
		assertEquals(valueLongueur == valueLargeur, isCarre, String.format(
				"Le rectangle (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s) a été testé avec la méthode isCarre et a eu le résultat %s"
				+ " au lieu de %s", valueX, valueY, valueLongueur, valueLargeur, valueAngle, isCarre, valueX == valueY));
	}
	
	private static final Stream<Arguments> argumentsForRedimensionnerTest() {
		return Stream.of(Arguments.of(Arrays.asList(0.0)), Arguments.of(Arrays.asList(2.0)),
				Arguments.of(Arrays.asList(1.5)), Arguments.of(Arrays.asList(0.3)));
	}
	
	
	@Order(9)
	@DisplayName("Fonctionnement de la méthode redimensionner")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForRedimensionnerTest")
	public void redimensionner_doit_modifier_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
				
		double oldValueX = 1.0;
		double oldValueY = 1.0;
		double oldValueLongueur = 5.0;
		double oldValueLargeur = 3.0;
		double oldValueAngle = 90.0;
		Object rectangle = getRectangle(new Object[] {oldValueX, oldValueY, oldValueLongueur, oldValueLargeur, oldValueAngle});
			
		Object[] args = argsList.toArray();
		callMethodAndReturn(rectangle, "redimensionner", new Class[] {double.class}, args, void.class);
		
		double newValueX = (double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(rectangle);
		double newValueY = (double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(rectangle);
		double newValueLongueur = (double) privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		double newValueLargeur = (double) privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		double newValueAngle = (double) privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		
		assertEquals(Arrays.asList(oldValueX, oldValueY, oldValueLongueur*argsList.get(0), oldValueLargeur*argsList.get(0), oldValueAngle), 
				Arrays.asList(newValueX, newValueY, newValueLongueur, newValueLargeur, newValueAngle), String.format(
						"Le rectangle redimensionné (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s) n'est pas celui attendu :"
						+ " (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s).",
						newValueX, newValueY, newValueLongueur, newValueLargeur, newValueAngle,
						oldValueX, oldValueY, oldValueLongueur*argsList.get(0), oldValueLargeur*argsList.get(0), oldValueAngle));				
	}


	public static final Stream<Arguments> argumentsForTournerTest() {
		return Stream.of(Arguments.of(Arrays.asList(0.0)), Arguments.of(Arrays.asList(90.0)),
				Arguments.of(Arrays.asList(30.5)), Arguments.of(Arrays.asList(270.3)));
	}
	
	
	@Order(10)
	@DisplayName("Fonctionnement de la méthode tourner")
	@ParameterizedTest(name = "Test numéro {index}")
	@MethodSource("argumentsForTournerTest")
	public void tourner_doit_modifier_correctement_les_variables_dInstance(List<Double> argsList) {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
				
		double oldValueX = 1.0;
		double oldValueY = 1.0;
		double oldValueLongueur = 5.0;
		double oldValueLargeur = 3.0;
		double oldValueAngle = 90.0;
		Object rectangle = getRectangle(new Object[] {oldValueX, oldValueY, oldValueLongueur, oldValueLargeur, oldValueAngle});
			
		Object[] args = argsList.toArray();
		callMethodAndReturn(rectangle, "tourner", new Class[] {double.class}, args, void.class);
		
		double newValueX = (double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(rectangle);
		double newValueY = (double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(rectangle);
		double newValueLongueur = (double) privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType()).apply(rectangle);
		double newValueLargeur = (double) privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType()).apply(rectangle);
		double newValueAngle = (double) privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType()).apply(rectangle);
		
		assertEquals(Arrays.asList(oldValueX, oldValueY, oldValueLongueur, oldValueLargeur, oldValueAngle + argsList.get(0)), 
				Arrays.asList(newValueX, newValueY, newValueLongueur, newValueLargeur, newValueAngle), String.format(
						"Le rectangle tourné (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s) n'est pas celui attendu :"
						+ " (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s).",
						newValueX, newValueY, newValueLongueur, newValueLargeur, newValueAngle,
						oldValueX, oldValueY, oldValueLongueur, oldValueLargeur, oldValueAngle + argsList.get(0)));				
	}
	
	private static final Stream<Arguments> argumentsForBonusTest() {
		return Stream.of(
				Arguments.of("Constructeur intelligent - longueur", Arrays.asList(2.0, 1.0, -5.0, 1.0, 60.0), null, null, null,
						null, Arrays.asList(2.0, 1.0, 0.0, 1.0, 60.0), "Une longueur négative doit être ramenée à 0."),
				Arguments.of("Constructeur intelligent - largeur", Arrays.asList(2.0, 1.0, 5.0, -1.0, 60.0), null, null, null,
						null, Arrays.asList(2.0, 1.0, 5.0, 0.0, 60.0), "Une largeur négative doit être ramenée à 0."),
				Arguments.of("Redimensionnement intelligent", Arrays.asList(2.0, 1.0, 1.0, 5.0, 60.0), "redimensionner",
						new Class<?>[] { double.class }, new Object[] { -1.0 }, void.class, Arrays.asList(2.0, 1.0, 0.0, 0.0, 60.0),
						"Un redimensionnement avec un facteur négatif doit mettre à zéro la longueur et la largeur."));
	}

	@Order(11)
	@DisplayName("[BONUS]")
	@ParameterizedTest(name = "{0}")
	@MethodSource("argumentsForBonusTest")
	public <T> void test_sur_les_bonus(String testName, List<Double> rectangleArgs, String methodName, Class<?>[] types,
			Object[] args, Class<T> returnType, List<Double> expectedRectangleArgs, String message) {
		checkIfRectangleIsImplemented();
		checkIfConstructorsAreCorrectlyDefined();
		
		Object rectangle = getRectangle(rectangleArgs.toArray());
		Double valueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType()).apply(rectangle);
		Double valueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType()).apply(rectangle);
		Double valueLongueur = (Double) privateValueGetter(Variables.LONGUEUR.getFieldName(), Variables.LONGUEUR.getType())
				.apply(rectangle);
		Double valueLargeur = (Double) privateValueGetter(Variables.LARGEUR.getFieldName(), Variables.LARGEUR.getType())
				.apply(rectangle);
		Double valueAngle = (Double) privateValueGetter(Variables.ANGLE.getFieldName(), Variables.ANGLE.getType())
				.apply(rectangle);
		
		if (methodName == null) {
			assertEquals(expectedRectangleArgs, Arrays.asList(valueX, valueY, valueLongueur, valueLargeur, valueAngle), message);
		} else {
			callMethodAndReturn(rectangle, methodName, types, args, returnType);
			Double newValueX = (Double) privateValueGetter(Variables.X.getFieldName(), Variables.X.getType())
					.apply(rectangle);
			Double newValueY = (Double) privateValueGetter(Variables.Y.getFieldName(), Variables.Y.getType())
					.apply(rectangle);
			Double newValueLongueur = (Double) privateValueGetter(Variables.LONGUEUR.getFieldName(),
					Variables.LONGUEUR.getType()).apply(rectangle);
			Double newValueLargeur = (Double) privateValueGetter(Variables.LARGEUR.getFieldName(),
					Variables.LARGEUR.getType()).apply(rectangle);
			Double newValueAngle = (Double) privateValueGetter(Variables.ANGLE.getFieldName(),
					Variables.ANGLE.getType()).apply(rectangle);
			assertEquals(expectedRectangleArgs, Arrays.asList(newValueX, newValueY, newValueLongueur, newValueLargeur, newValueAngle), message);
		}
	}


	public Object getRectangle(Object[] args) {
		Class<?>[] constructorTypes = Variables.valuesList().stream().map(v -> v.getType()).toArray(Class<?>[]::new);
		return assertDoesNotThrow(() -> RectangleClass.getConstructor(constructorTypes).newInstance(args), String.format(
				"La création du rectangle (x, y, longueur, largeur, angle)=(%s, %s, %s, %s, %s) a levé une exception alors qu'elle n'aurait pas dû.",
				args));
	}
	
	public Object getRectangleVide() {
		return assertDoesNotThrow(() -> RectangleClass.getConstructor(new Class<?>[] {}).newInstance(),
				"La création du rectangle (x, y, longueur, largeur, angle)=(0,0,0,0,0) a levé une exception alors qu'elle n'aurait pas dû.");
	}
	
	public Object getRectangleCopie(Object r) {
		return assertDoesNotThrow(() -> RectangleClass.getConstructor(new Class<?>[] {RectangleClass}).newInstance(r),
				"La création du rectangle par copie a levé une exception alors qu'elle n'aurait pas dû.");
	}


	private void checkIfConstructorsAreCorrectlyDefined() {
		List<String> constructors = getConstructors(RectangleClass);
		if (constructors.isEmpty()) {
			fail("Les constructeurs de Rectangle doivent être publics.");
		}
		String expectedTypes = Variables.valuesList().stream().map(v -> v.getType().getSimpleName())
				.collect(Collectors.joining(", "));
		
		if (!constructors.contains(String.format("Rectangle(%s)", expectedTypes))) {
			fail(String.format("On attend un constructeur Rectangle(%s), et ceux présents sont : %s. Vérifiez qu'il " 
					+ "n'est pas privé.", expectedTypes, listToFrenchString(constructors)));
		}
		if (!constructors.contains("Rectangle()")) {
			fail(String.format("On attend un constructeur Rectangle(), et ceux présents sont : %s. Vérifiez qu'il "
					+ "n'est pas privé.", listToFrenchString(constructors)));
		}
		if (!constructors.contains("Rectangle(Rectangle)")) {
			fail(String.format("On attend un constructeur Rectangle(Rectangle), et ceux présents sont : %s. Vérifiez qu'il "
					+ "n'est pas privé.", listToFrenchString(constructors)));
		}
	}

	private void checkIfRectangleIsImplemented() {
		if (!isRectangleImplemented) {
			fail("La classe Rectangle.java n'est pas implémentée.");
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

	// Ex : [Rectangle(int, int), Rectangle(String)]
	private List<String> getConstructors(Class<?> classe) {
		return Arrays.asList(classe.getConstructors()).stream()
				.map(c -> Arrays.asList(c.getParameters()).stream().map(p -> p.getType().getSimpleName())
						.collect(Collectors.toList()))
				.map(list -> "Rectangle(" + (list.size() == 0 ? "" : list.toString().split("[\\[\\]]")[1]) + ")")
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
	
	private Object callMethod(Object o, String methodName, Class<?>[] types, Object[] args) {
        try {
            Method method = RectangleClass.getDeclaredMethod(methodName, types);
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
	
	public Object invokeDeplacer(Object o, Object... args) {
		return callMethod(o, "deplacer", new Class[] {double.class, double.class}, args);
    }
	
	public Object invokeTourner(Object o, Object... args) {
		return callMethod(o, "tourner", new Class[] {double.class}, args);
    }
	
	public Object invokeRedimensionner(Object o, Object... args) {
		return callMethod(o, "redimensionner", new Class[] {double.class}, args);
    }
	
	public double invokeGetCentreX(Object o) {
		return callMethodAndReturn(o, "getCentreX", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetCentreY(Object o) {
		return callMethodAndReturn(o, "getCentreY", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetLongueur(Object o) {
		return callMethodAndReturn(o, "getLongueur", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetLargeur(Object o) {
		return callMethodAndReturn(o, "getLargeur", new Class[]{}, new Object[]{}, double.class);
    }
	
	public double invokeGetAngle(Object o) {
		return callMethodAndReturn(o, "getAngle", new Class[]{}, new Object[]{}, double.class);
    }
	
	public boolean invokeIsCarre(Object o) {
		return callMethodAndReturn(o, "isCarre", new Class[]{}, new Object[]{}, boolean.class);
    }
	
	@SuppressWarnings("unchecked")
	private <T> T callMethodAndReturn(Object o, String methodName, Class<?>[] types, Object[] args,
			Class<T> returnType) {
		Object objectRes = callMethod(o, methodName, types, args);
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

	
}
