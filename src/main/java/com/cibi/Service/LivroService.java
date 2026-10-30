package com.cibi.Service;

import com.cibi.model.Livro;
import com.cibi.repository.LivroTemporarioRepository;
import java.time.Year;
import java.util.List;

public class LivroService {
    public static final List<String> STATUS = List.of("Disponível", "Indisponível", "Consulta local", "Em manutenção");
    private final LivroTemporarioRepository repository = new LivroTemporarioRepository();

    public List<Livro> listarLivros() { return repository.listar(); }

    public void inserirLivro(Livro livro) {
        validar(livro);
        repository.inserir(livro);
    }

    public void atualizarLivro(Livro livro) {
        validar(livro);
        if (livro.getId() <= 0) throw new IllegalArgumentException("ID inválido.");
        repository.atualizar(livro);
    }

    public void excluirLivro(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido.");
        repository.excluir(id);
    }

    public static void validar(Livro livro) {
        if (livro == null) throw new IllegalArgumentException("Informe os dados do livro.");
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Informe o título do livro.");
        }
        if (livro.getTitulo().length() > 100) throw new IllegalArgumentException("O título deve ter até 100 caracteres.");
        if (livro.getAutor() != null && livro.getAutor().length() > 100) {
            throw new IllegalArgumentException("O autor deve ter até 100 caracteres.");
        }
        if (!isbnValido(livro.getIsbn())) throw new IllegalArgumentException("Informe um ISBN-10 ou ISBN-13 válido, ou deixe o campo vazio.");
        if (livro.getAnoPublicacao() != null && (livro.getAnoPublicacao() < 1 || livro.getAnoPublicacao() > Year.now().getValue() + 1)) {
            throw new IllegalArgumentException("Informe um ano de publicação entre 1 e " + (Year.now().getValue() + 1) + ".");
        }
        if (livro.getNumeroPaginas() != null && livro.getNumeroPaginas() < 1) {
            throw new IllegalArgumentException("O número de páginas deve ser maior que zero.");
        }
        if (livro.getQuantidadeTotal() < 1) throw new IllegalArgumentException("A quantidade total deve ser de pelo menos 1 exemplar.");
        if (livro.getQuantidadeDisponivel() < 0 || livro.getQuantidadeDisponivel() > livro.getQuantidadeTotal()) {
            throw new IllegalArgumentException("A quantidade disponível deve estar entre zero e a quantidade total.");
        }
        if (!STATUS.contains(livro.getStatus())) throw new IllegalArgumentException("Selecione o status do livro.");
        if ("Disponível".equals(livro.getStatus()) && livro.getQuantidadeDisponivel() == 0) {
            throw new IllegalArgumentException("Um livro disponível deve ter pelo menos 1 exemplar disponível.");
        }
        if ("Indisponível".equals(livro.getStatus()) && livro.getQuantidadeDisponivel() > 0) {
            throw new IllegalArgumentException("Use quantidade disponível zero para um livro indisponível.");
        }
    }

    public static boolean isbnValido(String valor) {
        if (valor == null || valor.isBlank()) return true;
        String isbn = valor.replaceAll("[\\s-]", "").toUpperCase(java.util.Locale.ROOT);
        int soma = 0;
        if (isbn.matches("[0-9]{9}[0-9X]")) {
            for (int i = 0; i < 10; i++) soma += (10 - i) * (isbn.charAt(i) == 'X' ? 10 : isbn.charAt(i) - '0');
            return soma % 11 == 0;
        }
        if (isbn.matches("97[89][0-9]{10}")) {
            for (int i = 0; i < 13; i++) soma += (isbn.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
            return soma % 10 == 0;
        }
        return false;
    }

    // Mantém os pontos de entrada antigos usando o mesmo acervo provisório.
    public static Livro BuscarLivro(Livro livro) {
        if (livro == null || livro.getId() <= 0) throw new IllegalArgumentException("ID inválido.");
        Livro encontrado = new LivroTemporarioRepository().buscarPorId(livro.getId());
        if (encontrado == null) throw new IllegalStateException("Livro não encontrado.");
        return encontrado;
    }

    public static void InserirLivro(Livro livro) { new LivroService().inserirLivro(livro); }
}
