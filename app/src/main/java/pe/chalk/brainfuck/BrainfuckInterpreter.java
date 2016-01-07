package pe.chalk.brainfuck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2016-01-07
 */
public class BrainfuckInterpreter extends Thread{
    public static class Commands {
        public final static String COMMANDS = "><+-.,[]";

        public final static char NEXT = '>';
        public final static char PREVIOUS = '<';
        public final static char PLUS = '+';
        public final static char MINUS = '-';
        public final static char OUTPUT = '.';
        public final static char INPUT = ',';
        public final static char OPEN = '[';
        public final static char CLOSE = ']';
    }

    protected List<Integer> data;
    protected List<Character> program;

    protected int dataPointer = 0;
    protected int programPointer = 0;

    public InputStream in;
    public OutputStream out;
    
    public BrainfuckInterpreter(String program){
        this(program.toCharArray());
    }

    public BrainfuckInterpreter(String program, OutputStream out){
        this(program.toCharArray(), out);
    }

    public BrainfuckInterpreter(String program, InputStream in){
        this(program.toCharArray(), in);
    }

    public BrainfuckInterpreter(String program, OutputStream out, InputStream in){
        this(program.toCharArray(), out, in);
    }

    public BrainfuckInterpreter(char[] program){
        this(program, System.out, System.in);
    }

    public BrainfuckInterpreter(char[] program, OutputStream out){
        this(program, out, System.in);
    }

    public BrainfuckInterpreter(char[] program, InputStream in){
        this(program, System.out, in);
    }

    public BrainfuckInterpreter(char[] program, OutputStream out, InputStream in){
        this.program = new ArrayList<>(program.length);
        for(char command: program) if(Commands.COMMANDS.indexOf(command) > -1) this.program.add(command);

        this.data = new ArrayList<>();
        this.out = out; this.in = in;
    }

    @Override
    public void run(){
        while(programPointer < program.size()){
            for(int i = 0; i <= (dataPointer - data.size()); i++) data.add(0);

            try{
                switch(program.get(programPointer)){
                    case Commands.NEXT:
                        dataPointer++;
                        break;

                    case Commands.PREVIOUS:
                        dataPointer = Math.max(0, dataPointer - 1);
                        break;

                    case Commands.PLUS:
                        data.set(dataPointer, data.get(dataPointer) + 1);
                        break;

                    case Commands.MINUS:
                        data.set(dataPointer, Math.max(0, data.get(dataPointer) - 1));
                        break;

                    case Commands.OUTPUT:
                        out.write(data.get(dataPointer));
                        break;

                    case Commands.INPUT:
                        data.set(dataPointer, in.read());
                        break;

                    case Commands.OPEN:
                        if(data.get(dataPointer) == 0){
                            int level = 1;
                            while(level > 0){
                                char command = program.get(++programPointer);
                                if(command == Commands.OPEN) level++;
                                else if(command == Commands.CLOSE) level--;
                            }
                        }
                        break;

                    case Commands.CLOSE:
                        if(data.get(dataPointer) != 0){
                            int level = 1;
                            while(level > 0){
                                char command = program.get(--programPointer);
                                if(command == Commands.CLOSE) level++;
                                else if(command == Commands.OPEN) level--;
                            }
                            programPointer--;
                        }
                        break;
                }

                programPointer++;
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BrainfuckInterpreter interpreter = new BrainfuckInterpreter(
                "+++++ ++ [>>>>> >>>>>\n" +
                "    +++++ ++ > +++++\n" +
                "<<<<< <<<<< <-] >>>>> >>>>> - > --- <<<<< <<<<< <\n" +
                "\n" +
                "+++++ +++++ [>\n" +
                "    +++++ +++++ + [>\n" +
                "        + > +++ > ++++ > +++++ ++ > +++++ ++++ >\n" +
                "        +++++ +++++ > +++++ +++++ +\n" +
                "    <<<<<<<-]\n" +
                "\n" +
                "    >\n" +
                "        >>>>> >>> .+ >. < <<<<< <<<\n" +
                "        >>> -----. > ++. > --.. +++. <<< . < -.\n" +
                "        >>>>> --. <. +++. ----- -. < -. <<< +. < ++. ---.\n" +
                "    <\n" +
                "    \n" +
                "    [-] > [-] > [-] > [-] > [-] > [-] > [-] > [-] <<<<< <<\n" +
                "<-]");
        interpreter.start();
    }
}
