  -- таблиця директори
  CREATE TABLE directors (
    passport_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name        VARCHAR(30)         NOT NULL
  );

  -- таблиця Національні банки
  CREATE TABLE national_banks (
    country_code INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name         VARCHAR(30)         NOT NULL,
    director     INTEGER             NOT NULL,

    FOREIGN KEY (director) REFERENCES directors(passport_id)
  );

  -- таблиця Департаменти
  CREATE TABLE national_bank_departments (
    name VARCHAR(30) NOT NULL,
    bank INTEGER     NOT NULL,

    PRIMARY KEY (name, bank),
    FOREIGN KEY (bank) REFERENCES national_banks(country_code)
  );

  -- таблиця Користувачі
  CREATE TABLE users (
    passport_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name        VARCHAR(30)         NOT NULL,
    email       VARCHAR(20)         NOT NULL
  );

  -- таблиця Рахунки
  CREATE TABLE accounts (
    id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    balance INTEGER             NOT NULL,
    creator INTEGER             NOT NULL,
    bank    INTEGER             NOT NULL,

    FOREIGN KEY (bank)    REFERENCES national_banks(country_code),
    FOREIGN KEY (creator) REFERENCES users         (passport_id)
  );

    -- таблиця Cпільних рахунків
  CREATE TABLE multiuser_account (
    id INTEGER PRIMARY KEY NOT NULL,
    FOREIGN KEY (id) REFERENCES accounts(id)
  );

  -- таблиця-звʼязок спільних рахунків та користувачів (багато-до-багатьох)
  CREATE TABLE multiuser_account_users_relation (
    account_id INTEGER NOT NULL,
    user_id    INTEGER NOT NULL,
    
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (user_id)    REFERENCES users(id),
    PRIMARY KEY (account_id, user_id)
  );

  -- таблиця Транзакцій
  CREATE TABLE transactions (
    id                     INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,
    amount                 INTEGER              NOT NULL CHECK(amount >= 0),
    timestamp              DATETIME             DEFAULT CURRENT_TIMESTAMP,
    sender                 INTEGER              NOT NULL,
    receiver               INTEGER              NOT NULL,
    cancelling_transaction INTEGER UNIQUE,
  
    FOREIGN KEY (cancelling_transaction) REFERENCES transactions(id),
    FOREIGN KEY (sender)                 REFERENCES accounts    (id),
    FOREIGN KEY (receiver)               REFERENCES accounts    (id)
  );

  CREATE TRIGGER check_cancelling_transaction_insert
  BEFORE INSERT ON transactions
  WHEN NEW.cancelling_transaction IS NOT NULL
  BEGIN
    SELECT CASE
    WHEN NEW.sender NOT IN (SELECT sender FROM transactions WHERE id = NEW.cancelling_transaction)
    THEN RAISE(ABORT, 'Sender of cancelling transaction does not match the sender of the original transaction')
    END;
  END;
  
  CREATE TRIGGER check_cancelling_transaction_update
  BEFORE UPDATE ON transactions
  WHEN NEW.cancelling_transaction IS NOT NULL
  BEGIN
    SELECT CASE
    WHEN NEW.sender NOT IN (SELECT sender FROM transactions WHERE id = NEW.cancelling_transaction)
    THEN RAISE(ABORT, 'Sender of cancelling transaction does not match the sender of the original transaction')
    END;
  END;
