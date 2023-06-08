-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: fdb31.125mb.com
-- Tiempo de generación: 08-06-2023 a las 20:47:30
-- Versión del servidor: 5.7.40-log
-- Versión de PHP: 8.1.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `4110240_revolico`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `add_me`
--

CREATE TABLE `add_me` (
  `id_contacto` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `country` varchar(5) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `instagram` varchar(100) NOT NULL,
  `facebook` varchar(100) NOT NULL,
  `password` varchar(300) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `add_me`
--

INSERT INTO `add_me` (`id_contacto`, `name`, `country`, `phone`, `instagram`, `facebook`, `password`) VALUES
(6, 'Esteban', '53', '5359241479', 'estebanjr.dev', 'estebanjr.dev', '$2y$10$Y8kj0ywlfpDJBd0EFIieQ.t3OtF2m0.HPESO2Tc4jThIEG1yRh/CG');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `add_me`
--
ALTER TABLE `add_me`
  ADD PRIMARY KEY (`id_contacto`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `add_me`
--
ALTER TABLE `add_me`
  MODIFY `id_contacto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
