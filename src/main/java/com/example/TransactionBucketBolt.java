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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]");

    private static final LocalDateTime START_DATE = LocalDateTime.of(2023, 1, 1, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2024, 12, 31, 23, 59);

    private Map<String, Integer> buckets = new HashMap<>();

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        for (int i = 1; i <= 8; i++) {
            buckets.put("Bucket " + i, 0);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        String timestampStr = tuple.getStringByField("timestamp");
        LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

        if (!timestamp.isBefore(START_DATE) && !timestamp.isAfter(END_DATE)) {
            int bucketIndex = determineBucketIndex(timestamp);
            String bucketKey = "Bucket " + bucketIndex;
            buckets.put(bucketKey, buckets.get(bucketKey) + 1);
        }

        collector.emit(new Values(
            buckets.get("Bucket 1"), buckets.get("Bucket 2"),
            buckets.get("Bucket 3"), buckets.get("Bucket 4"),
            buckets.get("Bucket 5"), buckets.get("Bucket 6"),
            buckets.get("Bucket 7"), buckets.get("Bucket 8")
        ));
    }

    private int determineBucketIndex(LocalDateTime timestamp) {
        int year = timestamp.getYear();
        int season = getSeasonIndex(timestamp.getMonthValue());

        if (year == 2023) {
            return season;
        } else if (year == 2024) {
            return season + 4;
        }
        return -1;
    }

    private int getSeasonIndex(int month) {
        if (month == 12 || month <= 2) return 1; // Winter
        if (month >= 3 && month <= 5) return 2; // Spring
        if (month >= 6 && month <= 8) return 3; // Summer
        if (month >= 9 && month <= 11) return 4; // Fall
        return -1;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("Bucket 1", "Bucket 2", "Bucket 3", "Bucket 4", 
                                    "Bucket 5", "Bucket 6", "Bucket 7", "Bucket 8"));
    }
}
