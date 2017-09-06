package de.fxdiagram.core.model;

import de.fxdiagram.core.model.ModelLoad;
import de.fxdiagram.core.model.TestBean;
import de.fxdiagram.core.model.TestEnum;
import java.io.StringReader;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class ModelPersistenceTest {
  @Test
  public void testReadEnum() {
    ModelLoad _modelLoad = new ModelLoad();
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("{");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("\"__class\":\"de.fxdiagram.core.model.TestBean\",");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("\"testEnum\":\"BAR\"");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    StringReader _stringReader = new StringReader(_builder.toString());
    final Object object = _modelLoad.load(_stringReader);
    Assert.assertTrue((object instanceof TestBean));
    Assert.assertEquals(TestEnum.BAR, ((TestBean) object).getTestEnum());
  }
}
