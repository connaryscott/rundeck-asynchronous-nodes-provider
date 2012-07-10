package com.dtolabs.rundeck.plugin.resources.url;

import java.io.*;
import java.util.Properties;
import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import com.dtolabs.rundeck.core.resources.format.ResourceFormatParserException;
import com.dtolabs.rundeck.core.resources.format.ResourceFormatParserService;
import com.dtolabs.rundeck.core.resources.format.ResourceFormatParser;
import com.dtolabs.rundeck.core.resources.format.UnsupportedFormatException;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.common.INodeSet;
import org.apache.log4j.Logger;

public class ResourceModelSourceProvider implements ResourceModelSource {

    private ResourceFormatParser resourceFormatParser; 

    public static final Logger logger = Logger.getLogger(ResourceModelSourceProvider.class);

   private AsynchronousWorker asynchronousWorker;
   public ResourceModelSourceProvider(Framework framework, final Properties configuration) throws UnsupportedFormatException {

        logger.debug("constructing ResourceModelSourceProvider");

      String resourcesFormat = configuration.getProperty(AsynchronousResourceModelSourceFactory.RESOURCES_FORMAT_KEY);
      String resourcesUrl = configuration.getProperty(AsynchronousResourceModelSourceFactory.RESOURCES_URL_KEY);
      int refreshInterval = Integer.parseInt(configuration.getProperty(AsynchronousResourceModelSourceFactory.REFRESH_INTERVAL_KEY));
      this.asynchronousWorker = new AsynchronousWorker(resourcesUrl, refreshInterval);
      this.asynchronousWorker.initialize();
      ResourceFormatParser resourceFormatParser = null;
      logger.debug("RESOURCES_FORMAT_KEY is: " +  AsynchronousResourceModelSourceFactory.RESOURCES_FORMAT_KEY);
      logger.debug("resourcesFormat: " +  resourcesFormat);
      resourceFormatParser = new ResourceFormatParserService(framework).getParserForFileExtension(resourcesFormat);
      this.resourceFormatParser = resourceFormatParser;
   }

   public synchronized INodeSet getNodes() throws ResourceModelSourceException {

        logger.debug("entering ResourceModelSourceProvider.getNodes()");

      InputStream is = null;
      INodeSet iNodeSet = null;
      try {
         is = new ByteArrayInputStream(this.asynchronousWorker.getUrlWorker().getVolatileResourcesXml().getBytes("UTF-8"));
         iNodeSet = this.resourceFormatParser.parseDocument(is);
      } catch (IOException e) {
         e.printStackTrace();
         logger.error("Caught IOException: " + e.getMessage());
         throw new ResourceModelSourceException(e);
      } catch (ResourceFormatParserException e) {
         logger.error("Caught ResourceFormatParserException: " + e.getMessage());
         throw new ResourceModelSourceException(e);
      }

        logger.debug("leaving ResourceModelSourceProvider.getNodes()");
      return iNodeSet;
   }

    public void validate() throws ConfigurationException {
       if (false) throw new ConfigurationException("never thrown");
    }

}
