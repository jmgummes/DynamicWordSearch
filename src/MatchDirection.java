
public enum MatchDirection { 
  UP {
    @Override
    public int getDx() {
      return 0;
    }

    @Override
    public int getDy() {
      return -1;
    }
  },
  DOWN {
    @Override
    public int getDx() {
      return 0;
    }

    @Override
    public int getDy() {
      return 1;
    }
  },
  LEFT {
    @Override
    public int getDx() {
      return -1;
    }
    @Override
    public int getDy() {
      return 0;
    }
  },
  RIGHT {
    @Override
    public int getDx() {
      return 1;
    }

    @Override
    public int getDy() {
      return 0;
    }
  },
  UP_LEFT {
    @Override
    public int getDx() {
      return -1;
    }

    @Override
    public int getDy() {
      return -1;
    }
  },
  UP_RIGHT {
    @Override
    public int getDx() {
      return 1;
    }

    @Override
    public int getDy() {
      return -1;
    }
  },
  DOWN_LEFT {
    @Override
    public int getDx() {
      return -1;
    }

    @Override
    public int getDy() {
      return 1;
    }
  },
  DOWN_RIGHT
  {
    @Override
    public int getDx() {
      return 1;
    }

    @Override
    public int getDy() {
      return 1;
    }
  };
  
  public abstract int getDx();
  public abstract int getDy(); 
}
