package jp.co.axa.apidemo.fanctor;

import lombok.NonNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Functor class to express either of success result or error status.
 *
 * @param <L> Type of wrong result. expected to be Exception or error status.
 * @param <R> Type of correct result. Right value is nullable.
 */
public class Either<L, R> {
  /**
   * Create the Either type with the wrong/error result.
   *
   * @param value error type. expected to be exception or enum of error status. Mustn't be null.
   * @return Either with error value.
   */
  public static <L, R> Either<L, R> left(@NonNull L value) {
    return new Either<>(Optional.of(value), Optional.empty());
  }

  /**
   * Create the either type with the correct result.
   *
   * @param value correct result. Though this is functor, null is acceptable,
   * @return Either with the correct value.
   */
  public static <L, R> Either<L, R> right(R value) {
    return new Either<>(Optional.empty(), Optional.ofNullable(value));
  }

  private final Optional<L> left;
  private final Optional<R> right;

  private Either(Optional<L> l, Optional<R> r) {
    left = l;
    right = r;
  }

  public boolean isLeft() {
    return this.left.isPresent();
  }

  public boolean isRight() {
    return !this.left.isPresent();
  }

  public L getLeft() {
    return this.left.orElse(null);
  }

  public R getRight() {
    return this.right.orElse(null);
  }

  public <T> T map(
    Function<? super L, ? extends T> lFunc,
    Function<? super R, ? extends T> rFunc) {
    return left.<T>map(lFunc).orElseGet(() -> right.map(rFunc).orElse(null));
  }

  public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> lFunc) {
    return new Either<>(left.map(lFunc), right);
  }

  public <T> Either<L, T> mapRight(Function<? super R, ? extends T> rFunc) {
    if (!left.isPresent() && !right.isPresent()) {
      return new Either<>(left, Optional.ofNullable(rFunc.apply(null)));
    }
    return new Either<>(left, right.map(rFunc));
  }

  public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
    left.ifPresent(lFunc);
    if (!left.isPresent() && !right.isPresent()) {
      rFunc.accept(null);
    } else {
      right.ifPresent(rFunc);
    }
  }
}
