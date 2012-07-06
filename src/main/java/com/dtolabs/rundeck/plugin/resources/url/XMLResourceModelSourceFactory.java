package com.dtolabs.rundeck.plugin.resources.url;

import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.*;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

@Plugin(name="genericurlprovider", service="ResourceModelSource")
public class XMLResourceModelSourceFactory implements ResourceModelSourceFactory, Describable {

    public static final String PROVIDER_NAME = "genericurlprovider";
    public static final String RESOURCES_URL_KEY = "resourcesUrlKey";
    public static final String RESOURCES_URL = "http://localhost/resources.xml";

    private Framework framework;

    private static List<Property> descriptionProperties = new ArrayList<Property>();

    public XMLResourceModelSourceFactory(final Framework framework) {
        this.framework = framework;
    }

    public ResourceModelSource createResourceModelSource(final Properties properties) throws ConfigurationException {
        final XMLResourceModelSource xmlResourceModelSource = new XMLResourceModelSource(properties);
        //ec2ResourceModelSource.validate();
        return xmlResourceModelSource;
    }
 

    static {
        descriptionProperties.add(PropertyUtil.string(RESOURCES_URL_KEY, "resources url", "source resource url", false, RESOURCES_URL));
    }


    static Description DESC = new Description() {
        public String getName() {
            return PROVIDER_NAME;
        }

        public String getTitle() {
            return "generic url xml resources provider";
        }

        public String getDescription() {
            return "produce cached resources xml data";
        }

        public List<Property> getProperties() {
            return descriptionProperties;
        }

        public Map<String, String> getPropertiesMapping() {
            return null;
        }
    };

    public Description getDescription() {
        return DESC;
    }
}
