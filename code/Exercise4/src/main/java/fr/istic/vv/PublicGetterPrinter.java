package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.utils.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PublicGetterPrinter extends VoidVisitorWithDefaults<Void> {

    Map<FieldDeclaration, Pair<String,String>> privateFieldsWithContext = new HashMap<>();

    String currentClass;

    String currentPackage;


    @Override
    public void visit(CompilationUnit unit, Void arg) {
        if(unit.getPackageDeclaration().isPresent()){
            currentPackage = unit.getPackageDeclaration().get().toString().replace("\n", "").replace("package ", "").replace(";","");
        }else{
            currentPackage = "no package";
        }
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
        for(FieldDeclaration member : declaration.getFields()) {
            if (member instanceof FieldDeclaration)
                member.accept(this, arg);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        currentClass = declaration.getNameAsString();
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {

        if(!declaration.isPublic()) return;
        Iterator<Map.Entry<FieldDeclaration, Pair<String,String>>> iter = privateFieldsWithContext.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<FieldDeclaration, Pair<String,String>> entry = iter.next();
            String fieldString = entry.getKey().getVariable(0).getNameAsString();
            String capitalizedField = fieldString.substring(0, 1).toUpperCase() + fieldString.substring(1);

            //check if the name of the method is get<name-of-field> with a capitalized first letter
            if (declaration.getNameAsString().startsWith("get" + capitalizedField)) {

                //check if the method returns the field
                if (declaration.getBody().isPresent()) {
                    for (Statement statement : declaration.getBody().get().getStatements()) {
                        if ((statement.toString().equalsIgnoreCase("return " + fieldString + ";")) || (statement.toString().equalsIgnoreCase("return this." + fieldString + ";"))) {
                            System.out.println("statement " + statement.toString());
                            iter.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visit(FieldDeclaration declaration, Void arg) {
        //We put all the private fields in a map with its class and package
        if(!declaration.isPublic()){
            privateFieldsWithContext.put(declaration, new Pair<>(currentClass, currentPackage));
        }
    }

    //method to export our private fields without getter to a csv file
    public void export() throws IOException {
        List<String[]> dataLines = new ArrayList<>();
        for (Map.Entry<FieldDeclaration, Pair<String,String>> entry : privateFieldsWithContext.entrySet()) {
            String[] noGetter = new String[]
                    { entry.getValue().b, entry.getValue().a, entry.getKey().getVariable(0).getNameAsString()};
            dataLines.add(noGetter);
        }
        String currentPath = new java.io.File(".").getCanonicalPath();
        File csvOutputFile = new File(currentPath + "/exportedCSV.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
    }

    // method to convert data to CSV data
    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }

}