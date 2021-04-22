package rushhour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static rushhour.RushHour.*;

public class Solver
{
	public static char[] car;
	public static int[] direction;
	public static int[] len;
	public static int n;

	private static class Node{
		private final char[][] board;
		private final String move;
		private final Node prev;

		public Node(char[][] board, String move, Node prev){
			this.board = board;
			this.move = move;
			this.prev = prev;
		}
	}

	public static void solveFromFile(String inputPath, String outputPath) throws FileNotFoundException {
		RushHour input = new RushHour(inputPath);
		char[][] initial = input.getBoard();
		Node initialNode = new Node(initial, null, null);
		boolean solved = false;
		Node solutionNode = null;

		n = input.cars.size();
		car = new char[n];
		direction = new int[n];
		len = new int[n];


		int c = 0;
		for(Character key : input.cars.keySet()) {
			RushHour.Car given = input.cars.get(key);
			car[c] = given.name;
			direction[c] = given.dir;
			len[c] = given.length;
			c++;
		}

		Queue<Node> queue = new LinkedList<>();
		queue.add(initialNode);
		///////////////////////////////////////////////////////////////////////////////////////////////
		// arrayList
		/*
		ArrayList<String> visited = new ArrayList<>();
		visited.add(oneD(initial));

		outerLoop:
		while(!queue.isEmpty()) {
			int size = queue.size();
			while(size > 0) {
				Node node = queue.poll();
				ArrayList<Node> childList = states(node);

				for(Node childNode: childList) {
					if(isSolved(childNode.board)) {
						solved = true;
						solutionNode = childNode;
						break outerLoop;
					}
					if(!visited.contains(oneD(childNode.board))) {
						queue.add(childNode);
						visited.add(oneD(childNode.board));
					}
				}
				size--;
			}
		}

		 */
		///////////////////////////////////////////////////////////////////////////////
		// hash map
		Byte b = 0;
		HashMap<String, Byte> visit = new HashMap<>();
		visit.put(oneD(initial), b);

		outerLoop:
		while(!queue.isEmpty()) {
			int size = queue.size();
			while(size > 0) {
				Node node = queue.poll();
				ArrayList<Node> childList = states(node);

				for(Node childNode: childList) {
					if(isSolved(childNode.board)) {
						solved = true;
						solutionNode = childNode;
						break outerLoop;
					}
					if(visit.get(oneD(childNode.board)) == null) {
						queue.add(childNode);
						visit.put(oneD(childNode.board), b);
					}
				}
				size--;
			}
		}
		///////////////////////////////////////////////////////////////


		Vector<String> moveSequence = new Vector<>();
		if(solved) {
			while(solutionNode.prev != null) {
				moveSequence.add(solutionNode.move);
				solutionNode = solutionNode.prev;
			}
		}
		Collections.reverse(moveSequence);

		/*
		for (String s : moveSequence) {
			System.out.println(s);
		}
		 */

		File solFile = new File(outputPath);
		try {
			if (solFile.createNewFile()) {
				FileWriter myWriter = new FileWriter(outputPath);
				for(int i = 0; i < moveSequence.size() - 1; i++) {
					myWriter.write(moveSequence.get(i));
					myWriter.write("\n");
				}
				myWriter.write(moveSequence.get(moveSequence.size() - 1));
				myWriter.close();
			}
			else{
				FileWriter myWriter = new FileWriter(outputPath);
				for(int i = 0; i < moveSequence.size() - 1; i++) {
					myWriter.write(moveSequence.get(i));
					myWriter.write("\n");
				}
				myWriter.write(moveSequence.get(moveSequence.size() - 1));
				myWriter.close();
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	/*
	for(String s : moveSequence){
					myWriter.write(s);
					myWriter.write("\n");
				}
	 */

	public static String oneD(char[][] arr) {
		StringBuilder convert = new StringBuilder();
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				convert.append(arr[i][j]);
			}
		}
		return convert.toString();
	}

	public static ArrayList<Node> states(Node nd) {
		ArrayList<Node> adjacent = new ArrayList<>();
		boolean clear;
		int start, constant;
		for(int v = 0; v < n; v++) {
			start = 0;
			constant = 0;
			if(direction[v] == HORIZONTAL) {
				outerLoop:
				for(int i = 0; i < size; i++) {
					for(int j = 0; j < size; j++) {
						if(nd.board[i][j] == car[v]) {
							start = j;
							constant = i;
							break outerLoop;
						}
					}
				}
				// left
				for(int l = 1; l < size; l++) {
					clear = true;
					// in bounds?
					if(start - l >= 0) {
						for(int j = start - l; j < start; j++) {
							if(nd.board[constant][j] != '.' && nd.board[constant][j] != car[v]) {
								clear = false;
								break;
							}
						}
						if(clear) {
							char[][] base = new char[size][size];
							for(int i = 0; i < size; i++) {
								System.arraycopy(nd.board[i], 0, base[i], 0, size);
							}
							for(int j = start; j < start + len[v]; j++) {
								base[constant][j] = '.';
							}
							for(int j = start - l; j < start - l + len[v]; j++) {
								base[constant][j] = car[v];
							}
							String action = "" + car[v] + "L" + l;
							Node newNode = new Node(base, action, nd);
							adjacent.add(newNode);
						}
					}
				}
				// right
				for(int l = 1; l < size; l++) {
					clear = true;
					// in bounds?
					if(start + l + len[v] <= size) {
						for(int j = start + len[v]; j < start + l + len[v]; j++) {
							if(nd.board[constant][j] != '.' && nd.board[constant][j] != car[v]) {
								clear = false;
								break;
							}
						}
						if(clear) {
							char[][] base = new char[size][size];
							for(int i = 0; i < size; i++) {
								System.arraycopy(nd.board[i], 0, base[i], 0, size);
							}
							for(int j = start; j < start + len[v]; j++) {
								base[constant][j] = '.';
							}
							for(int j = start + l; j < start + l + len[v]; j++) {
								base[constant][j] = car[v];
							}
							String action = "" + car[v] + "R" + l;
							Node newNode = new Node(base, action, nd);
							adjacent.add(newNode);
						}
					}
				}
			}
			else {
				outerLoop:
				for(int i = 0; i < size; i++) {
					for(int j = 0; j < size; j++) {
						if(nd.board[i][j] == car[v]) {
							start = i;
							constant = j;
							break outerLoop;
						}
					}
				}
				// up
				for(int l = 1; l < size; l++) {
					clear = true;
					// in bounds?
					if(start - l >= 0) {
						for(int i = start - l; i < start; i++) {
							if(nd.board[i][constant] != '.' && nd.board[i][constant] != car[v]) {
								clear = false;
								break;
							}
						}
						if(clear) {
							char[][] base = new char[size][size];
							for(int i = 0; i < size; i++) {
								System.arraycopy(nd.board[i], 0, base[i], 0, size);
							}
							for(int i = start; i < start + len[v]; i++) {
								base[i][constant] = '.';
							}
							for(int i = start - l; i < start - l + len[v]; i++) {
								base[i][constant] = car[v];
							}
							String action = "" + car[v] + "U" + l;
							Node newNode = new Node(base, action, nd);
							adjacent.add(newNode);
						}
					}
				}

				// down
				for(int l = 1; l < size; l++) {
					clear = true;
					// in bounds?
					if(start + l + len[v] <= size) {
						for(int i = start + len[v]; i < start + l + len[v]; i++) {
							if(nd.board[i][constant] != '.' && nd.board[i][constant] != car[v]) {
								clear = false;
								break;
							}
						}
						if(clear) {
							char[][] base = new char[size][size];
							for(int i = 0; i < size; i++) {
								System.arraycopy(nd.board[i], 0, base[i], 0, size);
							}
							for(int i = start; i < start + len[v]; i++) {
								base[i][constant] = '.';
							}
							for(int i = start + l; i < start + l + len[v]; i++) {
								base[i][constant] = car[v];
							}
							String action = "" + car[v] + "D" + l;
							Node newNode = new Node(base, action, nd);
							adjacent.add(newNode);
						}
					}
				}
			}
		}
		return adjacent;
	}

	public static void printBoard(char[][] arr) {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				System.out.print(arr[i][j]);
			}
			System.out.println();
		}
	}

	public static boolean isSolved(char[][] input) {
		for(int i = 0; i < size; i++) {
			if(input[i][size - 1] == 'X') {
				return true;
			}
		}
		return false;
	}
}