package com.docker.elastic.base;

import com.docker.elastic.pojo.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;

import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public RestHighLevelClient getClient() {
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
        return highClient;
    }

    public void createIndex(RestHighLevelClient highClient, String indexName) {
        try {
            // elastic create index and index data
            String jsonObject = "{\"age\":19,\"dateOfBirth\":1471462076567,"
                    + "\"fullName\":\"Jack Doe\"}";
            IndexRequest request = new IndexRequest(indexName);
            request.source(jsonObject, XContentType.JSON);

            IndexResponse response = highClient.index(request, RequestOptions.DEFAULT);
            String index = response.getIndex();
            long version = response.getVersion();

            System.out.println("checking result: " + response.toString());
            System.out.println("version: " + version);
            System.out.println("index name: " + index);


            // To delete record from elastic index based on index id
            /*DeleteRequest request = new DeleteRequest("index-logger", "_doc", "ZcYBgHkBTvqz3OPx08e0");
            DeleteResponse deleteResponse = highClient.delete(request, COMMON_OPTIONS);
            System.out.println("Delete document successfully! \n" + deleteResponse.toString() + "\n" + deleteResponse.status());*/
        } catch (IOException ioException) {
            // Handle exceptions.
        }

    }



    public List<Person> readAllRec(int startDocument,RestHighLevelClient highClient, String indexName) {

        final int searchSize = 50;
        final SearchRequest searchRequest = new SearchRequest(indexName);
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        if (startDocument != 0) {
            startDocument += searchSize;
        }

        searchSourceBuilder.from(startDocument);
        searchSourceBuilder.size(searchSize);

        // sort the document
        searchSourceBuilder.sort(new FieldSortBuilder("dateOfBirth").order(SortOrder.ASC));
        searchRequest.source(searchSourceBuilder);

        List<Person> personList = new ArrayList<>();

        try {
            final SearchResponse searchResponse = highClient.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHits hits = searchResponse.getHits();

            // Do you want to know how many documents (results) are returned? here is you get:
            TotalHits totalHits = hits.getTotalHits();
            long numHits = totalHits.value;

            final SearchHit[] searchHits = hits.getHits();

            final ObjectMapper mapper = new ObjectMapper();

            for (SearchHit hit : searchHits) {
                // convert json object to Person
                personList.add(mapper.readValue(hit.getSourceAsString(), Person.class));
            }
        } catch (IOException e) {
            System.out.println("Cannot search by all mach. " + e);
        }
        return personList;
    }



    public static void main(String[] args) {
        DockerElasticConnect dockerElasticConnect = new DockerElasticConnect();
        RestHighLevelClient highClient = dockerElasticConnect.getClient();
        String indexName = "people" ;
        try {
            dockerElasticConnect.createIndex(highClient,indexName);
            List<Person> personList = dockerElasticConnect.readAllRec(0,highClient,indexName);
            for (Person person : personList) {
                System.out.println(person.getAge());
                System.out.println(person.getDateOfBirth());
                System.out.println(person.getFullName());
            }

            highClient.close();

        } catch (IOException ioException) {
            // Handle exceptions.
        }


    }
}
