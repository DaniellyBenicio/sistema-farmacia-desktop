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
    nome varchar(50) not null COLLATE utf8mb4_general_ci,
    dosagem varchar (50) not null COLLATE utf8mb4_general_ci,
    formaFarmaceutica varchar (50) not null COLLATE utf8mb4_general_ci,
    valorUnit decimal(10,2) not null,
    dataValidade date not null,
    dataFabricacao date not null,
    tipoReceita enum('SIMPLES', 'ESPECIAL'),
    qnt int not null,
    tipo enum('ETICO', 'GENERICO', 'SIMILAR'),
    fornecedor_id int not null,
	funcionario_id int not null,
    categoria_id int not null,
    foreign key (funcionario_id) references funcionario(id),
    foreign key (fornecedor_id) references fornecedor(id),
    foreign key (categoria_id) references categoria (id)
);

create table categoria(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci
);

create table fabricante(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci
);

create table fabricanteMedicamento(
	fabricante_id int,
    medicamento_id int,
    primary key(fabricante_id, medicamento_id),
    foreign key (fabricante_id) references fabricante(id) on delete cascade,
    foreign key (medicamento_id) references medicamento(id) on delete cascade
);

create table fornecedorMedicamento(
	fornecedor_id int not null,
    medicamento_id int not null,
    primary key (fornecedor_id, medicamento_id),
    foreign key (fornecedor_id) references fornecedor(id),
    foreign key (medicamento_id) references medicamento(id)
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
