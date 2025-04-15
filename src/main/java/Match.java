/**
 * This class represents the location in the search grid of consecutive characters in the default
 * direction. It's used for matches of length one because their direction doesn't matter. The 
 * location in the search grid is represented as an int searchGridRow and an int searchGridCol.
 */
public class Match {
  private int searchGridRow;
  private int searchGridCol;
  
  /**
   * @param searchGridRow
   * @param searchGridCol
   * @param direction
   * @return match
   */
  public static Match create(int searchGridRow, int searchGridCol, WordDirection direction) {
    if(direction.isDefault())
      return new Match(searchGridRow, searchGridCol);
    return new MatchWithDirection(searchGridRow, searchGridCol, direction);
  }
  
  /**
   * @param searchGridRow
   * @param searchGridCol
   */
  protected Match(int searchGridRow, int searchGridCol) {
    this.searchGridRow = searchGridRow;
    this.searchGridCol = searchGridCol;
  }
  
  /**
   * @return searchGridRow
   */
  public int getSearchGridRow() {
    return searchGridRow;
  }
  
  /**
   * @return searchGridCol
   */
  public int getSearchGridCol() {
    return searchGridCol;
  }
  
  /**
   * @return default WordDirection
   */
  public WordDirection getDirection() {
    return new WordDirection();
  }
}
