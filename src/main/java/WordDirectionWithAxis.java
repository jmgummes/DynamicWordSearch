import java.util.LinkedList;
import java.util.List;

/**
 * This is class is like its parent except that axis is given. It's used for the direction
 * of palindromes of length more than one.
 */
public class WordDirectionWithAxis extends WordDirection {
    private Axis axis;
    
    /**
     * @param axis
     */
    protected WordDirectionWithAxis(Axis axis) {
      this.axis = axis;
    }
    
    @Override
    protected Axis getAxis() {
      return axis;
    }
    
    @Override
    public boolean isDefault() {
      return false;
    }
    
    /**
     * @return all wordDirectionWithAxes
     */
    public static List<WordDirection> getAll() {
      List<WordDirection> directions = new LinkedList<WordDirection>();
      for(Axis axis : Axis.values())
        directions.add(new WordDirectionWithAxis(axis));
      return directions;
    }   
  }