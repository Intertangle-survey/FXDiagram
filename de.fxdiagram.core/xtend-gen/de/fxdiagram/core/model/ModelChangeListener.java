package de.fxdiagram.core.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;

@SuppressWarnings("all")
public interface ModelChangeListener {
  public abstract void propertyChanged(final Property<? extends Object> property, final Object oldVal, final Object newVal);
  
  public abstract void listPropertyChanged(final ListProperty<? extends Object> property, final ListChangeListener.Change<? extends Object> change);
}
