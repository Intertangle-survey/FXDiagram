package de.fxdiagram.mapping;

import com.google.common.base.Objects;
import de.fxdiagram.annotations.properties.ModelNode;
import de.fxdiagram.core.model.ModelElementImpl;
import de.fxdiagram.core.model.ToString;
import de.fxdiagram.mapping.AbstractMapping;
import de.fxdiagram.mapping.IMappedElementDescriptor;
import de.fxdiagram.mapping.IMappedElementDescriptorProvider;
import de.fxdiagram.mapping.XDiagramConfig;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * Base implementation of {@link IMappedElementDescriptor}.
 */
@ModelNode({ "mappingConfigID", "mappingID" })
@SuppressWarnings("all")
public abstract class AbstractMappedElementDescriptor<T extends Object> implements IMappedElementDescriptor<T> {
  private AbstractMapping<T> mapping;
  
  private IMappedElementDescriptorProvider provider;
  
  public AbstractMappedElementDescriptor(final String mappingConfigID, final String mappingID) {
    this.mappingConfigIDProperty.set(mappingConfigID);
    this.mappingIDProperty.set(mappingID);
  }
  
  public IMappedElementDescriptorProvider getProvider() {
    boolean _equals = Objects.equal(this.provider, null);
    if (_equals) {
      XDiagramConfig.Registry _instance = XDiagramConfig.Registry.getInstance();
      String _mappingConfigID = this.getMappingConfigID();
      final XDiagramConfig config = _instance.getConfigByID(_mappingConfigID);
      IMappedElementDescriptorProvider _domainObjectProvider = config.getDomainObjectProvider();
      this.provider = _domainObjectProvider;
    }
    return this.provider;
  }
  
  @Override
  public AbstractMapping<T> getMapping() {
    AbstractMapping<T> _xblockexpression = null;
    {
      boolean _equals = Objects.equal(this.mapping, null);
      if (_equals) {
        XDiagramConfig.Registry _instance = XDiagramConfig.Registry.getInstance();
        String _mappingConfigID = this.getMappingConfigID();
        final XDiagramConfig config = _instance.getConfigByID(_mappingConfigID);
        String _mappingID = this.getMappingID();
        AbstractMapping<?> _mappingByID = config.getMappingByID(_mappingID);
        this.mapping = ((AbstractMapping<T>) _mappingByID);
      }
      _xblockexpression = this.mapping;
    }
    return _xblockexpression;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if ((obj instanceof AbstractMappedElementDescriptor<?>)) {
      return (Objects.equal(this.getMappingConfigID(), ((AbstractMappedElementDescriptor<?>)obj).getMappingConfigID()) && Objects.equal(this.getMappingID(), ((AbstractMappedElementDescriptor<?>)obj).getMappingID()));
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    String _mappingConfigID = this.getMappingConfigID();
    int _hashCode = _mappingConfigID.hashCode();
    int _multiply = (31 * _hashCode);
    String _mappingID = this.getMappingID();
    int _hashCode_1 = _mappingID.hashCode();
    int _multiply_1 = (37 * _hashCode_1);
    return (_multiply + _multiply_1);
  }
  
  /**
   * Automatically generated by @ModelNode. Needed for deserialization.
   */
  public AbstractMappedElementDescriptor() {
  }
  
  public void populate(final ModelElementImpl modelElement) {
    modelElement.addProperty(mappingConfigIDProperty, String.class);
    modelElement.addProperty(mappingIDProperty, String.class);
  }
  
  public String toString() {
    return ToString.toString(this);
  }
  
  private ReadOnlyStringWrapper mappingConfigIDProperty = new ReadOnlyStringWrapper(this, "mappingConfigID");
  
  public String getMappingConfigID() {
    return this.mappingConfigIDProperty.get();
  }
  
  public ReadOnlyStringProperty mappingConfigIDProperty() {
    return this.mappingConfigIDProperty.getReadOnlyProperty();
  }
  
  private ReadOnlyStringWrapper mappingIDProperty = new ReadOnlyStringWrapper(this, "mappingID");
  
  public String getMappingID() {
    return this.mappingIDProperty.get();
  }
  
  public ReadOnlyStringProperty mappingIDProperty() {
    return this.mappingIDProperty.getReadOnlyProperty();
  }
}
