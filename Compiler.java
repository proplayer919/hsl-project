import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.fifly.honeycomb.*;


public class Compiler {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: hsl-compiler.jar <filepath>.hsl");
            System.exit(1);
        }
        String filepath = args[0];

        //extract all of the html from the file
        String html = "";
        String jsp = "";
        try {
            html = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
        //if the html contains the tag <%hsl></%hsl> then compile what is between the tags
        // check if the html contains the tag
        Pattern pattern = Pattern.compile("<%hsl>(.*?)</%hsl>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if(matcher.find()) {
            //get the hsl code
            String hsl = matcher.group(1);
            // handle the hsl code
            HoneycombCompiler compiler = new HoneycombCompiler();
            String compiled = compiler.compile(hsl);
            // replace the hsl tags and the content of the tags with "<% <Compiled HSL Here> %>"
            html = matcher.replaceAll("<%= " + compiled + " %>");
            jsp = html;
        }
        // overwrite the file with the string jsp
        try {
            Files.write(Paths.get(filepath), jsp.getBytes());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            System.exit(1);
        }
    }
}