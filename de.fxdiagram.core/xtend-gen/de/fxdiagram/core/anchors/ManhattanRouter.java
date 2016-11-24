package de.fxdiagram.core.anchors;

import com.google.common.base.Objects;
import de.fxdiagram.core.XConnection;
import de.fxdiagram.core.XControlPoint;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.anchors.ArrowHead;
import de.fxdiagram.core.anchors.PointsOnEdge;
import de.fxdiagram.core.behavior.MoveBehavior;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class ManhattanRouter {
  private final int STANDARD_DISTANCE = 20;
  
  private XConnection connection;
  
  private PointsOnEdge sourceRect;
  
  private PointsOnEdge targetRect;
  
  @Accessors
  private boolean reroutingEnabled = false;
  
  public ManhattanRouter(final XConnection connection) {
    this.connection = connection;
  }
  
  public void calculatePoints() {
    if (((this.connection.getControlPoints().size() == 0) && (!Objects.equal(this.sourceRect, null)))) {
      return;
    }
    XNode _source = this.connection.getSource();
    final PointsOnEdge newSourceRect = new PointsOnEdge(_source);
    XNode _target = this.connection.getTarget();
    final PointsOnEdge newTargetRect = new PointsOnEdge(_target);
    if ((((!Objects.equal(this.sourceRect, null)) && (!Objects.equal(this.targetRect, null))) && IterableExtensions.<XControlPoint>exists(this.connection.getControlPoints(), ((Function1<XControlPoint, Boolean>) (XControlPoint it) -> {
      boolean _or = false;
      boolean _manuallyPlaced = it.getManuallyPlaced();
      if (_manuallyPlaced) {
        _or = true;
      } else {
        MoveBehavior _behavior = it.<MoveBehavior>getBehavior(MoveBehavior.class);
        boolean _hasMoved = false;
        if (_behavior!=null) {
          _hasMoved=_behavior.hasMoved();
        }
        _or = _hasMoved;
      }
      return Boolean.valueOf(_or);
    })))) {
      this.sourceRect = newSourceRect;
      this.targetRect = newTargetRect;
      ObservableList<XControlPoint> _controlPoints = this.connection.getControlPoints();
      XControlPoint _head = IterableExtensions.<XControlPoint>head(_controlPoints);
      this.partiallyRerouteIfNecessary(this.sourceRect, _head, true);
      ObservableList<XControlPoint> _controlPoints_1 = this.connection.getControlPoints();
      XControlPoint _last = IterableExtensions.<XControlPoint>last(_controlPoints_1);
      this.partiallyRerouteIfNecessary(this.targetRect, _last, false);
      return;
    } else {
      this.sourceRect = newSourceRect;
      this.targetRect = newTargetRect;
      final ArrayList<XControlPoint> newControlPoints = this.getDefaultPoints();
      ObservableList<XControlPoint> _controlPoints_2 = this.connection.getControlPoints();
      _controlPoints_2.setAll(newControlPoints);
      this.reroutingEnabled = true;
    }
  }
  
  public ArrayList<XControlPoint> getDefaultPoints() {
    final Pair<Side, Side> connectionDir = this.getConnectionDirection();
    Side _key = connectionDir.getKey();
    Side _value = connectionDir.getValue();
    final ArrayList<Point2D> points = this.doRecalculatePoints(_key, _value);
    ArrowHead _sourceArrowHead = this.connection.getSourceArrowHead();
    boolean _notEquals = (!Objects.equal(_sourceArrowHead, null));
    if (_notEquals) {
      ArrowHead _sourceArrowHead_1 = this.connection.getSourceArrowHead();
      Point2D _get = points.get(1);
      double _x = _get.getX();
      Point2D _get_1 = points.get(1);
      double _y = _get_1.getY();
      Point2D _head = IterableExtensions.<Point2D>head(points);
      Point2D _correctAnchor = _sourceArrowHead_1.correctAnchor(_x, _y, _head);
      points.set(0, _correctAnchor);
    }
    ArrowHead _targetArrowHead = this.connection.getTargetArrowHead();
    boolean _notEquals_1 = (!Objects.equal(_targetArrowHead, null));
    if (_notEquals_1) {
      int _size = points.size();
      int _minus = (_size - 1);
      ArrowHead _targetArrowHead_1 = this.connection.getTargetArrowHead();
      int _size_1 = points.size();
      int _minus_1 = (_size_1 - 2);
      Point2D _get_2 = points.get(_minus_1);
      double _x_1 = _get_2.getX();
      int _size_2 = points.size();
      int _minus_2 = (_size_2 - 2);
      Point2D _get_3 = points.get(_minus_2);
      double _y_1 = _get_3.getY();
      Point2D _last = IterableExtensions.<Point2D>last(points);
      Point2D _correctAnchor_1 = _targetArrowHead_1.correctAnchor(_x_1, _y_1, _last);
      points.set(_minus, _correctAnchor_1);
    }
    final ArrayList<XControlPoint> newControlPoints = CollectionLiterals.<XControlPoint>newArrayList();
    final Procedure2<Point2D, Integer> _function = (Point2D point, Integer i) -> {
      XControlPoint _xControlPoint = new XControlPoint();
      final Procedure1<XControlPoint> _function_1 = (XControlPoint it) -> {
        double _x_2 = point.getX();
        it.setLayoutX(_x_2);
        double _y_2 = point.getY();
        it.setLayoutY(_y_2);
        XControlPoint.Type _xifexpression = null;
        if ((((i).intValue() == 0) || ((i).intValue() == (points.size() - 1)))) {
          _xifexpression = XControlPoint.Type.ANCHOR;
        } else {
          _xifexpression = XControlPoint.Type.INTERPOLATED;
        }
        it.setType(_xifexpression);
      };
      XControlPoint _doubleArrow = ObjectExtensions.<XControlPoint>operator_doubleArrow(_xControlPoint, _function_1);
      newControlPoints.add(_doubleArrow);
    };
    IterableExtensions.<Point2D>forEach(points, _function);
    XControlPoint _head_1 = IterableExtensions.<XControlPoint>head(newControlPoints);
    Side _key_1 = connectionDir.getKey();
    _head_1.setSide(_key_1);
    XControlPoint _last_1 = IterableExtensions.<XControlPoint>last(newControlPoints);
    Side _value_1 = connectionDir.getValue();
    _last_1.setSide(_value_1);
    return newControlPoints;
  }
  
  protected void partiallyRerouteIfNecessary(final PointsOnEdge connected, final XControlPoint anchor, final boolean isSource) {
    final Side lastSide = anchor.getSide();
    XControlPoint _xifexpression = null;
    if (isSource) {
      ObservableList<XControlPoint> _controlPoints = this.connection.getControlPoints();
      _xifexpression = _controlPoints.get(1);
    } else {
      ObservableList<XControlPoint> _controlPoints_1 = this.connection.getControlPoints();
      ObservableList<XControlPoint> _controlPoints_2 = this.connection.getControlPoints();
      int _size = _controlPoints_2.size();
      int _minus = (_size - 2);
      _xifexpression = _controlPoints_1.get(_minus);
    }
    final XControlPoint referencePoint = _xifexpression;
    XControlPoint _xifexpression_1 = null;
    ObservableList<XControlPoint> _controlPoints_3 = this.connection.getControlPoints();
    int _size_1 = _controlPoints_3.size();
    boolean _greaterThan = (_size_1 > 3);
    if (_greaterThan) {
      XControlPoint _xifexpression_2 = null;
      if (isSource) {
        ObservableList<XControlPoint> _controlPoints_4 = this.connection.getControlPoints();
        _xifexpression_2 = _controlPoints_4.get(2);
      } else {
        ObservableList<XControlPoint> _controlPoints_5 = this.connection.getControlPoints();
        ObservableList<XControlPoint> _controlPoints_6 = this.connection.getControlPoints();
        int _size_2 = _controlPoints_6.size();
        int _minus_1 = (_size_2 - 3);
        _xifexpression_2 = _controlPoints_5.get(_minus_1);
      }
      _xifexpression_1 = _xifexpression_2;
    } else {
      _xifexpression_1 = null;
    }
    final XControlPoint refPoint2 = _xifexpression_1;
    double _xifexpression_3 = (double) 0;
    if (isSource) {
      ArrowHead _sourceArrowHead = this.connection.getSourceArrowHead();
      double _lineCut = 0.0;
      if (_sourceArrowHead!=null) {
        _lineCut=_sourceArrowHead.getLineCut();
      }
      _xifexpression_3 = _lineCut;
    } else {
      ArrowHead _targetArrowHead = this.connection.getTargetArrowHead();
      double _lineCut_1 = 0.0;
      if (_targetArrowHead!=null) {
        _lineCut_1=_targetArrowHead.getLineCut();
      }
      _xifexpression_3 = _lineCut_1;
    }
    final double lineCut = _xifexpression_3;
    final boolean doReroute = ((this.reroutingEnabled && (!referencePoint.layoutXProperty().isBound())) && (!referencePoint.layoutYProperty().isBound()));
    if (lastSide != null) {
      switch (lastSide) {
        case TOP:
        case BOTTOM:
          if (doReroute) {
            double _layoutX = anchor.getLayoutX();
            Point2D _get = connected.get(Side.LEFT);
            double _x = _get.getX();
            boolean _lessThan = (_layoutX < _x);
            if (_lessThan) {
              this.addCorner(connected, anchor, isSource, Side.LEFT);
              return;
            } else {
              double _layoutX_1 = anchor.getLayoutX();
              Point2D _get_1 = connected.get(Side.RIGHT);
              double _x_1 = _get_1.getX();
              boolean _greaterThan_1 = (_layoutX_1 > _x_1);
              if (_greaterThan_1) {
                this.addCorner(connected, anchor, isSource, Side.RIGHT);
                return;
              } else {
                if ((((!Objects.equal(refPoint2, null)) && (refPoint2.getLayoutY() > connected.get(Side.TOP).getY())) && (refPoint2.getLayoutY() < connected.get(Side.BOTTOM).getY()))) {
                  double _layoutX_2 = refPoint2.getLayoutX();
                  Point2D _get_2 = connected.get(lastSide);
                  double _x_2 = _get_2.getX();
                  boolean _lessThan_1 = (_layoutX_2 < _x_2);
                  if (_lessThan_1) {
                    this.removeCorner(connected, anchor, isSource, Side.LEFT);
                  } else {
                    this.removeCorner(connected, anchor, isSource, Side.RIGHT);
                  }
                  return;
                } else {
                  if ((Objects.equal(lastSide, Side.TOP) && ((referencePoint.getLayoutY() > (connected.get(Side.BOTTOM).getY() + lineCut)) || (Math.abs((referencePoint.getLayoutY() - connected.get(Side.TOP).getY())) < lineCut)))) {
                    this.switchSide(connected, anchor, isSource, Side.BOTTOM);
                    return;
                  } else {
                    if ((Objects.equal(lastSide, Side.BOTTOM) && ((referencePoint.getLayoutY() < (connected.get(Side.TOP).getY() - lineCut)) || (Math.abs((referencePoint.getLayoutY() - connected.get(Side.BOTTOM).getY())) < lineCut)))) {
                      this.switchSide(connected, anchor, isSource, Side.TOP);
                      return;
                    }
                  }
                }
              }
            }
          }
          double _layoutX_3 = referencePoint.getLayoutX();
          Point2D _get_3 = connected.get(lastSide);
          double _y = _get_3.getY();
          Point2D _point2D = new Point2D(_layoutX_3, _y);
          this.setAnchorPoint(connected, anchor, _point2D, isSource, lastSide, referencePoint);
          break;
        case LEFT:
        case RIGHT:
          if (doReroute) {
            double _layoutY = anchor.getLayoutY();
            Point2D _get_4 = connected.get(Side.TOP);
            double _y_1 = _get_4.getY();
            boolean _lessThan_2 = (_layoutY < _y_1);
            if (_lessThan_2) {
              this.addCorner(connected, anchor, isSource, Side.TOP);
              return;
            } else {
              double _layoutY_1 = anchor.getLayoutY();
              Point2D _get_5 = connected.get(Side.BOTTOM);
              double _y_2 = _get_5.getY();
              boolean _greaterThan_2 = (_layoutY_1 > _y_2);
              if (_greaterThan_2) {
                this.addCorner(connected, anchor, isSource, Side.BOTTOM);
                return;
              } else {
                if ((((!Objects.equal(refPoint2, null)) && (refPoint2.getLayoutX() > connected.get(Side.LEFT).getX())) && (refPoint2.getLayoutX() < connected.get(Side.RIGHT).getX()))) {
                  double _layoutY_2 = refPoint2.getLayoutY();
                  Point2D _get_6 = connected.get(lastSide);
                  double _y_3 = _get_6.getY();
                  boolean _lessThan_3 = (_layoutY_2 < _y_3);
                  if (_lessThan_3) {
                    this.removeCorner(connected, anchor, isSource, Side.TOP);
                  } else {
                    this.removeCorner(connected, anchor, isSource, Side.BOTTOM);
                  }
                  return;
                } else {
                  if ((Objects.equal(lastSide, Side.LEFT) && ((referencePoint.getLayoutX() > (connected.get(Side.RIGHT).getX() + lineCut)) || (Math.abs((referencePoint.getLayoutX() - connected.get(Side.LEFT).getX())) < lineCut)))) {
                    this.switchSide(connected, anchor, isSource, Side.RIGHT);
                    return;
                  } else {
                    if ((Objects.equal(lastSide, Side.RIGHT) && ((referencePoint.getLayoutX() < (connected.get(Side.LEFT).getX() - lineCut)) || (Math.abs((referencePoint.getLayoutX() - connected.get(Side.RIGHT).getX())) < lineCut)))) {
                      this.switchSide(connected, anchor, isSource, Side.LEFT);
                      return;
                    }
                  }
                }
              }
            }
          }
          Point2D _get_7 = connected.get(lastSide);
          double _x_3 = _get_7.getX();
          double _layoutY_3 = referencePoint.getLayoutY();
          Point2D _point2D_1 = new Point2D(_x_3, _layoutY_3);
          this.setAnchorPoint(connected, anchor, _point2D_1, isSource, lastSide, referencePoint);
          break;
        default:
          break;
      }
    }
  }
  
  protected void switchSide(final PointsOnEdge connected, final XControlPoint anchor, final boolean isSource, final Side newSide) {
    XControlPoint _xifexpression = null;
    if (isSource) {
      ObservableList<XControlPoint> _controlPoints = this.connection.getControlPoints();
      _xifexpression = _controlPoints.get(1);
    } else {
      ObservableList<XControlPoint> _controlPoints_1 = this.connection.getControlPoints();
      ObservableList<XControlPoint> _controlPoints_2 = this.connection.getControlPoints();
      int _size = _controlPoints_2.size();
      int _minus = (_size - 2);
      _xifexpression = _controlPoints_1.get(_minus);
    }
    final XControlPoint referencePoint = _xifexpression;
    Point2D _get = connected.get(newSide);
    this.setAnchorPoint(connected, anchor, _get, isSource, newSide, referencePoint);
  }
  
  protected void addCorner(final PointsOnEdge connected, final XControlPoint anchor, final boolean isSource, final Side newSide) {
    int _xifexpression = (int) 0;
    if (isSource) {
      _xifexpression = 1;
    } else {
      ObservableList<XControlPoint> _controlPoints = this.connection.getControlPoints();
      int _size = _controlPoints.size();
      _xifexpression = (_size - 1);
    }
    final int index = _xifexpression;
    final double cpX = anchor.getLayoutX();
    final double cpY = anchor.getLayoutY();
    XControlPoint _xControlPoint = new XControlPoint();
    final Procedure1<XControlPoint> _function = (XControlPoint it) -> {
      double _xifexpression_1 = (double) 0;
      boolean _isVertical = newSide.isVertical();
      if (_isVertical) {
        _xifexpression_1 = cpX;
      } else {
        Point2D _get = connected.get(newSide);
        _xifexpression_1 = _get.getX();
      }
      it.setLayoutX(_xifexpression_1);
      double _xifexpression_2 = (double) 0;
      boolean _isVertical_1 = newSide.isVertical();
      if (_isVertical_1) {
        Point2D _get_1 = connected.get(newSide);
        _xifexpression_2 = _get_1.getY();
      } else {
        _xifexpression_2 = cpY;
      }
      it.setLayoutY(_xifexpression_2);
      it.setType(XControlPoint.Type.INTERPOLATED);
    };
    final XControlPoint newPoint = ObjectExtensions.<XControlPoint>operator_doubleArrow(_xControlPoint, _function);
    ObservableList<XControlPoint> _controlPoints_1 = this.connection.getControlPoints();
    _controlPoints_1.add(index, newPoint);
    Point2D _get = connected.get(newSide);
    this.setAnchorPoint(connected, anchor, _get, isSource, newSide, newPoint);
  }
  
  protected void removeCorner(final PointsOnEdge connected, final XControlPoint anchor, final boolean isSource, final Side newSide) {
    XControlPoint _xifexpression = null;
    if (isSource) {
      XControlPoint _xblockexpression = null;
      {
        ObservableList<XControlPoint> _controlPoints = this.connection.getControlPoints();
        _controlPoints.remove(1);
        ObservableList<XControlPoint> _controlPoints_1 = this.connection.getControlPoints();
        _xblockexpression = _controlPoints_1.get(1);
      }
      _xifexpression = _xblockexpression;
    } else {
      XControlPoint _xblockexpression_1 = null;
      {
        ObservableList<XControlPoint> _controlPoints = this.connection.getControlPoints();
        ObservableList<XControlPoint> _controlPoints_1 = this.connection.getControlPoints();
        int _size = _controlPoints_1.size();
        int _minus = (_size - 2);
        _controlPoints.remove(_minus);
        ObservableList<XControlPoint> _controlPoints_2 = this.connection.getControlPoints();
        ObservableList<XControlPoint> _controlPoints_3 = this.connection.getControlPoints();
        int _size_1 = _controlPoints_3.size();
        int _minus_1 = (_size_1 - 2);
        _xblockexpression_1 = _controlPoints_2.get(_minus_1);
      }
      _xifexpression = _xblockexpression_1;
    }
    final XControlPoint referencePoint = _xifexpression;
    Point2D _xifexpression_1 = null;
    boolean _isHorizontal = newSide.isHorizontal();
    if (_isHorizontal) {
      double _layoutX = referencePoint.getLayoutX();
      Point2D _get = connected.get(newSide);
      double _y = _get.getY();
      _xifexpression_1 = new Point2D(_layoutX, _y);
    } else {
      Point2D _get_1 = connected.get(newSide);
      double _x = _get_1.getX();
      double _layoutY = referencePoint.getLayoutY();
      _xifexpression_1 = new Point2D(_x, _layoutY);
    }
    final Point2D anchorPoint = _xifexpression_1;
    this.setAnchorPoint(connected, anchor, anchorPoint, isSource, newSide, referencePoint);
  }
  
  protected void setAnchorPoint(final PointsOnEdge connected, final XControlPoint anchor, final Point2D newAnchorPoint, final boolean isSource, final Side newSide, final XControlPoint referencePoint) {
    Point2D anchorPoint = newAnchorPoint;
    if (isSource) {
      ArrowHead _sourceArrowHead = this.connection.getSourceArrowHead();
      boolean _notEquals = (!Objects.equal(_sourceArrowHead, null));
      if (_notEquals) {
        ArrowHead _sourceArrowHead_1 = this.connection.getSourceArrowHead();
        double _layoutX = referencePoint.getLayoutX();
        double _layoutY = referencePoint.getLayoutY();
        Point2D _correctAnchor = _sourceArrowHead_1.correctAnchor(_layoutX, _layoutY, newAnchorPoint);
        anchorPoint = _correctAnchor;
      }
    } else {
      ArrowHead _targetArrowHead = this.connection.getTargetArrowHead();
      boolean _notEquals_1 = (!Objects.equal(_targetArrowHead, null));
      if (_notEquals_1) {
        ArrowHead _targetArrowHead_1 = this.connection.getTargetArrowHead();
        double _layoutX_1 = referencePoint.getLayoutX();
        double _layoutY_1 = referencePoint.getLayoutY();
        Point2D _correctAnchor_1 = _targetArrowHead_1.correctAnchor(_layoutX_1, _layoutY_1, newAnchorPoint);
        anchorPoint = _correctAnchor_1;
      }
    }
    anchor.setSide(newSide);
    double _x = anchorPoint.getX();
    anchor.setLayoutX(_x);
    double _y = anchorPoint.getY();
    anchor.setLayoutY(_y);
  }
  
  protected ArrayList<Point2D> doRecalculatePoints(final Side sourceSide, final Side targetSide) {
    final ArrayList<Point2D> points = CollectionLiterals.<Point2D>newArrayList();
    final Point2D startPoint = this.sourceRect.get(sourceSide);
    Point2D endPoint = this.targetRect.get(targetSide);
    if (sourceSide != null) {
      switch (sourceSide) {
        case RIGHT:
          points.add(startPoint);
          if (targetSide != null) {
            switch (targetSide) {
              case BOTTOM:
                double _x = endPoint.getX();
                double _y = startPoint.getY();
                Point2D _point2D = new Point2D(_x, _y);
                points.add(_point2D);
                break;
              case TOP:
                double _x_1 = endPoint.getX();
                double _y_1 = startPoint.getY();
                Point2D _point2D_1 = new Point2D(_x_1, _y_1);
                points.add(_point2D_1);
                break;
              case RIGHT:
                double _x_2 = startPoint.getX();
                double _x_3 = endPoint.getX();
                double _max = Math.max(_x_2, _x_3);
                double _plus = (_max + (1.5 * this.STANDARD_DISTANCE));
                double _y_2 = startPoint.getY();
                Point2D _point2D_2 = new Point2D(_plus, _y_2);
                points.add(_point2D_2);
                double _x_4 = startPoint.getX();
                double _x_5 = endPoint.getX();
                double _max_1 = Math.max(_x_4, _x_5);
                double _plus_1 = (_max_1 + (1.5 * this.STANDARD_DISTANCE));
                double _y_3 = endPoint.getY();
                Point2D _point2D_3 = new Point2D(_plus_1, _y_3);
                points.add(_point2D_3);
                break;
              case LEFT:
                double _y_4 = endPoint.getY();
                double _y_5 = startPoint.getY();
                boolean _notEquals = (_y_4 != _y_5);
                if (_notEquals) {
                  double _x_6 = startPoint.getX();
                  double _x_7 = endPoint.getX();
                  double _plus_2 = (_x_6 + _x_7);
                  double _divide = (_plus_2 / 2);
                  double _y_6 = startPoint.getY();
                  Point2D _point2D_4 = new Point2D(_divide, _y_6);
                  points.add(_point2D_4);
                  double _x_8 = startPoint.getX();
                  double _x_9 = endPoint.getX();
                  double _plus_3 = (_x_8 + _x_9);
                  double _divide_1 = (_plus_3 / 2);
                  double _y_7 = endPoint.getY();
                  Point2D _point2D_5 = new Point2D(_divide_1, _y_7);
                  points.add(_point2D_5);
                }
                break;
              default:
                break;
            }
          }
          break;
        case LEFT:
          points.add(startPoint);
          if (targetSide != null) {
            switch (targetSide) {
              case BOTTOM:
                double _x_10 = endPoint.getX();
                double _y_8 = startPoint.getY();
                Point2D _point2D_6 = new Point2D(_x_10, _y_8);
                points.add(_point2D_6);
                break;
              case TOP:
                double _x_11 = endPoint.getX();
                double _y_9 = startPoint.getY();
                Point2D _point2D_7 = new Point2D(_x_11, _y_9);
                points.add(_point2D_7);
                break;
              default:
                {
                  Point2D _get = this.targetRect.get(Side.RIGHT);
                  endPoint = _get;
                  double _y_10 = endPoint.getY();
                  double _y_11 = startPoint.getY();
                  boolean _notEquals_1 = (_y_10 != _y_11);
                  if (_notEquals_1) {
                    double _x_12 = startPoint.getX();
                    double _x_13 = endPoint.getX();
                    double _plus_4 = (_x_12 + _x_13);
                    double _divide_2 = (_plus_4 / 2);
                    double _y_12 = startPoint.getY();
                    Point2D _point2D_8 = new Point2D(_divide_2, _y_12);
                    points.add(_point2D_8);
                    double _x_14 = startPoint.getX();
                    double _x_15 = endPoint.getX();
                    double _plus_5 = (_x_14 + _x_15);
                    double _divide_3 = (_plus_5 / 2);
                    double _y_13 = endPoint.getY();
                    Point2D _point2D_9 = new Point2D(_divide_3, _y_13);
                    points.add(_point2D_9);
                  }
                }
                break;
            }
          } else {
            {
              Point2D _get = this.targetRect.get(Side.RIGHT);
              endPoint = _get;
              double _y_10 = endPoint.getY();
              double _y_11 = startPoint.getY();
              boolean _notEquals_1 = (_y_10 != _y_11);
              if (_notEquals_1) {
                double _x_12 = startPoint.getX();
                double _x_13 = endPoint.getX();
                double _plus_4 = (_x_12 + _x_13);
                double _divide_2 = (_plus_4 / 2);
                double _y_12 = startPoint.getY();
                Point2D _point2D_8 = new Point2D(_divide_2, _y_12);
                points.add(_point2D_8);
                double _x_14 = startPoint.getX();
                double _x_15 = endPoint.getX();
                double _plus_5 = (_x_14 + _x_15);
                double _divide_3 = (_plus_5 / 2);
                double _y_13 = endPoint.getY();
                Point2D _point2D_9 = new Point2D(_divide_3, _y_13);
                points.add(_point2D_9);
              }
            }
          }
          break;
        case TOP:
          points.add(startPoint);
          if (targetSide != null) {
            switch (targetSide) {
              case RIGHT:
                double _x_12 = endPoint.getX();
                double _x_13 = startPoint.getX();
                double _minus = (_x_12 - _x_13);
                boolean _greaterThan = (_minus > 0);
                if (_greaterThan) {
                  double _x_14 = startPoint.getX();
                  double _y_10 = startPoint.getY();
                  double _minus_1 = (_y_10 - this.STANDARD_DISTANCE);
                  Point2D _point2D_8 = new Point2D(_x_14, _minus_1);
                  points.add(_point2D_8);
                  double _x_15 = endPoint.getX();
                  double _plus_4 = (_x_15 + (1.5 * this.STANDARD_DISTANCE));
                  double _y_11 = startPoint.getY();
                  double _minus_2 = (_y_11 - this.STANDARD_DISTANCE);
                  Point2D _point2D_9 = new Point2D(_plus_4, _minus_2);
                  points.add(_point2D_9);
                  double _x_16 = endPoint.getX();
                  double _plus_5 = (_x_16 + (1.5 * this.STANDARD_DISTANCE));
                  double _y_12 = endPoint.getY();
                  Point2D _point2D_10 = new Point2D(_plus_5, _y_12);
                  points.add(_point2D_10);
                } else {
                  double _x_17 = startPoint.getX();
                  double _y_13 = endPoint.getY();
                  Point2D _point2D_11 = new Point2D(_x_17, _y_13);
                  points.add(_point2D_11);
                }
                break;
              case LEFT:
                double _x_18 = endPoint.getX();
                double _x_19 = startPoint.getX();
                double _minus_3 = (_x_18 - _x_19);
                boolean _lessThan = (_minus_3 < 0);
                if (_lessThan) {
                  double _x_20 = startPoint.getX();
                  double _y_14 = startPoint.getY();
                  double _minus_4 = (_y_14 - this.STANDARD_DISTANCE);
                  Point2D _point2D_12 = new Point2D(_x_20, _minus_4);
                  points.add(_point2D_12);
                  double _x_21 = endPoint.getX();
                  double _minus_5 = (_x_21 - (1.5 * this.STANDARD_DISTANCE));
                  double _y_15 = startPoint.getY();
                  double _minus_6 = (_y_15 - this.STANDARD_DISTANCE);
                  Point2D _point2D_13 = new Point2D(_minus_5, _minus_6);
                  points.add(_point2D_13);
                  double _x_22 = endPoint.getX();
                  double _minus_7 = (_x_22 - (1.5 * this.STANDARD_DISTANCE));
                  double _y_16 = endPoint.getY();
                  Point2D _point2D_14 = new Point2D(_minus_7, _y_16);
                  points.add(_point2D_14);
                } else {
                  double _x_23 = startPoint.getX();
                  double _y_17 = endPoint.getY();
                  Point2D _point2D_15 = new Point2D(_x_23, _y_17);
                  points.add(_point2D_15);
                }
                break;
              case TOP:
                double _x_24 = startPoint.getX();
                double _y_18 = startPoint.getY();
                double _y_19 = endPoint.getY();
                double _min = Math.min(_y_18, _y_19);
                double _minus_8 = (_min - (1.5 * this.STANDARD_DISTANCE));
                Point2D _point2D_16 = new Point2D(_x_24, _minus_8);
                points.add(_point2D_16);
                double _x_25 = endPoint.getX();
                double _y_20 = startPoint.getY();
                double _y_21 = endPoint.getY();
                double _min_1 = Math.min(_y_20, _y_21);
                double _minus_9 = (_min_1 - (1.5 * this.STANDARD_DISTANCE));
                Point2D _point2D_17 = new Point2D(_x_25, _minus_9);
                points.add(_point2D_17);
                break;
              case BOTTOM:
                double _x_26 = endPoint.getX();
                double _x_27 = startPoint.getX();
                boolean _notEquals_1 = (_x_26 != _x_27);
                if (_notEquals_1) {
                  double _x_28 = startPoint.getX();
                  double _y_22 = startPoint.getY();
                  double _y_23 = endPoint.getY();
                  double _plus_6 = (_y_22 + _y_23);
                  double _divide_2 = (_plus_6 / 2);
                  Point2D _point2D_18 = new Point2D(_x_28, _divide_2);
                  points.add(_point2D_18);
                  double _x_29 = endPoint.getX();
                  double _y_24 = startPoint.getY();
                  double _y_25 = endPoint.getY();
                  double _plus_7 = (_y_24 + _y_25);
                  double _divide_3 = (_plus_7 / 2);
                  Point2D _point2D_19 = new Point2D(_x_29, _divide_3);
                  points.add(_point2D_19);
                }
                break;
              default:
                break;
            }
          }
          break;
        case BOTTOM:
          points.add(startPoint);
          if (targetSide != null) {
            switch (targetSide) {
              case RIGHT:
                double _x_30 = endPoint.getX();
                double _x_31 = startPoint.getX();
                double _minus_10 = (_x_30 - _x_31);
                boolean _greaterThan_1 = (_minus_10 > 0);
                if (_greaterThan_1) {
                  double _x_32 = startPoint.getX();
                  double _y_26 = startPoint.getY();
                  double _plus_8 = (_y_26 + this.STANDARD_DISTANCE);
                  Point2D _point2D_20 = new Point2D(_x_32, _plus_8);
                  points.add(_point2D_20);
                  double _x_33 = endPoint.getX();
                  double _plus_9 = (_x_33 + (1.5 * this.STANDARD_DISTANCE));
                  double _y_27 = startPoint.getY();
                  double _plus_10 = (_y_27 + this.STANDARD_DISTANCE);
                  Point2D _point2D_21 = new Point2D(_plus_9, _plus_10);
                  points.add(_point2D_21);
                  double _x_34 = endPoint.getX();
                  double _plus_11 = (_x_34 + (1.5 * this.STANDARD_DISTANCE));
                  double _y_28 = endPoint.getY();
                  Point2D _point2D_22 = new Point2D(_plus_11, _y_28);
                  points.add(_point2D_22);
                } else {
                  double _x_35 = startPoint.getX();
                  double _y_29 = endPoint.getY();
                  Point2D _point2D_23 = new Point2D(_x_35, _y_29);
                  points.add(_point2D_23);
                }
                break;
              case LEFT:
                double _x_36 = endPoint.getX();
                double _x_37 = startPoint.getX();
                double _minus_11 = (_x_36 - _x_37);
                boolean _lessThan_1 = (_minus_11 < 0);
                if (_lessThan_1) {
                  double _x_38 = startPoint.getX();
                  double _y_30 = startPoint.getY();
                  double _plus_12 = (_y_30 + this.STANDARD_DISTANCE);
                  Point2D _point2D_24 = new Point2D(_x_38, _plus_12);
                  points.add(_point2D_24);
                  double _x_39 = endPoint.getX();
                  double _minus_12 = (_x_39 - (1.5 * this.STANDARD_DISTANCE));
                  double _y_31 = startPoint.getY();
                  double _plus_13 = (_y_31 + this.STANDARD_DISTANCE);
                  Point2D _point2D_25 = new Point2D(_minus_12, _plus_13);
                  points.add(_point2D_25);
                  double _x_40 = endPoint.getX();
                  double _minus_13 = (_x_40 - (1.5 * this.STANDARD_DISTANCE));
                  double _y_32 = endPoint.getY();
                  Point2D _point2D_26 = new Point2D(_minus_13, _y_32);
                  points.add(_point2D_26);
                } else {
                  double _x_41 = startPoint.getX();
                  double _y_33 = endPoint.getY();
                  Point2D _point2D_27 = new Point2D(_x_41, _y_33);
                  points.add(_point2D_27);
                }
                break;
              default:
                {
                  Point2D _get = this.targetRect.get(Side.TOP);
                  endPoint = _get;
                  double _x_42 = endPoint.getX();
                  double _x_43 = startPoint.getX();
                  boolean _notEquals_2 = (_x_42 != _x_43);
                  if (_notEquals_2) {
                    double _x_44 = startPoint.getX();
                    double _y_34 = startPoint.getY();
                    double _y_35 = endPoint.getY();
                    double _plus_14 = (_y_34 + _y_35);
                    double _divide_4 = (_plus_14 / 2);
                    Point2D _point2D_28 = new Point2D(_x_44, _divide_4);
                    points.add(_point2D_28);
                    double _x_45 = endPoint.getX();
                    double _y_36 = startPoint.getY();
                    double _y_37 = endPoint.getY();
                    double _plus_15 = (_y_36 + _y_37);
                    double _divide_5 = (_plus_15 / 2);
                    Point2D _point2D_29 = new Point2D(_x_45, _divide_5);
                    points.add(_point2D_29);
                  }
                }
                break;
            }
          } else {
            {
              Point2D _get = this.targetRect.get(Side.TOP);
              endPoint = _get;
              double _x_42 = endPoint.getX();
              double _x_43 = startPoint.getX();
              boolean _notEquals_2 = (_x_42 != _x_43);
              if (_notEquals_2) {
                double _x_44 = startPoint.getX();
                double _y_34 = startPoint.getY();
                double _y_35 = endPoint.getY();
                double _plus_14 = (_y_34 + _y_35);
                double _divide_4 = (_plus_14 / 2);
                Point2D _point2D_28 = new Point2D(_x_44, _divide_4);
                points.add(_point2D_28);
                double _x_45 = endPoint.getX();
                double _y_36 = startPoint.getY();
                double _y_37 = endPoint.getY();
                double _plus_15 = (_y_36 + _y_37);
                double _divide_5 = (_plus_15 / 2);
                Point2D _point2D_29 = new Point2D(_x_45, _divide_5);
                points.add(_point2D_29);
              }
            }
          }
          break;
        default:
          break;
      }
    }
    points.add(endPoint);
    return points;
  }
  
  protected Pair<Side, Side> getConnectionDirection() {
    Point2D sourcePoint = this.sourceRect.get(Side.RIGHT);
    Point2D targetPoint = this.targetRect.get(Side.LEFT);
    double _x = targetPoint.getX();
    double _x_1 = sourcePoint.getX();
    double _minus = (_x - _x_1);
    boolean _greaterThan = (_minus > this.STANDARD_DISTANCE);
    if (_greaterThan) {
      return Pair.<Side, Side>of(Side.RIGHT, Side.LEFT);
    }
    Point2D _get = this.sourceRect.get(Side.LEFT);
    sourcePoint = _get;
    Point2D _get_1 = this.targetRect.get(Side.RIGHT);
    targetPoint = _get_1;
    double _x_2 = sourcePoint.getX();
    double _x_3 = targetPoint.getX();
    double _minus_1 = (_x_2 - _x_3);
    boolean _greaterThan_1 = (_minus_1 > this.STANDARD_DISTANCE);
    if (_greaterThan_1) {
      return Pair.<Side, Side>of(Side.LEFT, Side.RIGHT);
    }
    Point2D _get_2 = this.sourceRect.get(Side.TOP);
    sourcePoint = _get_2;
    Point2D _get_3 = this.targetRect.get(Side.BOTTOM);
    targetPoint = _get_3;
    double _y = sourcePoint.getY();
    double _y_1 = targetPoint.getY();
    double _minus_2 = (_y - _y_1);
    boolean _greaterThan_2 = (_minus_2 > this.STANDARD_DISTANCE);
    if (_greaterThan_2) {
      return Pair.<Side, Side>of(Side.TOP, Side.BOTTOM);
    }
    Point2D _get_4 = this.sourceRect.get(Side.BOTTOM);
    sourcePoint = _get_4;
    Point2D _get_5 = this.targetRect.get(Side.TOP);
    targetPoint = _get_5;
    double _y_2 = targetPoint.getY();
    double _y_3 = sourcePoint.getY();
    double _minus_3 = (_y_2 - _y_3);
    boolean _greaterThan_3 = (_minus_3 > this.STANDARD_DISTANCE);
    if (_greaterThan_3) {
      return Pair.<Side, Side>of(Side.BOTTOM, Side.TOP);
    }
    Point2D _get_6 = this.sourceRect.get(Side.RIGHT);
    sourcePoint = _get_6;
    Point2D _get_7 = this.targetRect.get(Side.TOP);
    targetPoint = _get_7;
    if ((((targetPoint.getX() - sourcePoint.getX()) > (0.5 * this.STANDARD_DISTANCE)) && ((targetPoint.getY() - sourcePoint.getY()) > this.STANDARD_DISTANCE))) {
      return Pair.<Side, Side>of(Side.RIGHT, Side.TOP);
    }
    Point2D _get_8 = this.targetRect.get(Side.BOTTOM);
    targetPoint = _get_8;
    if ((((targetPoint.getX() - sourcePoint.getX()) > (0.5 * this.STANDARD_DISTANCE)) && ((sourcePoint.getY() - targetPoint.getY()) > this.STANDARD_DISTANCE))) {
      return Pair.<Side, Side>of(Side.RIGHT, Side.BOTTOM);
    }
    Point2D _get_9 = this.sourceRect.get(Side.LEFT);
    sourcePoint = _get_9;
    Point2D _get_10 = this.targetRect.get(Side.BOTTOM);
    targetPoint = _get_10;
    if ((((sourcePoint.getX() - targetPoint.getX()) > (0.5 * this.STANDARD_DISTANCE)) && ((sourcePoint.getY() - targetPoint.getY()) > this.STANDARD_DISTANCE))) {
      return Pair.<Side, Side>of(Side.LEFT, Side.BOTTOM);
    }
    Point2D _get_11 = this.targetRect.get(Side.TOP);
    targetPoint = _get_11;
    if ((((sourcePoint.getX() - targetPoint.getX()) > (0.5 * this.STANDARD_DISTANCE)) && ((targetPoint.getY() - sourcePoint.getY()) > this.STANDARD_DISTANCE))) {
      return Pair.<Side, Side>of(Side.LEFT, Side.TOP);
    }
    Point2D _get_12 = this.sourceRect.get(Side.TOP);
    sourcePoint = _get_12;
    Point2D _get_13 = this.targetRect.get(Side.RIGHT);
    targetPoint = _get_13;
    if ((((sourcePoint.getY() - targetPoint.getY()) > (0.5 * this.STANDARD_DISTANCE)) && ((sourcePoint.getX() - targetPoint.getX()) > this.STANDARD_DISTANCE))) {
      return Pair.<Side, Side>of(Side.TOP, Side.RIGHT);
    }
    Point2D _get_14 = this.targetRect.get(Side.LEFT);
    targetPoint = _get_14;
    if ((((sourcePoint.getY() - targetPoint.getY()) > (0.5 * this.STANDARD_DISTANCE)) && ((targetPoint.getX() - sourcePoint.getX()) > this.STANDARD_DISTANCE))) {
      return Pair.<Side, Side>of(Side.TOP, Side.LEFT);
    }
    Point2D _get_15 = this.sourceRect.get(Side.BOTTOM);
    sourcePoint = _get_15;
    Point2D _get_16 = this.targetRect.get(Side.RIGHT);
    targetPoint = _get_16;
    if ((((targetPoint.getY() - sourcePoint.getY()) > (0.5 * this.STANDARD_DISTANCE)) && ((sourcePoint.getX() - targetPoint.getX()) > this.STANDARD_DISTANCE))) {
      return Pair.<Side, Side>of(Side.BOTTOM, Side.RIGHT);
    }
    Point2D _get_17 = this.targetRect.get(Side.LEFT);
    targetPoint = _get_17;
    if ((((targetPoint.getY() - sourcePoint.getY()) > (0.5 * this.STANDARD_DISTANCE)) && ((targetPoint.getX() - sourcePoint.getX()) > this.STANDARD_DISTANCE))) {
      return Pair.<Side, Side>of(Side.BOTTOM, Side.LEFT);
    }
    Point2D _get_18 = this.sourceRect.get(Side.TOP);
    sourcePoint = _get_18;
    Point2D _get_19 = this.targetRect.get(Side.TOP);
    targetPoint = _get_19;
    if (((!this.targetRect.contains(sourcePoint)) && (!this.sourceRect.contains(targetPoint)))) {
      double _y_4 = sourcePoint.getY();
      double _y_5 = targetPoint.getY();
      double _minus_4 = (_y_4 - _y_5);
      boolean _lessThan = (_minus_4 < 0);
      if (_lessThan) {
        double _x_4 = sourcePoint.getX();
        double _x_5 = targetPoint.getX();
        double _minus_5 = (_x_4 - _x_5);
        double _abs = Math.abs(_minus_5);
        double _width = this.sourceRect.getWidth();
        double _plus = (_width + this.STANDARD_DISTANCE);
        double _divide = (_plus / 2);
        boolean _greaterThan_4 = (_abs > _divide);
        if (_greaterThan_4) {
          return Pair.<Side, Side>of(Side.TOP, Side.TOP);
        }
      } else {
        double _x_6 = sourcePoint.getX();
        double _x_7 = targetPoint.getX();
        double _minus_6 = (_x_6 - _x_7);
        double _abs_1 = Math.abs(_minus_6);
        double _width_1 = this.targetRect.getWidth();
        double _divide_1 = (_width_1 / 2);
        boolean _greaterThan_5 = (_abs_1 > _divide_1);
        if (_greaterThan_5) {
          return Pair.<Side, Side>of(Side.TOP, Side.TOP);
        }
      }
    }
    Point2D _get_20 = this.sourceRect.get(Side.RIGHT);
    sourcePoint = _get_20;
    Point2D _get_21 = this.targetRect.get(Side.RIGHT);
    targetPoint = _get_21;
    if (((!this.targetRect.contains(sourcePoint)) && (!this.sourceRect.contains(targetPoint)))) {
      double _x_8 = sourcePoint.getX();
      double _x_9 = targetPoint.getX();
      double _minus_7 = (_x_8 - _x_9);
      boolean _greaterThan_6 = (_minus_7 > 0);
      if (_greaterThan_6) {
        double _y_6 = sourcePoint.getY();
        double _y_7 = targetPoint.getY();
        double _minus_8 = (_y_6 - _y_7);
        double _abs_2 = Math.abs(_minus_8);
        double _height = this.sourceRect.getHeight();
        double _plus_1 = (_height + this.STANDARD_DISTANCE);
        double _divide_2 = (_plus_1 / 2);
        boolean _greaterThan_7 = (_abs_2 > _divide_2);
        if (_greaterThan_7) {
          return Pair.<Side, Side>of(Side.RIGHT, Side.RIGHT);
        }
      } else {
        double _y_8 = sourcePoint.getY();
        double _y_9 = targetPoint.getY();
        double _minus_9 = (_y_8 - _y_9);
        double _abs_3 = Math.abs(_minus_9);
        double _height_1 = this.targetRect.getHeight();
        double _divide_3 = (_height_1 / 2);
        boolean _greaterThan_8 = (_abs_3 > _divide_3);
        if (_greaterThan_8) {
          return Pair.<Side, Side>of(Side.RIGHT, Side.RIGHT);
        }
      }
    }
    Point2D _get_22 = this.sourceRect.get(Side.TOP);
    sourcePoint = _get_22;
    Point2D _get_23 = this.targetRect.get(Side.RIGHT);
    targetPoint = _get_23;
    if (((!this.targetRect.contains(sourcePoint)) && (!this.sourceRect.contains(targetPoint)))) {
      return Pair.<Side, Side>of(Side.TOP, Side.RIGHT);
    }
    Point2D _get_24 = this.targetRect.get(Side.LEFT);
    targetPoint = _get_24;
    if (((!this.targetRect.contains(sourcePoint)) && (!this.sourceRect.contains(targetPoint)))) {
      return Pair.<Side, Side>of(Side.TOP, Side.LEFT);
    }
    Point2D _get_25 = this.sourceRect.get(Side.BOTTOM);
    sourcePoint = _get_25;
    Point2D _get_26 = this.targetRect.get(Side.RIGHT);
    targetPoint = _get_26;
    if (((!this.targetRect.contains(sourcePoint)) && (!this.sourceRect.contains(targetPoint)))) {
      return Pair.<Side, Side>of(Side.BOTTOM, Side.RIGHT);
    }
    Point2D _get_27 = this.targetRect.get(Side.LEFT);
    targetPoint = _get_27;
    if (((!this.targetRect.contains(sourcePoint)) && (!this.sourceRect.contains(targetPoint)))) {
      return Pair.<Side, Side>of(Side.BOTTOM, Side.LEFT);
    }
    return Pair.<Side, Side>of(Side.RIGHT, Side.LEFT);
  }
  
  @Pure
  public boolean isReroutingEnabled() {
    return this.reroutingEnabled;
  }
  
  public void setReroutingEnabled(final boolean reroutingEnabled) {
    this.reroutingEnabled = reroutingEnabled;
  }
}
