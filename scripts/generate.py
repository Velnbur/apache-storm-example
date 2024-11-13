import sqlite3
from faker import Faker
import random
from pathlib import Path

database_file = Path("database.sqlite")
migration = Path("migrations/001_init.sql")
fake = Faker()

if not database_file.exists():
    content = ""
    with open(migration, 'r') as file:
        content = file.read()

    conn = sqlite3.connect(database_file)
    curr = conn.cursor()
    curr.executescript(content)
    conn.commit()
    conn.close()

conn = sqlite3.connect(database_file)

# Constants for the number of rows to insert
NUM_DIRECTORS = 10
NUM_BANKS = 5
NUM_USERS = 10_000
NUM_ACCOUNTS = 50_000
NUM_TRANSACTIONS = 10_000_000

cursor = conn.cursor()

# Assuming the provided SQL schema is executed to create tables

# Populate directors
directors = [(fake.name(),) for _ in range(NUM_DIRECTORS)]
cursor.executemany("INSERT INTO directors (name) VALUES (?)", directors)

# Populate national_banks
national_banks = [(fake.company(), random.randint(1, NUM_DIRECTORS)) for _ in range(NUM_BANKS)]
cursor.executemany(
    "INSERT INTO national_banks (name, director) VALUES (?, ?)", national_banks
)

# Populate users
users = [(fake.name(), fake.email()) for _ in range(NUM_USERS)]
cursor.executemany("INSERT INTO users (name, email) VALUES (?, ?)", users)

# Populate accounts
accounts = [
    (random.randint(1000, 10000), random.randint(1, NUM_USERS), random.randint(1, NUM_BANKS))
    for _ in range(NUM_ACCOUNTS)
]
cursor.executemany(
    "INSERT INTO accounts (balance, creator, bank) VALUES (?, ?, ?)", accounts
)

# Populate transactions
transactions = [
    (random.randint(1, 1000), random.randint(1, NUM_ACCOUNTS), random.randint(1, NUM_ACCOUNTS))
    for _ in range(NUM_TRANSACTIONS)
]
cursor.executemany(
    "INSERT INTO transactions (amount, sender, receiver) VALUES (?, ?, ?)", transactions
)

conn.commit()
conn.close()