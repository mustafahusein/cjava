package com.mustafahusein.tests;

import com.mustafahusein.CLexer;
import com.mustafahusein.CParser;
import com.mustafahusein.errors.errorlisteners.CErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Scanner;

public class ParserTest {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine())
            builder.append(scanner.nextLine());

        System.out.println(matches(builder.toString()));
        scanner.close();
    }

    private static boolean matches(String program) {
        try {
            CLexer lexer = new CLexer(CharStreams.fromString(program));
            CParser parser = new CParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new CErrorListener());
            parser.program();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
