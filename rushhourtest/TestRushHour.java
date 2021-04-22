package rushhourtest;
import java.io.File;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;

import rushhour.IllegalMoveException;
import rushhour.RushHour;
import rushhour.Solver;


public class TestRushHour
{

	public static int dirChar2Int(char d) {
		switch (d) {
			case 'U' -> {
				return RushHour.UP;
			}
			case 'D' -> {
				return RushHour.DOWN;
			}
			case 'L' -> {
				return RushHour.LEFT;
			}
			case 'R' -> {
				return RushHour.RIGHT;
			}
			default -> throw new IllegalArgumentException("Unexpected direction: " + d);
		}
	}
	
	public static void testSolution(String puzzleName, String solName) {
		try {
			RushHour puzzle = new RushHour(puzzleName);
			Scanner scannerSolution = new Scanner(new File(solName));
			while (scannerSolution.hasNextLine()) {
				String line = scannerSolution.nextLine();
				if (line.length() != 3)
					throw new IllegalMoveException(line);
				puzzle.makeMove(line.charAt(0), dirChar2Int(line.charAt(1)), line.charAt(2) - '0');
			}
			
			if (puzzle.isSolved()) {
				System.out.println(puzzleName + " Solved");
			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static void main(String[] args) {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		//String puzzleName = "A01.txt";
		//String solName = "solFile.txt";
		try {
			//Solver.solveFromFile("piazza.txt", "solFile.txt");
			//testSolution("piazza.txt", "solFile.txt");

			for(int i = 0; i <= 9; i++) {
				String puzzleName = "A0" + i + ".txt";
				String solName = "solFile.txt";
				Solver.solveFromFile(puzzleName, solName);
				testSolution(puzzleName, solName);
			}
			String puzzleName;
			String solName = "solFile.txt";
			Solver.solveFromFile("A10.txt", solName);
			testSolution("A10.txt", solName);
			for(int i = 11; i <= 20; i++) {
				puzzleName = "B" + i + ".txt";
				solName = "solFile.txt";
				Solver.solveFromFile(puzzleName, solName);
				testSolution(puzzleName, solName);
			}
			for(int i = 21; i <= 29; i++) {
				puzzleName = "C" + i + ".txt";
				solName = "solFile.txt";
				Solver.solveFromFile(puzzleName, solName);
				testSolution(puzzleName, solName);
			}
			for(int i = 30; i <= 35; i++) {
				puzzleName = "D" + i + ".txt";
				solName = "solFile.txt";
				Solver.solveFromFile(puzzleName, solName);
				testSolution(puzzleName, solName);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//testSolution(puzzleName, solName);
	}
}