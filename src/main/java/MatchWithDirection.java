public class MatchWithDirection extends Match {
  private WordDirection direction;
  
  protected MatchWithDirection(int searchGridRow, int searchGridCol, WordDirection direction) {
    super(searchGridRow, searchGridCol);
    this.direction = direction;
  }
  
  @Override
  public WordDirection getDirection() {
    return direction;
  }
}
