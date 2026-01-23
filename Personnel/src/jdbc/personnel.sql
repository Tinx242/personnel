CREATE DATABASE personnel;

USE personnel;

CREATE TABLE Employe ( 
    num_employe INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    nom VARCHAR(156),
    prenom VARCHAR(156),
    mail VARCHAR(156),
    password VARCHAR(156),
    role ENUM('root','admin','employe'),
    date_arrivee DATE,
    date_depart DATE
);

CREATE TABLE Ligue (
    num_ligue INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    nom VARCHAR(156)
);