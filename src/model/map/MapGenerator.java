package model.map;

/**
 * Generates a map based upon a 2D heightmap. The algorithm used for generation
 * is explained in detail <a href="http://dxprog.com/files/randmaps.html">here</a>.
 * @author Christopher Chapline, James Fagan, Emily Leones, Michelle Yung.
 *
 */
public class MapGenerator {

	/**
	 * Generates a two-dimensional heightmap and lays it out with terrain
	 * @param rows The number of rows in the map
	 * @param cols The number of columsn in the map
	 * @param waterBorder The number of rows/columns on the outer border automatically set to water
	 * @return The map generated by the 2D heightmap
	 */
	public static Cell[][] generateMap( int rows, int cols, int waterBorder ) {

		Cell[][] map = new Cell[rows][cols];
		
		//Initialize cells
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				map[i][j] = new Cell();
			}
		}
		
		//Generate terrain and water
		map = setTerrain( map, rows, cols );
		map = generateWater( map, rows, cols, waterBorder );
		map = addCoast( map, rows, cols );
		return map;
	}

	/**
	 * Adds resources to the map based on the 2D heightmap algorithm described <a href="http://dxprog.com/files/randmaps.html">here</a>.
	 * @param map The base map being generated on
	 * @return The map with resources added
	 */
	private static Cell[][] setTerrain( Cell[][] map, int rows, int cols ) {
		int average;
		
		//Iterate through each row and calculate it's average with respect to surrounding cells
		for (int i = 1; i < rows - 1; i++) {
			for (int j = 1; j < cols - 1; j++) {
				average = 0;
				
				//Calculates the average on the cell based on it's neighbors
				for (int m = i - 1; m <= i + 1; m++) {
					for (int n = j - 1; n <= j + 1; n++) {
						average += map[i][j].getAverage();
					}
				}
				map[i][j].setAverage(average / 9);
			}
		}

		//Add resources based on the values in the height map
		for (int i = 1; i < rows - 1; i++) {
			for (int j = 1; j < cols - 1; j++) {
				int a = map[i][j].getAverage();
				if (a < 40) {
					map[i][j].setResource(Resource.tree);
				} else if (a > 290) {
					map[i][j].setResource(Resource.stone);
				} else if (a >= 40 && a <= 43) {
					map[i][j].setResource(Resource.goldMine);
				} else if (a >= 44 && a < 50) {
					map[i][j].setResource(Resource.garden);
				} else if (a >=50 && a < 55) {
					map[i][j].setTerrain(Terrain.rockyPlains);
				}
			}
		}

		return map;
	}

	/**
	 * Places water tiles on the map
	 * @param map The map to place water on
	 * @param rows The number of 
	 * @param cols
	 * @param waterBorder Dictates how much of the outer border will default to water. That is, moving from
	 * 						the outer border of the map, how many rows/columns in will automatically be set
	 * 						to water tiles.
	 * @return The map with water tiles added
	 */
	private static Cell[][] generateWater( Cell[][] map, int rows, int cols, int waterBorder ) {

		//Fills the outer border of the map with water tiles (dictated by the waterBorder parameter)
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (i < waterBorder || j < waterBorder || i > (rows - waterBorder - 1) || j > (cols - waterBorder - 1)) {
					map[i][j].setTerrain(Terrain.water);
				}
			}
		}

		//Spreads water to create pools and rivers
		for (int k = waterBorder; k < rows * .75; k++) {
			for (int i = waterBorder - 1; i <= rows - waterBorder; i++) {
				for (int j = waterBorder - 1; j <= cols - waterBorder; j++) {
					if (map[i][j].getTerrain().equals(Terrain.water)
							&& map[i][j].getAverage() < 200) {
						if (j == k - 1) {
							map[i][j  + 1].setTerrain(Terrain.water);
							map[i][j + 1].removeResource();
						}
						if (j == cols - k) {
							map[i][j - 1].setTerrain(Terrain.water);
							map[i][j - 1].removeResource();
						}
						if (i == k - 1) {
							map[i + 1][j].setTerrain(Terrain.water);
							map[i + 1][j].removeResource();
						}
						if (i == rows - k) {
							map[i - 1][j].setTerrain(Terrain.water);
							map[i - 1][j].removeResource();
						}
					}
				}
			}
		}

		//Handles situations where a land tile is surrounded by water on all 4 sides.
		for (int i = 1; i < rows - 1; i++) {
			for (int j = 1; j < cols - 1; j++)
				if (map[i + 1][j].getTerrain().equals(Terrain.water)
						&& map[i - 1][j].getTerrain().equals(Terrain.water)
						&& map[i][j + 1].getTerrain().equals(Terrain.water)
						&& map[i][j - 1].getTerrain().equals(Terrain.water)) {
					map[i][j].setTerrain(Terrain.water);
					map[i][j].removeResource();
				}
		}
		return map;
	}
	
	// sets terrain to coast if tile is next to water
	private static Cell[][] addCoast( Cell[][] map, int rows, int cols) {
		for (int i = 1; i < rows - 1; i++) {
			for (int j = 1; j < cols - 1; j++) {
				if (!map[i][j].getTerrain().equals(Terrain.water)) {
					if (map[i + 1][j].getTerrain().equals(Terrain.water)
						|| map[i - 1][j].getTerrain().equals(Terrain.water)
						|| map[i][j + 1].getTerrain().equals(Terrain.water)
						|| map[i][j - 1].getTerrain().equals(Terrain.water)) {
						map[i][j].setTerrain(Terrain.coast);
					}
				}
			}
		}
		return map;
	}
}
