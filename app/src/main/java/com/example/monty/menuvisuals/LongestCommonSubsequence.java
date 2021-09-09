package com.example.monty.menuvisuals;

/**
 * Created by Monty on 19-03-2017.
 */

public class LongestCommonSubsequence {
    private String ocrString = "";
    private String databaseString= "";
    LongestCommonSubsequence(String databaseString,String ocrString){
        this.databaseString = databaseString;
        this.ocrString = ocrString;
    }

    boolean check(){
        int sizeofDatabaseString = databaseString.length() + 1;
        int sizeofOCRString = ocrString.length() + 1;
        int[][] table = new int[sizeofDatabaseString][sizeofOCRString];
       for (int i=0 ; i < sizeofDatabaseString ; i++)
           table[i][0] = 0;
        for (int i=0 ; i < sizeofOCRString ; i++ )
            table[0][i] = 0;
        for( int i = 1 ; i < sizeofDatabaseString ; i++ ){
            for(int j = 1 ; j < sizeofOCRString ; j++){
                int val1 = table[i][j-1];
                int val2 = table[i-1][j];
                if(databaseString.charAt(i-1) == ocrString.charAt(j-1))
                    table[i][j] = table[i-1][j-1] + 1;
                else if(val1 > val2){
                    table[i][j] = val1;
                }
                else{
                    table[i][j] = val2;
                }
            }
        }
        //Error Calculation
        double error = ((databaseString.length()-table[databaseString.length()][ocrString.length()])*100)/databaseString.length();
        if(error <= 40)
            return true;
        else
            return false;
    }
}
