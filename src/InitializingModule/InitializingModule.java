package InitializingModule;

import InteractionModule.InteractionModule;
import java.io.IOException;

public class InitializingModule {
    // toto by mohla byt ta trieda, ktora si bude drzat referencie na moduly a bude mat staticke gettery, ktore si mozu volat hocijake ine moduly
    public void run(char initSwitch, String inputAddress) throws IOException {    // initSwitch = 'a' / 'b' / 'c' (ani jedno)
        switch (initSwitch){
            case 'a':
                new InteractionModule().runA(inputAddress);
                break;
            case 'b':
                new InteractionModule().runB();
                break;
            case 'c':
                new InteractionModule().run();
                break;
            default:
                System.out.println("Wrong switch");
                new InteractionModule().run();
                break;
        }
    }
    public static void main(String[] args) throws IOException {
        // TODO - prirob moznost, ze bude argument "-help" -> vtedy sa vypisu nejake instrukcie, akymi vsetkymi moznostami sa to da pustit
        if (args.length == 0) {
            new InitializingModule().run('c',null);
            return;
        } else {
            String arg = args[0];
            if (arg.substring(0, 1).equals("-")) {
                arg = arg.substring(1);
            }
            switch (arg) {
                case "a":
                    if (args.length == 1) {
                        // initializing module to posle na interaction module, ten to vyhodnoti ako zlu adresu a vypyta si novu
                        new InitializingModule().run('a',null);
                    }
                    else if (args.length == 2){
                        new InitializingModule().run('a',args[1]);
                    }
                    return;
                case "b":
                    if (args.length > 1) {
                        System.out.println("Variant b selected, any other arguments are ignored");
                    }
                    new InitializingModule().run('b',null);
                    return;
                default:
                    System.out.println("Selected variant is invalid");
                    new InitializingModule().run('c',null);
                    return;
            }
        }
    }
}
