public class Main {
  public static void main(String[]  args) {
    // First 151 Pokémon names
    
    String[] pokemonWords = new String[]{
      "Bulbasaur", "Ivysaur", "Venusaur", "Charmander", "Charmeleon", "Charizard", 
      "Squirtle", "Wartortle", "Blastoise", "Caterpie", "Metapod", "Butterfree", 
      "Weedle", "Kakuna", "Beedrill", "Pidgey", "Pidgeotto", "Pidgeot", "Rattata", 
      "Raticate", "Spearow", "Fearow", "Ekans", "Arbok", "Pikachu", "Raichu", 
      "Sandshrew", "Sandslash", "Nidoran♀", "Nidorina", "Nidoqueen", "Nidoran♂", 
      "Nidorino", "Nidoking", "Clefairy", "Clefable", "Vulpix", "Ninetales", 
      "Jigglypuff", "Wigglytuff", "Zubat", "Golbat", "Oddish", "Gloom", "Vileplume", 
      "Paras", "Parasect", "Venonat", "Venomoth", "Diglett", "Dugtrio", "Meowth", 
      "Persian", "Psyduck", "Golduck", "Machop", "Machoke", "Machamp", "Bellsprout", 
      "Weepinbell", "Victreebel", "Tentacool", "Tentacruel", "Geodude", "Graveler", 
      "Golem", "Ponyta", "Rapidash", "Magnemite", "Magneton", "Krabby", "Kingler", 
      "Exeggcute", "Exeggutor", "Cubone", "Marowak", "Hitmonlee", "Hitmonchan", 
      "Lickitung", "Lickilicky", "Koffing", "Weezing", "Rhyhorn", "Rhydon", "Chansey", 
      "Tangela", "Kangaskhan", "Horsea", "Seadra", "Goldeen", "Seaking", "Staryu", 
      "Starmie", "Mr. Mime", "Scyther", "Electabuzz", "Magmar", "Pinsir", "Tauros", 
      "Magikarp", "Gyarados", "Lapras", "Ditto", "Eevee", "Vaporeon", "Jolteon", 
      "Flareon", "Porygon", "Omanyte", "Omastar", "Kabuto", "Kabutops", "Aerodactyl", 
      "Snorlax", "Articuno", "Zapdos", "Moltres", "Dratini", "Dragonair", "Dragonite"
    };
    
    //String[] words = new String[] {
    //    "ACCORDION", "BAGPIPES", "BALALAIKA", "BANJO", "BASSOON", "BONGO", "BUGLE", "CALLIOPE", "CASTANETS", "CELESTA", "CELLO", "CHIMES", "CLARINET", "CLAVICHORD", "CONGA", "CORNET", "CRUMHORN", "CUATRO", "CYMBAL", "DIDGERIDOO", "DJEMBE", "DULCIMER", "EUPHONIUM", "FIDDLE", "FLUTE", "FUGLHORN", "GLOCKENSPIEL", "GONG", "GUITAR", "HARMONICA", "HARP", "HARPSICHORD", "HELICON", "HORN", "KALIMBA", "KAZOO", "KOTO", "LUTE", "LYRE", "MANDOLIN", "MARACA", "MARIMBA", "MELODICA", "OBOE", "OCTOBASS", "ORGAN", "PANFLUTE", "PICCOLO", "RECORDER", "SAXOPHONE", "SHAMISEN", "SITAR", "SNARE", "SYNTHESIZER", "TABLA", "TAMBOURINE", "THEREMIN", "TIMBALES", "TIMPANI", "TROMBONE", "TRUMPET", "TUBA", "UKULELE", "VIOLA", "VIOLIN", "VUVUZELA", "WASHBOARD", "XYLOPHONE", "ZITHER"
    //};
    
    for(int i = 0; i < pokemonWords.length; i++)
      pokemonWords[i] = pokemonWords[i].toUpperCase();

    // Generate the word search with the first 151 Pokémon names, 30 words, and a 100x100 grid
    Generator g = new Generator(pokemonWords, 20, 20, 10, 0, 0);
    WordSearch wordSearch = g.generate();
    if(wordSearch == null) {
      System.out.println("failed to generate word search :(");
      System.exit(1);
    }
    System.out.println(wordSearch);
    System.out.println(wordSearch.toJson());
  }
}