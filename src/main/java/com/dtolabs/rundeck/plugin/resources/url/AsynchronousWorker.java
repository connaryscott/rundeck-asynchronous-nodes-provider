package com.dtolabs.rundeck.plugin.resources.url;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;


public class AsynchronousWorker {
    public static final Logger logger = Logger.getLogger(AsynchronousWorker.class);
    private boolean initialized = false;

    private String resourcesUrl;
    private int refreshInterval;
    
    private UrlWorker worker;
    public UrlWorker getUrlWorker() {
       return this.worker;
    }

    public AsynchronousWorker(final String resourcesUrl, final int refreshInterval) {
       this.resourcesUrl = resourcesUrl;
       this.refreshInterval = refreshInterval;
    }


    public void initialize() {
        if (initialized == true) {
           Util.logit("initialized is true, immediately returning");
           logger.debug("initialized is true, immediately returning");
           return;
        }
        Util.logit("initialized is false, continuing");
        logger.debug("initialized is false, continuing");
 
        Util.logit("Start Work"  + new java.util.Date());
        logger.debug("Start Work"  + new java.util.Date());
        ExecutorService es = Executors.newFixedThreadPool(1);

        Future<String> futureResult = null;

        worker = new UrlWorker(this.resourcesUrl, this.refreshInterval);
        Future<String> future = es.submit(worker);

        Util.logit("future done ?: " + future.isDone()); 
        logger.debug("future done ?: " + future.isDone()); 

        // first time we wait until data refresh has occurs
        synchronized (worker) {
           Util.logit("worker refreshed ?: " + worker.isDataRefreshed()); 
           logger.debug("worker refreshed ?: " + worker.isDataRefreshed()); 
           while (!worker.isDataRefreshed()) {
              try {
                Util.logit("wait() worker refresh 2 ?: " + worker.isDataRefreshed()); 
                logger.debug("wait() worker refresh 2 ?: " + worker.isDataRefreshed()); 
                worker.wait();
              } catch (InterruptedException e) { 
                e.printStackTrace();
                logger.error("caught InterruptedException"); 
              } finally {
                Util.logit("wait() refresh 3 ?: " + worker.isDataRefreshed()); 
                logger.debug("wait() refresh 3 ?: " + worker.isDataRefreshed()); 
              }
           }
        }
        initialized = true;
        Util.logit("initialization complete, leaving");
        logger.debug("initialization complete, leaving");

    }

    public static void main(String[] args) throws Exception {
       AsynchronousWorker asynchronousWorker = new AsynchronousWorker("http://localhost/resources.xml", 30);
       asynchronousWorker.initialize();
       System.out.println("volatile xml: " + asynchronousWorker.worker.getVolatileResourcesXml()); 
    }
    
}
