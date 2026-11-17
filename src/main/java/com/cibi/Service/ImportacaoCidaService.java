package com.cibi.Service;

import com.cibi.repository.AlunoRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public final class ImportacaoCidaService {
    private static final Pattern JPG = Pattern.compile(".+\\.jpe?g", Pattern.CASE_INSENSITIVE);
    private static final Pattern FOTO_CIDA = Pattern.compile(".+_([0-9]{1,19})_[^_]+\\.jpe?g", Pattern.CASE_INSENSITIVE);
    private static final int MAX_BYTES_FOTO = 5 * 1024 * 1024;
    private static final int MAX_BYTES_LOTE = 64 * 1024 * 1024;
    private static final int MAX_LADO_MINIATURA = 320;

    public record Registro(int linha, long matricula, String nome, String turma, byte[] foto, String erro) {
        public String status() {
            if (erro != null) return erro;
            return foto == null ? "Sem foto no lote" : "Foto pronta";
        }
    }

    public record Previa(List<Registro> registros, int fotosIgnoradas) {
        public Previa {
            registros = List.copyOf(registros);
        }

        public long erros() {
            return registros.stream().filter(registro -> registro.erro() != null).count();
        }

        public long fotos() {
            return registros.stream().filter(registro -> registro.foto() != null).count();
        }

        public boolean pronta() {
            return !registros.isEmpty() && erros() == 0;
        }
    }

    public Previa validar(Path planilha, Path pastaFotos) throws IOException {
        var linhas = PlanilhaCida.ler(planilha);
        Map<Long, List<Path>> fotos = new HashMap<>();
        int totalFotos = indexarFotos(pastaFotos, fotos);
        int fotosVinculadas = 0;
        int tamanhoLote = 0;
        Set<Long> matriculas = new HashSet<>();
        List<Registro> registros = new ArrayList<>(linhas.size());
        for (var linha : linhas) {
            long matricula = lerMatricula(linha.matricula());
            String erro = validarLinha(linha, matricula, matriculas);
            byte[] foto = null;
            List<Path> arquivos = fotos.getOrDefault(matricula, List.of());
            if (erro == null && arquivos.size() > 1) {
                erro = "Mais de uma foto para a matrícula.";
            } else if (erro == null && arquivos.size() == 1) {
                try {
                    foto = miniatura(arquivos.getFirst());
                    fotosVinculadas++;
                    tamanhoLote += foto.length;
                } catch (IOException | RuntimeException falha) {
                    erro = "Foto inválida ou acima do limite de 5 MB ou 16 megapixels.";
                }
            }
            if (tamanhoLote > MAX_BYTES_LOTE) {
                throw new IOException("O lote de fotos é muito grande. Divida a importação em lotes menores.");
            }
            registros.add(new Registro(linha.numero(), matricula, linha.nome(), linha.turma(), foto, erro));
        }
        return new Previa(registros, totalFotos - fotosVinculadas);
    }

    public void importar(Previa previa) {
        if (!previa.pronta()) throw new IllegalArgumentException("Valide um lote sem erros antes de importar.");
        var alunos = previa.registros().stream()
                .map(registro -> new AlunoRepository.CadastroCida(
                        registro.matricula(), registro.nome(), registro.turma(), registro.foto()))
                .toList();
        AlunoRepository.importarCida(alunos);
    }

    private static int indexarFotos(Path pasta, Map<Long, List<Path>> fotos) throws IOException {
        if (pasta == null) return 0;
        int total = 0;
        try (var arquivos = Files.newDirectoryStream(pasta)) {
            for (Path arquivo : arquivos) {
                String nome = arquivo.getFileName().toString();
                if (!JPG.matcher(nome).matches() || !Files.isRegularFile(arquivo, NOFOLLOW_LINKS)) continue;
                total++;
                var correspondencia = FOTO_CIDA.matcher(nome);
                if (!correspondencia.matches()) continue;
                long matricula = lerMatricula(correspondencia.group(1));
                if (matricula > 0) fotos.computeIfAbsent(matricula, chave -> new ArrayList<>()).add(arquivo);
            }
        }
        return total;
    }

    private static String validarLinha(PlanilhaCida.Linha linha, long matricula, Set<Long> matriculas) {
        if (linha.erro() != null) return linha.erro();
        if (matricula <= 0) return "Matrícula inválida.";
        if (!matriculas.add(matricula)) return "Matrícula repetida na planilha.";
        if (linha.nome().isBlank() || linha.nome().codePointCount(0, linha.nome().length()) > 100) {
            return "Informe um nome com até 100 caracteres.";
        }
        if (linha.turma().isBlank() || linha.turma().codePointCount(0, linha.turma().length()) > 12) {
            return "Informe uma turma com até 12 caracteres.";
        }
        return null;
    }

    private static long lerMatricula(String valor) {
        if (!valor.matches("[0-9]{1,19}")) return 0;
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException erro) {
            return 0;
        }
    }

    private static byte[] miniatura(Path arquivo) throws IOException {
        byte[] jpeg;
        try (var entrada = Files.newInputStream(arquivo, NOFOLLOW_LINKS)) {
            jpeg = entrada.readNBytes(MAX_BYTES_FOTO + 1);
        }
        if (jpeg.length < 3 || jpeg.length > MAX_BYTES_FOTO
                || jpeg[0] != (byte) 0xff || jpeg[1] != (byte) 0xd8 || jpeg[2] != (byte) 0xff) {
            throw new IOException("JPEG inválido.");
        }
        var leitor = ImageIO.getImageReadersByFormatName("JPEG").next();
        try (var entrada = new MemoryCacheImageInputStream(new ByteArrayInputStream(jpeg))) {
            leitor.setInput(entrada, true, true);
            int largura = leitor.getWidth(0);
            int altura = leitor.getHeight(0);
            if ((long) largura * altura > 16_000_000) throw new IOException("Imagem grande demais.");
            int passo = Math.ceilDiv(Math.max(largura, altura), MAX_LADO_MINIATURA);
            var parametros = leitor.getDefaultReadParam();
            parametros.setSourceSubsampling(passo, passo, 0, 0);
            var imagem = leitor.read(0, parametros);
            var saida = new ByteArrayOutputStream();
            try (var fluxo = new MemoryCacheImageOutputStream(saida)) {
                if (!ImageIO.write(imagem, "JPEG", fluxo)) {
                    throw new IOException("Não foi possível gerar a miniatura.");
                }
            }
            return saida.toByteArray();
        } finally {
            leitor.dispose();
        }
    }
}
