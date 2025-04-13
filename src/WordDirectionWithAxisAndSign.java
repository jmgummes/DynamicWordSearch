import java.util.LinkedList;
import java.util.List;

public class WordDirectionWithAxisAndSign extends WordDirectionWithAxis {
  
  private int sign;
  
  protected WordDirectionWithAxisAndSign(Axis axis, int sign) {
    super(axis);
    this.sign = sign;
  }
  
  @Override
  protected Integer getSign() {
    return sign;
  }  
  
  public static List<WordDirection> getAll() {
    List<WordDirection> direction = new LinkedList<WordDirection>();
    for(Axis axis : WordDirection.Axis.values())
      for(int sign : new Integer[] { -1, 1 })
        direction.add(new WordDirectionWithAxisAndSign(axis, sign));
    return direction;
  }  
}