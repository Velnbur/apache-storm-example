import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TransactionBucketBolt extends BaseRichBolt {
    private OutputCollector collector;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LocalDateTime START_DATE = LocalDateTime.of(2023, 1, 1, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2024, 12, 31, 23, 59);
    
    private Map<String, Integer> buckets = new HashMap<>();

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        // Initialize 5 time-based buckets
        for (int i = 1; i <= 5; i++) {
            buckets.put("Bucket " + i, 0);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        String timestampStr = tuple.getStringByField("timestamp");
        LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

        // Check if the timestamp falls within the specified range
        if (!timestamp.isBefore(START_DATE) && !timestamp.isAfter(END_DATE)) {
            long totalMinutes = java.time.Duration.between(START_DATE, END_DATE).toMinutes();
            long minutesElapsed = java.time.Duration.between(START_DATE, timestamp).toMinutes();
            int bucketIndex = (int) ((minutesElapsed * 5) / totalMinutes) + 1;
            String bucketKey = "Bucket " + bucketIndex;

            // Increment the count for the corresponding bucket
            buckets.put(bucketKey, buckets.get(bucketKey) + 1);
        }

        // Emit current bucket stats
        collector.emit(new Values(buckets.get("Bucket 1"), buckets.get("Bucket 2"),
                                  buckets.get("Bucket 3"), buckets.get("Bucket 4"), buckets.get("Bucket 5")));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("Bucket 1", "Bucket 2", "Bucket 3", "Bucket 4", "Bucket 5"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
	Map<String, Object> config = new HashMap<>();
	config.put(Config.TOPOLOGY_TASKS, 1); // Customize as needed, e.g., to increase parallelism
	return config;
    }
}
