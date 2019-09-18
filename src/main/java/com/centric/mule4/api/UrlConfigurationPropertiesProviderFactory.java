package com.centric.mule4.api;

import static com.centric.mule4.api.CustomConfPropertiesExntensionLoadingDelegate.CONFIG_ELEMENT;
import static com.centric.mule4.api.CustomConfPropertiesExntensionLoadingDelegate.EXTENSION_NAME;

import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;


public class UrlConfigurationPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {
	
	public static final String EXTENSION_NAMESPACE = EXTENSION_NAME.toLowerCase().replace(" ", "-");
	public static final ComponentIdentifier CUSTOM_PROPERTIES_PROVIDER = 
			ComponentIdentifier.builder().namespace(EXTENSION_NAMESPACE).name(CONFIG_ELEMENT).build();
	@Override
	public ComponentIdentifier getSupportedComponentIdentifier() {
		// TODO Auto-generated method stub
		return CUSTOM_PROPERTIES_PROVIDER;
	}

	@Override
	public CustomConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters,
			ResourceProvider externalResourceProvider) {
		String file = parameters.getStringParameter("filePath");
		return new CustomConfigurationPropertiesProvider(file,externalResourceProvider);
	}

}
