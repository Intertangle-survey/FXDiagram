package de.fxdiagram.lib.buttons;

import com.google.common.collect.Iterables;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.behavior.AbstractHostBehavior;
import de.fxdiagram.core.behavior.Behavior;
import de.fxdiagram.core.extensions.BoundsExtensions;
import de.fxdiagram.core.extensions.CoreExtensions;
import de.fxdiagram.core.extensions.DurationExtensions;
import de.fxdiagram.core.extensions.InitializingListListener;
import de.fxdiagram.core.extensions.InitializingListener;
import de.fxdiagram.core.extensions.Point2DExtensions;
import de.fxdiagram.lib.buttons.RapidButton;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * Adds rapid buttons to a host {@link XNode}.
 * 
 * @see RapidButton
 */
@SuppressWarnings("all")
public class RapidButtonBehavior<HOST extends XNode> extends AbstractHostBehavior<HOST> {
  @Accessors
  private double border;
  
  private ObservableList<RapidButton> buttonsProperty = FXCollections.<RapidButton>observableArrayList();
  
  private final Map<Side, Pane> pos2group = Collections.<Side, Pane>unmodifiableMap(CollectionLiterals.<Side, Pane>newHashMap(Pair.<Side, HBox>of(Side.TOP, new HBox()), Pair.<Side, HBox>of(Side.BOTTOM, new HBox()), Pair.<Side, VBox>of(Side.LEFT, new VBox()), Pair.<Side, VBox>of(Side.RIGHT, new VBox())));
  
  private Group allButtons = new Group();
  
  private final FadeTransition fadeTransition = ObjectExtensions.<FadeTransition>operator_doubleArrow(new FadeTransition(), ((Procedure1<FadeTransition>) (FadeTransition it) -> {
    it.setNode(this.allButtons);
    it.setDelay(DurationExtensions.millis(500));
    it.setDuration(DurationExtensions.millis(500));
    it.setFromValue(1);
    it.setToValue(0);
    final EventHandler<ActionEvent> _function = (ActionEvent it_1) -> {
      this.allButtons.setVisible(false);
    };
    it.setOnFinished(_function);
  }));
  
  public RapidButtonBehavior(final HOST host) {
    super(host);
    this.allButtons.setVisible(false);
    ObservableList<Node> _children = this.allButtons.getChildren();
    Collection<Pane> _values = this.pos2group.values();
    Iterables.<Node>addAll(_children, _values);
  }
  
  public boolean add(final RapidButton button) {
    return this.buttonsProperty.add(button);
  }
  
  public boolean remove(final RapidButton button) {
    return this.buttonsProperty.remove(button);
  }
  
  @Override
  protected void doActivate() {
    InitializingListListener<RapidButton> _initializingListListener = new InitializingListListener<RapidButton>();
    final Procedure1<InitializingListListener<RapidButton>> _function = (InitializingListListener<RapidButton> it) -> {
      final Procedure1<RapidButton> _function_1 = (RapidButton button) -> {
        button.activate();
        final EventHandler<MouseEvent> _function_2 = (MouseEvent it_1) -> {
          this.allButtons.setVisible(false);
        };
        button.<MouseEvent>addEventHandler(MouseEvent.MOUSE_CLICKED, _function_2);
      };
      it.setAdd(_function_1);
    };
    InitializingListListener<RapidButton> _doubleArrow = ObjectExtensions.<InitializingListListener<RapidButton>>operator_doubleArrow(_initializingListListener, _function);
    CoreExtensions.<RapidButton>addInitializingListener(this.buttonsProperty, _doubleArrow);
    this.updateButtons();
    this.layout();
    final EventHandler<MouseEvent> _function_1 = (MouseEvent it) -> {
      this.show();
    };
    this.getHost().getNode().<MouseEvent>addEventHandler(MouseEvent.MOUSE_ENTERED, _function_1);
    final EventHandler<MouseEvent> _function_2 = (MouseEvent it) -> {
      this.fade();
    };
    this.getHost().getNode().<MouseEvent>addEventHandler(MouseEvent.MOUSE_EXITED, _function_2);
    final EventHandler<MouseEvent> _function_3 = (MouseEvent it) -> {
      this.show();
    };
    this.allButtons.setOnMouseEntered(_function_3);
    final EventHandler<MouseEvent> _function_4 = (MouseEvent it) -> {
      this.fade();
    };
    this.allButtons.setOnMouseExited(_function_4);
    final ChangeListener<Bounds> _function_5 = (ObservableValue<? extends Bounds> p, Bounds o, Bounds n) -> {
      boolean _isVisible = this.allButtons.isVisible();
      if (_isVisible) {
        this.layout();
      }
    };
    this.getHost().boundsInParentProperty().addListener(_function_5);
    ReadOnlyObjectProperty<Parent> _parentProperty = this.getHost().parentProperty();
    InitializingListener<Parent> _initializingListener = new InitializingListener<Parent>();
    final Procedure1<InitializingListener<Parent>> _function_6 = (InitializingListener<Parent> it) -> {
      final Procedure1<Parent> _function_7 = (Parent it_1) -> {
        ObservableList<Node> _children = CoreExtensions.getDiagram(it_1).getButtonLayer().getChildren();
        _children.add(this.allButtons);
      };
      it.setSet(_function_7);
      final Procedure1<Parent> _function_8 = (Parent it_1) -> {
        if ((it_1 != null)) {
          ObservableList<Node> _children = CoreExtensions.getDiagram(it_1).getButtonLayer().getChildren();
          _children.remove(this.allButtons);
        }
      };
      it.setUnset(_function_8);
    };
    InitializingListener<Parent> _doubleArrow_1 = ObjectExtensions.<InitializingListener<Parent>>operator_doubleArrow(_initializingListener, _function_6);
    CoreExtensions.<Parent>addInitializingListener(_parentProperty, _doubleArrow_1);
  }
  
  public Group show() {
    Group _xblockexpression = null;
    {
      final ObservableList<Node> blChildren = CoreExtensions.getDiagram(this.getHost()).getButtonLayer().getChildren();
      boolean _contains = blChildren.contains(this.allButtons);
      boolean _not = (!_contains);
      if (_not) {
        blChildren.add(this.allButtons);
      }
      this.fadeTransition.stop();
      boolean _isVisible = this.allButtons.isVisible();
      boolean _not_1 = (!_isVisible);
      if (_not_1) {
        this.updateButtons();
        this.layout();
      }
      final Procedure1<Group> _function = (Group it) -> {
        it.setVisible(true);
        it.setOpacity(1.0);
      };
      _xblockexpression = ObjectExtensions.<Group>operator_doubleArrow(
        this.allButtons, _function);
    }
    return _xblockexpression;
  }
  
  public void fade() {
    this.fadeTransition.play();
  }
  
  @Override
  public Class<? extends Behavior> getBehaviorKey() {
    return this.getClass();
  }
  
  protected void updateButtons() {
    for (final RapidButton button : this.buttonsProperty) {
      {
        final ObservableList<Node> group = this.pos2group.get(button.getPosition()).getChildren();
        boolean _isEnabled = button.getAction().isEnabled(this.getHost());
        if (_isEnabled) {
          boolean _contains = group.contains(button);
          boolean _not = (!_contains);
          if (_not) {
            group.add(button);
          }
        } else {
          boolean _contains_1 = group.contains(button);
          if (_contains_1) {
            group.remove(button);
          }
        }
      }
    }
  }
  
  protected void layout() {
    final Bounds hostBounds = CoreExtensions.localToRootDiagram(this.getHost(), this.getHost().getLayoutBounds());
    Point2D _center = null;
    if (hostBounds!=null) {
      _center=BoundsExtensions.center(hostBounds);
    }
    final Point2D hostCenter = _center;
    if ((hostCenter != null)) {
      Set<Map.Entry<Side, Pane>> _entrySet = this.pos2group.entrySet();
      for (final Map.Entry<Side, Pane> entry : _entrySet) {
        {
          final Side pos = entry.getKey();
          final Pane group = entry.getValue();
          group.autosize();
          final Bounds groupBounds = group.getBoundsInLocal();
          Point2D _center_1 = BoundsExtensions.center(groupBounds);
          final Point2D centered = Point2DExtensions.operator_minus(hostCenter, _center_1);
          final Procedure1<Pane> _function = (Pane it) -> {
            double _x = centered.getX();
            double _switchResult = (double) 0;
            if (pos != null) {
              switch (pos) {
                case LEFT:
                  double _width = hostBounds.getWidth();
                  double _multiply = ((-0.5) * _width);
                  double _minus = (_multiply - this.border);
                  double _width_1 = groupBounds.getWidth();
                  _switchResult = (_minus - _width_1);
                  break;
                case RIGHT:
                  double _width_2 = hostBounds.getWidth();
                  double _multiply_1 = (0.5 * _width_2);
                  double _plus = (_multiply_1 + this.border);
                  double _width_3 = groupBounds.getWidth();
                  _switchResult = (_plus + _width_3);
                  break;
                default:
                  _switchResult = 0;
                  break;
              }
            } else {
              _switchResult = 0;
            }
            double _plus_1 = (_x + _switchResult);
            it.setLayoutX(_plus_1);
            double _y = centered.getY();
            double _switchResult_1 = (double) 0;
            if (pos != null) {
              switch (pos) {
                case TOP:
                  double _height = hostBounds.getHeight();
                  double _multiply_2 = ((-0.5) * _height);
                  double _minus_1 = (_multiply_2 - this.border);
                  double _height_1 = groupBounds.getHeight();
                  _switchResult_1 = (_minus_1 - _height_1);
                  break;
                case BOTTOM:
                  double _height_2 = hostBounds.getHeight();
                  double _multiply_3 = (0.5 * _height_2);
                  double _plus_2 = (_multiply_3 + this.border);
                  double _height_3 = groupBounds.getHeight();
                  _switchResult_1 = (_plus_2 + _height_3);
                  break;
                default:
                  _switchResult_1 = 0;
                  break;
              }
            } else {
              _switchResult_1 = 0;
            }
            double _plus_3 = (_y + _switchResult_1);
            it.setLayoutY(_plus_3);
          };
          ObjectExtensions.<Pane>operator_doubleArrow(group, _function);
        }
      }
    }
  }
  
  @Pure
  public double getBorder() {
    return this.border;
  }
  
  public void setBorder(final double border) {
    this.border = border;
  }
}
