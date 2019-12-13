package com.mustafahusein.tests;

import com.mustafahusein.CLexer;
import com.mustafahusein.CParser;
import com.mustafahusein.errors.exceptions.InterpretationError;
import com.mustafahusein.visitors.CInterpreter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Scanner;


public class InterpreterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine())
            builder.append(scanner.nextLine()).append("\n");

        scanner.close();
        CLexer lexer = new CLexer(CharStreams.fromString(builder.toString()));
        CParser parser = new CParser(new CommonTokenStream(lexer));
        CInterpreter interpreter = new CInterpreter();

        try {
            interpreter.visitProgram(parser.program());
        } catch (InterpretationError ex) {
            System.out.println(ex.getMessage());
        }
    }
}
