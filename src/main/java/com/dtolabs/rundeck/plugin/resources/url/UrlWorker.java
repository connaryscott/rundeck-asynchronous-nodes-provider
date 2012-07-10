package com.dtolabs.rundeck.plugin.resources.url;
import java.net.*;
import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;


public class UrlWorker  implements Callable<String> {

    public static final Logger logger = Logger.getLogger(UrlWorker.class);

    public String call() throws Exception {
       //System.out.println("url: " + this.resourcesUrl);
       logger.debug("resourcesUrl: " + this.resourcesUrl);
       this.doWork();
       return resourcesXml;
    }

    // data refresh variable to notify calling thread when new data refresh occurs
    private volatile boolean dataRefreshed = false;
    public synchronized boolean isDataRefreshed() {
       return dataRefreshed;
    }

    // set refresh with a default to not notify calling thread
    private synchronized void setDataRefreshed(boolean b) {
       setDataRefreshed(b, false);
    }
    // set refersh and to notify or not to notify calling thread
    private synchronized void setDataRefreshed(boolean b, boolean notify) {
       dataRefreshed = b;
       if (notify) notifyAll();
    }


    // this working thread never really finishes, however, the methods are here if needed.
    private boolean done = false;
    public boolean workerDone() {
       return done;
    }
    private void setDone(boolean b) {
       done = b;
    }

    // some url that takes "forever and a day" to respond, such as that url used with the puppet/rundeck plugin. 
    private String resourcesUrl;

    // wait time in between url downloads
    private int refreshInterval;
    
    public UrlWorker(String resourcesUrl, int refreshInterval) {
       this.resourcesUrl = resourcesUrl;
       this.refreshInterval = refreshInterval;
    }

    // the currently stored resourcesXml
    private volatile String resourcesXml;
    public void setResourcesXml(String xml) {
       resourcesXml = xml;
    }

    // provide a method to get what we currently have in memory, it may not be authoritative, but is immediately available.
    public String getVolatileResourcesXml() {
       return resourcesXml;
    }

    // we really are not using this since we do not require waiting on a future event returning the data since this respresents a single background thread which continually runs.
    // Instead, we synchronize on the isDataRefreshed() method, which during initialization, allows us to get synchronous behavior.
    // After that, we depend on asynchronous behavior thru getVolatileResourcesXml().
    public Future<String> getResourcesXml() {
       return new Future<String>() { 

            public String get() throws InterruptedException, ExecutionException {
               return new String(resourcesXml);
            }

            public String get(final long l, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
               return new String(resourcesXml);
            }

            public boolean isDone() {
                return workerDone();
            }

            public boolean isCancelled() {
                return false;
            }

            public boolean cancel(boolean b) {
                return false;
            }

       };
    }

    
    // this is the worker thread which periodically gets new resourcesXml data
    // we use setDataRefreshed() method to notify the watching thread when new data comes in and is useful for synchronous behavior when needed.
    public void doWork() throws Exception {

          URL url = null;


          while (true) {

             logger.debug("polling resourcesUrl: " + this.resourcesUrl);
             // reset referesh to false and do not notify calling thread 
             setDataRefreshed(false, false);
   
             StringBuffer sb = new StringBuffer();
             try {
                url = new URL(this.resourcesUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                logger.debug("reading new data");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      
                String inputLine = null;
      
                while ((inputLine = in.readLine()) != null) {
                   sb.append(inputLine+System.getProperty("line.separator"));
                }
                in.close();
      
             } catch(IOException e) {
                e.printStackTrace();   
                throw new Exception("caught and threw IOException");
             }
   
             // set resourcesXml string, referesh to true, and notify calling thread 
             setResourcesXml(sb.toString());
                logger.debug("refresh done");
             setDataRefreshed(true, true);
       
             // polling loop wait time
             logger.debug("sleeping for " + this.refreshInterval + " + seconds");
             try {
                Thread.sleep(this.refreshInterval*1000);
             } catch (InterruptedException e) {
                e.printStackTrace();   
                throw new Exception("caught and threw InterruptedException");
             }
   
          }

     }

    // take a test drive here, otherwise unused
    public static void main(String[] args) throws Exception {
        UrlWorker worker = new UrlWorker("http://localhost/resources.xml", 30);
        worker.doWork();
        System.out.println(worker.getResourcesXml().get());
        System.out.println("... try to do something while the work is being done....");
        
        System.out.println("End work" + new java.util.Date());
        System.exit(0);
    }



}
