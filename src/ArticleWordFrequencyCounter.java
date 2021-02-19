import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;
import java.net.MalformedURLException;
import java.net.URL;

public class ArticleWordFrequencyCounter {

  private static ConcurrentHashMap<String, Integer> countMultiThreaded(String filePath) throws FileNotFoundException, java.io.IOException 
  {
	  // TODO: Use a better threadpool size
      ExecutorService executor = Executors.newFixedThreadPool(10); //creating a pool of 10 threads 
      ConcurrentHashMap<String, Integer> wordCounts = new ConcurrentHashMap<>(); 
      try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
         String line;

         while ((line = br.readLine()) != null) {
             Runnable worker = new CounterThread(line, wordCounts);
             executor.execute(worker);
         }
      }
      executor.shutdown();  
      while (!executor.isTerminated()) {   } 
      
      return wordCounts;
  }
  
  public static void main(String[] args)  throws FileNotFoundException, java.io.IOException {
     String filePath = args[0];
     ConcurrentHashMap<String, Integer> wordCounts = countMultiThreaded(filePath);
     // print wordCounts in required format
  }
    
}

class CounterThread implements Runnable {
    private String url_string;
    ConcurrentHashMap<String, Integer> wordCounts = new ConcurrentHashMap<>();
    
    public CounterThread(String url, ConcurrentHashMap<String, Integer> wordCounts) {
        this.url_string = url;
        this.wordCounts = wordCounts;
    }

    public void run() {
          for(String s : getWords(loadResponse())){
        	  if(this.wordCounts.containsKey(s)) {
        		  wordCounts.put(s, wordCounts.get(s) + 1);
        	  }
          }
    }
    
    
    // TODO: Use a better way to get words from response and replace the logic in here
    private String[] getWords(String response) {
    	return response.split(" ");
    }
    
    private String loadResponse() {
      InputStream is = null;
      BufferedReader br;
      String line;
      String response = "";

      try {
          URL url = new URL(this.url_string);
          is = url.openStream();
          br = new BufferedReader(new InputStreamReader(is));

          while ((line = br.readLine()) != null) {
              response += line;
          }
      } catch (MalformedURLException mue) {
          mue.printStackTrace();
      } catch (IOException ioe) {
          ioe.printStackTrace();
      } finally {
          try {
              if (is != null) is.close();
          } catch (IOException ioe) {
              // nothing to see here
          }
      }
      
      return response;
  }
}


