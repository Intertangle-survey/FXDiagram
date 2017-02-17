package de.fxdiagram.core.model;

import de.fxdiagram.annotations.properties.ModelNode;
import de.fxdiagram.core.model.ModelElementImpl;
import de.fxdiagram.core.model.TestEnum;
import de.fxdiagram.core.model.ToString;
import de.fxdiagram.core.model.XModelProvider;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

@ModelNode("testEnum")
@SuppressWarnings("all")
public class TestBean implements XModelProvider {
  public void populate(final ModelElementImpl modelElement) {
    modelElement.addProperty(testEnumProperty, TestEnum.class);
  }
  
  public void postLoad() {
    
  }
  
  public String toString() {
    return ToString.toString(this);
  }
  
  private SimpleObjectProperty<TestEnum> testEnumProperty = new SimpleObjectProperty<TestEnum>(this, "testEnum");
  
  public TestEnum getTestEnum() {
    return this.testEnumProperty.get();
  }
  
  public void setTestEnum(final TestEnum testEnum) {
    this.testEnumProperty.set(testEnum);
  }
  
  public ObjectProperty<TestEnum> testEnumProperty() {
    return this.testEnumProperty;
  }
}
