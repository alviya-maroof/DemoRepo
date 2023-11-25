package com.zinfitech.pojo;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface ZiniFunctionalInterface {

  BiPredicate<String, Integer> dollarBiPredicate = (string, offset) -> string.trim()
      .startsWith("$", offset);
  BiPredicate<String, Integer> doubleQuoteBiPredicate = (string, offset) -> string.trim()
      .startsWith("\"", offset);
  Function<String, List<String>> splitByComa = string -> List.of(string.split(","));

  Function<String, List<String>> splitByDot = string -> List.of(string.split("\\."));
  BiFunction<List<Object>, Enum<?>, String> biFunction = (headers, test) -> headers.get(
      headers.indexOf(test.toString())).toString();
  BiFunction<List<Object>, Enum<?>, Integer> indexOf = (headers, test) ->  headers.indexOf(test.toString());


  default boolean startStringWith(BiPredicate<String, Integer> biPredicate, String string,
      Integer offset) {
    return biPredicate.test(string, offset);
  }

  default List<String> splitString(Function<String, List<String>> function, String string) {
    return function.apply(string);
  }

  default String getHeader(List<Object> headers, Enum<?> enumType) {
    return biFunction.apply(headers, enumType);
  }

  default String getRowValue(List<Object>rowData,List<Object> headers, Enum<?> enumType) {
    return rowData.get(indexOf.apply(headers, enumType)).toString();
  }
}
