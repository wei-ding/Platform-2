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

package org.clinical3po.backendservices.server.handler.loader;

import org.clinical3po.backendservices.server.Clinical3POServer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by w.ding on 9/22/2015.
 */
public class MenuLoader extends Loader {
    static public String menuFolder = "menu";
    static CloseableHttpClient httpclient = null;

    public static void loadForm() throws Exception {
        File folder = getFileFromResourceFolder(menuFolder);
        if(folder != null) {
            Clinical3POServer.start();
            httpclient = HttpClients.createDefault();

            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                loadMenuFile(listOfFiles[i]);
            }

            Clinical3POServer.stop();
            httpclient.close();
        }
    }

    private static void loadMenuFile(File file) {
        Scanner scan = null;
        try {
            scan = new Scanner(file, Loader.encoding);
            // the content is only the data portion. convert to map
            String content = scan.useDelimiter("\\Z").next();
            HttpPost httpPost = new HttpPost("http://injector:8080/api/rs");
            StringEntity input = new StringEntity(content);
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
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            /*
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            OSchema schema = db.getMetadata().getSchema();
            for (OClass oClass : schema.getClasses()) {
                System.out.println(oClass.getName());
            }
            String formId = file.getName();
            try {
                db.begin();
                // remove the document for the class if there are any.
                OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select from Form where id = ?");
                List<ODocument> result = db.command(query).execute(formId);
                for (ODocument form : result) {
                    form.delete();
                }
                ODocument doc = new ODocument(schema.getClass("Form"));
                doc.field("id", formId);
                doc.field("content", content);
                doc.save();
                db.commit();
                System.out.println("Form " + formId + " is loaded!");
            } catch (Exception e) {
                db.rollback();
                e.printStackTrace();
            } finally {
                db.close();
            }
            */
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (scan != null) scan.close();
        }
    }
}
