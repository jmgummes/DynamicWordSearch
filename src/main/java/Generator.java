import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Generator {
  private List<String> sourceWords;
  private int rows;
  private int cols;
  private Integer wordBankCount;
  private int branchCount;
  private int missingInSolutionCount;
  private int extraMatchesCount;
  
  public Generator(String[] sourceWords, int rows, int cols, Integer wordBankCount, int branchCount, int missingInSolutionCount, int extraMatchesCount) {
    this.sourceWords = new LinkedList<String>();
    for(String word : sourceWords)
      this.sourceWords.add(word);
    this.missingInSolutionCount = missingInSolutionCount;
    this.extraMatchesCount = extraMatchesCount;
    if(sourceWordsLeftForSolutionCount() < 0)
      throw new IllegalArgumentException("missingInSolutionCount > sourceWords.length");
    if(extraMatchesCount > 0 && extraMatchesCount >= (sourceWordsLeftForSolutionCount()))
      throw new IllegalArgumentException("extraMatchesCount >= (sourceWords.length - missingInSolutionCount)");
    this.rows = rows;
    this.cols = cols;
    this.wordBankCount = wordBankCount;
    this.branchCount = branchCount;
  }
  
  private int sourceWordsLeftForSolutionCount() {
    return sourceWords.size() - missingInSolutionCount; 
  }
  
  public WordSearch generate() {
    SolutionWords solutionWords = getSolutionWords();
    if(extraMatchesCount > 0 && solutionWords.size() <= extraMatchesCount) {
      System.out.println("not enough solution words to satisfy requested extraMatchesCount");
      return null;
    }
                
    int[] solutionWordsPositions = new int[solutionWords.size()];
    WordDirection[] solutionWordsDirections = new WordDirection[solutionWords.size()];
    for(int i = 0; i < solutionWords.size(); i++)
      placeWord(solutionWordsPositions, solutionWordsDirections, solutionWords, i);
    
    WordSearch wordSearch;
    do {
      char[] searchGrid = generateSearchGrid(solutionWords, solutionWordsPositions, solutionWordsDirections);
      wordSearch = new WordSearch(solutionWords.generateWordBank(), rows, cols, searchGrid);
    }
    while(!checkWordSearch(wordSearch, solutionWords));
    return wordSearch;
  }
  
  private Integer fetchCount() {
    if(wordBankCount == null)
      return null;

    return Math.min(wordBankCount - missingInSolutionCount, sourceWordsLeftForSolutionCount());
  }
  
  private boolean checkWordSearch(WordSearch wordSearch, SolutionWords solutionWords) {
    WordSearch.Solution solution = wordSearch.solve();
    
    int expectedTurnsCount = solutionWords.size() - extraMatchesCount;
    if(missingInSolutionCount > 0 && extraMatchesCount == 0)
      expectedTurnsCount++;
    if(solution.getTurns().size() != expectedTurnsCount)
      return false;
    
    if(expectedTurnsCount == 0) 
      return true;
    
    int matchesCount = solution.getTurns().get(solution.getTurns().size() - 1).getMatchesCount();
    if(missingInSolutionCount == 0 && extraMatchesCount == 0)
      return matchesCount == 1;
    if(extraMatchesCount > 0)
      return matchesCount == (extraMatchesCount + 1);
    return matchesCount == 0;
  }
  
  private char[] generateSearchGrid(SolutionWords solutionWords, int[] solutionWordsPositions, WordDirection[] solutionWordsDirections) {
    Map<Integer, Character> searchGridValuesFromWords = new HashMap<Integer, Character>();
    for(int i = 0; i < solutionWords.scrambledWords.size(); i++) {
      for(int j = 0; j < solutionWords.scrambledWords.get(i).length(); j++) {
        int row = (solutionWordsPositions[i] / cols) + solutionWordsDirections[i].getDy() * j;
        int col = (solutionWordsPositions[i] % cols) + solutionWordsDirections[i].getDx() * j;
        searchGridValuesFromWords.put(cols * row + col, solutionWords.scrambledWords.get(i).charAt(j));
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
  
  private void placeWord(int[] solutionWordPosition, WordDirection[] solutionDirection, SolutionWords solutionWords, int wordIndex) {
    WordDirectionMapper mapper = new WordDirectionMapper();
    WordDirection direction;
    int row;
    int col;
    do {
      List<WordDirection> directions = mapper.getPossibleWordDirections(solutionWords.scrambledWords.get(wordIndex));
      direction = directions.get((int)(Math.random() * directions.size()));
      row = getRandomCoordinate(solutionWords.scrambledWords.get(wordIndex), rows, direction.getDy());
      col = getRandomCoordinate(solutionWords.scrambledWords.get(wordIndex), cols, direction.getDx());
    } while(!checkPlacement(solutionWords, solutionWordPosition, solutionDirection,
        wordIndex, direction, row, col));
    solutionDirection[wordIndex] = direction;
    solutionWordPosition[wordIndex] = (row * cols) + col;
  }
  
  private boolean checkPlacement(SolutionWords solutionWords, int[] solutionWordPosition, WordDirection[] solutionDirection, int wordIndex,
      WordDirection direction, int row, int col) {
    for(int i = 0; i < wordIndex; i++) {
      int otherRow = solutionWordPosition[i] / cols;
      int otherCol = solutionWordPosition[i] % cols;
      Map<Integer, Integer> intersections = getIntersections(row, col, direction, solutionWords.scrambledWords.get(wordIndex).length(),
          otherRow, otherCol, solutionDirection[i], solutionWords.scrambledWords.get(i).length());
      for(Entry<Integer, Integer> intersection : intersections.entrySet())
        if(solutionWords.scrambledWords.get(wordIndex).charAt(intersection.getKey()) 
            != solutionWords.scrambledWords.get(i).charAt(intersection.getValue()))
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
  
  private SolutionWords getSolutionWords() {
    return getWordsRecursive(new SolutionWords(), fetchCount(), new LinkedList<String>(sourceWords));
  }
  
  private SolutionWords getWordsRecursive(SolutionWords solutionWords, Integer count, List<String> remainingSourceWords) {
    if((count != null && count == 0) || solutionWords.size() >= sourceWordsLeftForSolutionCount())
      return solutionWords;
    
    List<String> viableSourceWords = new LinkedList<String>();
    for(String word : remainingSourceWords)
      if(solutionWords.checkPreviousWordsAreNotVisibleAfterScrambling(word))
        viableSourceWords.add(word);

    Map<String, Integer> scores = new HashMap<String, Integer>();
    for(String word: viableSourceWords)
      scores.put(word, 0);
    for(int i = 0; i < viableSourceWords.size() - 1; i++) {
      for(int j = i + 1; j < viableSourceWords.size(); j++) {
        boolean aBlocksB = aBlocksB(viableSourceWords.get(i), viableSourceWords.get(j));
        boolean bBlocksA = aBlocksB(viableSourceWords.get(j), viableSourceWords.get(i));
        if(aBlocksB == bBlocksA)
          continue;
        
        int scoreA = scores.get(viableSourceWords.get(i));
        int scoreB = scores.get(viableSourceWords.get(j));
        if(aBlocksB) {
          scoreA++;
          scoreB--;
        }
        else {
          scoreA--;
          scoreB++;
        }
        scores.put(viableSourceWords.get(i), scoreA);
        scores.put(viableSourceWords.get(j), scoreB);
      }
    }
    
    viableSourceWords.sort(new Comparator<String>() {
      @Override
      public int compare(String a, String b) {
        return Integer.compare(scores.get(a), scores.get(b));
      }
    });
    
    if(solutionWords.size() == 0 && extraMatchesCount > 0) {
      List<String> startingWords = getStartingWordsForSolutionWithExtraMatches(viableSourceWords);
      solutionWords.addWords(startingWords, remainingSourceWords);
      if(count != null)
        count -= solutionWords.size();
      return getWordsRecursive(solutionWords, count, remainingSourceWords);
    }
    
    int branchCount;
    if(count != null && count == 1)
      branchCount = 1;
    else {
      branchCount = this.branchCount;
    }
    
    List<SolutionWords> wordLists = new LinkedList<SolutionWords>();
    wordLists.add(solutionWords);
    for(String word : viableSourceWords) {
      SolutionWords recursiveSolutionWords = new SolutionWords(solutionWords);
      List<String> rsw = new LinkedList<String>(remainingSourceWords);
      recursiveSolutionWords.addWordAndScramble(word, rsw);
      wordLists.add(getWordsRecursive(recursiveSolutionWords, count == null ? null : (count - 1), rsw));
        
      branchCount--;
      if(branchCount == 0)
        break;
    }
    
    return Collections.max(wordLists, new Comparator<SolutionWords>() {
      @Override
      public int compare(Generator.SolutionWords a, Generator.SolutionWords b) {
        return Integer.compare(a.size(), b.size());
      }
    });
  }
  
  private boolean aBlocksB(String a, String b) {
    return a.contains(b.substring(0, 1));
  }
  
  private Map<Integer, Integer> getIntersections(int row, int col, WordDirection direction, int length,
      int otherRow, int otherColumn, WordDirection otherDirection, int otherLength) {
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

  private List<String> getStartingWordsForSolutionWithExtraMatches(List<String> words) {
    Map<Character, List<String>> map = new HashMap<Character, List<String>>();
    for(int i = 0; i < words.size(); i++) {
      Set<Character> charSet = new HashSet<Character>();
      for(int j = 0; j < words.get(i).length(); j++) {
        charSet.add(words.get(i).charAt(j));
      }
      for(char c : charSet) {
        List<String> list = map.get(c);
        if(list == null) {
          list = new LinkedList<String>();
          map.put(c, list);
        }
        list.add(words.get(i));
        if(list.size() > extraMatchesCount)
          return list;
      }
    }
    return words.subList(0, extraMatchesCount + 1);
  }
  
  private class SolutionWords {
    private List<String> words;
    private List<String> scrambledWords;
    private Set<Character> closedChars;
    
    private SolutionWords() {
      this.words = new LinkedList<String>();
      this.scrambledWords = new LinkedList<String>();
      this.closedChars = new HashSet<Character>();
    }
    
    private SolutionWords(SolutionWords sW) {
      this.words = new LinkedList<String>(sW.words);
      this.scrambledWords = new LinkedList<String>(sW.scrambledWords);
      this.closedChars = new HashSet<Character>(sW.closedChars);
    }
    
    private int size() {
      return words.size();
    }
    
    private void addWord(String word, List<String> remainingSourceWords) {
      addWords(List.of(word), remainingSourceWords);
    }
    
    private void addWordAndScramble(String word, List<String> remainingSourceWords) {
      scrambleExistingWords(word);
      addWord(word, remainingSourceWords);
    }
    
    private void addWords(List<String> words, List<String> remainingSourceWords) {
      for(String word : words) {
        this.words.add(word);
        scrambledWords.add(word);
        for(int i = 0; i < word.length(); i++)
          closedChars.add(word.charAt(i));
      }
      Iterator<String> iterator = remainingSourceWords.iterator();
      while(iterator.hasNext()) {
        String word = iterator.next();
        if(closedChars.contains(word.charAt(0)))
          iterator.remove();
      }
    }
    
    private void scrambleExistingWords(String newWord) {
      for(int i = 0; i < words.size(); i++) {
        scrambledWords.set(i, getScrambledWord(newWord, scrambledWords.get(i)));
      }
    }
    
    private String getScrambledWord(String newWord, String wordToScramble) {
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < wordToScramble.length(); i++) {
        char c = wordToScramble.charAt(i);
        if(c == newWord.charAt(newWord.length() - 1))
            c = newWord.charAt(0);
        sb.append(c);
      }
      return sb.toString(); 
    }
    
    private boolean checkPreviousWordsAreNotVisibleAfterScrambling(String word) {
      for(int i = 0; i < size(); i++)
        if(!checkPreviousWordIsNotVisibleAfterScrambling(word, i))
          return false;
      return true;
    }
    
    private boolean checkPreviousWordIsNotVisibleAfterScrambling(String word, int prevWordIndex) {
      String scrambledWord = getScrambledWord(word, scrambledWords.get(prevWordIndex));
      for(int i = 0; i < words.size(); i++) {
        if(scrambledWord.contains(words.get(i)))
          return false;
      }
      return true;
    }
    
    private List<String> getUniqueWords() {
      Set<String> uniqueWords = new HashSet<String>();
      for(String word : words)
        uniqueWords.add(word);
      return new LinkedList<String>(uniqueWords);
    }
    
    private String[] generateWordBank() {
      List<String> uniqueWords = getUniqueWords();
      String[] wordBank = new String[uniqueWords.size()];
      for(int i = 0; i < uniqueWords.size(); i++)
        wordBank[missingInSolutionCount + i] = uniqueWords.get(i);
      setMissingWords(wordBank);
      Arrays.sort(wordBank);
      return wordBank;
    }
    
    private void setMissingWords(String[] wordBank) {
      List<String> words = new LinkedList<String>();
      for(String word : sourceWords)
        words.add(word);
      for(int i = 0; i < missingInSolutionCount; i++) {
        int j = (int) (Math.random() * words.size());
        wordBank[i] = words.get(j);
        words.remove(j);
      }
    }
  }
}
