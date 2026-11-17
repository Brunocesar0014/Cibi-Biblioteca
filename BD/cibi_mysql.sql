-- CIBI | MySQL 8.0.41 | instalação em um banco novo
-- Se cibi já existir, use outro nome nas duas instruções abaixo.
-- A seção final contém as instruções para recuperar os dados dos dumps.
SET NAMES utf8mb4;
CREATE DATABASE cibi CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE cibi;

CREATE TABLE alunos (
    matricula BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    turma VARCHAR(12) NOT NULL,
    foto_cida MEDIUMBLOB NULL,
    PRIMARY KEY (matricula),
    CONSTRAINT ck_alunos_matricula CHECK (matricula > 0)
) ENGINE=InnoDB;

CREATE TABLE colaboradores (
    matricula BIGINT NOT NULL,
    nome VARCHAR(50) NOT NULL,
    -- O login atual do Java compara o valor diretamente. Hash exige ajuste no Java.
    senha VARCHAR(255) COLLATE utf8mb4_0900_bin NOT NULL,
    telefone VARCHAR(20) NULL,
    turma VARCHAR(9) NULL,
    perfil ENUM('COLABORADOR', 'GESTORA') NOT NULL DEFAULT 'COLABORADOR',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (matricula),
    UNIQUE KEY uq_colaboradores_nome (nome),
    CONSTRAINT ck_colaboradores_matricula CHECK (matricula > 0),
    CONSTRAINT ck_colaboradores_ativo CHECK (ativo IN (0, 1))
) ENGINE=InnoDB;

CREATE TABLE contas_funcionarios (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    telefone VARCHAR(11),
    usuario VARCHAR(50) NOT NULL UNIQUE,
    senha_hash VARCHAR(80) NOT NULL,
    administrador BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE configuracoes (
    chave VARCHAR(80) NOT NULL,
    valor VARCHAR(255) NOT NULL,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (chave)
) ENGINE=InnoDB;

CREATE TABLE categorias_livro (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) COLLATE utf8mb4_0900_as_ci NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_categorias_livro_nome (nome),
    CONSTRAINT ck_categorias_livro_nome CHECK (CHAR_LENGTH(TRIM(nome)) > 0)
) ENGINE=InnoDB;

-- INT com sinal acompanha o tipo usado pelo modelo e pelo JDBC do CIBI.
CREATE TABLE livros (
    id INT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(100) NOT NULL,
    autor VARCHAR(100) NULL,
    sinopse TEXT NULL,
    subtitulo VARCHAR(255) NULL,
    editora VARCHAR(255) NULL,
    categoria_id INT UNSIGNED NULL,
    isbn VARCHAR(64) NULL,
    ano_publicacao SMALLINT UNSIGNED NULL,
    edicao VARCHAR(100) NULL,
    volume VARCHAR(100) NULL,
    numero_paginas INT UNSIGNED NULL,
    idioma VARCHAR(80) NULL,
    local_publicacao VARCHAR(255) NULL,
    assuntos TEXT NULL,
    numero_chamada VARCHAR(100) NULL,
    localizacao VARCHAR(255) NULL,
    quantidade_total INT NOT NULL DEFAULT 1,
    quantidade_disponivel INT NOT NULL DEFAULT 1,
    status ENUM('Disponível', 'Indisponível', 'Consulta local', 'Em manutenção')
        NOT NULL DEFAULT 'Disponível',
    arquivado_em DATETIME NULL,
    PRIMARY KEY (id),
    KEY ix_livros_titulo (titulo),
    KEY ix_livros_isbn (isbn),
    CONSTRAINT fk_livros_categoria FOREIGN KEY (categoria_id) REFERENCES categorias_livro (id),
    CONSTRAINT ck_livros_titulo CHECK (CHAR_LENGTH(TRIM(titulo)) > 0),
    CONSTRAINT ck_livros_ano CHECK (ano_publicacao BETWEEN 1 AND 9999),
    CONSTRAINT ck_livros_paginas CHECK (numero_paginas > 0),
    CONSTRAINT ck_livros_estoque CHECK (
        quantidade_total > 0 AND quantidade_disponivel BETWEEN 0 AND quantidade_total
    ),
    CONSTRAINT ck_livros_status CHECK (
        (status = 'Disponível' AND quantidade_disponivel > 0)
        OR (status = 'Indisponível' AND quantidade_disponivel = 0)
        OR status IN ('Consulta local', 'Em manutenção')
    )
) ENGINE=InnoDB;

CREATE TABLE emprestimos (
    id INT NOT NULL AUTO_INCREMENT,
    matricula_aluno BIGINT NOT NULL,
    livro_id INT NOT NULL,
    matricula_colaborador BIGINT NOT NULL,
    -- Nomes do momento da retirada, para preservar o histórico após edições.
    aluno_nome VARCHAR(100) NOT NULL,
    livro_titulo VARCHAR(100) NOT NULL,
    livro_autor VARCHAR(100) NULL,
    colaborador_nome VARCHAR(50) NOT NULL,
    retirada DATE NOT NULL,
    devolucao_prevista DATE NOT NULL,
    devolvido_em DATE NULL,
    matricula_colaborador_devolucao BIGINT NULL,
    PRIMARY KEY (id),
    KEY ix_emprestimos_aluno_abertos (matricula_aluno, devolvido_em, devolucao_prevista),
    KEY ix_emprestimos_livro_abertos (livro_id, devolvido_em),
    KEY ix_emprestimos_vencimento (devolvido_em, devolucao_prevista),
    KEY ix_emprestimos_retirada (retirada),
    CONSTRAINT fk_emprestimos_aluno FOREIGN KEY (matricula_aluno) REFERENCES alunos (matricula),
    CONSTRAINT fk_emprestimos_livro FOREIGN KEY (livro_id) REFERENCES livros (id),
    CONSTRAINT fk_emprestimos_colaborador FOREIGN KEY (matricula_colaborador)
        REFERENCES colaboradores (matricula),
    CONSTRAINT fk_emprestimos_colaborador_devolucao FOREIGN KEY (matricula_colaborador_devolucao)
        REFERENCES colaboradores (matricula),
    CONSTRAINT ck_emprestimos_datas CHECK (
        devolucao_prevista >= retirada AND (devolvido_em IS NULL OR devolvido_em >= retirada)
    ),
    CONSTRAINT ck_emprestimos_devolucao CHECK (
        (devolvido_em IS NULL) = (matricula_colaborador_devolucao IS NULL)
    )
) ENGINE=InnoDB;

CREATE TABLE renovacoes_emprestimo (
    id INT NOT NULL AUTO_INCREMENT,
    emprestimo_id INT NOT NULL,
    matricula_colaborador BIGINT NOT NULL,
    renovado_em DATE NOT NULL,
    vencimento_anterior DATE NOT NULL,
    novo_vencimento DATE NOT NULL,
    PRIMARY KEY (id),
    KEY ix_renovacoes_emprestimo (emprestimo_id, id),
    CONSTRAINT fk_renovacoes_emprestimo FOREIGN KEY (emprestimo_id) REFERENCES emprestimos (id),
    CONSTRAINT fk_renovacoes_colaborador FOREIGN KEY (matricula_colaborador)
        REFERENCES colaboradores (matricula),
    CONSTRAINT ck_renovacoes_vencimento CHECK (novo_vencimento > vencimento_anterior)
) ENGINE=InnoDB;

INSERT INTO categorias_livro (nome) VALUES
    ('Literatura brasileira'), ('Literatura estrangeira'), ('Matemática'),
    ('Física'), ('Informática'), ('História'), ('Ciências'), ('Referência'), ('Outros');

-- A pasta de backup é resolvida no computador em que o Java executa.
INSERT INTO configuracoes (chave, valor) VALUES
    ('aparencia.tema', 'ESCURO'),
    ('aparencia.cor_destaque', '#2563EB'),
    ('aparencia.sidebar_compacta', 'false'),
    ('aparencia.tamanho_fonte', 'MEDIO'),
    ('emprestimo.prazo_dias', '14'),
    ('emprestimo.limite_livros', '3'),
    ('emprestimo.renovacoes', '1'),
    ('emprestimo.prazo_renovacao_dias', '30'),
    ('emprestimo.multa_dia', '0.50'),
    ('emprestimo.cobrar_multa', 'false'),
    ('emprestimo.bloquear_atraso', 'true'),
    ('notificacao.devolucao_hoje', 'true'),
    ('notificacao.atraso', 'true'),
    ('notificacao.sem_exemplar', 'false'),
    ('notificacao.antecedencia_dias', '2'),
    ('sistema.nome_biblioteca', 'CIBI — Controle Interno de Biblioteca'),
    ('sistema.itens_pagina', '50'),
    ('sistema.backup_automatico', 'false');

CREATE VIEW vw_acervo AS
SELECT l.*, c.nome AS categoria
FROM livros AS l
LEFT JOIN categorias_livro AS c ON c.id = l.categoria_id
WHERE l.arquivado_em IS NULL;

CREATE VIEW vw_emprestimos AS
SELECT e.*,
    CASE
        WHEN e.devolvido_em IS NOT NULL THEN 'Devolvido'
        WHEN e.devolucao_prevista < CURRENT_DATE THEN 'Atrasado'
        ELSE 'Em andamento'
    END AS status
FROM emprestimos AS e;

-- As rotinas abaixo gerenciam a própria transação. Use CALL em uma conexão
-- sem transação aberta. Grave a circulação por essas rotinas.
DELIMITER $$

CREATE TRIGGER trg_livros_validar_estoque
BEFORE UPDATE ON livros
FOR EACH ROW
BEGIN
    DECLARE v_abertos INT;

    IF NEW.quantidade_total <> OLD.quantidade_total
        OR NEW.quantidade_disponivel <> OLD.quantidade_disponivel
        OR (OLD.arquivado_em IS NULL AND NEW.arquivado_em IS NOT NULL) THEN
        SELECT COUNT(*) INTO v_abertos
        FROM emprestimos
        WHERE livro_id = OLD.id AND devolvido_em IS NULL
        FOR SHARE;

        IF NEW.quantidade_disponivel + v_abertos > NEW.quantidade_total THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'O total deve comportar as unidades disponíveis e emprestadas.';
        END IF;
        IF NEW.arquivado_em IS NOT NULL AND v_abertos > 0 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Registre as devoluções antes de arquivar o livro.';
        END IF;
    END IF;
END$$

CREATE PROCEDURE registrar_emprestimo(
    IN p_matricula_aluno BIGINT,
    IN p_livro_id INT,
    IN p_matricula_colaborador BIGINT,
    IN p_retirada DATE,
    OUT p_emprestimo_id INT
)
BEGIN
    DECLARE v_aluno VARCHAR(100);
    DECLARE v_titulo VARCHAR(100);
    DECLARE v_autor VARCHAR(100);
    DECLARE v_colaborador VARCHAR(50);
    DECLARE v_disponiveis INT;
    DECLARE v_prazo INT;
    DECLARE v_limite INT;
    DECLARE v_bloquear BOOLEAN;
    DECLARE v_abertos INT;
    DECLARE v_atrasados INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET p_emprestimo_id = NULL;
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;
    IF p_retirada IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Informe a data da retirada.';
    END IF;

    -- A linha do aluno serializa as operações que alteram sua circulação.
    SELECT nome INTO v_aluno FROM alunos
    WHERE matricula = p_matricula_aluno FOR UPDATE;
    IF v_aluno IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Aluno não encontrado.';
    END IF;

    SELECT titulo, autor, quantidade_disponivel INTO v_titulo, v_autor, v_disponiveis
    FROM livros
    WHERE id = p_livro_id AND arquivado_em IS NULL AND status = 'Disponível'
    FOR UPDATE;
    IF v_titulo IS NULL OR v_disponiveis < 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Livro indisponível para empréstimo.';
    END IF;

    SELECT nome INTO v_colaborador FROM colaboradores
    WHERE matricula = p_matricula_colaborador AND ativo = TRUE FOR SHARE;
    IF v_colaborador IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Colaborador ativo não encontrado.';
    END IF;

    SELECT MAX(CASE WHEN chave = 'emprestimo.prazo_dias' AND TRIM(valor) REGEXP '^[0-9]{1,2}$'
                    THEN CAST(TRIM(valor) AS UNSIGNED) END),
           MAX(CASE WHEN chave = 'emprestimo.limite_livros' AND TRIM(valor) REGEXP '^[0-9]{1,2}$'
                    THEN CAST(TRIM(valor) AS UNSIGNED) END),
           COALESCE(MAX(CASE WHEN chave = 'emprestimo.bloquear_atraso'
                             THEN LOWER(TRIM(valor)) = 'true' END), TRUE)
    INTO v_prazo, v_limite, v_bloquear
    FROM configuracoes
    WHERE chave IN ('emprestimo.prazo_dias', 'emprestimo.limite_livros', 'emprestimo.bloquear_atraso');

    SET v_prazo = IF(v_prazo BETWEEN 1 AND 90, v_prazo, 14);
    SET v_limite = IF(v_limite BETWEEN 1 AND 20, v_limite, 3);

    SELECT COUNT(*), COUNT(CASE WHEN devolucao_prevista < p_retirada THEN 1 END)
    INTO v_abertos, v_atrasados
    FROM emprestimos
    WHERE matricula_aluno = p_matricula_aluno AND devolvido_em IS NULL;

    IF v_bloquear AND v_atrasados > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O aluno tem devoluções em atraso.';
    END IF;
    IF v_abertos >= v_limite THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O aluno atingiu o limite de livros emprestados.';
    END IF;

    UPDATE livros
    SET status = IF(v_disponiveis = 1, 'Indisponível', 'Disponível'),
        quantidade_disponivel = v_disponiveis - 1
    WHERE id = p_livro_id;

    INSERT INTO emprestimos (
        matricula_aluno, livro_id, matricula_colaborador,
        aluno_nome, livro_titulo, livro_autor, colaborador_nome, retirada, devolucao_prevista
    ) VALUES (
        p_matricula_aluno, p_livro_id, p_matricula_colaborador,
        v_aluno, v_titulo, v_autor, v_colaborador, p_retirada, DATE_ADD(p_retirada, INTERVAL v_prazo DAY)
    );
    SET p_emprestimo_id = LAST_INSERT_ID();
    COMMIT;
END$$

CREATE PROCEDURE registrar_devolucao(
    IN p_emprestimo_id INT,
    IN p_matricula_colaborador BIGINT,
    IN p_devolvido_em DATE
)
BEGIN
    DECLARE v_livro INT;
    DECLARE v_aluno BIGINT;
    DECLARE v_colaborador BIGINT;
    DECLARE v_retirada DATE;
    DECLARE v_devolvido DATE;
    DECLARE v_ultima_renovacao DATE;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;
    SELECT matricula_aluno, livro_id INTO v_aluno, v_livro
    FROM emprestimos WHERE id = p_emprestimo_id;
    IF v_aluno IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empréstimo não encontrado.';
    END IF;

    SELECT matricula INTO v_aluno FROM alunos WHERE matricula = v_aluno FOR UPDATE;
    SELECT id INTO v_livro FROM livros WHERE id = v_livro FOR UPDATE;
    SELECT retirada, devolvido_em INTO v_retirada, v_devolvido
    FROM emprestimos WHERE id = p_emprestimo_id FOR UPDATE;

    IF v_devolvido IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A devolução já foi registrada.';
    END IF;
    SELECT MAX(renovado_em) INTO v_ultima_renovacao
    FROM renovacoes_emprestimo WHERE emprestimo_id = p_emprestimo_id FOR SHARE;
    IF p_devolvido_em IS NULL OR p_devolvido_em < COALESCE(v_ultima_renovacao, v_retirada) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A data da devolução é inválida.';
    END IF;
    SELECT matricula INTO v_colaborador FROM colaboradores
    WHERE matricula = p_matricula_colaborador AND ativo = TRUE FOR SHARE;
    IF v_colaborador IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Colaborador ativo não encontrado.';
    END IF;

    UPDATE emprestimos
    SET devolvido_em = p_devolvido_em, matricula_colaborador_devolucao = p_matricula_colaborador
    WHERE id = p_emprestimo_id;

    UPDATE livros
    SET status = IF(status = 'Indisponível', 'Disponível', status),
        quantidade_disponivel = quantidade_disponivel + 1
    WHERE id = v_livro;
    COMMIT;
END$$

CREATE PROCEDURE registrar_renovacao(
    IN p_emprestimo_id INT,
    IN p_matricula_colaborador BIGINT,
    IN p_renovado_em DATE
)
BEGIN
    DECLARE v_aluno BIGINT;
    DECLARE v_colaborador BIGINT;
    DECLARE v_retirada DATE;
    DECLARE v_devolvido DATE;
    DECLARE v_anterior DATE;
    DECLARE v_novo DATE;
    DECLARE v_ultima_renovacao DATE;
    DECLARE v_usadas INT;
    DECLARE v_limite INT;
    DECLARE v_prazo INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
    START TRANSACTION;
    SELECT matricula_aluno INTO v_aluno FROM emprestimos WHERE id = p_emprestimo_id;
    IF v_aluno IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empréstimo não encontrado.';
    END IF;

    SELECT matricula INTO v_aluno FROM alunos WHERE matricula = v_aluno FOR UPDATE;
    SELECT retirada, devolucao_prevista, devolvido_em INTO v_retirada, v_anterior, v_devolvido
    FROM emprestimos WHERE id = p_emprestimo_id FOR UPDATE;
    IF v_devolvido IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O empréstimo já foi devolvido.';
    END IF;

    SELECT matricula INTO v_colaborador FROM colaboradores
    WHERE matricula = p_matricula_colaborador AND ativo = TRUE FOR SHARE;
    IF v_colaborador IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Colaborador ativo não encontrado.';
    END IF;

    SELECT MAX(CASE WHEN chave = 'emprestimo.renovacoes' AND TRIM(valor) REGEXP '^[0-9]$'
                    THEN CAST(TRIM(valor) AS UNSIGNED) END),
           MAX(CASE WHEN chave = 'emprestimo.prazo_renovacao_dias' AND TRIM(valor) REGEXP '^[0-9]{1,2}$'
                    THEN CAST(TRIM(valor) AS UNSIGNED) END)
    INTO v_limite, v_prazo
    FROM configuracoes
    WHERE chave IN ('emprestimo.renovacoes', 'emprestimo.prazo_renovacao_dias')
    FOR SHARE;
    SET v_limite = IF(v_limite BETWEEN 0 AND 5, v_limite, 1);
    SET v_prazo = IF(v_prazo BETWEEN 1 AND 90, v_prazo, 30);

    SELECT COUNT(*), MAX(renovado_em) INTO v_usadas, v_ultima_renovacao
    FROM renovacoes_emprestimo WHERE emprestimo_id = p_emprestimo_id FOR SHARE;
    IF v_usadas >= v_limite THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O limite de renovações foi atingido.';
    END IF;
    IF p_renovado_em IS NULL OR p_renovado_em < COALESCE(v_ultima_renovacao, v_retirada) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A data da renovação é inválida.';
    END IF;

    SET v_novo = DATE_ADD(v_anterior, INTERVAL v_prazo DAY);
    INSERT INTO renovacoes_emprestimo (
        emprestimo_id, matricula_colaborador, renovado_em, vencimento_anterior, novo_vencimento
    ) VALUES (p_emprestimo_id, p_matricula_colaborador, p_renovado_em, v_anterior, v_novo);
    UPDATE emprestimos SET devolucao_prevista = v_novo WHERE id = p_emprestimo_id;
    COMMIT;
END$$

DELIMITER ;

-- Consultas para conferência
-- SELECT matricula, nome, telefone, turma, perfil, ativo FROM colaboradores;
-- SELECT id, titulo, autor, categoria, quantidade_disponivel, status FROM vw_acervo;
-- SELECT matricula, nome, turma, foto_cida IS NOT NULL AS tem_foto FROM alunos;
-- SELECT chave, valor, atualizado_em FROM configuracoes ORDER BY chave;
-- SELECT * FROM vw_emprestimos ORDER BY retirada DESC, id DESC;

-- Recuperação opcional dos dumps de 12/11/2026
-- 1. Crie cibi_legado e importe nele os quatro dumps pelo MySQL Workbench.
--    CREATE DATABASE cibi_legado CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
--    Se o dump de alunos não tiver foto_cida, aplique migracao_importacao_cida.sql
--    no banco cibi_legado antes de copiar os dados.
-- 2. Abra uma nova aba SQL, selecione o banco novo e execute o bloco abaixo
--    sem os delimitadores do comentário. O destino deve estar recém-criado.
--    As quatro cópias são feitas na mesma transação. Se houver erro, faça
--    ROLLBACK na mesma sessão antes de prosseguir.
/*
START TRANSACTION;

INSERT INTO alunos (matricula, nome, turma, foto_cida)
SELECT matricula, nome, turma, foto_cida FROM cibi_legado.alunos;

INSERT INTO colaboradores (matricula, nome, senha, telefone, turma)
SELECT matricula, nome, senha, telefone, turma FROM cibi_legado.colaboradores;

INSERT INTO livros (id, titulo, autor, sinopse)
SELECT id, titulo, autor, sinopse FROM cibi_legado.livros;

INSERT INTO configuracoes (chave, valor, atualizado_em)
SELECT legado.chave, legado.valor, legado.atualizado_em
FROM cibi_legado.configuracoes AS legado
ON DUPLICATE KEY UPDATE valor = legado.valor, atualizado_em = legado.atualizado_em;

COMMIT;
*/
