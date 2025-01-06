create database farmacia;
use farmacia;

create table cargo(
	id int primary key auto_increment not null,
    nome varchar(50) not null COLLATE utf8mb4_general_ci
);

create table funcionario(
	id int primary key auto_increment not null,
    nome varchar(50) not null,
    telefone char(11) not null unique,
    email varchar(100) not null unique,
    cargo_id int not null,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    foreign key (cargo_id) references cargo(id)
);

create table fornecedor(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci,
    cnpj char(14) not null unique,
    email varchar(100) not null unique,
    telefone char(11) not null unique,
	funcionario_id int not null,
    foreign key (funcionario_id) references funcionario(id) on delete restrict
);

create table representante(
	fornecedor_id int primary key not null,
    nome varchar(50) not null,
    telefone char(11) not null unique,
    foreign key (fornecedor_id) references fornecedor(id) on delete cascade
);

create  table medicamento(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci,
	valorUnit decimal(10,2) not null,
    dataValidade date not null,
    tipoReceita enum('Azul', 'Comum', 'Especial'),
    formaFarmaceutica varchar (50) not null COLLATE utf8mb4_general_ci,
    dosagem varchar (50) not null COLLATE utf8mb4_general_ci,
    qnt int not null,
	funcionario_id int not null,
    foreign key (funcionario_id) references funcionario(id) 
);

create table categoria(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci
);

create table CategoriaMedicamento(
    medicamento_id int not null,
    categoria_id int not null,
    foreign key (medicamento_id) references medicamento(id),
    foreign key (categoria_id) references categoria(id),
    primary key (medicamento_id, categoria_id)
);

create table fornecedorMedicamento(
	fornecedor_id int not null,
    medicamento_id int not null,
    foreign key (fornecedor_id) references fornecedor(id),
    foreign key (medicamento_id) references medicamento(id)
);

drop database farmacia;

