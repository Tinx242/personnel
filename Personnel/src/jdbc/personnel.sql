CREATE DATABASE personnel;

USE personnel;

CREATE TABLE Ligue (
    num_ligue INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    nom VARCHAR(156)
);

CREATE TABLE Employe (
    num_employe INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    nom VARCHAR(156),
    prenom VARCHAR(156),
    mail VARCHAR(156),
    password VARCHAR(156),
    role ENUM('root','admin','employe'),
    date_arrivee DATE,
    date_depart DATE,
    num_ligue INT,
    FOREIGN KEY (num_ligue) REFERENCES Ligue(num_ligue)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);