/**
 * This class represents the direction of a word in the search grid with a default axis and a default
 * sign. It's used for words of length one because their direction doesn't matter.
 */
public class WordDirection {
  protected static enum Axis {
    ORDINATE {
      @Override
      protected int getYMultiplier() {
        return 1;
      }

      @Override
      protected int getXMultiplier() {
        return 0;
      }
    },
    ABSCISSA {
      @Override
      protected int getYMultiplier() {
        return 0;
      }

      @Override
      protected int getXMultiplier() {
        return 1;
      }
    },  
    MAJOR_DIAGONAL {
      @Override
      protected int getYMultiplier() {
        return 1;
      }

      @Override
      protected int getXMultiplier() {
        return 1;
      }
    },
    MINOR_DIAGONAL {
      @Override
      protected int getYMultiplier() {
        return -1;
      }

      @Override
      protected int getXMultiplier() {
        return 1;
      }
    };

    /**
     * @return y multiplier
     */
    protected abstract int getYMultiplier();
    
    /**
     * @return x multiplier
     */
    protected abstract int getXMultiplier();
  }
  
  /**
   * @return whether this is the default direction
   */
  public boolean isDefault() {
    return true;
  }
  
  protected WordDirection() {}
  
  /**
   * @return effective axis
   */
  private Axis getEffectiveAxis() {
    if(getAxis() != null)
      return getAxis();
    return Axis.ABSCISSA;
  }

  /**
   * @return effective sign
   */
  private Integer getEffectiveSign() { 
    if(getSign() != null)
      return getSign();
    return -1;
  }
  
  /**
   * @return sign
   */
  protected Axis getAxis() {
    return null;
  }
  
  /**
   * @return sign
   */
  protected Integer getSign() {
    return null;
  }
  
  /**
   * @return dx
   */
  public final int getDx() {
    return getDelta(getEffectiveAxis().getXMultiplier());
  }
  
  /**
   * @return dy
   */
  public final int getDy() {
    return getDelta(getEffectiveAxis().getYMultiplier());
  }

  /**
   * @param multiplier
   * @return delta
   */
  private int getDelta(int multiplier) {
    return multiplier * getEffectiveSign();
  }
}