package com.dtolabs.rundeck.plugin.resources.url;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class AsynchronousWorker {
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
 
        System.out.println("Start Work"  + new java.util.Date());
        ExecutorService es = Executors.newFixedThreadPool(1);

        Future<String> futureResult = null;

        worker = new UrlWorker(this.resourcesUrl, this.refreshInterval);
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

    }

    public static void main(String[] args) throws Exception {
       AsynchronousWorker asynchronousWorker = new AsynchronousWorker("http://localhost/resources.xml", 30);
       asynchronousWorker.initialize();
       System.out.println("volatile xml: " + asynchronousWorker.worker.getVolatileResourcesXml()); 
    }
    
}
