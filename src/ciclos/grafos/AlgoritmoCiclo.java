package ciclos.grafos;

import ciclos.grafos.estruturas.In;
import ciclos.grafos.estruturas.Grafo;
import ciclos.grafos.estruturas.Aresta;
import ciclos.grafos.estruturas.Pilha;

/******************************************************************************
 *  Compilation:  javac AlgoritmoCiclo.java
 *  Execution:    java  AlgoritmoCiclo dados.txt
 *  Dependencies: Grafo.java Aresta.java Pilha.java
 *  Identifica um ciclo.
 ******************************************************************************/

public class AlgoritmoCiclo {
    private boolean[] marcado;
    private int[] arestaPara;
    private Pilha<Integer> ciclo;

    /**
     * Determina se o grafo não direcionado G tem um ciclo e, se assim for, encontra um tal ciclo.
     * @param G o grafo não direcionado
     */
    public AlgoritmoCiclo(Grafo G) {
        if (temAutoLoop(G)) return;
        if (temArestasParalelas(G)) return;
        marcado = new boolean[G.V()];
        arestaPara = new int[G.V()];
        for (int v = 0; v < G.V(); v++)
            if (!marcado[v])
                dfs(G, -1, v);
    }

    // este grafo tem um auto loop?
    private boolean temAutoLoop(Grafo G) {
        for (int v = 0; v < G.V(); v++) {
            for (Aresta a : G.adj(v)) {
                int w = a.getV2();
                if (v == w) {
                    ciclo = new Pilha<Integer>();
                    ciclo.empilha(v);
                    ciclo.empilha(v);
                    return true;
                }
            }
        }
        return false;
    }

    // este grafo tem duas arestas paralelas?
    private boolean temArestasParalelas(Grafo G) {
        marcado = new boolean[G.V()];

        for (int v = 0; v < G.V(); v++) {

            // verifica se há aretas paralelas incidentes no vértice v
            for (Aresta a : G.adj(v)) {
                int w = a.getV2();
                if (marcado[w]) {
                    ciclo = new Pilha<Integer>();
                    ciclo.empilha(v);
                    ciclo.empilha(w);
                    ciclo.empilha(v);
                    return true;
                }
                marcado[w] = true;
            }

            // reinicia marcado[v] = false para todos arestas incidentes em v
            for (Aresta a : G.adj(v)) {
                int w = a.getV2();
                marcado[w] = false;
            }
        }
        return false;
    }

    /**
     * Retorna true se o grafo G tem um ciclo.
     * @return true se o grafo tem um ciclo; false, caso contrário
     */
    public boolean temCiclo() {
        return ciclo != null;
    }

     /**
     * Retorna um ciclo no grafo G.
     * @return um ciclo se o grafo G tem um ciclo, e null, caso contrário
     */
    public Iterable<Integer> ciclo() {
        return ciclo;
    }

    private void dfs(Grafo G, int u, int v) {
        marcado[v] = true;
        for (Aresta a : G.adj(v)) {
            int w = a.getV2();
            // finaliza a chamada recursiva se o ciclo tiver sido encontrado
            if (ciclo != null) return;

            if (!marcado[w]) {
                arestaPara[w] = v;
                dfs(G, v, w);
            }

            // verifica a existência de ciclo (mas desconsidera a aresta invertida para v)
            else if (w != u) {
                ciclo = new Pilha<Integer>();
                for (int x = v; x != w; x = arestaPara[x]) {
                    ciclo.empilha(x);
                }
                ciclo.empilha(w);
                ciclo.empilha(v);
            }
        }
    }

    /**
     * Testa a classe AlgoritmoCiclo.
     */
    public static void main(String[] args) {
        // instanciando o Grafo G via arquivo
        In in = new In("C:\\Users\\Iza\\Documents\\NetBeansProjects\\Ciclos-Grafos\\src\\ciclos\\grafos\\grafo_suspeito.txt");
        Grafo G = new Grafo(in);
        System.out.println(G);
        
        /*
        // instanciando o Grafo G via código
        Grafo G = new Grafo(4);
        G.addAresta(new Aresta(0, 1));
        G.addAresta(new Aresta(0, 2));
        G.addAresta(new Aresta(1, 3));
        G.addAresta(new Aresta(2, 3));
        System.out.println(G);
        */
        
        AlgoritmoCiclo finder = new AlgoritmoCiclo(G);
        if (finder.temCiclo()) {
            System.out.print("Ciclo:");
            for (int v : finder.ciclo()) {
                System.out.print(" " + v);
            }
            System.out.println("");
        }
        else {
            System.out.println("Grafo é acíclico");
        }
    }


}

