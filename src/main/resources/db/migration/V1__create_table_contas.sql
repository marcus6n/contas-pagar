CREATE TABLE
    contas (
        id BIGSERIAL PRIMARY KEY,
        nome VARCHAR(255) NOT NULL,
        valor_original NUMERIC(10, 2) NOT NULL,
        data_vencimento DATE NOT NULL,
        data_pagamento DATE NOT NULL,
        dias_atraso INTEGER NOT NULL,
        percentual_multa NUMERIC(5, 2) NOT NULL,
        percentual_juros_dia NUMERIC(5, 3) NOT NULL,
        valor_corrigido NUMERIC(10, 2) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_contas_data_vencimento ON contas (data_vencimento);

CREATE INDEX idx_contas_data_pagamento ON contas (data_pagamento);