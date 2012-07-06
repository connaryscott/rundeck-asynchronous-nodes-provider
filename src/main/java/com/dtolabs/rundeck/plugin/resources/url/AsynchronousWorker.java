package com.dtolabs.rundeck.plugin.resources.url;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Properties;

import java.io.*;

import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceFactory;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;


import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import com.dtolabs.rundeck.core.resources.format.ResourceFormatParserException;
import com.dtolabs.rundeck.core.resources.format.ResourceXMLFormatParser;
import com.dtolabs.rundeck.core.common.INodeSet;

import com.dtolabs.rundeck.core.plugins.Plugin;

//@Plugin(name="genericurlprovider", service="ResourceModelSourceFactory")
public class AsynchronousWorker implements ResourceModelSourceFactory {
    private boolean initialized = false;

    
    private UrlWorker worker;
    public UrlWorker getUrlWorker() {
       return this.worker;
    }
    private Framework framework;

    public AsynchronousWorker(final Framework framework) {
       this.framework = framework;
    }
    public AsynchronousWorker() {
    }


   public ResourceModelSource createResourceModelSource(Properties configuration) throws ConfigurationException {

      // figure out how to take a basic url of xml and map it back to a RMS object
      return (ResourceModelSource)null;
   }



    //public void initialize() throws Exception {
    public void initialize() {
        if (initialized == true) {
           return;
        }
 
        System.out.println("Start Work"  + new java.util.Date());
        //ExecutorService es = Executors.newFixedThreadPool(3);
        ExecutorService es = Executors.newFixedThreadPool(1);

        Future<String> futureResult = null;

        //UrlWorker worker = null;
        worker = new UrlWorker();
        Future<String> future = es.submit(worker);

        System.out.println("... sleep then try to do something while the work is being done....");
        System.out.println("future done 1 ?: " + future.isDone()); 
        System.out.println("refresh 1 ?: " + worker.isDataRefreshed()); 

        // first time we wait until data refresh has occurs
        synchronized (worker) {
           while (!worker.isDataRefreshed()) {
              try {
                System.out.println("wait() refresh 2 ?: " + worker.isDataRefreshed()); 
                worker.wait();
              } catch (InterruptedException e) { 
                e.printStackTrace();
                System.out.println("caught InterruptedException"); 
              } finally {
                System.out.println("wait() refresh 2.5 ?: " + worker.isDataRefreshed()); 
              }
           }
        }
        initialized = true;

        //System.out.println("volatile xml: " + worker.getVolatileResourcesXml()); 

        // subsequent times

/*
        while (true) {
          try {
             Thread.sleep(20000);
          } catch (InterruptedException e) {
             e.printStackTrace();
             throw new Exception("caught and threw InterruptedException");
          }
          System.out.println("volatile xml: " + worker.getVolatileResourcesXml()); 
        }
*/

    }



/*
    //@Plugin(name="genericurlprovider", service="ResourceModelSource")
    @Plugin(name="genericurlprovider", service="ResourceModelSource")
    public class XMLResourceModelSource implements ResourceModelSource {

       public XMLResourceModelSource(final Properties configuration) {

          AsynchronousWorker asynchronousWorker = new AsynchronousWorker();
          asynchronousWorker.initialize();
       }

       public synchronized INodeSet getNodes() throws ResourceModelSourceException {

      //ResourceXMLFormatParser rxfp = new ResourceXMLFormatParser();
      // or InputStream not a file as we see here
      //INodeSet iNodeSet = rxfp.parseDocument(new File(RESOURCES_XML));

          ResourceXMLFormatParser resourceFormatParser = new ResourceXMLFormatParser();

          InputStream is = null;
          INodeSet iNodeSet = null;
          try {
             is = new ByteArrayInputStream(worker.getVolatileResourcesXml().getBytes("UTF-8"));
             iNodeSet = resourceFormatParser.parseDocument(is);
          } catch (IOException e) {
             e.printStackTrace();
             throw new ResourceModelSourceException("caught IOException");
          } catch (ResourceFormatParserException e) {
             e.printStackTrace();
             throw new ResourceModelSourceException("caught ResourceFormatParserException");
          }

          return iNodeSet;
       }

   }
*/

    public static void main(String[] args) throws Exception {
       AsynchronousWorker asynchronousWorker = new AsynchronousWorker();
       asynchronousWorker.initialize();
       System.out.println("volatile xml: " + asynchronousWorker.worker.getVolatileResourcesXml()); 
    }
    
}
