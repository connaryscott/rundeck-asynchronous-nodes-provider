package com.dtolabs.rundeck.plugin.resources.url;
import java.net.*;
import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;


public class UrlWorker  implements Callable<String> {

    public String call() throws Exception {
       System.out.println("url: " + RESOURCES_URL);
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

    // test url, try a puppet url here
    public static String RESOURCES_URL = "http://localhost/resources.xml";
    // default wait time in between url downloads
    public static int WAIT_TIME_URL = 30;
    
    public UrlWorker() {
    }

    private volatile String resourcesXml;
    public void setResourcesXml(String xml) {
       resourcesXml = xml;
    }
    public String getVolatileResourcesXml() {
       return resourcesXml;
    }

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

    
    public void doWork() throws Exception {

          URL url = null;


          while (true) {

             // reset referesh to false and do not notify calling thread 
             setDataRefreshed(false, false);
   
             StringBuffer sb = new StringBuffer();
             try {
                url = new URL(RESOURCES_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
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
   
             // set resources string, referesh to true, and notify calling thread 
             setResourcesXml(sb.toString());
             setDataRefreshed(true, true);
       
             // polling loop wait time
             try {
                Thread.sleep(WAIT_TIME_URL*1000);
             } catch (InterruptedException e) {
                e.printStackTrace();   
                throw new Exception("caught and threw InterruptedException");
             }
   
          }

     }

    // take a test drive here, otherwise unused
    public static void main(String[] args) throws Exception {
        UrlWorker worker = new UrlWorker();
        worker.doWork();
        System.out.println(worker.getResourcesXml().get());
        System.out.println("... try to do something while the work is being done....");
        
        System.out.println("End work" + new java.util.Date());
        System.exit(0);
    }



}
