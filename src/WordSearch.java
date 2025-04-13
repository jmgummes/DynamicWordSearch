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
  
  public String[] getWordBank() {
    return wordBank;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(toStringCore(" "));
    sb.append("\n\nWord bank count: ");
    sb.append(wordBank.length);
    sb.append("\nWord bank:\n");
    sb.append(String.join("\n", wordBank));
    return sb.toString();
  }
  
  public String toCSV() {
    return toStringCore(",");
  }
  
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
      WordDirectionMapper mapper = new WordDirectionMapper();
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

    public List<Turn> getTurns() {
      return turns;
    }
    
    private void transform(Turn turn) {
      turn.modifiedSearchGrid = Arrays.copyOf(getLatestSearchGrid(1), getLatestSearchGrid(1).length);
      int wordIndex = turn.map.entrySet().iterator().next().getKey();
      for(int i = 0; i < turn.modifiedSearchGrid.length; i++)
        if(turn.modifiedSearchGrid[i] == wordBank[wordIndex].charAt(0))
          turn.modifiedSearchGrid[i] = wordBank[wordIndex].charAt(wordBank[wordIndex].length() - 1); 
    }
    
    private char[] getLatestSearchGrid(int i) {
      if(turns.size() - 1 - i < 0) return searchGrid;
      return turns.get(turns.size() - 1 - i).getModifiedSearchGrid();
    }
    
    private boolean lookForMatch(int i, int row, int col, WordDirection direction) {    
      for(int j = 0; j < wordBank[i].length(); j++) {
        if(getSearchGridChar(row + j * direction.getDy(), col + j * direction.getDx()) != wordBank[i].charAt(j))
          return false;
      }
      return true;
    }
    
    private char getSearchGridChar(int row, int col) {
      char[] currentGrid = getLatestSearchGrid(0);
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
    
    public int getMatchesCount() {
      int count = 0;
      for(Set<Match> s : map.values())
        count += s.size();
      return count;
    }
    
    public char[] getModifiedSearchGrid() {
      return modifiedSearchGrid;
    }
    
    private void addMatches(int i, Set<Match> matches) {
      map.put(i, matches);
    }
  }
}
