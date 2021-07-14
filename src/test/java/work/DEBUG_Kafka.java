package work;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;

public class DEBUG_Kafka {
	
	private static final String TOPIC = "testAAA"; //kafka创建的topic
	private static final String PORT ="9092";

//    private static final String HOST = "172.17.8.10";
    
    private static final String HOST = "managerwtg.com";
    
    private static final String BROKER_LIST = HOST+":"+PORT; //broker的地址和端口
	
	@Test
	public void testProducerAPI() {
		Properties props = new Properties();
		props.put("bootstrap.servers", BROKER_LIST);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("linger.ms", 1);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//		try (Producer<String, String> producer = new KafkaProducer<String, String>(props);) {
//			producer.send(new ProducerRecord<String, String>(TOPIC, "Fate", "Successs1c"));
//		}
		
		List<String> msgs = Arrays.asList("sss","sss","sss","sssssss");
		
		 try(Producer<String, String> producer = new KafkaProducer<String, String>(props);){
			 msgs.forEach(val->{
				 producer.send(new ProducerRecord<String, String>(TOPIC,"Fate1",val));
			 });
		 }
		
		 System.out.println("send end");
	}
	
	@Test
	public void testConsumerAPI() {
		Properties props = new Properties();

		props.put("bootstrap.servers", BROKER_LIST);
		// 每个消费者分配独立的组号
		props.put("group.id", "test-consumer-group");

		// 如果value合法，则自动提交偏移量
		props.put("enable.auto.commit", "true");

		// 设置多久一次更新被消费消息的偏移量
		props.put("auto.commit.interval.ms", "1000");

		// 设置会话响应的时间，超过这个时间kafka可以选择放弃消费或者消费下一条消息
		props.put("session.timeout.ms", "30000");

		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);) {
			consumer.subscribe(Collections.singletonList("log_monitor_estack"));

			while (true) {
				Duration duration = Duration.ofMillis(100);
				ConsumerRecords<String, String> records = consumer.poll(duration);
				for (ConsumerRecord<String, String> record : records) {
					// print the offset,key and value for the consumer records.
					System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(),
							record.value());
				}
			}
		}
	}
	
	@Test
	public void testKafkaConsumer() {
		//配置信息
        Properties props = new Properties();
        //kafka服务器地址
        props.put("bootstrap.servers", BROKER_LIST);
        //必须指定消费者组
        props.put("group.id", "test");
        //设置数据key和value的序列化处理类
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        //创建消息者实例
        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(props);
        //订阅topic1的消息
        consumer.subscribe(Arrays.asList("log_monitor_estack"));
        //到服务器中读取记录
        while (true){
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<String,String> record : records){
                System.out.println("key:" + record.key() + "" + ",value:" + record.value());
            }
            System.out.println(records.count());
        }
	}
}
