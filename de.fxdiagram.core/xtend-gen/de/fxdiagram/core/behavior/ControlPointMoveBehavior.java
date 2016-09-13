package de.fxdiagram.core.behavior;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.fxdiagram.core.XConnection;
import de.fxdiagram.core.XConnectionLabel;
import de.fxdiagram.core.XControlPoint;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.XShape;
import de.fxdiagram.core.behavior.MoveBehavior;
import de.fxdiagram.core.command.AbstractCommand;
import de.fxdiagram.core.command.AnimationCommand;
import de.fxdiagram.core.command.CommandContext;
import de.fxdiagram.core.command.CommandStack;
import de.fxdiagram.core.command.MoveCommand;
import de.fxdiagram.core.command.ParallelAnimationCommand;
import de.fxdiagram.core.extensions.ConnectionExtensions;
import de.fxdiagram.core.extensions.CoreExtensions;
import de.fxdiagram.core.extensions.NumberExpressionExtensions;
import de.fxdiagram.core.extensions.Point2DExtensions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

@SuppressWarnings("all")
public class ControlPointMoveBehavior extends MoveBehavior<XControlPoint> {
  public ControlPointMoveBehavior(final XControlPoint host) {
    super(host);
  }
  
  @Override
  public void doActivate() {
    super.doActivate();
    XControlPoint _host = this.getHost();
    BooleanProperty _selectedProperty = _host.selectedProperty();
    final ChangeListener<Boolean> _function = (ObservableValue<? extends Boolean> p, Boolean o, Boolean newValue) -> {
      final XConnection connection = this.getConnection();
      if ((((!(newValue).booleanValue()) && (!Objects.equal(connection, null))) && Objects.equal(connection.getKind(), XConnection.Kind.POLYLINE))) {
        final ObservableList<XControlPoint> siblings = this.getSiblings();
        XControlPoint _host_1 = this.getHost();
        final int index = siblings.indexOf(_host_1);
        if (((index > 0) && (index < (siblings.size() - 1)))) {
          final XControlPoint predecessor = siblings.get((index - 1));
          final XControlPoint successor = siblings.get((index + 1));
          double _layoutX = predecessor.getLayoutX();
          double _layoutY = predecessor.getLayoutY();
          XControlPoint _host_2 = this.getHost();
          double _layoutX_1 = _host_2.getLayoutX();
          XControlPoint _host_3 = this.getHost();
          double _layoutY_1 = _host_3.getLayoutY();
          double _layoutX_2 = successor.getLayoutX();
          double _layoutY_2 = successor.getLayoutY();
          boolean _areOnSameLine = Point2DExtensions.areOnSameLine(_layoutX, _layoutY, _layoutX_1, _layoutY_1, _layoutX_2, _layoutY_2);
          if (_areOnSameLine) {
            XControlPoint _host_4 = this.getHost();
            XRoot _root = CoreExtensions.getRoot(_host_4);
            CommandStack _commandStack = _root.getCommandStack();
            _commandStack.execute(new AbstractCommand() {
              @Override
              public void execute(final CommandContext context) {
                ObservableList<XConnectionLabel> _labels = connection.getLabels();
                final Function1<XConnectionLabel, Pair<XConnectionLabel, Point2D>> _function = (XConnectionLabel it) -> {
                  XConnection _connection = it.getConnection();
                  double _position = it.getPosition();
                  Point2D _at = _connection.at(_position);
                  return Pair.<XConnectionLabel, Point2D>of(it, _at);
                };
                List<Pair<XConnectionLabel, Point2D>> _map = ListExtensions.<XConnectionLabel, Pair<XConnectionLabel, Point2D>>map(_labels, _function);
                final HashMap<XConnectionLabel, Point2D> label2position = CollectionLiterals.<XConnectionLabel, Point2D>newHashMap(((Pair<? extends XConnectionLabel, ? extends Point2D>[])Conversions.unwrapArray(_map, Pair.class)));
                siblings.remove(index);
                ObservableList<XConnectionLabel> _labels_1 = connection.getLabels();
                final Consumer<XConnectionLabel> _function_1 = (XConnectionLabel it) -> {
                  Point2D _get = label2position.get(it);
                  final Function1<XControlPoint, Point2D> _function_2 = (XControlPoint it_1) -> {
                    return ConnectionExtensions.toPoint2D(it_1);
                  };
                  List<Point2D> _map_1 = ListExtensions.<XControlPoint, Point2D>map(siblings, _function_2);
                  ConnectionExtensions.PointOnCurve _nearestPointOnConnection = ConnectionExtensions.getNearestPointOnConnection(_get, _map_1, XConnection.Kind.POLYLINE);
                  double _parameter = _nearestPointOnConnection.getParameter();
                  it.setPosition(_parameter);
                };
                _labels_1.forEach(_function_1);
              }
              
              @Override
              public void undo(final CommandContext context) {
                ObservableList<XConnectionLabel> _labels = connection.getLabels();
                final Function1<XConnectionLabel, Pair<XConnectionLabel, Point2D>> _function = (XConnectionLabel it) -> {
                  XConnection _connection = it.getConnection();
                  double _position = it.getPosition();
                  Point2D _at = _connection.at(_position);
                  return Pair.<XConnectionLabel, Point2D>of(it, _at);
                };
                List<Pair<XConnectionLabel, Point2D>> _map = ListExtensions.<XConnectionLabel, Pair<XConnectionLabel, Point2D>>map(_labels, _function);
                final HashMap<XConnectionLabel, Point2D> label2position = CollectionLiterals.<XConnectionLabel, Point2D>newHashMap(((Pair<? extends XConnectionLabel, ? extends Point2D>[])Conversions.unwrapArray(_map, Pair.class)));
                XControlPoint _host = ControlPointMoveBehavior.this.getHost();
                siblings.add(index, _host);
                ObservableList<XConnectionLabel> _labels_1 = connection.getLabels();
                final Consumer<XConnectionLabel> _function_1 = (XConnectionLabel it) -> {
                  Point2D _get = label2position.get(it);
                  final Function1<XControlPoint, Point2D> _function_2 = (XControlPoint it_1) -> {
                    return ConnectionExtensions.toPoint2D(it_1);
                  };
                  List<Point2D> _map_1 = ListExtensions.<XControlPoint, Point2D>map(siblings, _function_2);
                  ConnectionExtensions.PointOnCurve _nearestPointOnConnection = ConnectionExtensions.getNearestPointOnConnection(_get, _map_1, XConnection.Kind.POLYLINE);
                  double _parameter = _nearestPointOnConnection.getParameter();
                  it.setPosition(_parameter);
                };
                _labels_1.forEach(_function_1);
              }
              
              @Override
              public void redo(final CommandContext context) {
                ObservableList<XConnectionLabel> _labels = connection.getLabels();
                final Function1<XConnectionLabel, Pair<XConnectionLabel, Point2D>> _function = (XConnectionLabel it) -> {
                  XConnection _connection = it.getConnection();
                  double _position = it.getPosition();
                  Point2D _at = _connection.at(_position);
                  return Pair.<XConnectionLabel, Point2D>of(it, _at);
                };
                List<Pair<XConnectionLabel, Point2D>> _map = ListExtensions.<XConnectionLabel, Pair<XConnectionLabel, Point2D>>map(_labels, _function);
                final HashMap<XConnectionLabel, Point2D> label2position = CollectionLiterals.<XConnectionLabel, Point2D>newHashMap(((Pair<? extends XConnectionLabel, ? extends Point2D>[])Conversions.unwrapArray(_map, Pair.class)));
                siblings.remove(index);
                ObservableList<XConnectionLabel> _labels_1 = connection.getLabels();
                final Consumer<XConnectionLabel> _function_1 = (XConnectionLabel it) -> {
                  Point2D _get = label2position.get(it);
                  final Function1<XControlPoint, Point2D> _function_2 = (XControlPoint it_1) -> {
                    return ConnectionExtensions.toPoint2D(it_1);
                  };
                  List<Point2D> _map_1 = ListExtensions.<XControlPoint, Point2D>map(siblings, _function_2);
                  ConnectionExtensions.PointOnCurve _nearestPointOnConnection = ConnectionExtensions.getNearestPointOnConnection(_get, _map_1, XConnection.Kind.POLYLINE);
                  double _parameter = _nearestPointOnConnection.getParameter();
                  it.setPosition(_parameter);
                };
                _labels_1.forEach(_function_1);
              }
            });
          }
        }
      }
    };
    _selectedProperty.addListener(_function);
  }
  
  @Override
  protected void dragTo(final Point2D newPositionInDiagram) {
    boolean _notEquals = (!Objects.equal(newPositionInDiagram, null));
    if (_notEquals) {
      double _x = newPositionInDiagram.getX();
      XControlPoint _host = this.getHost();
      double _layoutX = _host.getLayoutX();
      final double moveDeltaX = (_x - _layoutX);
      double _y = newPositionInDiagram.getY();
      XControlPoint _host_1 = this.getHost();
      double _layoutY = _host_1.getLayoutY();
      final double moveDeltaY = (_y - _layoutY);
      super.dragTo(newPositionInDiagram);
      final ObservableList<XControlPoint> siblings = this.getSiblings();
      XControlPoint _host_2 = this.getHost();
      final int index = siblings.indexOf(_host_2);
      XControlPoint _host_3 = this.getHost();
      XControlPoint.Type _type = _host_3.getType();
      if (_type != null) {
        switch (_type) {
          case INTERPOLATED:
            this.adjustBoth(index, siblings, moveDeltaX, moveDeltaY);
            this.updateDangling(index, siblings);
            break;
          case DANGLING:
            this.updateDangling(index, siblings);
            break;
          case CONTROL_POINT:
            this.adjustLeft(index, siblings, moveDeltaX, moveDeltaY);
            this.adjustRight(index, siblings, moveDeltaX, moveDeltaY);
            break;
          default:
            break;
        }
      }
    }
  }
  
  protected void adjustBoth(final int index, final ObservableList<XControlPoint> siblings, final double moveDeltaX, final double moveDeltaY) {
    if (((index > 0) && (index < (siblings.size() - 1)))) {
      final XControlPoint predecessor = siblings.get((index - 1));
      final XControlPoint successor = siblings.get((index + 1));
      if ((Objects.equal(predecessor.getType(), XControlPoint.Type.CONTROL_POINT) && Objects.equal(successor.getType(), XControlPoint.Type.CONTROL_POINT))) {
        double _layoutX = successor.getLayoutX();
        double _layoutX_1 = predecessor.getLayoutX();
        final double dx0 = (_layoutX - _layoutX_1);
        double _layoutY = successor.getLayoutY();
        double _layoutY_1 = predecessor.getLayoutY();
        final double dy0 = (_layoutY - _layoutY_1);
        XControlPoint _host = this.getHost();
        double _layoutX_2 = _host.getLayoutX();
        double _layoutX_3 = predecessor.getLayoutX();
        final double dx1 = (_layoutX_2 - _layoutX_3);
        XControlPoint _host_1 = this.getHost();
        double _layoutY_2 = _host_1.getLayoutY();
        double _layoutY_3 = predecessor.getLayoutY();
        final double dy1 = (_layoutY_2 - _layoutY_3);
        double _norm = Point2DExtensions.norm(dx0, dy0);
        double _norm_1 = Point2DExtensions.norm(dx1, dy1);
        final double normProd = (_norm * _norm_1);
        if ((normProd > (NumberExpressionExtensions.EPSILON * NumberExpressionExtensions.EPSILON))) {
          final double scalarProd = ((0.5 * ((dx0 * dx1) + (dy0 * dy1))) / normProd);
          final double orthoX = (dx1 - (scalarProd * dx0));
          final double orthoY = (dy1 - (scalarProd * dy0));
          double _norm_2 = Point2DExtensions.norm(orthoX, orthoY);
          boolean _greaterThan = (_norm_2 > NumberExpressionExtensions.EPSILON);
          if (_greaterThan) {
            boolean _selected = predecessor.getSelected();
            boolean _not = (!_selected);
            if (_not) {
              double _layoutX_4 = predecessor.getLayoutX();
              double _plus = (_layoutX_4 + orthoX);
              predecessor.setLayoutX(_plus);
              double _layoutY_4 = predecessor.getLayoutY();
              double _plus_1 = (_layoutY_4 + orthoY);
              predecessor.setLayoutY(_plus_1);
              this.adjustLeft((index - 1), siblings, moveDeltaX, moveDeltaY);
            }
            boolean _selected_1 = successor.getSelected();
            boolean _not_1 = (!_selected_1);
            if (_not_1) {
              double _layoutX_5 = successor.getLayoutX();
              double _plus_2 = (_layoutX_5 + orthoX);
              successor.setLayoutX(_plus_2);
              double _layoutY_5 = successor.getLayoutY();
              double _plus_3 = (_layoutY_5 + orthoY);
              successor.setLayoutY(_plus_3);
              this.adjustRight((index + 1), siblings, moveDeltaX, moveDeltaY);
            }
          }
        }
      }
    }
  }
  
  protected void adjustLeft(final int index, final List<XControlPoint> siblings, final double moveDeltaX, final double moveDeltaY) {
    if ((index > 1)) {
      final XControlPoint current = siblings.get(index);
      final XControlPoint predecessor = siblings.get((index - 1));
      final XControlPoint prepredecessor = siblings.get((index - 2));
      if ((Objects.equal(predecessor.getType(), XControlPoint.Type.INTERPOLATED) && Objects.equal(prepredecessor.getType(), XControlPoint.Type.CONTROL_POINT))) {
        boolean _selected = prepredecessor.getSelected();
        boolean _not = (!_selected);
        if (_not) {
          double _layoutX = predecessor.getLayoutX();
          double _layoutX_1 = current.getLayoutX();
          final double dx0 = (_layoutX - _layoutX_1);
          double _layoutY = predecessor.getLayoutY();
          double _layoutY_1 = current.getLayoutY();
          final double dy0 = (_layoutY - _layoutY_1);
          final double norm0 = Point2DExtensions.norm(dx0, dy0);
          if ((norm0 > NumberExpressionExtensions.EPSILON)) {
            double _layoutX_2 = prepredecessor.getLayoutX();
            double _layoutX_3 = predecessor.getLayoutX();
            final double dx1 = (_layoutX_2 - _layoutX_3);
            double _layoutY_2 = prepredecessor.getLayoutY();
            double _layoutY_3 = predecessor.getLayoutY();
            final double dy1 = (_layoutY_2 - _layoutY_3);
            double _norm = Point2DExtensions.norm(dx1, dy1);
            final double scale = (_norm / norm0);
            double _layoutX_4 = predecessor.getLayoutX();
            double _plus = (_layoutX_4 + (scale * dx0));
            prepredecessor.setLayoutX(_plus);
            double _layoutY_4 = predecessor.getLayoutY();
            double _plus_1 = (_layoutY_4 + (scale * dy0));
            prepredecessor.setLayoutY(_plus_1);
          }
          this.adjustLeft((index - 2), siblings, moveDeltaX, moveDeltaY);
        } else {
          boolean _selected_1 = predecessor.getSelected();
          boolean _not_1 = (!_selected_1);
          if (_not_1) {
            double _layoutX_5 = predecessor.getLayoutX();
            double _plus_2 = (_layoutX_5 + (0.5 * moveDeltaX));
            predecessor.setLayoutX(_plus_2);
            double _layoutY_5 = predecessor.getLayoutY();
            double _plus_3 = (_layoutY_5 + (0.5 * moveDeltaY));
            predecessor.setLayoutY(_plus_3);
          }
        }
      }
    }
  }
  
  protected void adjustRight(final int index, final List<XControlPoint> siblings, final double moveDeltaX, final double moveDeltaY) {
    int _size = siblings.size();
    int _minus = (_size - 2);
    boolean _lessThan = (index < _minus);
    if (_lessThan) {
      final XControlPoint current = siblings.get(index);
      final XControlPoint successor = siblings.get((index + 1));
      final XControlPoint postsuccessor = siblings.get((index + 2));
      if ((Objects.equal(successor.getType(), XControlPoint.Type.INTERPOLATED) && Objects.equal(postsuccessor.getType(), XControlPoint.Type.CONTROL_POINT))) {
        boolean _selected = postsuccessor.getSelected();
        boolean _not = (!_selected);
        if (_not) {
          double _layoutX = successor.getLayoutX();
          double _layoutX_1 = current.getLayoutX();
          final double dx0 = (_layoutX - _layoutX_1);
          double _layoutY = successor.getLayoutY();
          double _layoutY_1 = current.getLayoutY();
          final double dy0 = (_layoutY - _layoutY_1);
          final double norm0 = Point2DExtensions.norm(dx0, dy0);
          if ((norm0 > NumberExpressionExtensions.EPSILON)) {
            double _layoutX_2 = postsuccessor.getLayoutX();
            double _layoutX_3 = successor.getLayoutX();
            final double dx1 = (_layoutX_2 - _layoutX_3);
            double _layoutY_2 = postsuccessor.getLayoutY();
            double _layoutY_3 = successor.getLayoutY();
            final double dy1 = (_layoutY_2 - _layoutY_3);
            double _norm = Point2DExtensions.norm(dx1, dy1);
            final double scale = (_norm / norm0);
            double _layoutX_4 = successor.getLayoutX();
            double _plus = (_layoutX_4 + (scale * dx0));
            postsuccessor.setLayoutX(_plus);
            double _layoutY_4 = successor.getLayoutY();
            double _plus_1 = (_layoutY_4 + (scale * dy0));
            postsuccessor.setLayoutY(_plus_1);
          }
          this.adjustRight((index + 2), siblings, moveDeltaX, moveDeltaY);
        } else {
          boolean _selected_1 = successor.getSelected();
          boolean _not_1 = (!_selected_1);
          if (_not_1) {
            double _layoutX_5 = successor.getLayoutX();
            double _plus_2 = (_layoutX_5 + (0.5 * moveDeltaX));
            successor.setLayoutX(_plus_2);
            double _layoutY_5 = successor.getLayoutY();
            double _plus_3 = (_layoutY_5 + (0.5 * moveDeltaY));
            successor.setLayoutY(_plus_3);
          }
        }
      }
    }
  }
  
  protected void updateDangling(final int index, final List<XControlPoint> siblings) {
    XConnection _connection = this.getConnection();
    XConnection.Kind _kind = _connection.getKind();
    boolean _equals = Objects.equal(_kind, XConnection.Kind.POLYLINE);
    if (_equals) {
      final XControlPoint predecessor = siblings.get((index - 1));
      final XControlPoint successor = siblings.get((index + 1));
      double _layoutX = predecessor.getLayoutX();
      double _layoutY = predecessor.getLayoutY();
      XControlPoint _host = this.getHost();
      double _layoutX_1 = _host.getLayoutX();
      XControlPoint _host_1 = this.getHost();
      double _layoutY_1 = _host_1.getLayoutY();
      double _layoutX_2 = successor.getLayoutX();
      double _layoutY_2 = successor.getLayoutY();
      boolean _areOnSameLine = Point2DExtensions.areOnSameLine(_layoutX, _layoutY, _layoutX_1, _layoutY_1, _layoutX_2, _layoutY_2);
      if (_areOnSameLine) {
        XControlPoint _host_2 = this.getHost();
        _host_2.setType(XControlPoint.Type.DANGLING);
      } else {
        XControlPoint _host_3 = this.getHost();
        _host_3.setType(XControlPoint.Type.INTERPOLATED);
      }
    }
  }
  
  protected ObservableList<XControlPoint> getSiblings() {
    XConnection _connection = this.getConnection();
    ObservableList<XControlPoint> _controlPoints = null;
    if (_connection!=null) {
      _controlPoints=_connection.getControlPoints();
    }
    return _controlPoints;
  }
  
  protected XConnection getConnection() {
    Object _xblockexpression = null;
    {
      XControlPoint _host = this.getHost();
      Parent _parent = _host.getParent();
      XShape _containerShape = null;
      if (_parent!=null) {
        _containerShape=CoreExtensions.getContainerShape(_parent);
      }
      final XShape containerShape = _containerShape;
      Object _xifexpression = null;
      if ((containerShape instanceof XConnection)) {
        return ((XConnection)containerShape);
      } else {
        _xifexpression = null;
      }
      _xblockexpression = _xifexpression;
    }
    return ((XConnection)_xblockexpression);
  }
  
  private Map<XControlPoint, Point2D> initialPositions;
  
  @Override
  public void startDrag(final double screenX, final double screenY) {
    super.startDrag(screenX, screenY);
    ObservableList<XControlPoint> _siblings = this.getSiblings();
    final Function<XControlPoint, Point2D> _function = (XControlPoint it) -> {
      double _layoutX = it.getLayoutX();
      double _layoutY = it.getLayoutY();
      return new Point2D(_layoutX, _layoutY);
    };
    ImmutableMap<XControlPoint, Point2D> _map = Maps.<XControlPoint, Point2D>toMap(_siblings, _function);
    this.initialPositions = _map;
  }
  
  @Override
  protected AnimationCommand createMoveCommand() {
    ParallelAnimationCommand _xblockexpression = null;
    {
      final ParallelAnimationCommand pac = new ParallelAnimationCommand();
      Set<Map.Entry<XControlPoint, Point2D>> _entrySet = this.initialPositions.entrySet();
      final Consumer<Map.Entry<XControlPoint, Point2D>> _function = (Map.Entry<XControlPoint, Point2D> it) -> {
        if (((it.getKey().getLayoutX() != it.getValue().getX()) || (it.getKey().getLayoutY() != it.getValue().getY()))) {
          XControlPoint _key = it.getKey();
          Point2D _value = it.getValue();
          double _x = _value.getX();
          Point2D _value_1 = it.getValue();
          double _y = _value_1.getY();
          XControlPoint _key_1 = it.getKey();
          double _layoutX = _key_1.getLayoutX();
          XControlPoint _key_2 = it.getKey();
          double _layoutY = _key_2.getLayoutY();
          MoveCommand _moveCommand = new MoveCommand(_key, _x, _y, _layoutX, _layoutY);
          pac.operator_add(_moveCommand);
        }
      };
      _entrySet.forEach(_function);
      _xblockexpression = pac;
    }
    return _xblockexpression;
  }
}
