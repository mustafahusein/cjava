package com.mustafahusein.utility;

import com.mustafahusein.CLexer;
import com.mustafahusein.CParser;
import com.mustafahusein.errors.errorlisteners.CErrorListener;
import com.mustafahusein.visitors.CTypeChecker;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Scanner;

public final class CTestUtility {
    private CTestUtility() {
    }

    public static boolean parse(String program) {
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

    public static boolean typeCheck(String program) {
        try {
            CLexer lexer = new CLexer(CharStreams.fromString(program));
            CParser parser = new CParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new CErrorListener());
            CTypeChecker typeChecker = new CTypeChecker();
            typeChecker.visitProgram(parser.program());
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static Class<?> serialize(byte[] bytecode) {
        return new ClassLoader() {
            Class<?> defineClass(byte[] bytes) {
                return super.defineClass(null, bytes, 0, bytes.length);
            }
        }.defineClass(bytecode);
    }

    public static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine())
            builder.append(scanner.nextLine()).append("\n");

        scanner.close();
        return builder.toString();
    }
}
