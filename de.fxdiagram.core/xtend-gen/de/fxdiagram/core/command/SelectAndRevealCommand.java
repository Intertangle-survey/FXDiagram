package de.fxdiagram.core.command;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import de.fxdiagram.core.XConnection;
import de.fxdiagram.core.XDiagram;
import de.fxdiagram.core.XDomainObjectShape;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.XShape;
import de.fxdiagram.core.command.CommandContext;
import de.fxdiagram.core.command.ViewportCommand;
import de.fxdiagram.core.extensions.BoundsExtensions;
import de.fxdiagram.core.extensions.CoreExtensions;
import de.fxdiagram.core.extensions.DurationExtensions;
import de.fxdiagram.core.extensions.NumberExpressionExtensions;
import de.fxdiagram.core.viewport.ViewportTransition;
import java.util.Set;
import java.util.function.Consumer;
import javafx.animation.Animation;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.util.Duration;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class SelectAndRevealCommand extends ViewportCommand {
  private Set<XShape> originalSelection = CollectionLiterals.<XShape>newHashSet();
  
  private Function1<? super XShape, ? extends Boolean> selectionPredicate;
  
  public SelectAndRevealCommand(final XRoot root, final Function1<? super XShape, ? extends Boolean> selectionPredicate) {
    this.selectionPredicate = selectionPredicate;
  }
  
  @Override
  public ViewportTransition createViewportTransiton(final CommandContext context) {
    final XRoot root = context.getRoot();
    Iterable<XShape> _currentSelection = root.getCurrentSelection();
    Iterables.<XShape>addAll(this.originalSelection, _currentSelection);
    XDiagram _diagram = root.getDiagram();
    ObservableList<XNode> _nodes = _diagram.getNodes();
    XDiagram _diagram_1 = root.getDiagram();
    ObservableList<XConnection> _connections = _diagram_1.getConnections();
    Iterable<XDomainObjectShape> _plus = Iterables.<XDomainObjectShape>concat(_nodes, _connections);
    final Function1<XDomainObjectShape, XDomainObjectShape> _function = (XDomainObjectShape it) -> {
      Boolean _apply = this.selectionPredicate.apply(it);
      if ((_apply).booleanValue()) {
        it.setSelected(true);
        return it;
      } else {
        it.setSelected(false);
        return null;
      }
    };
    Iterable<XDomainObjectShape> _map = IterableExtensions.<XDomainObjectShape, XDomainObjectShape>map(_plus, _function);
    Iterable<XDomainObjectShape> selection = IterableExtensions.<XDomainObjectShape>filterNull(_map);
    Iterable<XDomainObjectShape> _xifexpression = null;
    boolean _isEmpty = IterableExtensions.isEmpty(selection);
    if (_isEmpty) {
      XDiagram _diagram_2 = root.getDiagram();
      ObservableList<XNode> _nodes_1 = _diagram_2.getNodes();
      XDiagram _diagram_3 = root.getDiagram();
      ObservableList<XConnection> _connections_1 = _diagram_3.getConnections();
      _xifexpression = Iterables.<XDomainObjectShape>concat(_nodes_1, _connections_1);
    } else {
      _xifexpression = selection;
    }
    selection = _xifexpression;
    final Function1<XDomainObjectShape, Bounds> _function_1 = (XDomainObjectShape it) -> {
      Bounds _snapBounds = it.getSnapBounds();
      return CoreExtensions.localToRootDiagram(it, _snapBounds);
    };
    Iterable<Bounds> _map_1 = IterableExtensions.<XDomainObjectShape, Bounds>map(selection, _function_1);
    Iterable<Bounds> _filterNull = IterableExtensions.<Bounds>filterNull(_map_1);
    final Function2<Bounds, Bounds, Bounds> _function_2 = (Bounds a, Bounds b) -> {
      return BoundsExtensions.operator_plus(a, b);
    };
    final Bounds selectionBounds = IterableExtensions.<Bounds>reduce(_filterNull, _function_2);
    if ((((!Objects.equal(selectionBounds, null)) && (selectionBounds.getWidth() > NumberExpressionExtensions.EPSILON)) && (selectionBounds.getHeight() > NumberExpressionExtensions.EPSILON))) {
      Scene _scene = root.getScene();
      double _width = _scene.getWidth();
      double _width_1 = selectionBounds.getWidth();
      double _divide = (_width / _width_1);
      Scene _scene_1 = root.getScene();
      double _height = _scene_1.getHeight();
      double _height_1 = selectionBounds.getHeight();
      double _divide_1 = (_height / _height_1);
      double _min = Math.min(_divide, _divide_1);
      final double targetScale = Math.min(1, _min);
      Point2D _center = BoundsExtensions.center(selectionBounds);
      ViewportTransition _viewportTransition = new ViewportTransition(root, _center, targetScale);
      final Procedure1<ViewportTransition> _function_3 = (ViewportTransition it) -> {
        Duration _millis = DurationExtensions.millis(400);
        it.setMaxDuration(_millis);
      };
      return ObjectExtensions.<ViewportTransition>operator_doubleArrow(_viewportTransition, _function_3);
    } else {
      return null;
    }
  }
  
  @Override
  public Animation getUndoAnimation(final CommandContext context) {
    Animation _xblockexpression = null;
    {
      XRoot _root = context.getRoot();
      XDiagram _diagram = _root.getDiagram();
      ObservableList<XNode> _nodes = _diagram.getNodes();
      XRoot _root_1 = context.getRoot();
      XDiagram _diagram_1 = _root_1.getDiagram();
      ObservableList<XConnection> _connections = _diagram_1.getConnections();
      Iterable<XDomainObjectShape> _plus = Iterables.<XDomainObjectShape>concat(_nodes, _connections);
      final Consumer<XDomainObjectShape> _function = (XDomainObjectShape it) -> {
        boolean _contains = this.originalSelection.contains(it);
        it.setSelected(_contains);
      };
      _plus.forEach(_function);
      _xblockexpression = super.getUndoAnimation(context);
    }
    return _xblockexpression;
  }
  
  @Override
  public Animation getRedoAnimation(final CommandContext context) {
    Animation _xblockexpression = null;
    {
      XRoot _root = context.getRoot();
      XDiagram _diagram = _root.getDiagram();
      ObservableList<XNode> _nodes = _diagram.getNodes();
      XRoot _root_1 = context.getRoot();
      XDiagram _diagram_1 = _root_1.getDiagram();
      ObservableList<XConnection> _connections = _diagram_1.getConnections();
      Iterable<XDomainObjectShape> _plus = Iterables.<XDomainObjectShape>concat(_nodes, _connections);
      final Consumer<XDomainObjectShape> _function = (XDomainObjectShape it) -> {
        Boolean _apply = this.selectionPredicate.apply(it);
        it.setSelected((_apply).booleanValue());
      };
      _plus.forEach(_function);
      _xblockexpression = super.getRedoAnimation(context);
    }
    return _xblockexpression;
  }
}
