/* Project: continuum
 * File: MessageDaoImpl.java
 * Created: Apr 20, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.mapping.MessageMappingException;
import org.springframework.integration.store.MessageStoreException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Component("jdbcChannel")
public class JdbcPubSubChannel extends PublishSubscribeChannel
{
  private static final Logger LOGGER     =
      LoggerFactory.getLogger(JdbcPubSubChannel.class);
  private static final String MESSAGE_ID = "messageId";

  private final String serverNodeIdentifier;

  private final ObjectMapper            objectMapper;
  private final JdbcTemplate            jdbcTemplate;
  private final TransactionTemplate     tx;
  private final Supplier<ZonedDateTime> nowSource;

  @Autowired
  public JdbcPubSubChannel(final JdbcTemplate inJdbcTemplate,
                           @Qualifier("objectMapper")
                           final ObjectMapper inObjectMapper,
                           final TransactionTemplate inTx,
                           @Value("${server.node.identifier}")
                           final String inServerNodeIdentifier)
  {
    this(inJdbcTemplate, inObjectMapper, inTx, inServerNodeIdentifier, ZonedDateTime::now);
  }

  protected JdbcPubSubChannel(final JdbcTemplate inJdbcTemplate,
                              final ObjectMapper inObjectMapper,
                              final TransactionTemplate inTx,
                              final String inServerNodeIdentifier,
                              final Supplier<ZonedDateTime> inNowSource)
  {
    jdbcTemplate = inJdbcTemplate;
    objectMapper = inObjectMapper;
    tx = inTx;
    serverNodeIdentifier = inServerNodeIdentifier;
    nowSource = inNowSource;
  }

  @Override
  protected boolean doSend(final Message<?> message, final long timeout)
  {
    super.doSend(message, timeout);
    try
    {
      tx.setTimeout(timeout == -1 ? -1 : (int)timeout / 1000);
      return tx.execute(txn -> {
        if (!doSend(message))
        {
          txn.setRollbackOnly();
          return false;
        }
        return true;
      });
    }
    finally {
      tx.setTimeout(-1);
    }
  }

  private boolean doSend(final Message<?> message)
  {
    String messageId = UUID.randomUUID().toString();

    final String headersAsString;
    final String payloadTypeAsString = message.getPayload().getClass().getName();
    final String payloadAsString;
    try
    {
      headersAsString = objectMapper.writeValueAsString(Collections.unmodifiableMap(message.getHeaders()));
      payloadAsString = objectMapper.writeValueAsString(message.getPayload());
    }
    catch (JsonProcessingException e)
    {
      throw new MessageMappingException("Could not convert message", e);
    }

    try
    {
      jdbcTemplate
          .update("insert into message(id, headers, payload_class, payload, timestamp) " +
                  "values (?,?,?,?,current_timestamp)",
              ps ->
              {
                ps.setString(1, messageId);
                ps.setString(2, headersAsString);
                ps.setString(3, payloadTypeAsString);
                ps.setString(4, payloadAsString);
              });
      acknowledgeMessage(messageId);
    }
    catch (QueryTimeoutException e)
    {
      LOGGER.warn("Timeout writing message to database",e);
      return false;
    }

    return true;
  }

  @Scheduled(fixedDelay = 5000)
  @Transactional
  protected synchronized void poll()
  {
    Iterable<Message<?>> messages = jdbcTemplate.query(
        "select m.id as message_id, m.headers, m.payload_class, m.payload " +
        "from message m " +
        "where m.id not in (" +
        "select message_id " +
        "from message_receipt " +
        "where server_node_identifier = ?" +
        ") order by m.timestamp desc ",
        ps -> ps.setString(1, serverNodeIdentifier),
        (r, n) ->
        {
          try
          {
            Object payload = objectMapper.readValue(r.getString("payload"), Class.forName(r.getString("payload_class")));
            Map<String, Object> headers = objectMapper.readValue(r.getString("headers"), new TypeReference<Map<String,Object>>() {});
            headers.put(MESSAGE_ID, r.getString("message_id"));
            return new GenericMessage<>(payload, headers);
          }
          catch (IOException | ClassNotFoundException e)
          {
            throw new MessageStoreException("Unable to recreate message from database", e);
          }
        }
    );
    for (Message<?> message : messages) {
      super.doSend(message, (long) -1);
      acknowledgeMessage((String)message.getHeaders().get(MESSAGE_ID));
    }
  }

  @Scheduled(initialDelay = 0, fixedRate = 3_600_000)
  @Transactional
  protected void purgeOldMessages()
  {
    Date cutoff = new Date(1000 * nowSource.get().minusDays(1).toEpochSecond());
    jdbcTemplate.update(
        "delete from message_receipt where message_id in (select id from message where timestamp < ?)",
        ps -> ps.setDate(1, cutoff));
    jdbcTemplate.update(
        "delete from message where timestamp < ?",
        ps -> ps.setDate(1, cutoff));
  }

  private void acknowledgeMessage(String messageId)
  {
    try
    {
      jdbcTemplate
          .update("INSERT INTO message_receipt(message_id, server_node_identifier) VALUES (?,?)",
              ps ->
              {
                ps.setString(1, messageId);
                ps.setString(2, serverNodeIdentifier);
              });
    } catch (DuplicateKeyException e) {
      LOGGER.warn("Message already acknowledged: " + messageId);
    }
  }

}
