package uk.gov.hmrc.itmp.service.common.rules.api.utils;

import java.util.List;
import java.util.stream.Collectors;
import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesResponse;

/**
 * <strong>This class provide utility methods like filtering the collection object.</strong>
 *
 * <p>It can be used to filter the objects returned in the {@link
 * RulesResponse#getFactsFromSession()} using the type of object
 *
 * <pre>
 * Simple example to showing filtering object using RulesUtils.
 * {@code
 *
 *  List<Class<?>> returnedFactsClass = new ArrayList<>();
 * returnedFactsClass.add(Employee.class);
 *
 * List<Object> filteredFacts =
 * RulesUtils.filterFacts(rulesResponse.getFactsFromSession(), returnedFactsClass);
 *  }
 *  </pre>
 *
 * @since 1.0.0
 */
public class RulesUtils {

  private RulesUtils() {
    throw new AssertionError();
  }

  /**
   * This method is used to filter the facts returned in the {@link
   * RulesResponse#getFactsFromSession()} using the type of object
   *
   * @param objectList List of object passed for filtration
   * @param returnedFactsClass List of object class that need to be filtered
   * @return List of filtered objects
   */
  public static List<Object> filterFacts(
      List<Object> objectList, List<Class<?>> returnedFactsClass) {

    return objectList
        .stream()
        .filter(element -> returnedFactsClass.contains(element.getClass()))
        .collect(Collectors.toList());
  }
}
