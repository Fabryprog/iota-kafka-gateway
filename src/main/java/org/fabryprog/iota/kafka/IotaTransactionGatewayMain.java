package org.fabryprog.iota.kafka;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Properties;

public class IotaTransactionGatewayMain {

    public static void main(String[] args) {
        Config conf = ConfigFactory.load();

        Properties props = new Properties();
        props.put("bootstrap.servers", conf.getString("kafka.bootstrapServers"));
        props.put("schema.registry.url", conf.getString("kafka.schemaRegistry"));

        // N.B. IOTA ZMQ Public node: https://iota-nodes.net/
        new IotaTransactionGateway(props, conf.getString("zmq"), conf.getString("kafka.topic"), IotaTransactionGateway.KeyEnum.valueOf(conf.getString("kafka.topicKey")), conf.getBoolean("debug")).run();
    }
}
