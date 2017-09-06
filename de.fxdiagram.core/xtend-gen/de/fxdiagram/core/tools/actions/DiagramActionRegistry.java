package de.fxdiagram.core.tools.actions;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import de.fxdiagram.core.tools.actions.DiagramAction;
import eu.hansolo.enzo.radialmenu.SymbolType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class DiagramActionRegistry {
  private List<DiagramAction> actions = CollectionLiterals.<DiagramAction>newArrayList();
  
  private Map<SymbolType, DiagramAction> symbol2action = CollectionLiterals.<SymbolType, DiagramAction>newHashMap();
  
  public void operator_add(final Iterable<? extends DiagramAction> diagramActions) {
    final Consumer<DiagramAction> _function = (DiagramAction it) -> {
      this.operator_add(it);
    };
    diagramActions.forEach(_function);
  }
  
  public void operator_add(final DiagramAction diagramAction) {
    this.actions.add(diagramAction);
    SymbolType _symbol = diagramAction.getSymbol();
    boolean _notEquals = (!Objects.equal(_symbol, null));
    if (_notEquals) {
      this.symbol2action.put(diagramAction.getSymbol(), diagramAction);
    }
  }
  
  public void operator_remove(final DiagramAction diagramAction) {
    this.actions.remove(diagramAction);
    SymbolType _symbol = diagramAction.getSymbol();
    boolean _notEquals = (!Objects.equal(_symbol, null));
    if (_notEquals) {
      this.symbol2action.remove(diagramAction.getSymbol());
    }
  }
  
  public DiagramAction getBySymbol(final SymbolType symbol) {
    return this.symbol2action.get(symbol);
  }
  
  public ArrayList<DiagramAction> getActions() {
    return Lists.<DiagramAction>newArrayList(this.actions);
  }
}
