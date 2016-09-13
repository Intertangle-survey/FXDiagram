package de.fxdiagram.core.model;

import com.google.common.base.Objects;
import de.fxdiagram.annotations.logging.Logging;
import de.fxdiagram.core.extensions.ClassLoaderExtensions;
import de.fxdiagram.core.model.Model;
import de.fxdiagram.core.model.ModelElement;
import de.fxdiagram.core.model.ModelRepairer;
import de.fxdiagram.core.tools.actions.SaveAction;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.ExclusiveRange;
import org.eclipse.xtext.xbase.lib.Pair;

/**
 * Functionality of a {@link SaveAction}
 */
@Logging
@SuppressWarnings("all")
public class ModelSave {
  private Map<ModelElement, String> idMap;
  
  private Model model;
  
  public void save(final Object root, final Writer out) {
    ModelRepairer _modelRepairer = new ModelRepairer();
    _modelRepairer.repair(root);
    Model _model = new Model(root);
    this.model = _model;
    ModelElement _rootElement = this.model.getRootElement();
    boolean _notEquals = (!Objects.equal(_rootElement, null));
    if (_notEquals) {
      HashMap<ModelElement, String> _newHashMap = CollectionLiterals.<ModelElement, String>newHashMap();
      this.idMap = _newHashMap;
      Pair<String, Boolean> _mappedTo = Pair.<String, Boolean>of(JsonGenerator.PRETTY_PRINTING, Boolean.valueOf(true));
      JsonGeneratorFactory _createGeneratorFactory = Json.createGeneratorFactory(Collections.<String, Boolean>unmodifiableMap(CollectionLiterals.<String, Boolean>newHashMap(_mappedTo)));
      JsonGenerator _createGenerator = _createGeneratorFactory.createGenerator(out);
      ModelElement _rootElement_1 = this.model.getRootElement();
      JsonGenerator _write = this.write(_createGenerator, _rootElement_1, null, "");
      _write.close();
    }
  }
  
  protected JsonGenerator write(final JsonGenerator gen, final ModelElement element, final String propertyName, final String currentId) {
    boolean _equals = Objects.equal(element, null);
    if (_equals) {
      gen.writeNull(propertyName);
    } else {
      final String cachedId = this.idMap.get(element);
      boolean _notEquals = (!Objects.equal(cachedId, null));
      if (_notEquals) {
        boolean _notEquals_1 = (!Objects.equal(propertyName, null));
        if (_notEquals_1) {
          gen.write(propertyName, cachedId);
        } else {
          gen.write(cachedId);
        }
      } else {
        this.idMap.put(element, currentId);
        Object _node = element.getNode();
        Class<?> _class = _node.getClass();
        final String className = ClassLoaderExtensions.serialize(_class);
        boolean _notEquals_2 = (!Objects.equal(propertyName, null));
        if (_notEquals_2) {
          gen.writeStartObject(propertyName);
        } else {
          gen.writeStartObject();
        }
        gen.write("__class", className);
        List<? extends Property<?>> _properties = element.getProperties();
        final Consumer<Property<?>> _function = (Property<?> it) -> {
          Class<?> _type = element.getType(it);
          this.write(gen, it, _type, currentId);
        };
        _properties.forEach(_function);
        List<? extends ListProperty<?>> _listProperties = element.getListProperties();
        final Consumer<ListProperty<?>> _function_1 = (ListProperty<?> it) -> {
          Class<?> _type = element.getType(it);
          this.write(gen, it, _type, currentId);
        };
        _listProperties.forEach(_function_1);
        gen.writeEnd();
      }
    }
    return gen;
  }
  
  protected JsonGenerator write(final JsonGenerator gen, final Property<?> property, final Class<?> propertyType, final String currentId) {
    JsonGenerator _xifexpression = null;
    if (((!Objects.equal(property.getValue(), null)) && (!property.isBound()))) {
      JsonGenerator _switchResult = null;
      boolean _matched = false;
      if (Objects.equal(propertyType, String.class)) {
        _matched=true;
        String _name = property.getName();
        String _value = ((StringProperty) property).getValue();
        _switchResult = gen.write(_name, _value);
      }
      if (!_matched) {
        if (Objects.equal(propertyType, Double.class)) {
          _matched=true;
          String _name_1 = property.getName();
          double _doubleValue = ((DoubleProperty) property).doubleValue();
          _switchResult = gen.write(_name_1, _doubleValue);
        }
      }
      if (!_matched) {
        if (Objects.equal(propertyType, Float.class)) {
          _matched=true;
          String _name_2 = property.getName();
          float _floatValue = ((FloatProperty) property).floatValue();
          _switchResult = gen.write(_name_2, _floatValue);
        }
      }
      if (!_matched) {
        if (Objects.equal(propertyType, Long.class)) {
          _matched=true;
          String _name_3 = property.getName();
          long _longValue = ((LongProperty) property).longValue();
          _switchResult = gen.write(_name_3, _longValue);
        }
      }
      if (!_matched) {
        if (Objects.equal(propertyType, Integer.class)) {
          _matched=true;
          String _name_4 = property.getName();
          int _intValue = ((IntegerProperty) property).intValue();
          _switchResult = gen.write(_name_4, _intValue);
        }
      }
      if (!_matched) {
        if (Objects.equal(propertyType, Boolean.class)) {
          _matched=true;
          String _name_5 = property.getName();
          Boolean _value_1 = ((BooleanProperty) property).getValue();
          boolean _booleanValue = _value_1.booleanValue();
          _switchResult = gen.write(_name_5, _booleanValue);
        }
      }
      if (!_matched) {
        boolean _isAssignableFrom = Enum.class.isAssignableFrom(propertyType);
        if (_isAssignableFrom) {
          _matched=true;
          String _name_6 = property.getName();
          Object _value_2 = property.getValue();
          String _string = _value_2.toString();
          _switchResult = gen.write(_name_6, _string);
        }
      }
      if (!_matched) {
        Map<Object, ModelElement> _index = this.model.getIndex();
        Object _value_3 = property.getValue();
        ModelElement _get = _index.get(_value_3);
        String _name_7 = property.getName();
        String _name_8 = property.getName();
        String _plus = ((currentId + "/") + _name_8);
        _switchResult = this.write(gen, _get, _name_7, _plus);
      }
      _xifexpression = _switchResult;
    }
    return _xifexpression;
  }
  
  protected JsonGenerator write(final JsonGenerator gen, final ListProperty<?> property, final Class<?> propertyType, final String currentId) {
    JsonGenerator _xblockexpression = null;
    {
      String _name = property.getName();
      gen.writeStartArray(_name);
      ObservableList<?> _value = property.getValue();
      int _size = _value.size();
      ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, _size, true);
      for (final Integer i : _doubleDotLessThan) {
        {
          ObservableList<?> _value_1 = property.getValue();
          final Object value = _value_1.get((i).intValue());
          boolean _matched = false;
          if (Objects.equal(propertyType, String.class)) {
            _matched=true;
            gen.write(((String) value));
          }
          if (!_matched) {
            if (Objects.equal(propertyType, Double.class)) {
              _matched=true;
              double _doubleValue = ((Double) value).doubleValue();
              gen.write(_doubleValue);
            }
          }
          if (!_matched) {
            if (Objects.equal(propertyType, Float.class)) {
              _matched=true;
              float _floatValue = ((Float) value).floatValue();
              gen.write(_floatValue);
            }
          }
          if (!_matched) {
            if (Objects.equal(propertyType, Long.class)) {
              _matched=true;
              long _longValue = ((Long) value).longValue();
              gen.write(_longValue);
            }
          }
          if (!_matched) {
            if (Objects.equal(propertyType, Integer.class)) {
              _matched=true;
              int _intValue = ((Integer) value).intValue();
              gen.write(_intValue);
            }
          }
          if (!_matched) {
            if (Objects.equal(propertyType, Boolean.class)) {
              _matched=true;
              boolean _booleanValue = ((Boolean) value).booleanValue();
              gen.write(_booleanValue);
            }
          }
          if (!_matched) {
            boolean _isAssignableFrom = Enum.class.isAssignableFrom(propertyType);
            if (_isAssignableFrom) {
              _matched=true;
              String _string = value.toString();
              gen.write(_string);
            }
          }
          if (!_matched) {
            Map<Object, ModelElement> _index = this.model.getIndex();
            boolean _containsKey = _index.containsKey(value);
            if (_containsKey) {
              Map<Object, ModelElement> _index_1 = this.model.getIndex();
              ModelElement _get = _index_1.get(value);
              String _name_1 = property.getName();
              String _plus = ((currentId + "/") + _name_1);
              String _plus_1 = (_plus + ".");
              String _plus_2 = (_plus_1 + i);
              this.write(gen, _get, 
                null, _plus_2);
            }
          }
        }
      }
      _xblockexpression = gen.writeEnd();
    }
    return _xblockexpression;
  }
  
  private static Logger LOG = Logger.getLogger("de.fxdiagram.core.model.ModelSave");
    ;
}
