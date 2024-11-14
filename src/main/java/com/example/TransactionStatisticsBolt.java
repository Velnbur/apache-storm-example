import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

public class TransactionStatisticsBolt extends BaseRichBolt {
    public static Logger logger = LoggerFactory.getLogger(TransactionStatisticsBolt.class);
    private OutputCollector collector;
    private double average = 0.0;
    private int count = 0;
    private int min = 500_000;
    private int max = 0;

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        int amount = tuple.getIntegerByField("amount");
        String timestamp = tuple.getStringByField("timestamp");

        if (amount < min) min = amount;
        if (amount > max) max = amount;
	
        average = (average * count + amount) / (count+1);
	count++;

        collector.emit(new Values(timestamp, average, min, max));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("timestamp", "average", "min", "max"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
	Map<String, Object> config = new HashMap<>();
	config.put(Config.TOPOLOGY_TASKS, 1); // Customize as needed, e.g., to increase parallelism
	return config;
    }
}
