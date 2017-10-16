package com.sample;

import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;

public class DroolsTest {

	
	public static void main(String args[])
	{
		getKieContainer();
	}
	
	  public static KieContainer getKieContainer() {

	    System.out.println("START - method - [getKieContainer()]");

	    System.out.println("Building Container");

	    KieServices kieService = KieServices.Factory.get();

	    //Building container using kiejar from repository
	    KieContainer kContainer = kieService.newKieClasspathContainer();

	    //Verifying all the rules loaded in container
	    Results results = kContainer.verify();

	    //checking for errors in rule file.
	    if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {

	      List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);

	      for (Message message : messages) {

	    	  System.out.println(
	            "Compilation errors in rules: {} in file {} at line number  {} and coloumn {}.Error message is: {}"+
	            message.getLevel()+
	            message.getPath()+
	            message.getLine()+
	            message.getColumn()+
	            message.getText());
	      }

	   
	    }

	    kContainer
	        .getKieBaseNames()
	        .stream()
	        .map(
	            (kieBase) -> {
	            	System.out.println(">> Loading KieBase: " + kieBase);
	              return kieBase;
	            })
	        .forEach(
	            (kieBase) -> {
	              kContainer
	                  .getKieSessionNamesInKieBase(kieBase)
	                  .stream()
	                  .forEach(
	                      (kieSession) -> {
	                    	  System.out.println("\t >> Containing KieSession: " + kieSession);
	                      });
	            });
	    System.out.println("END - method - [getKieContainer()]");
	    return kContainer;
	  }
}
