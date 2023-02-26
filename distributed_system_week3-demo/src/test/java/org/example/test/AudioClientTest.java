	package org.example.test;
	
	import static org.hamcrest.MatcherAssert.assertThat;
	import static org.hamcrest.Matchers.equalTo;
	
	import java.util.Dictionary;
	import java.util.HashMap;
	import java.util.Random;
	import java.util.concurrent.ExecutionException;
	import java.util.concurrent.TimeUnit;
	import java.util.concurrent.TimeoutException;
	
	import org.junit.jupiter.api.Test;
	
	import org.apache.commons.io.IOUtils;
	import org.apache.http.client.methods.HttpPost;
	import org.apache.http.entity.StringEntity;
	import org.eclipse.jetty.client.HttpClient;
	import org.eclipse.jetty.client.api.ContentProvider;
	import org.eclipse.jetty.client.api.ContentResponse;
	import org.eclipse.jetty.client.api.Request;
	import org.eclipse.jetty.client.util.FormContentProvider;
	import org.eclipse.jetty.client.util.StringContentProvider;
	import org.eclipse.jetty.http.HttpMethod;
	import org.eclipse.jetty.util.Fields;
	import org.eclipse.jetty.util.Fields.Field;
	import org.jfree.chart.ChartFactory;
	import org.jfree.chart.ChartUtilities;
	import org.jfree.chart.JFreeChart;
	import org.jfree.chart.plot.PlotOrientation;
	import org.jfree.data.category.DefaultCategoryDataset;
	import java.io.File;
	
	import com.google.gson.Gson;
	
	
	import org.example.model.Audio;
	
	public class AudioClientTest {
	
		private static final String BASE_URL = "http://localhost:9090/coen6317/audio";
		private static final int NUM_CLIENTS_10 = 10;
		private static final int NUM_CLIENTS_50 = 50;
		private static final int NUM_CLIENTS_100 = 100;
	
		@SuppressWarnings("deprecation")
		@Test
		void testConcurrentRequests() throws Exception {
		    HttpClient client = new HttpClient();
		    client.start();
	
		    long[][][] results = new long[3][3][100];
	
		    // ratio of GET and POST requests
		    int[][] ratios = { { 2, 1 }, { 5, 1 }, { 10, 1 } };
	
		    // number of clients
		    int[] numClients = { NUM_CLIENTS_10, NUM_CLIENTS_50, NUM_CLIENTS_100 };
	
		    for (int r = 0; r < ratios.length; r++) {
		        for (int c = 0; c < numClients.length; c++) {
		            long[][] clientResults = new long[100][numClients[c]];
		            Thread[] threads = new Thread[numClients[c]];
		            for (int i = 0; i < numClients[c]; i++) {
		                threads[i] = new Thread(new RequestTask(client, clientResults, i, ratios[r], numClients));
		            }
		            for (int i = 0; i < numClients[c]; i++) {
		                threads[i].start();
		            }
		            for (int i = 0; i < numClients[c]; i++) {
		                threads[i].join();
		            }
		            // Calculate average time taken for each request
		            for (int i = 0; i < 100; i++) {
		                long sum = 0;
		                for (int j = 0; j < numClients[c]; j++) {
		                    sum += clientResults[i][j];
		                }
		                double avgTime = (double) sum / numClients[c];
		                results[r][c][i] = (long) avgTime;
		            }
		        }
		    }
	
		    client.stop();

			DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
		    // Plot line chart
		    for (int r = 0; r < ratios.length; r++) {
		        System.out.println("Ratio of GET to POST requests: " + ratios[r][0] + ":" + ratios[r][1]);
		        for (int c = 0; c < numClients.length; c++) {
		            System.out.println("Number of clients: " + numClients[c]);
		            for (int i = 0; i < numClients[c]; i++) {
		                System.out.println("Average time for request " + (i + 1) + " = " + results[r][c][i] + " ms");
						dataset1.addValue(results[r][c][i], "Ratio " + ratios[r][0] + " / " + (ratios[r][1]), numClients[c] + " clients");
		            }
		        }
		    }



			// Generate and save line chart
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			/*for (int r = 0; r < ratios.length; r++) {
				for (int c = 0; c < numClients.length; c++) {
					long[] clientResultsForRatioAndNumClients = null ;;//= results[r * numClients.length + c];
					for (int i = 0; i < clientResultsForRatioAndNumClients.length; i++) {
						dataset.addValue(clientResultsForRatioAndNumClients[i], "Ratio " + ratios[r][0] + " / " + (ratios[r][1]), numClients[c] + " clients");
					}
				}
			}*/

			JFreeChart lineChart = ChartFactory.createLineChart(
					"Concurrency vs. Response Time",
					"Number of Clients",
					"Response Time (ms)",
					dataset1,
					PlotOrientation.VERTICAL,
					true,
					true,
					false
			);

			int chartWidth = 640; /* Width of the chart */
			int chartHeight = 480; /* Height of the chart */
			ChartUtilities.saveChartAsPNG(new File("chart"+ + Math.random()+".png"), lineChart, chartWidth, chartHeight);


		}
		public class RequestTask implements Runnable {
		    private final HttpClient client;
		    private final long[][] results;
		    private final int clientId;
		    private final int[] ratio;
		    private final int[] numClients;
	
		    public RequestTask(HttpClient client, long[][] results, int clientId, int[] ratio, int[] numClients) {
		        this.client = client;
		        this.results = results;
		        this.clientId = clientId;
		        this.ratio = ratio;
		        this.numClients = numClients;
		    }
	
		    @Override
		    public void run() {
		        try {
		            long[] clientResults = new long[100];
		            Random rand = new Random();
	
		            for (int i = 0; i < 100; i++) {
		                int getRatio = ratio[0];
		                int postRatio = ratio[1];
		                int randNum = rand.nextInt(getRatio + postRatio);
	
		                if (randNum < getRatio) {
		                    int propOrJson = rand.nextInt(2);
	
		                    if (propOrJson == 0) {
		                        long startTime = System.currentTimeMillis();
		                        
		                        Random r = new Random();
		                        int low = 1;
		                        int high = 4;
		                        int result = r.nextInt(high-low) + low;
		                        String artistName = "artist_name_" + result;
		                        String url = BASE_URL + "?artistName=" + artistName;
		                        ContentResponse response = client.newRequest(url).method(HttpMethod.GET).send();
		                        System.out.println("Status for GET request " + response.getStatus());
		                        System.out.println("Response for GET request " + response.getContentAsString());
		                        long endTime = System.currentTimeMillis();
		                        clientResults[i] = endTime - startTime;
		                    } else {
		                        long startTime = System.currentTimeMillis();
		                        String url = BASE_URL + "/json";
		                        ContentResponse response = client.newRequest(url).method(HttpMethod.GET).send();
		                        System.out.println("Status for GET request " + response.getStatus());
		                        System.out.println("Response for GET request " + response.getContentAsString());
		                        long endTime = System.currentTimeMillis();
		                        clientResults[i] = endTime - startTime;
		                    }
		                } else {
		                    long startTime = System.currentTimeMillis();
		                    String url = BASE_URL;
		                    Request request = client.newRequest(url).method(HttpMethod.POST);
	                        Random r = new Random();
	                        int low = 1;
	                        int high = 40;
	                        int result = r.nextInt(high-low) + low;
		                    
		                    Fields dict = new Fields();
		                    dict.put("artistName", "artist_name_" + result);
		                    dict.put("trackTitle", "TEST2" + result);
		                    dict.put("numofCopiesSold","34" + result);
		                    dict.put("numberOfReviews","34" + result);
		                    dict.put("year","34"+ result);
		                    dict.put("trackNumber","34" + result);
		                    dict.put("albumTitle","fsdfdsfds"+result);
		                    
		                    
		                    ContentResponse response= client.POST(url).content(new FormContentProvider(dict)).send();
		                   
		                    System.out.println("Status for POST request " + response.getStatus());
		                    System.out.println("Response for POST request " + response.toString());
		                    long endTime = System.currentTimeMillis();
		                    clientResults[i] = endTime - startTime;
		                }
		            }
	
		            results[clientId] = clientResults;
	
		            // Generate and save line chart
/*
		            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		            for (int r = 0; r < ratio.length; r++) {
		                for (int c = 0; c < numClients.length; c++) {
		                    long[] clientResultsForRatioAndNumClients = results[r * numClients.length + c];
		                    for (int i = 0; i < clientResultsForRatioAndNumClients.length; i++) {
		                        dataset.addValue(clientResultsForRatioAndNumClients[i], "Ratio " + ratio[r] + " / " + (100 - ratio[r]), numClients[c] + " clients");
		                    }
		                }
		            }

		            JFreeChart lineChart = ChartFactory.createLineChart(
		                    "Concurrency vs. Response Time",
		                    "Number of Clients",
		                    "Response Time (ms)",
		                    dataset,
		                    PlotOrientation.VERTICAL,
		                    true,
		                    true,
		                    false
		            );

		            int chartWidth = 640; */
/* Width of the chart *//*

		            int chartHeight = 480; */
/* Height of the chart *//*

		            ChartUtilities.saveChartAsPNG(new File("chart"+ + Math.random()+".png"), lineChart, chartWidth, chartHeight);
*/

		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		}
	
	
		}