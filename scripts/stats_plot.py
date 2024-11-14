import pandas as pd
import matplotlib.pyplot as plt

# Load the CSV data
data = pd.read_csv('transaction_statistics.csv')

data = data.iloc[::1_000, :]

# Convert the timestamp to a datetime format for proper plotting
data['Timestamp'] = pd.to_datetime(data['Timestamp'])
data['Average'] = data['Average'].str.replace(",", ".")


# Set the timestamp as the index
data.set_index('Timestamp', inplace=True)

# Plotting the data
plt.figure(figsize=(12, 6))

# Plot the lines for min, average, and max amounts
plt.plot(data.index, data['Min'], label='Min Amount', color='skyblue')
plt.plot(data.index, data['Average'], label='Average Amount', color='cornflowerblue')
plt.plot(data.index, data['Max'], label='Max Amount', color='royalblue')

# Fill areas under each line
plt.fill_between(data.index, data['Min'], color='skyblue', alpha=0.3)
plt.fill_between(data.index, data['Min'], data['Average'], color='cornflowerblue', alpha=0.3)
plt.fill_between(data.index, data['Average'], data['Max'], color='royalblue', alpha=0.3)

# Add titles and labels
plt.title('Transaction Statistics Over Time')
plt.xlabel('Timestamp')
plt.ylabel('Amount')

# Display legend
plt.legend()

# Show the plot
plt.tight_layout()
plt.show()

print(data)
