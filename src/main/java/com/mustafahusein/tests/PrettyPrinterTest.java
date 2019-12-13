package com.mustafahusein.tests;

import com.mustafahusein.CLexer;
import com.mustafahusein.CParser;
import com.mustafahusein.errors.errorlisteners.CErrorListener;
import com.mustafahusein.visitors.CPrettyPrinter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Scanner;

public class PrettyPrinterTest  {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine())
            builder.append(scanner.nextLine());

        scanner.close();
        String program = builder.toString();
        CLexer lexer = new CLexer(CharStreams.fromString(program));
        CParser parser = new CParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new CErrorListener());
        String prettyPrinted = new CPrettyPrinter().visitProgram(parser.program());

        System.out.println(prettyPrinted.replaceAll("\\s+","").equals(program.replaceAll("\\s+","")));
    }
}
