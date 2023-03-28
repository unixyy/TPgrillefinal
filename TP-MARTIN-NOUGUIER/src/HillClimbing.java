import java.util.ArrayList;
import java.util.stream.Collectors;

class HillClimbing {

  private HillClimbing() {
  }

  public static Solution hillClimbingWithRestart(IFactory f, int nbRestart) {

    // prérequis : nbRestart >= 1
    // effectue nbRestart fois l'algorithme de hillClimbing, en partant à chaque
    // fois d'un élément donné par f

    // à compléter
    if (nbRestart < 1)
      throw new IllegalArgumentException("nbRestart doit être >= 1");

    IElemHC max = f.getRandomSol();
    for (int i = 0; i < nbRestart; i++) {
      IElemHC sol = hillClimbing(f.getRandomSol());
      if (sol.getVal() >= max.getVal()) {
        max = sol;
      }
    }

    return max.getSol();
  }

  public static IElemHC hillClimbing(IElemHC s) {

    // effectue une recherche locale en partant de s :
    // - en prenant à chaque étape le meilleur des voisins de la solution courante
    // (ou un des meilleurs si il y a plusieurs ex aequo)
    // - en s'arrêtant dès que la solution courante n'a pas de voisin strictement
    // meilleur qu'elle
    // (meilleur au sens de getVal strictement plus grand)

    // à compléter

    var neighbors = s.getVoisins();

    if (neighbors.isEmpty())
      return s;

    var bestNeighbor = neighbors
        .stream()
        .sorted((a, b) -> b.getVal() - a.getVal())
        .collect(Collectors.toList())
        .get(0);

    if (bestNeighbor.getVal() > s.getVal())
      return hillClimbing(bestNeighbor);
    else
      return bestNeighbor;
  }
}