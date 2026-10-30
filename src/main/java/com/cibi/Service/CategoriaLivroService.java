package com.cibi.Service;

import com.cibi.model.Livro;
import com.cibi.repository.LivroTemporarioRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

/** Categorias compartilhadas somente durante a execução do aplicativo. */
public final class CategoriaLivroService {
    private static final List<String> CATEGORIAS = new ArrayList<>(List.of(
            "Literatura brasileira", "Literatura estrangeira", "Matemática",
            "Física", "Informática", "História", "Ciências", "Referência", "Outros"));
    private final LivroTemporarioRepository livros = new LivroTemporarioRepository();

    public List<String> listar() {
        synchronized (CATEGORIAS) {
            TreeSet<String> nomes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            nomes.addAll(CATEGORIAS);
            livros.listar().stream().map(Livro::getCategoria).filter(Objects::nonNull)
                    .map(String::strip).filter(nome -> !nome.isBlank()).forEach(nomes::add);
            return List.copyOf(nomes);
        }
    }

    public Edicao iniciarEdicao() { return new Edicao(listar()); }

    public record Item(String original, String nome) {
        @Override public String toString() { return nome; }
    }

    public record Resultado(Map<String, String> nomesAtualizados, String selecionada) {
        public String atualizarNome(String nome) {
            if (nome == null) return "";
            return nomesAtualizados.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(nome.strip()))
                    .map(Map.Entry::getValue).findFirst().orElse(nome);
        }
    }

    /** Cópia de trabalho do modal. Nenhum comando altera o acervo antes de salvar. */
    public final class Edicao {
        private final List<String> originais;
        private final List<Item> itens = new ArrayList<>();

        private Edicao(List<String> nomes) {
            originais = nomes;
            nomes.forEach(nome -> itens.add(new Item(nome, nome)));
        }

        public List<Item> listar() { return List.copyOf(itens); }

        public Item adicionar(String nome) {
            Item item = new Item(null, validarNome(nome, null));
            itens.add(item);
            return item;
        }

        public Item renomear(Item item, String nome) {
            int indice = itens.indexOf(item);
            if (indice < 0) throw new IllegalArgumentException("Selecione uma categoria para renomear.");
            Item atualizado = new Item(item.original(), validarNome(nome, item));
            itens.set(indice, atualizado);
            return atualizado;
        }

        public void excluir(Item item) {
            if (item == null || !itens.contains(item)) throw new IllegalArgumentException("Selecione uma categoria para excluir.");
            if (emUso(item)) throw new IllegalArgumentException("Esta categoria possui livros vinculados e não pode ser excluída. Você pode renomeá-la.");
            itens.remove(item);
        }

        public boolean emUso(Item item) {
            return item != null && item.original() != null && livros.listar().stream()
                    .anyMatch(l -> l.getCategoria() != null && l.getCategoria().strip().equalsIgnoreCase(item.original()));
        }

        private String validarNome(String nome, Item ignorar) {
            String valor = nome == null ? "" : nome.strip();
            if (valor.isBlank()) throw new IllegalArgumentException("Informe o nome da categoria.");
            if (valor.length() > 100) throw new IllegalArgumentException("O nome da categoria deve ter até 100 caracteres.");
            if (itens.stream().anyMatch(i -> i != ignorar && i.nome().equalsIgnoreCase(valor))) {
                throw new IllegalArgumentException("Já existe uma categoria com esse nome.");
            }
            return valor;
        }

        public Resultado salvar(Item selecionada) {
            synchronized (CATEGORIAS) {
                if (!originais.equals(CategoriaLivroService.this.listar())) {
                    throw new IllegalStateException("As categorias mudaram. Feche e abra o gerenciamento novamente.");
                }
                Map<String, String> novosNomes = new LinkedHashMap<>();
                for (String original : originais) {
                    String novo = itens.stream().filter(i -> original.equals(i.original()))
                            .map(Item::nome).findFirst().orElse("");
                    novosNomes.put(original, novo);
                }
                List<Livro> atualizacoes = new ArrayList<>();
                for (Livro livro : livros.listar()) {
                    if (livro.getCategoria() == null) continue;
                    String novo = novosNomes.entrySet().stream()
                            .filter(e -> e.getKey().equalsIgnoreCase(livro.getCategoria().strip()))
                            .map(Map.Entry::getValue).findFirst().orElse(livro.getCategoria());
                    if (novo.isEmpty() && !livro.getCategoria().isBlank()) {
                        throw new IllegalArgumentException("Não é possível excluir uma categoria com livros vinculados.");
                    }
                    if (!novo.equals(livro.getCategoria())) {
                        livro.setCategoria(novo);
                        atualizacoes.add(livro);
                    }
                }
                atualizacoes.forEach(livros::atualizar);
                CATEGORIAS.clear();
                itens.forEach(i -> CATEGORIAS.add(i.nome()));
                return new Resultado(Map.copyOf(novosNomes),
                        selecionada != null && itens.contains(selecionada) ? selecionada.nome() : "");
            }
        }
    }
}
