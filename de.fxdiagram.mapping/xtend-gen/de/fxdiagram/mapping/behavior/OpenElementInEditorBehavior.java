package de.fxdiagram.mapping.behavior;

import de.fxdiagram.core.XDomainObjectShape;
import de.fxdiagram.core.XShape;
import de.fxdiagram.core.behavior.AbstractHostBehavior;
import de.fxdiagram.core.behavior.Behavior;
import de.fxdiagram.core.behavior.OpenBehavior;
import de.fxdiagram.core.model.DomainObjectDescriptor;
import de.fxdiagram.mapping.IMappedElementDescriptor;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Opens the domain object of this {@link XShape} in the respective Eclipse editor.
 */
@SuppressWarnings("all")
public class OpenElementInEditorBehavior extends AbstractHostBehavior<XDomainObjectShape> implements OpenBehavior {
  public OpenElementInEditorBehavior(final XDomainObjectShape host) {
    super(host);
  }
  
  @Override
  public Class<? extends Behavior> getBehaviorKey() {
    return OpenElementInEditorBehavior.class;
  }
  
  @Override
  protected void doActivate() {
    final EventHandler<MouseEvent> _function = (MouseEvent it) -> {
      int _clickCount = it.getClickCount();
      boolean _equals = (_clickCount == 2);
      if (_equals) {
        this.open();
        it.consume();
      }
    };
    this.getHost().<MouseEvent>addEventHandler(MouseEvent.MOUSE_CLICKED, _function);
  }
  
  @Override
  public void open() {
    final DomainObjectDescriptor descriptor = this.getHost().getDomainObjectDescriptor();
    if ((descriptor instanceof IMappedElementDescriptor<?>)) {
      ((IMappedElementDescriptor<?>)descriptor).openInEditor(true);
    }
  }
}
