package de.fxdiagram.core;

import com.google.common.base.Objects;
import de.fxdiagram.core.XControlPointType;
import de.fxdiagram.core.XShape;
import de.fxdiagram.core.behavior.MoveBehavior;
import de.fxdiagram.core.extensions.Point2DExtensions;
import de.fxdiagram.core.extensions.TransformExtensions;
import de.fxdiagram.core.extensions.UriExtensions;
import de.fxdiagram.core.model.ModelElement;
import de.fxdiagram.core.model.XModelProvider;
import java.net.URL;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class XControlPoint extends XShape implements XModelProvider {
  public XControlPoint() {
    this.setType(XControlPointType.CONTROL_POINT);
  }
  
  public boolean setType(final XControlPointType type) {
    boolean _xblockexpression = false;
    {
      this.typeProperty.set(type);
      boolean _switchResult = false;
      boolean _matched = false;
      if (!_matched) {
        if (Objects.equal(type,XControlPointType.ANCHOR)) {
          _matched=true;
          Circle _circle = new Circle();
          final Procedure1<Circle> _function = new Procedure1<Circle>() {
            public void apply(final Circle it) {
              it.setRadius(3);
              it.setStroke(Color.BLUE);
              it.setFill(Color.WHITE);
            }
          };
          Circle _doubleArrow = ObjectExtensions.<Circle>operator_doubleArrow(_circle, _function);
          boolean _setNode = this.setNode(_doubleArrow);
          _switchResult = _setNode;
        }
      }
      if (!_matched) {
        if (Objects.equal(type,XControlPointType.CONTROL_POINT)) {
          _matched=true;
          Node _newMagnet = this.newMagnet();
          boolean _setNode_1 = this.setNode(_newMagnet);
          _switchResult = _setNode_1;
        }
      }
      if (!_matched) {
        if (Objects.equal(type,XControlPointType.INTERPOLATED)) {
          _matched=true;
          Circle _circle_1 = new Circle();
          final Procedure1<Circle> _function_1 = new Procedure1<Circle>() {
            public void apply(final Circle it) {
              it.setRadius(5);
              it.setStroke(Color.RED);
              it.setFill(Color.WHITE);
            }
          };
          Circle _doubleArrow_1 = ObjectExtensions.<Circle>operator_doubleArrow(_circle_1, _function_1);
          boolean _setNode_2 = this.setNode(_doubleArrow_1);
          _switchResult = _setNode_2;
        }
      }
      _xblockexpression = (_switchResult);
    }
    return _xblockexpression;
  }
  
  protected void doActivate() {
    XControlPointType _type = this.getType();
    boolean _notEquals = (!Objects.equal(_type, XControlPointType.ANCHOR));
    if (_notEquals) {
      MoveBehavior<XControlPoint> _moveBehavior = new MoveBehavior<XControlPoint>(this);
      this.addBehavior(_moveBehavior);
    }
  }
  
  public void selectionFeedback(final boolean isSelected) {
    if (isSelected) {
      XControlPointType _type = this.getType();
      final XControlPointType getType = _type;
      boolean _matched = false;
      if (!_matched) {
        if (Objects.equal(getType,XControlPointType.CONTROL_POINT)) {
          _matched=true;
          Node _node = this.getNode();
          DropShadow _dropShadow = new DropShadow();
          _node.setEffect(_dropShadow);
        }
      }
      if (!_matched) {
        if (Objects.equal(getType,XControlPointType.INTERPOLATED)) {
          _matched=true;
          Node _node_1 = this.getNode();
          ((Circle) _node_1).setFill(Color.RED);
        }
      }
    } else {
      XControlPointType _type_1 = this.getType();
      final XControlPointType getType_1 = _type_1;
      boolean _matched_1 = false;
      if (!_matched_1) {
        if (Objects.equal(getType_1,XControlPointType.CONTROL_POINT)) {
          _matched_1=true;
          Node _node_2 = this.getNode();
          _node_2.setEffect(null);
        }
      }
      if (!_matched_1) {
        if (Objects.equal(getType_1,XControlPointType.INTERPOLATED)) {
          _matched_1=true;
          Node _node_3 = this.getNode();
          ((Circle) _node_3).setFill(Color.WHITE);
        }
      }
    }
  }
  
  public boolean isSelectable() {
    boolean _and = false;
    XControlPointType _type = this.getType();
    boolean _notEquals = (!Objects.equal(_type, XControlPointType.ANCHOR));
    if (!_notEquals) {
      _and = false;
    } else {
      boolean _isSelectable = super.isSelectable();
      _and = (_notEquals && _isSelectable);
    }
    return _and;
  }
  
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("XControlPoint at (");
    double _layoutX = this.getLayoutX();
    _builder.append(_layoutX, "");
    _builder.append(",");
    double _layoutY = this.getLayoutY();
    _builder.append(_layoutY, "");
    _builder.append(")");
    return _builder.toString();
  }
  
  public boolean update(final List<XControlPoint> siblings) {
    boolean _xifexpression = false;
    XControlPointType _type = this.getType();
    boolean _equals = Objects.equal(_type, XControlPointType.CONTROL_POINT);
    if (_equals) {
      boolean _xblockexpression = false;
      {
        final int index = siblings.indexOf(this);
        boolean _xifexpression_1 = false;
        boolean _and = false;
        if (!(index > 0)) {
          _and = false;
        } else {
          int _size = siblings.size();
          int _minus = (_size - 1);
          boolean _lessThan = (index < _minus);
          _and = ((index > 0) && _lessThan);
        }
        if (_and) {
          boolean _xblockexpression_1 = false;
          {
            final XControlPoint predecessor = siblings.get((index - 1));
            final XControlPoint successor = siblings.get((index + 1));
            double _layoutX = successor.getLayoutX();
            double _layoutX_1 = predecessor.getLayoutX();
            final double dx = (_layoutX - _layoutX_1);
            double _layoutY = successor.getLayoutY();
            double _layoutY_1 = predecessor.getLayoutY();
            final double dy = (_layoutY - _layoutY_1);
            double _atan2 = Math.atan2(dy, dx);
            double angle = Math.toDegrees(_atan2);
            double _layoutX_2 = this.getLayoutX();
            double _layoutY_2 = this.getLayoutY();
            double _layoutX_3 = successor.getLayoutX();
            double _layoutY_3 = successor.getLayoutY();
            double _layoutX_4 = predecessor.getLayoutX();
            double _layoutY_4 = predecessor.getLayoutY();
            boolean _isClockwise = Point2DExtensions.isClockwise(_layoutX_2, _layoutY_2, _layoutX_3, _layoutY_3, _layoutX_4, _layoutY_4);
            if (_isClockwise) {
              angle = (angle + 180);
            }
            Affine _affine = new Affine();
            final Affine trafo = _affine;
            Node _node = this.getNode();
            Bounds _layoutBounds = _node.getLayoutBounds();
            double _width = _layoutBounds.getWidth();
            double _multiply = ((-0.5) * _width);
            Node _node_1 = this.getNode();
            Bounds _layoutBounds_1 = _node_1.getLayoutBounds();
            double _height = _layoutBounds_1.getHeight();
            double _minus_1 = (-_height);
            double _minus_2 = (_minus_1 - 5);
            TransformExtensions.translate(trafo, _multiply, _minus_2);
            TransformExtensions.rotate(trafo, angle);
            ObservableList<Transform> _transforms = this.getTransforms();
            boolean _setAll = _transforms.setAll(trafo);
            _xblockexpression_1 = (_setAll);
          }
          _xifexpression_1 = _xblockexpression_1;
        }
        _xblockexpression = (_xifexpression_1);
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }
  
  protected Node newMagnet() {
    Group _group = new Group();
    final Procedure1<Group> _function = new Procedure1<Group>() {
      public void apply(final Group it) {
        try {
          ObservableList<Node> _children = it.getChildren();
          String _uRI = UriExtensions.toURI(XControlPoint.this, "images/Magnet.fxml");
          URL _uRL = new URL(_uRI);
          Node _load = FXMLLoader.<Node>load(_uRL);
          _children.add(_load);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    };
    Group _doubleArrow = ObjectExtensions.<Group>operator_doubleArrow(_group, _function);
    return _doubleArrow;
  }
  
  public void populate(final ModelElement it) {
    super.populate(it);
    it.<XControlPointType>addProperty(this.typeProperty, XControlPointType.class);
  }
  
  private ReadOnlyObjectWrapper<XControlPointType> typeProperty = new ReadOnlyObjectWrapper<XControlPointType>(this, "type");
  
  public XControlPointType getType() {
    return this.typeProperty.get();
  }
  
  public ReadOnlyObjectProperty<XControlPointType> typeProperty() {
    return this.typeProperty.getReadOnlyProperty();
  }
}
