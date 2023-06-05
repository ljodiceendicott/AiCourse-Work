package csc460.drivers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class SudokuTranslator {
    static ArrayList<String> assignments;
    static ArrayList<String> constraints;
    String[][] board;
    
    public static void main(String[] args){

    assignments = new ArrayList<>();
    constraints = new ArrayList<>();

    String usage = "Usage: SudokuTranslator <InputFileName> \n"+
    "<InputFileName> - This file should contain only the starting state for the board to be made\n"+
    "This will have values (1-9) and zeros which will be the places where there are blank values\n"+
    "The locations that have values will be set in the initial values";
    
    // if (args.length <= 1) {
    //     System.err.print(usage);
    //     return;
    // }
    // else{
        readfile("../csps/sudokuBoard.txt");
        constraints = makeConstraints();
        exportFile(constraints);

    // }
//args[1] is the input file
//read from this input file
//then output it into a file at ../../cps/sudoku[num+2]
    }
    public static void readfile(String file){
        Scanner reader;
        try {
            reader = new Scanner(new File(file));
            for(int i = 0; i<9; i++){
                for(String variable : reader.nextLine().split(",")){
                    variable= variable.trim();
                    if(Integer.valueOf(variable) == 0){
                        assignments.add(null);
                    }
                    else{
                    assignments.add(variable);
                    }
                    System.out.println(variable);
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
   
    
    }

    public static ArrayList<String> makeConstraints(){
        HashSet<String> combos = new HashSet<>();
        ArrayList<String> constraint = new ArrayList<>();
        for(int i = 0; i<assignments.size(); i++){
            //assignment at i is what the constraints are being made for
            String aspot = "spot"+(i/9+1)+":"+(i%9+1);
            for(int j = 0; j<assignments.size(); j++){
                String bspot = "spot"+(j/9+1)+":"+(j%9+1);
                //if these are both the same then it is the same value so skip
                if(aspot.equals(bspot)){
                    break;
                }
                //they are in the same row
                else if((i/9+1)==(j/9+1)){
                    if(!combos.contains(aspot+","+bspot)&&!combos.contains(bspot+","+aspot)){
                       constraint.add(aspot+","+bspot);
                       combos.add(aspot+","+bspot);
                    }
                }
                //they are in the same column
                else if((i%9+1)==(j%9+1)){
                    if(!combos.contains(aspot+","+bspot)&&!combos.contains(bspot+","+aspot)){
                        constraint.add(aspot+","+bspot);
                        combos.add(aspot+","+bspot);
                     }
                }


            }
        }
        //         //
        //         int rowone = i/9+1;
        //         int colone = i%9+1;
        //         //
        //         int rowtwo = j/9+1;
        //         int coltwo = j%9+1;
        //         if(i==j){
        //             break;
        //         }
        //         else if(colone == coltwo){
        //             constraint.add("spot"+rowtwo+":"+coltwo+",spot"+rowone+":"+colone);
        //         }
        //         else if(rowone == rowtwo){
        //             constraint.add("spot"+rowtwo+":"+coltwo+","+"spot"+rowone+":"+colone);
        //         }
        //     }
        // }
        return constraint;
    }

     /**
     * 
     * Gives the output for the solution to the problem as an output text file
     * 
     * each of the possible variables has a domain value associated with it as well as 
     * 
     * @param filename The name of the CSP file to export.
     */
    public static void exportFile(ArrayList<String> constraint){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("sudoku-from_translator.csp"));
            //writes the 
            for(int i= 0;i<assignments.size(); i++){
                //if the assignment is zero then there is no value making it blank
                if(assignments.get(i)!=null){
                    writer.write("spot"+(i/9+1)+":"+(i%9+1)+"="+assignments.get(i)+" ");
                }
                else{
                    writer.write("spot"+(i/9+1)+":"+(i%9+1)+" ");
                }
            }
            //this is going to be the possible numbers in the sudoku board
            writer.write("\n1 2 3 4 5 6 7 8 9\n");
            //this is the start of te values that all have to be different 
            writer.write("diff ");
            //first loop gets the spot
            for(int i= 0;i<constraint.size()-1; i++){
               writer.write(constraint.get(i)+" ");
            }
            writer.write(constraint.get(constraint.size()-1));
            writer.close();        
        }catch(Exception ex){
            System.out.println(ex+"\nThere was an error writing to the file");
        }
    }
}
