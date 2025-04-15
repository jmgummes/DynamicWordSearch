import java.util.List;

/**
 * This class represents a mapper that maps a word to all of its possible directions in the search
 * grid.
 */
public class WordDirectionMapper {
  
  /**
   * @param word
   * @return possible word directions
   */
  List<WordDirection> getPossibleWordDirections(String word) {
    if(word.length() < 2)
      return List.of(new WordDirection());
    
    if(isPalindrome(word))
      return WordDirectionWithAxis.getAll();
    
    return WordDirectionWithAxisAndSign.getAll();
  }
  
  /**
   * @param word
   * @return whether given word is a palindrome
   */
  private static boolean isPalindrome(String word) {
    for(int i = 0; i < word.length() / 2; i++)
      if(word.charAt(i) != word.charAt(word.length() - i - 1))
        return false;
    return true;
  }
}
