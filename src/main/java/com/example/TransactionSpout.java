import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

public class TransactionSpout implements IRichSpout {
    private SpoutOutputCollector collector;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private boolean completed = false;

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        try {
            // Connect to the database
            String url = "jdbc:sqlite:database.sqlite";  // Replace with your database connection URL
            connection = DriverManager.getConnection(url);

            // Prepare statement to retrieve transactions
            String query = "SELECT id, amount, timestamp, sender, receiver FROM transactions ORDER BY timestamp";
            preparedStatement = connection.prepareStatement(query);

            // Execute the query and get the ResultSet
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextTuple() {
        try {
            if (resultSet != null && resultSet.next()) {
                // Extract transaction data
                int id = resultSet.getInt("id");
                int amount = resultSet.getInt("amount");
                String timestamp = resultSet.getString("timestamp");
                int sender = resultSet.getInt("sender");
                int receiver = resultSet.getInt("receiver");

                // Emit the transaction as a tuple
                collector.emit(new Values(id, amount, timestamp, sender, receiver));
            } else {
                // If no more rows are available, mark as completed
                if (!completed) {
                    completed = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "amount", "timestamp", "sender", "receiver"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
	Map<String, Object> config = new HashMap<>();
	config.put(Config.TOPOLOGY_TASKS, 1); // Customize as needed, e.g., to increase parallelism
	return config;
    }

    @Override
    public void activate() {}
    @Override
    public void deactivate() {}
    @Override
    public void ack(Object msgId) {}
    @Override
    public void fail(Object msgId) {}
}
