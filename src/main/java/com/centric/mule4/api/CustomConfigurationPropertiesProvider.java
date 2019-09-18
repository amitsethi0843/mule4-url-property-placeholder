package com.centric.mule4.api;

import static java.lang.String.format;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.DefaultConfigurationPropertiesProvider;
import org.mule.runtime.config.internal.dsl.model.config.ConfigurationPropertiesException;

public class CustomConfigurationPropertiesProvider extends DefaultConfigurationPropertiesProvider {

	public CustomConfigurationPropertiesProvider(String fileLocation, ResourceProvider resourceProvider) {
		super(fileLocation, resourceProvider);
		// TODO Auto-generated constructor stub
	}

	@Override
	 public void initialise() throws InitialisationException{
		if (!fileLocation.endsWith(PROPERTIES_EXTENSION) && !fileLocation.endsWith(YAML_EXTENSION)) {
		      throw new RuntimeException("Configuration properties file must end with yaml or properties extension");
		    }
		 try (InputStream is = getResourceInputStream()) {
		      if (is == null) {
		        throw new RuntimeException("Couldn't find configuration properties file neither on classpath or in file system");
		      }
		      super.readAttributesFromFile(is);
		    } 
		     catch (Exception ex) {
		     System.out.println(ex.getMessage());
		    }
	}
	
	@Override
	  public String getDescription() {
	    ComponentLocation location = (ComponentLocation) getAnnotation(LOCATION_KEY);
	    return format("<custom-configuration-properties file=\"%s\"> - file: %s, line number: %s", fileLocation,
	                  location.getFileName().orElse(UNKNOWN),
	                  location.getLineInFile().map(String::valueOf).orElse("unknown"));

	  }
	
	private InputStream getResourceInputStream() throws IOException {
		if(fileLocation.contains("http")) {
			return new URL(fileLocation).openStream();
		}
		else {
	    return isAbsolutePath(fileLocation) ? new FileInputStream(fileLocation) : resourceProvider.getResourceAsStream(fileLocation);
		}
	}
	
	private boolean isAbsolutePath(String file) {
	    return new File(file).isAbsolute();
	  }

}
