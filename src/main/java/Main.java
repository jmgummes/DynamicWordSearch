import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    if (args.length < 7) {
      System.out.println("Usage: java Main <wordListName> <arg1> <arg2> <arg3> <arg4> <arg5> <arg6>");
      System.exit(1);
    }

    String[] words;
    try {
      Path filePath = Paths.get("words", args[0]);
      List<String> lines = Files.readAllLines(filePath);
      // Clean up blank lines and trim
      lines.removeIf(String::isBlank);
      lines.replaceAll(String::trim);
      words = lines.toArray(new String[0]);
    } catch (IOException e) {
      System.out.println("Failed to read words from file: " + e.getMessage());
      System.exit(1);
      return;
    }

    Generator g = new Generator(words, Integer.parseInt(args[1]), Integer.parseInt(args[2]),
        parseWordBankCount(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), 
        Integer.parseInt(args[6])
    );
    WordSearch wordSearch = g.generate();

    if (wordSearch == null) {
      System.out.println("failed to generate word search :(");
      System.exit(1);
    }

    System.out.println("CSV:");
    System.out.println(wordSearch.toCSV());
    System.out.println();

    System.out.println("JSON:");
    System.out.println(wordSearch.toJson());
    System.out.println();

    System.out.println(wordSearch);
  }
  
  private static Integer parseWordBankCount(String arg) {
    if(arg.equals("dynamic"))
      return null;
    return Integer.parseInt(arg);
  }
}