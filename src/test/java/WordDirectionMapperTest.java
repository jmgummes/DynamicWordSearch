import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class WordDirectionMapperTest {

  @Test
  void testGetPossibleWordDirectionsWithWordOfLengthOne() {
    WordDirectionMapper mapper = new WordDirectionMapper();
    List<WordDirection> directions = mapper.getPossibleWordDirections("A");
    assertThat(directions).extracting("dx", "dy").containsExactly(
      tuple(-1, 0)
    );
  }
  
  @Test
  void testGetPossibleWordDirectionsWithPalindromeOfLengthMoreThanOne() {
    WordDirectionMapper mapper = new WordDirectionMapper();
    List<WordDirection> directions = mapper.getPossibleWordDirections("EEVEE");
    assertThat(directions).hasSize(4);
    assertThat(directions).extracting("dx", "dy").containsExactlyInAnyOrder(
      tuple(-1, -1),
      tuple(-1, 0),
      tuple(-1, 1),
      tuple(0, -1)
    );
  }
  
  @Test
  void testGetPossibleWordDirectionsWithNonPalindromeOfLengthMoreThanOne() {
    WordDirectionMapper mapper = new WordDirectionMapper();
    List<WordDirection> directions = mapper.getPossibleWordDirections("GENGAR");
    assertThat(directions).extracting("dx", "dy").containsExactlyInAnyOrder(
      tuple(-1, -1),
      tuple(-1, 0),
      tuple(-1, 1),
      tuple(0, -1),      
      tuple(0, 1),
      tuple(1, -1),
      tuple(1, 0),
      tuple(1, 1)
    );
  }
}
