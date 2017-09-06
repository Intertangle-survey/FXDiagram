package de.fxdiagram.core.command;

import com.google.common.base.Objects;
import java.util.Iterator;
import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class ChainedAnimationUtil {
  public static <T extends Object> Animation createChainedAnimation(final Iterable<T> iterable, final Function1<? super T, ? extends Animation> animationFactory) {
    return ChainedAnimationUtil.<T>createChainedAnimation(iterable.iterator(), animationFactory);
  }
  
  protected static <T extends Object> Animation createChainedAnimation(final Iterator<T> iterator, final Function1<? super T, ? extends Animation> animationFactory) {
    boolean _hasNext = iterator.hasNext();
    boolean _not = (!_hasNext);
    if (_not) {
      return null;
    }
    final Animation nextAnimation = animationFactory.apply(iterator.next());
    boolean _equals = Objects.equal(nextAnimation, null);
    if (_equals) {
      return ChainedAnimationUtil.<T>createChainedAnimation(iterator, animationFactory);
    }
    SequentialTransition _sequentialTransition = new SequentialTransition();
    final Procedure1<SequentialTransition> _function = (SequentialTransition it) -> {
      ObservableList<Animation> _children = it.getChildren();
      _children.add(nextAnimation);
      final EventHandler<ActionEvent> _function_1 = (ActionEvent it_1) -> {
        Animation _createChainedAnimation = ChainedAnimationUtil.<T>createChainedAnimation(iterator, animationFactory);
        if (_createChainedAnimation!=null) {
          _createChainedAnimation.playFromStart();
        }
      };
      it.setOnFinished(_function_1);
    };
    return ObjectExtensions.<SequentialTransition>operator_doubleArrow(_sequentialTransition, _function);
  }
}
