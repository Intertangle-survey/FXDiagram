package de.fxdiagram.core.tools;

import com.google.common.base.Objects;
import de.fxdiagram.core.HeadsUpDisplay;
import de.fxdiagram.core.XControlPoint;
import de.fxdiagram.core.XDiagram;
import de.fxdiagram.core.XRapidButton;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.XShape;
import de.fxdiagram.core.auxlines.AuxiliaryLinesSupport;
import de.fxdiagram.core.behavior.MoveBehavior;
import de.fxdiagram.core.extensions.BoundsExtensions;
import de.fxdiagram.core.extensions.CoreExtensions;
import de.fxdiagram.core.extensions.DurationExtensions;
import de.fxdiagram.core.extensions.SoftTooltip;
import de.fxdiagram.core.extensions.TimerExtensions;
import de.fxdiagram.core.tools.XDiagramTool;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class SelectionTool implements XDiagramTool {
  private XRoot root;
  
  private EventHandler<MouseEvent> mousePressedHandler;
  
  private EventHandler<MouseEvent> mouseDraggedHandler;
  
  private EventHandler<MouseEvent> mouseReleasedHandler;
  
  private SoftTooltip positionTip;
  
  public SelectionTool(final XRoot root) {
    this.root = root;
    final EventHandler<MouseEvent> _function = new EventHandler<MouseEvent>() {
      public void handle(final MouseEvent event) {
        Iterable<XShape> _currentSelection = root.getCurrentSelection();
        final Set<XShape> selection = IterableExtensions.<XShape>toSet(_currentSelection);
        boolean _and = false;
        EventTarget _target = event.getTarget();
        Pane _diagramCanvas = root.getDiagramCanvas();
        boolean _equals = Objects.equal(_target, _diagramCanvas);
        if (!_equals) {
          _and = false;
        } else {
          MouseButton _button = event.getButton();
          boolean _equals_1 = Objects.equal(_button, MouseButton.PRIMARY);
          _and = (_equals && _equals_1);
        }
        if (_and) {
          final Function1<XShape,Boolean> _function = new Function1<XShape,Boolean>() {
            public Boolean apply(final XShape it) {
              return Boolean.valueOf(true);
            }
          };
          SelectionTool.this.deselect(selection, _function);
        } else {
          XRapidButton _targetButton = CoreExtensions.getTargetButton(event);
          boolean _not = (!(_targetButton instanceof XRapidButton));
          if (_not) {
            final XShape targetShape = CoreExtensions.getTargetShape(event);
            boolean _isSelectable = false;
            if (targetShape!=null) {
              _isSelectable=targetShape.isSelectable();
            }
            if (_isSelectable) {
              boolean _and_1 = false;
              boolean _selected = targetShape.getSelected();
              boolean _not_1 = (!_selected);
              if (!_not_1) {
                _and_1 = false;
              } else {
                boolean _isShortcutDown = event.isShortcutDown();
                boolean _not_2 = (!_isShortcutDown);
                _and_1 = (_not_1 && _not_2);
              }
              if (_and_1) {
                XShape _switchResult = null;
                boolean _matched = false;
                if (!_matched) {
                  if (targetShape instanceof XControlPoint) {
                    _matched=true;
                    Parent _parent = ((XControlPoint)targetShape).getParent();
                    XShape _containerShape = CoreExtensions.getContainerShape(_parent);
                    _switchResult = _containerShape;
                  }
                }
                if (!_matched) {
                  _switchResult = null;
                }
                final XShape skip = _switchResult;
                final Function1<XShape,Boolean> _function_1 = new Function1<XShape,Boolean>() {
                  public Boolean apply(final XShape it) {
                    boolean _notEquals = (!Objects.equal(it, skip));
                    return Boolean.valueOf(_notEquals);
                  }
                };
                SelectionTool.this.deselect(selection, _function_1);
              }
              final Function1<XShape,Boolean> _function_2 = new Function1<XShape,Boolean>() {
                public Boolean apply(final XShape it) {
                  XDiagram _diagram = CoreExtensions.getDiagram(it);
                  XDiagram _diagram_1 = CoreExtensions.getDiagram(targetShape);
                  boolean _notEquals = (!Objects.equal(_diagram, _diagram_1));
                  return Boolean.valueOf(_notEquals);
                }
              };
              SelectionTool.this.deselect(selection, _function_2);
              boolean _isShortcutDown_1 = event.isShortcutDown();
              if (_isShortcutDown_1) {
                targetShape.toggleSelect(event);
              } else {
                targetShape.select(event);
              }
              boolean _selected_1 = targetShape.getSelected();
              if (_selected_1) {
                selection.add(targetShape);
              }
              final Procedure1<XShape> _function_3 = new Procedure1<XShape>() {
                public void apply(final XShape it) {
                  MoveBehavior<? extends XShape> _moveBehavior = it.getMoveBehavior();
                  if (_moveBehavior!=null) {
                    _moveBehavior.mousePressed(event);
                  }
                }
              };
              IterableExtensions.<XShape>forEach(selection, _function_3);
              MoveBehavior<? extends XShape> _moveBehavior = targetShape.getMoveBehavior();
              if (_moveBehavior!=null) {
                _moveBehavior.mousePressed(event);
              }
              double _sceneX = event.getSceneX();
              double _sceneY = event.getSceneY();
              SelectionTool.this.updatePositionTooltip(selection, _sceneX, _sceneY);
              final Runnable _function_4 = new Runnable() {
                public void run() {
                  SelectionTool.this.showPositionTooltip();
                }
              };
              Duration _millis = DurationExtensions.millis(200);
              TimerExtensions.defer(_function_4, _millis);
            }
          }
        }
      }
    };
    this.mousePressedHandler = _function;
    final EventHandler<MouseEvent> _function_1 = new EventHandler<MouseEvent>() {
      public void handle(final MouseEvent it) {
        final Iterable<XShape> selection = root.getCurrentSelection();
        for (final XShape shape : selection) {
          MoveBehavior<? extends XShape> _moveBehavior = null;
          if (shape!=null) {
            _moveBehavior=shape.getMoveBehavior();
          }
          if (_moveBehavior!=null) {
            _moveBehavior.mouseDragged(it);
          }
        }
        XDiagram _diagram = root.getDiagram();
        AuxiliaryLinesSupport _auxiliaryLinesSupport = _diagram.getAuxiliaryLinesSupport();
        if (_auxiliaryLinesSupport!=null) {
          _auxiliaryLinesSupport.show(selection);
        }
        double _sceneX = it.getSceneX();
        double _sceneY = it.getSceneY();
        SelectionTool.this.updatePositionTooltip(selection, _sceneX, _sceneY);
        SelectionTool.this.showPositionTooltip();
        it.consume();
      }
    };
    this.mouseDraggedHandler = _function_1;
    final EventHandler<MouseEvent> _function_2 = new EventHandler<MouseEvent>() {
      public void handle(final MouseEvent it) {
        XDiagram _diagram = root.getDiagram();
        AuxiliaryLinesSupport _auxiliaryLinesSupport = _diagram.getAuxiliaryLinesSupport();
        if (_auxiliaryLinesSupport!=null) {
          _auxiliaryLinesSupport.hide();
        }
        SelectionTool.this.hidePositionTooltip();
      }
    };
    this.mouseReleasedHandler = _function_2;
  }
  
  protected void updatePositionTooltip(final Iterable<? extends XShape> selection, final double screenX, final double screenY) {
    final Function1<XShape,Bounds> _function = new Function1<XShape,Bounds>() {
      public Bounds apply(final XShape it) {
        Bounds _snapBounds = it.getSnapBounds();
        Bounds _localToRootDiagram = CoreExtensions.localToRootDiagram(it, _snapBounds);
        return _localToRootDiagram;
      }
    };
    Iterable<Bounds> _map = IterableExtensions.map(selection, _function);
    final Function2<Bounds,Bounds,Bounds> _function_1 = new Function2<Bounds,Bounds,Bounds>() {
      public Bounds apply(final Bounds a, final Bounds b) {
        BoundingBox _plus = BoundsExtensions.operator_plus(a, b);
        return _plus;
      }
    };
    Bounds selectionBounds = IterableExtensions.<Bounds>reduce(_map, _function_1);
    boolean _notEquals = (!Objects.equal(selectionBounds, null));
    if (_notEquals) {
      double _minX = selectionBounds.getMinX();
      double _minY = selectionBounds.getMinY();
      final String positionString = String.format("(%.3f : %.3f)", Double.valueOf(_minX), Double.valueOf(_minY));
      SoftTooltip _elvis = null;
      if (this.positionTip != null) {
        _elvis = this.positionTip;
      } else {
        HeadsUpDisplay _headsUpDisplay = this.root.getHeadsUpDisplay();
        SoftTooltip _softTooltip = new SoftTooltip(_headsUpDisplay, positionString);
        _elvis = ObjectExtensions.<SoftTooltip>operator_elvis(this.positionTip, _softTooltip);
      }
      this.positionTip = _elvis;
      this.positionTip.setReferencePosition(screenX, screenY);
      this.positionTip.setText(positionString);
    }
  }
  
  protected Boolean showPositionTooltip() {
    Boolean _xifexpression = null;
    boolean _and = false;
    boolean _notEquals = (!Objects.equal(this.positionTip, null));
    if (!_notEquals) {
      _and = false;
    } else {
      boolean _isShowing = this.positionTip.isShowing();
      boolean _not = (!_isShowing);
      _and = (_notEquals && _not);
    }
    if (_and) {
      boolean _show = this.positionTip.show();
      _xifexpression = Boolean.valueOf(_show);
    }
    return _xifexpression;
  }
  
  protected SoftTooltip hidePositionTooltip() {
    SoftTooltip _xblockexpression = null;
    {
      boolean _isShowing = false;
      if (this.positionTip!=null) {
        _isShowing=this.positionTip.isShowing();
      }
      if (_isShowing) {
        this.positionTip.hide();
      }
      SoftTooltip _positionTip = this.positionTip = null;
      _xblockexpression = (_positionTip);
    }
    return _xblockexpression;
  }
  
  protected void deselect(final Collection<XShape> selection, final Function1<? super XShape,? extends Boolean> filter) {
    final Iterator<XShape> i = selection.iterator();
    boolean _hasNext = i.hasNext();
    boolean _while = _hasNext;
    while (_while) {
      {
        final XShape element = i.next();
        Boolean _apply = filter.apply(element);
        if ((_apply).booleanValue()) {
          element.setSelected(false);
          i.remove();
        }
      }
      boolean _hasNext_1 = i.hasNext();
      _while = _hasNext_1;
    }
  }
  
  public boolean activate() {
    boolean _xblockexpression = false;
    {
      this.root.<MouseEvent>addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedHandler);
      this.root.<MouseEvent>addEventFilter(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedHandler);
      this.root.<MouseEvent>addEventFilter(MouseEvent.MOUSE_RELEASED, this.mouseReleasedHandler);
      _xblockexpression = (true);
    }
    return _xblockexpression;
  }
  
  public boolean deactivate() {
    boolean _xblockexpression = false;
    {
      this.root.<MouseEvent>removeEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedHandler);
      this.root.<MouseEvent>removeEventFilter(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedHandler);
      this.root.<MouseEvent>removeEventFilter(MouseEvent.MOUSE_RELEASED, this.mouseReleasedHandler);
      _xblockexpression = (true);
    }
    return _xblockexpression;
  }
}
