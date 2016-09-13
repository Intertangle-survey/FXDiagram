package de.fxdiagram.core.tools.actions;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import de.fxdiagram.core.XConnection;
import de.fxdiagram.core.XDiagram;
import de.fxdiagram.core.XDomainObjectShape;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.XShape;
import de.fxdiagram.core.command.CommandContext;
import de.fxdiagram.core.command.CommandStack;
import de.fxdiagram.core.command.ViewportCommand;
import de.fxdiagram.core.extensions.BoundsExtensions;
import de.fxdiagram.core.extensions.CoreExtensions;
import de.fxdiagram.core.extensions.NumberExpressionExtensions;
import de.fxdiagram.core.tools.actions.DiagramAction;
import de.fxdiagram.core.viewport.ViewportTransition;
import eu.hansolo.enzo.radialmenu.SymbolType;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class CenterAction implements DiagramAction {
  @Override
  public boolean matches(final KeyEvent it) {
    return (it.isShortcutDown() && Objects.equal(it.getCode(), KeyCode.C));
  }
  
  @Override
  public SymbolType getSymbol() {
    return SymbolType.SELECTION2;
  }
  
  @Override
  public String getTooltip() {
    return "Center selection";
  }
  
  @Override
  public void perform(final XRoot root) {
    final ViewportCommand _function = new ViewportCommand() {
      @Override
      public ViewportTransition createViewportTransiton(final CommandContext it) {
        Iterable<? extends XShape> _xifexpression = null;
        Iterable<XShape> _currentSelection = root.getCurrentSelection();
        boolean _isEmpty = IterableExtensions.isEmpty(_currentSelection);
        if (_isEmpty) {
          XDiagram _diagram = root.getDiagram();
          ObservableList<XNode> _nodes = _diagram.getNodes();
          XDiagram _diagram_1 = root.getDiagram();
          ObservableList<XConnection> _connections = _diagram_1.getConnections();
          _xifexpression = Iterables.<XDomainObjectShape>concat(_nodes, _connections);
        } else {
          _xifexpression = root.getCurrentSelection();
        }
        final Iterable<? extends XShape> elements = _xifexpression;
        final Function1<XShape, Bounds> _function = (XShape it_1) -> {
          Bounds _snapBounds = it_1.getSnapBounds();
          return CoreExtensions.localToRootDiagram(it_1, _snapBounds);
        };
        Iterable<Bounds> _map = IterableExtensions.map(elements, _function);
        final Function2<Bounds, Bounds, Bounds> _function_1 = (Bounds a, Bounds b) -> {
          return BoundsExtensions.operator_plus(a, b);
        };
        final Bounds selectionBounds = IterableExtensions.<Bounds>reduce(_map, _function_1);
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
          return new ViewportTransition(root, _center, targetScale);
        } else {
          return null;
        }
      }
    };
    final ViewportCommand command = _function;
    CommandStack _commandStack = root.getCommandStack();
    _commandStack.execute(command);
  }
}
