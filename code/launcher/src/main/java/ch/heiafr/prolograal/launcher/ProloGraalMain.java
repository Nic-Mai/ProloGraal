package ch.heiafr.prolograal.launcher;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class ProloGraalMain {

   private static final String PROLOGRAAL = "pl";

   /**
    * The main entry point.
    */
   public static void main(String[] args) throws IOException {
      Source source;
      Map<String, String> options = new HashMap<>();
      String file = null;
      for (String arg : args) {
         if (!parseOption(options, arg)) {
            if (file == null) {
               file = arg;
            }
         }
      }

      if (file == null) {
         // @formatter:off
         source = Source.newBuilder(PROLOGRAAL, new InputStreamReader(System.in), "<stdin>").build();
         // @formatter:on
      } else {
         source = Source.newBuilder(PROLOGRAAL, new File(file)).build();
      }

      System.exit(executeSource(source, options));
   }

   private static int executeSource(Source source, Map<String, String> options) {
      Context context;
      PrintStream err = System.err;
      try {
         context = Context.newBuilder(PROLOGRAAL).in(System.in).out(System.out).options(options).build();
      } catch (IllegalArgumentException e) {
         err.println(e.getMessage());
         return 1;
      }

      try {
         context.eval(source);
         return 0;
      } catch (PolyglotException ex) {
         if (ex.isInternalError()) {
            // for internal errors we print the full stack trace
            ex.printStackTrace();
         } else {
            err.println(ex.getMessage());
         }
         return 1;
      } finally {
         context.close();
      }
   }

   private static boolean parseOption(Map<String, String> options, String arg) {
      if (arg.length() <= 2 || !arg.startsWith("--")) {
         return false;
      }
      int eqIdx = arg.indexOf('=');
      String key;
      String value;
      if (eqIdx < 0) {
         key = arg.substring(2);
         value = null;
      } else {
         key = arg.substring(2, eqIdx);
         value = arg.substring(eqIdx + 1);
      }

      if (value == null) {
         value = "true";
      }
      options.put(key, value);
      return true;
   }

}
