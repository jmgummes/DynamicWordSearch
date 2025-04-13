
import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class WordDirectionMapperTest {
  @ParameterizedTest
  @MethodSource("provideTestData")
  public void testGetPossibleWordDirections(String word, List<Map<String, Integer>> expectedDirections) {
    WordDirectionMapper mapper = new WordDirectionMapper();
    List<WordDirection> directions = mapper.getPossibleWordDirections(word);
  }
}
