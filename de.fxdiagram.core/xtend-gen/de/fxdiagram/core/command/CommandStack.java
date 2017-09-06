package de.fxdiagram.core.command;

import com.google.common.base.Objects;
import de.fxdiagram.core.XActivatable;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.command.AnimationCommand;
import de.fxdiagram.core.command.CommandContext;
import java.util.LinkedList;
import javafx.animation.Animation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function0;

/**
 * Executes and stores {@link AnimationCommands} for undo/redo functionality.
 * 
 * The command stack is reachable via the {@link XRoot} of the application.
 */
@SuppressWarnings("all")
public class CommandStack implements XActivatable {
  private LinkedList<AnimationCommand> undoStack = CollectionLiterals.<AnimationCommand>newLinkedList();
  
  private LinkedList<AnimationCommand> redoStack = CollectionLiterals.<AnimationCommand>newLinkedList();
  
  private CommandContext context;
  
  private AnimationCommand lastBeforeSave = null;
  
  public CommandStack(final XRoot root) {
    CommandContext _commandContext = new CommandContext(root);
    this.context = _commandContext;
  }
  
  @Override
  public void activate() {
    final ChangeListener<Boolean> _function = (ObservableValue<? extends Boolean> p, Boolean o, Boolean n) -> {
      if (((n).booleanValue() == false)) {
        this.lastBeforeSave = this.undoStack.peek();
      }
    };
    this.context.getRoot().needsSaveProperty().addListener(_function);
  }
  
  public void clear() {
    this.undoStack.clear();
    this.redoStack.clear();
  }
  
  public boolean canUndo() {
    boolean _isEmpty = this.undoStack.isEmpty();
    return (!_isEmpty);
  }
  
  public boolean canRedo() {
    boolean _isEmpty = this.redoStack.isEmpty();
    return (!_isEmpty);
  }
  
  public void undo() {
    boolean _canUndo = this.canUndo();
    if (_canUndo) {
      final AnimationCommand command = this.undoStack.pop();
      final Function0<Animation> _function = () -> {
        return command.getUndoAnimation(this.context);
      };
      this.context.getAnimationQueue().enqueue(_function);
      this.redoStack.push(command);
      XRoot _root = this.context.getRoot();
      AnimationCommand _peek = this.undoStack.peek();
      boolean _notEquals = (!Objects.equal(_peek, this.lastBeforeSave));
      _root.setNeedsSave(_notEquals);
    }
  }
  
  public void redo() {
    boolean _canRedo = this.canRedo();
    if (_canRedo) {
      final AnimationCommand command = this.redoStack.pop();
      final Function0<Animation> _function = () -> {
        return command.getRedoAnimation(this.context);
      };
      this.context.getAnimationQueue().enqueue(_function);
      this.undoStack.push(command);
      XRoot _root = this.context.getRoot();
      AnimationCommand _peek = this.undoStack.peek();
      boolean _notEquals = (!Objects.equal(_peek, this.lastBeforeSave));
      _root.setNeedsSave(_notEquals);
    }
  }
  
  public void execute(final AnimationCommand command) {
    final Function0<Animation> _function = () -> {
      return command.getExecuteAnimation(this.context);
    };
    this.context.getAnimationQueue().enqueue(_function);
    this.undoStack.push(command);
    XRoot _root = this.context.getRoot();
    AnimationCommand _peek = this.undoStack.peek();
    boolean _notEquals = (!Objects.equal(_peek, this.lastBeforeSave));
    _root.setNeedsSave(_notEquals);
    boolean _clearRedoStackOnExecute = command.clearRedoStackOnExecute();
    if (_clearRedoStackOnExecute) {
      this.redoStack.clear();
    }
  }
  
  public CommandContext getContext() {
    return this.context;
  }
}
