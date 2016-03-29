package com;

import java.util.Scanner;

/**
 * Created by lhtan on 23/3/16.
 * This is the Console Class for
 * the client side
 * This class in charge on the
 * result displaying on console
 *
 */
public class Console {

    public static boolean showInfo = true;

    private Scanner scanner;

    /**
     * Class Constructor for Console Class
     * @param scanner       Scanner for user input
     */
    public Console(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Prompt User to key in integer of choices
     * @param question      A string of question to ask user for input
     * @return
     */
    public int askForInteger(String question){
        System.out.println(question);
        return askForInteger();
    }

    /**
     * This method read Client's input of integer
     * @return  Integer if user key in correctly, error message otherwise
     */
    public int askForInteger(){
        while(true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
            }
        }
    }

    public String askForString(String question){
        System.out.println(question);
        return scanner.nextLine();
    }

    public static void println(String line){
        System.out.println(line);
    }
    public static void info(String line){
        if (showInfo)   System.out.println(line);
    }
}
