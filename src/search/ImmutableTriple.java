package search;

import lombok.Getter;

/**
 * An immutable triple consisting of three elements.
 *
 * @param <L>
 *        the left element type
 * @param <M>
 *        the middle element type
 * @param <R>
 *        the right element type
 */
@Getter
public final class ImmutableTriple<L, M, R> {

  /** Left object */
  public final L left;
  /** Middle object */
  public final M middle;
  /** Right object */
  public final R right;

  /**
   * Obtains an immutable triple of from three objects inferring the generic types.
   */
  public static <L, M, R> ImmutableTriple<L, M, R> of(final L left, final M middle, final R right) {
    return new ImmutableTriple<L, M, R>(left, middle, right);
  }

  /**
   * Create a new triple instance.
   */
  public ImmutableTriple(final L left, final M middle, final R right) {
    this.left = left;
    this.middle = middle;
    this.right = right;
  }

}
