package wzjtech;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import picocli.CommandLine;

import java.util.Properties;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "Flink Job",
        mixinStandardHelpOptions = true,
        version = "0.1",
        description = "Launch a flink job to execute ")
public class LinkParserJob implements Callable<Integer> {


    @CommandLine.Option(names = {"-d", "--selenium-driver-path"},
            required = true,
            description = "The path of selenium chrome webdriver.")
    private String driverPath;

    @CommandLine.Option(names = {"-m", "--mongo-url"},
            required = true,
            description = "Mongodb connection string, the format likes this: 'mongodb://user1:pwd1@host1/?authSource=db1'")
    private String mongoUrl;

    @CommandLine.Option(names = {"-p", "--parallelism"},
            description = "The path of selenium chrome webdriver.")
    private Integer parallelism = 1;


    public static void main(String[] args) throws Exception {
        new CommandLine(new LinkParserJob()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        WebDriverManager driverManager = new WebDriverManager(driverPath);
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);  //根据数据源来定，如果有限数据使用Batch
//        env.registerTypeWithKryoSerializer(Catalog.class, );
        env.getConfig().registerTypeWithKryoSerializer(Entities.Catalog.class,);

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "catalog_consumer");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.setProperty("flink.partition-discovery.interval-millis", "5000");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//        properties.setProperty("auto.commit.interval.ms", "2000");

        //TypeInformationSerializationSchema适用于数据是由Flink读和写的场景。比起其他序列化方法，这种schema性能更好
        var stream = env
                .addSource(new FlinkKafkaConsumer<>("catalog_request",
                        new TypeInformationSerializationSchema<>(TypeInformation.of(Entities.Catalog.class), env.getConfig()), properties))
                .setParallelism(parallelism);

        //首页解析后的DataStream
        var catalogDs = stream.map((Entities.Catalog catalog) ->
                new HomePageProcessor(catalog, driverManager).start()
        ).returns(Entities.Catalog.class);

        //save Catalog into mongodb
        catalogDs.addSink(new MongodbSink(mongoUrl));

        //获取所有分页的url
        catalogDs.flatMap((FlatMapFunction<Entities.Catalog, String>) (catalog, collector) -> {
            catalog.getPages().forEach(collector::collect);
        }).returns(Types.STRING);

        //1. source
        var ds = env.socketTextStream("localhost", 9999);

        //2. transfer
        //3. sink
        ds.print();

        //启动执行
        try {
            env.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
