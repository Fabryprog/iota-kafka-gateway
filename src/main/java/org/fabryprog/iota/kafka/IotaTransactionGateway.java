package org.fabryprog.iota.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jota.model.Transaction;
import jota.utils.TrytesConverter;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.LogContext;
import org.slf4j.Logger;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.text.MessageFormat;
import java.util.Properties;

public class IotaTransactionGateway {
    private final Logger log;

    private KafkaProducer kafkaProducer = null;
    private String zmqUrl = null;
    private String kafkaTopic = null;
    private boolean debug = false;

    public IotaTransactionGateway(final Properties properties, final String zmqUrl, final String IotaTXTopic, final boolean debug) {
        this.zmqUrl = zmqUrl;
        this.kafkaTopic = IotaTXTopic;
        this.debug = debug;

        LogContext logContext = new LogContext(String.format("[Iota Kafka Gateway %s] ", this.kafkaTopic));

        this.log = logContext.logger(KafkaProducer.class);

        //force serializers
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        kafkaProducer = new KafkaProducer(properties);
    }

    public void run() {
        log.info("Starting the Kafka producer");
        ZContext context = new ZContext();
        // Socket to talk to clients
        ZMQ.Socket socket = context.createSocket(SocketType.SUB);
        socket.connect(zmqUrl);

        socket.subscribe("tx_trytes");
        log.info("ZMQ Listening...");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                String[] payload = new String(socket.recv(0), ZMQ.CHARSET).split(" ");

                String msg = payload[1];
                String hash = payload[2];

                Transaction transaction = Transaction.asTransactionObject(msg);

                String _msg = null;
                if(transaction.getSignatureFragments().length() % 2 == 0) {
                    _msg = TrytesConverter.trytesToAscii(transaction.getSignatureFragments());
                } else {
                    _msg = TrytesConverter.trytesToAscii(transaction.getSignatureFragments() + "9");
                }

                String _tag = null;
                if(transaction.getTag().length() % 2 == 0) {
                    _tag = TrytesConverter.trytesToAscii(transaction.getTag());
                } else {
                    _tag = TrytesConverter.trytesToAscii(transaction.getTag() + "9");
                }

                Schema.Parser parser = new Schema.Parser();
                Schema schema = parser.parse(org.fabryprog.iota.kafka.pojo.Transaction.AVRO_SCHEMA);
                GenericRecord avroRecord = new GenericData.Record(schema);
                avroRecord.put("hash", transaction.getHash());
                avroRecord.put("address", transaction.getAddress());
                avroRecord.put("value", transaction.getValue());
                avroRecord.put("tag", _tag);
                avroRecord.put("timestamp", transaction.getTimestamp());
                avroRecord.put("payload", _msg);

                ProducerRecord<String, Object> record = new ProducerRecord<>(kafkaTopic, transaction.getHash(), avroRecord);

                kafkaProducer.send(record);
                if(debug) {
                    log.info(MessageFormat.format("Message sended to producer! [Topic {0}] - [Key {1}]", kafkaTopic, transaction.getHash()));
                }
            }
        } finally {
            if(kafkaProducer != null) {kafkaProducer.close();}
        }
    }

}
