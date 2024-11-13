# Apache Storm Example

## Visulization of topology

```mermaid
graph LR
    A[TransactionSpout] --> B[TransactionStatisticsBolt]
    A --> C[FraudDetectionBolt]
    B --> D[TransactionStatisticsCSVWriterBolt]
    B --> C
    C --> E[FraudDetectionCSVWriterBolt]
```
