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
           return;
        }
 
        //System.out.println("Start Work"  + new java.util.Date());
        logger.info("Start Work"  + new java.util.Date());
        ExecutorService es = Executors.newFixedThreadPool(1);

        Future<String> futureResult = null;

        worker = new UrlWorker(this.resourcesUrl, this.refreshInterval);
        Future<String> future = es.submit(worker);

        logger.info("future done ?: " + future.isDone()); 
        logger.info("refresh 1 ?: " + worker.isDataRefreshed()); 

        // first time we wait until data refresh has occurs
        synchronized (worker) {
           while (!worker.isDataRefreshed()) {
              try {
                logger.info("wait() refresh 2 ?: " + worker.isDataRefreshed()); 
                worker.wait();
              } catch (InterruptedException e) { 
                e.printStackTrace();
                logger.error("caught InterruptedException"); 
              } finally {
                logger.info("wait() refresh 2.5 ?: " + worker.isDataRefreshed()); 
              }
           }
        }
        initialized = true;

    }

    public static void main(String[] args) throws Exception {
       AsynchronousWorker asynchronousWorker = new AsynchronousWorker("http://localhost/resources.xml", 30);
       asynchronousWorker.initialize();
       System.out.println("volatile xml: " + asynchronousWorker.worker.getVolatileResourcesXml()); 
    }
    
}
