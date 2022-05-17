package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {

    public static void processDir(String name, String resultFile){

        try{
            FileChannel fcout = FileChannel.open(Paths.get(resultFile), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            Files.walkFileTree(Paths.get(name), new SimpleFileVisitor<Path>(){
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    FileChannel fcin = FileChannel.open(file);

                    ByteBuffer buf = ByteBuffer.allocateDirect((int)(fcin.size()));

                    fcin.read(buf);
                    buf.flip();
                    Charset input = Charset.forName("Cp1250");
                    CharBuffer charBuffer = input.decode(buf);
                    Charset out = StandardCharsets.UTF_8;
                    fcout.write(out.encode(charBuffer));

                    return FileVisitResult.CONTINUE;
                }
            });

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
