import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Instance {
	private Coord startingP; // point de départ
	private int k; // k>=0, nombre de pas disponibles
	private boolean[][] plateau; // orientation: plateau[0][0] en haut à gauche, et plateau[ligne][col]

	// sortie du problème: une Solution (une solution est valide ssi c'est une liste
	// de coordonées de taille au plus k+1, tel que deux coordonnées consécutives
	// sont à distance 1,
	// et les coordonnées ne sortent pas du plateau)

	private ArrayList<Coord> listeCoordPieces;// attribut supplémentaire (qui est certes redondant) qui contiendra la
	// liste des coordonnées des pièces du plateau
	// on numérote les pièces de haut en bas, puis de gauche à droite, par exemple
	// sur l'instance suivante (s représente
	// le startinP et x les pièces
	// .x..x
	// x..s.
	// ....x

	// les numéros sont
	// .0..1
	// 2..s.
	// ....3
	// et donc listeCoordPices.get(0) est la Coord (0,1)

	/************************************************
	 **** debut methodes fournies ******
	 *************************************************/
	public Instance(boolean[][] p, Coord s, int kk, int hh) {
		plateau = p;
		startingP = s;
		k = kk;
		listeCoordPieces = getListeCoordPieces();
	}

	public Instance(boolean[][] p, Coord s, int kk) {
		plateau = p;
		startingP = s;
		k = kk;
		listeCoordPieces = getListeCoordPieces();
	}

	public Instance(Instance i) { // créer une instance qui est une deep copy (this doit etre independante de i)
		boolean[][] p2 = new boolean[i.plateau.length][i.plateau[0].length];
		for (int l = 0; l < p2.length; l++) {
			for (int c = 0; c < p2[0].length; c++) {
				p2[l][c] = i.plateau[l][c];
			}
		}

		plateau = p2;
		startingP = new Coord(i.startingP);
		k = i.k;
		listeCoordPieces = new ArrayList<>();
		for (Coord c : i.listeCoordPieces) {
			listeCoordPieces.add(new Coord(c));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Instance))
			return false;
		Instance instance = (Instance) o;
		return getK() == instance.getK() && getStartingP().equals(instance.getStartingP())
				&& Algos.egalEnsembliste(getListeCoordPieces(), instance.getListeCoordPieces());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getStartingP(), getK(), getListeCoordPieces());
	}

	public int getNbL() {
		return plateau.length;
	}

	public int getNbC() {
		return plateau[0].length;
	}

	public Coord getStartingP() {
		return startingP;
	}

	public void setStartingP(Coord c) {
		startingP = c;
	}

	public int getK() {
		return k;
	}

	public void setK(int kk) {
		k = kk;
	}

	public ArrayList<Coord> getListeCoordPieces() {
		if (listeCoordPieces == null) {
			ArrayList<Coord> listeCoordPieces = new ArrayList<>();
			for (int l = 0; l < getNbL(); l++) {
				for (int c = 0; c < getNbC(); c++) {
					if (piecePresente(new Coord(l, c))) {
						listeCoordPieces.add(new Coord(l, c));
					}
				}
			}
			return listeCoordPieces;
		} else
			return listeCoordPieces;
	}

	public boolean piecePresente(Coord c) {
		return plateau[c.getL()][c.getC()];
	}

	public void retirerPiece(Coord c) {
		// si pas de piece en c ne fait rien, sinon la retire du plateau et met à jour
		// les coordonnées
		if (piecePresente(c)) {
			plateau[c.getL()][c.getC()] = false;
			listeCoordPieces.remove(c);
		}
	}

	@Override
	public String toString() {
		// retourne une chaine représentant this,
		StringBuilder res = new StringBuilder("k = " + k + "\n" + "nb pieces = " + getListeCoordPieces().size()
				+ "\nstarting point = " + startingP + "\n");
		for (int l = 0; l < getNbL(); l++) {
			for (int c = 0; c < getNbC(); c++) {

				if (startingP.equals(new Coord(l, c)))
					res.append("s");
				else {
					if (piecePresente(new Coord(l, c))) {
						res.append("x");
					} else {
						res.append(".");
					}
				}

			}
			res.append("\n");
		}
		return res.toString() + "\nliste pieces " + getListeCoordPieces();
	}

	public String toString(Solution s) {

		// retourne une chaine sous la forme suivante
		// o!..
		// .ox.
		// .o..
		// .o..

		// où
		// '.' signifie que la solution ne passe pas là, et qu'il n'y a pas de pièce
		// 'x' signifie que la solution ne passe pas là, et qu'il y a pas une pièce
		// 'o' signifie que la solution passe par là, et qu'il n'y a pas de pièce
		// '!' signifie que la solution passe par là, et qu'il y a une pièce

		// dans l'exemple ci-dessus, on avait donc 2 pièces dans l'instance (dont 1
		// ramassée par s)
		// et la chaîne de l'exemple contient 4 fois le caractère "\n" (une fois à
		// chaque fin de ligne)

		if (s == null)
			return null;
		StringBuilder res = new StringBuilder("");// \n k = " + k + "\n" + "nb pieces = " + listeCoordPieces.size() +
		// "\n");
		for (int l = 0; l < getNbL(); l++) {
			for (int c = 0; c < getNbC(); c++) {
				if (startingP.equals(new Coord(l, c)))
					res.append("s");
				else {
					if (s.contains(new Coord(l, c)) && piecePresente(new Coord(l, c))) {
						res.append("!");
					}
					if (!s.contains(new Coord(l, c)) && piecePresente(new Coord(l, c))) {
						res.append("x");
					}
					if (s.contains(new Coord(l, c)) && !piecePresente(new Coord(l, c))) {
						res.append("o");
					}
					if (!s.contains(new Coord(l, c)) && !piecePresente(new Coord(l, c))) {
						res.append(".");
					}
				}
			}
			res.append("\n");
		}
		return res.toString();
	}

	/************************************************
	 **** méthodes à fournir relatives à une solution **
	 *************************************************/

	public boolean estValide(Solution s) {
		// prérequis : s!=null, et les s.get(i) !=null pour tout i (mais par contre s
		// peut contenir n'importe quelle séquence de coordonnées)
		// retourne vrai ssi s est une solution valide (une solution est valide ssi
		// c'est une liste de coordonnées de taille au plus k+1, telle que deux
		// coordonnées consécutives sont à distance 1,
		// et les coordonnées ne sortent pas du plateau)

		if (s == null || s.size() > k + 1)
			return false;

		var noNulls = s.stream().anyMatch((Coord x) -> Objects.isNull(x));
		if (!noNulls)
			return false;

		return !IntStream.range(1, s.size())
				.anyMatch(i -> !s.get(i - 1).estADistanceUn(s.get(i)) || !s.get(i).estDansPlateau(getNbL(), getNbC()));
	}

	public int evaluerSolution(Solution s) {
		// prerequis : s est valide (et donc !=null)
		// action : retourne le nombre de pièces ramassées par s (et ne doit pas
		// modifier this ni s)
		return s.stream().filter(this::piecePresente).reduce(0, (acc, x) -> acc + 1, Integer::sum);
	}

	public int nbStepsToCollectAll(ArrayList<Integer> permut) {

		// prérequis : permut est une permutation des entiers
		// {0,..,listeCoordPieces.size()-1}
		// (et donc permut peut être vide, mais pas null, si il n'y a pas de pièces)

		// retourne le nombre de pas qu'il faudrait pour ramasser toutes les pièces dans
		// l'ordre de permut

		// à compléter

		return 0;
	}

	/************************************************
	 **** méthodes à fournir relatives au greedy ******
	 *************************************************/

	private List<Coord> closestPieces(Coord x, List<Coord> pieces, List<Coord> path) {

		if (path.size() == pieces.size() + 1)
			return path;

		var closest = pieces.stream().min(Comparator.comparingInt(x::distanceFrom)).orElseThrow();

		path.add(x);
		pieces.remove(x);
		return closestPieces(closest, pieces, path);
	}

	public ArrayList<Integer> greedyPermut() {
		// retourne une liste (x1,..,xp) où
		// x1 est la pièce la plus proche du point de départ
		// x2 est la pièce restante la plus proche de x1
		// x3 est la pièce restante la plus proche de x2
		// etc
		// Remarques :
		// -on doit donc retourner une sequence de taille listeCoordPieces.size() (donc
		// sequence vide (et pas null) si il n'y a pas de pièces)
		// -si à un moment donné, lorsque l'on est sur une pièce xi, la pièce restante
		// la plus proche de xi n'est pas unique, alors on peut prendre n'importe quelle
		// pièce (parmi ces plus proches de xi)
		// par exemple,
		// si le plateau est
		// .s.x
		// ....
		// x..x
		// avec la pièce 0 en haut à droite, la pièce 1 en bas à gauche, et la pièce 2
		// en bas à droite,
		// on doit retourner (0,2,1)

		// a compléter

		var res = closestPieces(startingP, listeCoordPieces, new ArrayList<Coord>());
		res.remove(0);

		return res.stream().map(x -> listeCoordPieces.indexOf(x)).collect(Collectors.toCollection(ArrayList::new));
	}

	public List<Coord> getPathBetween(Coord start, Coord end) {
		return Stream
				.iterate(
						start,
						coord -> !coord.equals(end),
						coord -> coord.nextTo(end)
				).collect(Collectors.toList());
		// Same
		// var path = new ArrayList<Coord>();
		// var current = start;
		// while (!current.equals(end)) {
		// 	path.add(current);
		// 	var next = current.nextTo(end);
		// 	current = next;
		// }
		// return path;
	}

	// TODO : à compléter
	public Solution calculerSol(ArrayList<Integer> permut) {

		// prérequis : permut est une permutation des entiers
		// {0,..,listeCoordPieces.size()-1}
		// (et donc permut peut être vide, mais pas null, si il n'y a pas de pièces)

		// retourne la solution obtenue en concaténant les plus courts chemins
		// successifs pour attraper
		// les pièces dans l'ordre donné par this.permut, jusqu'à avoir k mouvements ou
		// à avoir ramassé toutes les pièces de la permut.
		// Remarque : entre deux pièces consécutives, le plus court chemin n'est pas
		// unique, n'importe quel plus court chemin est ok.
		// par ex, si le plateau est
		// .s.x
		// ....
		// x..x
		// avec la pièce 0 en haut à droite, la pièce 1 en bas à gauche, et la pièce 2
		// en bas à droite,
		// et que permut = (0,2,1), alors pour
		// k=3, il faut retourner (0,1)(0,2)(0,3)(1,3) (dans ce cas là les plus courts
		// sont uniques)
		// k=10, il faut retourner (0,1)(0,2)(0,3)(1,3)(2,3)(2,2)(2,1)(2,0) (dans ce cas
		// là les plus courts sont aussi uniques,
		// et on s'arrête avant d'avoir fait k pas car on a tout collecté)

		// a compléter

		Function<Integer, Coord> getPiece = i -> listeCoordPieces.get(permut.get(i));

		var piecesPath = Stream
			.iterate(0, n -> n < permut.size() - 1, n -> n + 1)
			.flatMap(i -> getPathBetween(
							getPiece.apply(i),
							getPiece.apply(i + 1)
					).stream()
			);

		

		var current = startingP;
		var path = new ArrayList<Coord>();

		for (int i = 0; i < permut.size() - 1; i++) {
			var tempPath = getPathBetween(current, listeCoordPieces.get(permut.get(i)));
			path.addAll(path);
			current = listeCoordPieces.get(permut.get(i));
		}

		Solution res;
		
		for(int i=0;i<k;i++) {
			if(path.size() == 0) {
				return res;
			}
			res.add(path.remove(0));
		}
		
		return res;

		return null;
	}

	/************************************************
	 **** fin algo algo greedy ******
	 *************************************************/

	// TODO
	public int borneSup() {
		// soit d0 la distance min entre la position de départ et une pièce
		// soit {d1,..,dx} l'ensemble des distances entre pièces (donc x =
		// (nbpiece-1)*npbpiece / 2), triées par ordre croissant
		// retourne le nombre de pièces que l'on capturerait avec k pas dans le cas
		// idéal où
		// toutes les pièces seraient disposées sur une ligne ainsi : (avec sp le point
		// de départ à gauche)
		// sp .... p .. p .... p ....... p ...
		// d0 d1 d2 d3
		// (vous pouvez réfléchir au fait que c'est bien une borne supérieure)
		// (pour des exemples précis, cf les tests)

		// à compléter

		return 0;
	}
}