public class Match {
  private int searchGridRow;
  private int searchGridCol;
  
  public Match(int searchGridRow, int searchGridCol) {
    this.searchGridRow = searchGridRow;
    this.searchGridCol = searchGridCol;
  }
  
  public int getSearchGridRow() {
    return searchGridRow;
  }
  
  public int getSearchGridCol() {
    return searchGridCol;
  }
  
  public MatchDirection getDirection() {
    return null;
  }
}
