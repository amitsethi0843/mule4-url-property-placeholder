package com.centric.mule4.api;

import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;
import static org.mule.runtime.api.meta.Category.SELECT;
import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import org.mule.metadata.api.builder.BaseTypeBuilder;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

public class CustomConfPropertiesExntensionLoadingDelegate implements ExtensionLoadingDelegate{

	public static final String EXTENSION_NAME = "Url Properties";
    public static final String CONFIG_ELEMENT = "config";
    
	@Override
	public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {
		 ConfigurationDeclarer configurationDeclarer = extensionDeclarer.named(EXTENSION_NAME)
			        .describedAs(String.format("Crafted %s Extension", EXTENSION_NAME))
			        .withCategory(SELECT)
			        .onVersion("1.0.0")
			        // TODO replace with you company name
			        .fromVendor("Centric")
			        // This defines a global element in the extension with name config
			        .withConfig(CONFIG_ELEMENT);
		  ParameterGroupDeclarer defaultParameterGroup = configurationDeclarer.onDefaultParameterGroup();
		    // TODO you can add/remove configuration parameter using the code below.
		    defaultParameterGroup
		        .withRequiredParameter("filePath").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
		        .withExpressionSupport(NOT_SUPPORTED)
		        .describedAs("Used for reading properties file from the url");
	}

}
