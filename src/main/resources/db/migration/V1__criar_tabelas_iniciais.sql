CREATE TABLE servidor (
                          id BIGSERIAL PRIMARY KEY,
                          matricula VARCHAR(20) UNIQUE NOT NULL,
                          nome VARCHAR(150) NOT NULL,
                          cargo VARCHAR(100),
                          lotacao VARCHAR(150)
);

CREATE TABLE periodo_aquisitivo (
                                    id BIGSERIAL PRIMARY KEY,
                                    servidor_id BIGINT NOT NULL REFERENCES servidor(id),
                                    ano_referencia INTEGER NOT NULL,
                                    data_inicio DATE NOT NULL,
                                    data_fim DATE NOT NULL,
                                    saldo_dias INTEGER NOT NULL DEFAULT 30
);

CREATE TABLE solicitacao_ferias (
                                    id BIGSERIAL PRIMARY KEY,
                                    periodo_aquisitivo_id BIGINT NOT NULL REFERENCES periodo_aquisitivo(id),
                                    data_inicio_gozo DATE NOT NULL,
                                    dias_solicitados INTEGER NOT NULL,
                                    abono_pecuniario BOOLEAN DEFAULT FALSE,
                                    status VARCHAR(50) NOT NULL
);