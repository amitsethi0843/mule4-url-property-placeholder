package com.centric.mule4.api;

import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

public class CustomConfigurationProperty implements ConfigurationProperty {

	private Object source;
	private Object rawValue;
	private String key;
	
	public CustomConfigurationProperty(Object source, String key, Object rawValue) {
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
