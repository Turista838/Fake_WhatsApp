CREATE DATABASE  IF NOT EXISTS `pd_tp_final` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `pd_tp_final`;
-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pd_tp_final
-- ------------------------------------------------------
-- Server version	8.0.27

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
-- Table structure for table `grupo`
--

DROP TABLE IF EXISTS `grupo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grupo` (
  `ID_Grupo` int NOT NULL AUTO_INCREMENT,
  `Nome` varchar(45) NOT NULL,
  `User_Admin` varchar(45) NOT NULL,
  PRIMARY KEY (`ID_Grupo`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inclui`
--

DROP TABLE IF EXISTS `inclui`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inclui` (
  `Grupo_ID_Grupo` int NOT NULL AUTO_INCREMENT,
  `Utilizador_Username` varchar(45) NOT NULL,
  `Adicionado` tinyint DEFAULT NULL,
  PRIMARY KEY (`Grupo_ID_Grupo`,`Utilizador_Username`),
  KEY `fk_Grupo_has_Utilizador_Utilizador1_idx` (`Utilizador_Username`),
  KEY `fk_Grupo_has_Utilizador_Grupo_idx` (`Grupo_ID_Grupo`),
  CONSTRAINT `fk_Grupo_has_Utilizador_Grupo` FOREIGN KEY (`Grupo_ID_Grupo`) REFERENCES `grupo` (`ID_Grupo`),
  CONSTRAINT `fk_Grupo_has_Utilizador_Utilizador1` FOREIGN KEY (`Utilizador_Username`) REFERENCES `utilizador` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mensagem_de_grupo`
--

DROP TABLE IF EXISTS `mensagem_de_grupo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mensagem_de_grupo` (
  `Visto` tinyint NOT NULL,
  `Ficheiro` tinyint NOT NULL,
  `Data` timestamp NOT NULL,
  `Texto` varchar(512) NOT NULL,
  `Remetente` varchar(45) NOT NULL,
  `Grupo` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mensagem_de_pares`
--

DROP TABLE IF EXISTS `mensagem_de_pares`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mensagem_de_pares` (
  `Visto` tinyint NOT NULL,
  `Ficheiro` tinyint NOT NULL,
  `Data` timestamp NOT NULL,
  `Texto` varchar(512) NOT NULL,
  `Remetente` varchar(45) NOT NULL,
  `Destinatario` varchar(45) NOT NULL,
  KEY `fk_Mensagem de Pares_Utilizador2_idx` (`Destinatario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tem_o_contacto`
--

DROP TABLE IF EXISTS `tem_o_contacto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tem_o_contacto` (
  `Username` varchar(45) NOT NULL,
  `Contacto` varchar(45) NOT NULL,
  `Adicionado` tinyint DEFAULT NULL,
  PRIMARY KEY (`Username`,`Contacto`),
  KEY `fk_Utilizador_has_Utilizador_Utilizador2_idx` (`Contacto`),
  KEY `fk_Utilizador_has_Utilizador_Utilizador1_idx` (`Username`),
  CONSTRAINT `fk_Utilizador_has_Utilizador_Utilizador1` FOREIGN KEY (`Username`) REFERENCES `utilizador` (`Username`),
  CONSTRAINT `fk_Utilizador_has_Utilizador_Utilizador2` FOREIGN KEY (`Contacto`) REFERENCES `utilizador` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `utilizador`
--

DROP TABLE IF EXISTS `utilizador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utilizador` (
  `Username` varchar(45) NOT NULL,
  `Nome` varchar(45) NOT NULL,
  `Password` varchar(45) NOT NULL,
  `Flag_Online` tinyint NOT NULL,
  `TimeStamp_Online` timestamp(6) NOT NULL,
  PRIMARY KEY (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-02-18 11:26:42
