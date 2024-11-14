import pandas as pd
import matplotlib.pyplot as plt

# Load the CSV data
data = pd.read_csv('transaction_buckets.csv')

# Get the last row of the data (most recent bucket counts)
last_row = data.iloc[-1]


# Plot the data
plt.figure(figsize=(8, 5))
plt.bar(last_row.index, last_row.values, color='cornflowerblue')
plt.title('Transaction Counts by Bucket (Most Recent)')
plt.xlabel('Bucket')
plt.ylabel('Transaction Count')

# Show the plot
plt.tight_layout()
plt.show()
