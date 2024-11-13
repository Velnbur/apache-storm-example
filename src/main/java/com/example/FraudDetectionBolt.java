import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

public class FraudDetectionBolt extends BaseRichBolt {
    private OutputCollector collector;
    private static final int MAX_SINGLE_TRANSACTION_AMOUNT = 10000;
    private static final int MAX_AVERAGE_AMOUNT = 5000;
    private static final int MAX_TRANSACTIONS_PER_MINUTE = 5;

    private Map<Integer, Integer> transactionCounts = new HashMap<>();
    private Map<Integer, Integer> lastTransactionAmount = new HashMap<>();

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String sourceComponent = tuple.getSourceComponent();

        if (sourceComponent.equals("TransactionSpout")) {
            int amount = tuple.getIntegerByField("amount");
            String timestamp = tuple.getStringByField("timestamp");
            int sender = tuple.getIntegerByField("sender");

            if (amount > MAX_SINGLE_TRANSACTION_AMOUNT) {
                collector.emit(new Values(sender, timestamp, "High single transaction amount"));
            }

            transactionCounts.put(sender, transactionCounts.getOrDefault(sender, 0) + 1);
            lastTransactionAmount.put(sender, amount);
        } else if (sourceComponent.equals("TransactionStatisticsBolt")) {
            String timestamp = tuple.getStringByField("timestamp");
            int averageAmount = tuple.getIntegerByField("average");

            if (averageAmount > MAX_AVERAGE_AMOUNT) {
                for (Integer account : transactionCounts.keySet()) {
                    collector.emit(new Values(account, timestamp, "High average transaction amount"));
                }
            }

            for (Map.Entry<Integer, Integer> entry : transactionCounts.entrySet()) {
                if (entry.getValue() > MAX_TRANSACTIONS_PER_MINUTE) {
                    collector.emit(new Values(entry.getKey(), timestamp, "High transaction rate"));
                }
            }

            transactionCounts.clear();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("accountId", "timestamp", "reason"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
	Map<String, Object> config = new HashMap<>();
	config.put(Config.TOPOLOGY_TASKS, 1); // Customize as needed, e.g., to increase parallelism
	return config;
    }
}
