package ciclos.grafos.ui;

import ciclos.grafos.AlgoritmoCiclo;
import ciclos.grafos.estruturas.Grafo;
import ciclos.grafos.estruturas.In;
import ciclos.grafos.estruturas.Aresta;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    public static Grafo G;
    public static AlgoritmoCiclo ciclo;

    private Group root = new Group();
    private Group groupArestas = new Group();
    private Group groupVertices = new Group();

    private Circle[] verticesCirculos;
    private Text[] verticesLabels;

    // >>> NOVO: nomes automáticos "Conta X"
    private Map<Integer, String> contaNomes = new HashMap<>();

    Button botaoExecutar = new Button("Executar Ciclo");

    public static void main(String[] args) {

        In in = new In("C:\\Users\\Iza\\Documents\\NetBeansProjects\\Ciclos-Grafos\\src\\ciclos\\grafos\\grafo_suspeito.txt");
        G = new Grafo(in);

        launch();
    }

    @Override
    public void start(Stage stage) {

        // >>> Gera nomes automáticos de acordo com o número de vértices
        gerarNomesAutomaticos(G.V());

        botaoExecutar.setLayoutX(10);
        botaoExecutar.setLayoutY(10);

        botaoExecutar.setOnAction((ActionEvent e) -> {
            ciclo = new AlgoritmoCiclo(G);
            destacarCiclo();
        });

        root.getChildren().add(botaoExecutar);
        root.getChildren().add(groupArestas);
        root.getChildren().add(groupVertices);

        desenharGrafoCircular();

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Detecção de Ciclos em Grafos");
        stage.show();
    }

    // >>> NOVO: gera nomes tipo "Conta A", "Conta B", "Conta C"...
    private void gerarNomesAutomaticos(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            contaNomes.put(i, "Conta " + (char) ('A' + i));
        }
    }

    private void desenharGrafoCircular() {

        int V = G.V();
        verticesCirculos = new Circle[V];
        verticesLabels = new Text[V];

        double centerX = 450;
        double centerY = 300;
        double raio = 220;

        for (int v = 0; v < V; v++) {

            double ang = 2 * Math.PI * v / V;
            double x = centerX + Math.cos(ang) * raio;
            double y = centerY + Math.sin(ang) * raio;

            Circle c = new Circle(x, y, 22, Color.WHITE);
            c.setStroke(Color.BLACK);

            // >>> troca o número pelo nome "Conta X"
            Text t = new Text(x - 20, y + 4, contaNomes.get(v));

            verticesCirculos[v] = c;
            verticesLabels[v] = t;

            groupVertices.getChildren().add(c);
            groupVertices.getChildren().add(t);
        }

        // desenhar arestas sem duplicação
        boolean[][] desenhadas = new boolean[V][V];

        for (int v = 0; v < V; v++) {
            for (Aresta a : G.adj(v)) {

                int w = (a.getV1() == v) ? a.getV2() : a.getV1();

                if (desenhadas[v][w] || desenhadas[w][v]) {
                    continue;
                }
                desenhadas[v][w] = desenhadas[w][v] = true;

                Line linha = new Line();
                linha.startXProperty().bind(verticesCirculos[v].centerXProperty());
                linha.startYProperty().bind(verticesCirculos[v].centerYProperty());
                linha.endXProperty().bind(verticesCirculos[w].centerXProperty());
                linha.endYProperty().bind(verticesCirculos[w].centerYProperty());
                linha.setStroke(Color.BLACK);

                groupArestas.getChildren().add(linha);

                // >>> NOVO: texto com peso da aresta
                double x1 = verticesCirculos[v].getCenterX();
                double y1 = verticesCirculos[v].getCenterY();
                double x2 = verticesCirculos[w].getCenterX();
                double y2 = verticesCirculos[w].getCenterY();

                double midX = (x1 + x2) / 2;
                double midY = (y1 + y2) / 2;

                Text peso = new Text(midX, midY, String.valueOf(a.peso()));
                peso.setFill(Color.BLUE);
                peso.setStyle("-fx-font-weight: bold;");

                groupArestas.getChildren().add(peso);
            }
        }

    }

    private void destacarCiclo() {

        if (!ciclo.temCiclo()) {
            System.out.println("Grafo é acíclico.");
            return;
        }

        List<Integer> lista = new java.util.ArrayList<>();
        for (int v : ciclo.ciclo()) {
            lista.add(v);
        }

        // >>> aqui imprime os nomes em vez dos números
        System.out.println("Ciclo encontrado:");
        for (int v : lista) {
            System.out.print(contaNomes.get(v) + " ");
        }
        System.out.println();

        // pintar arestas
        for (int i = 0; i < lista.size(); i++) {
            int v1 = lista.get(i);
            int v2 = lista.get((i + 1) % lista.size());

            Line linha = new Line(
                    verticesCirculos[v1].getCenterX(),
                    verticesCirculos[v1].getCenterY(),
                    verticesCirculos[v2].getCenterX(),
                    verticesCirculos[v2].getCenterY()
            );

            linha.setStroke(Color.RED);
            linha.setStrokeWidth(4);

            root.getChildren().add(linha);
        }

        // pintar vértices
        for (int v : lista) {
            verticesCirculos[v].setFill(Color.YELLOW);
        }
    }

}
