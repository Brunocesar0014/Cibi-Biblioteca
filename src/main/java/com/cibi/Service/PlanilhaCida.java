package com.cibi.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Leitor do layout exportado pelo CIDA. Lê apenas as partes XML de cadastro.
 */
final class PlanilhaCida {
    private static final String PLANILHA = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
    private static final String RELACAO = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
    private static final String PACOTE = "http://schemas.openxmlformats.org/package/2006/relationships";
    private static final int LIMITE_XML = 8 * 1024 * 1024;

    record Linha(int numero, String matricula, String nome, String turma, String erro) {}

    static List<Linha> ler(Path arquivo) throws IOException {
        try (var zip = new ZipFile(arquivo.toFile())) {
            Element workbook = xml(zip, "xl/workbook.xml");
            var abas = workbook.getElementsByTagNameNS(PLANILHA, "sheet");
            Element aba = null;
            for (int i = 0; i < abas.getLength(); i++) {
                var candidata = (Element) abas.item(i);
                String nome = normalizar(candidata.getAttribute("name"));
                if (nome.equals("controle") || nome.equals("refeitorio")) {
                    if (aba != null) throw new IOException("A planilha tem mais de uma aba de cadastro do CIDA.");
                    aba = candidata;
                }
            }
            if (aba == null) throw new IOException("A aba Controle ou Refeitório do CIDA não foi encontrada.");

            Element relacoes = xml(zip, "xl/_rels/workbook.xml.rels");
            String id = aba.getAttributeNS(RELACAO, "id");
            Element dados = xml(zip, parte(relacoes, id, "worksheet"));
            String compartilhadas = parte(relacoes, null, "sharedStrings");
            List<String> textos = compartilhadas == null ? List.of() : textos(xml(zip, compartilhadas));
            return lerLinhas(dados, textos);
        }
    }

    private static List<Linha> lerLinhas(Element dados, List<String> textos) throws IOException {
        var linhas = dados.getElementsByTagNameNS(PLANILHA, "row");
        List<Linha> alunos = new ArrayList<>();
        boolean cabecalho = false;
        for (int i = 0; i < linhas.getLength(); i++) {
            var linha = (Element) linhas.item(i);
            int numero;
            try {
                numero = Integer.parseInt(linha.getAttribute("r"));
            } catch (NumberFormatException erro) {
                throw new IOException("A planilha tem uma linha sem numeração válida.", erro);
            }
            Element[] celulas = new Element[3];
            for (Node no = linha.getFirstChild(); no != null; no = no.getNextSibling()) {
                if (no instanceof Element celula && "c".equals(celula.getLocalName())) {
                    String referencia = celula.getAttribute("r");
                    if (referencia.length() >= 2 && referencia.charAt(0) >= 'A'
                            && referencia.charAt(0) <= 'C' && referencia.substring(1).equals(Integer.toString(numero))) {
                        celulas[referencia.charAt(0) - 'A'] = celula;
                    }
                }
            }
            try {
                String matricula = valor(celulas[0], textos, numero != 1);
                String nome = valor(celulas[1], textos, false);
                String turma = valor(celulas[2], textos, false);
                if (numero == 1) {
                    cabecalho = normalizar(matricula).equals("matricula")
                            && normalizar(nome).equals("aluno") && normalizar(turma).equals("turma");
                } else if (!matricula.isEmpty() || !nome.isEmpty() || !turma.isEmpty()) {
                    alunos.add(new Linha(numero, matricula, nome, turma, null));
                }
            } catch (IllegalArgumentException erro) {
                alunos.add(new Linha(numero, "", "", "", erro.getMessage()));
            }
        }
        if (!cabecalho) throw new IOException("A primeira linha deve conter Matrícula, Aluno e Turma nas colunas A, B e C.");
        if (alunos.isEmpty()) throw new IOException("A planilha não contém alunos.");
        return alunos;
    }

    private static String valor(Element celula, List<String> textos, boolean matricula) {
        if (celula == null) return "";
        if (celula.getElementsByTagNameNS(PLANILHA, "f").getLength() > 0) {
            throw new IllegalArgumentException("Use valores preenchidos diretamente, sem fórmulas.");
        }
        String tipo = celula.getAttribute("t");
        if (tipo.equals("inlineStr")) return concatenarTextos(celula).strip();
        var valores = celula.getElementsByTagNameNS(PLANILHA, "v");
        if (valores.getLength() == 0) return "";
        String valor = valores.item(0).getTextContent().strip();
        if (tipo.equals("s")) {
            try {
                return textos.get(Integer.parseInt(valor)).strip();
            } catch (NumberFormatException | IndexOutOfBoundsException erro) {
                throw new IllegalArgumentException("A célula aponta para um texto inválido.");
            }
        }
        if (matricula && (tipo.isEmpty() || tipo.equals("n"))) {
            try {
                if (valor.length() > 32) throw new ArithmeticException();
                var numero = new BigDecimal(valor).stripTrailingZeros();
                if (numero.signum() <= 0 || numero.scale() > 0
                        || (long) numero.precision() - numero.scale() > 15) throw new ArithmeticException();
                return Long.toString(numero.longValueExact());
            } catch (NumberFormatException | ArithmeticException erro) {
                throw new IllegalArgumentException("Matrícula numérica inválida. Use texto se tiver mais de 15 dígitos.");
            }
        }
        throw new IllegalArgumentException("O nome e a turma devem ser textos e a matrícula deve conter apenas dígitos.");
    }

    private static List<String> textos(Element compartilhadas) {
        var itens = compartilhadas.getElementsByTagNameNS(PLANILHA, "si");
        List<String> textos = new ArrayList<>(itens.getLength());
        for (int i = 0; i < itens.getLength(); i++) textos.add(concatenarTextos((Element) itens.item(i)));
        return textos;
    }

    private static String concatenarTextos(Element elemento) {
        var partes = elemento.getElementsByTagNameNS(PLANILHA, "t");
        var texto = new StringBuilder();
        for (int i = 0; i < partes.getLength(); i++) {
            Node parte = partes.item(i);
            if (!"rPh".equals(parte.getParentNode().getLocalName())) texto.append(parte.getTextContent());
        }
        return texto.toString();
    }

    private static String parte(Element relacoes, String id, String tipo) throws IOException {
        var itens = relacoes.getElementsByTagNameNS(PACOTE, "Relationship");
        for (int i = 0; i < itens.getLength(); i++) {
            var relacao = (Element) itens.item(i);
            if (!relacao.getAttribute("Type").equals(RELACAO + "/" + tipo)
                    || (id != null && !id.equals(relacao.getAttribute("Id")))) continue;
            String destino = relacao.getAttribute("Target");
            if (relacao.getAttribute("TargetMode").equals("External")
                    || !destino.matches("/?([a-zA-Z0-9_-]+/)*[a-zA-Z0-9_-]+\\.xml")) {
                throw new IOException("A planilha contém uma referência interna inválida.");
            }
            return destino.startsWith("/") ? destino.substring(1) : "xl/" + destino;
        }
        if (id == null) return null;
        throw new IOException("O arquivo da aba de cadastro não foi encontrado na planilha.");
    }

    private static Element xml(ZipFile zip, String nome) throws IOException {
        var item = zip.getEntry(nome);
        if (item == null) throw new IOException("O arquivo selecionado não é uma planilha válida do CIDA.");
        try (var entrada = zip.getInputStream(item)) {
            byte[] xml = entrada.readNBytes(LIMITE_XML + 1);
            if (xml.length > LIMITE_XML) throw new IOException("A planilha ultrapassa o tamanho aceito.");
            var fabrica = DocumentBuilderFactory.newDefaultNSInstance();
            fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return fabrica.newDocumentBuilder().parse(new ByteArrayInputStream(xml)).getDocumentElement();
        } catch (ParserConfigurationException | SAXException erro) {
            throw new IOException("O arquivo selecionado tem uma estrutura de planilha inválida.", erro);
        }
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }
}
