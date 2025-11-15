package ciclos.grafos.ui;

import ciclos.grafos.AlgoritmoCiclo;
import ciclos.grafos.estruturas.Grafo;
import ciclos.grafos.estruturas.In;
import ciclos.grafos.estruturas.Aresta;
import java.util.List;

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

    Button botaoExecutar = new Button("Executar Ciclo");

    public static void main(String[] args) {

        // lê arquivo (sem coordenadas!)
        In in = new In("C:\\Users\\Iza\\Documents\\NetBeansProjects\\Ciclos-Grafos\\src\\ciclos\\grafos\\grafo_suspeito.txt");
        G = new Grafo(in);

        launch();
    }

    @Override
    public void start(Stage stage) {

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

    /**
     * Desenha os vértices em círculo
     */
    private void desenharGrafoCircular() {

        int V = G.V();
        verticesCirculos = new Circle[V];
        verticesLabels = new Text[V];

        double centerX = 450;
        double centerY = 300;
        double raio = 220;

        // desenhar vértices
        for (int v = 0; v < V; v++) {

            double ang = 2 * Math.PI * v / V;  // ângulo do vértice
            double x = centerX + Math.cos(ang) * raio;
            double y = centerY + Math.sin(ang) * raio;

            Circle c = new Circle(x, y, 22, Color.WHITE);
            c.setStroke(Color.BLACK);

            Text t = new Text(x - 4, y + 4, String.valueOf(v));

            verticesCirculos[v] = c;
            verticesLabels[v] = t;

            groupVertices.getChildren().add(c);
            groupVertices.getChildren().add(t);
        }

        // desenhar arestas
        for (int v = 0; v < G.V(); v++) {
            for (Aresta a : G.adj(v)) {

                // descobrir o outro vértice da aresta
                int w;
                if (a.getV1() == v) {
                    w = a.getV2();
                } else {
                    w = a.getV1();
                }

                if (v < w) { // evita desenhar duplicado
                    Line linha = new Line();
                    linha.startXProperty().bind(verticesCirculos[v].centerXProperty());
                    linha.startYProperty().bind(verticesCirculos[v].centerYProperty());
                    linha.endXProperty().bind(verticesCirculos[w].centerXProperty());
                    linha.endYProperty().bind(verticesCirculos[w].centerYProperty());
                    linha.setStroke(Color.BLACK);

                    groupArestas.getChildren().add(linha);
                }
            }
        }

    }

    /**
     * Destaca o ciclo encontrado
     */
    private void destacarCiclo() {

        if (!ciclo.temCiclo()) {
            System.out.println("Grafo é acíclico.");
            return;
        }

        // converte Iterable para List
        List<Integer> lista = new java.util.ArrayList<>();
        for (int v : ciclo.ciclo()) {
            lista.add(v);
        }

        System.out.println("Ciclo encontrado: " + lista);

        // pintar arestas do ciclo
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

        // pintar vértices do ciclo
        for (int v : lista) {
            verticesCirculos[v].setFill(Color.YELLOW);
        }
    }

}
