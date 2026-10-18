-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: cibi
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `configuracoes`
--

DROP TABLE IF EXISTS `configuracoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracoes` (
  `chave` varchar(80) NOT NULL,
  `valor` varchar(255) NOT NULL,
  `atualizado_em` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`chave`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuracoes`
--

LOCK TABLES `configuracoes` WRITE;
/*!40000 ALTER TABLE `configuracoes` DISABLE KEYS */;
INSERT INTO `configuracoes` VALUES ('aparencia.cor_destaque','#2563EB','2026-09-18 11:17:13'),('aparencia.sidebar_compacta','false','2026-09-16 23:42:23'),('aparencia.tamanho_fonte','MEDIO','2026-09-16 23:42:23'),('aparencia.tema','CLARO','2026-09-18 11:18:06'),('emprestimo.bloquear_atraso','true','2026-09-16 23:42:23'),('emprestimo.cobrar_multa','false','2026-09-16 23:42:23'),('emprestimo.limite_livros','3','2026-09-16 23:42:23'),('emprestimo.multa_dia','0.50','2026-09-16 23:42:23'),('emprestimo.prazo_dias','14','2026-09-16 23:42:23'),('emprestimo.renovacoes','1','2026-09-16 23:42:23'),('notificacao.antecedencia_dias','2','2026-09-16 23:42:23'),('notificacao.atraso','true','2026-09-16 23:42:23'),('notificacao.devolucao_hoje','true','2026-09-16 23:42:23'),('notificacao.sem_exemplar','false','2026-09-16 23:42:23'),('sistema.backup_automatico','false','2026-09-16 23:42:23'),('sistema.itens_pagina','50','2026-09-16 23:42:23'),('sistema.nome_biblioteca','CIBI — Controle Interno de Biblioteca','2026-09-16 23:42:23'),('sistema.pasta_backup','C:\\Users\\Bruno/CIBI/backup','2026-09-16 23:42:23');
/*!40000 ALTER TABLE `configuracoes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-18 14:14:08
