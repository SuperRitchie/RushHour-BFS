package rushhour;

import java.io.*;
import java.util.*;

// this class taken as template from Igor's testSolver
public class RushHour
{

	public final static int HORIZONTAL = 0;
	public final static int VERTICAL = 1;


	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;

	public final static int size = 6;

	public class Car
	{
		public char name;
		public int dir;
		public int topLeftX;
		public int topLeftY;
		public int length;


		/**
		 * @param name 
		 * @param dir - HORIZONTAL or VERTICAL
		 * @param x,y - represent the top left position of the top left corner of the car
		 */
		public Car(char name, int dir, int x, int y, int len) {
			this.name = name;
			this.dir = dir;
			this.topLeftX = x;
			this.topLeftY = y;
			this.length = len;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;

			if (!(other instanceof Car))
				return false;

			return (this.name == ((Car)other).name);
		}

	}

	char[][] board;

	public Map<Character, Car> cars;

	public RushHour(RushHour rb) {
		this.board = rb.board;
		this.cars = rb.cars;
	}

	public char[][] getBoard() {
		return board;
	}

	public RushHour(String fileName) throws FileNotFoundException {

		//
		//	..CCC.
		//  ..XX..	// board[1][2] = X board[1][3] = X
		//  ..G...
		//  ..G..A
		//  ..G..A
		//  LL....
		board = new char[size][size];
		int i,j;
		File file= new File(fileName);
		try (Scanner reader = new Scanner(file)) {
			for (i = 0; i < size; i++) {
				String data = reader.nextLine();
				for (j = 0; j < size; j++)
					board[i][j] = data.charAt(j);
			}
		} catch (FileNotFoundException exception) {
			throw exception;
		} catch (Exception e) {
			throw new BadBoardException(e);
		}


		// create list of cars
		cars = new HashMap<>();
		for (i = 0; i < board.length; i++) {
			for (j = 0; j < board.length; j++) {
				if (board[i][j] != '.') {
					int dir = findDirection(i,j);
					int len = 1;
					if (dir == HORIZONTAL) {
						int k = 0;
						while (j+k < size && board[i][j+k] == board[i][j])
							k++;
						len = k;
					}
					if (dir == VERTICAL) {
						int k = 0;
						while (i+k < size && board[i+k][j] == board[i][j])
							k++;
						len = k;
					}
					Car newCar = new Car(board[i][j], dir, j, i, len);
					cars.putIfAbsent(newCar.name, newCar);	// only the top left corner is added
				}
			}
		}

	}
	/**
	 * @param carName
	 * @param direction
	 * @param dist
	 * Moves car with the given name for length steps in the given direction  
	 * @throws IllegalMoveException if the move is illegal
	 */
	public void makeMove(char carName, int direction, int dist) throws IllegalMoveException {
		Car car = cars.get(carName);
		if (car.dir == HORIZONTAL && (direction == UP || direction == DOWN)) {
			throw new IllegalMoveException("car" + carName + " tried moving vertically");
		}
		if (car.dir == VERTICAL && (direction == RIGHT || direction == LEFT)) {
			throw new IllegalMoveException("car" + carName + " tried moving horizontally");
		}
		checkLegalMove(car, direction, dist);
		switch (direction) {
			case RIGHT -> {
				// erase from current position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY][car.topLeftX + j] = '.';

				// add to new position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY][car.topLeftX + dist + j] = car.name;

				car.topLeftX += dist;
			}
			case LEFT -> {
				// erase from current position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY][car.topLeftX + j] = '.';

				// add to new position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY][car.topLeftX - dist + j] = car.name;

				car.topLeftX -= dist;
			}
			case DOWN -> {
				// erase from current position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY + j][car.topLeftX] = '.';

				// add to new position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY + dist + j][car.topLeftX] = car.name;

				car.topLeftY += dist;
			}
			case UP -> {

				// erase from current position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY + j][car.topLeftX] = '.';

				// add to new position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY - dist + j][car.topLeftX] = car.name;
				car.topLeftY -= dist;
			}
			default -> throw new IllegalArgumentException("Bad direction: " + direction);
		}
		
	}

	private void checkLegalMove(Car car, int direction, int dist) throws IllegalMoveException
	{
		switch (direction) {
			case RIGHT -> {
				if (car.topLeftX + car.length + dist > size)
					throw new IllegalMoveException("move " + car.name + " RIGHT " + dist + ": OUT OF BOUNDS");
				for (int j = 0; j < dist; j++) {
					if (board[car.topLeftY][car.topLeftX + car.length + j] != '.')
						throw new IllegalMoveException("move " + car.name + " RIGHT " + dist + ": "
								+ board[car.topLeftY][car.topLeftX + car.length + j] + " IN A WAY");
				}
			}
			case LEFT -> {
				if (car.topLeftX - dist < 0)
					throw new IllegalMoveException("move " + car.name + " LEFT " + dist + ": OUT OF BOUNDS");
				for (int j = 0; j < dist; j++) {
					if (board[car.topLeftY][car.topLeftX - j - 1] != '.')
						throw new IllegalMoveException("move " + car.name + " LEFT " + dist + ": "
								+ board[car.topLeftY][car.topLeftX - j - 1] + " IN A WAY");
				}
			}
			case DOWN -> {
				if (car.topLeftY + car.length + dist > size)
					throw new IllegalMoveException("move " + car.name + " DOWN " + dist + ": OUT OF BOUNDS");
				for (int j = 0; j < dist; j++) {
					if (board[car.topLeftY + car.length + j][car.topLeftX] != '.')
						throw new IllegalMoveException("move " + car.name + " DOWN " + dist + ": "
								+ board[car.topLeftY][car.topLeftX + car.length + j] + " IN A WAY");
				}
			}
			case UP -> {
				if (car.topLeftY - dist < 0)
					throw new IllegalMoveException("move " + car.name + " UP " + dist + ": OUT OF BOUNDS");
				for (int j = 0; j < dist; j++) {
					if (board[car.topLeftY - j - 1][car.topLeftX] != '.')
						throw new IllegalMoveException("move " + car.name + " UP " + dist + ": "
								+ board[car.topLeftY - j - 1][car.topLeftX] + " IN A WAY");
				}
			}
			default -> throw new IllegalMoveException("Bad direction: " + direction);
		}
	}

	private int findDirection(int i, int j){
		if ((j>=1 && board[i][j] == board[i][j-1]) || (j<=size-2 && board[i][j] == board[i][j+1]))
			return HORIZONTAL;
		else if ((i>=1 && board[i][j] == board[i-1][j]) || (i<=size-2 && board[i][j] == board[i+1][j]))
			return VERTICAL;
		else
			throw new BadBoardException("board[" + i + "][" + j + "j]");
		
	}

	/**
	 * @return true if and only if the board is solved,
	 * i.e., the XX car is touching the right edge of the board
	 */
	public boolean isSolved() {
		Car xCar = cars.get('X');
		return (xCar.topLeftX+xCar.length == size);
	}

	public void printBoard() {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
	}

	public ArrayList<RushHour> neighbours()  {
		ArrayList<RushHour> neighbours = new ArrayList<>();
		int magnitude;
		for(Character key : cars.keySet()) {
			magnitude = size;
			if(cars.get(key).dir == VERTICAL) {
				for(int i = 0; i < size; i++) {
					if(move(key, UP, magnitude)) {
						RushHour neighbour = new RushHour(this);
						neighbours.add(neighbour);
					}
					if(legal(cars.get(key), DOWN, magnitude)) {
						RushHour neighbour = new RushHour(this);
						neighbours.add(neighbour);
					}
					magnitude--;
				}

			}
			else {
				for(int i = 0; i < size; i++) {
					if(legal(cars.get(key), LEFT, magnitude)) {
						RushHour neighbour = new RushHour(this);
						neighbours.add(neighbour);
					}
					if(legal(cars.get(key), RIGHT, magnitude)) {
						RushHour neighbour = new RushHour(this);
						neighbours.add(neighbour);
					}
					magnitude--;
				}
			}
		}
		return neighbours;
	}

	public boolean move(char carName, int direction, int dist) {
		Car car = cars.get(carName);
		if (car.dir == HORIZONTAL && (direction == UP || direction == DOWN)) {
			return false;
		}
		if (car.dir == VERTICAL && (direction == RIGHT || direction == LEFT)) {
			return false;
		}
		if(!legal(car, direction, dist)) {
			return false;
		}
		switch (direction) {
			case RIGHT: {

				// erase from current position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY][car.topLeftX+j] ='.';

				// add to new position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY][car.topLeftX+dist+j] =car.name;

				car.topLeftX+=dist;
				return true;
			}
			case LEFT: {
				// erase from current position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY][car.topLeftX+j] ='.';

				// add to new position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY][car.topLeftX-dist+j] =car.name;

				car.topLeftX-=dist;
				return true;
			}
			case DOWN: {
				// erase from current position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY+j][car.topLeftX] ='.';

				// add to new position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY+dist+j][car.topLeftX] =car.name;

				car.topLeftY+=dist;
				return true;
			}
			case UP: {

				// erase from current position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY+j][car.topLeftX] ='.';

				// add to new position
				for (int j = 0; j < car.length; j++)
					board[car.topLeftY-dist+j][car.topLeftX] =car.name;
				car.topLeftY-=dist;
				return true;
			}
			default:
				return false;
		}

	}

	private boolean legal(Car car, int direction, int dist) {
		switch (direction) {
			case RIGHT: {
				if (car.topLeftX + car.length + dist > size) {
					return false;
				}
				for (int j = 0; j < dist; j++) {
					if (board[car.topLeftY][car.topLeftX+car.length+j] !='.') {
						return false;
					}
				}
				return true;
			}
			case LEFT: {
				if (car.topLeftX - dist < 0) {
					return false;
				}
				for (int j = 0; j < dist; j++) {
					if (board[car.topLeftY][car.topLeftX-j-1] !='.') {
						return false;
					}
				}
				return true;
			}
			case DOWN: {
				if (car.topLeftY + car.length + dist > size) {
					return false;
				}
				for (int j = 0; j < dist; j++) {
					if (board[car.topLeftY+car.length+j][car.topLeftX] !='.') {
						return false;
					}
				}
				return true;
			}
			case UP: {
				if (car.topLeftY - dist < 0) {
					return false;
				}
				for (int j = 0; j < dist; j++) {
					if (board[car.topLeftY-j-1][car.topLeftX] !='.') {
						return false;
					}
				}
				return true;
			}
			default:
				return false;
		}
	}
}
