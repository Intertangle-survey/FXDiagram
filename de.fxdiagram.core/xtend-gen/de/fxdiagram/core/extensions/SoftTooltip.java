package de.fxdiagram.core.extensions;

import com.google.common.base.Objects;
import de.fxdiagram.core.HeadsUpDisplay;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.extensions.CoreExtensions;
import de.fxdiagram.core.extensions.DurationExtensions;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * In Java7, JavaFX's {@link Tooltip} affects gesture events in an unpredictable way.
 * This tooltip is a lightweight version not using a pop-up.
 * Obsolete for Java8.
 */
@SuppressWarnings("all")
public class SoftTooltip {
  public static class Timer implements Runnable {
    private SoftTooltip tooltip;
    
    private boolean isRunning;
    
    private long endTime;
    
    public Timer(final SoftTooltip behavior) {
      this.tooltip = behavior;
      this.isRunning = false;
    }
    
    public boolean stop() {
      return this.isRunning = false;
    }
    
    public void restart() {
      long _currentTimeMillis = System.currentTimeMillis();
      Duration _delay = this.tooltip.getDelay();
      double _millis = _delay.toMillis();
      long _plus = (_currentTimeMillis + ((long) _millis));
      this.endTime = _plus;
      if ((!this.isRunning)) {
        this.isRunning = true;
        Thread _thread = new Thread(this);
        _thread.start();
      }
    }
    
    @Override
    public void run() {
      try {
        long delay = 0;
        do {
          {
            long _currentTimeMillis = System.currentTimeMillis();
            long _minus = (this.endTime - _currentTimeMillis);
            Thread.sleep(_minus);
            if ((!this.isRunning)) {
              return;
            }
            long _currentTimeMillis_1 = System.currentTimeMillis();
            long _minus_1 = (this.endTime - _currentTimeMillis_1);
            delay = _minus_1;
          }
        } while((delay > 0));
        if (this.isRunning) {
          final Runnable _function = () -> {
            this.tooltip.trigger();
          };
          Platform.runLater(_function);
        }
        this.isRunning = false;
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    }
  }
  
  private StringProperty textProperty;
  
  private Node host;
  
  private XRoot root;
  
  private Node tooltip;
  
  private SoftTooltip.Timer timer;
  
  private boolean isHideOnTrigger = false;
  
  private boolean isShowing;
  
  public SoftTooltip(final Node host, final String text) {
    this.host = host;
    StackPane _stackPane = new StackPane();
    final Procedure1<StackPane> _function = (StackPane it) -> {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("-fx-border-color: black;");
      _builder.newLine();
      _builder.append("-fx-border-width: 1;");
      _builder.newLine();
      _builder.append("-fx-background-color: #ffffbb;");
      _builder.newLine();
      it.setStyle(_builder.toString());
      ObservableList<Node> _children = it.getChildren();
      Text _text = new Text();
      final Procedure1<Text> _function_1 = (Text it_1) -> {
        it_1.setText(text);
        StringProperty _textProperty = it_1.textProperty();
        this.textProperty = _textProperty;
        Insets _insets = new Insets(2, 2, 2, 2);
        StackPane.setMargin(it_1, _insets);
      };
      Text _doubleArrow = ObjectExtensions.<Text>operator_doubleArrow(_text, _function_1);
      _children.add(_doubleArrow);
      it.setMouseTransparent(true);
    };
    StackPane _doubleArrow = ObjectExtensions.<StackPane>operator_doubleArrow(_stackPane, _function);
    this.tooltip = _doubleArrow;
    SoftTooltip.Timer _timer = new SoftTooltip.Timer(this);
    this.timer = _timer;
  }
  
  public void install() {
    final EventHandler<MouseEvent> _function = (MouseEvent it) -> {
      EventType<? extends MouseEvent> _eventType = it.getEventType();
      boolean _matched = false;
      if (Objects.equal(_eventType, MouseEvent.MOUSE_ENTERED_TARGET)) {
        _matched=true;
        this.isHideOnTrigger = false;
        double _sceneX = it.getSceneX();
        double _sceneY = it.getSceneY();
        this.setReferencePosition(_sceneX, _sceneY);
        if (this.timer!=null) {
          this.timer.restart();
        }
      }
      if (!_matched) {
        if (Objects.equal(_eventType, MouseEvent.MOUSE_EXITED_TARGET)) {
          _matched=true;
        }
      }
      if (!_matched) {
        if (Objects.equal(_eventType, MouseEvent.MOUSE_ENTERED)) {
          _matched=true;
          this.isHideOnTrigger = false;
          double _sceneX_1 = it.getSceneX();
          double _sceneY_1 = it.getSceneY();
          this.setReferencePosition(_sceneX_1, _sceneY_1);
          if (this.timer!=null) {
            this.timer.restart();
          }
        }
      }
      if (!_matched) {
        if (Objects.equal(_eventType, MouseEvent.MOUSE_MOVED)) {
          _matched=true;
          double _sceneX_2 = it.getSceneX();
          double _sceneY_2 = it.getSceneY();
          this.setReferencePosition(_sceneX_2, _sceneY_2);
          if (this.timer!=null) {
            this.timer.restart();
          }
        }
      }
      if (!_matched) {
        {
          this.isHideOnTrigger = true;
          if (this.timer!=null) {
            this.timer.restart();
          }
        }
      }
    };
    this.host.<MouseEvent>addEventHandler(MouseEvent.ANY, _function);
  }
  
  public String getText() {
    return this.textProperty.get();
  }
  
  public void setText(final String text) {
    this.textProperty.set(text);
  }
  
  public boolean isShowing() {
    return this.isShowing;
  }
  
  public Node setReferencePosition(final double positionX, final double positionY) {
    return this.setPosition((positionX + 16), (positionY - 32));
  }
  
  public Node setPosition(final double positionX, final double positionY) {
    final Procedure1<Node> _function = (Node it) -> {
      it.setLayoutX(positionX);
      it.setLayoutY(positionY);
    };
    return ObjectExtensions.<Node>operator_doubleArrow(
      this.tooltip, _function);
  }
  
  public boolean show(final double positionX, final double positionY) {
    boolean _xblockexpression = false;
    {
      this.setReferencePosition(positionX, positionY);
      _xblockexpression = this.show();
    }
    return _xblockexpression;
  }
  
  public boolean trigger() {
    boolean _xifexpression = false;
    if (this.isHideOnTrigger) {
      _xifexpression = this.hide();
    } else {
      _xifexpression = this.show();
    }
    return _xifexpression;
  }
  
  public boolean show() {
    boolean _xblockexpression = false;
    {
      if ((!this.isShowing)) {
        XRoot _root = CoreExtensions.getRoot(this.host);
        this.root = _root;
        Parent _parent = this.tooltip.getParent();
        HeadsUpDisplay _headsUpDisplay = null;
        if (this.root!=null) {
          _headsUpDisplay=this.root.getHeadsUpDisplay();
        }
        boolean _notEquals = (!Objects.equal(_parent, _headsUpDisplay));
        if (_notEquals) {
          HeadsUpDisplay _headsUpDisplay_1 = null;
          if (this.root!=null) {
            _headsUpDisplay_1=this.root.getHeadsUpDisplay();
          }
          ObservableList<Node> _children = null;
          if (_headsUpDisplay_1!=null) {
            _children=_headsUpDisplay_1.getChildren();
          }
          if (_children!=null) {
            _children.add(this.tooltip);
          }
        }
        this.tooltip.setOpacity(1);
      }
      _xblockexpression = this.isShowing = true;
    }
    return _xblockexpression;
  }
  
  public boolean hide() {
    boolean _xblockexpression = false;
    {
      if (this.isShowing) {
        this.tooltip.setOpacity(0);
      }
      _xblockexpression = this.isShowing = false;
    }
    return _xblockexpression;
  }
  
  private SimpleObjectProperty<Duration> delayProperty = new SimpleObjectProperty<Duration>(this, "delay",_initDelay());
  
  private static final Duration _initDelay() {
    Duration _millis = DurationExtensions.millis(200);
    return _millis;
  }
  
  public Duration getDelay() {
    return this.delayProperty.get();
  }
  
  public void setDelay(final Duration delay) {
    this.delayProperty.set(delay);
  }
  
  public ObjectProperty<Duration> delayProperty() {
    return this.delayProperty;
  }
}
