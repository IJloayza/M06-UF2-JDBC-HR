CREATE DATABASE IF NOT EXISTS RENFE;

USE RENFE;

CREATE TABLE IF NOT EXISTS Estacio (
    id INT PRIMARY KEY,
    Nombre VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS Horari (
    id INT PRIMARY KEY,
    hora_Salida TIME NOT NULL,
    hora_Llegada TIME NOT NULL,
    fecha DATE NOT NULL
);
CREATE TABLE IF NOT EXISTS Companyia (
    id INT PRIMARY KEY,
    Nombre VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS Tren (
    id INT PRIMARY KEY,
    Nombre VARCHAR(255) NOT NULL,
    Capacitat INT NOT NULL
);
CREATE TABLE IF NOT EXISTS Trajecte (
    id INT PRIMARY KEY,
    id_EstOrigen INT,
    id_EsDestino INT,
    id_Horari INT,
    FOREIGN KEY (id_EstOrigen) REFERENCES Estacio(id),
    FOREIGN KEY (id_EsDestino) REFERENCES Estacio(id),
    FOREIGN KEY (id_Horari) REFERENCES Horari(id)
);
CREATE TABLE IF NOT EXISTS Recorregut (
    id INT PRIMARY KEY,
    id_Trajecte INT,
    id_Companyia INT,
    id_Tren INT,
    FOREIGN KEY (id_Trajecte) REFERENCES Trajecte(id),
    FOREIGN KEY (id_Companyia) REFERENCES Companyia(id),
    FOREIGN KEY (id_Tren) REFERENCES Tren(id)
);
CREATE TABLE IF NOT EXISTS Tenir (
    idEstacio INT,
    idTrajecte INT,
    PRIMARY KEY (idEstacio, idTrajecte),
    FOREIGN KEY (idEstacio) REFERENCES Estacio(id),
    FOREIGN KEY (idTrajecte) REFERENCES Trajecte(id)
);