package com.example.vanguard.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.vanguard.entity.GameSales;
import com.example.vanguard.repository.GameSalesRepository;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RabbitListenerUtilTest {

  @Mock private GameSalesRepository gameSalesRepository;

  @Mock private Channel channel;

  @InjectMocks private RabbitListenerUtil rabbitListenerUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    rabbitListenerUtil = spy(rabbitListenerUtil);
    when(channel.isOpen()).thenReturn(true);
  }

  @Test
  void testConsumeMessage_Success() throws IOException, TimeoutException {
    String message =
        "1,101,Game1,Code1,1,10.00,1.00,11.00,2023-01-01T10:00:00Z\n"
            + "2,102,Game2,Code2,2,20.00,2.00,22.00,2023-01-02T10:00:00Z";
    long tag = 1L;

    rabbitListenerUtil.consumeMessage(message, channel, tag);

    verify(channel).basicAck(tag, false);
    verify(gameSalesRepository).saveAllAndFlush(anyList());
  }

  @Test
  void testConsumeMessage_Failure() throws IOException, TimeoutException {
    String message = "invalid,data";
    long tag = 1L;

    when(rabbitListenerUtil.createThreadPoolExecutor())
        .thenThrow(new RuntimeException("Database error"));

    rabbitListenerUtil.consumeMessage(message, channel, tag);

    verify(channel).basicNack(tag, false, true);
  }

  @Test
  void testProcessChunk_NewRecords() {
    List<String> chunk =
        Arrays.asList(
            "1,101,Game1,Code1,1,10.00,1.00,11.00,2023-01-01T10:00:00Z",
            "2,102,Game2,Code2,2,20.00,2.00,22.00,2023-01-02T10:00:00Z");

    rabbitListenerUtil.processChunk(chunk);

    ArgumentCaptor<List<GameSales>> captor = ArgumentCaptor.forClass(List.class);
    verify(gameSalesRepository).saveAllAndFlush(captor.capture());

    List<GameSales> savedRecords = captor.getValue();
    assertEquals(2, savedRecords.size());
  }

  @Test
  void testAckRabbitMq_ChannelOpen() throws IOException, TimeoutException {
    when(channel.isOpen()).thenReturn(true);

    rabbitListenerUtil.ackRabbitMq(channel, 1L);

    verify(channel).basicAck(1L, false);
    verify(channel).close();
  }

  @Test
  void testAckRabbitMq_ChannelClosed() throws IOException, TimeoutException {
    when(channel.isOpen()).thenReturn(false);

    rabbitListenerUtil.ackRabbitMq(channel, 1L);

    verify(channel, never()).basicAck(anyLong(), anyBoolean());
    verify(channel, never()).close();
  }

  @Test
  void testNackRabbitMq_ChannelOpen() throws IOException, TimeoutException {
    when(channel.isOpen()).thenReturn(true);

    rabbitListenerUtil.nackRabbitMq(channel, 1L);

    verify(channel).basicNack(1L, false, true);
    verify(channel).close();
  }

  @Test
  void testNackRabbitMq_ChannelClosed() throws IOException, TimeoutException {
    when(channel.isOpen()).thenReturn(false);

    rabbitListenerUtil.nackRabbitMq(channel, 1L);

    verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    verify(channel, never()).close();
  }
}
