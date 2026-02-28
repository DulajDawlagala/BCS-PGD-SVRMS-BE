package com.svrmslk.common.events.consumer;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ConsumerSeekAware;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * Provides replay capabilities by controlling consumer offset positions.
 * Allows replaying events from specific points in time or offset positions.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class ReplayableConsumer implements ConsumerSeekAware {

    private static final Logger log = LoggerFactory.getLogger(ReplayableConsumer.class);

    private ConsumerSeekCallback seekCallback;

    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {
        this.seekCallback = callback;
        log.info("Registered seek callback for replayable consumer");
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        log.info("Partitions assigned: {}", assignments.keySet());
    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        // No-op
    }

    /**
     * Replays events from a specific timestamp.
     *
     * @param topic the topic to replay
     * @param timestamp the timestamp to start replay from
     */
    public void replayFromTimestamp(String topic, Instant timestamp) {
        if (seekCallback == null) {
            log.error("Cannot replay: seek callback not registered");
            throw new IllegalStateException("Seek callback not registered");
        }

        log.info("Replaying events from timestamp: topic={}, timestamp={}", topic, timestamp);

        seekCallback.seekToTimestamp(Collections.singletonList(new TopicPartition(topic, 0)), timestamp.toEpochMilli());

        log.info("Replay initiated from timestamp: topic={}, timestamp={}", topic, timestamp);
    }

    /**
     * Replays events from a specific offset on a partition.
     *
     * @param topic the topic
     * @param partition the partition
     * @param offset the offset to start from
     */
    public void replayFromOffset(String topic, int partition, long offset) {
        if (seekCallback == null) {
            log.error("Cannot replay: seek callback not registered");
            throw new IllegalStateException("Seek callback not registered");
        }

        log.info("Replaying events from offset: topic={}, partition={}, offset={}",
                topic, partition, offset);

        TopicPartition topicPartition = new TopicPartition(topic, partition);
        seekCallback.seek(topicPartition.topic(), topicPartition.partition(), offset);

        log.info("Replay initiated from offset: topic={}, partition={}, offset={}",
                topic, partition, offset);
    }

    /**
     * Replays all events from the beginning of the topic.
     *
     * @param topic the topic to replay
     */
    public void replayFromBeginning(String topic) {
        if (seekCallback == null) {
            log.error("Cannot replay: seek callback not registered");
            throw new IllegalStateException("Seek callback not registered");
        }

        log.info("Replaying all events from beginning: topic={}", topic);

        seekCallback.seekToBeginning(Collections.singletonList(new TopicPartition(topic, 0)));

        log.info("Replay initiated from beginning: topic={}", topic);
    }

    /**
     * Seeks to the end of the topic (skip all existing messages).
     *
     * @param topic the topic
     */
    public void seekToEnd(String topic) {
        if (seekCallback == null) {
            log.error("Cannot seek: seek callback not registered");
            throw new IllegalStateException("Seek callback not registered");
        }

        log.info("Seeking to end: topic={}", topic);

        seekCallback.seekToEnd(Collections.singletonList(new TopicPartition(topic, 0)));

        log.info("Seeked to end: topic={}", topic);
    }
}