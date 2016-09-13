package de.fxdiagram.eclipse.xtext;

import com.google.common.base.Objects;
import de.fxdiagram.annotations.properties.ModelNode;
import de.fxdiagram.core.model.ModelElementImpl;
import de.fxdiagram.core.model.ToString;
import de.fxdiagram.eclipse.xtext.XtextDomainObjectProvider;
import de.fxdiagram.eclipse.xtext.ids.XtextEObjectID;
import de.fxdiagram.mapping.AbstractMappedElementDescriptor;
import de.fxdiagram.mapping.IMappedElementDescriptorProvider;
import java.util.NoSuchElementException;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;

@ModelNode("elementID")
@SuppressWarnings("all")
public class EObjectDescriptionDescriptor extends AbstractMappedElementDescriptor<IEObjectDescription> {
  public EObjectDescriptionDescriptor(final XtextEObjectID elementID, final String mappingConfigId, final String mappingId) {
    super(mappingConfigId, mappingId);
    this.elementIDProperty.set(elementID);
  }
  
  @Override
  public XtextDomainObjectProvider getProvider() {
    IMappedElementDescriptorProvider _provider = super.getProvider();
    return ((XtextDomainObjectProvider) _provider);
  }
  
  @Override
  public <U extends Object> U withDomainObject(final Function1<? super IEObjectDescription, ? extends U> lambda) {
    U _xblockexpression = null;
    {
      XtextDomainObjectProvider _provider = this.getProvider();
      XtextEObjectID _elementID = this.getElementID();
      final IResourceDescriptions index = _provider.getIndex(_elementID);
      XtextEObjectID _elementID_1 = this.getElementID();
      final IEObjectDescription description = _elementID_1.findInIndex(index);
      boolean _equals = Objects.equal(description, null);
      if (_equals) {
        XtextEObjectID _elementID_2 = this.getElementID();
        String _plus = ("Element " + _elementID_2);
        String _plus_1 = (_plus + " does not exist");
        throw new NoSuchElementException(_plus_1);
      }
      _xblockexpression = lambda.apply(description);
    }
    return _xblockexpression;
  }
  
  @Override
  public Object openInEditor(final boolean select) {
    XtextDomainObjectProvider _provider = this.getProvider();
    XtextEObjectID _elementID = this.getElementID();
    return ((XtextDomainObjectProvider) _provider).getCachedEditor(_elementID, true, true);
  }
  
  @Override
  public String getName() {
    XtextEObjectID _elementID = this.getElementID();
    QualifiedName _qualifiedName = null;
    if (_elementID!=null) {
      _qualifiedName=_elementID.getQualifiedName();
    }
    String _lastSegment = null;
    if (_qualifiedName!=null) {
      _lastSegment=_qualifiedName.getLastSegment();
    }
    return _lastSegment;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if ((obj instanceof EObjectDescriptionDescriptor)) {
      return (super.equals(obj) && Objects.equal(((EObjectDescriptionDescriptor)obj).getElementID(), this.getElementID()));
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    int _hashCode = super.hashCode();
    XtextEObjectID _elementID = this.getElementID();
    int _hashCode_1 = _elementID.hashCode();
    int _multiply = (131 * _hashCode_1);
    return (_hashCode + _multiply);
  }
  
  /**
   * Automatically generated by @ModelNode. Needed for deserialization.
   */
  public EObjectDescriptionDescriptor() {
  }
  
  public void populate(final ModelElementImpl modelElement) {
    super.populate(modelElement);
    modelElement.addProperty(elementIDProperty, XtextEObjectID.class);
  }
  
  public String toString() {
    return ToString.toString(this);
  }
  
  private ReadOnlyObjectWrapper<XtextEObjectID> elementIDProperty = new ReadOnlyObjectWrapper<XtextEObjectID>(this, "elementID");
  
  public XtextEObjectID getElementID() {
    return this.elementIDProperty.get();
  }
  
  public ReadOnlyObjectProperty<XtextEObjectID> elementIDProperty() {
    return this.elementIDProperty.getReadOnlyProperty();
  }
}
