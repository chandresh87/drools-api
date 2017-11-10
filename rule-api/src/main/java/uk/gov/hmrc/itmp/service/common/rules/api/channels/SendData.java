package uk.gov.hmrc.itmp.service.common.rules.api.channels;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.runtime.Channel;

/**
 * <strong>This class provides implementation for the channel.</strong>
 *
 * <p>A channel provides a mechanism to send objects from the working memory to some external
 * process or function.
 *
 * <p>It is used for collecting all the data inserted as a part of rule RHS and passed through
 * channel.
 *
 * <p>Rule engine automatically register this channel in session with name send-channel before
 * firing the rule.
 *
 * <p>Channels are invoked from the consequence side of a rule:
 *
 * <pre>
 * Simple example showing how to use channel in rules.
 *
 * when
 *   ...
 * then
 *   channels["send-channel"].send($object);
 * </pre>
 *
 * @since 1.0.0
 */
public class SendData implements Channel {

  //List of new facts inserted
  private List<Object> newObjectInsterted = new ArrayList<>();

  private Logger log = LogManager.getLogger(this);

  @Override
  public void send(Object object) {

    newObjectInsterted.add(object);
    log.debug("inserted new fact in channels" + object.toString());
  }

  /**
   * This method returns the object inserted in channel.
   *
   * @return new object inserted in channel
   */
  public List<Object> getNewObjectInsterted() {
    return newObjectInsterted;
  }
}
