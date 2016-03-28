package com;

import java.util.Scanner;

/**
 * Created by lhtan on 23/3/16.
 */
public class Console {

    public static boolean showInfo = true;

    private Scanner scanner;
    public Console(Scanner scanner) {
        this.scanner = scanner;
    }

    public int askForInteger(){
        while(true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
            }
        }
    }

    public int askForInteger(String question){
        System.out.println(question);
        return askForInteger();
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
