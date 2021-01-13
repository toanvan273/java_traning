package vn.com.vndirect.report.job;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

//@Service
public class WHJobListener {
	
	private final static Logger log = LoggerFactory.getLogger(WHJobListener.class);
	
	 private final ObjectMapper objectMapper = new ObjectMapper();
	 
	 @Autowired
	 private DWImportJob job;
	
	@Autowired
	public WHJobListener(Environment environment) {
//	public static Thread consumeKafka(String host, String port, String topic, String groupId, String clientId, Consumer<String> msgHandler, boolean fromBeginningOrLastCommit) {
		String host = environment.getProperty("kafka.host");
		String port = environment.getProperty("kafka.port");
		try {
			
			final Properties config = new Properties();
			config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  String.format("%s:%s", host, port));
			config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
			config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);

			final KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(config);
			String topic = environment.getProperty("kafka.topic.dw");
			kafkaConsumer.subscribe(Arrays.asList(topic));

			new Thread(() -> {
				final String consumePath = String.format("%s:%s/%s", host, port, topic);
				log.info("Start polling kafka at {}", consumePath);
				while (!Thread.interrupted()){
					try {
						kafkaConsumer.poll(1000).forEach(record -> {
							parseDWJobSignal(record.value());
						});
					} catch (Exception e) {
						log.warn("polling got error -->", e);
					}
				}
				log.warn("Kafka polling at path {} interrupted  --> closing consumer...", consumePath);
				if (kafkaConsumer != null) {
					kafkaConsumer.commitSync();
					kafkaConsumer.close();
					log.warn("kafka polling at path {} interrupted  --> closing consumer...DONE!", consumePath);
				}
			});
		} catch (Exception e) {
			log.error("consumeKafka failed --> {}:{}/{}/{}/{}", host, port, e);
		}
	}

	private void parseDWJobSignal(String jsonString) {
		try {
			DWJobStatus trigger = objectMapper.readValue(jsonString, DWJobStatus.class);
			if (!DWJobStatus.DW_JOB_AS.equals(trigger.getJobs()))
				return;
			switch (trigger.getStatus()) {
			case DWJobStatus.DW_JOB_STATUS_SUCCESS:
				LocalDate.parse(trigger.getDate(), DateTimeFormatter.ISO_DATE);
				log.warn("triggering batch by DW Job status {} using trading date --> {}", trigger, trigger.getDate());
				job.sync();
				break;
			case DWJobStatus.DW_JOB_STATUS_FAILED:
				log.error("DW Batch Job failed, firing notifications --> {}", trigger);
//				notiService.sendErrorMessage("DW AS Batch Job failed", String.format("found DW batch job failed, detail --> %s", trigger));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			log.error("failed to parse object --> {}", jsonString, e);
		}
	}
}
