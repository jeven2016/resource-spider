package wzjtech;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bson.Document;

public class MongodbSink extends RichSinkFunction<Entities.Catalog> {
    private final String mongoUrl;
    private MongoClient mongoClient;
    private MongoCollection<Document> collection;

    public MongodbSink(String url) {
        mongoUrl = url;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        //"mongodb://user1:pwd1@host1/?authSource=db1&authMechanism=SCRAM-SHA-1"
        //"mongodb://host1:27107,host2:27017/?ssl=true"
        ConnectionString connectionString = new ConnectionString(mongoUrl);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        mongoClient = MongoClients.create(settings);
        var database = mongoClient.getDatabase("s4info");
        collection = database.getCollection("catalog");
    }

    @Override
    public void close() throws Exception {
        System.out.println("close mongo connection~~~");
        mongoClient.close();
    }

    @Override
    public void invoke(Entities.Catalog value, Context context) throws Exception {
//        getRuntimeContext().
        //插入一个记录
        Document catalogDoc = new Document()
                .append("name", value.getName())
                .append("url", value.getUrl())
                .append("pageCount", value.getPageCount());

        collection.insertOne(catalogDoc);
    }
}
