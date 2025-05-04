CREATE DATABASE GerenciamentoProducao;

USE GerenciamentoProducao;

CREATE TABLE funcionario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    setor VARCHAR(50) NOT NULL
);

CREATE TABLE maquina (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    setor TEXT
);

CREATE TABLE produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL
);

CREATE TABLE producao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    funcionario_id INT,
    maquina_id INT,
    produto_id INT,
    quantidade INT NOT NULL,
    data_producao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (funcionario_id) REFERENCES funcionario(id),
    FOREIGN KEY (maquina_id) REFERENCES maquina(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);
