package uk.gov.hmrc.itmp.service.common.rules.api.enums;

import uk.gov.hmrc.itmp.service.common.rules.api.message.RulesRequest;

/**
 * <strong>Enum holding constant for session type- STATEFUL or STATELESS.</strong>
 *
 * <p>It is used in populating RulesRequest object.
 *
 * @see RulesRequest
 * @since 1.0.0
 */
public enum SessionType {
  STATEFUL,
  STATELESS;
}
