package de.fxdiagram.examples.ecore;

import de.fxdiagram.core.XConnection;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.anchors.TriangleArrowHead;
import de.fxdiagram.core.extensions.ButtonExtensions;
import de.fxdiagram.core.extensions.CoreExtensions;
import de.fxdiagram.core.model.DomainObjectDescriptor;
import de.fxdiagram.examples.ecore.EClassDescriptor;
import de.fxdiagram.examples.ecore.EClassNode;
import de.fxdiagram.examples.ecore.ESuperTypeDescriptor;
import de.fxdiagram.examples.ecore.ESuperTypeHandle;
import de.fxdiagram.examples.ecore.EcoreDomainObjectProvider;
import de.fxdiagram.lib.buttons.RapidButton;
import de.fxdiagram.lib.buttons.RapidButtonAction;
import de.fxdiagram.lib.chooser.ChooserConnectionProvider;
import de.fxdiagram.lib.chooser.ConnectedNodeChooser;
import de.fxdiagram.lib.chooser.CoverFlowChoice;
import de.fxdiagram.lib.model.AbstractConnectionRapidButtonBehavior;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import javafx.geometry.Side;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class AddESuperTypeRapidButtonBehavior extends AbstractConnectionRapidButtonBehavior<EClassNode, EClass, ESuperTypeDescriptor> {
  public AddESuperTypeRapidButtonBehavior(final EClassNode host) {
    super(host);
  }
  
  @Override
  protected Iterable<EClass> getInitialModelChoices() {
    return this.getHost().getEClass().getESuperTypes();
  }
  
  @Override
  protected ESuperTypeDescriptor getChoiceKey(final EClass superType) {
    EcoreDomainObjectProvider _domainObjectProvider = this.getDomainObjectProvider();
    EClass _eClass = this.getHost().getEClass();
    ESuperTypeHandle _eSuperTypeHandle = new ESuperTypeHandle(_eClass, superType);
    return _domainObjectProvider.createESuperClassDescriptor(_eSuperTypeHandle);
  }
  
  @Override
  protected XNode createNode(final ESuperTypeDescriptor key) {
    EClassDescriptor _createEClassDescriptor = this.getDomainObjectProvider().createEClassDescriptor(key.getDomainObject().getSuperType());
    return new EClassNode(_createEClassDescriptor);
  }
  
  protected EcoreDomainObjectProvider getDomainObjectProvider() {
    return CoreExtensions.getRoot(this.getHost()).<EcoreDomainObjectProvider>getDomainObjectProvider(EcoreDomainObjectProvider.class);
  }
  
  @Override
  protected ConnectedNodeChooser createChooser(final RapidButton button, final Set<ESuperTypeDescriptor> availableChoiceKeys, final Set<ESuperTypeDescriptor> unavailableChoiceKeys) {
    ConnectedNodeChooser _xblockexpression = null;
    {
      EClassNode _host = this.getHost();
      Side _position = button.getPosition();
      CoverFlowChoice _coverFlowChoice = new CoverFlowChoice();
      final ConnectedNodeChooser chooser = new ConnectedNodeChooser(_host, _position, _coverFlowChoice);
      final Consumer<ESuperTypeDescriptor> _function = (ESuperTypeDescriptor it) -> {
        chooser.addChoice(this.createNode(it), it);
      };
      availableChoiceKeys.forEach(_function);
      final ChooserConnectionProvider _function_1 = (XNode host, XNode choice, DomainObjectDescriptor descriptor) -> {
        XConnection _xConnection = new XConnection(host, choice, descriptor);
        final Procedure1<XConnection> _function_2 = (XConnection it) -> {
          Paint _backgroundPaint = CoreExtensions.getDiagram(host).getBackgroundPaint();
          TriangleArrowHead _triangleArrowHead = new TriangleArrowHead(it, 10, 15, 
            null, _backgroundPaint, false);
          it.setTargetArrowHead(_triangleArrowHead);
        };
        return ObjectExtensions.<XConnection>operator_doubleArrow(_xConnection, _function_2);
      };
      chooser.setConnectionProvider(_function_1);
      _xblockexpression = chooser;
    }
    return _xblockexpression;
  }
  
  @Override
  protected Iterable<RapidButton> createButtons(final RapidButtonAction addConnectionAction) {
    EClassNode _host = this.getHost();
    SVGPath _triangleButton = ButtonExtensions.getTriangleButton(Side.TOP, "Discover supertypes");
    RapidButton _rapidButton = new RapidButton(_host, Side.TOP, _triangleButton, addConnectionAction);
    EClassNode _host_1 = this.getHost();
    SVGPath _triangleButton_1 = ButtonExtensions.getTriangleButton(Side.BOTTOM, "Discover supertypes");
    RapidButton _rapidButton_1 = new RapidButton(_host_1, Side.BOTTOM, _triangleButton_1, addConnectionAction);
    return Collections.<RapidButton>unmodifiableList(CollectionLiterals.<RapidButton>newArrayList(_rapidButton, _rapidButton_1));
  }
}
