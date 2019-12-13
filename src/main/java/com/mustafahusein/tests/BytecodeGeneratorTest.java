package com.mustafahusein.tests;

import com.mustafahusein.CLexer;
import com.mustafahusein.CParser;
import com.mustafahusein.errors.exceptions.TypeCheckError;
import com.mustafahusein.visitors.CBytecodeGenerator;
import com.mustafahusein.visitors.CTypeChecker;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.mustafahusein.utility.CTestUtility.getUserInput;
import static com.mustafahusein.utility.CTestUtility.serialize;

public class BytecodeGeneratorTest {
    public static void main(String[] args) {
        String program = getUserInput();

        CLexer lexer = new CLexer(CharStreams.fromString(program));
        CParser parser = new CParser(new CommonTokenStream(lexer));

        CTypeChecker typeChecker = new CTypeChecker();
        CBytecodeGenerator bytecodeGenerator = new CBytecodeGenerator();

        try {
            typeChecker.visitProgram(parser.program());
            bytecodeGenerator.visitProgram(parser.program());

            FileOutputStream outputStream = new FileOutputStream("Main.class");
            outputStream.write(bytecodeGenerator.getBytecode());
            outputStream.close();

            Class<?> generatedClass = serialize(bytecodeGenerator.getBytecode());
            Method mainMethod = generatedClass.getDeclaredMethod("main");
            mainMethod.invoke(null);
        } catch (TypeCheckError ex) {
            System.out.println(ex.getMessage());
        } catch (IllegalAccessException |
                NoSuchMethodException |
                InvocationTargetException |
                IOException e) {
            e.printStackTrace();
        }
    }
}
