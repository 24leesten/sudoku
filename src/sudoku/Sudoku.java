package sudoku;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;

/**
 * 
 * @reference Algorithm supplied by http://code.geeksforgeeks.org
 * @author Team SimU
 * 
 * This a simple Sudoku Solver implemented using a recursive strategy.
 *
 */
public class Sudoku {
  
  private static String outFile = "OUTPUT.txt";
  private static String jsonStart = "{\"type\":\"html\",\"html\":\"";
  private static String jsonEnd = "\",\"required_files\":\"sudoku.css\"}";

  public static void main(String[] args) {
    
    // make sure that we have the correct arguments
    if (args.length != 1){
      System.out.println("ERROR: You did not input the correct arguments. \n" + 
      "Please input one csv File to represent the Sudoku Puzzle.");
      return;
    }
    
    // get the filename from the arguments
    String filename = args[0];
    
    // read in the csv file
    CSVReader reader = null;
    List<String[]> sudoku = null;
    try {
      reader = new CSVReader(new FileReader(filename));
      sudoku = reader.readAll();
      reader.close();
    } catch (IOException e) {
      if (sudoku==null)
        System.out.println(" <link rel=\\\"stylesheet\\\" href=\\\"sudoku.css\\\"><h1>Your Sudoku was not read in properly.</h1>");
      e.printStackTrace();
      return;
    }
    
    // create an empty Grid 
    int[][] grid = new int[9][9];
    // fill the empty grid with the Grid from the csv
    int y = 0;
    for (String[] row:sudoku){
      int x = 0;
      for (String col:row){
        grid[y][x] = Integer.parseInt(col);
        x++;
      }
      y++;
    }
    
    // send the css
    // sendCSS();
    
    // show the puzzle being solved
    List<String> lines = Arrays.asList("<link rel=\\\"stylesheet\\\" href=\\\"sudoku.css\\\">" + jsonStart + "<h1>Input</h1>" +  generateHTML(grid) + "\",\"required_files\":\"sudoku.css\"}");
    Path file = Paths.get(outFile);
    try {
      Files.write(file, lines, Charset.forName("UTF-8"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // try to solve the puzzle
    if (solve(grid) != null){
      lines = Arrays.asList(jsonStart + "<h1>Solved</h1>" +  generateHTML(grid) + "\"}");
      file = Paths.get(outFile);
      try {
        Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else{
      lines = Arrays.asList(jsonStart + "<h1>No Solution</h1>" + "\"}");
      file = Paths.get(outFile);
      try {
        Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
  }
  
  
  /**
   * recursive solve function
   * 
   * @param grid
   * @return int[][]
   */
  public static int[][] solve(int[][] grid){
    int[] pos = {0,0};
    
    pos = emptyLocation(grid, pos);
    if (pos == null)
      return grid;
      
    int row = pos[0];
    int col = pos[1];
    
    for (int new_val = 1; new_val < 10; new_val++){
      if (safe(grid, row, col, new_val)){
        grid[row][col] = new_val;
        
        int[][] check = solve(grid);
        if (check != null)
          return check;
        
        grid[row][col] = 0;
      }
    }
    
    return null;
  }

  
  /**
   * find an empty location
   * 
   * @param grid
   * @param pos
   * @return in[]
   */
  private static int[] emptyLocation(int[][] grid, int[] pos) {
    for (int row = 0; row < 9; row++){
      for (int col = 0; col < 9; col++){
        if (grid[row][col] == 0){
          pos[0] = row;
          pos[1] = col;
          return pos;
        }
      }
    }
    return null;
  }

  
  /**
   * check if the cell is safe
   * 
   * @param grid
   * @param row
   * @param col
   * @param new_val
   * @return boolean
   */
  private static boolean safe(int[][] grid, int row, int col, int new_val) {
    if (inRow(grid, row, new_val))
      return false;
    
    else if (inCol(grid, col, new_val))
      return false;
    
    else if (inBox(grid, row, col, new_val))
      return false;
    
    return true;
  }

  
  /**
   * Is the value in the box?
   * 
   * @param grid
   * @param row
   * @param col
   * @param new_val
   * @return boolean
   */
  private static boolean inBox(int[][] grid, int row, int col, int new_val) {
    row = row - row % 3;
    col = col - col % 3;
    
    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++)
        if (grid[i + row][j + col] == new_val)
          return true;
      
    return false;
  }

  
  /**
   * Is the value in the col?
   * 
   * @param grid
   * @param col
   * @param new_val
   * @return
   */
  private static boolean inCol(int[][] grid, int col, int new_val) {
    for (int i = 0; i < 9; i++)
      if (grid[i][col] == new_val)
        return true;
         
    return false;
  }

  /**
   * Is the value in the row?
   * 
   * @param grid
   * @param row
   * @param new_val
   * @return
   */
  private static boolean inRow(int[][] grid, int row, int new_val) {
    for (int i = 0; i < 9; i++)
      if (grid[row][i] == new_val)
        return true;     
    
    return false;
  }
  
  
  /**
   * generate the HTML
   * 
   * @param grid
   * @return
   */
  private static String generateHTML(int[][] grid){
    String html = "";
    
    String tableHeader = "<table id=\\\"sudoku_grid\\\" style=\\\"border-collapse: collapse;\\\">";
    String endTableHeader = "</table>";
    
    html += tableHeader;
    
    for (int[] row:grid)
      html += generateRow(row);
    
    html += endTableHeader;
    
    return html;
  }
  
  
  /**
   * create an html row
   * 
   * @param row
   * @return
   */
  private static String generateRow(int[] row){
    String html = "";
    String tableRow = "<tr>";
    String endTableRow = "</tr>";
    
    html += tableRow;
    
    for (int value:row)
      html += generateCell(value);
    
    html += endTableRow;
    
    return html;
  }
  
  
  /**
   * create and html cell
   * 
   * @param value
   * @return
   */
  private static String generateCell(int value){
    String html = "";
    
    String tableData = "<td class=\\\"sudoku_cell\\\">";
    String endTableData = "</td>";
    String cellVal = "";
    if (value != 0)
      cellVal = Integer.toString(value);
    
    html += tableData + cellVal + endTableData;
    
    return html;
  }
  
  
  /**
   * send css
   */
//  private static void sendCSS(){
//    BufferedReader br = null;
//    try {
//      br = new BufferedReader(new FileReader("css/sudoku.css"));
//      StringBuilder sb = new StringBuilder();
//      String line = br.readLine();
//
//      while (line != null) {
//          sb.append(line);
//          sb.append(System.lineSeparator());
//          line = br.readLine();
//      }
//      String everything = sb.toString();
//      System.out.println(everything);
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//            e.printStackTrace();
//    } finally {
//      try {
//        br.close();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//  }
}

