package de.fxdiagram.core.command;

import com.google.common.base.Objects;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.command.AnimationCommand;
import de.fxdiagram.core.command.CommandContext;
import de.fxdiagram.core.viewport.ViewportMemento;
import de.fxdiagram.core.viewport.ViewportTransition;
import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Base class for animation commands.
 * 
 * Handles viewport restoration and on-demand animation creation.
 */
@SuppressWarnings("all")
public abstract class AbstractAnimationCommand implements AnimationCommand {
  private ViewportMemento fromMemento;
  
  private ViewportMemento toMemento;
  
  private Duration executeDuration;
  
  private boolean isRestoreViewport = true;
  
  @Override
  public void skipViewportRestore() {
    this.isRestoreViewport = false;
  }
  
  @Override
  public Animation getExecuteAnimation(final CommandContext context) {
    if (this.isRestoreViewport) {
      this.fromMemento = context.getRoot().getViewportTransform().createMemento();
    }
    final Animation animation = this.createExecuteAnimation(context);
    boolean _notEquals = (!Objects.equal(animation, null));
    if (_notEquals) {
      SequentialTransition _sequentialTransition = new SequentialTransition();
      final Procedure1<SequentialTransition> _function = (SequentialTransition it) -> {
        ObservableList<Animation> _children = it.getChildren();
        _children.add(animation);
        final EventHandler<ActionEvent> _function_1 = (ActionEvent it_1) -> {
          if (this.isRestoreViewport) {
            this.toMemento = context.getRoot().getViewportTransform().createMemento();
          }
        };
        it.setOnFinished(_function_1);
      };
      return ObjectExtensions.<SequentialTransition>operator_doubleArrow(_sequentialTransition, _function);
    } else {
      if (this.isRestoreViewport) {
        this.toMemento = this.fromMemento;
      }
      return null;
    }
  }
  
  public abstract Animation createExecuteAnimation(final CommandContext context);
  
  @Override
  public Animation getUndoAnimation(final CommandContext context) {
    SequentialTransition _xblockexpression = null;
    {
      final Animation undoAnimation = this.createUndoAnimation(context);
      SequentialTransition _xifexpression = null;
      if (((!Objects.equal(this.toMemento, null)) || (!Objects.equal(undoAnimation, null)))) {
        SequentialTransition _sequentialTransition = new SequentialTransition();
        final Procedure1<SequentialTransition> _function = (SequentialTransition it) -> {
          boolean _notEquals = (!Objects.equal(this.toMemento, null));
          if (_notEquals) {
            ObservableList<Animation> _children = it.getChildren();
            XRoot _root = context.getRoot();
            Duration _defaultUndoDuration = context.getDefaultUndoDuration();
            ViewportTransition _viewportTransition = new ViewportTransition(_root, this.toMemento, _defaultUndoDuration);
            _children.add(_viewportTransition);
          }
          boolean _notEquals_1 = (!Objects.equal(undoAnimation, null));
          if (_notEquals_1) {
            ObservableList<Animation> _children_1 = it.getChildren();
            _children_1.add(undoAnimation);
          }
        };
        _xifexpression = ObjectExtensions.<SequentialTransition>operator_doubleArrow(_sequentialTransition, _function);
      } else {
        _xifexpression = null;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public abstract Animation createUndoAnimation(final CommandContext context);
  
  @Override
  public Animation getRedoAnimation(final CommandContext context) {
    SequentialTransition _xblockexpression = null;
    {
      final Animation redoAnimation = this.createRedoAnimation(context);
      SequentialTransition _xifexpression = null;
      if (((!Objects.equal(this.fromMemento, null)) || (!Objects.equal(redoAnimation, null)))) {
        SequentialTransition _sequentialTransition = new SequentialTransition();
        final Procedure1<SequentialTransition> _function = (SequentialTransition it) -> {
          boolean _notEquals = (!Objects.equal(this.fromMemento, null));
          if (_notEquals) {
            ObservableList<Animation> _children = it.getChildren();
            XRoot _root = context.getRoot();
            Duration _defaultUndoDuration = context.getDefaultUndoDuration();
            ViewportTransition _viewportTransition = new ViewportTransition(_root, this.fromMemento, _defaultUndoDuration);
            _children.add(_viewportTransition);
          }
          boolean _notEquals_1 = (!Objects.equal(redoAnimation, null));
          if (_notEquals_1) {
            ObservableList<Animation> _children_1 = it.getChildren();
            _children_1.add(redoAnimation);
          }
        };
        _xifexpression = ObjectExtensions.<SequentialTransition>operator_doubleArrow(_sequentialTransition, _function);
      } else {
        _xifexpression = null;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public abstract Animation createRedoAnimation(final CommandContext context);
  
  @Override
  public boolean clearRedoStackOnExecute() {
    return true;
  }
  
  public Duration setExecuteDuration(final Duration executeDuration) {
    return this.executeDuration = executeDuration;
  }
  
  public Duration getExecuteDuration(final CommandContext context) {
    Duration _elvis = null;
    if (this.executeDuration != null) {
      _elvis = this.executeDuration;
    } else {
      Duration _defaultExecuteDuration = context.getDefaultExecuteDuration();
      _elvis = _defaultExecuteDuration;
    }
    return _elvis;
  }
}
