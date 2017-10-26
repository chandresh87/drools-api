/** */
package rules.api.config;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import rules.api.exception.RulesApiException;

/**
 * This class is responsible for rule engine Java based configuration.
 *
 * @author chandresh.mishra
 */
@Configuration
@ComponentScan(basePackages = {"rules.api.*", "rules.api"})
public class RulesConfig {

  private Logger logger = LogManager.getLogger(this);

  /**
   * Bean used for property place holder.It is used by spring to populate values from property file.
   *
   * @return PropertySourcesPlaceholderConfigurer
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /**
   * This method initialise the container with given group ,artifact and version. It will verify all
   * the rules loaded from the kieJar and throw an exception in case of syntax error
   *
   * @return KieContainer
   */
  @Bean
  public KieContainer getKieContainer() {

    logger.traceEntry("START - method - [getKieContainer()]");

    logger.info("Building Container");

    KieServices kieService = KieServices.Factory.get();

    //Building container using kiejar from repository
    KieContainer kContainer = kieService.newKieClasspathContainer();

    //Verifying all the rules loaded in container
    Results results = kContainer.verify();

    //checking for errors in rule file.
    if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {

      List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);

      for (Message message : messages) {

        logger.error(
            "Compilation errors in rules: {} in file {} at line number  {} and coloumn {}.Error message is: {}",
            message.getLevel(),
            message.getPath(),
            message.getLine(),
            message.getColumn(),
            message.getText());
      }

      throw new RulesApiException(
          "Compilation errors are found in the rules file. Please check the logs.");
    }

    kContainer
        .getKieBaseNames()
        .stream()
        .map(
            (kieBase) -> {
              logger.info(">> Loading KieBase: " + kieBase);
              return kieBase;
            })
        .forEach(
            (kieBase) -> {
              kContainer
                  .getKieSessionNamesInKieBase(kieBase)
                  .stream()
                  .forEach(
                      (kieSession) -> {
                        logger.info("\t >> Containing KieSession: " + kieSession);
                      });
            });
    logger.traceExit("END - method - [getKieContainer()]");
    return kContainer;
  }
}
