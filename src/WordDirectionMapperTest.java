import java.util.List;
import java.util.Map;

import org.assertj.core.api.recursive.assertion.*;

public class WordDirectionMapperTest {
  @ParameterizedTest
  @MethodSource("provideTestData")
  void testGetPossibleWordDirections(String word, List<Map<String, Integer>> expectedDirections) {
    WordDirectionMapper mapper = new WordDirectionMapper();
    List<WordDirection> directions = mapper.getPossibleWordDirections(word);
    assertThat(directions);
  }
}
