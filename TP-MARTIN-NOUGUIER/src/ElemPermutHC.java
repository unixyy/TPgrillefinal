import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ElemPermutHC implements IElemHC {

  private Instance i;
  private ArrayList<Integer> permut; // permutation de {0,..,i.getListePieces().size()-1} représentant l'ordre dans
                                     // lequel on souhaite ramasser les pièces
  private static int dist = 1; // distance à laquelle on génère voisinage

  public ElemPermutHC(Instance i, ArrayList<Integer> p) {
    this.i = i;
    permut = p;
  }

  public ElemPermutHC(ElemPermutHC s) {
    this.i = new Instance(s.i);
    this.permut = new ArrayList<Integer>();
    permut.addAll(s.permut);
  }

  public static void setDist(int d) {
    dist = d;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ElemPermutHC))
      return false;
    ElemPermutHC that = (ElemPermutHC) o;
    return i.equals(that.i) && permut.equals(that.permut);
  }

  @Override
  public int hashCode() {
    return Objects.hash(i, permut);
  }

  public int getVal() {

    // retourne nbCases * valSol - nbStepsTotal, où :
    // - nbCases est le nombre de cases du plateau
    // - valSol est la valeur de la solution associée à this
    // - nbStepsTotal est le nombre de pas total qu'il faudrait pour ramasser toutes
    // les pièces dans l'ordre de permut

    // à compléter
    var nbCases = i.getNbL() * i.getNbC();
    var valSol = i.evaluerSolution(i.calculerSol(permut));
    var nbStepsTotal = i.nbStepsToCollectAll(permut);
    return nbCases * valSol - nbStepsTotal;
  }

  public Solution getSol() {
    return i.calculerSol(permut);
  }

  public ArrayList<ElemPermutHC> getVoisinsImmediats() {

    // retourne l'ensemble des voisins à dist <= 1 (et donc this fait partie du
    // résultat car à distance 0)
    // voisins = toutes les permutations que l'on peut atteindre en repoussant un
    // élément de permut à la fin
    // ex pour permut = (0,1,2), doit retourner {(1,2,0),(0,2,1),(0,1,2)}
    // les objets retournés doivent être indépendant de this, et cette méthode ne
    // doit pas modifier this

    // ne dois pas modifier this

    // à compléter

    var voisins = Stream
        .iterate(this.permut, p -> {
          var temp = new ArrayList<Integer>(p.subList(0, 1));
          var newList = new ArrayList<>(p.subList(1, p.size()));
          newList.addAll(temp);
          return newList;
        })
        .limit(this.permut.size())
        .map(x -> new ElemPermutHC(this.i, x))
        .collect(Collectors.toList());

    return new ArrayList<>(voisins);
  }

  public ArrayList<ElemPermutHC> getVoisins() {

    // retourne voisins (sans doublons) à une distance <= dist
    // pour dist = 1, doit retourner getVoisinsImmediats();

    // à compléter

    if (dist <= 1)
      return getVoisinsImmediats();
    return null;
  }

  @Override
  public String toString() {
    return this.permut.stream().map(Object::toString).reduce("", (a, b) -> a + " " + b);
  }

}