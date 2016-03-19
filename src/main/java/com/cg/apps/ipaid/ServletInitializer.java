package com.cg.apps.ipaid;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(basePackages = "com")
@EnableMongoRepositories
@EnableAutoConfiguration
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(IPaidApplication.class);
	}

	@Bean(name = "org.dozer.Mapper")
	public Mapper mapper() {
		final DozerBeanMapper dozerBean = new DozerBeanMapper();
		List<String> mappingFileUrls = new ArrayList<String>();
		mappingFileUrls.add("mapping.xml");
		dozerBean.setMappingFiles(mappingFileUrls);
		return dozerBean;
	}
}
