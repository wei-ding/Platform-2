/*
 * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.clinical3po.backendservices.server.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.clinical3po.backendservices.server.Clinical3POServer;
import org.clinical3po.backendservices.util.ServiceLocator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 8/25/2015.
 */
public class RestHandlerTest extends TestCase {
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"w.ding\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";

    // user @rid will be getting from jwt token
    String logOutAdmin = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"logOutUser\",\"data\":{\"host\":\"example\"}}";

    String getUserByUserId = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"getUser\",\"data\":{\"host\":\"example\",\"userId\":\"w.ding\"}}";
    String logOutUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"logOutUser\",\"data\":{\"host\":\"example\",\"userId\":\"w.ding\"}}";
    String delUser = "{\"readOnly\": false, \"category\": \"user\", \"name\": \"delUser\", \"data\": {\"host\":\"example\",\"userId\":\"w.ding\"}}";

    String signUpJson = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signUpUser\",\"data\":{\"host\":\"example\",\"userId\":\"w.ding\",\"email\":\"vvei.ding@gmail.com\",\"password\":\"abcdefg\",\"passwordConfirm\":\"abcdefg\",\"firstName\":\"w.ding\",\"lastName\":\"hu\"}}";
    String getUserByEmail = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"getUser\",\"data\":{\"host\":\"example\",\"email\":\"vvei.ding@gmail.com\"}}";

    String signInJsonEmail = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"vvei.ding@gmail.com\",\"password\":\"abcdefg\",\"rememberMe\":false}}";
    String signInByUserId = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"clientId\":\"example@Browser\",\"userIdEmail\":\"w.ding\",\"password\":\"abcdefg\",\"rememberMe\":true}}";

    String updPasswordJson = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updPassword\",\"data\":{\"host\":\"example\",\"userId\":\"w.ding\",\"password\":\"abcdefg\",\"newPassword\":\"123456\",\"passwordConfirm\":\"123456\"}}";
    String signInJsonNewPass = "{\"readOnly\": true, \"category\": \"user\", \"name\": \"signInUser\", \"data\": {\"host\":\"example\",\"userIdEmail\":\"w.dingu@gmail.com\", \"password\": \"123456\"}}";

    String updProfileJson = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updProfile\",\"data\":{\"host\":\"example\",\"userId\":\"w.ding\",\"firstName\":\"Wei\",\"lastName\":\"Ding\"}}";


    String addJson = "{\"readOnly\": false, \"category\": \"form\", \"name\": \"addForm\", \"data\": {\"host\":\"example\",\"id\": \"org.clinical3po.backendservices.common.test.json\", \"schema\": {\"type\": \"object\", \"title\": \"Comment\",\"properties\": { \"name\":  { \"title\": \"Name\",\"type\": \"string\"}, \"email\":  {\n        \"title\": \"Email\",\n        \"type\": \"string\",\n        \"pattern\": \"^\\\\S+@\\\\S+$\",\n        \"description\": \"Email will be used for evil.\"\n      },\n      \"comment\": {\n        \"title\": \"Comment\",\n        \"type\": \"string\",\n        \"maxLength\": 20,\n        \"validationMessage\": \"Don't be greedy!\"\n      }\n    },\n    \"required\": [\"name\",\"email\",\"comment\"]\n  },\n  \"form\": [\n    \"name\",\n    \"email\",\n    {\n      \"key\": \"comment\",\n      \"type\": \"textarea\"\n    },\n    {\n      \"type\": \"submit\",\n\t  \"style\": \"btn-info\",\n      \"title\": \"OK\"} ]}}";
    String getJson = "{\"readOnly\": true, \"category\": \"form\", \"name\": \"getForm\", \"data\": {\"host\":\"example\",\"id\":\"org.clinical3po.backendservices.common.test.json\"}}";
    String updJson = "{\"readOnly\": false, \"category\": \"form\", \"name\": \"updForm\", \"data\": {\"host\":\"example\",\"id\": \"org.clinical3po.backendservices.common.test.json\", \"version\": 0, \"schema\": {\"type\": \"object\", \"title\": \"Updated Comment\",\"properties\": { \"name\":  { \"title\": \"Name\",\"type\": \"string\"}, \"email\":  {\n        \"title\": \"Email\",\n        \"type\": \"string\",\n        \"pattern\": \"^\\\\S+@\\\\S+$\",\n        \"description\": \"Email will be used for evil.\"\n      },\n      \"comment\": {\n        \"title\": \"Comment\",\n        \"type\": \"string\",\n        \"maxLength\": 20,\n        \"validationMessage\": \"Don't be greedy!\"\n      }\n    },\n    \"required\": [\"name\",\"email\",\"comment\"]\n  },\n  \"form\": [\n    \"name\",\n    \"email\",\n    {\n      \"key\": \"comment\",\n      \"type\": \"textarea\"\n    },\n    {\n      \"type\": \"submit\",\n\t  \"style\": \"btn-info\",\n      \"title\": \"OK\"} ]}}";
    String getAllJson = "{\"readOnly\": true, \"category\": \"form\", \"name\": \"getAllForm\"}";
    String delJson = "{\"readOnly\": false, \"category\": \"form\", \"name\": \"delForm\", \"data\": {\"host\":\"example\",\"id\":\"org.clinical3po.backendservices.common.test.json\", \"version\": 1}}";

    String getMenuJson = "{\"readOnly\": true, \"category\": \"menu\", \"name\": \"getMenu\", \"data\": {\"host\":\"example\"}}";

    CloseableHttpClient httpclient = null;
    String ownerToken = null;

    public RestHandlerTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(RestHandlerTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        Clinical3POServer.start();
        httpclient = HttpClients.createDefault();
        // get owner token here
        signInOwner();
        super.setUp();
    }

    public void tearDown() throws Exception {
        Clinical3POServer.stop();
        httpclient.close();
        super.tearDown();
    }

    /*
    public void testRewrite() throws Exception {
        HttpGet get = new HttpGet("http://example:8080/page/a");
        HttpResponse response = httpclient.execute(get);
        Assert.assertEquals(StatusCodes.OK, response.getStatusLine().getStatusCode());
    }
    */

    public void testUser() throws Exception {

        cleanUpUser();
        signUpUser();
        // sleep 100 ms in order to make sure that signUp user is available in this
        // db connection. Orientdb sometimes needs to be synched between two calls
        Thread.sleep(100);
        signInUser();
        /*
        postGetUserByUserId();
        postGetUserByEmail();
        postAddForm();
        postGetForm();
        getForm();
        postUpdForm();
        postGetFormVerifyUpdate();
        postGetAllForm();
        postDelForm();
        postGetFormVerifyDelete();
        */
    }

    /*
    public void testForm() throws Exception {
        postAddForm();
        postGetForm();
        getForm();
        postUpdForm();
        postGetFormVerifyUpdate();
        postGetAllForm();
        postDelForm();
        postGetFormVerifyDelete();
    }
    */
    /*
    public void testMenu() throws Exception {
        postGetMenu();

    }
    */

    /**
     * Set ownerToken in case any action uses it to do update. For example delUser
     * @throws Exception
     */
    private void signInOwner() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(signInOwner);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            Map<String, Object> jsonMap = ServiceLocator.getInstance().getMapper().readValue(json,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            ownerToken = "Bearer " + (String)jsonMap.get("accessToken");
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    /**
     * Delete test user in order to run the same signUp again and again.
     * @throws Exception
     */
    private void cleanUpUser() throws Exception {
        StatusLine statusLine = null;
        // getUser and check status
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getUserByUserId);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        httpPost.setHeader("Authorization", ownerToken);

        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        // delete user if it exists.
        if(statusLine.getStatusCode() == 200) {
            delUser();
        }
    }

    /**
     * Delete user in clean up above if the test user exists
     * @throws Exception
     */
    private void delUser() throws Exception {
        // getUser and check status
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(delUser);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        httpPost.setHeader("Authorization", ownerToken);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    /**
     * SignUp a new test user
     * @throws Exception
     */
    private void signUpUser() throws Exception {
        // getUser and check status
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(signUpJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    /**
     * Login as the new test user
     * @throws Exception
     */
    private void signInUser() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(signInByUserId);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            // make sure there is an accessToken in the json.
            assertTrue(json.contains("accessToken"));
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    private void postGetUserByUserId() throws Exception {
        System.out.println("postGetUserByUserId starts");
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getUserByUserId);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postGetUserByUserId ends");
    }

    private void postGetUserByEmail() throws Exception {
        System.out.println("postGetUserByEmail starts");
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getUserByEmail);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postGetUserByEmail ends");
    }

    private void postGetMenu() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getMenuJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
            Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            List<Map<String, Object>> result = (List<Map<String, Object>>) jsonMap.get("data");
            assertTrue(result.size() == 5);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }


    private void getForm() {

        /*
        HttpGet httpGet = new HttpGet("http://targethost/homepage");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        */
        boolean result = true;
        assertTrue(result);
    }

    private void postAddForm() throws Exception {

        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(addJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            assertEquals("{\"data\":\"success\"}", json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    private void postGetForm() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
            Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            Map<String, Object> result = (Map<String, Object>) jsonMap.get("data");
            assertTrue(result.size() == 3);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    private void postUpdForm() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(updJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            assertEquals("{\"data\":\"success\"}", json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    private void postGetFormVerifyUpdate() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
            Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            Map<String, Object> result = (Map<String, Object>) jsonMap.get("data");
            assertTrue(result.size() == 3);

            Map<String, Object> schema = (Map<String, Object>)result.get("schema");
            String title = (String)schema.get("title");
            assertEquals(title, "Updated Comment");

            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    private void postGetAllForm() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getAllJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    private void postDelForm() throws Exception {
        System.out.println("postDelForm starts");
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(delJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            assertEquals("{\"data\":\"success\"}", json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postDelForm ends");
    }

    private void postGetFormVerifyDelete() throws Exception {
        System.out.println("postGetFormVerifyDelete starts");

        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            assertTrue(json.contains("cannot be found"));

            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postGetFormVerifyDelete ends");
    }

}
