package Parsers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import Models.*;
import java.util.List;

public class OutputParser {

    private static final String fn = "output.xyz";

    public static void writeUniverse(List<Particle> particles, List<Particle> borders, double t) {
        try {
            StringBuilder dump = new StringBuilder(particles.size() + 6 + "\n" + "Time=" + t + "\n");
            for (Particle p : borders) {
                dump.append(200).append(" ");
                dump.append(p.getX()).append(" ")
                        .append(p.getY()).append(" ")
                        .append(0).append(" ")
                        .append(p.getRadius()).append("\n");
            }
            for (Particle p : particles) {
                dump.append(200).append(" ");
                dump.append(p.getX()).append(" ")
                        .append(p.getY()).append(" ")
                        .append(0).append(" ")
                        .append(p.getRadius()).append("\n");
            }
            appendToEndOfFile(fn,dump.toString());
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void appendToEndOfFile(String file,String text) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(text);
        bw.close();
    }

    public static void createCleanUniverseFile(String fn) {
        Path fileToDeletePath = Paths.get(fn);
        try {
            Files.deleteIfExists(fileToDeletePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
