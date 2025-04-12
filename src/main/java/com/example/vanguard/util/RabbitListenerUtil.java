package com.example.vanguard.util;

import com.example.vanguard.common.config.RabbitMQConfig;
import com.example.vanguard.entity.GameSales;
import com.example.vanguard.repository.GameSalesRepository;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.Channel;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class RabbitListenerUtil {

  private static final Logger log = LoggerFactory.getLogger(RabbitListenerUtil.class);

  private final GameSalesRepository gameSalesRepository;

  public RabbitListenerUtil(GameSalesRepository gameSalesRepository) {
    this.gameSalesRepository = gameSalesRepository;
  }

  /**
   * Consumes messages from RabbitMQ and processes them in chunks.
   *
   * @param message the message to process
   * @param channel the RabbitMQ channel
   * @param tag the delivery tag
   * @throws IOException if an I/O error occurs
   * @throws TimeoutException if a timeout occurs
   */
  @RabbitListener(
      queues = RabbitMQConfig.QUEUE_NAME,
      containerFactory = "rabbitListenerContainerFactory")
  @Transactional
  public void consumeMessage(
      String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException, TimeoutException {
    long startTime = System.currentTimeMillis();
    List<String> messages = Arrays.asList(message.split("\n")); // Example of chunking

    try (ExecutorService executorService = createThreadPoolExecutor()) {
      int chunkSize = 10000;
      for (int i = 0; i < messages.size(); i += chunkSize) {
        List<String> chunk = messages.subList(i, Math.min(i + chunkSize, messages.size()));
        executorService.submit(() -> processChunk(new ArrayList<>(chunk)));
      }
      ackRabbitMq(channel, tag);
    } catch (Exception e) {
      log.error("Failed to process message: {}", e.getMessage());
      nackRabbitMq(channel, tag);
    }

    long endTime = System.currentTimeMillis();
    log.info("Time taken to process chunk: {} ms", (endTime - startTime));
  }

  /**
   * Creates a thread pool executor for processing messages.
   *
   * @return the thread pool executor
   */
  protected ExecutorService createThreadPoolExecutor() {
    int corePoolSize = Runtime.getRuntime().availableProcessors();
    int maxPoolSize = corePoolSize * 2;
    int keepAliveTime = 60; // seconds
    int queueCapacity = 100;

    return new ThreadPoolExecutor(
        corePoolSize,
        maxPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(queueCapacity),
        new ThreadFactoryBuilder().setNameFormat("CsvImport-%d").build());
  }

  /**
   * Acknowledges a message in RabbitMQ.
   *
   * @param channel the RabbitMQ channel
   * @param tag the delivery tag
   * @throws IOException if an I/O error occurs
   * @throws TimeoutException if a timeout occurs
   */
  protected void ackRabbitMq(Channel channel, long tag) throws IOException, TimeoutException {
    if (channel != null && channel.isOpen()) {
      channel.basicAck(tag, false);
      channel.close();
    } else {
      log.error("Channel is closed; cannot acknowledge message.");
    }
  }

  /**
   * Negative acknowledges a message in RabbitMQ.
   *
   * @param channel the RabbitMQ channel
   * @param tag the delivery tag
   * @throws IOException if an I/O error occurs
   * @throws TimeoutException if a timeout occurs
   */
  protected void nackRabbitMq(Channel channel, long tag) throws IOException, TimeoutException {
    if (channel != null && channel.isOpen()) {
      channel.basicNack(tag, false, true); // Requeue the message
      channel.close();
    } else {
      log.error("Channel is closed; cannot acknowledge message.");
    }
  }

  /**
   * Processes a chunk of messages and saves them to the database.
   *
   * @param chunk the chunk of messages to process
   */
  protected void processChunk(List<String> chunk) {
    List<GameSales> batch = new ArrayList<>();

    for (String line : chunk) {
      String[] columns = line.split(",");
      Integer gameNo = Integer.parseInt(columns[1]);
      String gameName = columns[2];
      String gameCode = columns[3];
      Integer type = Integer.parseInt(columns[4]);
      BigDecimal costPrice = new BigDecimal(columns[5]);
      BigDecimal tax = new BigDecimal(columns[6]);
      BigDecimal salePrice = new BigDecimal(columns[7]);
      ZonedDateTime dateOfSale = ZonedDateTime.parse(columns[8]);

      GameSales gameSales =
          GameSales.builder()
              .gameNo(gameNo)
              .gameName(gameName)
              .gameCode(gameCode)
              .type(type)
              .costPrice(costPrice)
              .tax(tax)
              .salePrice(salePrice)
              .dateOfSale(dateOfSale)
              .build();

      batch.add(gameSales);
    }

    gameSalesRepository.saveAllAndFlush(batch);
  }
}
