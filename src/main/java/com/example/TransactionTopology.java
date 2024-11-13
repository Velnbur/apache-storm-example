import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

public class TransactionTopology {
    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("TransactionSpout", new TransactionSpout());

        builder.setBolt("TransactionStatisticsBolt", new TransactionStatisticsBolt())
               .shuffleGrouping("TransactionSpout");

        builder.setBolt("TransactionStatisticsCSVWriterBolt", new TransactionStatisticsCSVWriterBolt())
               .shuffleGrouping("TransactionStatisticsBolt");

        builder.setBolt("FraudDetectionBolt", new FraudDetectionBolt())
               .allGrouping("TransactionSpout")
               .allGrouping("TransactionStatisticsBolt");

        builder.setBolt("FraudDetectionCSVWriterBolt", new FraudDetectionCSVWriterBolt())
               .shuffleGrouping("FraudDetectionBolt");

	builder.setBolt("TransactionBucketBolt", new TransactionBucketBolt())
               .shuffleGrouping("TransactionSpout");

	builder.setBolt("TransactionBucketCSVWriterBolt", new TransactionBucketCSVWriterBolt())
               .shuffleGrouping("TransactionBucketBolt");

        Config config = new Config();
        config.setDebug(true);

        LocalCluster cluster = new LocalCluster();
        try {
            cluster.submitTopology("TransactionTopology", config, builder.createTopology());
            Utils.sleep(20 * 60 * 1000); // Let it run for 20 minutes
        } finally {
            cluster.shutdown();
        }
    }
}
