package com.centric.mule4.api;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Optional.of;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.mule.runtime.api.component.Component;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.mule.runtime.config.api.dsl.model.properties.DefaultConfigurationPropertiesProvider;
import org.yaml.snakeyaml.Yaml;

public class CustomConfigurationPropertiesProvider extends DefaultConfigurationPropertiesProvider {

	  protected static final String PROPERTIES_EXTENSION = ".properties";
	  protected static final String YAML_EXTENSION = ".yaml";
	  protected static final String UNKNOWN = "unknown";
	  
	  protected final Map<String, ConfigurationProperty> configurationAttributes = new HashMap<>();
	  protected String fileLocation;
	  protected ResourceProvider resourceProvider;

	  
	public CustomConfigurationPropertiesProvider(String fileLocation, ResourceProvider resourceProvider) {
		super(fileLocation,resourceProvider);
		this.fileLocation=fileLocation;
		this.resourceProvider=resourceProvider;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	  public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
	    return Optional.ofNullable(configurationAttributes.get(configurationAttributeKey));
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
		      readAttributesFromFile(is);
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

	
	protected void readAttributesFromFile(InputStream is) throws IOException {
	    if (fileLocation.endsWith(PROPERTIES_EXTENSION)) {
	      Properties properties = new Properties();
	      properties.load(is);
	      properties.keySet().stream().map(key -> {
	        Object rawValue = properties.get(key);
	        rawValue = createValue((String) key, (String) rawValue);
	        return new DefaultConfigurationProperty(of(this), (String) key, rawValue);
	      }).forEach(configurationAttribute -> {
	        configurationAttributes.put(configurationAttribute.getKey(), configurationAttribute);
	      });
	    } else {
	      Yaml yaml = new Yaml();
	      Iterable<Object> yamlObjects = yaml.loadAll(is);
	      yamlObjects.forEach(yamlObject -> {
	        createAttributesFromYamlObject(null, null, yamlObject);
	      });
	    }
	  }

	  protected void createAttributesFromYamlObject(String parentPath, Object parentYamlObject, Object yamlObject) {
	    if (yamlObject instanceof List) {
	      List list = (List) yamlObject;
	      if (list.get(0) instanceof Map) {
	        list.forEach(value -> createAttributesFromYamlObject(parentPath, yamlObject, value));
	      } else {
	        if (!(list.get(0) instanceof String)) {
	          throw new RuntimeException("List of complex objects are not supported as property values. Offending key is ");
	        }
	        String[] values = new String[list.size()];
	        list.toArray(values);
	        String value = join(",", list);
	        configurationAttributes.put(parentPath, new DefaultConfigurationProperty(this, parentPath, value));
	      }
	    } else if (yamlObject instanceof Map) {
	      if (parentYamlObject instanceof List) {
	        throw new RuntimeException("Configuration properties does not support type a list of complex types. Complex type keys are: ");
	      }
	      Map<String, Object> map = (Map) yamlObject;
	      map.entrySet().stream()
	          .forEach(entry -> createAttributesFromYamlObject(createKey(parentPath, entry.getKey()), yamlObject, entry.getValue()));
	    } else {
	      if (!(yamlObject instanceof String)) {
	        throw new RuntimeException("YAML configuration properties only supports string values, make sure to wrap the value with so you force the value to be an string.");
	      }
	      String resultObject = createValue(parentPath, (String) yamlObject);
	      configurationAttributes.put(parentPath, new DefaultConfigurationProperty(this, parentPath, resultObject));
	    }
	  }

	  protected String createKey(String parentKey, String key) {
	    if (parentKey == null) {
	      return key;
	    }
	    return parentKey + "." + key;
	  }

	  protected String createValue(String key, String value) {
	    return value;
	  }
	  
	  
	  public class DefaultConfigurationProperty implements ConfigurationProperty {

		  private Object source;
		  private Object rawValue;
		  private String key;

		  /**
		   * Creates a new configuration value
		   *
		   * @param source the source of this configuration attribute. For instance, it may be an {@link Component} if it's source was
		   *        defined in the artifact configuration or it may be the deployment properties configured at deployment time.
		   * @param key the key of the configuration attribute to reference it.
		   * @param rawValue the plain configuration value without resolution. A configuration value may contain reference to other
		   *        configuration attributes.
		   */
		  public DefaultConfigurationProperty(Object source, String key, Object rawValue) {
		    this.source = source;
		    this.rawValue = rawValue;
		    this.key = key;
		  }


		  @Override
		  public Object getSource() {
		    return source;
		  }

		  @Override
		  public Object getRawValue() {
		    return rawValue;
		  }

		  @Override
		  public String getKey() {
		    return key;
		  }

		}
}
