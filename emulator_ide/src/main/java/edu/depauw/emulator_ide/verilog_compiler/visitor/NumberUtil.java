package edu.depauw.emulator_ide.verilog_compiler.visitor;

/** The number utils class was designed to keep track of methods to help with the conversion from String to numerical values
 * @author Jacob Bauer
 */

import java.lang.String;
import java.lang.Number;
import java.lang.Integer;

public class NumberUtil {

    /**The checksize method is used to check if the size on the right hand side of the ' 
     * is equal to the amount of characters on the left hand size of the equals.
     * Ex: "8'b10101010" (Returns True) 2'b10101010(Returns False)
     * @param val the string representation of the number passed in
     */
    public static boolean checkSize(String val){
	return getSize(val) == (val.substring(val.indexOf('\'') + 2).length());
    }

    /* The get size method returns the Size of the string located on the right of the '
     * 20'b00000000... the size of this is 20
     */
    private static int getSize(String val){
        int upTo = val.indexOf('\'');
	return Integer.parseInt(val.substring(0, upTo));
    }

    /** The getBase method returns the base of the number given the base letter
     * b is binary d is decimal 0 is octal etc...
     * @param val string representation of the number
     */

    public static int getBase(String val){
	int id = val.indexOf('\'') + 1;
        switch(Character.toLowerCase(val.charAt(id))){
	case 'd': return 10;
	case 'h': return 16;
	case 'o': return 8;
	case 'b': return 2;
	}
	return -1;
    }

    /** Returns the numerical version of the number
     * @param val the string representation of the number
     */
    public static int getBinary(String val){
	if(checkSize(val)){
	    return Integer.parseInt("0b" + val.substring(val.indexOf('\'') + 2, val.length()));
	} else {
	    System.out.println("Sizes to dot match on integer " + val);
	    return -1;
	}
    }

    /**Returns the Hexidecimal version of the number
     * @param val Number representation of the number
     */

    public static int getHexidecimal(String val){
	if(checkSize(val)){
	    return Integer.parseInt("0x" + val.substring(val.indexOf('\'') + 2));
	} else {
	    System.out.println("Sizes to dot match on integer " + val);
	    return -1;
	}
    }

    /**Returns the Octal version of the number
     * @param val String representation of the number
     */
    public static int getOctal(String val){
	if(checkSize(val)){
	    return Integer.parseInt("0" + val.substring(val.indexOf('\'') + 2));
	} else {
	    System.out.println("Sizes to dot match on integer " + val);
	    return -1;
	}
    }

    /**Returns the Decimal version of the number
     * @param val String representing the number
     */
    public static int getDecimal(String val){
	if(checkSize(val)){
	    return Integer.parseInt(val.substring(val.indexOf('\'') + 2));
	} else {
	    System.out.println("Sizes to dot match on integer " + val);
	    return -1;
	}
    }
}
