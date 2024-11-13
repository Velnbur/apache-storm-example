import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Fields;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class TransactionBucketCSVWriterBolt extends BaseRichBolt {
    private static final String CSV_FILE_PATH = "transaction_buckets.csv";

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
            writer.write("Bucket 1,Bucket 2,Bucket 3,Bucket 4,Bucket 5\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try (FileWriter writer = new FileWriter(CSV_FILE_PATH, true)) {
            writer.write(String.format("%d,%d,%d,%d,%d\n",
                tuple.getIntegerByField("Bucket 1"),
                tuple.getIntegerByField("Bucket 2"),
                tuple.getIntegerByField("Bucket 3"),
                tuple.getIntegerByField("Bucket 4"),
                tuple.getIntegerByField("Bucket 5")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {}

    @Override
    public Map<String, Object> getComponentConfiguration() {
	Map<String, Object> config = new HashMap<>();
	config.put(Config.TOPOLOGY_TASKS, 1); // Customize as needed, e.g., to increase parallelism
	return config;
    }
}
