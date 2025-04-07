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


public class WordSearch {
  private String[] wordBank;
  private int rows;
  private int cols;
  private char[] searchGrid;
  
  public WordSearch(String[] wordBank, int rows, int cols, char[] searchGrid) {
    this.wordBank = wordBank;
    this.rows = rows;
    this.cols = cols;
    this.searchGrid = searchGrid;
  }
  
  public String toString() {
    List<String> lines = new LinkedList<String>();
    for(int row = 0; row < rows; row++) {
      List<String> strings  = new LinkedList<String>();
      for(int col = 0; col < cols; col++) {
        strings.add(String.valueOf(searchGrid[row*cols + col]));
      }
      lines.add(String.join(",", strings));
    }
    return String.join("\n", lines);
  }
  
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
  
  public Solution solve() {
    return new Solution();
  }
  
  public class Solution {
    private List<Turn> turns;
    
    private Solution() {      
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
          
          MatchDirection[] directions;
          if(wordBank[i].length() <= 1)
            directions = new MatchDirection[] { MatchDirection.LEFT };
          else if(isPalindrome(wordBank[i]))
            directions = new MatchDirection[] {
              MatchDirection.LEFT,
              MatchDirection.UP,
              MatchDirection.UP_LEFT,
              MatchDirection.UP_RIGHT
            };
          else
            directions = MatchDirection.values();
          
          for(MatchDirection d : directions) {
            for(int row = searchStartOffset(d.getDy(), wordBank[i].length());
                row < searchEndOffset(d.getDy(), rows, wordBank[i].length());
                row++) {
              for(int col = searchStartOffset(d.getDx(), wordBank[i].length());
                  col < searchEndOffset(d.getDx(), cols, wordBank[i].length());
                  col++) {
                if(lookForMatch(i, row, col, d)) {
                  Match m;
                  if(wordBank[i].length() <= 1)
                    m = new Match(row, col);
                  else
                    m = new MatchWithDirection(row, col, d);
                  matches.add(m);
                }
              }
            }
          }
          if(matches.size() > 0) {
            wordIndicesIterator.remove();
            turn.addMatches(i, matches);
          }
        } while(wordIndicesIterator.hasNext());
        if(turn.isValid())
          transform(turn);
        else {
        }
        turns.add(turn);
        if(!isValid())
          break;
      }
    }

    private boolean isPalindrome(String string) {
      for(int i = 0; i < string.length() / 2; i++)
        if(string.charAt(i) != string.charAt(string.length() - i - 1))
          return false;
      return true;
    }

    public List<Turn> getTurns() {
      return turns;
    }
    
    public boolean isValid() {
      return turns.get(turns.size() - 1).isValid();
    }
    
    private void transform(Turn turn) {
      turn.modifiedSearchGrid = Arrays.copyOf(getLatestSearchGrid(), getLatestSearchGrid().length);
      int wordIndex = turn.map.entrySet().iterator().next().getKey();
      for(int i = 0; i < turn.modifiedSearchGrid.length; i++)
        if(turn.modifiedSearchGrid[i] == wordBank[wordIndex].charAt(0))
          turn.modifiedSearchGrid[i] = wordBank[wordIndex].charAt(wordBank[wordIndex].length() - 1); 
    }
    
    private char[] getLatestSearchGrid() {
      if(turns.isEmpty()) return searchGrid;
      return turns.get(turns.size() - 1).getModifiedSearchGrid();
    }
    
    private boolean lookForMatch(int i, int row, int col, MatchDirection direction) {    
      for(int j = 0; j < wordBank[i].length(); j++) {
        if(getSearchGridChar(row + j * direction.getDy(), col + j * direction.getDx()) != wordBank[i].charAt(j))
          return false;
      }
      return true;
    }
    
    private char getSearchGridChar(int row, int col) {
      char[] currentGrid = getLatestSearchGrid();
      char c = currentGrid[(cols * row) + col];
      return c;
    }
    
    private int offsetAdjustment(int wordLength) {
      return wordLength - 1;
    }
    
    private int searchStartOffset(int sign, int wordLength) {
      int adjustment = sign < 0 ? offsetAdjustment(wordLength) : 0; 
      return adjustment;
    }
    
    private int searchEndOffset(int sign, int length, int wordLength) {
      int adjustment = sign > 0 ? offsetAdjustment(wordLength) : 0;
      return length - adjustment;
    }
  }
  
  public class Turn {
    private Map<Integer, Set<Match>> map;
    private char[] modifiedSearchGrid;
    
    private Turn() {
      this.map = new HashMap<Integer, Set<Match>>();
    }

    public Map<Integer, Set<Match>> getMap() {
      return map;
    }
    
    public char[] getModifiedSearchGrid() {
      return modifiedSearchGrid;
    }
    
    private boolean isValid() {
      if(map.size() != 1)
        return false; 
      if(map.entrySet().iterator().next().getValue().size() > 1)
        return false;
      return true;
    }
    
    private void addMatches(int i, Set<Match> matches) {
      map.put(i, matches);
    }
  }
}
