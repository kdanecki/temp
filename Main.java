package org.example;

import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        List<Path> files;
        Path source = Path.of("obrazki");
        try (Stream<Path> stream = Files.list(source)) {
            files = stream.collect(Collectors.toList());
            ForkJoinPool threadPool = new ForkJoinPool(8);
            threadPool.submit( () -> files.parallelStream().map((Path p) -> {
                BufferedImage image = null;
                try {
                    image = ImageIO.read(p.toFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String name = String.valueOf(p.getFileName());
                Pair<String, BufferedImage> pair = Pair.of(name, image);
                return pair;
            }).map((Pair<String, BufferedImage> p) -> {
                BufferedImage original = p.getRight();
                BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
                for (int i = 0; i < original.getWidth(); i++) {
                    for (int j = 0; j < original.getHeight(); j++) {
                        int rgb = original.getRGB(i, j);
                        Color c1 = new Color(rgb);
                        Color out;
                        if (i < original.getWidth() / 3)
                            out = new Color(c1.getRed(), 128, 128);
                        else if (i < original.getWidth() * 2 / 3)
                            out = new Color(128, c1.getGreen(), 128);
                        else
                            out = new Color(128, 128, c1.getBlue());
                        image.setRGB(i, j, out.getRGB());
                    }
                }
                return Pair.of(p.getLeft(), image);
            }).forEach((Pair<String, BufferedImage> p) -> {
                File file = new File("modified/" + p.getLeft());
                try {
                    file.createNewFile();
                    ImageIO.write(p.getRight(), "jpg", file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })).get();
        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println(System.currentTimeMillis() - time);
    }
}