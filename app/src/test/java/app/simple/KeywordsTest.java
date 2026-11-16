package app.simple;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeywordsTest {
  @Test
  public void extractsTechnicalTerms() {
    assertTrue(Keywords.tokens("Java, SQL and C++").contains("c++"));
    assertFalse(Keywords.tokens("and the with").contains("and"));
  }

  @Test
  public void partitionsKeywords() {
    String result = Keywords.compare("Java SQL", "Java Python");
    assertTrue(result.contains("Matched (1)\njava"));
    assertTrue(result.contains("Missing (1)\npython"));
  }

  @Test
  public void emptyDescriptionHasNoMatches() {
    assertTrue(Keywords.compare("Java", "").contains("Matched (0)"));
  }
}
