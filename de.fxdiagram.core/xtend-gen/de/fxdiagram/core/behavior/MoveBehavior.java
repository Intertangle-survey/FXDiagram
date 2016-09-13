package de.fxdiagram.core.behavior;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import de.fxdiagram.core.XConnection;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.XShape;
import de.fxdiagram.core.anchors.ConnectionRouter;
import de.fxdiagram.core.behavior.AbstractHostBehavior;
import de.fxdiagram.core.behavior.Behavior;
import de.fxdiagram.core.command.AnimationCommand;
import de.fxdiagram.core.command.CommandStack;
import de.fxdiagram.core.command.MoveCommand;
import de.fxdiagram.core.extensions.CoreExtensions;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class MoveBehavior<T extends XShape> extends AbstractHostBehavior<T> {
  @Data
  public static class DragContext {
    private final double initialX;
    
    private final double initialY;
    
    private final double mouseAnchorX;
    
    private final double mouseAnchorY;
    
    private final Point2D initialPosInScene;
    
    public DragContext(final double initialX, final double initialY, final double mouseAnchorX, final double mouseAnchorY, final Point2D initialPosInScene) {
      super();
      this.initialX = initialX;
      this.initialY = initialY;
      this.mouseAnchorX = mouseAnchorX;
      this.mouseAnchorY = mouseAnchorY;
      this.initialPosInScene = initialPosInScene;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (Double.doubleToLongBits(this.initialX) ^ (Double.doubleToLongBits(this.initialX) >>> 32));
      result = prime * result + (int) (Double.doubleToLongBits(this.initialY) ^ (Double.doubleToLongBits(this.initialY) >>> 32));
      result = prime * result + (int) (Double.doubleToLongBits(this.mouseAnchorX) ^ (Double.doubleToLongBits(this.mouseAnchorX) >>> 32));
      result = prime * result + (int) (Double.doubleToLongBits(this.mouseAnchorY) ^ (Double.doubleToLongBits(this.mouseAnchorY) >>> 32));
      result = prime * result + ((this.initialPosInScene== null) ? 0 : this.initialPosInScene.hashCode());
      return result;
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MoveBehavior.DragContext other = (MoveBehavior.DragContext) obj;
      if (Double.doubleToLongBits(other.initialX) != Double.doubleToLongBits(this.initialX))
        return false; 
      if (Double.doubleToLongBits(other.initialY) != Double.doubleToLongBits(this.initialY))
        return false; 
      if (Double.doubleToLongBits(other.mouseAnchorX) != Double.doubleToLongBits(this.mouseAnchorX))
        return false; 
      if (Double.doubleToLongBits(other.mouseAnchorY) != Double.doubleToLongBits(this.mouseAnchorY))
        return false; 
      if (this.initialPosInScene == null) {
        if (other.initialPosInScene != null)
          return false;
      } else if (!this.initialPosInScene.equals(other.initialPosInScene))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      ToStringBuilder b = new ToStringBuilder(this);
      b.add("initialX", this.initialX);
      b.add("initialY", this.initialY);
      b.add("mouseAnchorX", this.mouseAnchorX);
      b.add("mouseAnchorY", this.mouseAnchorY);
      b.add("initialPosInScene", this.initialPosInScene);
      return b.toString();
    }
    
    @Pure
    public double getInitialX() {
      return this.initialX;
    }
    
    @Pure
    public double getInitialY() {
      return this.initialY;
    }
    
    @Pure
    public double getMouseAnchorX() {
      return this.mouseAnchorX;
    }
    
    @Pure
    public double getMouseAnchorY() {
      return this.mouseAnchorY;
    }
    
    @Pure
    public Point2D getInitialPosInScene() {
      return this.initialPosInScene;
    }
  }
  
  private MoveBehavior.DragContext dragContext;
  
  public MoveBehavior(final T host) {
    super(host);
  }
  
  @Override
  public void doActivate() {
    T _host = this.getHost();
    final EventHandler<MouseEvent> _function = (MouseEvent it) -> {
      boolean _hasMoved = this.hasMoved();
      if (_hasMoved) {
        T _host_1 = this.getHost();
        XRoot _root = CoreExtensions.getRoot(_host_1);
        CommandStack _commandStack = _root.getCommandStack();
        AnimationCommand _createMoveCommand = this.createMoveCommand();
        _commandStack.execute(_createMoveCommand);
        this.setManuallyPlaced(true);
      }
    };
    _host.setOnMouseReleased(_function);
  }
  
  protected boolean hasMoved() {
    return ((!Objects.equal(this.dragContext, null)) && ((this.dragContext.initialX != this.getHost().getLayoutX()) || (this.dragContext.initialY != this.getHost().getLayoutY())));
  }
  
  protected AnimationCommand createMoveCommand() {
    T _host = this.getHost();
    T _host_1 = this.getHost();
    double _layoutX = _host_1.getLayoutX();
    T _host_2 = this.getHost();
    double _layoutY = _host_2.getLayoutY();
    return new MoveCommand(_host, 
      this.dragContext.initialX, this.dragContext.initialY, _layoutX, _layoutY);
  }
  
  @Override
  public Class<? extends Behavior> getBehaviorKey() {
    return MoveBehavior.class;
  }
  
  public void mousePressed(final MouseEvent it) {
    double _screenX = it.getScreenX();
    double _screenY = it.getScreenY();
    this.startDrag(_screenX, _screenY);
  }
  
  public void startDrag(final double screenX, final double screenY) {
    T _host = this.getHost();
    Parent _parent = _host.getParent();
    T _host_1 = this.getHost();
    double _layoutX = _host_1.getLayoutX();
    T _host_2 = this.getHost();
    double _layoutY = _host_2.getLayoutY();
    final Point2D initialPositionInScene = _parent.localToScene(_layoutX, _layoutY);
    T _host_3 = this.getHost();
    double _layoutX_1 = _host_3.getLayoutX();
    T _host_4 = this.getHost();
    double _layoutY_1 = _host_4.getLayoutY();
    MoveBehavior.DragContext _dragContext = new MoveBehavior.DragContext(_layoutX_1, _layoutY_1, screenX, screenY, initialPositionInScene);
    this.dragContext = _dragContext;
    T _host_5 = this.getHost();
    if ((_host_5 instanceof XNode)) {
      T _host_6 = this.getHost();
      final XNode node = ((XNode) _host_6);
      ObservableList<XConnection> _incomingConnections = node.getIncomingConnections();
      ObservableList<XConnection> _outgoingConnections = node.getOutgoingConnections();
      Iterable<XConnection> _plus = Iterables.<XConnection>concat(_incomingConnections, _outgoingConnections);
      final Consumer<XConnection> _function = (XConnection it) -> {
        ConnectionRouter _connectionRouter = it.getConnectionRouter();
        _connectionRouter.setSplineShapeKeeperEnabled(true);
      };
      _plus.forEach(_function);
    }
  }
  
  public void mouseDragged(final MouseEvent it) {
    double _x = this.dragContext.initialPosInScene.getX();
    double _screenX = it.getScreenX();
    double _plus = (_x + _screenX);
    double _minus = (_plus - this.dragContext.mouseAnchorX);
    double _y = this.dragContext.initialPosInScene.getY();
    double _screenY = it.getScreenY();
    double _plus_1 = (_y + _screenY);
    double _minus_1 = (_plus_1 - this.dragContext.mouseAnchorY);
    final Point2D newPositionInScene = new Point2D(_minus, _minus_1);
    T _host = this.getHost();
    Parent _parent = _host.getParent();
    final Point2D newPositionInDiagram = _parent.sceneToLocal(newPositionInScene);
    this.dragTo(newPositionInDiagram);
  }
  
  protected void dragTo(final Point2D newPositionInDiagram) {
    boolean _notEquals = (!Objects.equal(newPositionInDiagram, null));
    if (_notEquals) {
      T _host = this.getHost();
      double _x = newPositionInDiagram.getX();
      _host.setLayoutX(_x);
      T _host_1 = this.getHost();
      double _y = newPositionInDiagram.getY();
      _host_1.setLayoutY(_y);
    }
  }
  
  private SimpleBooleanProperty manuallyPlacedProperty = new SimpleBooleanProperty(this, "manuallyPlaced");
  
  public boolean getManuallyPlaced() {
    return this.manuallyPlacedProperty.get();
  }
  
  public void setManuallyPlaced(final boolean manuallyPlaced) {
    this.manuallyPlacedProperty.set(manuallyPlaced);
  }
  
  public BooleanProperty manuallyPlacedProperty() {
    return this.manuallyPlacedProperty;
  }
}
