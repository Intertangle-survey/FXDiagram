package de.fxdiagram.core.extensions;

import de.fxdiagram.core.extensions.DoubleConstant;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberExpressionBase;
import javafx.beans.value.ObservableNumberValue;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class NumberExpressionExtensions {
  public static double EPSILON = 1e-9;
  
  public static double SQRT_EPSILON = Math.sqrt(1e-9);
  
  @Pure
  public static BooleanBinding operator_lessThan(final NumberExpressionBase a, final ObservableNumberValue b) {
    return a.lessThan(b);
  }
  
  @Pure
  public static BooleanBinding operator_lessEqualsThan(final NumberExpressionBase a, final ObservableNumberValue b) {
    return a.lessThanOrEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_greaterThan(final NumberExpressionBase a, final ObservableNumberValue b) {
    return a.greaterThan(b);
  }
  
  @Pure
  public static BooleanBinding operator_greaterEqualsThan(final NumberExpressionBase a, final ObservableNumberValue b) {
    return a.greaterThanOrEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_equals(final NumberExpressionBase a, final ObservableNumberValue b) {
    return a.isEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_notEquals(final NumberExpressionBase a, final ObservableNumberValue b) {
    return a.isNotEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_lessThan(final double a, final NumberExpressionBase b) {
    return new DoubleConstant(a).lessThan(b);
  }
  
  @Pure
  public static BooleanBinding operator_lessEqualsThan(final double a, final NumberExpressionBase b) {
    return new DoubleConstant(a).lessThanOrEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_greaterThan(final double a, final NumberExpressionBase b) {
    return new DoubleConstant(a).greaterThan(b);
  }
  
  @Pure
  public static BooleanBinding operator_greaterEqualsThan(final double a, final NumberExpressionBase b) {
    return new DoubleConstant(a).greaterThanOrEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_equals(final double a, final NumberExpressionBase b) {
    return new DoubleConstant(a).isEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_notEquals(final double a, final NumberExpressionBase b) {
    return new DoubleConstant(a).isNotEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_lessThan(final NumberExpressionBase a, final double b) {
    return a.lessThan(b);
  }
  
  @Pure
  public static BooleanBinding operator_lessEqualsThan(final NumberExpressionBase a, final double b) {
    return a.lessThanOrEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_greaterThan(final NumberExpressionBase a, final double b) {
    return a.greaterThan(b);
  }
  
  @Pure
  public static BooleanBinding operator_greaterEqualsThan(final NumberExpressionBase a, final double b) {
    return a.greaterThanOrEqualTo(b);
  }
  
  @Pure
  public static BooleanBinding operator_equals(final NumberExpressionBase a, final double b) {
    return a.isEqualTo(b, NumberExpressionExtensions.EPSILON);
  }
  
  @Pure
  public static BooleanBinding operator_notEquals(final NumberExpressionBase a, final double b) {
    return a.isNotEqualTo(b, NumberExpressionExtensions.EPSILON);
  }
}
