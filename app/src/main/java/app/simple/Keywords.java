package app.simple;

import java.util.*;

final class Keywords {
  static final Set<String> STOP =
      Set.of(
          "the",
          "and",
          "with",
          "that",
          "this",
          "for",
          "you",
          "your",
          "our",
          "are",
          "will",
          "from",
          "have",
          "work",
          "job",
          "role",
          "team",
          "experience",
          "about",
          "their",
          "into",
          "not",
          "all",
          "can",
          "who",
          "has",
          "was",
          "but",
          "more");

  static Set<String> tokens(String text) {
    Set<String> s = new TreeSet<>();
    for (String t : text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}+#.]+")) {
      t = t.replaceAll("^\\.+|\\.+$", "");
      if (t.length() > 1 && !STOP.contains(t)) s.add(t);
    }
    return s;
  }

  static String compare(String resume, String job) {
    Set<String> missing = tokens(job), matched = new TreeSet<>(missing);
    matched.retainAll(tokens(resume));
    missing.removeAll(matched);
    return "Matched ("
        + matched.size()
        + ")\n"
        + String.join(", ", matched)
        + "\n\nMissing ("
        + missing.size()
        + ")\n"
        + String.join(", ", missing)
        + "\n\nLiteral keyword overlap, not a hiring score.";
  }
}
