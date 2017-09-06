package de.fxdiagram.annotations.logging;

import java.util.logging.Logger;
import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.CompilationStrategy;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class LoggingProcessor extends AbstractClassProcessor {
  @Override
  public void doTransform(final MutableClassDeclaration annotatedClass, @Extension final TransformationContext context) {
    final Procedure1<MutableFieldDeclaration> _function = (MutableFieldDeclaration it) -> {
      it.setStatic(true);
      it.setType(context.newTypeReference(context.findTypeGlobally(Logger.class)));
      final CompilationStrategy _function_1 = (CompilationStrategy.CompilationContext it_1) -> {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("Logger.getLogger(\"");
        String _qualifiedName = annotatedClass.getQualifiedName();
        _builder.append(_qualifiedName);
        _builder.append("\");");
        _builder.newLineIfNotEmpty();
        return _builder;
      };
      it.setInitializer(_function_1);
    };
    annotatedClass.addField("LOG", _function);
  }
}
