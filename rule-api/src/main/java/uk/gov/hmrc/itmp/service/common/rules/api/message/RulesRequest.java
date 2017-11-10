package uk.gov.hmrc.itmp.service.common.rules.api.message;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import uk.gov.hmrc.itmp.service.common.rules.api.enums.SessionType;

/**
 * <strong>This class hold the request parameters required by the rule engine.</strong>
 *
 * <p>None of these attributes is mandatory.It has builder to build the immutable object.
 *
 * <pre>
 * It has following attributes:
 *
 * sessionName - KieSession name from Kiemodule.If not provided and buildSessionByKieBase is False then engine will build a default session from container.
 * kieBasename - KieBase name from Kiemodule.If not provided and buildSessionByKieBase is True then engine will build a default kiebase from container.
 * sessionType - Type of session.STATEFUL or STATELESS.If not provided STATEFUL is default.
 * facts - List of facts passes to an engine.If not provided engine will fire rules without facts.
 * globalElement - List of global elements passed to an engine to fire rules.It is not a mandatory field.
 * buildSessionByKieBase - Build session either using a Kiebase or Kiesession name.By default engine will use Kiesseion name to build session.
 * </pre>
 *
 * @since 1.0.0
 */
public class RulesRequest {

  // KieSession name from Kiemodule
  private String sessionName;
  // KieBase name from Kiemodule
  private String kieBasename;
  // Type of session .Stateful or Stateless
  private SessionType sessionType;
  // List of facts passes to session.
  private List<Object> facts;
  // Map of global elements if present in rule.
  private Map<String, Object> globalElement;
  // Build session by using kiebase name or kieSession name.
  private boolean buildSessionByKieBase;

  private RulesRequest(RulesRequestBuilder builder) {

    this.sessionName = builder.sessionName;
    this.kieBasename = builder.kieBasename;
    this.sessionType = builder.sessionType;
    this.facts = builder.facts;
    this.globalElement = builder.globalElement;
    this.buildSessionByKieBase = builder.buildSessionByKieBase;
  }

  /** Builder pattern implementation to build RulesRequest object. */
  public static class RulesRequestBuilder {

    private String sessionName;
    private String kieBasename;
    private SessionType sessionType;
    private List<Object> facts;
    private Map<String, Object> globalElement;
    private boolean buildSessionByKieBase;

    /** Default constructor */
    public RulesRequestBuilder() {
      // default constructor
    }

    /**
     * Set facts in request
     *
     * @param facts facts passed to rule API.
     * @return RulesRequestBuilder
     */
    public RulesRequestBuilder facts(List<Object> facts) {
      this.facts = facts;
      return this;
    }

    /**
     * Set buildSessionByKieBase in request
     *
     * @param buildSessionByKieBase Build session either by session name or kiebase name.
     * @return RulesRequestBuilder
     */
    public RulesRequestBuilder buildSessionByKieBase(boolean buildSessionByKieBase) {
      this.buildSessionByKieBase = buildSessionByKieBase;
      return this;
    }

    /**
     * Set sessionName in request
     *
     * @param sessionName Session name
     * @return RulesRequestBuilder
     */
    public RulesRequestBuilder sessionName(String sessionName) {
      this.sessionName = sessionName;
      return this;
    }

    /**
     * Set sessionType in request
     *
     * @param sessionType Type of session. Stateful or stateless.
     * @return RulesRequestBuilder
     */
    public RulesRequestBuilder sessionType(SessionType sessionType) {
      this.sessionType = sessionType;
      return this;
    }

    /**
     * Set globalElement in request
     *
     * @param globalElement Map of global elements passes to an API.
     * @return RulesRequestBuilder
     */
    public RulesRequestBuilder globalElement(Map<String, Object> globalElement) {
      this.globalElement = globalElement;
      return this;
    }

    /**
     * Set kieBasename in request
     *
     * @param kieBasename Name of Kiebase.
     * @return RulesRequestBuilder
     */
    public RulesRequestBuilder kieBasename(String kieBasename) {
      this.kieBasename = kieBasename;
      return this;
    }

    /**
     * Build the RulesRequest object by calling private constructor in RulesRequest class.
     *
     * @return RulesRequest object.
     */
    public RulesRequest build() {

      return new RulesRequest(this);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RulesRequest [sessionName=");
    builder.append(sessionName);
    builder.append(", kieBasename=");
    builder.append(kieBasename);
    builder.append(", sessionType=");
    builder.append(sessionType);
    builder.append(", facts=");
    builder.append(facts);
    builder.append(", globalElement=");
    builder.append(globalElement);
    builder.append(", buildSessionByKieBase=");
    builder.append(buildSessionByKieBase);
    builder.append("]");
    return builder.toString();
  }

  /** @return the kieBasename */
  public String getKieBasename() {
    return kieBasename;
  }

  /** @return buildSessionByKieBase */
  public boolean isBuildSessionByKieBase() {
    return buildSessionByKieBase;
  }

  /** @return the sessionName */
  public String getSessionName() {
    return sessionName;
  }

  /** @return the sessionType */
  public SessionType getSessionType() {
    return sessionType;
  }

  /** @return the facts */
  public List<Object> getFacts() {

    if (CollectionUtils.isNotEmpty(facts)) {
      return Collections.unmodifiableList(facts);
    }
    return facts;
  }

  /** @return the globalElement */
  public Map<String, Object> getGlobalElement() {
    if (null != globalElement) {
      return Collections.unmodifiableMap(globalElement);
    }
    return globalElement;
  }
}
