package de.fxdiagram.core.command;

import com.google.common.collect.Iterables;
import de.fxdiagram.core.XConnection;
import de.fxdiagram.core.XDiagram;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.XShape;
import de.fxdiagram.core.command.AbstractAnimationCommand;
import de.fxdiagram.core.command.CommandContext;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class AddRemoveCommand extends AbstractAnimationCommand {
  private boolean isAdd;
  
  private XDiagram diagram;
  
  private Set<? extends XShape> shapes;
  
  private Map<XConnection, Pair<XNode, XNode>> connectedNodesMap = CollectionLiterals.<XConnection, Pair<XNode, XNode>>newHashMap();
  
  private Map<XShape, Double> shapeOpacities = CollectionLiterals.<XShape, Double>newHashMap();
  
  public static AddRemoveCommand newAddCommand(final XDiagram diagram, final XShape... shapes) {
    return new AddRemoveCommand(true, diagram, shapes);
  }
  
  public static AddRemoveCommand newRemoveCommand(final XDiagram diagram, final XShape... shapes) {
    return new AddRemoveCommand(false, diagram, shapes);
  }
  
  protected AddRemoveCommand(final boolean isAdd, final XDiagram diagram, final XShape... shapes) {
    this.isAdd = isAdd;
    this.diagram = diagram;
    this.shapes = IterableExtensions.<XShape>toSet(((Iterable<XShape>)Conversions.doWrapArray(shapes)));
  }
  
  @Override
  public Animation createExecuteAnimation(final CommandContext context) {
    final Consumer<XShape> _function = (XShape it) -> {
      this.shapeOpacities.put(it, Double.valueOf(it.getOpacity()));
    };
    this.shapes.forEach(_function);
    final Consumer<XNode> _function_1 = (XNode it) -> {
      if (this.isAdd) {
        boolean _contains = this.diagram.getNodes().contains(it);
        boolean _not = (!_contains);
        if (_not) {
          ObservableList<XNode> _nodes = this.diagram.getNodes();
          _nodes.add(it);
        }
      } else {
        boolean _contains_1 = this.diagram.getNodes().contains(it);
        if (_contains_1) {
          ObservableList<XNode> _nodes_1 = this.diagram.getNodes();
          _nodes_1.remove(it);
        }
      }
    };
    Iterables.<XNode>filter(this.shapes, XNode.class).forEach(_function_1);
    final Consumer<XConnection> _function_2 = (XConnection it) -> {
      XNode _source = it.getSource();
      XNode _target = it.getTarget();
      Pair<XNode, XNode> _mappedTo = Pair.<XNode, XNode>of(_source, _target);
      this.connectedNodesMap.put(it, _mappedTo);
      if (this.isAdd) {
        boolean _contains = this.diagram.getConnections().contains(it);
        boolean _not = (!_contains);
        if (_not) {
          ObservableList<XConnection> _connections = this.diagram.getConnections();
          _connections.add(it);
        }
      } else {
        it.setTarget(null);
        it.setSource(null);
        boolean _contains_1 = this.diagram.getConnections().contains(it);
        if (_contains_1) {
          ObservableList<XConnection> _connections_1 = this.diagram.getConnections();
          _connections_1.remove(it);
        }
      }
    };
    Iterables.<XConnection>filter(this.shapes, XConnection.class).forEach(_function_2);
    return null;
  }
  
  @Override
  public Animation createUndoAnimation(final CommandContext context) {
    ParallelTransition _xifexpression = null;
    if (this.isAdd) {
      _xifexpression = this.remove(context);
    } else {
      _xifexpression = this.add(context);
    }
    return _xifexpression;
  }
  
  @Override
  public Animation createRedoAnimation(final CommandContext context) {
    ParallelTransition _xifexpression = null;
    if (this.isAdd) {
      _xifexpression = this.add(context);
    } else {
      _xifexpression = this.remove(context);
    }
    return _xifexpression;
  }
  
  protected ParallelTransition remove(@Extension final CommandContext context) {
    ParallelTransition _parallelTransition = new ParallelTransition();
    final Procedure1<ParallelTransition> _function = (ParallelTransition it) -> {
      ObservableList<Animation> _children = it.getChildren();
      final Function1<XShape, Animation> _function_1 = (XShape it_1) -> {
        return this.disappear(it_1, context.getDefaultUndoDuration());
      };
      Iterable<Animation> _map = IterableExtensions.map(this.shapes, _function_1);
      Iterables.<Animation>addAll(_children, _map);
      final EventHandler<ActionEvent> _function_2 = (ActionEvent it_1) -> {
        final Consumer<XConnection> _function_3 = (XConnection it_2) -> {
          it_2.setTarget(null);
          it_2.setSource(null);
          boolean _contains = this.diagram.getConnections().contains(it_2);
          if (_contains) {
            ObservableList<XConnection> _connections = this.diagram.getConnections();
            _connections.remove(it_2);
          }
        };
        Iterables.<XConnection>filter(this.shapes, XConnection.class).forEach(_function_3);
        final Consumer<XNode> _function_4 = (XNode it_2) -> {
          boolean _contains = this.diagram.getNodes().contains(it_2);
          if (_contains) {
            ObservableList<XNode> _nodes = this.diagram.getNodes();
            _nodes.remove(it_2);
          }
        };
        Iterables.<XNode>filter(this.shapes, XNode.class).forEach(_function_4);
      };
      it.setOnFinished(_function_2);
    };
    return ObjectExtensions.<ParallelTransition>operator_doubleArrow(_parallelTransition, _function);
  }
  
  protected ParallelTransition add(@Extension final CommandContext context) {
    ParallelTransition _xblockexpression = null;
    {
      final Consumer<XNode> _function = (XNode it) -> {
        boolean _contains = this.diagram.getNodes().contains(it);
        boolean _not = (!_contains);
        if (_not) {
          ObservableList<XNode> _nodes = this.diagram.getNodes();
          _nodes.add(it);
        }
      };
      Iterables.<XNode>filter(this.shapes, XNode.class).forEach(_function);
      final Consumer<XConnection> _function_1 = (XConnection it) -> {
        final Pair<XNode, XNode> nodes = this.connectedNodesMap.get(it);
        it.setSource(nodes.getKey());
        it.setTarget(nodes.getValue());
        boolean _contains = this.diagram.getNodes().contains(it);
        boolean _not = (!_contains);
        if (_not) {
          ObservableList<XConnection> _connections = this.diagram.getConnections();
          _connections.add(it);
        }
      };
      Iterables.<XConnection>filter(this.shapes, XConnection.class).forEach(_function_1);
      ParallelTransition _parallelTransition = new ParallelTransition();
      final Procedure1<ParallelTransition> _function_2 = (ParallelTransition it) -> {
        ObservableList<Animation> _children = it.getChildren();
        final Function1<XShape, Animation> _function_3 = (XShape it_1) -> {
          return this.appear(it_1, context.getDefaultUndoDuration());
        };
        Iterable<Animation> _map = IterableExtensions.map(this.shapes, _function_3);
        Iterables.<Animation>addAll(_children, _map);
      };
      _xblockexpression = ObjectExtensions.<ParallelTransition>operator_doubleArrow(_parallelTransition, _function_2);
    }
    return _xblockexpression;
  }
  
  protected Animation appear(final XShape node, final Duration duration) {
    FadeTransition _fadeTransition = new FadeTransition();
    final Procedure1<FadeTransition> _function = (FadeTransition it) -> {
      it.setNode(node);
      it.setFromValue(0);
      it.setToValue((this.shapeOpacities.get(node)).doubleValue());
      it.setCycleCount(1);
      it.setDuration(duration);
    };
    return ObjectExtensions.<FadeTransition>operator_doubleArrow(_fadeTransition, _function);
  }
  
  protected Animation disappear(final XShape node, final Duration duration) {
    FadeTransition _fadeTransition = new FadeTransition();
    final Procedure1<FadeTransition> _function = (FadeTransition it) -> {
      it.setNode(node);
      it.setFromValue((this.shapeOpacities.get(node)).doubleValue());
      it.setToValue(0);
      it.setCycleCount(1);
      it.setDuration(duration);
    };
    return ObjectExtensions.<FadeTransition>operator_doubleArrow(_fadeTransition, _function);
  }
}
