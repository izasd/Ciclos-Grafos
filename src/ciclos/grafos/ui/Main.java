package ciclos.grafos.ui;

import ciclos.grafos.AlgoritmoCiclo;
import ciclos.grafos.estruturas.Grafo;
import ciclos.grafos.estruturas.In;
import ciclos.grafos.estruturas.Aresta;

import java.text.NumberFormat;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main com estilo "forense": painel lateral, tooltips, cores por valor,
 * formatação monetária, hover, clique para detalhes e efeito de destaque para
 * ciclo.
 */
public class Main extends Application {

    public static Grafo G;
    public static AlgoritmoCiclo ciclo;

    private Group root = new Group();
    private Group groupArestas = new Group();   // linhas, setas, textos de peso
    private Group groupVertices = new Group();  // círculos e labels (nomes)

    private Circle[] verticesCirculos;
    private Text[] verticesLabels;

    // nomes automáticos "Conta A", "Conta B", ...
    private Map<Integer, String> contaNomes = new HashMap<>();

    // painel lateral (forense)
    private VBox painelLateral;
    private Label lblTitulo;
    private Label lblConta;
    private Label lblGrau;
    private Label lblTotalConexo;
    private Label lblDetalhes;

    Button botaoExecutar = new Button("Executar Ciclo");

    // formato monetário pt-BR
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public static void main(String[] args) {
        In in = new In("C:\\Users\\Iza\\Documents\\NetBeansProjects\\Ciclos-Grafos\\src\\ciclos\\grafos\\grafo_suspeito.txt");
        G = new Grafo(in);
        launch();
    }

    @Override
    public void start(Stage stage) {

        // gera nomes automáticos de acordo com o número de vértices
        gerarNomesAutomaticos(G.V());

        // painel lateral (estilo forense)
        criarPainelLateral();

        // botão estilizado e posicionado
        botaoExecutar.setLayoutX(20);
        botaoExecutar.setLayoutY(20);
        botaoExecutar.setStyle("-fx-background-color: #2b6fa3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;");
        botaoExecutar.setOnAction((ActionEvent e) -> {
            ciclo = new AlgoritmoCiclo(G);
            destacarCiclo();
        });

        // fundo neutro e retângulo do painel lateral
        Rectangle fundo = new Rectangle(0, 0, 900, 600);
        fundo.setFill(Color.web("#0f1720")); // muito escuro para o estilo forense
        Rectangle painelBg = new Rectangle(680, 0, 220, 600);
        painelBg.setFill(Color.web("#0b1220")); // tom ligeiramente diferente
        painelBg.setStroke(Color.web("#1f2a36"));
        painelBg.setStrokeWidth(1);

        root.getChildren().addAll(fundo, painelBg, botaoExecutar, groupArestas, groupVertices);

        // adiciona componentes do painel lateral
        painelLateral.setLayoutX(690);
        painelLateral.setLayoutY(20);
        root.getChildren().add(painelLateral);

        desenharGrafoCircular();

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Detecção de Ciclos em Grafos - Estilo Forense");
        stage.show();
    }

    private void criarPainelLateral() {
        painelLateral = new VBox(8);
        painelLateral.setPadding(new Insets(8));
        lblTitulo = new Label("Painel Forense");
        lblTitulo.setTextFill(Color.web("#9fb8d9"));
        lblTitulo.setFont(Font.font("Consolas", 16));
        lblConta = new Label("Conta: —");
        lblConta.setTextFill(Color.web("#cfe8ff"));
        lblGrau = new Label("Grau: —");
        lblGrau.setTextFill(Color.web("#cfe8ff"));
        lblTotalConexo = new Label("Total transações (conexo): —");
        lblTotalConexo.setTextFill(Color.web("#cfe8ff"));
        lblDetalhes = new Label("Detalhes:\n(Selecione um nó)");
        lblDetalhes.setTextFill(Color.web("#cfe8ff"));
        lblDetalhes.setWrapText(true);

        painelLateral.getChildren().addAll(lblTitulo, lblConta, lblGrau, lblTotalConexo, lblDetalhes);
        painelLateral.setPrefWidth(200);
    }

    // gera "Conta A", "Conta B", ...
    private void gerarNomesAutomaticos(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            contaNomes.put(i, "Conta " + (char) ('A' + i));
        }
    }

    private void desenharGrafoCircular() {

        int V = G.V();
        verticesCirculos = new Circle[V];
        verticesLabels = new Text[V];

        double centerX = 300; // reduzido para abrir espaço do painel
        double centerY = 300;
        double raio = 200;

        // desenhar vértices e labels
        for (int v = 0; v < V; v++) {

            double ang = 2 * Math.PI * v / V;
            double x = centerX + Math.cos(ang) * raio;
            double y = centerY + Math.sin(ang) * raio;

            Circle c = new Circle(x, y, 22, Color.web("#0b1620")); // fundo escuro no nó
            c.setStroke(Color.web("#2a9fd6"));
            c.setStrokeWidth(2);

            // nome no lugar do número (estilo mono, técnico)
            Text t = new Text(x - 20, y + 6, contaNomes.get(v));
            t.setFill(Color.web("#cfe8ff"));
            t.setFont(Font.font("Consolas", 12));

            // hover effect
            final int idx = v;
            c.setOnMouseEntered(evt -> {
                c.setFill(Color.web("#083b58"));
                c.setScaleX(1.05);
                c.setScaleY(1.05);
            });
            c.setOnMouseExited(evt -> {
                // se faz parte do ciclo e já destacado, manter cor; aqui simplificamos e volta ao normal
                c.setFill(Color.web("#0b1620"));
                c.setScaleX(1.0);
                c.setScaleY(1.0);
            });

            // click mostra detalhes no painel lateral
            c.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                mostrarDetalhesNoPainel(idx);
            });

            // tooltip com resumo
            Tooltip.install(c, new Tooltip(contaNomes.get(v)));

            verticesCirculos[v] = c;
            verticesLabels[v] = t;

            groupVertices.getChildren().addAll(c, t);
        }

        // desenhar arestas (uma vez cada)
        boolean[][] desenhadas = new boolean[V][V];

        for (int v = 0; v < V; v++) {
            for (Aresta a : G.adj(v)) {

                int w = (a.getV1() == v) ? a.getV2() : a.getV1();

                if (desenhadas[v][w] || desenhadas[w][v]) {
                    continue;
                }
                desenhadas[v][w] = desenhadas[w][v] = true;

                Circle cOrig = verticesCirculos[v];
                Circle cDest = verticesCirculos[w];

                // cria a linha que liga os dois círculos e a vincula às propriedades dos centros
                Line linha = new Line();
                linha.startXProperty().bind(cOrig.centerXProperty());
                linha.startYProperty().bind(cOrig.centerYProperty());
                linha.endXProperty().bind(cDest.centerXProperty());
                linha.endYProperty().bind(cDest.centerYProperty());

                // cor e espessura por faixa de valor (visual forense)
                double valor = a.peso();
                if (valor < 300) {
                    linha.setStroke(Color.web("#5f8c6a")); // verde discreto
                    linha.setStrokeWidth(1.2);
                } else if (valor < 800) {
                    linha.setStroke(Color.web("#e6b23c")); // laranja técnico
                    linha.setStrokeWidth(2.0);
                } else {
                    linha.setStroke(Color.web("#c84b4b")); // vermelho "alarme"
                    linha.setStrokeWidth(2.8);
                }

                // efeito sutil
                linha.setOpacity(0.95);

                // tooltip para a aresta com valor formatado
                Tooltip.install(linha, new Tooltip("Valor: " + nf.format(a.peso())));

                groupArestas.getChildren().add(linha);

                // texto do peso (posicionado no meio) com deslocamento perpendicular
                Text pesoText = new Text();
                pesoText.setText(nf.format(a.peso()));
                pesoText.setFill(Color.web("#8ec5ff"));
                pesoText.setFont(Font.font("Consolas", 12));
                groupArestas.getChildren().add(pesoText);

                // polygon (small triangle) para sentido visual leve (não verdadeiro direção)
                Polygon arrow = new Polygon();
                arrow.setFill(Color.web("#9fb8d9"));
                groupArestas.getChildren().add(arrow);

// criar versões finais das variáveis usadas no listener
                final int vFinal = v;
                final int wFinal = w;
                final Line linhaFinal = linha;
                final Polygon arrowFinal = arrow;
                final Text pesoTextFinal = pesoText;
                final Circle cOrigFinal = cOrig;
                final Circle cDestFinal = cDest;

// listener que recalcula a posição do texto e do triângulo sempre que círculos se movem
                ChangeListener<Number> atualiza = new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV) {
                        updateEdgeGraphicsForense(vFinal, wFinal, linhaFinal, arrowFinal, pesoTextFinal, cOrigFinal, cDestFinal);
                    }
                };

                cOrig.centerXProperty().addListener(atualiza);
                cOrig.centerYProperty().addListener(atualiza);
                cDest.centerXProperty().addListener(atualiza);
                cDest.centerYProperty().addListener(atualiza);

                // inicializa posicoes
                updateEdgeGraphicsForense(vFinal, wFinal, linhaFinal, arrowFinal, pesoTextFinal, cOrigFinal, cDestFinal);

            }
        }
    }

    /**
     * Atualiza posição do texto do peso e do pequeno triângulo indicador
     * (apenas estético)
     */
    private void updateEdgeGraphicsForense(int origem, int destino,
            Line linha, Polygon arrow, Text pesoText,
            Circle cOrig, Circle cDest) {

        double x1 = cOrig.getCenterX();
        double y1 = cOrig.getCenterY();
        double x2 = cDest.getCenterX();
        double y2 = cDest.getCenterY();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) {
            return;
        }

        double ux = dx / dist;
        double uy = dy / dist;

        // ponto médio
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;

        // deslocamento perpendicular pequeno para não colidir com a linha
        double perpOffset = 10;
        double px = -uy * perpOffset;
        double py = ux * perpOffset;

        pesoText.setX(midX + px - pesoText.getLayoutBounds().getWidth() / 2);
        pesoText.setY(midY + py + pesoText.getLayoutBounds().getHeight() / 4);

        // triângulo decorativo posicionado um pouco antes do meio (apenas para visual técnico)
        double triBack = 14; // quão próximo do meio ele fica
        double tipX = midX - ux * triBack;
        double tipY = midY - uy * triBack;

        double tamanho = 6;
        double leftX = tipX - uy * tamanho;
        double leftY = tipY + ux * tamanho;
        double rightX = tipX + uy * tamanho;
        double rightY = tipY - ux * tamanho;

        arrow.getPoints().setAll(
                tipX, tipY,
                leftX, leftY,
                rightX, rightY
        );
    }

    /**
     * Mostra no painel lateral um resumo da conta v: grau e soma dos pesos
     * incidentes. Como grafo é não-direcionado na estrutura, mostramos soma
     * total "conexa".
     */
    private void mostrarDetalhesNoPainel(int v) {
        int grau = G.grau(v);
        double soma = 0;
        StringBuilder neighbors = new StringBuilder();

        for (Aresta a : G.adj(v)) {
            soma += a.peso();
            int outro = a.outroVertice(v);
            neighbors.append(contaNomes.get(outro))
                    .append(" (")
                    .append(nf.format(a.peso()))
                    .append(")\n");
        }

        lblConta.setText("Conta: " + contaNomes.get(v));
        lblGrau.setText("Grau: " + grau);
        lblTotalConexo.setText("Total (conexo): " + nf.format(soma));
        lblDetalhes.setText("Vizinhos:\n" + neighbors.toString());
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

        // imprime nomes em vez dos números (console)
        System.out.println("Ciclo encontrado:");
        for (int v : lista) {
            System.out.print(contaNomes.get(v) + " ");
        }
        System.out.println();

        // aplicar efeito de fade nas arestas do ciclo; desenhamos linhas vermelhas por cima
        for (int i = 0; i < lista.size(); i++) {
            int v1 = lista.get(i);
            int v2 = lista.get((i + 1) % lista.size());

            Line linha = new Line(
                    verticesCirculos[v1].getCenterX(),
                    verticesCirculos[v1].getCenterY(),
                    verticesCirculos[v2].getCenterX(),
                    verticesCirculos[v2].getCenterY()
            );

            linha.setStroke(Color.web("#ff3333"));
            linha.setStrokeWidth(4);
            linha.setOpacity(0.95);

            // animação piscante para chamar atenção
            FadeTransition ft = new FadeTransition(Duration.seconds(0.7), linha);
            ft.setFromValue(1.0);
            ft.setToValue(0.25);
            ft.setCycleCount(6);
            ft.setAutoReverse(true);
            ft.play();

            root.getChildren().add(linha);
        }

        // pintar vértices do ciclo com sombra vermelha
        DropShadow ds = new DropShadow();
        ds.setOffsetX(0);
        ds.setOffsetY(0);
        ds.setRadius(12);
        ds.setColor(Color.web("#6b1f1f"));

        for (int v : lista) {
            verticesCirculos[v].setFill(Color.web("#3b2e2e"));
            verticesCirculos[v].setEffect(ds);
        }
    }
}
