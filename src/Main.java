/*
    Projeto: Conversor de moedas
    Desenvolvido por Giovanna OR
 */

import com.google.gson.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    private static final String API_KEY = "d2d855a778d4ee0d2ed6503d"; // Chave de acesso ao serviço de conversão de moedas.
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/d2d855a778d4ee0d2ed6503d/latest/USD"; // URL com chave para acesso.

    static Scanner scanner = new Scanner(System.in); // Ler entradas do usuário.
    static Gson gson = new Gson();

    public static void interacao() {
        System.out.println("Seja bem-vindo/a ao Conversor de Moeda");
        System.out.println("1) Dólar =>> Peso argentino");
        System.out.println("2) Peso argentino =>> Dólar");
        System.out.println("3) Dólar =>> Real brasileiro");
        System.out.println("4) Real brasileiro =>> Dólar");
        System.out.println("5) Dólar =>> Peso colombiano");
        System.out.println("6) Peso colombiano =>> Dólar");
        System.out.println("7) Sair");
        System.out.println("Escolha uma opção válida: ");
    } // Primeira interação com o usuário. Escolhendo uma das opções, dá seguimento à conversão.

    private static int lerOpcao() {
        while (!scanner.hasNextInt()) {
            System.out.println("Esta opção não é válida.");
            scanner.next();
        }
        return scanner.nextInt();
    } // Lê a opção escolhida. Sendo falsa, há intervenção.

    public static void main(String[] args) {
        boolean seguir = true;

        while (seguir) {
            interacao();

            int opcao = lerOpcao();

            if (opcao == 7) {
                seguir = false;
                System.out.println("Volte sempre!");
            } else if (opcao >= 1 && opcao <= 6) {
                String[] moedas = valorParaConversao(opcao);
                converterMoeda(moedas[0], moedas[1]);
            } else {
                System.out.println("Esta opção não é válida. Nova tentativa: ");
            }
        }

        scanner.close();
    } // Lê a opção escolhida. Sendo verdadeira, dá seguimento. Falsa, há intervenção.

    private static String[] valorParaConversao(int opcao) {
        return switch (opcao) {
            case 1 -> new String[]{"USD", "ARS"};
            case 2 -> new String[]{"ARS", "USD"};
            case 3 -> new String[]{"USD", "BRL"};
            case 4 -> new String[]{"BRL", "USD"};
            case 5 -> new String[]{"USD", "COP"};
            case 6 -> new String[]{"COP", "USD"};
            default -> throw new IllegalArgumentException("Opção inválida.");
        };
    }

    private static double taxaCambio(String moeda1, String moeda2) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro HTTP: " + response.statusCode());
        }

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject taxas = jsonObject.getAsJsonObject("conversion_rates");

        double taxaMoeda1 = taxas.get(moeda1).getAsDouble();
        double taxaMoeda2 = taxas.get(moeda2).getAsDouble();

        return taxaMoeda2 / taxaMoeda1;
    }

    private static double lerValor() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Escolha um valor válido.");
            scanner.next();
        }
        return scanner.nextDouble();
    }

    private static void converterMoeda(String moeda1, String moeda2) {
        try {
            double taxa = taxaCambio(moeda1, moeda2);

            System.out.print("Digite valor para conversão: ");
            double valor = lerValor();

            double resultado = valor * taxa;
            System.out.printf("%.2f %s = %.2f %s%n", valor, moeda1, resultado, moeda2);
        } catch (Exception e){
            System.out.println("Erro ao converter moeda: " + e.getMessage());
        }
    }
}