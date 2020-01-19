package uj.java.pwj2019.battleships;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ClientOld implements Runnable {
    public static void main(String[] args) throws IOException, InterruptedException {
        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_2)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://192.168.121.173:8080"))
                .setHeader("User-Agent", "PWJ Example HTTP client v 1.0")
                .build();

        HttpResponse<String> response
                = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        printResponse(response);
    }

    @Override
    public void run() {
        boolean endOfGame=false;
        while(!endOfGame){

        }
    }

    private void requestServer() throws IOException, InterruptedException {
        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_2)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://192.168.121.173:8080"))
                .setHeader("User-Agent", "PWJ Example HTTP client v 1.0")
                .build();

        HttpResponse<String> response
                = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        printResponse(response);
    }

    private static void printResponse(HttpResponse<String> response) {
        System.out.println("RESPONSE CODE: " + response.statusCode());
        HttpHeaders headers = response.headers();
        System.out.println("START OF HEADERS");
        headers.map().forEach(ClientOld::printHeader);
        System.out.println("END OF HEADERS");
        System.out.println("START OF BODY");
        System.out.println(response.body());
        System.out.println("END OF BODY");
    }

    private static void printHeader(String key, List<String> values) {
        System.out.print(key + ": ");
        switch (values.size()) {
            case 0:
                System.out.println("<EMPTY>");
                break;
            case 1:
                System.out.println(values.get(0));
                break;
            default:
                System.out.println(values);
        }
    }

}
