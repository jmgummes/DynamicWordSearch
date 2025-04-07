public class MatchWithDirection extends Match {
  private MatchDirection direction;
  
  public MatchWithDirection(int gridSearchRow, int gridSearchCol, MatchDirection direction) {
    super(gridSearchRow, gridSearchCol);
    this.direction = direction;
  }
  
  public MatchDirection getMatchDirection() {
    return direction;
  }
}
