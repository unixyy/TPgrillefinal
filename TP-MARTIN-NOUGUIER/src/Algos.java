
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Algos {

  public static boolean egalEnsembliste(ArrayList<?> a1, ArrayList<?> a2) {
    // retourn vrai ssi les a1 à les même éléments que a2 (peut importe l'ordre)
    return a1.containsAll(a2) && a2.containsAll(a1);
  }

  public static Solution greedySolver(Instance i) {

    // calcule la solution obtenue en allant chercher à chaque étape la pièce
    // restante la plus proche
    // (si plusieurs pièces sont à la même distance, on fait un choix arbitraire)

    return i.calculerSol(i.greedyPermut());
  }

  private static class Pair<T, U> {
    public final T first;
    public final U second;

    public Pair(T first, U second) {
      this.first = first;
      this.second = second;
    }
  }

  private static Pair<Stream<Coord>, Integer> greedySolver(
      Coord coord,
      int pieceCount,
      List<Coord> dejaVisite,
      InstanceDec id) {
    var newDejaVisite = new ArrayList<Coord>(dejaVisite);
    newDejaVisite.add(coord);

    if (pieceCount >= id.c || newDejaVisite.size() == id.i.getK() + 1)
      return new Pair<>(newDejaVisite.stream(), pieceCount);

    return coord
        .neighbors(id.i.getNbL(), id.i.getNbC())
        .map((Coord c) -> bestPathForCord(c, pieceCount, id, newDejaVisite))
        .filter(x -> !Objects.isNull(x))
        .reduce((a, b) -> a.second > b.second ? a : b)
        .orElse(new Pair<>(new ArrayList<Coord>().stream(), 0));
  }

  private static Pair<Stream<Coord>, Integer> bestPathForCord(Coord coord, int pieceCount, InstanceDec id,
      ArrayList<Coord> newDejaVisite) {
    ToIntFunction<Coord> hasPiece = (Coord c) -> id.i.piecePresente(c) ? 1 : 0;
    return newDejaVisite.contains(coord)
        ? null
        : greedySolver(coord, pieceCount + hasPiece.applyAsInt(coord), newDejaVisite, id);
  }

  public static Solution algoFPT1(InstanceDec id) {
    // algorithme qui décide id (c'est à dire si opt(id.i) >= id.c) en branchant (en
    // 4^k) dans les 4 directions pour chacun des k pas
    // retourne une solution de valeur >= c si une telle solution existe, et null
    // sinon
    // Ne doit pas modifier le paramètre
    // Rappel : si c==0, on peut retourner la solution égale au point de départ
    // puisque l'on est pas obligé d'utiliser les k pas
    // (on peut aussi retourner une solution plus longue si on veut)
    // Remarque : quand vous aurez codé la borneSup, pensez à l'utiliser dans cet
    // algorithme pour ajouter un cas de base

    // à compléter

    if (id.c == 0)
      return new Solution(id.i.getStartingP());

    var pieces = id.i.getListeCoordPieces();
    var onSingleLine = pieces
        .stream()
        .map(Coord::getL)
        .distinct()
        .count() == 1;
    var onSingleColumn = pieces
        .stream()
        .map(Coord::getC)
        .distinct()
        .count() == 1;

    if ((onSingleLine || onSingleColumn)) {
      if (id.i.borneSup() < id.c)
        return null;
      else {
        var coords = pieces
            .stream()
            .sorted(Comparator.comparingInt(id.i.getStartingP()::distanceFrom))
            .takeWhile(
                (Coord c) -> id.i.getStartingP().distanceFrom(c) <= id.c)
            .collect(Collectors.toList());
        var sol = new Solution();
        coords.forEach(sol::add);
      }
    }

    List<Coord> dejaVisite = new ArrayList<>();
    var result = greedySolver(
        id.i.getStartingP(),
        id.i.piecePresente(id.i.getStartingP()) ? 1 : 0,
        dejaVisite,
        id);

    if (result.second < id.c)
      return null;

    var sol = new Solution();
    result.first.forEach(sol::add);

    return sol;
  }

  public static Solution algoFPT1DP(InstanceDec id, HashMap<InstanceDec, Solution> table) {
    // même spécification que algoFPT1, si ce n'est que
    // - si table.containsKey(id), alors id a déjà été calculée, et on se contente
    // de retourner table.get(id)
    // - sinon, alors on doit calculer la solution s pour id, la ranger dans la
    // table (table.put(id,res)), et la retourner

    // Remarques
    // - ne doit pas modifier l'instance id en param (mais va modifier la table bien
    // sûr)
    // - même si le branchement est le même que dans algoFPT1, ne faites PAS appel à
    // algoFPT1 (et donc il y aura de la duplication de code)

    // à compléter

    if (table.containsKey(id))
      return table.get(id);

    if (id.c == 0)
      return new Solution(id.i.getStartingP());

    ToIntFunction<Coord> hasPiece = (Coord c) -> id.i.piecePresente(c) ? 1 : 0;

    var result = id.i.getStartingP()
        .neighbors(id.i.getNbL(), id.i.getNbC())
        .map((Coord c) -> {
          var instance = new Instance(id.i);
          var gotPiece = hasPiece.applyAsInt(c);
          if (gotPiece == 1)
            instance.retirerPiece(c);
          instance.setStartingP(c);
          var instanceDec = new InstanceDec(id.i, id.c - hasPiece.applyAsInt(c));
          return algoFPT1DP(instanceDec, table);
        })
        .reduce((a, b) -> a.size() > b.size() ? a : b)
        .orElse(null);

    if (result == null)
      return null;

    var sol = new Solution();
    result.forEach(sol::add);

    table.put(id, sol);

    return sol;
  }

  public static Solution algoFPT1DPClient(InstanceDec id) {
    return algoFPT1DP(id, new HashMap<>());
  }

}
