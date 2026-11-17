package com.cibi.Service;

import com.cibi.model.Aluno;
import com.cibi.model.Emprestimo;
import com.cibi.model.Livro;
import com.cibi.repository.LivroTemporarioRepository;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class EmprestimoService {
    private final LivroTemporarioRepository repository = new LivroTemporarioRepository();

    public record Regras(int prazoDias, int limiteLivros, boolean bloquearAtraso) {
        public LocalDate vencimento(LocalDate retirada) {
            return retirada.plusDays(prazoDias);
        }
    }

    public Regras carregarRegras() {
        ConfiguracoesService configuracoes = new ConfiguracoesService();
        return new Regras(configuracoes.getInt(ConfiguracoesService.PRAZO_DIAS),
                configuracoes.getInt(ConfiguracoesService.LIMITE_LIVROS),
                configuracoes.getBoolean(ConfiguracoesService.BLOQUEAR_ATRASO));
    }

    public List<Emprestimo> listar() {
        return repository.listarEmprestimos().reversed();
    }

    public List<Livro> buscarLivrosDisponiveis(String busca) {
        String termo = normalizar(busca.strip());
        String isbn = termo.replaceAll("[\\s-]", "");
        return repository.listar().stream()
                .filter(l -> l.getQuantidadeDisponivel() > 0 && "Disponível".equals(l.getStatus()))
                .filter(l -> normalizar(l.getTitulo()).contains(termo)
                        || normalizar(l.getAutor()).contains(termo)
                        || (!isbn.isEmpty() && normalizar(l.getIsbn()).replace("-", "").contains(isbn)))
                .sorted(Comparator.comparing(Livro::getTitulo, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public void registrar(Aluno aluno, Livro livro, String colaborador, Regras regras) {
        LocalDate hoje = LocalDate.now();
        repository.registrarEmprestimo(aluno, livro.getId(), colaborador, hoje, regras.vencimento(hoje),
                regras.limiteLivros(), regras.bloquearAtraso());
    }

    public void devolver(int id) {
        repository.registrarDevolucao(id, LocalDate.now());
    }

    public static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }
}
