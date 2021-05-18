package com.docker.elastic.base;

import org.apache.http.HttpHost;

import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DockerElasticConnect {

    private static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

        // The default cache size is 100 MB. Change it to 30 MB.
        builder.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory
                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    public static void main(String[] args) {
        // Use basic access authentication for the Elasticsearch cluster.
        /*final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("{Username}", "{Password}"));*/

        // Create a Java REST client by using the builder and configure HttpClientConfigCallback for the HTTP client.

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder;
                        //return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });

        // Create a RestHighLevelClient instance by using the REST low-level client builder.
        RestHighLevelClient highClient = new RestHighLevelClient(builder);

        try {
            // Create a request.
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("1", "value1");
            jsonMap.put("2", "value2");

            IndexRequest indexRequest = new IndexRequest("index-logger", "_doc").source(jsonMap);
            // to auto-increment index id
            indexRequest.opType(DocWriteRequest.OpType.INDEX);

            // Run the following command and use the custom configuration of RequestOptions:
            IndexResponse indexResponse = highClient.index(indexRequest, COMMON_OPTIONS);
            DocWriteResponse.Result result = indexResponse.getResult();
            System.out.println("checking result: "+result.toString());

            // To delete record from elastic index based on index id
            /*DeleteRequest request = new DeleteRequest("index-logger", "_doc", "ZcYBgHkBTvqz3OPx08e0");
            DeleteResponse deleteResponse = highClient.delete(request, COMMON_OPTIONS);
            System.out.println("Delete document successfully! \n" + deleteResponse.toString() + "\n" + deleteResponse.status());*/

            highClient.close();

        } catch (IOException ioException) {
            // Handle exceptions.
        }
    }
}
