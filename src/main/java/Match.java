public class Match {
  private int searchGridRow;
  private int searchGridCol;
  
  public static Match create(int searchGridRow, int searchGridCol, WordDirection direction) {
    if(direction.isDefault())
      return new Match(searchGridRow, searchGridCol);
    return new MatchWithDirection(searchGridRow, searchGridCol, direction);
  }
  
  protected Match(int searchGridRow, int searchGridCol) {
    this.searchGridRow = searchGridRow;
    this.searchGridCol = searchGridCol;
  }
  
  public int getSearchGridRow() {
    return searchGridRow;
  }
  
  public int getSearchGridCol() {
    return searchGridCol;
  }
  
  public WordDirection getDirection() {
    return new WordDirection();
  }
}
