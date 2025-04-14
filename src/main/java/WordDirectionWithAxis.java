import java.util.LinkedList;
import java.util.List;

public class WordDirectionWithAxis extends WordDirection {
    private Axis axis;
    
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
    
    public static List<WordDirection> getAll() {
      List<WordDirection> directions = new LinkedList<WordDirection>();
      for(Axis axis : Axis.values())
        directions.add(new WordDirectionWithAxis(axis));
      return directions;
    }   
  }