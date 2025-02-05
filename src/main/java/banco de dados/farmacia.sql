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

create table categoria(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci
);

create table fabricante(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci
);

create  table medicamento(
	id int primary key auto_increment not null,
    nome varchar(50) not null COLLATE utf8mb4_bin,
    dosagem varchar (50) not null COLLATE utf8mb4_bin,
    formaFarmaceutica varchar (50) not null COLLATE utf8mb4_bin,
    valorUnit decimal(10,2) not null,
    dataValidade date not null,
    dataFabricacao date not null,
    tipoReceita enum('SIMPLES', 'ESPECIAL'),
    qnt int not null,
    tipo enum('ÉTICO', 'GENÉRICO', 'SIMILAR'),
    categoria_id int not null,
    funcionario_id int not null,
    fabricante_id int not null,
    fornecedor_id int not null,
    foreign key (categoria_id) references categoria (id) on delete cascade,
    foreign key (funcionario_id) references funcionario (id),
    foreign key (fabricante_id) references fabricante (id),
    foreign key (fornecedor_id) references fornecedor (id),    
	unique (nome, dosagem, formaFarmaceutica, dataValidade, dataFabricacao, fabricante_id)
);

create table cliente(
	id int primary key auto_increment not null,
    nome varchar(50) not null,
    cpf VARCHAR(255) not null unique,
    telefone char(11) not null unique,
    rua varchar(100) not null,
    numCasa varchar(5),
    bairro varchar(100) not null,
    cidade varchar(50) not null,
	estado ENUM('AC', 'AL', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 
                'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO') NOT NULL,    
	pontoReferencia varchar(255),
    funcionario_id int not null,
    foreign key (funcionario_id) references funcionario(id) 
);

drop database farmacia;
insert into cargo values (1, 'Gerente');
insert into funcionario values (1, 'Daniel', '88998045537', 'd@gmail.com', 1, true);


