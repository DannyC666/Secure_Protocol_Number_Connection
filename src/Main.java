import javax.crypto.spec.IvParameterSpec;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");
        System.out.println("");
        IvParameterSpec iv = GeneratorIv.generateIv();
        System.out.print("Generated IV (Hex): ");
        for (byte b : iv.getIV()) {
            System.out.format("%02x", b); 
        }
        System.out.println("");
        System.out.print("Generated IV (Decimal): ");
        for (byte b : iv.getIV()) {
            System.out.print((b & 0xFF) + " "); 
        }
        System.out.println(); 
        System.out.println("Hola");
        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
}