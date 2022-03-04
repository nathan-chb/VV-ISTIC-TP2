# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

## Answer

TCC et LCC produise la même valeur lorsque toute les méthodes sont connectées, c'est à dire que toutes les méthodes de la classe utilise les mémes variables.

  class Name {
      String name;
      public String getName(String name)
      {
          this.name = name;
          return name;
      }
  }

LCC est forcément supérieur ou égale à TCC, parce que dans le meilleur des cas LCC n'a pas de noeuds indirect mais possède le même nombre de noeuds direct que TCC.
