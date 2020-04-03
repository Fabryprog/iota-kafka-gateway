# IOTA Kafka Gateway

This class implements a listener for IOTA Transactions (from ZMQ IOTA Node) to Kafka Topic

Note: Every transactions are converted into an AvroRecord with following schema

```json
{
  "type": "record",
  "namespace": "org.fabryprog.iota.kafka.pojo",
  "name": "Transaction",
  "version": "1",
  "fields": [
    { "name": "hash", "type": "string", "doc": "Transaction Hash" },
    { "name": "address", "type": "string", "doc": "Address" },
    { "name": "value", "type": "long", "doc": "Transaction value" },
    { "name": "tag", "type": "string", "doc": "TAG" },
    { "name": "timestamp", "type": "long", "doc": "Timestamp" },
    { "name": "payload", "type": "string", "doc": "Payload" }
  ]
}
```

## USAGE

## Usage nested MainClass

The jar file (create with maven package lifecycle phase) uses a config file called **application.conf** (use -Dconfig.file=path/to/application.conf)

The config file must be equals below:

```hocon
# KAFKA
kafka {
  bootstrapServers = "kafka-broker-one:9092,kafka-broker-two:9092,kafka-broker-three:9092"
  schemaRegistry = "http://kafka-registry:8081"
  topic = "iota-gateway"
  topicKey = "TAG"
}

# IOTA
# visit https://iota-nodes.net/
zmq="tcp://ultranode.iotatoken.nl:5556"

# MISC
debug=false
```

## Usage the Java API

**IotaTransactionGateway** class parameters are:
 - Kafka Properties
 - IOTA ZMQ node URL
 - Kafka Topic Name
 - Kafka Key value
 - debug mode (default false)
 
The method **run()** could be use to start transaction's listening 

```java
package org.fabryprog.iota.kafka;

import java.util.Properties;

public class MainClass {
    public static void main(String args[]) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka-broker-one:9092,kafka-broker-two:9092,kafka-broker-three:9092");
        props.put("schema.registry.url", "http://kafka-registry:8081");
        
        // N.B. IOTA ZMQ Public node: https://iota-nodes.net/
        new IotaTransactionGateway(props, "tcp://ultranode.iotatoken.nl:5556", "iota-gateway", KeyEnum.HASH, true).run();
    }
}
```
