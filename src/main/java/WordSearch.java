import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class represents the beginning of a word search game. It consists of a word bank
 * and a search grid. The search grid is represented by a one-dimensional array. The letter
 * at the ith row from the top and jth column from the left is stored at index i * cols + j.
 * See the the comment above the inner Solution class for an explanation of how the word search
 * is solved. 
 */
public class WordSearch {
  private String[] wordBank;
  private int rows;
  private int cols;
  private char[] searchGrid;
  
  /**
   * @param wordBank
   * @param rows
   * @param cols
   * @param searchGrid
   */
  public WordSearch(String[] wordBank, int rows, int cols, char[] searchGrid) {
    this.wordBank = wordBank;
    this.rows = rows;
    this.cols = cols;
    this.searchGrid = searchGrid;
  }
  
  /**
   * @param json
   */
  public WordSearch(String json) {
    JSONObject jsonObject = new JSONObject(json);
    
    JSONArray wordBankArray = jsonObject.getJSONArray("word_bank");
    wordBank = new String[wordBankArray.length()];
    for (int i = 0; i < wordBankArray.length(); i++)
      wordBank[i] = wordBankArray.getString(i);
    
    rows = jsonObject.getInt("rows");
    cols = jsonObject.getInt("cols");
    
    searchGrid = new char[rows * cols];
    String searchGridStr = jsonObject.getString("search_grid");
    for(int row = 0; row < rows; row++)
      for(int col = 0; col < cols; col++)
        searchGrid[(row * cols) + col] = searchGridStr.charAt((row * cols) + col);
  }
  
  /**
   * @return word bank
   */
  public String[] getWordBank() {
    return wordBank;
  }
  
  /**
   * @return this as a string
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(toStringCore(" "));
    sb.append("\n\nWord bank count: ");
    sb.append(wordBank.length);
    sb.append("\nWord bank:\n");
    sb.append(String.join("\n", wordBank));
    return sb.toString();
  }
  
  /**
   * @return search grid as a csv
   */
  public String toCSV() {
    return toStringCore(",");
  }
  
  /**
   * @param separator
   * @return search grid as a string
   */
  private String toStringCore(String separator) {
    List<String> lines = new LinkedList<String>();
    for(int row = 0; row < rows; row++) {
      List<String> strings  = new LinkedList<String>();
      for(int col = 0; col < cols; col++) {
        strings.add(String.valueOf(searchGrid[row*cols + col]));
      }
      lines.add(String.join(separator, strings));
    }
    return String.join("\n", lines);    
  }
  
  /**
   * @return json
   */
  public JSONObject toJson() {
    JSONObject json = new JSONObject();

    json.put("rows", rows);
    json.put("cols", cols);
    json.put("search_grid", new String(searchGrid)); // flatten the char[] into a String

    JSONArray wordArray = new JSONArray();
    for (String word : wordBank) {
      wordArray.put(word);
    }
    json.put("word_bank", wordArray);

    return json;
  }
  
  /**
   * @return solution
   */
  public Solution solve(WordDirectionMapper mapper) {
    return new Solution(mapper);
  }
  
  /**
   * This class represents a solution to a word search. The word search is solved in a series
   * of turns. In each turn, the player finds all of the words from the word bank that have not
   * been found and are currently visible. Words can be found in any of the eight directions seen
   * in standard word search games. Depending on how many words are found, the game proceeds in one
   * of two ways. If the number of words found, including duplicates in a turn is zero, or more 
   * than one, the game ends. However, if the the number of words found is one, all of the instances
   * of its first letter in the search grid are replaced with its last letter. The game also ends 
   * if every word in the word bank is found in the search grid. See the comment above the other
   * inner class Turn for an explanation of how each Turn is represented. 
   * */
  public class Solution {
    private List<Turn> turns;
    
    private Solution(WordDirectionMapper mapper) {      
      turns = new LinkedList<>();
      List<Integer> remainingWordIndices = new LinkedList<Integer>();
      for(int i = 0; i < wordBank.length; i++)
        remainingWordIndices.add(i);
      while(!remainingWordIndices.isEmpty()) {
        Turn turn = new Turn();
        Iterator<Integer> wordIndicesIterator = remainingWordIndices.iterator();
        do {
          int i = wordIndicesIterator.next();
          
          Set<Match> matches = new HashSet<Match>();
          
          List<WordDirection> directions = mapper.getPossibleWordDirections(wordBank[i]);
          
          for(WordDirection d : directions) {
            for(int row = searchStartOffset(d.getDy(), wordBank[i].length());
                row < searchEndOffset(d.getDy(), rows, wordBank[i].length());
                row++) {
              for(int col = searchStartOffset(d.getDx(), wordBank[i].length());
                  col < searchEndOffset(d.getDx(), cols, wordBank[i].length());
                  col++) {
                if(lookForMatch(i, row, col, d)) {
                  matches.add(Match.create(row, col, d));
                }
              }
            }
          }
          if(matches.size() > 0) {
            wordIndicesIterator.remove();
            turn.addMatches(i, matches);
          }
        } while(wordIndicesIterator.hasNext());
        turns.add(turn);
        if(turn.getMatchesCount() != 1)
          break;
        transform(turn);
      }
    }

    /**
     * @return turns
     */
    public List<Turn> getTurns() {
      return turns;
    }
    
    /**
     * @param turn
     */
    private void transform(Turn turn) {
      turn.modifiedSearchGrid = Arrays.copyOf(getLatestSearchGrid(1), getLatestSearchGrid(1).length);
      int wordIndex = turn.map.entrySet().iterator().next().getKey();
      for(int i = 0; i < turn.modifiedSearchGrid.length; i++)
        if(turn.modifiedSearchGrid[i] == wordBank[wordIndex].charAt(0))
          turn.modifiedSearchGrid[i] = wordBank[wordIndex].charAt(wordBank[wordIndex].length() - 1); 
    }
    
    /**
     * @param i
     * @return latest search grid
     */
    private char[] getLatestSearchGrid(int i) {
      if(turns.size() - 1 - i < 0) return searchGrid;
      return turns.get(turns.size() - 1 - i).getModifiedSearchGrid();
    }
    
    /**
     * @param i
     * @param row
     * @param col
     * @param direction
     * @return whether there is a match with word i in the search grid with the given row, col and direction
     */
    private boolean lookForMatch(int i, int row, int col, WordDirection direction) {    
      for(int j = 0; j < wordBank[i].length(); j++) {
        if(getSearchGridChar(row + j * direction.getDy(), col + j * direction.getDx()) != wordBank[i].charAt(j))
          return false;
      }
      return true;
    }
    
    /**
     * @param row
     * @param col
     * @return search grid char
     */
    private char getSearchGridChar(int row, int col) {
      char[] currentGrid = getLatestSearchGrid(0);
      char c = currentGrid[(cols * row) + col];
      return c;
    }
    
    /**
     * @param wordLength
     * @return offset adjustment
     */
    private int offsetAdjustment(int wordLength) {
      return wordLength - 1;
    }
    
    /**
     * @param sign
     * @param wordLength
     * @return search start offset
     */
    private int searchStartOffset(int sign, int wordLength) {
      int adjustment = sign < 0 ? offsetAdjustment(wordLength) : 0; 
      return adjustment;
    }
    
    /**
     * @param sign
     * @param length
     * @param wordLength
     * @return search end offset
     */
    private int searchEndOffset(int sign, int length, int wordLength) {
      int adjustment = sign > 0 ? offsetAdjustment(wordLength) : 0;
      return length - adjustment;
    }
  }
  
  /**
   * This class represents a turn in the word search game. Each turn consists of matches of words
   * in the word bank not previously found with characters in the search grid, and the transformation
   * applied to the search grid if the number of matches is exactly one. The matches are represented
   * as a Map<Integer, <Set<Match>>> where the keys correspond to the indices of words in the word
   * bank. See the comment above the Match class for a more precise definition of "match".
   */
  public class Turn {
    private Map<Integer, Set<Match>> map;
    private char[] modifiedSearchGrid;
    
    private Turn() {
      this.map = new HashMap<Integer, Set<Match>>();
    }

    /**
     * @return map
     */
    public Map<Integer, Set<Match>> getMap() {
      return map;
    }
    
    /**
     * @return matches count
     */
    public int getMatchesCount() {
      int count = 0;
      for(Set<Match> s : map.values())
        count += s.size();
      return count;
    }
    
    /**
     * @return modified search grid
     */
    public char[] getModifiedSearchGrid() {
      return modifiedSearchGrid;
    }
    
    /**
     * @param i
     * @param matches
     */
    private void addMatches(int i, Set<Match> matches) {
      map.put(i, matches);
    }
  }
}
