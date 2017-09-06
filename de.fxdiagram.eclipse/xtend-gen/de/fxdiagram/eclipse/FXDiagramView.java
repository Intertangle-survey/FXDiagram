package de.fxdiagram.eclipse;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import de.fxdiagram.annotations.logging.Logging;
import de.fxdiagram.core.XDiagram;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.layout.LayoutType;
import de.fxdiagram.core.model.DomainObjectDescriptor;
import de.fxdiagram.core.model.DomainObjectProvider;
import de.fxdiagram.core.model.ModelLoad;
import de.fxdiagram.core.services.ClassLoaderProvider;
import de.fxdiagram.core.tools.actions.CenterAction;
import de.fxdiagram.core.tools.actions.DeleteAction;
import de.fxdiagram.core.tools.actions.DiagramAction;
import de.fxdiagram.core.tools.actions.DiagramActionRegistry;
import de.fxdiagram.core.tools.actions.ExportSvgAction;
import de.fxdiagram.core.tools.actions.FullScreenAction;
import de.fxdiagram.core.tools.actions.LayoutAction;
import de.fxdiagram.core.tools.actions.NavigateNextAction;
import de.fxdiagram.core.tools.actions.NavigatePreviousAction;
import de.fxdiagram.core.tools.actions.ReconcileAction;
import de.fxdiagram.core.tools.actions.RedoAction;
import de.fxdiagram.core.tools.actions.RevealAction;
import de.fxdiagram.core.tools.actions.SelectAllAction;
import de.fxdiagram.core.tools.actions.UndoAction;
import de.fxdiagram.core.tools.actions.ZoomToFitAction;
import de.fxdiagram.eclipse.FXDiagramTab;
import de.fxdiagram.eclipse.actions.EclipseLoadAction;
import de.fxdiagram.eclipse.actions.EclipseSaveAction;
import de.fxdiagram.eclipse.changes.ModelChangeBroker;
import de.fxdiagram.lib.actions.UndoRedoPlayerAction;
import de.fxdiagram.mapping.AbstractMapping;
import de.fxdiagram.mapping.DiagramMapping;
import de.fxdiagram.mapping.DiagramMappingCall;
import de.fxdiagram.mapping.IMappedElementDescriptor;
import de.fxdiagram.mapping.IMappedElementDescriptorProvider;
import de.fxdiagram.mapping.XDiagramConfig;
import de.fxdiagram.mapping.execution.DiagramEntryCall;
import de.fxdiagram.mapping.execution.EntryCall;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * Embeds an {@link FXCanvas} with an {@link XRoot} in an eclipse {@link ViewPart}.
 * 
 * Uses {@link AbstractMapping} API to map domain objects to diagram elements.
 */
@Logging
@SuppressWarnings("all")
public class FXDiagramView extends ViewPart {
  private CTabFolder tabFolder;
  
  private Map<CTabItem, FXDiagramTab> tab2content = CollectionLiterals.<CTabItem, FXDiagramTab>newHashMap();
  
  private List<Pair<EventType<?>, EventHandler<?>>> globalEventHandlers = CollectionLiterals.<Pair<EventType<?>, EventHandler<?>>>newArrayList();
  
  @Accessors
  private boolean linkWithEditor;
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private ModelChangeBroker modelChangeBroker;
  
  @Override
  public void createPartControl(final Composite parent) {
    CTabFolder _cTabFolder = new CTabFolder(parent, (SWT.BORDER + SWT.BOTTOM));
    this.tabFolder = _cTabFolder;
    this.tabFolder.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
    IWorkbench _workbench = PlatformUI.getWorkbench();
    ModelChangeBroker _modelChangeBroker = new ModelChangeBroker(_workbench);
    this.modelChangeBroker = _modelChangeBroker;
    this.tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
      @Override
      public void close(final CTabFolderEvent event) {
        final FXDiagramTab tab = FXDiagramView.this.tab2content.get(event.item);
        event.doit = tab.confirmClose();
      }
    });
    final Command command = this.getSite().<ICommandService>getService(ICommandService.class).getCommand("de.fxdiagram.eclipse.LinkWithEditor");
    Boolean _elvis = null;
    State _state = command.getState(RegistryToggleState.STATE_ID);
    Object _value = null;
    if (_state!=null) {
      _value=_state.getValue();
    }
    if (((Boolean) _value) != null) {
      _elvis = ((Boolean) _value);
    } else {
      _elvis = Boolean.valueOf(false);
    }
    this.linkWithEditor = (_elvis).booleanValue();
  }
  
  public FXDiagramTab createNewTab() {
    FXDiagramTab _xblockexpression = null;
    {
      XRoot _createRoot = this.createRoot();
      final FXDiagramTab diagramTab = new FXDiagramTab(this, this.tabFolder, _createRoot);
      this.tab2content.put(diagramTab.getCTabItem(), diagramTab);
      this.tabFolder.setSelection(diagramTab.getCTabItem());
      final Consumer<Pair<EventType<?>, EventHandler<?>>> _function = (Pair<EventType<?>, EventHandler<?>> it) -> {
        EventType<?> _key = it.getKey();
        this.addEventHandlerWrapper(diagramTab.getRoot(), ((EventType<? extends Event>) _key), it.getValue());
      };
      this.globalEventHandlers.forEach(_function);
      diagramTab.setLinkWithEditor(this.linkWithEditor);
      _xblockexpression = diagramTab;
    }
    return _xblockexpression;
  }
  
  public FXDiagramTab removeTab(final CTabItem tab) {
    FXDiagramTab _xblockexpression = null;
    {
      this.tab2content.get(tab).confirmClose();
      _xblockexpression = this.tab2content.remove(tab);
    }
    return _xblockexpression;
  }
  
  protected XRoot createRoot() {
    XRoot _xRoot = new XRoot();
    final Procedure1<XRoot> _function = (XRoot it) -> {
      XDiagram _xDiagram = new XDiagram();
      it.setRootDiagram(_xDiagram);
      ObservableList<DomainObjectProvider> _domainObjectProviders = it.getDomainObjectProviders();
      ClassLoaderProvider _classLoaderProvider = new ClassLoaderProvider();
      _domainObjectProviders.add(_classLoaderProvider);
      ObservableList<DomainObjectProvider> _domainObjectProviders_1 = it.getDomainObjectProviders();
      final Function1<XDiagramConfig, IMappedElementDescriptorProvider> _function_1 = (XDiagramConfig it_1) -> {
        return it_1.getDomainObjectProvider();
      };
      Set<IMappedElementDescriptorProvider> _set = IterableExtensions.<IMappedElementDescriptorProvider>toSet(IterableExtensions.map(XDiagramConfig.Registry.getInstance().getConfigurations(), _function_1));
      Iterables.<DomainObjectProvider>addAll(_domainObjectProviders_1, _set);
      DiagramActionRegistry _diagramActionRegistry = it.getDiagramActionRegistry();
      CenterAction _centerAction = new CenterAction();
      DeleteAction _deleteAction = new DeleteAction();
      LayoutAction _layoutAction = new LayoutAction(LayoutType.DOT);
      ExportSvgAction _exportSvgAction = new ExportSvgAction();
      RedoAction _redoAction = new RedoAction();
      UndoRedoPlayerAction _undoRedoPlayerAction = new UndoRedoPlayerAction();
      UndoAction _undoAction = new UndoAction();
      RevealAction _revealAction = new RevealAction();
      EclipseLoadAction _eclipseLoadAction = new EclipseLoadAction();
      EclipseSaveAction _eclipseSaveAction = new EclipseSaveAction();
      ReconcileAction _reconcileAction = new ReconcileAction();
      SelectAllAction _selectAllAction = new SelectAllAction();
      ZoomToFitAction _zoomToFitAction = new ZoomToFitAction();
      NavigatePreviousAction _navigatePreviousAction = new NavigatePreviousAction();
      NavigateNextAction _navigateNextAction = new NavigateNextAction();
      FullScreenAction _fullScreenAction = new FullScreenAction();
      _diagramActionRegistry.operator_add(
        Collections.<DiagramAction>unmodifiableList(CollectionLiterals.<DiagramAction>newArrayList(_centerAction, _deleteAction, _layoutAction, _exportSvgAction, _redoAction, _undoRedoPlayerAction, _undoAction, _revealAction, _eclipseLoadAction, _eclipseSaveAction, _reconcileAction, _selectAllAction, _zoomToFitAction, _navigatePreviousAction, _navigateNextAction, _fullScreenAction)));
    };
    return ObjectExtensions.<XRoot>operator_doubleArrow(_xRoot, _function);
  }
  
  public void setLinkWithEditor(final boolean linkWithEditor) {
    this.linkWithEditor = linkWithEditor;
    final Consumer<FXDiagramTab> _function = (FXDiagramTab it) -> {
      it.setLinkWithEditor(linkWithEditor);
    };
    this.tab2content.values().forEach(_function);
  }
  
  private <T extends Event> void addEventHandlerWrapper(final XRoot root, final EventType<T> eventType, final EventHandler<?> handler) {
    root.<T>addEventHandler(eventType, ((EventHandler<? super T>) handler));
  }
  
  public <T extends Event> void addGlobalEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler) {
    Pair<EventType<?>, EventHandler<?>> _mappedTo = Pair.<EventType<?>, EventHandler<?>>of(eventType, eventHandler);
    this.globalEventHandlers.add(_mappedTo);
    final Consumer<FXDiagramTab> _function = (FXDiagramTab it) -> {
      it.getRoot().<T>addEventHandler(eventType, eventHandler);
    };
    this.tab2content.values().forEach(_function);
  }
  
  public <T extends Event> void removeGlobalEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler) {
    Pair<EventType<T>, EventHandler<? super T>> _mappedTo = Pair.<EventType<T>, EventHandler<? super T>>of(eventType, eventHandler);
    this.globalEventHandlers.remove(_mappedTo);
    final Consumer<FXDiagramTab> _function = (FXDiagramTab it) -> {
      it.getRoot().<T>removeEventHandler(eventType, eventHandler);
    };
    this.tab2content.values().forEach(_function);
  }
  
  protected FXDiagramTab getCurrentDiagramTab() {
    FXDiagramTab _xblockexpression = null;
    {
      final CTabItem currentTab = this.tabFolder.getSelection();
      FXDiagramTab _xifexpression = null;
      boolean _notEquals = (!Objects.equal(currentTab, null));
      if (_notEquals) {
        _xifexpression = this.tab2content.get(currentTab);
      } else {
        _xifexpression = null;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public XRoot getCurrentRoot() {
    FXDiagramTab _elvis = null;
    FXDiagramTab _currentDiagramTab = this.getCurrentDiagramTab();
    if (_currentDiagramTab != null) {
      _elvis = _currentDiagramTab;
    } else {
      FXDiagramTab _createNewTab = this.createNewTab();
      _elvis = _createNewTab;
    }
    return _elvis.getRoot();
  }
  
  @Override
  public void setFocus() {
    FXDiagramTab _currentDiagramTab = this.getCurrentDiagramTab();
    if (_currentDiagramTab!=null) {
      _currentDiagramTab.setFocus();
    }
  }
  
  public void clear() {
    FXDiagramTab _currentDiagramTab = this.getCurrentDiagramTab();
    if (_currentDiagramTab!=null) {
      _currentDiagramTab.clear();
    }
  }
  
  public <T extends Object> void revealElement(final T element, final EntryCall<? super T> entryCall, final IEditorPart editor) {
    try {
      if ((entryCall instanceof DiagramEntryCall<?, ?>)) {
        final DiagramMappingCall<?, T> mappingCall = ((DiagramEntryCall<?, T>) entryCall).getMappingCall();
        final Object diagramElement = mappingCall.getSelector().apply(element);
        final IMappedElementDescriptor<Object> diagramDescriptor = entryCall.getConfig().getDomainObjectProvider().<Object>createMappedElementDescriptor(diagramElement, mappingCall.getMapping());
        final Function1<Map.Entry<CTabItem, FXDiagramTab>, Boolean> _function = (Map.Entry<CTabItem, FXDiagramTab> it) -> {
          DomainObjectDescriptor _domainObjectDescriptor = it.getValue().getRoot().getDiagram().getDomainObjectDescriptor();
          return Boolean.valueOf(Objects.equal(_domainObjectDescriptor, diagramDescriptor));
        };
        final Map.Entry<CTabItem, FXDiagramTab> tab = IterableExtensions.<Map.Entry<CTabItem, FXDiagramTab>>findFirst(this.tab2content.entrySet(), _function);
        boolean _notEquals = (!Objects.equal(tab, null));
        if (_notEquals) {
          tab.getValue().<T>revealElement(element, entryCall, editor);
          this.tabFolder.setSelection(tab.getKey());
          return;
        }
        final FXDiagramTab newTab = this.createNewTab();
        AbstractMapping<?> _mapping = mappingCall.getMapping();
        final String filePath = ((DiagramMapping<Object>) _mapping).getDefaultFilePath(diagramElement);
        boolean _notEquals_1 = (!Objects.equal(filePath, null));
        if (_notEquals_1) {
          IWorkspaceRoot _root = ResourcesPlugin.getWorkspace().getRoot();
          Path _path = new Path(filePath);
          final IFile file = _root.getFile(_path);
          NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
          file.refreshLocal(IResource.DEPTH_ONE, _nullProgressMonitor);
          boolean _exists = file.exists();
          if (_exists) {
            ModelLoad _modelLoad = new ModelLoad();
            InputStream _contents = file.getContents();
            String _charset = file.getCharset();
            InputStreamReader _inputStreamReader = new InputStreamReader(_contents, _charset);
            final Object node = _modelLoad.load(_inputStreamReader);
            if ((node instanceof XRoot)) {
              final Runnable _function_1 = () -> {
                try {
                  newTab.getRoot().replaceDomainObjectProviders(((XRoot)node).getDomainObjectProviders());
                  XRoot _root_1 = newTab.getRoot();
                  _root_1.setRootDiagram(((XRoot)node).getDiagram());
                  XRoot _root_2 = newTab.getRoot();
                  _root_2.setFileName(filePath);
                } catch (final Throwable _t) {
                  if (_t instanceof Exception) {
                    final Exception exc = (Exception)_t;
                    exc.printStackTrace();
                    Shell _activeShell = Display.getCurrent().getActiveShell();
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("Error showing element in FXDiagram:");
                    _builder.newLine();
                    String _message = exc.getMessage();
                    _builder.append(_message);
                    _builder.newLineIfNotEmpty();
                    _builder.append("See log for details.");
                    _builder.newLine();
                    MessageDialog.openError(_activeShell, "Error", _builder.toString());
                  } else {
                    throw Exceptions.sneakyThrow(_t);
                  }
                }
              };
              Platform.runLater(_function_1);
              return;
            }
          }
        }
        final Procedure1<FXDiagramTab> _function_2 = (FXDiagramTab it) -> {
          XRoot _root_1 = it.getRoot();
          _root_1.setFileName(filePath);
          it.<T>revealElement(element, entryCall, editor);
        };
        ObjectExtensions.<FXDiagramTab>operator_doubleArrow(newTab, _function_2);
        return;
      }
      FXDiagramTab _elvis = null;
      FXDiagramTab _currentDiagramTab = this.getCurrentDiagramTab();
      if (_currentDiagramTab != null) {
        _elvis = _currentDiagramTab;
      } else {
        FXDiagramTab _createNewTab = this.createNewTab();
        _elvis = _createNewTab;
      }
      _elvis.<T>revealElement(element, entryCall, editor);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public void dispose() {
    super.dispose();
    this.tab2content.clear();
  }
  
  private static Logger LOG = Logger.getLogger("de.fxdiagram.eclipse.FXDiagramView");
    ;
  
  @Pure
  public boolean isLinkWithEditor() {
    return this.linkWithEditor;
  }
  
  @Pure
  public ModelChangeBroker getModelChangeBroker() {
    return this.modelChangeBroker;
  }
}
