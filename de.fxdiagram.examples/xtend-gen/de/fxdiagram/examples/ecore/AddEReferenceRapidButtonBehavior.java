package de.fxdiagram.examples.ecore;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import de.fxdiagram.core.XConnection;
import de.fxdiagram.core.XConnectionLabel;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.XRapidButton;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.anchors.ArrowHead;
import de.fxdiagram.core.anchors.DiamondArrowHead;
import de.fxdiagram.core.anchors.LineArrowHead;
import de.fxdiagram.core.extensions.ButtonExtensions;
import de.fxdiagram.core.extensions.CoreExtensions;
import de.fxdiagram.core.model.DomainObjectHandle;
import de.fxdiagram.core.tools.AbstractChooser;
import de.fxdiagram.core.tools.ChooserConnectionProvider;
import de.fxdiagram.examples.ecore.EClassNode;
import de.fxdiagram.examples.ecore.EReferenceHandle;
import de.fxdiagram.examples.ecore.EcoreDomainObjectProvider;
import de.fxdiagram.lib.model.AbstractConnectionRapidButtonBehavior;
import de.fxdiagram.lib.tools.CarusselChooser;
import java.util.Collections;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class AddEReferenceRapidButtonBehavior extends AbstractConnectionRapidButtonBehavior<EClassNode,EReference,EReferenceHandle> {
  public AddEReferenceRapidButtonBehavior(final EClassNode host) {
    super(host);
  }
  
  protected Iterable<EReference> getInitialModelChoices() {
    EClassNode _host = this.getHost();
    EClass _eClass = _host.getEClass();
    EList<EReference> _eReferences = _eClass.getEReferences();
    return _eReferences;
  }
  
  protected EReferenceHandle getChoiceKey(final EReference model) {
    EcoreDomainObjectProvider _domainObjectProvider = this.getDomainObjectProvider();
    EReferenceHandle _createEReferenceHandle = _domainObjectProvider.createEReferenceHandle(model);
    return _createEReferenceHandle;
  }
  
  protected XNode createNode(final EReferenceHandle handle) {
    EClassNode _eClassNode = new EClassNode();
    final Procedure1<EClassNode> _function = new Procedure1<EClassNode>() {
      public void apply(final EClassNode it) {
        EcoreDomainObjectProvider _domainObjectProvider = AddEReferenceRapidButtonBehavior.this.getDomainObjectProvider();
        EReference _domainObject = handle.getDomainObject();
        EClass _eReferenceType = _domainObject.getEReferenceType();
        DomainObjectHandle _createDomainObjectHandle = _domainObjectProvider.createDomainObjectHandle(_eReferenceType);
        it.setDomainObject(_createDomainObjectHandle);
      }
    };
    EClassNode _doubleArrow = ObjectExtensions.<EClassNode>operator_doubleArrow(_eClassNode, _function);
    return _doubleArrow;
  }
  
  protected EcoreDomainObjectProvider getDomainObjectProvider() {
    EClassNode _host = this.getHost();
    XRoot _root = CoreExtensions.getRoot(_host);
    EcoreDomainObjectProvider _domainObjectProvider = _root.<EcoreDomainObjectProvider>getDomainObjectProvider(EcoreDomainObjectProvider.class);
    return _domainObjectProvider;
  }
  
  protected AbstractChooser createChooser(final XRapidButton button, final Set<EReferenceHandle> availableChoiceKeys, final Set<EReferenceHandle> unavailableChoiceKeys) {
    CarusselChooser _xblockexpression = null;
    {
      EClassNode _host = this.getHost();
      Pos _chooserPosition = button.getChooserPosition();
      CarusselChooser _carusselChooser = new CarusselChooser(_host, _chooserPosition);
      final CarusselChooser chooser = _carusselChooser;
      final Procedure1<EReferenceHandle> _function = new Procedure1<EReferenceHandle>() {
        public void apply(final EReferenceHandle it) {
          XNode _createNode = AddEReferenceRapidButtonBehavior.this.createNode(it);
          chooser.addChoice(_createNode, it);
        }
      };
      IterableExtensions.<EReferenceHandle>forEach(availableChoiceKeys, _function);
      final ChooserConnectionProvider _function_1 = new ChooserConnectionProvider() {
        public XConnection getConnection(final XNode host, final XNode choice, final DomainObjectHandle handle) {
          XConnection _xblockexpression = null;
          {
            Object _domainObject = handle.getDomainObject();
            final EReference reference = ((EReference) _domainObject);
            XConnection _xConnection = new XConnection(host, choice, handle);
            final Procedure1<XConnection> _function = new Procedure1<XConnection>() {
              public void apply(final XConnection it) {
                ArrowHead _xifexpression = null;
                boolean _isContainer = reference.isContainer();
                if (_isContainer) {
                  DiamondArrowHead _diamondArrowHead = new DiamondArrowHead(it, false);
                  _xifexpression = _diamondArrowHead;
                } else {
                  LineArrowHead _lineArrowHead = new LineArrowHead(it, false);
                  _xifexpression = _lineArrowHead;
                }
                it.setTargetArrowHead(_xifexpression);
                ArrowHead _xifexpression_1 = null;
                boolean _isContainment = reference.isContainment();
                if (_isContainment) {
                  DiamondArrowHead _diamondArrowHead_1 = new DiamondArrowHead(it, true);
                  _xifexpression_1 = _diamondArrowHead_1;
                } else {
                  LineArrowHead _xifexpression_2 = null;
                  boolean _and = false;
                  boolean _isContainer_1 = reference.isContainer();
                  boolean _not = (!_isContainer_1);
                  if (!_not) {
                    _and = false;
                  } else {
                    EReference _eOpposite = reference.getEOpposite();
                    boolean _notEquals = (!Objects.equal(_eOpposite, null));
                    _and = (_not && _notEquals);
                  }
                  if (_and) {
                    LineArrowHead _lineArrowHead_1 = new LineArrowHead(it, true);
                    _xifexpression_2 = _lineArrowHead_1;
                  }
                  _xifexpression_1 = _xifexpression_2;
                }
                it.setSourceArrowHead(_xifexpression_1);
                XConnectionLabel _xConnectionLabel = new XConnectionLabel(it);
                final Procedure1<XConnectionLabel> _function = new Procedure1<XConnectionLabel>() {
                  public void apply(final XConnectionLabel it) {
                    Text _text = it.getText();
                    String _name = reference.getName();
                    _text.setText(_name);
                    it.setPosition(0.8);
                  }
                };
                ObjectExtensions.<XConnectionLabel>operator_doubleArrow(_xConnectionLabel, _function);
                EReference _eOpposite_1 = reference.getEOpposite();
                boolean _notEquals_1 = (!Objects.equal(_eOpposite_1, null));
                if (_notEquals_1) {
                  XConnectionLabel _xConnectionLabel_1 = new XConnectionLabel(it);
                  final Procedure1<XConnectionLabel> _function_1 = new Procedure1<XConnectionLabel>() {
                    public void apply(final XConnectionLabel it) {
                      Text _text = it.getText();
                      EReference _eOpposite = reference.getEOpposite();
                      String _name = _eOpposite.getName();
                      _text.setText(_name);
                      it.setPosition(0.2);
                    }
                  };
                  ObjectExtensions.<XConnectionLabel>operator_doubleArrow(_xConnectionLabel_1, _function_1);
                }
              }
            };
            XConnection _doubleArrow = ObjectExtensions.<XConnection>operator_doubleArrow(_xConnection, _function);
            _xblockexpression = (_doubleArrow);
          }
          return _xblockexpression;
        }
      };
      chooser.setConnectionProvider(_function_1);
      _xblockexpression = (chooser);
    }
    return _xblockexpression;
  }
  
  protected Iterable<XRapidButton> createButtons(final Procedure1<? super XRapidButton> addConnectionAction) {
    EClassNode _host = this.getHost();
    SVGPath _arrowButton = ButtonExtensions.getArrowButton(Side.LEFT, "Discover references");
    XRapidButton _xRapidButton = new XRapidButton(_host, 0, 0.5, _arrowButton, addConnectionAction);
    EClassNode _host_1 = this.getHost();
    SVGPath _arrowButton_1 = ButtonExtensions.getArrowButton(Side.RIGHT, "Discover references");
    XRapidButton _xRapidButton_1 = new XRapidButton(_host_1, 1, 0.5, _arrowButton_1, addConnectionAction);
    return Collections.<XRapidButton>unmodifiableList(Lists.<XRapidButton>newArrayList(_xRapidButton, _xRapidButton_1));
  }
}
