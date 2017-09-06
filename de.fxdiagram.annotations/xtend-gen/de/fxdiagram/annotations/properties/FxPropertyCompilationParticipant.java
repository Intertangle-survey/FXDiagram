package de.fxdiagram.annotations.properties;

import com.google.common.base.Objects;
import de.fxdiagram.annotations.properties.FxProperty;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.TransformationParticipant;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.CompilationStrategy;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableTypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.Type;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend.lib.macro.expression.Expression;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class FxPropertyCompilationParticipant implements TransformationParticipant<MutableFieldDeclaration> {
  @Override
  public void doTransform(final List<? extends MutableFieldDeclaration> fields, @Extension final TransformationContext context) {
    final Type annotationType = context.findTypeGlobally(FxProperty.class);
    for (final MutableFieldDeclaration f : fields) {
      {
        final AnnotationReference annotation = f.findAnnotation(annotationType);
        Object _value = annotation.getValue("readOnly");
        final boolean isReadOnly = (!Objects.equal(_value, Boolean.FALSE));
        String _qualifiedName = f.getType().getType().getQualifiedName();
        final boolean isList = Objects.equal(_qualifiedName, "javafx.collections.ObservableList");
        final String fieldName = f.getSimpleName();
        final TypeReference fieldType = f.getType();
        String _simpleName = f.getSimpleName();
        final String propName = (_simpleName + "Property");
        final TypeReference propType = this.toPropertyType(f.getType(), isReadOnly, context);
        final TypeReference propTypeAPI = this.toPropertyType_API(f.getType(), isReadOnly, context);
        MutableTypeDeclaration _declaringType = f.getDeclaringType();
        final MutableClassDeclaration clazz = ((MutableClassDeclaration) _declaringType);
        this.createField(f, clazz, propName, propType, fieldName, fieldType, isReadOnly, isList, propTypeAPI);
      }
    }
  }
  
  public void createField(final MutableFieldDeclaration f, final MutableClassDeclaration clazz, final String propName, final TypeReference propType, final String fieldName, final TypeReference fieldType, final boolean isReadOnly, final boolean isList, final TypeReference propTypeAPI) {
    Expression _initializer = f.getInitializer();
    boolean _tripleEquals = (_initializer == null);
    if (_tripleEquals) {
      final Procedure1<MutableFieldDeclaration> _function = (MutableFieldDeclaration it) -> {
        it.setType(propType);
        final CompilationStrategy _function_1 = (CompilationStrategy.CompilationContext it_1) -> {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("new ");
          String _javaCode = it_1.toJavaCode(propType);
          _builder.append(_javaCode);
          _builder.append("(this, \"");
          _builder.append(fieldName);
          _builder.append("\")");
          return _builder;
        };
        it.setInitializer(_function_1);
      };
      clazz.addField(propName, _function);
    } else {
      final Procedure1<MutableFieldDeclaration> _function_1 = (MutableFieldDeclaration it) -> {
        it.setType(propType);
        final CompilationStrategy _function_2 = (CompilationStrategy.CompilationContext it_1) -> {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("new ");
          String _javaCode = it_1.toJavaCode(propType);
          _builder.append(_javaCode);
          _builder.append("(this, \"");
          _builder.append(fieldName);
          _builder.append("\",_init");
          String _firstUpper = StringExtensions.toFirstUpper(fieldName);
          _builder.append(_firstUpper);
          _builder.append("())");
          return _builder;
        };
        it.setInitializer(_function_2);
      };
      clazz.addField(propName, _function_1);
      String _firstUpper = StringExtensions.toFirstUpper(fieldName);
      String _plus = ("_init" + _firstUpper);
      final Procedure1<MutableMethodDeclaration> _function_2 = (MutableMethodDeclaration it) -> {
        it.setReturnType(fieldType);
        it.setVisibility(Visibility.PRIVATE);
        it.setStatic(true);
        it.setFinal(true);
        it.setBody(f.getInitializer());
      };
      clazz.addMethod(_plus, _function_2);
    }
    String _firstUpper_1 = StringExtensions.toFirstUpper(fieldName);
    String _plus_1 = ("get" + _firstUpper_1);
    final Procedure1<MutableMethodDeclaration> _function_3 = (MutableMethodDeclaration it) -> {
      it.setReturnType(fieldType);
      final CompilationStrategy _function_4 = (CompilationStrategy.CompilationContext it_1) -> {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("return this.");
        _builder.append(propName);
        _builder.append(".get();");
        _builder.newLineIfNotEmpty();
        return _builder;
      };
      it.setBody(_function_4);
    };
    clazz.addMethod(_plus_1, _function_3);
    if (((!isReadOnly) && (!isList))) {
      String _firstUpper_2 = StringExtensions.toFirstUpper(fieldName);
      String _plus_2 = ("set" + _firstUpper_2);
      final Procedure1<MutableMethodDeclaration> _function_4 = (MutableMethodDeclaration it) -> {
        it.addParameter(fieldName, fieldType);
        final CompilationStrategy _function_5 = (CompilationStrategy.CompilationContext it_1) -> {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("this.");
          _builder.append(propName);
          _builder.append(".set(");
          _builder.append(fieldName);
          _builder.append(");");
          _builder.newLineIfNotEmpty();
          return _builder;
        };
        it.setBody(_function_5);
      };
      clazz.addMethod(_plus_2, _function_4);
    }
    final Procedure1<MutableMethodDeclaration> _function_5 = (MutableMethodDeclaration it) -> {
      it.setReturnType(propTypeAPI);
      final CompilationStrategy _function_6 = (CompilationStrategy.CompilationContext it_1) -> {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("return ");
        {
          if (isReadOnly) {
            _builder.append("this.");
            _builder.append(propName);
            _builder.append(".getReadOnlyProperty()");
          } else {
            _builder.append("this.");
            _builder.append(propName);
          }
        }
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        return _builder;
      };
      it.setBody(_function_6);
    };
    clazz.addMethod((fieldName + "Property"), _function_5);
    f.remove();
  }
  
  public String defaultValue(final TypeReference ref) {
    String _switchResult = null;
    String _string = ref.toString();
    if (_string != null) {
      switch (_string) {
        case "boolean":
          _switchResult = "false";
          break;
        case "double":
          _switchResult = "0d";
          break;
        case "float":
          _switchResult = "0f";
          break;
        case "long":
          _switchResult = "0";
          break;
        case "int":
          _switchResult = "0";
          break;
        default:
          _switchResult = "null";
          break;
      }
    } else {
      _switchResult = "null";
    }
    return _switchResult;
  }
  
  public TypeReference toPropertyType_API(final TypeReference ref, final boolean isReadOnly, @Extension final TransformationContext context) {
    TypeReference _xifexpression = null;
    if (isReadOnly) {
      TypeReference _switchResult = null;
      String _qualifiedName = ref.getType().getQualifiedName();
      if (_qualifiedName != null) {
        switch (_qualifiedName) {
          case "boolean":
            _switchResult = context.newTypeReference(ReadOnlyBooleanProperty.class);
            break;
          case "double":
            _switchResult = context.newTypeReference(ReadOnlyDoubleProperty.class);
            break;
          case "float":
            _switchResult = context.newTypeReference(ReadOnlyFloatProperty.class);
            break;
          case "long":
            _switchResult = context.newTypeReference(ReadOnlyLongProperty.class);
            break;
          case "java.lang.String":
            _switchResult = context.newTypeReference(ReadOnlyStringProperty.class);
            break;
          case "int":
            _switchResult = context.newTypeReference(ReadOnlyIntegerProperty.class);
            break;
          case "javafx.collections.ObservableList":
            _switchResult = context.newTypeReference(ReadOnlyListProperty.class, 
              IterableExtensions.<TypeReference>head(ref.getActualTypeArguments()));
            break;
          default:
            _switchResult = context.newTypeReference(ReadOnlyObjectProperty.class, ref);
            break;
        }
      } else {
        _switchResult = context.newTypeReference(ReadOnlyObjectProperty.class, ref);
      }
      _xifexpression = _switchResult;
    } else {
      TypeReference _switchResult_1 = null;
      String _qualifiedName_1 = ref.getType().getQualifiedName();
      if (_qualifiedName_1 != null) {
        switch (_qualifiedName_1) {
          case "boolean":
            _switchResult_1 = context.newTypeReference(BooleanProperty.class);
            break;
          case "double":
            _switchResult_1 = context.newTypeReference(DoubleProperty.class);
            break;
          case "float":
            _switchResult_1 = context.newTypeReference(FloatProperty.class);
            break;
          case "long":
            _switchResult_1 = context.newTypeReference(LongProperty.class);
            break;
          case "java.lang.String":
            _switchResult_1 = context.newTypeReference(StringProperty.class);
            break;
          case "int":
            _switchResult_1 = context.newTypeReference(IntegerProperty.class);
            break;
          case "javafx.collections.ObservableList":
            _switchResult_1 = context.newTypeReference(ListProperty.class, IterableExtensions.<TypeReference>head(ref.getActualTypeArguments()));
            break;
          default:
            _switchResult_1 = context.newTypeReference(ObjectProperty.class, ref);
            break;
        }
      } else {
        _switchResult_1 = context.newTypeReference(ObjectProperty.class, ref);
      }
      _xifexpression = _switchResult_1;
    }
    return _xifexpression;
  }
  
  public TypeReference toPropertyType(final TypeReference ref, final boolean isReadOnly, @Extension final TransformationContext context) {
    TypeReference _xifexpression = null;
    if (isReadOnly) {
      TypeReference _switchResult = null;
      String _qualifiedName = ref.getType().getQualifiedName();
      if (_qualifiedName != null) {
        switch (_qualifiedName) {
          case "boolean":
            _switchResult = context.newTypeReference(ReadOnlyBooleanWrapper.class);
            break;
          case "double":
            _switchResult = context.newTypeReference(ReadOnlyDoubleWrapper.class);
            break;
          case "float":
            _switchResult = context.newTypeReference(ReadOnlyFloatWrapper.class);
            break;
          case "long":
            _switchResult = context.newTypeReference(ReadOnlyLongWrapper.class);
            break;
          case "java.lang.String":
            _switchResult = context.newTypeReference(ReadOnlyStringWrapper.class);
            break;
          case "int":
            _switchResult = context.newTypeReference(ReadOnlyIntegerWrapper.class);
            break;
          case "javafx.collections.ObservableList":
            _switchResult = context.newTypeReference(ReadOnlyListWrapper.class, 
              IterableExtensions.<TypeReference>head(ref.getActualTypeArguments()));
            break;
          default:
            _switchResult = context.newTypeReference(ReadOnlyObjectWrapper.class, ref);
            break;
        }
      } else {
        _switchResult = context.newTypeReference(ReadOnlyObjectWrapper.class, ref);
      }
      _xifexpression = _switchResult;
    } else {
      TypeReference _switchResult_1 = null;
      String _qualifiedName_1 = ref.getType().getQualifiedName();
      if (_qualifiedName_1 != null) {
        switch (_qualifiedName_1) {
          case "boolean":
            _switchResult_1 = context.newTypeReference(SimpleBooleanProperty.class);
            break;
          case "double":
            _switchResult_1 = context.newTypeReference(SimpleDoubleProperty.class);
            break;
          case "float":
            _switchResult_1 = context.newTypeReference(SimpleFloatProperty.class);
            break;
          case "long":
            _switchResult_1 = context.newTypeReference(SimpleLongProperty.class);
            break;
          case "java.lang.String":
            _switchResult_1 = context.newTypeReference(SimpleStringProperty.class);
            break;
          case "int":
            _switchResult_1 = context.newTypeReference(SimpleIntegerProperty.class);
            break;
          case "javafx.collections.ObservableList":
            _switchResult_1 = context.newTypeReference(SimpleListProperty.class, 
              IterableExtensions.<TypeReference>head(ref.getActualTypeArguments()));
            break;
          default:
            _switchResult_1 = context.newTypeReference(SimpleObjectProperty.class, ref);
            break;
        }
      } else {
        _switchResult_1 = context.newTypeReference(SimpleObjectProperty.class, ref);
      }
      _xifexpression = _switchResult_1;
    }
    return _xifexpression;
  }
}
