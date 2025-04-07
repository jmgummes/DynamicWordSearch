import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Generator {
  private String[] sourceWords;
  private int rows;
  private int cols;
  private int wordBankCount;
  private Invalidator invalidator;
  
  public Generator(String[] sourceWords, int rows, int cols, int wordBankCount, Invalidator invalidator) {
    this.sourceWords = sourceWords;
    this.rows = rows;
    this.cols = cols;
    this.wordBankCount = wordBankCount;
    this.invalidator = invalidator;
  }
  
  public WordSearch generate() {
    String[] solutionWord = new String[wordBankCount];
    String[] solutionWordScrambled = new String[wordBankCount];
    int[] solutionWordPosition = new int[wordBankCount];
    MatchDirection[] solutionDirection = new MatchDirection[wordBankCount];
    
    for(int i = 0; i < nTurns(); i++) {
      int nWordsToSet;
      if(invalidator != null && (i == nTurns() - 1))
        nWordsToSet = invalidator.nMatches;
      else
        nWordsToSet = 1;
      for(int j = 0; j < nWordsToSet; j++) {
        if(!setWord(solutionWord, solutionWordScrambled, i + j))
          return null;
        scramblePreviousWords(solutionWordScrambled, i + j);
        placeWord(solutionWordPosition, solutionDirection, solutionWordScrambled, i + j);
      }
    }
    
    String[] wordBank = generateWordBank(solutionWord);
    
    WordSearch wordSearch;
    do {
      char[] searchGrid = generateSearchGrid(solutionWord, solutionWordScrambled,
          solutionWordPosition, solutionDirection);
      wordSearch = new WordSearch(wordBank, rows, cols, searchGrid);
    }
    while(!wordSearch.solve().isValid());
    return wordSearch;
  }
  
  private boolean checkWordSearch(WordSearch wordSearch) {
    WordSearch.Solution solution = wordSearch.solve();
    if(invalidator == null)
      return solution.isValid();
    if(solution.getTurns().size() != invalidator.nTurns)
      return false;
    return solution.getTurns().get(solution.getTurns().size() - 1).getMap().size() == invalidator.nMatches;
  }
  
  private int nTurns() {
    if(invalidator != null)
      return invalidator.nTurns;
    return wordBankCount;
  }
  
  private String[] generateWordBank(String[] solutionWord) {
    String[] wordBank = Arrays.copyOf(solutionWord, solutionWord.length);
    Arrays.sort(wordBank);
    return wordBank;
  }
  
  private char[] generateSearchGrid(String[] solutionWord, String[] solutionWordScrambled, int[] solutionWordPosition, MatchDirection[] solutionDirection) {
    Map<Integer, Character> searchGridValuesFromWords = new HashMap<Integer, Character>();
    for(int i = 0; i < solutionWordScrambled.length; i++) {
      for(int j = 0; j < solutionWordScrambled[i].length(); j++) {
        int row = (solutionWordPosition[i] / cols) + solutionDirection[i].getDy() * j;
        int col = (solutionWordPosition[i] % cols) + solutionDirection[i].getDx() * j;
        searchGridValuesFromWords.put(cols * row + col, solutionWordScrambled[i].charAt(j));
      }
    }
    char[] searchGrid = new char[rows * cols];
    for(int i = 0; i < (rows * cols); i++) {
      Character c = searchGridValuesFromWords.get(i);
      if(c == null)
        c = (char) ((int) (Math.random() * 26) + 65);
      searchGrid[i] = c;
    }
    return searchGrid;
  }
  
  private void placeWord(int[] solutionWordPosition, MatchDirection[] solutionDirection, String[] solutionWordScrambled, int wordIndex) {
    MatchDirection direction;
    int row;
    int col;
    do {
      direction = MatchDirection.values()[(int)(Math.random() * MatchDirection.values().length)];
      row = getRandomCoordinate(solutionWordScrambled[wordIndex], rows, direction.getDy());
      col = getRandomCoordinate(solutionWordScrambled[wordIndex], cols, direction.getDx());
    } while(!checkPlacement(solutionWordScrambled, solutionWordPosition, solutionDirection,
        wordIndex, direction, row, col));
    solutionDirection[wordIndex] = direction;
    solutionWordPosition[wordIndex] = (row * cols) + col;
  }
  
  private boolean checkPlacement(String[] solutionWordScrambled, int[] solutionWordPosition, MatchDirection[] solutionDirection, int wordIndex,
      MatchDirection direction, int row, int col) {
    for(int i = 0; i < wordIndex; i++) {
      int otherRow = solutionWordPosition[i] / cols;
      int otherCol = solutionWordPosition[i] % cols;
      Map<Integer, Integer> intersections = getIntersections(row, col, direction, solutionWordScrambled[wordIndex].length(),
          otherRow, otherCol, solutionDirection[i], solutionWordScrambled[i].length());
      for(Entry<Integer, Integer> intersection : intersections.entrySet())
        if(solutionWordScrambled[wordIndex].charAt(intersection.getKey()) 
            != solutionWordScrambled[i].charAt(intersection.getValue()))
          return false;
    }
    return true;
  }

  private int getRandomCoordinate(String word, int gridLength, int axisSign) {
    int offset = 0;
    int placementLength = gridLength;
    if(axisSign != 0) {
       placementLength -= word.length();
       if(axisSign < 0)
         offset += word.length();
    }
    return (int) (Math.random() * placementLength) + offset;    
  }
  
  private boolean setWord(String[] solutionWord, String[] solutionWordScrambled, int wordIndex) {
    String word = getWord(solutionWord, solutionWordScrambled, wordIndex);
    if(word == null)
      return false;
    solutionWord[wordIndex] = word;
    solutionWordScrambled[wordIndex] = word;
    return true;
  }
  
  private String scrambleWord(String currentWord, String wordToScramble) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < wordToScramble.length(); i++) {
      char c = wordToScramble.charAt(i);
      if(c == currentWord.charAt(currentWord.length() - 1))
          c = currentWord.charAt(0);
      sb.append(c);
    }
    return sb.toString(); 
  }
  
  private void scramblePreviousWords(String[] solutionWordScrambled, int wordIndex) {
    for(int i = 0; i < wordIndex; i++)
      solutionWordScrambled[i] = scrambleWord(solutionWordScrambled[wordIndex], solutionWordScrambled[i]);
  }
  
  private String getWord(String[] solutionWord, String[] solutionWordScrambled, int wordIndex) {
    Set<String> closedWords = new HashSet<String>();
    for(int i = 0; i < wordIndex; i++)
      closedWords.add(solutionWord[i]);
    List<String> words = new LinkedList<String>();
    for(String word : sourceWords)
      if(!closedWords.contains(word))
        words.add(word);
    Map<String, Integer> scores = new HashMap<String, Integer>();
    for(String word: words) {
      scores.put(word, 0);
    }
    for(int i = 0; i < words.size() - 1; i++) {
      for(int j = i + 1; j < words.size(); j++) {
        boolean aBlocksB = aBlocksB(words.get(i), words.get(j));
        boolean bBlocksA = aBlocksB(words.get(j), words.get(i));
        if(aBlocksB == bBlocksA)
          continue;
        
        int scoreA = scores.get(words.get(i));
        int scoreB = scores.get(words.get(j));
        if(aBlocksB) {
          scoreA++;
          scoreB--;
        }
        else {
          scoreA--;
          scoreB++;
        }
        scores.put(words.get(i), scoreA);
        scores.put(words.get(j), scoreB);
      }
    }
    
    words.sort(new Comparator<String>() {
      @Override
      public int compare(String a, String b) {
        return Integer.compare(scores.get(a), scores.get(b));
      }
    });
    
    for(String word : words) {
      if(checkWord(word, solutionWord, solutionWordScrambled, wordIndex))
        return word;
    }
    return null;
  }
  
  private boolean aBlocksB(String a, String b) {
    return a.contains(b.substring(0, 1));
  }
  
  private boolean checkWord(String word, String[] solutionWord, String[] solutionWordScrambled, int wordIndex) {
    for(int i = 0; i < wordIndex; i++) {
      if(!checkWordDoesNotChangePreviousWordBeforeScrambling(word, solutionWordScrambled[i]))
        return false;
      if(!checkPreviousWordIsNotVisibleAfterScrambling(word, solutionWord, solutionWordScrambled, wordIndex, i))
        return false;
    }
    return true;
  }
  
  private boolean checkWordDoesNotChangePreviousWordBeforeScrambling(String word, String prevWord) {
    for(int i = 0; i < prevWord.length(); i++)
      if(prevWord.charAt(i) == word.charAt(0))
        return false;
    return true;
  }
  
  private boolean checkPreviousWordIsNotVisibleAfterScrambling(String word, String[] solutionWords, String[] solutionWordScrambled, int currentWordIndex, int prevWordIndex) {
    String scrambledWord = scrambleWord(word, solutionWordScrambled[prevWordIndex]);
    for(int i = 0; i < currentWordIndex; i++) {
      String previousWord = solutionWords[i];
      if(scrambledWord.contains(previousWord)) 
        return false;
    }
    return true;
  }
  
  private Map<Integer, Integer> getIntersections(int row, int col, MatchDirection direction, int length,
      int otherRow, int otherColumn, MatchDirection otherDirection, int otherLength) {
    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    for(int i = 0; i < length; i++) {
      for(int j = 0; j < otherLength; j++) {
        int r = row + (i * direction.getDy());
        int c = col + (i * direction.getDx());
        int otherR = otherRow + (j * otherDirection.getDy());
        int otherC = otherColumn + (j * otherDirection.getDx());
        if(r == otherR && c == otherC)
          map.put(i, j);
      }
    }
    return map;
  }
  
  public static class Invalidator {
    private int nTurns;
    private int nMatches;
      
    public Invalidator(int nTurns, int nMatches) {
      this.nTurns = nTurns;
      this.nMatches = nMatches;
    }    
  }
}
