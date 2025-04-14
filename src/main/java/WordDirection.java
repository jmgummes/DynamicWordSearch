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

    protected abstract int getYMultiplier();
    protected abstract int getXMultiplier();
  }
    
  public boolean isDefault() {
    return true;
  }
  
  protected WordDirection() {}
  
  private Axis getEffectiveAxis() {
    if(getAxis() != null)
      return getAxis();
    return Axis.ABSCISSA;
  }

  private Integer getEffectiveSign() { 
    if(getSign() != null)
      return getSign();
    return -1;
  }
  
  protected Axis getAxis() {
    return null;
  }
  
  protected Integer getSign() {
    return null;
  }
  
  public final int getDx() {
    return getDelta(getEffectiveAxis().getXMultiplier());
  }
  
  public final int getDy() {
    return getDelta(getEffectiveAxis().getYMultiplier());
  }

  private int getDelta(int multiplier) {
    return multiplier * getEffectiveSign();
  }
}