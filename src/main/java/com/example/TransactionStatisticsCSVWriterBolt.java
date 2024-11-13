import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class TransactionStatisticsCSVWriterBolt extends BaseRichBolt {
    private static final String CSV_FILE_PATH = "transaction_statistics.csv";

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
	File file = new File(CSV_FILE_PATH);
        try (FileWriter writer = new FileWriter(file)) {
	    writer.write("Timestamp,Average,Min,Max\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Tuple tuple) {
        String timestamp = tuple.getStringByField("timestamp");
        int average = tuple.getIntegerByField("average");
        int min = tuple.getIntegerByField("min");
        int max = tuple.getIntegerByField("max");

        try (FileWriter writer = new FileWriter(CSV_FILE_PATH, true)) {
            writer.write(String.format("%s,%d,%d,%d\n", timestamp, average, min, max));
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
