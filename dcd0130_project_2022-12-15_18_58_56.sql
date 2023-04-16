-- MySQL dump 10.19  Distrib 10.3.29-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.0.2    Database: dcd0130_project
-- ------------------------------------------------------
-- Server version	10.5.15-MariaDB-0+deb11u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Contact`
--

DROP TABLE IF EXISTS `Contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Contact` (
  `contactId` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(15) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`contactId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Contact`
--

LOCK TABLES `Contact` WRITE;
/*!40000 ALTER TABLE `Contact` DISABLE KEYS */;
INSERT INTO `Contact` VALUES (1,'801-893-8690','info@sbsef.com'),(2,'406-223-0330','larue.seitz@bridgerskifoundation.org'),(3,'303-492-5402','roko@scolorado.edu'),(4,'435-640-8510','darlene.nolting@usskiandsnowboard.org'),(5,'907-783-2160','executivedirector@alyeskaskiclub.com');
/*!40000 ALTER TABLE `Contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Location`
--

DROP TABLE IF EXISTS `Location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Location` (
  `locationId` int(11) NOT NULL AUTO_INCREMENT,
  `state` varchar(2) NOT NULL,
  `resort` varchar(64) NOT NULL,
  `elevation` int(11) NOT NULL,
  PRIMARY KEY (`locationId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Location`
--

LOCK TABLES `Location` WRITE;
/*!40000 ALTER TABLE `Location` DISABLE KEYS */;
INSERT INTO `Location` VALUES (1,'UT','Snowbird',11000),(2,'MT','Bridger',8700),(3,'CO','Eldora',10600),(4,'CO','Loveland',13010),(5,'AL','Alyeska',13040);
/*!40000 ALTER TABLE `Location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Race`
--

DROP TABLE IF EXISTS `Race`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Race` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location_id` int(11) DEFAULT NULL,
  `contact_id` int(11) DEFAULT NULL,
  `sex` enum('F','M') NOT NULL,
  `date` date NOT NULL,
  `discipline` enum('GS','SL') NOT NULL,
  `status` enum('A','C','R') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `location_id` (`location_id`),
  KEY `contact_id` (`contact_id`),
  CONSTRAINT `Race_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `Location` (`locationId`) ON DELETE SET NULL,
  CONSTRAINT `Race_ibfk_2` FOREIGN KEY (`contact_id`) REFERENCES `Contact` (`contactId`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Race`
--

LOCK TABLES `Race` WRITE;
/*!40000 ALTER TABLE `Race` DISABLE KEYS */;
INSERT INTO `Race` VALUES (1,1,1,'F','2023-01-26','SL','A'),(2,1,1,'F','2023-01-27','SL','A'),(3,2,2,'F','2023-01-30','GS','A'),(4,2,2,'F','2023-01-31','GS','A'),(5,3,3,'F','2023-02-09','GS','A'),(6,3,3,'F','2023-02-10','GS','A'),(7,4,4,'F','2023-02-11','SL','A'),(8,4,4,'F','2023-02-12','SL','A'),(9,5,5,'F','2023-02-21','GS','A'),(10,5,5,'F','2023-02-22','GS','A'),(11,5,5,'F','2023-02-23','SL','A'),(12,5,5,'F','2023-02-24','SL','A'),(13,1,1,'M','2023-01-26','SL','A'),(14,1,1,'M','2023-01-27','SL','A'),(15,2,2,'M','2023-01-30','GS','A'),(16,2,2,'M','2023-01-31','GS','A'),(17,3,3,'M','2023-02-09','GS','A'),(18,3,3,'M','2023-02-10','GS','A'),(19,4,4,'M','2023-02-11','SL','A'),(20,4,4,'M','2023-02-12','SL','A'),(21,5,5,'M','2023-02-21','GS','A'),(22,5,5,'M','2023-02-22','GS','A'),(23,5,5,'M','2023-02-23','SL','A'),(24,5,5,'M','2023-02-24','SL','A');
/*!40000 ALTER TABLE `Race` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RaceHasRelocation`
--

DROP TABLE IF EXISTS `RaceHasRelocation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RaceHasRelocation` (
  `raceId` int(11) NOT NULL,
  `locationtId` int(11) NOT NULL,
  PRIMARY KEY (`raceId`,`locationtId`),
  KEY `locationtId` (`locationtId`),
  CONSTRAINT `RaceHasRelocation_ibfk_1` FOREIGN KEY (`raceId`) REFERENCES `Race` (`id`) ON DELETE CASCADE,
  CONSTRAINT `RaceHasRelocation_ibfk_2` FOREIGN KEY (`locationtId`) REFERENCES `Location` (`locationId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RaceHasRelocation`
--

LOCK TABLES `RaceHasRelocation` WRITE;
/*!40000 ALTER TABLE `RaceHasRelocation` DISABLE KEYS */;
/*!40000 ALTER TABLE `RaceHasRelocation` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-16  1:58:56
