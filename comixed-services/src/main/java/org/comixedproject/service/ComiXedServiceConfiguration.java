package org.comixedproject.service;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComiXedServiceConfiguration {
  private static final String INCLUDE_MARVEL_RULE = "rules/smartlist-rule.drl";

  @Bean
  public KieContainer kieContainer() {
    final KieServices kieServices = KieServices.Factory.get();
    final KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
    kieFileSystem.write(ResourceFactory.newClassPathResource(INCLUDE_MARVEL_RULE));
    final KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
    kieBuilder.buildAll();
    final KieModule kieModule = kieBuilder.getKieModule();
    return kieServices.newKieContainer(kieModule.getReleaseId());
  }
}
