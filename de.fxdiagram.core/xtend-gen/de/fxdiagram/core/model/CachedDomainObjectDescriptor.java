package de.fxdiagram.core.model;

import com.google.common.base.Objects;
import de.fxdiagram.annotations.properties.ModelNode;
import de.fxdiagram.core.model.DomainObjectDescriptor;
import de.fxdiagram.core.model.DomainObjectProvider;
import de.fxdiagram.core.model.ModelElementImpl;
import de.fxdiagram.core.model.ToString;
import java.util.NoSuchElementException;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * Base class for {@link DomainObjectDescriptor}s whose domain object is constant and can
 * be cached.
 */
@ModelNode({ "id", "provider" })
@SuppressWarnings("all")
public abstract class CachedDomainObjectDescriptor<T extends Object> implements DomainObjectDescriptor {
  private T cachedDomainObject;
  
  public CachedDomainObjectDescriptor(final T domainObject, final String id, final DomainObjectProvider provider) {
    this.providerProperty.set(provider);
    this.idProperty.set(id);
    this.cachedDomainObject = domainObject;
  }
  
  @Override
  public String getName() {
    return this.getId();
  }
  
  @Override
  public boolean equals(final Object obj) {
    return ((obj instanceof CachedDomainObjectDescriptor<?>) && Objects.equal(((CachedDomainObjectDescriptor<?>) obj).getDomainObject(), this.getDomainObject()));
  }
  
  @Override
  public int hashCode() {
    T _domainObject = this.getDomainObject();
    return _domainObject.hashCode();
  }
  
  public T getDomainObject() {
    T _elvis = null;
    if (this.cachedDomainObject != null) {
      _elvis = this.cachedDomainObject;
    } else {
      T _xblockexpression = null;
      {
        T _resolveDomainObject = this.resolveDomainObject();
        this.cachedDomainObject = _resolveDomainObject;
        boolean _equals = Objects.equal(this.cachedDomainObject, null);
        if (_equals) {
          String _id = this.getId();
          String _plus = ("Element " + _id);
          String _plus_1 = (_plus + " not found");
          throw new NoSuchElementException(_plus_1);
        }
        _xblockexpression = this.cachedDomainObject;
      }
      _elvis = _xblockexpression;
    }
    return _elvis;
  }
  
  public abstract T resolveDomainObject();
  
  /**
   * Automatically generated by @ModelNode. Needed for deserialization.
   */
  public CachedDomainObjectDescriptor() {
  }
  
  public void populate(final ModelElementImpl modelElement) {
    modelElement.addProperty(idProperty, String.class);
    modelElement.addProperty(providerProperty, DomainObjectProvider.class);
  }
  
  public String toString() {
    return ToString.toString(this);
  }
  
  private ReadOnlyStringWrapper idProperty = new ReadOnlyStringWrapper(this, "id");
  
  public String getId() {
    return this.idProperty.get();
  }
  
  public ReadOnlyStringProperty idProperty() {
    return this.idProperty.getReadOnlyProperty();
  }
  
  private ReadOnlyObjectWrapper<DomainObjectProvider> providerProperty = new ReadOnlyObjectWrapper<DomainObjectProvider>(this, "provider");
  
  public DomainObjectProvider getProvider() {
    return this.providerProperty.get();
  }
  
  public ReadOnlyObjectProperty<DomainObjectProvider> providerProperty() {
    return this.providerProperty.getReadOnlyProperty();
  }
}
