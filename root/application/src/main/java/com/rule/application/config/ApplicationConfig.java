/** */
package com.rule.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/** @author chandresh.mishra */
@Configuration
@EnableWebMvc
@ComponentScan(value = {"com.rules.application.*"})
public class ApplicationConfig {}
