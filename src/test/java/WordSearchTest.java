import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordSearchTest {

    @Test
    void testSolutionWithMockedWordDirectionMapperAndSearchGrid() {
        WordDirectionMapper mockMapper = Mockito.mock(WordDirectionMapper.class);
        
        WordDirection mockUp = mockDir(0, -1);
        WordDirection mockDown = mockDir(0, 1);
        WordDirection mockLeft = mockDir(-1, 0);
        WordDirection mockRight = mockDir(1, 0);
        WordDirection mockUpLeft = mockDir(-1, -1);
        WordDirection mockUpRight = mockDir(1, -1);
        WordDirection mockDownLeft = mockDir(-1, 1);
        WordDirection mockDownRight = mockDir(1, 1);

        List<WordDirection> mockDirections = List.of(mockUp, mockDown, mockLeft, mockRight,
          mockUpLeft, mockUpRight, mockDownLeft, mockDownRight    
        );
        
        for (String w : List.of("DATE", "GRAPE", "APPLE", "LIME")) {
          when(mockMapper.getPossibleWordDirections(w)).thenReturn(mockDirections);
        }
        
        String[] wordBank = {"APPLE", "CHERRY", "DATE", "GRAPE", "LIME"};
        int rows = 7;
        int cols = 7;
        char[] searchGrid = new char[] {
          'O','O','O','O','O','O','G',
          'O','G','L','P','P','A','R',
          'D','O','O','O','G','O','A',
          'A','O','O','M','O','O','P',
          'T','O','I','O','O','O','D',
          'E','L','O','O','O','O','O',
          'O','O','O','O','O','O','O'
        };
        WordSearch wordSearch = new WordSearch(wordBank, rows, cols, searchGrid);
        
        WordSearch.Solution solution = wordSearch.solve(mockMapper);
        List<WordSearch.Turn> turns = solution.getTurns();
        assertThat(turns).hasSize(3);
        
        // First turn
        WordSearch.Turn firstTurn = turns.get(0);
        assertThat(firstTurn.getModifiedSearchGrid()).containsExactly(
          'O','O','O','O','O','O','G',
          'O','G','L','P','P','A','R',
          'E','O','O','O','G','O','A',
          'A','O','O','M','O','O','P',
          'T','O','I','O','O','O','E',
          'E','L','O','O','O','O','O',
          'O','O','O','O','O','O','O'
        ); 
        Map<Integer, Set<Match>> firstTurnMap = firstTurn.getMap();
        Set<Integer> firstTurnKeySet = firstTurnMap.keySet();
        assertThat(firstTurnKeySet).containsExactly(2);
        Set<Match> firstTurnMatches = firstTurnMap.get(2);
        assertThat(firstTurnMatches).extracting("searchGridRow", "searchGridCol", "direction")
          .containsExactly(tuple(2, 0, mockDown));
        
        // Second turn
        WordSearch.Turn secondTurn = turns.get(1);
        assertThat(secondTurn.getModifiedSearchGrid()).containsExactly(
          'O','O','O','O','O','O','E',
          'O','E','L','P','P','A','R',
          'E','O','O','O','E','O','A',
          'A','O','O','M','O','O','P',
          'T','O','I','O','O','O','E',
          'E','L','O','O','O','O','O',
          'O','O','O','O','O','O','O'
        ); 
        Map<Integer, Set<Match>> secondTurnMap = secondTurn.getMap();
        Set<Integer> secondTurnKeySet = secondTurnMap.keySet();
        assertThat(secondTurnKeySet).containsExactly(3);
        Set<Match> secondTurnMatches = secondTurnMap.get(3);
        assertThat(secondTurnMatches).extracting("searchGridRow", "searchGridCol", "direction")
          .containsExactly(tuple(0, 6, mockDown));
        
        // Third and last turn
        WordSearch.Turn lastTurn = turns.get(2);
        assertThat(lastTurn.getModifiedSearchGrid()).isNull();
        Map<Integer, Set<Match>> lastTurnMap = lastTurn.getMap();
        Set<Integer> lastTurnKeySet = lastTurnMap.keySet();
        assertThat(lastTurnKeySet).containsExactly(0, 4);
        Set<Match> lastTurnAppleMatches = lastTurnMap.get(0);
        assertThat(lastTurnAppleMatches).extracting("searchGridRow", "searchGridCol", "direction")
          .containsExactly(tuple(1, 5, mockLeft));
        Set<Match> lastTurnLimeMatches = lastTurnMap.get(4);
        assertThat(lastTurnLimeMatches).extracting("searchGridRow", "searchGridCol", "direction")
          .containsExactly(tuple(5, 1, mockUpRight));
    }
    
    private WordDirection mockDir(int dx, int dy) {
      WordDirection dir = Mockito.mock(WordDirection.class);
      when(dir.getDx()).thenReturn(dx);
      when(dir.getDy()).thenReturn(dy);
      return dir;
  }
}