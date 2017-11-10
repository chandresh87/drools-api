/** */
package com.app.test;

import static org.testng.Assert.assertNotNull;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import uk.gov.hmrc.itmp.service.common.rules.api.config.RulesConfig;

/** @author chandresh.mishra */
@ContextConfiguration(classes = RulesConfig.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class BaseTest extends AbstractTestNGSpringContextTests {

  @BeforeClass
  protected void setUp() {
    assertNotNull(this.applicationContext);
  }
}
