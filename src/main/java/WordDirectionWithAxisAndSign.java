import java.util.LinkedList;
import java.util.List;

/**
 * This is class is like its parent except that sign is given. It's used for the direction
 * of non-palindromes of length more than one.
 */
public class WordDirectionWithAxisAndSign extends WordDirectionWithAxis {
  
  private int sign;
  
  /**
   * @param axis
   * @param sign
   */
  protected WordDirectionWithAxisAndSign(Axis axis, int sign) {
    super(axis);
    this.sign = sign;
  }
  
  @Override
  protected Integer getSign() {
    return sign;
  }  
  
  /**
   * @return all wordDirectionWithAxisAndSigns
   */
  public static List<WordDirection> getAll() {
    List<WordDirection> direction = new LinkedList<WordDirection>();
    for(Axis axis : WordDirection.Axis.values())
      for(int sign : new Integer[] { -1, 1 })
        direction.add(new WordDirectionWithAxisAndSign(axis, sign));
    return direction;
  }  
}