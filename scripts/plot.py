import pandas as pd
import matplotlib.pyplot as plt

# Load data from CSV files
transaction_stats_df = pd.read_csv('transaction_statistics.csv')
fraud_accounts_df = pd.read_csv('fraud_accounts.csv')

# Plot Transaction Statistics: Average, Min, and Max over time
plt.figure(figsize=(12, 6))
plt.plot(transaction_stats_df['Timestamp'], transaction_stats_df['Average'], label='Average Amount', color='blue', marker='o')
plt.plot(transaction_stats_df['Timestamp'], transaction_stats_df['Min'], label='Min Amount', color='green', marker='o')
plt.plot(transaction_stats_df['Timestamp'], transaction_stats_df['Max'], label='Max Amount', color='red', marker='o')
plt.xlabel('Timestamp')
plt.ylabel('Transaction Amount')
plt.title('Transaction Statistics Over Time')
plt.legend()
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()

# Plot Fraudulent Transactions over time
plt.figure(figsize=(12, 6))
fraud_counts = fraud_accounts_df['Timestamp'].value_counts().sort_index()
plt.bar(fraud_counts.index, fraud_counts.values, color='orange')
plt.xlabel('Timestamp')
plt.ylabel('Number of Fraudulent Transactions')
plt.title('Fraudulent Transactions Over Time')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()

# Display tables for both CSV files
print("\n--- Transaction Statistics ---")
print(transaction_stats_df)

print("\n--- Fraudulent Accounts ---")
print(fraud_accounts_df)
