package de.fxdiagram.mapping;

import de.fxdiagram.annotations.logging.Logging;
import de.fxdiagram.mapping.AbstractLabelMapping;
import de.fxdiagram.mapping.AbstractMapping;
import de.fxdiagram.mapping.IMappedElementDescriptorProvider;
import de.fxdiagram.mapping.MappingAcceptor;
import de.fxdiagram.mapping.XDiagramConfig;
import de.fxdiagram.mapping.execution.EntryCall;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Pure;

@Logging
@SuppressWarnings("all")
public abstract class AbstractDiagramConfig implements XDiagramConfig {
  private Map<String, AbstractMapping<?>> mappings = CollectionLiterals.<String, AbstractMapping<?>>newHashMap();
  
  @Accessors
  private String ID;
  
  @Accessors
  private String label;
  
  private IMappedElementDescriptorProvider domainObjectProvider;
  
  protected abstract IMappedElementDescriptorProvider createDomainObjectProvider();
  
  @Override
  public AbstractMapping<?> getMappingByID(final String mappingID) {
    return this.mappings.get(mappingID);
  }
  
  protected abstract <ARG extends Object> void entryCalls(final ARG domainArgument, final MappingAcceptor<ARG> acceptor);
  
  @Override
  public Iterable<? extends AbstractMapping<?>> getMappings() {
    return this.mappings.values();
  }
  
  @Override
  public <ARG extends Object> Iterable<? extends EntryCall<ARG>> getEntryCalls(final ARG domainArgument) {
    List<EntryCall<ARG>> _xblockexpression = null;
    {
      final MappingAcceptor<ARG> acceptor = new MappingAcceptor<ARG>();
      this.<ARG>entryCalls(domainArgument, acceptor);
      _xblockexpression = acceptor.getEntryCalls();
    }
    return _xblockexpression;
  }
  
  @Override
  public <ARG extends Object> void addMapping(final AbstractMapping<ARG> mapping) {
    if (((mapping instanceof AbstractLabelMapping<?>) || (!this.mappings.containsKey(mapping.getID())))) {
      String _iD = mapping.getID();
      this.mappings.put(_iD, mapping);
    } else {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Duplicate mapping id=");
      String _iD_1 = mapping.getID();
      _builder.append(_iD_1, "");
      _builder.append(" in ");
      _builder.append(this.ID, "");
      AbstractDiagramConfig.LOG.severe(_builder.toString());
    }
  }
  
  @Override
  public IMappedElementDescriptorProvider getDomainObjectProvider() {
    IMappedElementDescriptorProvider _elvis = null;
    if (this.domainObjectProvider != null) {
      _elvis = this.domainObjectProvider;
    } else {
      IMappedElementDescriptorProvider _createDomainObjectProvider = this.createDomainObjectProvider();
      IMappedElementDescriptorProvider _domainObjectProvider = (this.domainObjectProvider = _createDomainObjectProvider);
      _elvis = _domainObjectProvider;
    }
    return _elvis;
  }
  
  private static Logger LOG = Logger.getLogger("de.fxdiagram.mapping.AbstractDiagramConfig");
    ;
  
  @Pure
  public String getID() {
    return this.ID;
  }
  
  public void setID(final String ID) {
    this.ID = ID;
  }
  
  @Pure
  public String getLabel() {
    return this.label;
  }
  
  public void setLabel(final String label) {
    this.label = label;
  }
}
