package com.accesadades.jdbc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Std {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    //Gestiona la IOexception a RuntimeException para que no sea obligatoria
    public static String readLine() {
        try {
            String line = reader.readLine();
            if (line == null) {
                throw new RuntimeException("Entrada no introducida");
            }
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}