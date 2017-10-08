/** */
package com.rules.application.controllers;

import com.rules.application.service.RulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author chandresh.mishra */
@RestController
public class ApplicationController {

  @Autowired private RulesService rulesService;

  @GetMapping(path = "/fireRules", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Integer> fireRule() {

    int rulesFired = rulesService.fireAppRules();
    return new ResponseEntity<>(rulesFired, HttpStatus.OK);
  }
}
