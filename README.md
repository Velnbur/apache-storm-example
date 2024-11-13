# Apache Storm Example

## Visulization of topology

```mermaid
graph TD
    A[TransactionSpout] --> B[TransactionStatisticsBolt]
    A --> C[FraudDetectionBolt]
    A --> F[TransactionBucketBolt]
    B --> D[TransactionStatisticsCSVWriterBolt]
    B --> C
    C --> E[FraudDetectionCSVWriterBolt]
    F --> G[TransactionBucketCSVWriterBolt]
```
