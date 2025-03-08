create database farmacia;
use farmacia;

create table cargo(
	id int primary key auto_increment not null,
    nome varchar(50) not null COLLATE utf8mb4_general_ci,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table funcionario(
	id int primary key auto_increment not null,
    nome varchar(50) not null,
    telefone char(11) not null unique,
    email varchar(100) not null unique,
    cargo_id int not null,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    foreign key (cargo_id) references cargo(id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table fornecedor(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci,
    cnpj char(14) not null unique,
    email varchar(100) not null unique,
    telefone char(11) not null unique,
	funcionario_id int not null,
    foreign key (funcionario_id) references funcionario(id) on delete restrict,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table representante(
	fornecedor_id int primary key not null,
    nome varchar(50) not null,
    telefone char(11) not null unique,
    foreign key (fornecedor_id) references fornecedor(id) on delete cascade,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table categoria(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table fabricante(
	id int primary key auto_increment not null,
    nome varchar(50) not null unique COLLATE utf8mb4_general_ci,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
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
    embalagem varchar (50) not null COLLATE utf8mb4_bin,
    qntEmbalagem int not null,
    categoria_id int not null,
    funcionario_id int not null,
    fabricante_id int not null,
    fornecedor_id int not null,
    foreign key (categoria_id) references categoria (id) on delete cascade,
    foreign key (funcionario_id) references funcionario (id),
    foreign key (fabricante_id) references fabricante (id),
    foreign key (fornecedor_id) references fornecedor (id),    
	unique (nome, dosagem, formaFarmaceutica, embalagem, qntEmbalagem, dataValidade, dataFabricacao, fabricante_id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
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
    foreign key (funcionario_id) references funcionario(id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table produto (
	id int primary key auto_increment not null,
    nome varchar(50) not null COLLATE utf8mb4_bin,
    valor decimal(10,2) not null,
    qntEstoque int not null,
    dataValidade date not null,
    dataFabricacao date not null,
    qntMedida varchar (50) not null COLLATE utf8mb4_bin,
    embalagem varchar (50) not null COLLATE utf8mb4_bin,
    qntEmbalagem int not null,
    funcionario_id int not null,
    fabricante_id int not null,
    fornecedor_id int not null,
    categoria_id int not null,
    foreign key (funcionario_id) references funcionario (id),
    foreign key (fabricante_id) references fabricante (id),
    foreign key (fornecedor_id) references fornecedor (id),    
    foreign key (categoria_id) references categoria (id),
    unique (nome, qntMedida, embalagem, fabricante_id, dataFabricacao, dataValidade, categoria_id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table venda(
    id int primary key auto_increment not null, 
    cliente_id int null,
    funcionario_id int not null,
    valorTotal decimal(10,2) not null,
    desconto decimal(10,2) default 0,
    formaPagamento enum('DINHEIRO', 'CARTÃO', 'PIX') not null,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    foreign key (cliente_id) references Cliente(id),
    foreign key (funcionario_id) references Funcionario(id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table itemVenda (
    id int primary key auto_increment not null, 
    venda_id int not null,
    produto_id int null,
    medicamento_id int null,
    qnt int not null check (qnt > 0),
    precoUnit decimal(10,2) not null check (precoUnit >= 0),
    subtotal decimal(10,2) generated always as (qnt * precoUnit) stored,
    desconto decimal(10,2) default 0 check (desconto >= 0),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp,
    foreign key (venda_id) references venda(id) on delete cascade,
    foreign key (produto_id) references produto(id) on delete set null,
    foreign key (medicamento_id) references medicamento(id) on delete set null
);

DELIMITER $$

CREATE TRIGGER checarItem BEFORE INSERT ON itemvenda
FOR EACH ROW
BEGIN
    IF NEW.produto_id IS NULL AND NEW.medicamento_id IS NULL THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erro: A venda precisa ter um produto, medicamento associado ou ambos.';
    END IF;
    
    IF NEW.produto_id IS NOT NULL THEN
        SET NEW.subtotal = NEW.qnt * NEW.precoUnit - NEW.desconto;
    ELSEIF NEW.medicamento_id IS NOT NULL THEN
        SET NEW.subtotal = NEW.qnt * NEW.precoUnit - NEW.desconto;
    END IF;

    IF NEW.desconto > NEW.subtotal THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro: O desconto não pode ser maior que o subtotal.';
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER atualizarEstoque 
AFTER INSERT ON itemVenda 
FOR EACH ROW
BEGIN 
    IF NEW.produto_id IS NOT NULL THEN
        UPDATE produto
        SET qntEstoque = GREATEST(qntEstoque - NEW.qnt, 0)
        WHERE id = NEW.produto_id;
    END IF;

    IF NEW.medicamento_id IS NOT NULL THEN
        UPDATE medicamento
        SET qnt = GREATEST(qnt - NEW.qnt, 0) 
        WHERE id = NEW.medicamento_id;
    END IF;
END $$

DELIMITER ;


insert into cargo (nome) values ('Gerente');
insert into funcionario (nome, telefone, email, cargo_id, status) values ('Danielly', '88998045537', 'd@gmail.com', 1, true);

SHOW TRIGGERS;
