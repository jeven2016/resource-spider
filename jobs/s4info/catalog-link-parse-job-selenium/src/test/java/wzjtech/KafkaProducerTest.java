package wzjtech;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerTest {
    public static void main(String[] args) throws InterruptedException {
        // 获取数据
        Entities.Catalog data = new Entities.Catalog();

        // 创建配置文件
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "192.168.100.201:9092,192.168.100.202:9092,192.168.100.203:9092");
        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer", "com.avro.AvroUtil.SimpleAvroSchemaJava");

        // 创建kafka的生产者
        KafkaProducer<String, Entities.Catalog> producer = new KafkaProducer<>(props);

        ProducerRecord<String, Entities.Catalog> producerRecord = new ProducerRecord<>("catalog_request", data);
        producer.send(producerRecord);
        System.out.println("数据写入成功" + data);
        Thread.sleep(1000);
    }
}
