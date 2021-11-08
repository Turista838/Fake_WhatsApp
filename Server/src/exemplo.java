import java.util.Scanner;

public class exemplo {

    public static void main(String[] args) {
        System.out.println("exemplo 1 - Servidor");
        Scanner sc = new Scanner(System.in);
        while (!sc.hasNextInt()) sc.next();
    }
}
