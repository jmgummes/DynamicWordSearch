/**
 * This class is like its parent except that direction is given. It's used for words
 * of length more than one.
 */
public class MatchWithDirection extends Match {
  private WordDirection direction;
  
  /**
   * @param searchGridRow
   * @param searchGridCol
   * @param direction
   */
  protected MatchWithDirection(int searchGridRow, int searchGridCol, WordDirection direction) {
    super(searchGridRow, searchGridCol);
    this.direction = direction;
  }
  
  @Override
  public WordDirection getDirection() {
    return direction;
  }
}
