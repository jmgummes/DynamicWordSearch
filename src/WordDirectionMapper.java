import java.util.List;

public class WordDirectionMapper {
  List<WordDirection> getPossibleWordDirections(String word) {
    if(word.length() < 2)
      return List.of(new WordDirection());
    
    if(isPalindrome(word))
      return WordDirectionWithAxis.getAll();
    
    return WordDirectionWithAxisAndSign.getAll();
  }
  
  private static boolean isPalindrome(String word) {
    for(int i = 0; i < word.length() / 2; i++)
      if(word.charAt(i) != word.charAt(word.length() - i - 1))
        return false;
    return true;
  }
}
