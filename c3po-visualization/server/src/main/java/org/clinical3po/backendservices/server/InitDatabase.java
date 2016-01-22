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

package org.clinical3po.backendservices.server;

import org.clinical3po.backendservices.util.HashUtil;
import org.clinical3po.backendservices.util.ServiceLocator;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by w.ding on 9/4/2015.
 */
public class InitDatabase {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(InitDatabase.class);

    public static void main(final String[] args) {
        initDb();
    }

    public static void initDb() {
        logger.debug("Start initdb()");
        initVertex();;
        refreshDoc();
        logger.debug("End initdb()");
    }

    public static void initVertex() {
        OrientGraphNoTx graph = ServiceLocator.getInstance().getGraphNoTx();
        try {

            OrientVertexType status = graph.createVertexType("Status");
            status.createProperty("host", OType.STRING);
            status.createProperty("app", OType.STRING);
            status.createProperty("map", OType.EMBEDDEDMAP);

            OrientVertexType config = graph.createVertexType("Config");
            config.createProperty("host", OType.STRING);
            config.createProperty("app", OType.STRING);
            config.createProperty("category", OType.STRING);
            config.createProperty("name", OType.STRING);
            config.createProperty("version", OType.STRING);
            config.createProperty("configId", OType.STRING);
            config.createProperty("properties", OType.EMBEDDEDMAP);
            config.createProperty("createDate", OType.DATETIME);
            config.createProperty("updateDate", OType.DATETIME);

            OrientVertexType role = graph.createVertexType("Role");
            role.createProperty("roleId", OType.STRING);
            role.createProperty("host", OType.STRING);
            role.createProperty("description", OType.STRING);
            role.createProperty("createDate", OType.DATETIME);
            role.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("roleId", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Role"));

            OrientVertexType user = graph.createVertexType("User");
            user.createProperty("host", OType.STRING);
            user.createProperty("userId", OType.STRING);
            user.createProperty("email", OType.STRING);
            user.createProperty("firstName", OType.STRING);
            user.createProperty("lastName", OType.STRING);
            user.createProperty("karma", OType.INTEGER);
            user.createProperty("locked", OType.BOOLEAN);
            user.createProperty("roles", OType.EMBEDDEDLIST);
            user.createProperty("credential", OType.LINK);
            user.createProperty("createDate", OType.DATETIME);
            user.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("userId", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "User"));
            graph.createKeyIndex("email", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "User"));

            OrientVertexType credential = graph.createVertexType("Credential");
            credential.createProperty("password", OType.STRING);
            // each client (host + device) has an entry with a list of refresh tokens up to 10.
            // when 11 refresh token is created, the first one is removed for the client.
            // there is no expire date for refresh token unless it is removed by log out action from user.
            credential.createProperty("clientRefreshTokens", OType.EMBEDDEDMAP);

            // activation code is in another table and it should be populated in the Rule not evRule as this
            // won't be populated during replay. Every time this table is populated, it will try to remove
            // entries older than 48 hours to keep this table smaller.
            OrientVertexType activation = graph.createVertexType("Activation");
            activation.createProperty("userId", OType.STRING);
            activation.createProperty("code", OType.STRING);
            activation.createProperty("createDate", OType.DATETIME);
            graph.createKeyIndex("userId", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Activation"));


            OrientVertexType client = graph.createVertexType("Client");
            // www.clinical3po.org@Browser www.clinical3po.org@Android www.clinical3po.org@iOS
            client.createProperty("clientId", OType.STRING);
            client.createProperty("type", OType.STRING); // 0 - CONFIDENTIAL, 1 - PUBLIC
            client.createProperty("secret", OType.STRING);
            client.createProperty("description", OType.STRING);
            client.createProperty("createDate", OType.DATETIME);
            client.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("clientId", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Client"));

            OrientVertexType event = graph.createVertexType("Event");
            event.createProperty("eventId", OType.LONG);
            event.createProperty("host", OType.STRING);
            event.createProperty("app", OType.STRING);
            event.createProperty("category", OType.STRING);
            event.createProperty("name", OType.STRING);
            event.createProperty("version", OType.STRING);
            event.createProperty("data", OType.EMBEDDEDMAP);
            event.createProperty("createDate", OType.DATETIME);
            graph.createKeyIndex("eventId", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Event"));

            OrientVertexType rule = graph.createVertexType("Rule");
            rule.createProperty("ruleClass", OType.STRING);
            rule.createProperty("host", OType.STRING);
            rule.createProperty("sourceCode", OType.STRING);
            rule.createProperty("isPublisher", OType.BOOLEAN);
            rule.createProperty("isSubscriber", OType.BOOLEAN);
            rule.createProperty("enableCors", OType.BOOLEAN);
            rule.createProperty("corsHosts", OType.STRING); // concat a list of hosts that can access this rule through cors.
            rule.createProperty("enableEtag", OType.BOOLEAN);
            rule.createProperty("cacheControl", OType.STRING);   // Cache-Control header string. example max-age=3600
            /*
             * For each rule class you can define a form id or a json schema for request validation
             * before the endpoint is called. If the end point is a form action then this schema is
             * auto populated when form is add/import/update/delete. Otherwise, it needs to be populated
             * manually with json schema.
             *
             * Note: the validation is for the data payload of the command only.
             *
             */
            rule.createProperty("schema",OType.EMBEDDEDMAP); // validation schema
            rule.createProperty("createDate", OType.DATETIME);
            rule.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("ruleClass", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Rule"));

            OrientVertexType menuItem = graph.createVertexType("MenuItem");
            menuItem.createProperty("menuItemId", OType.STRING);    // unique id
            menuItem.createProperty("label", OType.STRING);
            menuItem.createProperty("host", OType.STRING);  // host  some common menuItems have no host.
            menuItem.createProperty("path", OType.STRING);
            menuItem.createProperty("tpl", OType.STRING);
            menuItem.createProperty("ctrl", OType.STRING);
            menuItem.createProperty("left", OType.BOOLEAN); // on the left side of nav bar if true
            menuItem.createProperty("roles", OType.EMBEDDEDLIST);
            menuItem.createProperty("createDate", OType.DATETIME);
            menuItem.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("menuItemId", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "MenuItem"));

            OrientVertexType menu = graph.createVertexType("Menu");
            menu.createProperty("host", OType.STRING);    // unique id
            menu.createProperty("createDate", OType.DATETIME);
            menu.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("host", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Menu"));

            OrientVertexType form = graph.createVertexType("Form");
            form.createProperty("formId", OType.STRING);    // unique id
            form.createProperty("host", OType.STRING);  // host
            form.createProperty("action", OType.EMBEDDEDLIST);
            form.createProperty("schema", OType.EMBEDDEDMAP);
            form.createProperty("form", OType.EMBEDDEDLIST);
            form.createProperty("modelData", OType.EMBEDDEDMAP);
            form.createProperty("createDate", OType.DATETIME);
            form.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("formId", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Form"));

            OrientVertexType page = graph.createVertexType("Page");
            page.createProperty("pageId", OType.STRING);    // unique id
            page.createProperty("host", OType.STRING);  // host
            page.createProperty("content", OType.STRING);
            page.createProperty("createDate", OType.DATETIME);
            page.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("pageId", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Page"));

            OrientVertexType transformRequest = graph.createVertexType("TransformRequest");
            transformRequest.createProperty("ruleClass", OType.STRING);
            transformRequest.createProperty("sequence", OType.INTEGER);
            transformRequest.createProperty("transformRule", OType.STRING);
            transformRequest.createProperty("transformData", OType.EMBEDDEDMAP);
            transformRequest.createProperty("createDate", OType.DATETIME);
            transformRequest.createProperty("updateDate", OType.DATETIME);
            transformRequest.createIndex("ReqRuleSequenceIdx", OClass.INDEX_TYPE.UNIQUE, "ruleClass", "sequence");

            OrientVertexType transformResponse = graph.createVertexType("TransformResponse");
            transformResponse.createProperty("ruleClass", OType.STRING);
            transformResponse.createProperty("sequence", OType.INTEGER);
            transformResponse.createProperty("transformRule", OType.STRING);
            transformResponse.createProperty("transformData", OType.EMBEDDEDMAP);
            transformResponse.createProperty("createDate", OType.DATETIME);
            transformResponse.createProperty("updateDate", OType.DATETIME);
            transformResponse.createIndex("ResRuleSequenceIdx", OClass.INDEX_TYPE.UNIQUE, "ruleClass", "sequence");


            OrientVertexType access = graph.createVertexType("Access");
            access.createProperty("ruleClass", OType.STRING);
            access.createProperty("accessLevel", OType.STRING); // A/N/C/R/U/CR/CU/RU/CRU
            access.createProperty("clients", OType.EMBEDDEDLIST); // usually host + device. Browser/Andrioid/iOS
            access.createProperty("roles", OType.EMBEDDEDLIST);
            access.createProperty("users", OType.EMBEDDEDLIST);
            access.createProperty("createDate", OType.DATETIME);
            access.createProperty("updateDate", OType.DATETIME);
            graph.createKeyIndex("ruleClass", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Access"));

            OrientVertexType counter = graph.createVertexType("Counter");
            access.createProperty("name", OType.STRING);
            access.createProperty("value", OType.LONG);
            graph.createKeyIndex("name", Vertex.class, new Parameter("type", "UNIQUE"), new Parameter("class", "Counter"));

            OrientEdgeType create = graph.createEdgeType("Create");
            OrientEdgeType update = graph.createEdgeType("Update");
            OrientEdgeType upVote = graph.createEdgeType("UpVote");
            OrientEdgeType downVote = graph.createEdgeType("DownVote");
            OrientEdgeType own = graph.createEdgeType("Own");
            OrientEdgeType depend = graph.createEdgeType("Depend");
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
    }

    static void refreshDoc() {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            // global config without host. email server config
            Map mailServerMap = new HashMap<String, Object>();
            mailServerMap.put("mail.smtp.starttls.enable", "true");
            mailServerMap.put("mail.smtp.auth", "true");
            mailServerMap.put("mail.smtp.host", "mail.clinical3po.org");
            mailServerMap.put("mail.smtp.port", "587");
            graph.addVertex("class:Config", "category", "email", "name", "server", "properties", mailServerMap);

            // global config registration confirmation
            Map regConfirmMap = new HashMap<String, Object>();
            regConfirmMap.put("subject", "Registration Activation");
            regConfirmMap.put("content", "Hi,<br>Thanks for registering with us.<br>Please use %s to activate your account when you login.");

            graph.addVertex("class:Config", "category", "email", "name", "regConfirm", "properties", regConfirmMap);

            // host specific config for email
            Map Clinical3POMap = new HashMap<String, Object>();
            Clinical3POMap.put("username", "noreply@clinical3po.org"); // update during initial setup
            Clinical3POMap.put("password", ""); // update during initial setup



            graph.addVertex( "class:Role", "roleId", "anonymous", "description", "Anonymous or guest that have readonly access to certain things");
            graph.addVertex( "class:Role", "roleId", "user", "description", "logged in user who can do certain things");
            graph.addVertex( "class:Role", "roleId", "dbAdmin", "description", "admin database objects for the host");
            graph.addVertex( "class:Role", "roleId", "hostAdmin", "description", "admin hosts for the platform");
            graph.addVertex( "class:Role", "roleId", "userAdmin", "description", "admin users for the host");
            graph.addVertex( "class:Role", "roleId", "statusAdmin", "description", "admin status display for the host");
            graph.addVertex( "class:Role", "roleId", "configAdmin", "description", "admin config for the host");
            graph.addVertex( "class:Role", "roleId", "formAdmin", "description", "admin forms for the host");
            graph.addVertex( "class:Role", "roleId", "pageAdmin", "description", "admin pages for the host");
            graph.addVertex( "class:Role", "roleId", "ruleAdmin", "description", "admin rules for the host");
            graph.addVertex( "class:Role", "roleId", "menuAdmin", "description", "admin menus for the host");
            graph.addVertex( "class:Role", "roleId", "admin", "description", "admin every thing for the host");
            graph.addVertex( "class:Role", "roleId", "owner", "description", "owner of the site who can do anything");
            graph.addVertex( "class:Role", "roleId", "betaTester", "description", "Beta Tester that can be routed to differnt version of API");

            List roles = new ArrayList<String>();
            roles.add("owner");
            roles.add("user");
            Vertex credentialOwner = graph.addVertex("class:Credential", "password", HashUtil.generateStorngPasswordHash(ServiceLocator.getInstance().getOwnerPass()));
            Vertex userOwner = graph.addVertex("class:User",
                    "userId", ServiceLocator.getInstance().getOwnerId(),
                    "email", ServiceLocator.getInstance().getOwnerEmail(),
                    "roles", roles,
                    "credential", credentialOwner,
                    "createDate", new java.util.Date());


            roles = new ArrayList<String>();
            roles.add("user");
            Vertex credentialTest = graph.addVertex("class:Credential", "password", HashUtil.generateStorngPasswordHash(ServiceLocator.getInstance().getTestPass()));
            Vertex userTest = graph.addVertex("class:User",
                    "userId", ServiceLocator.getInstance().getTestId(),
                    "host", "example",
                    "email", ServiceLocator.getInstance().getTestEmail(),
                    "roles", roles,
                    "credential", credentialTest,
                    "createDate", new java.util.Date());

            Vertex m_accessAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "accessAdmin",
                    "label", "Access Admin",
                    "path", "/page/org.clinical3po.backendservices-v-access-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_accessAdmin);

            Vertex m_hostAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "hostAdmin",
                    "label", "Host Admin",
                    "path", "/page/org.clinical3po.backendservices-v-host-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_hostAdmin);

            Vertex m_pageAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "pageAdmin",
                    "label", "Page Admin",
                    "path", "/page/org.clinical3po.backendservices-v-page-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_pageAdmin);

            Vertex m_formAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "formAdmin",
                    "label", "Form Admin",
                    "path", "/page/org.clinical3po.backendservices-v-form-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_formAdmin);

            Vertex m_ruleAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "ruleAdmin",
                    "label", "Rule Admin",
                    "path", "/page/org.clinical3po.backendservices-v-rule-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_ruleAdmin);

            Vertex m_menuAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "menuAdmin",
                    "label", "Menu Admin",
                    "path", "/page/org.clinical3po.backendservices-v-menu-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_menuAdmin);

            Vertex m_dbAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "dbAdmin",
                    "label", "DB Admin",
                    "path", "/page/org.clinical3po.backendservices-v-db-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_dbAdmin);

            Vertex m_userAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "userAdmin",
                    "label", "User Admin",
                    "path", "/page/org.clinical3po.backendservices-v-user-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_userAdmin);

            Vertex m_roleAdmin = graph.addVertex("class:MenuItem",
                    "menuItemId", "roleAdmin",
                    "label", "Role Admin",
                    "path", "/page/org.clinical3po.backendservices-v-role-admin-home",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_roleAdmin);

            Vertex m_admin = graph.addVertex("class:MenuItem",
                    "menuItemId", "admin",
                    "label", "Admin",
                    "path", "/admin",
                    "ctrl", "AdminCtrl",
                    "left", true,
                    "roles", "owner,admin,pageAdmin,formAdmin,ruleAdmin,menuAdmin,dbAdmin,productAdmin,forumAdmin,blogAdmin,newsAdmin,userAdmin,roleAdmin", // make sure there is no space between ,
                    "createDate", new java.util.Date());
            m_admin.addEdge("Own", m_ruleAdmin);
            m_admin.addEdge("Own", m_accessAdmin);
            m_admin.addEdge("Own", m_hostAdmin);
            m_admin.addEdge("Own", m_roleAdmin);
            m_admin.addEdge("Own", m_userAdmin);
            m_admin.addEdge("Own", m_dbAdmin);
            m_admin.addEdge("Own", m_menuAdmin);
            m_admin.addEdge("Own", m_formAdmin);
            m_admin.addEdge("Own", m_pageAdmin);
            userOwner.addEdge("Create", m_admin);



            Vertex m_logOut = graph.addVertex("class:MenuItem",
                    "menuItemId", "logOut",
                    "label", "Log Out",
                    "path", "/page/org.clinical3po.backendservices-v-user-logout",
                    "left", false,
                    "roles", "user",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_logOut);

            Vertex m_logIn = graph.addVertex("class:MenuItem",
                    "menuItemId", "logIn",
                    "label", "Log In",
                    "path", "/signin",
                    "tpl", "views/form.html",
                    "ctrl", "signinCtrl",
                    "left", false,
                    "roles", "anonymous",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_logIn);

            Vertex m_signUp = graph.addVertex("class:MenuItem",
                    "menuItemId", "signUp",
                    "label", "Sign Up",
                    "path", "/form/org.clinical3po.backendservices.user.signup",
                    "tpl", "views/form.html",
                    "ctrl", "FormCtrl",
                    "left", false,
                    "roles", "anonymous",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", m_signUp);

            Vertex m_edibleforestgarden = graph.addVertex("class:Menu",
                    "host", "www.edibleforestgarden.ca",
                    "createDate", new java.util.Date());
            m_edibleforestgarden.addEdge("Own", m_admin);
            m_edibleforestgarden.addEdge("Own", m_logOut);
            m_edibleforestgarden.addEdge("Own", m_logIn);
            m_edibleforestgarden.addEdge("Own", m_signUp);
            userOwner.addEdge("Create", m_edibleforestgarden);

            Vertex m_clinical3po = graph.addVertex("class:Menu",
                    "host", "www.clinical3po.org",
                    "createDate", new java.util.Date());
            m_clinical3po.addEdge("Own", m_admin);
            m_clinical3po.addEdge("Own", m_logOut);
            m_clinical3po.addEdge("Own", m_logIn);
            m_clinical3po.addEdge("Own", m_signUp);
            userOwner.addEdge("Create", m_clinical3po);

            Vertex m_example = graph.addVertex("class:Menu",
                    "host", "example",
                    "createDate", new java.util.Date());
            m_example.addEdge("Own", m_admin);
            m_example.addEdge("Own", m_logOut);
            m_example.addEdge("Own", m_logIn);
            m_example.addEdge("Own", m_signUp);
            userOwner.addEdge("Create", m_example);

            Vertex m_demo = graph.addVertex("class:Menu",
                    "host", "demo.clinical3po.org",
                    "createDate", new java.util.Date());
            m_demo.addEdge("Own", m_admin);
            m_demo.addEdge("Own", m_logOut);
            m_demo.addEdge("Own", m_logIn);
            m_demo.addEdge("Own", m_signUp);
            userOwner.addEdge("Create", m_demo);

            // create rules to bootstrap the installation through event replay.
            // need signIn, replayEvent and impRule to start up.

            Vertex abstractRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.AbstractRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule;\n" +
                            "\n" +
                            "import com.fasterxml.jackson.core.type.TypeReference;\n" +
                            "import com.fasterxml.jackson.databind.JsonNode;\n" +
                            "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                            "import com.github.fge.jsonschema.main.JsonSchema;\n" +
                            "import com.github.fge.jsonschema.main.JsonSchemaFactory;\n" +
                            "import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;\n" +
                            "import org.clinical3po.backendservices.model.CacheObject;\n" +
                            "import org.clinical3po.backendservices.server.DbService;\n" +
                            "import org.clinical3po.backendservices.util.ServiceLocator;\n" +
                            "import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;\n" +
                            "import com.orientechnologies.orient.core.db.record.OIdentifiable;\n" +
                            "import com.orientechnologies.orient.core.index.OCompositeKey;\n" +
                            "import com.orientechnologies.orient.core.index.OIndex;\n" +
                            "import com.orientechnologies.orient.core.metadata.schema.OSchema;\n" +
                            "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                            "import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraph;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientVertex;\n" +
                            "import io.undertow.server.HttpServerExchange;\n" +
                            "import io.undertow.util.Headers;\n" +
                            "import net.engio.mbassy.bus.MBassador;\n" +
                            "import org.slf4j.Logger;\n" +
                            "import org.slf4j.LoggerFactory;\n" +
                            "\n" +
                            "import java.util.*;\n" +
                            "import java.util.concurrent.ConcurrentMap;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 10/14/2015.\n" +
                            " */\n" +
                            "public abstract class AbstractRule implements Rule {\n" +
                            "    static final Logger logger = LoggerFactory.getLogger(AbstractRule.class);\n" +
                            "    static final JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();\n" +
                            "\n" +
                            "    protected ObjectMapper mapper = ServiceLocator.getInstance().getMapper();\n" +
                            "    public abstract boolean execute (Object ...objects) throws Exception;\n" +
                            "\n" +
                            "    protected void publishEvent(Map<String, Object> eventMap) throws Exception {\n" +
                            "        // get class name\n" +
                            "        System.out.println(this.getClass().getPackage());\n" +
                            "        System.out.println(this.getClass().getName());\n" +
                            "        System.out.println(\"category = \" + eventMap.get(\"category\"));\n" +
                            "        // check if publisher is enabled.\n" +
                            "        Map map = getRuleByRuleClass(this.getClass().getName());\n" +
                            "        Object isPublisher = map.get(\"isPublisher\");\n" +
                            "        if(isPublisher != null && (boolean)isPublisher) {\n" +
                            "            System.out.println(\"isPublisher\");\n" +
                            "            MBassador<Map<String, Object>> eventBus = ServiceLocator.getInstance().getEventBus((String)eventMap.get(\"category\"));\n" +
                            "            eventBus.publish(eventMap);\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected boolean matchEtag(Map<String, Object> inputMap, CacheObject co) {\n" +
                            "        HttpServerExchange exchange = (HttpServerExchange)inputMap.get(\"exchange\");\n" +
                            "        if(exchange != null) {\n" +
                            "            String requestETag = exchange.getRequestHeaders().getFirst(Headers.IF_NONE_MATCH);\n" +
                            "            if (co.getEtag().equals(requestETag)) {\n" +
                            "                exchange.setResponseCode(304); // no change\n" +
                            "                return true;\n" +
                            "            } else {\n" +
                            "                exchange.getResponseHeaders().add(Headers.ETAG, co.getEtag());\n" +
                            "                return false;\n" +
                            "            }\n" +
                            "        } else {\n" +
                            "            // Exchange is always available in runtime but not available in unit test cases.\n" +
                            "            return false;\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    public static Map<String, Object> getRuleByRuleClass(String ruleClass) throws Exception {\n" +
                            "        String sqlTransformReq = \"SELECT FROM TransformRequest WHERE ruleClass = '\" + ruleClass + \"' ORDER BY sequence\";\n" +
                            "        String sqlTransformRes = \"SELECT FROM TransformResponse WHERE ruleClass = '\" + ruleClass + \"' ORDER BY sequence\";\n" +
                            "\n" +
                            "        Map<String, Object> map = null;\n" +
                            "        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage(\"ruleMap\");\n" +
                            "        ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get(\"cache\");\n" +
                            "        if(cache == null) {\n" +
                            "            cache = new ConcurrentLinkedHashMap.Builder<String, Map<String, Object>>()\n" +
                            "                    .maximumWeightedCapacity(1000)\n" +
                            "                    .build();\n" +
                            "            ruleMap.put(\"cache\", cache);\n" +
                            "        } else {\n" +
                            "            map = cache.get(ruleClass);\n" +
                            "        }\n" +
                            "        if(map == null) {\n" +
                            "            OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "            try {\n" +
                            "                OrientVertex rule = (OrientVertex)graph.getVertexByKey(\"Rule.ruleClass\", ruleClass);\n" +
                            "                if(rule != null) {\n" +
                            "                    map = rule.getRecord().toMap();\n" +
                            "                    // remove sourceCode as we don't need it and it is big\n" +
                            "                    map.remove(\"sourceCode\");\n" +
                            "\n" +
                            "                    // convert schema to JsonSchema in order to speed up validation.\n" +
                            "                    if(map.get(\"schema\") != null) {\n" +
                            "                        JsonNode schemaNode = ServiceLocator.getInstance().getMapper().valueToTree(map.get(\"schema\"));\n" +
                            "                        JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);\n" +
                            "                        map.put(\"schema\", schema);\n" +
                            "                    }\n" +
                            "                    OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sqlTransformReq);\n" +
                            "                    List<ODocument> docs = graph.getRawGraph().command(query).execute();\n" +
                            "                    List<Map<String, Object>> reqTransforms = new ArrayList<Map<String, Object>>();\n" +
                            "                    if(docs != null) {\n" +
                            "                        for(ODocument doc: docs) {\n" +
                            "                            Map<String, Object> reqTransform = new HashMap<String, Object> ();\n" +
                            "                            reqTransform.put(\"sequence\", doc.field(\"sequence\"));\n" +
                            "                            reqTransform.put(\"transformRule\", doc.field(\"transformRule\"));\n" +
                            "                            reqTransform.put(\"transformData\", doc.field(\"transformData\"));\n" +
                            "                            reqTransform.put(\"createUserId\", doc.field(\"createUserId\"));\n" +
                            "                            reqTransforms.add(reqTransform);\n" +
                            "                        }\n" +
                            "                    }\n" +
                            "                    map.put(\"reqTransforms\", reqTransforms);\n" +
                            "\n" +
                            "                    query = new OSQLSynchQuery<>(sqlTransformRes);\n" +
                            "                    docs = graph.getRawGraph().command(query).execute();\n" +
                            "                    List<Map<String, Object>> resTransforms = new ArrayList<Map<String, Object>> ();\n" +
                            "                    if(docs != null) {\n" +
                            "                        for(ODocument doc: docs) {\n" +
                            "                            Map<String, Object> resTransform = new HashMap<String, Object> ();\n" +
                            "                            resTransform.put(\"sequence\", doc.field(\"sequence\"));\n" +
                            "                            resTransform.put(\"transformRule\", doc.field(\"transformRule\"));\n" +
                            "                            resTransform.put(\"transformData\", doc.field(\"transformData\"));\n" +
                            "                            resTransform.put(\"createUserId\", doc.field(\"createUserId\"));\n" +
                            "                            resTransforms.add(resTransform);\n" +
                            "                        }\n" +
                            "                    }\n" +
                            "                    map.put(\"resTransforms\", resTransforms);\n" +
                            "\n" +
                            "                    logger.debug(\"map = \" + map);\n" +
                            "                    cache.put(ruleClass, map);\n" +
                            "                }\n" +
                            "            } catch (Exception e) {\n" +
                            "                logger.error(\"Exception:\", e);\n" +
                            "                throw e;\n" +
                            "            } finally {\n" +
                            "                graph.shutdown();\n" +
                            "            }\n" +
                            "        }\n" +
                            "        return map;\n" +
                            "    }\n" +
                            "\n" +
                            "    protected Map<String, Object> getEventMap(Map<String, Object> inputMap) {\n" +
                            "        Map<String, Object> eventMap = new HashMap<String, Object>();\n" +
                            "        Map<String, Object> payload = (Map<String, Object>)inputMap.get(\"payload\");\n" +
                            "        if(payload != null) {\n" +
                            "            Map<String, Object> user = (Map<String, Object>)payload.get(\"user\");\n" +
                            "            if(user != null)  eventMap.put(\"createUserId\", user.get(\"userId\"));\n" +
                            "        }\n" +
                            "        // IP address is used to identify event owner if user is not logged in.\n" +
                            "        if(inputMap.get(\"ipAddress\") != null) {\n" +
                            "            eventMap.put(\"ipAddress\", inputMap.get(\"ipAddress\"));\n" +
                            "        }\n" +
                            "        if(inputMap.get(\"host\") != null) {\n" +
                            "            eventMap.put(\"host\", inputMap.get(\"host\"));\n" +
                            "        }\n" +
                            "        if(inputMap.get(\"app\") != null) {\n" +
                            "            eventMap.put(\"app\", inputMap.get(\"app\"));\n" +
                            "        }\n" +
                            "        eventMap.put(\"category\", inputMap.get(\"category\"));\n" +
                            "        eventMap.put(\"name\", inputMap.get(\"name\"));\n" +
                            "        eventMap.put(\"createDate\", new java.util.Date());\n" +
                            "        eventMap.put(\"data\", new HashMap<String, Object>());\n" +
                            "        return eventMap;\n" +
                            "    }\n" +
                            "\n" +
                            "\n" +
                            "    protected ODocument getODocumentByHostId(OrientGraph graph, String index, String host, String id) {\n" +
                            "        ODocument doc = null;\n" +
                            "        OIndex<?> hostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex(index);\n" +
                            "        // this is a unique index, so it retrieves a OIdentifiable\n" +
                            "        OCompositeKey key = new OCompositeKey(host, id);\n" +
                            "        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);\n" +
                            "        if (oid != null) {\n" +
                            "            doc = (ODocument)oid.getRecord();\n" +
                            "        }\n" +
                            "        return doc;\n" +
                            "    }\n" +
                            "\n" +
                            "    public Map<String, Object> getAccessByRuleClass(String ruleClass) throws Exception {\n" +
                            "        Map<String, Object> access = null;\n" +
                            "        Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage(\"accessMap\");\n" +
                            "        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get(\"cache\");\n" +
                            "        if(cache == null) {\n" +
                            "            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                            "                    .maximumWeightedCapacity(1000)\n" +
                            "                    .build();\n" +
                            "            accessMap.put(\"cache\", cache);\n" +
                            "        } else {\n" +
                            "            access = (Map<String, Object>)cache.get(ruleClass);\n" +
                            "        }\n" +
                            "        if(access == null) {\n" +
                            "            OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "            try {\n" +
                            "                OrientVertex accessVertex = (OrientVertex)graph.getVertexByKey(\"Access.ruleClass\", ruleClass);\n" +
                            "                if(accessVertex != null) {\n" +
                            "                    String json = accessVertex.getRecord().toJSON();\n" +
                            "                    access = mapper.readValue(json,\n" +
                            "                            new TypeReference<HashMap<String, Object>>() {\n" +
                            "                            });\n" +
                            "                    cache.put(ruleClass, access);\n" +
                            "                }\n" +
                            "            } catch (Exception e) {\n" +
                            "                logger.error(\"Exception:\", e);\n" +
                            "                throw e;\n" +
                            "            } finally {\n" +
                            "                graph.shutdown();\n" +
                            "            }\n" +
                            "        }\n" +
                            "        return access;\n" +
                            "    }\n" +
                            "\n" +
                            "    public Map<String, Object> getAccessByRuleClass(OrientGraph graph, String ruleClass) throws Exception {\n" +
                            "        Map<String, Object> access = null;\n" +
                            "        Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage(\"accessMap\");\n" +
                            "        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get(\"cache\");\n" +
                            "        if(cache == null) {\n" +
                            "            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                            "                    .maximumWeightedCapacity(1000)\n" +
                            "                    .build();\n" +
                            "            accessMap.put(\"cache\", cache);\n" +
                            "        } else {\n" +
                            "            access = (Map<String, Object>)cache.get(ruleClass);\n" +
                            "        }\n" +
                            "        if(access == null) {\n" +
                            "            OrientVertex accessVertex = (OrientVertex)graph.getVertexByKey(\"Access.ruleClass\", ruleClass);\n" +
                            "            if(accessVertex != null) {\n" +
                            "                String json = accessVertex.getRecord().toJSON();\n" +
                            "                access = mapper.readValue(json,\n" +
                            "                        new TypeReference<HashMap<String, Object>>() {\n" +
                            "                        });\n" +
                            "                cache.put(ruleClass, access);\n" +
                            "            }\n" +
                            "        }\n" +
                            "        return access;\n" +
                            "    }\n" +
                            "\n" +
                            "}\n",
                    "createDate", new java.util.Date());
                    userOwner.addEdge("Create", abstractRule);


            Vertex abstractRuleRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.rule.AbstractRuleRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule.rule;\n" +
                            "\n" +
                            "import com.fasterxml.jackson.core.type.TypeReference;\n" +
                            "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                            "import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;\n" +
                            "import org.clinical3po.backendservices.rule.AbstractRule;\n" +
                            "import org.clinical3po.backendservices.rule.Rule;\n" +
                            "import org.clinical3po.backendservices.server.DbService;\n" +
                            "import org.clinical3po.backendservices.util.ServiceLocator;\n" +
                            "import com.orientechnologies.orient.core.Orient;\n" +
                            "import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;\n" +
                            "import com.orientechnologies.orient.core.db.record.OIdentifiable;\n" +
                            "import com.orientechnologies.orient.core.index.OIndex;\n" +
                            "import com.orientechnologies.orient.core.metadata.schema.OSchema;\n" +
                            "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                            "import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;\n" +
                            "import com.orientechnologies.orient.core.sql.OCommandSQL;\n" +
                            "import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;\n" +
                            "import com.tinkerpop.blueprints.Vertex;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraph;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientVertex;\n" +
                            "import org.slf4j.LoggerFactory;\n" +
                            "\n" +
                            "import java.util.*;\n" +
                            "import java.util.concurrent.ConcurrentMap;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 10/8/2015.\n" +
                            " */\n" +
                            "public abstract class AbstractRuleRule extends AbstractRule implements Rule {\n" +
                            "    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractRuleRule.class);\n" +
                            "\n" +
                            "    public abstract boolean execute (Object ...objects) throws Exception;\n" +
                            "\n" +
                            "\n" +
                            "    protected void addRule(Map<String, Object> data) throws Exception {\n" +
                            "        OrientVertex access = null;\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex createUser = graph.getVertexByKey(\"User.userId\", data.remove(\"createUserId\"));\n" +
                            "            OrientVertex rule = graph.addVertex(\"class:Rule\", data);\n" +
                            "            createUser.addEdge(\"Create\", rule);\n" +
                            "            if(rule != null) {\n" +
                            "                // add the rule into compile map\n" +
                            "                Map<String, Object> compileMap = ServiceLocator.getInstance().getMemoryImage(\"compileMap\");\n" +
                            "                ConcurrentMap<String, String> cache = (ConcurrentMap<String, String>)compileMap.get(\"cache\");\n" +
                            "                if(cache == null) {\n" +
                            "                    cache = new ConcurrentLinkedHashMap.Builder<String, String>()\n" +
                            "                            .maximumWeightedCapacity(10000)\n" +
                            "                            .build();\n" +
                            "                    compileMap.put(\"cache\", cache);\n" +
                            "                }\n" +
                            "                cache.put(ruleClass, (String)data.get(\"sourceCode\"));\n" +
                            "            }\n" +
                            "\n" +
                            "            // For all the newly added rules, the default security access is role based and only\n" +
                            "            // owner can access. For some of the rules, like getForm, getMenu, they are granted\n" +
                            "            // to anyone in the db script. Don't overwrite if access exists for these rules.\n" +
                            "\n" +
                            "            // check if access exists for the ruleClass and add access if not.\n" +
                            "            if(getAccessByRuleClass(ruleClass) == null) {\n" +
                            "                access = graph.addVertex(\"class:Access\");\n" +
                            "                access.setProperty(\"ruleClass\", ruleClass);\n" +
                            "                if(ruleClass.contains(\"Abstract\") || ruleClass.contains(\"_\")) {\n" +
                            "                    access.setProperty(\"accessLevel\", \"N\"); // abstract rule and internal beta tester rule\n" +
                            "                } else if(ruleClass.endsWith(\"EvRule\")) {\n" +
                            "                    access.setProperty(\"accessLevel\", \"A\"); // event rule can be only called internally.\n" +
                            "                } else {\n" +
                            "                    access.setProperty(\"accessLevel\", \"R\"); // role level access\n" +
                            "                    List roles = new ArrayList();\n" +
                            "                    roles.add(\"owner\");  // give owner access for the rule by default.\n" +
                            "                    access.setProperty(\"roles\", roles);\n" +
                            "                }\n" +
                            "                access.setProperty(\"createDate\", data.get(\"createDate\"));\n" +
                            "                createUser.addEdge(\"Create\", access);\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "\n" +
                            "            if(access != null) {\n" +
                            "                Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage(\"accessMap\");\n" +
                            "                ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get(\"cache\");\n" +
                            "                if(cache == null) {\n" +
                            "                    cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                            "                            .maximumWeightedCapacity(1000)\n" +
                            "                            .build();\n" +
                            "                    accessMap.put(\"cache\", cache);\n" +
                            "                }\n" +
                            "                cache.put(ruleClass, mapper.readValue(access.getRecord().toJSON(),\n" +
                            "                        new TypeReference<HashMap<String, Object>>() {\n" +
                            "                        }));\n" +
                            "            }\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void impRule(Map<String, Object> data) throws Exception {\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex rule = graph.getVertexByKey(\"Rule.ruleClass\", ruleClass);\n" +
                            "            boolean toCompile = true;\n" +
                            "            // remove the existing rule if there is.\n" +
                            "            if(rule != null) {\n" +
                            "                graph.removeVertex(rule);\n" +
                            "                // This is to replace existing rule in memory but due to class loader issue, the\n" +
                            "                // new rule will replace the old one after restart the server. don't compile it.\n" +
                            "                toCompile = false;\n" +
                            "            }\n" +
                            "            // create a new rule\n" +
                            "            Vertex createUser = graph.getVertexByKey(\"User.userId\", data.remove(\"createUserId\"));\n" +
                            "            rule = graph.addVertex(\"class:Rule\", data);\n" +
                            "            createUser.addEdge(\"Create\", rule);\n" +
                            "\n" +
                            "            if(rule != null && toCompile) {\n" +
                            "                // add the rule into compile map\n" +
                            "                Map<String, Object> compileMap = ServiceLocator.getInstance().getMemoryImage(\"compileMap\");\n" +
                            "                ConcurrentMap<String, String> cache = (ConcurrentMap<String, String>)compileMap.get(\"cache\");\n" +
                            "                if(cache == null) {\n" +
                            "                    cache = new ConcurrentLinkedHashMap.Builder<String, String>()\n" +
                            "                            .maximumWeightedCapacity(10000)\n" +
                            "                            .build();\n" +
                            "                    compileMap.put(\"cache\", cache);\n" +
                            "                }\n" +
                            "                cache.put(ruleClass, (String)data.get(\"sourceCode\"));\n" +
                            "            }\n" +
                            "\n" +
                            "            // For all the newly added rules, the default security access is role based and only\n" +
                            "            // owner can access. For some of the rules, like getForm, getMenu, they are granted\n" +
                            "            // to anyone in the db script. Don't overwrite if access exists for these rules.\n" +
                            "            // Also, if ruleClass contains \"Abstract\" then its access level should be N.\n" +
                            "\n" +
                            "            // check if access exists for the ruleClass and add access if not.\n" +
                            "            OrientVertex access = null;\n" +
                            "            if(getAccessByRuleClass(graph, ruleClass) == null) {\n" +
                            "                access = graph.addVertex(\"class:Access\");\n" +
                            "                access.setProperty(\"ruleClass\", ruleClass);\n" +
                            "                if(ruleClass.contains(\"Abstract\") || ruleClass.contains(\"_\")) {\n" +
                            "                    access.setProperty(\"accessLevel\", \"N\"); // abstract and internal beta tester rule\n" +
                            "                } else if(ruleClass.endsWith(\"EvRule\")) {\n" +
                            "                    access.setProperty(\"accessLevel\", \"A\"); // event rule can be only called internally.\n" +
                            "                } else {\n" +
                            "                    access.setProperty(\"accessLevel\", \"R\"); // role level access\n" +
                            "                    List roles = new ArrayList();\n" +
                            "                    roles.add(\"owner\");  // give owner access for the rule by default.\n" +
                            "                    access.setProperty(\"roles\", roles);\n" +
                            "                }\n" +
                            "                access.setProperty(\"createDate\", data.get(\"createDate\"));\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "\n" +
                            "            if(access != null) {\n" +
                            "                Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage(\"accessMap\");\n" +
                            "                ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get(\"cache\");\n" +
                            "                if(cache == null) {\n" +
                            "                    cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()\n" +
                            "                            .maximumWeightedCapacity(1000)\n" +
                            "                            .build();\n" +
                            "                    accessMap.put(\"cache\", cache);\n" +
                            "                }\n" +
                            "                cache.put(ruleClass, mapper.readValue(access.getRecord().toJSON(),\n" +
                            "                        new TypeReference<HashMap<String, Object>>() {\n" +
                            "                        }));\n" +
                            "            }\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected String updateValidation(Map<String, Object> inputMap, Map<String, Object> eventData) {\n" +
                            "        Map<String, Object> data = (Map<String, Object>)inputMap.get(\"data\");\n" +
                            "        Map<String, Object> payload = (Map<String, Object>) inputMap.get(\"payload\");\n" +
                            "        Map<String, Object> user = (Map<String, Object>)payload.get(\"user\");\n" +
                            "        String rid = (String)data.get(\"@rid\");\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        String error = null;\n" +
                            "        String host = (String)user.get(\"host\");\n" +
                            "        if(host != null) {\n" +
                            "            if(!host.equals(data.get(\"host\"))) {\n" +
                            "                error = \"You can only update rule for host: \" + host;\n" +
                            "                inputMap.put(\"responseCode\", 403);\n" +
                            "            } else {\n" +
                            "                // make sure the ruleClass contains the host.\n" +
                            "                if(host != null && !ruleClass.contains(host)) {\n" +
                            "                    // you are not allowed to update rule as it is not owned by the host.\n" +
                            "                    error = \"ruleClass is not owned by the host: \" + host;\n" +
                            "                    inputMap.put(\"responseCode\", 403);\n" +
                            "                }\n" +
                            "            }\n" +
                            "        }\n" +
                            "        if(error == null) {\n" +
                            "            OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "            try {\n" +
                            "                Vertex rule = DbService.getVertexByRid(graph, rid);\n" +
                            "                if(rule == null) {\n" +
                            "                    error = \"Rule with @rid \" + rid + \" cannot be found\";\n" +
                            "                    inputMap.put(\"responseCode\", 404);\n" +
                            "                } else {\n" +
                            "                    eventData.put(\"ruleClass\", ruleClass);\n" +
                            "                    eventData.put(\"updateUserId\", user.get(\"userId\"));\n" +
                            "                }\n" +
                            "            } catch (Exception e) {\n" +
                            "                logger.error(\"Exception:\", e);\n" +
                            "                throw e;\n" +
                            "            } finally {\n" +
                            "                graph.shutdown();\n" +
                            "            }\n" +
                            "        }\n" +
                            "        return error;\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updEtag(Map<String, Object> data) throws Exception {\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex rule = graph.getVertexByKey(\"Rule.ruleClass\", ruleClass);\n" +
                            "            if(rule != null) {\n" +
                            "                rule.setProperty(\"enableEtag\", data.get(\"enableEtag\"));\n" +
                            "                String cacheControl = (String)data.get(\"cacheControl\");\n" +
                            "                if(cacheControl != null) {\n" +
                            "                    rule.setProperty(\"cacheControl\", cacheControl);\n" +
                            "                } else {\n" +
                            "                    rule.removeProperty(\"cacheControl\");\n" +
                            "                }\n" +
                            "                rule.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "                Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage(\"ruleMap\");\n" +
                            "                ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get(\"cache\");\n" +
                            "                if(cache != null) {\n" +
                            "                    cache.remove(ruleClass);\n" +
                            "                }\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updSchema(Map<String, Object> data) throws Exception {\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex rule = graph.getVertexByKey(\"Rule.ruleClass\", ruleClass);\n" +
                            "            if(rule != null) {\n" +
                            "                String schema = (String)data.get(\"schema\");\n" +
                            "                if(schema != null && schema.length() > 0) {\n" +
                            "                    // convert it to map before setProperty.\n" +
                            "                    Map<String, Object> schemaMap = mapper.readValue(schema,\n" +
                            "                        new TypeReference<HashMap<String, Object>>() {});\n" +
                            "                    rule.setProperty(\"schema\", schemaMap);\n" +
                            "                } else {\n" +
                            "                    rule.removeProperty(\"schema\");\n" +
                            "                }\n" +
                            "                rule.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "                Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage(\"ruleMap\");\n" +
                            "                ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get(\"cache\");\n" +
                            "                if(cache != null) {\n" +
                            "                    cache.remove(ruleClass);\n" +
                            "                }\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updCors(Map<String, Object> data) throws Exception {\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex rule = graph.getVertexByKey(\"Rule.ruleClass\", ruleClass);\n" +
                            "            if(rule != null) {\n" +
                            "                rule.setProperty(\"enableCors\", data.get(\"enableCors\"));\n" +
                            "                String corsHosts = (String)data.get(\"corsHosts\");\n" +
                            "                if(corsHosts != null) {\n" +
                            "                    rule.setProperty(\"corsHosts\", corsHosts);\n" +
                            "                } else {\n" +
                            "                    rule.removeProperty(\"corsHosts\");\n" +
                            "                }\n" +
                            "                rule.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "                Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage(\"ruleMap\");\n" +
                            "                ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get(\"cache\");\n" +
                            "                if(cache != null) {\n" +
                            "                    cache.remove(ruleClass);\n" +
                            "                }\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updPublisher(Map<String, Object> data) throws Exception {\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex rule = graph.getVertexByKey(\"Rule.ruleClass\", ruleClass);\n" +
                            "            if(rule != null) {\n" +
                            "                rule.setProperty(\"isPublisher\", data.get(\"isPublisher\"));\n" +
                            "                rule.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "                Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage(\"ruleMap\");\n" +
                            "                ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get(\"cache\");\n" +
                            "                if(cache != null) {\n" +
                            "                    cache.remove(ruleClass);\n" +
                            "                }\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updSubscriber(Map<String, Object> data) throws Exception {\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex rule = graph.getVertexByKey(\"Rule.ruleClass\", ruleClass);\n" +
                            "            if(rule != null) {\n" +
                            "                rule.setProperty(\"isSubscriber\", data.get(\"isSubscriber\"));\n" +
                            "                rule.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "                Vertex updateUser = graph.getVertexByKey(\"User.userId\", data.get(\"updateUserId\"));\n" +
                            "                if(updateUser != null) {\n" +
                            "                    updateUser.addEdge(\"Update\", rule);\n" +
                            "                }\n" +
                            "                Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage(\"ruleMap\");\n" +
                            "                ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get(\"cache\");\n" +
                            "                if(cache != null) {\n" +
                            "                    cache.remove(ruleClass);\n" +
                            "                }\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updRule(Map<String, Object> data) throws Exception {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex rule = graph.getVertexByKey(\"Rule.ruleClass\", data.get(\"ruleClass\"));\n" +
                            "            if(rule != null) {\n" +
                            "                String sourceCode = (String)data.get(\"sourceCode\");\n" +
                            "                if(sourceCode != null && !sourceCode.equals(rule.getProperty(\"sourceCode\"))) {\n" +
                            "                    rule.setProperty(\"sourceCode\", sourceCode);\n" +
                            "                }\n" +
                            "                rule.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "                Vertex updateUser = graph.getVertexByKey(\"User.userId\", data.get(\"updateUserId\"));\n" +
                            "                if(updateUser != null) {\n" +
                            "                    updateUser.addEdge(\"Update\", rule);\n" +
                            "                }\n" +
                            "                // there is no need to put updated rule into compileMap.\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void delRule(Map<String, Object> data) throws Exception {\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex rule = graph.getVertexByKey(\"Rule.ruleClass\", ruleClass);\n" +
                            "            if(rule != null) {\n" +
                            "                graph.removeVertex(rule);\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "            // check if the rule is in compile cache, remove it.\n" +
                            "            Map<String, Object> compileMap = ServiceLocator.getInstance().getMemoryImage(\"compileMap\");\n" +
                            "            ConcurrentMap<String, String> cache = (ConcurrentMap<String, String>)compileMap.get(\"cache\");\n" +
                            "            if(cache != null) {\n" +
                            "                cache.remove(ruleClass);\n" +
                            "            }\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected String getRules(String host) {\n" +
                            "        String sql = \"SELECT FROM Rule\";\n" +
                            "        if(host != null) {\n" +
                            "            sql = sql + \" WHERE host = '\" + host;\n" +
                            "        }\n" +
                            "        String json = null;\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);\n" +
                            "            List<ODocument> rules = graph.getRawGraph().command(query).execute();\n" +
                            "            json = OJSONWriter.listToJSON(rules, null);\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "        return json;\n" +
                            "    }\n" +
                            "\n" +
                            "    public static void loadCompileCache() {\n" +
                            "        String sql = \"SELECT FROM Rule\";\n" +
                            "        Map<String, Object> compileMap = ServiceLocator.getInstance().getMemoryImage(\"compileMap\");\n" +
                            "        ConcurrentMap<String, String> cache = (ConcurrentMap<String, String>)compileMap.get(\"cache\");\n" +
                            "        if(cache == null) {\n" +
                            "            cache = new ConcurrentLinkedHashMap.Builder<String, String>()\n" +
                            "                    .maximumWeightedCapacity(10000)\n" +
                            "                    .build();\n" +
                            "            compileMap.put(\"cache\", cache);\n" +
                            "        }\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            for (Vertex rule : (Iterable<Vertex>) graph.command(new OCommandSQL(sql)).execute()) {\n" +
                            "                cache.put((String) rule.getProperty(\"ruleClass\"), (String) rule.getProperty(\"sourceCode\"));\n" +
                            "            }\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected String getRuleMap(OrientGraph graph, String host) throws Exception {\n" +
                            "        String sql = \"SELECT FROM Rule\";\n" +
                            "        if(host != null) {\n" +
                            "            sql = sql + \" WHERE host = '\" + host;\n" +
                            "        }\n" +
                            "        String json = null;\n" +
                            "        try {\n" +
                            "            Map<String, String> ruleMap = new HashMap<String, String> ();\n" +
                            "            for (Vertex rule : (Iterable<Vertex>) graph.command(new OCommandSQL(sql)).execute()) {\n" +
                            "                ruleMap.put((String) rule.getProperty(\"ruleClass\"), (String) rule.getProperty(\"sourceCode\"));\n" +
                            "            }\n" +
                            "            json = mapper.writeValueAsString(ruleMap);\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "        return json;\n" +
                            "    }\n" +
                            "\n" +
                            "    protected String getRuleDropdown(String host) {\n" +
                            "        String sql = \"SELECT FROM Rule\";\n" +
                            "        if(host != null) {\n" +
                            "            sql = sql + \" WHERE host = '\" + host + \"' OR host IS NULL\";\n" +
                            "        }\n" +
                            "        String json = null;\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);\n" +
                            "            List<ODocument> rules = graph.getRawGraph().command(query).execute();\n" +
                            "            if(rules.size() > 0) {\n" +
                            "                List<Map<String, String>> list = new ArrayList<Map<String, String>>();\n" +
                            "                for(ODocument doc: rules) {\n" +
                            "                    Map<String, String> map = new HashMap<String, String>();\n" +
                            "                    String ruleClass = doc.field(\"ruleClass\");\n" +
                            "                    map.put(\"label\", ruleClass);\n" +
                            "                    map.put(\"value\", ruleClass);\n" +
                            "                    list.add(map);\n" +
                            "                }\n" +
                            "                json = mapper.writeValueAsString(list);\n" +
                            "            }\n" +
                            "\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            //throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "        return json;\n" +
                            "    }\n" +
                            "\n" +
                            "}\n",
                    "createDate", new java.util.Date());
                    userOwner.addEdge("Create", abstractRuleRule);


            Vertex abstractUserRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.user.AbstractUserRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule.user;\n" +
                            "\n" +
                            "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                            "import org.clinical3po.backendservices.rule.AbstractRule;\n" +
                            "import org.clinical3po.backendservices.rule.Rule;\n" +
                            "import org.clinical3po.backendservices.server.DbService;\n" +
                            "import org.clinical3po.backendservices.util.HashUtil;\n" +
                            "import org.clinical3po.backendservices.util.JwtUtil;\n" +
                            "import org.clinical3po.backendservices.util.ServiceLocator;\n" +
                            "import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;\n" +
                            "import com.orientechnologies.orient.core.db.record.OIdentifiable;\n" +
                            "import com.orientechnologies.orient.core.index.OIndex;\n" +
                            "import com.orientechnologies.orient.core.metadata.schema.OSchema;\n" +
                            "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                            "import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;\n" +
                            "import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;\n" +
                            "import com.tinkerpop.blueprints.Direction;\n" +
                            "import com.tinkerpop.blueprints.Edge;\n" +
                            "import com.tinkerpop.blueprints.Vertex;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraph;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientVertex;\n" +
                            "import org.slf4j.LoggerFactory;\n" +
                            "\n" +
                            "import java.util.*;\n" +
                            "import java.util.regex.Matcher;\n" +
                            "import java.util.regex.Pattern;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 9/23/2015.\n" +
                            " */\n" +
                            "public abstract class AbstractUserRule extends AbstractRule implements Rule {\n" +
                            "    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractUserRule.class);\n" +
                            "\n" +
                            "    public static final String EMAIL_PATTERN = \"^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*@\"\n" +
                            "        + \"[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$\";\n" +
                            "    Pattern pattern = Pattern.compile(EMAIL_PATTERN);\n" +
                            "\n" +
                            "    public abstract boolean execute (Object ...objects) throws Exception;\n" +
                            "    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();\n" +
                            "\n" +
                            "    protected boolean isUserInDbByEmail(String email) {\n" +
                            "        boolean userInDb = false;\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            Vertex user = graph.getVertexByKey(\"User.email\", email);\n" +
                            "            if(user != null) {\n" +
                            "                userInDb = true;\n" +
                            "            }\n" +
                            "        } catch (Exception e) {\n" +
                            "            e.printStackTrace();\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "        return userInDb;\n" +
                            "    }\n" +
                            "\n" +
                            "    protected boolean isUserInDbByUserId(String userId) {\n" +
                            "        boolean userInDb = false;\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            Vertex user = graph.getVertexByKey(\"User.userId\", userId);\n" +
                            "            if(user != null) {\n" +
                            "                userInDb = true;\n" +
                            "            }\n" +
                            "        } catch (Exception e) {\n" +
                            "            e.printStackTrace();\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "        return userInDb;\n" +
                            "    }\n" +
                            "\n" +
                            "    protected Vertex getUserByUserId(OrientGraph graph, String userId) throws Exception {\n" +
                            "        return graph.getVertexByKey(\"User.userId\", userId);\n" +
                            "    }\n" +
                            "\n" +
                            "    protected Vertex getUserByEmail(OrientGraph graph, String email) throws Exception {\n" +
                            "        return graph.getVertexByKey(\"User.email\", email);\n" +
                            "    }\n" +
                            "\n" +
                            "    protected Vertex getCredential(OrientGraph graph, Vertex user) throws Exception {\n" +
                            "        return graph.getVertex(user.getProperty(\"credential\"));\n" +
                            "    }\n" +
                            "\n" +
                            "    protected Vertex addUser(Map<String, Object> data) throws Exception {\n" +
                            "        Vertex user = null;\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            String password = (String)data.remove(\"password\");\n" +
                            "            OrientVertex credential = graph.addVertex(\"class:Credential\", \"password\", password);\n" +
                            "            data.put(\"credential\", credential);\n" +
                            "            user = graph.addVertex(\"class:User\", data);\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "        return user;\n" +
                            "    }\n" +
                            "\n" +
                            "    protected Vertex addActivation(String userId) throws Exception {\n" +
                            "        Vertex activation = null;\n" +
                            "        String code = HashUtil.generateUUID();\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            activation = graph.addVertex(\"class:Activation\", \"userId\", userId, \"code\", code, \"createDate\", new Date());\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "        return activation;\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void delActivation(String userId, String code) throws Exception {\n" +
                            "        Vertex activation = null;\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            activation = graph.getVertexByKey(\"Activation.userId\", userId);\n" +
                            "            if(activation != null && code != null && code.equals(activation.getProperty(\"code\"))) {\n" +
                            "                activation.remove();\n" +
                            "            }\n" +
                            "            \n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void delUser(Map<String, Object> data) throws Exception {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex user = graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "            if(user != null) {\n" +
                            "                graph.removeVertex(user.getProperty(\"credential\"));\n" +
                            "                graph.removeVertex(user);\n" +
                            "            }\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updPassword(Map<String, Object> data) throws Exception {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex user = graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "            user.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "            Vertex credential = user.getProperty(\"credential\");\n" +
                            "            if (credential != null) {\n" +
                            "                credential.setProperty(\"password\", data.get(\"password\"));\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updRole(Map<String, Object> data) throws Exception {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex user = graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "            if(user != null) {\n" +
                            "                user.setProperty(\"roles\", data.get(\"roles\"));\n" +
                            "                user.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "                Vertex updateUser = graph.getVertexByKey(\"User.userId\", data.get(\"updateUserId\"));\n" +
                            "                updateUser.addEdge(\"Update\", user);\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updLockByUserId(Map<String, Object> data) throws Exception {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex user = graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "            if(user != null) {\n" +
                            "                user.setProperty(\"locked\", data.get(\"locked\"));\n" +
                            "                user.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "                Vertex updateUser = graph.getVertexByKey(\"User.userId\", data.get(\"updateUserId\"));\n" +
                            "                updateUser.addEdge(\"Update\", user);\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void updUser(Map<String, Object> data) throws Exception {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex user =  graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "            if(user != null) {\n" +
                            "                String firstName = (String)data.get(\"firstName\");\n" +
                            "                if(firstName != null && !firstName.equals(user.getProperty(\"firstName\"))) {\n" +
                            "                    user.setProperty(\"firstName\", firstName);\n" +
                            "                }\n" +
                            "                String lastName = (String)data.get(\"lastName\");\n" +
                            "                if(lastName != null && !lastName.equals(user.getProperty(\"lastName\"))) {\n" +
                            "                    user.setProperty(\"lastName\", lastName);\n" +
                            "                }\n" +
                            "                user.setProperty(\"updateDate\", data.get(\"updateDate\"));\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    // TODO need to know which clientId to remove only for that client or all?\n" +
                            "    protected void revokeRefreshToken(Map<String, Object> data) throws Exception {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            Vertex user = graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "            if(user != null) {\n" +
                            "                Vertex credential = user.getProperty(\"credential\");\n" +
                            "                if(credential != null) {\n" +
                            "                    credential.removeProperty(\"clientRefreshTokens\");\n" +
                            "                }\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "\n" +
                            "    protected void signIn(Map<String, Object> data) throws Exception {\n" +
                            "        String hashedRefreshToken = (String)data.get(\"hashedRefreshToken\");\n" +
                            "        if(hashedRefreshToken != null) {\n" +
                            "            OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "            try {\n" +
                            "                graph.begin();\n" +
                            "                Vertex user = graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "                if(user != null) {\n" +
                            "                    Vertex credential = user.getProperty(\"credential\");\n" +
                            "                    if(credential != null) {\n" +
                            "                        String clientId = (String)data.get(\"clientId\");\n" +
                            "                        // get hostRefreshTokens map here.\n" +
                            "                        Map clientRefreshTokens = credential.getProperty(\"clientRefreshTokens\");\n" +
                            "                        if(clientRefreshTokens != null) {\n" +
                            "                            // logged in before, check if logged in from the host.\n" +
                            "                            List<String> refreshTokens = (List)clientRefreshTokens.get(clientId);\n" +
                            "                            if(refreshTokens != null) {\n" +
                            "                                // max refresh tokens for user is 10. max 10 devices.\n" +
                            "                                if(refreshTokens.size() >= 10) {\n" +
                            "                                    refreshTokens.remove(0);\n" +
                            "                                }\n" +
                            "                                refreshTokens.add(hashedRefreshToken);\n" +
                            "                            } else {\n" +
                            "                                refreshTokens = new ArrayList<String>();\n" +
                            "                                refreshTokens.add(hashedRefreshToken);\n" +
                            "                                clientRefreshTokens.put(clientId, refreshTokens);\n" +
                            "                            }\n" +
                            "                            credential.setProperty(\"clientRefreshTokens\", clientRefreshTokens);\n" +
                            "                        } else {\n" +
                            "                            // never logged in, create the map.\n" +
                            "                            clientRefreshTokens = new HashMap<String, List<String>>();\n" +
                            "                            List<String> refreshTokens = new ArrayList<String>();\n" +
                            "                            refreshTokens.add(hashedRefreshToken);\n" +
                            "                            clientRefreshTokens.put(clientId, refreshTokens);\n" +
                            "                            credential.setProperty(\"clientRefreshTokens\", clientRefreshTokens);\n" +
                            "                        }\n" +
                            "                    }\n" +
                            "                }\n" +
                            "                graph.commit();\n" +
                            "            } catch (Exception e) {\n" +
                            "                logger.error(\"Exception:\", e);\n" +
                            "                graph.rollback();\n" +
                            "                throw e;\n" +
                            "            } finally {\n" +
                            "                graph.shutdown();\n" +
                            "            }\n" +
                            "        } else {\n" +
                            "            logger.debug(\"There is no hashedRefreshToken as user didn't select remember me. Do nothing\");\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void logOut(Map<String, Object> data) throws Exception {\n" +
                            "        String refreshToken = (String)data.get(\"refreshToken\");\n" +
                            "        if(refreshToken != null) {\n" +
                            "            OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "            try {\n" +
                            "                graph.begin();\n" +
                            "                Vertex user = graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "                if(user != null) {\n" +
                            "                    Vertex credential = user.getProperty(\"credential\");\n" +
                            "                    if(credential != null) {\n" +
                            "                        // now remove the refresh token\n" +
                            "                        String clientId = (String)data.get(\"clientId\");\n" +
                            "                        logger.debug(\"logOut to remove refreshToken {} from clientId {}\" , refreshToken, clientId);\n" +
                            "                        Map clientRefreshTokens = credential.getProperty(\"clientRefreshTokens\");\n" +
                            "                        if(clientRefreshTokens != null) {\n" +
                            "                            // logged in before, check if logged in from the host.\n" +
                            "                            List<String> refreshTokens = (List)clientRefreshTokens.get(clientId);\n" +
                            "                            if(refreshTokens != null) {\n" +
                            "                                String hashedRefreshToken = HashUtil.md5(refreshToken);\n" +
                            "                                refreshTokens.remove(hashedRefreshToken);\n" +
                            "                            }\n" +
                            "                        } else {\n" +
                            "                            logger.error(\"There is no refresh tokens\");\n" +
                            "                        }\n" +
                            "                    }\n" +
                            "                }\n" +
                            "                graph.commit();\n" +
                            "            } catch (Exception e) {\n" +
                            "                logger.error(\"Exception:\", e);\n" +
                            "                graph.rollback();\n" +
                            "                throw e;\n" +
                            "            } finally {\n" +
                            "                graph.shutdown();\n" +
                            "            }\n" +
                            "        } else {\n" +
                            "            logger.debug(\"There is no hashedRefreshToken as user didn't pass in refresh token when logging out. Do nothing\");\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    boolean checkRefreshToken(Vertex credential, String clientId, String refreshToken) throws Exception {\n" +
                            "        boolean result = false;\n" +
                            "        if(credential != null && refreshToken != null) {\n" +
                            "            Map clientRefreshTokens = credential.getProperty(\"clientRefreshTokens\");\n" +
                            "            if(clientRefreshTokens != null) {\n" +
                            "                List<String> refreshTokens = (List)clientRefreshTokens.get(clientId);\n" +
                            "                if(refreshTokens != null) {\n" +
                            "                    String hashedRefreshToken = HashUtil.md5(refreshToken);\n" +
                            "                    for(String token: refreshTokens) {\n" +
                            "                        if(hashedRefreshToken.equals(token)) {\n" +
                            "                            result = true;\n" +
                            "                            break;\n" +
                            "                        }\n" +
                            "                    }\n" +
                            "                }\n" +
                            "            } else {\n" +
                            "                logger.error(\"There is no refresh tokens\");\n" +
                            "            }\n" +
                            "        }\n" +
                            "        return result;\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void upVoteUser(Map<String, Object> data) {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            OrientVertex user = (OrientVertex)graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey(\"User.userId\", data.get(\"voteUserId\"));\n" +
                            "            if(user != null && voteUser != null) {\n" +
                            "                for (Edge edge : voteUser.getEdges(user, Direction.OUT, \"DownVote\")) {\n" +
                            "                    if(edge.getVertex(Direction.IN).equals(user)) graph.removeEdge(edge);\n" +
                            "                }\n" +
                            "                voteUser.addEdge(\"UpVote\", user);\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    protected void downVoteUser(Map<String, Object> data) {\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        try {\n" +
                            "            graph.begin();\n" +
                            "            OrientVertex user = (OrientVertex)graph.getVertexByKey(\"User.userId\", data.get(\"userId\"));\n" +
                            "            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey(\"User.userId\", data.get(\"voteUserId\"));\n" +
                            "            if(user != null && voteUser != null) {\n" +
                            "                for (Edge edge : voteUser.getEdges(user, Direction.OUT, \"UpVote\")) {\n" +
                            "                    if(edge.getVertex(Direction.IN).equals(user)) graph.removeEdge(edge);\n" +
                            "                }\n" +
                            "                voteUser.addEdge(\"DownVote\", user);\n" +
                            "            }\n" +
                            "            graph.commit();\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            graph.rollback();\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    // TODO refactor it to be generic. table name as part of the criteria? or a parameter?\n" +
                            "    protected long getTotalNumberUserFromDb(OrientGraph graph, Map<String, Object> criteria) throws Exception {\n" +
                            "        StringBuilder sql = new StringBuilder(\"SELECT COUNT(*) as count FROM User\");\n" +
                            "        String whereClause = DbService.getWhereClause(criteria);\n" +
                            "        if(whereClause != null && whereClause.length() > 0) {\n" +
                            "            sql.append(whereClause);\n" +
                            "        }\n" +
                            "        logger.debug(\"sql=\" + sql);\n" +
                            "        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql.toString());\n" +
                            "        List<ODocument> list = graph.getRawGraph().command(query).execute();\n" +
                            "        return list.get(0).field(\"count\");\n" +
                            "    }\n" +
                            "\n" +
                            "    protected String getUserFromDb(OrientGraph graph, Map<String, Object> criteria) throws Exception {\n" +
                            "        String json = null;\n" +
                            "        StringBuilder sql = new StringBuilder(\"SELECT FROM User \");\n" +
                            "        String whereClause = DbService.getWhereClause(criteria);\n" +
                            "        if(whereClause != null && whereClause.length() > 0) {\n" +
                            "            sql.append(whereClause);\n" +
                            "        }\n" +
                            "\n" +
                            "        String sortedBy = (String)criteria.get(\"sortedBy\");\n" +
                            "        String sortDir = (String)criteria.get(\"sortDir\");\n" +
                            "        if(sortedBy != null) {\n" +
                            "            sql.append(\" ORDER BY \").append(sortedBy);\n" +
                            "            if(sortDir != null) {\n" +
                            "                sql.append(\" \").append(sortDir);\n" +
                            "            }\n" +
                            "        }\n" +
                            "        Integer pageSize = (Integer)criteria.get(\"pageSize\");\n" +
                            "        Integer pageNo = (Integer)criteria.get(\"pageNo\");\n" +
                            "        if(pageNo != null && pageSize != null) {\n" +
                            "            sql.append(\" SKIP \").append((pageNo - 1) * pageSize);\n" +
                            "            sql.append(\" LIMIT \").append(pageSize);\n" +
                            "        }\n" +
                            "        logger.debug(\"sql=\" + sql);\n" +
                            "        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql.toString());\n" +
                            "        List<ODocument> list = graph.getRawGraph().command(query).execute();\n" +
                            "        if(list.size() > 0) {\n" +
                            "            json = OJSONWriter.listToJSON(list, null);\n" +
                            "        }\n" +
                            "        return json;\n" +
                            "    }\n" +
                            "\n" +
                            "    boolean isEmail(String userIdEmail) {\n" +
                            "        Matcher matcher = pattern.matcher(userIdEmail);\n" +
                            "        return matcher.matches();\n" +
                            "    }\n" +
                            "\n" +
                            "    String generateToken(Vertex user, String clientId) throws Exception {\n" +
                            "        Map<String, Object> jwtMap = new LinkedHashMap<String, Object>();\n" +
                            "        jwtMap.put(\"@rid\", user.getId().toString());\n" +
                            "        jwtMap.put(\"userId\", user.getProperty(\"userId\"));\n" +
                            "        jwtMap.put(\"clientId\", clientId);\n" +
                            "        jwtMap.put(\"roles\", user.getProperty(\"roles\"));\n" +
                            "        return JwtUtil.getJwt(jwtMap);\n" +
                            "    }\n" +
                            "\n" +
                            "    boolean checkPassword(OrientGraph graph, Vertex user, String inputPassword) throws Exception {\n" +
                            "        Vertex credential = user.getProperty(\"credential\");\n" +
                            "        //Vertex credential = getCredential(graph, user);\n" +
                            "        String storedPassword = (String) credential.getProperty(\"password\");\n" +
                            "        return HashUtil.validatePassword(inputPassword, storedPassword);\n" +
                            "    }\n" +
                            "\n" +
                            "}\n",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", abstractUserRule);



            Vertex signinUserRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.user.SignInUserRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule.user;\n" +
                            "\n" +
                            "import org.clinical3po.backendservices.rule.Rule;\n" +
                            "import org.clinical3po.backendservices.util.HashUtil;\n" +
                            "import org.clinical3po.backendservices.util.ServiceLocator;\n" +
                            "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                            "import com.tinkerpop.blueprints.Vertex;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraph;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;\n" +
                            "\n" +
                            "import java.util.HashMap;\n" +
                            "import java.util.Map;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 14/09/14.\n" +
                            " *\n" +
                            " * AccessLevel A\n" +
                            " *\n" +
                            " */\n" +
                            "public class SignInUserRule extends AbstractUserRule implements Rule {\n" +
                            "    public boolean execute (Object ...objects) throws Exception {\n" +
                            "        Map<String, Object> inputMap = (Map<String, Object>) objects[0];\n" +
                            "        Map<String, Object> data = (Map<String, Object>) inputMap.get(\"data\");\n" +
                            "        String userIdEmail = (String) data.get(\"userIdEmail\");\n" +
                            "        String inputPassword = (String) data.get(\"password\");\n" +
                            "        Boolean rememberMe = (Boolean)data.get(\"rememberMe\");\n" +
                            "        String clientId = (String)data.get(\"clientId\");\n" +
                            "        String error = null;\n" +
                            "        // check if clientId is passed in.\n" +
                            "        if(clientId == null || clientId.trim().length() == 0) {\n" +
                            "            error = \"ClientId is required\";\n" +
                            "            inputMap.put(\"responseCode\", 400);\n" +
                            "        } else {\n" +
                            "            OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "            try {\n" +
                            "                Vertex user = null;\n" +
                            "                if(isEmail(userIdEmail)) {\n" +
                            "                    user = getUserByEmail(graph, userIdEmail);\n" +
                            "                } else {\n" +
                            "                    user = getUserByUserId(graph, userIdEmail);\n" +
                            "                }\n" +
                            "                if(user != null) {\n" +
                            "                    if(checkPassword(graph, user, inputPassword)) {\n" +
                            "                        String jwt = generateToken(user, clientId);\n" +
                            "                        if(jwt != null) {\n" +
                            "                            Map eventMap = getEventMap(inputMap);\n" +
                            "                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get(\"data\");\n" +
                            "                            inputMap.put(\"eventMap\", eventMap);\n" +
                            "                            Map<String, String> tokens = new HashMap<String, String>();\n" +
                            "                            tokens.put(\"accessToken\", jwt);\n" +
                            "                            if(rememberMe != null && rememberMe) {\n" +
                            "                                // generate refreshToken\n" +
                            "                                String refreshToken = HashUtil.generateUUID();\n" +
                            "                                tokens.put(\"refreshToken\", refreshToken);\n" +
                            "                                String hashedRefreshToken = HashUtil.md5(refreshToken);\n" +
                            "                                eventData.put(\"hashedRefreshToken\", hashedRefreshToken);\n" +
                            "                            }\n" +
                            "                            inputMap.put(\"result\", mapper.writeValueAsString(tokens));\n" +
                            "                            eventData.put(\"clientId\", clientId);\n" +
                            "                            eventData.put(\"userId\", user.getProperty(\"userId\"));\n" +
                            "                            eventData.put(\"host\", data.get(\"host\"));  // add host as refreshToken will be associate with host.\n" +
                            "                            eventData.put(\"logInDate\", new java.util.Date());\n" +
                            "                        }\n" +
                            "                    } else {\n" +
                            "                        error = \"Invalid password\";\n" +
                            "                        inputMap.put(\"responseCode\", 400);\n" +
                            "                    }\n" +
                            "                } else {\n" +
                            "                    error = \"Invalid userId or email\";\n" +
                            "                    inputMap.put(\"responseCode\", 400);\n" +
                            "                }\n" +
                            "            } catch (Exception e) {\n" +
                            "                logger.error(\"Exception:\", e);\n" +
                            "                throw e;\n" +
                            "            } finally {\n" +
                            "                graph.shutdown();\n" +
                            "            }\n" +
                            "        }\n" +
                            "        if(error != null) {\n" +
                            "            inputMap.put(\"result\", error);\n" +
                            "            return false;\n" +
                            "        } else {\n" +
                            "            return true;\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", signinUserRule);


            Vertex signinUserEvRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.user.SignInUserEvRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule.user;\n" +
                            "\n" +
                            "import org.clinical3po.backendservices.rule.Rule;\n" +
                            "\n" +
                            "import java.util.Map;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 8/28/2015.\n" +
                            " */\n" +
                            "public class SignInUserEvRule extends AbstractUserRule implements Rule {\n" +
                            "    public boolean execute (Object ...objects) throws Exception {\n" +
                            "        Map<String, Object> eventMap = (Map<String, Object>) objects[0];\n" +
                            "        Map<String, Object> data = (Map<String, Object>) eventMap.get(\"data\");\n" +
                            "        signIn(data);\n" +
                            "\n" +
                            "        // TODO update global online user count\n" +
                            "        return true;\n" +
                            "    }\n" +
                            "}\n",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", signinUserEvRule);


            Vertex replayEventRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.db.ReplayEventRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule.db;\n" +
                            "\n" +
                            "import com.fasterxml.jackson.core.type.TypeReference;\n" +
                            "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                            "import org.clinical3po.backendservices.rule.Rule;\n" +
                            "import org.clinical3po.backendservices.rule.RuleEngine;\n" +
                            "import org.clinical3po.backendservices.util.ServiceLocator;\n" +
                            "import org.clinical3po.backendservices.util.Util;\n" +
                            "import org.slf4j.LoggerFactory;\n" +
                            "\n" +
                            "import java.util.HashMap;\n" +
                            "import java.util.List;\n" +
                            "import java.util.Map;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 14/12/14.\n" +
                            " *\n" +
                            " * Replay event file to create or recreate aggregation. This rule does update db but\n" +
                            " * there is no EvRule available. This is a very special rule or endpoint.\n" +
                            " *\n" +
                            " * AccessLevel R [owner, admin, dbAdmin]\n" +
                            " *\n" +
                            " * Current AccessLevel R [owner] TODO access control for host\n" +
                            " */\n" +
                            "public class ReplayEventRule implements Rule {\n" +
                            "    static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReplayEventRule.class);\n" +
                            "    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();\n" +
                            "\n" +
                            "    public boolean execute (Object ...objects) throws Exception {\n" +
                            "        Map<String, Object> inputMap = (Map<String, Object>)objects[0];\n" +
                            "        Map<String, Object> data = (Map<String, Object>)inputMap.get(\"data\");\n" +
                            "        String error = null;\n" +
                            "        Map<String, Object> payload = (Map<String, Object>) inputMap.get(\"payload\");\n" +
                            "        if(payload == null) {\n" +
                            "            error = \"Login is required\";\n" +
                            "            inputMap.put(\"responseCode\", 401);\n" +
                            "        } else {\n" +
                            "            Map<String, Object> user = (Map<String, Object>)payload.get(\"user\");\n" +
                            "            List roles = (List)user.get(\"roles\");\n" +
                            "            if(!roles.contains(\"owner\") && !roles.contains(\"admin\") && !roles.contains(\"dbAdmin\")) {\n" +
                            "                error = \"Role owner or admin or dbAdmin is required to replay events\";\n" +
                            "                inputMap.put(\"responseCode\", 403);\n" +
                            "            } else {\n" +
                            "                String content = (String)data.get(\"content\");\n" +
                            "                // content may contains several events, parse it.\n" +
                            "                List<Map<String, Object>> events = mapper.readValue(content,\n" +
                            "                    new TypeReference<List<HashMap<String, Object>>>() {});\n" +
                            "                // clear all cache before replay. in the future it might be clear only the category\n" +
                            "                // that the events involved. TODO\n" +
                            "                ServiceLocator.getInstance().clearMemoryImage();\n" +
                            "                // replay event one by one.\n" +
                            "                for(Map<String, Object> event: events) {\n" +
                            "                    RuleEngine.getInstance().executeRule(Util.getEventRuleId(event), event);\n" +
                            "                }\n" +
                            "            }\n" +
                            "        }\n" +
                            "        if(error != null) {\n" +
                            "            inputMap.put(\"result\", error);\n" +
                            "            return false;\n" +
                            "        } else {\n" +
                            "            return true;\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", replayEventRule);


            Vertex getRuleMapRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.rule.GetRuleMapRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule.rule;\n" +
                            "\n" +
                            "import org.clinical3po.backendservices.rule.Rule;\n" +
                            "import org.clinical3po.backendservices.rule.RuleEngine;\n" +
                            "import org.clinical3po.backendservices.server.DbService;\n" +
                            "import org.clinical3po.backendservices.util.ServiceLocator;\n" +
                            "import com.tinkerpop.blueprints.Vertex;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraph;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;\n" +
                            "\n" +
                            "import java.util.List;\n" +
                            "import java.util.Map;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 14/02/15.\n" +
                            " *\n" +
                            " * This rule is used by the rule:load plugin in maven-plugin project to check if source code\n" +
                            " * has been changed. It returns a map from ruleClass to sourceCode for easy comparison.\n" +
                            " *\n" +
                            " * accessLevel is owner by default\n" +
                            " *\n" +
                            " */\n" +
                            "public class GetRuleMapRule extends AbstractRuleRule implements Rule {\n" +
                            "    public boolean execute (Object ...objects) throws Exception {\n" +
                            "        Map<String, Object> inputMap = (Map<String, Object>) objects[0];\n" +
                            "        Map<String, Object> payload = (Map<String, Object>) inputMap.get(\"payload\");\n" +
                            "        Map<String, Object> user = (Map<String, Object>) payload.get(\"user\");\n" +
                            "        String host = (String) user.get(\"host\");\n" +
                            "        OrientGraph graph = ServiceLocator.getInstance().getGraph();\n" +
                            "        String hostRuleMap = null;\n" +
                            "        try {\n" +
                            "            hostRuleMap = getRuleMap(graph, host);\n" +
                            "        } catch (Exception e) {\n" +
                            "            logger.error(\"Exception:\", e);\n" +
                            "            throw e;\n" +
                            "        } finally {\n" +
                            "            graph.shutdown();\n" +
                            "        }\n" +
                            "        if(hostRuleMap != null) {\n" +
                            "            inputMap.put(\"result\", hostRuleMap);\n" +
                            "            return true;\n" +
                            "        } else {\n" +
                            "            inputMap.put(\"result\", \"No rule can be found.\");\n" +
                            "            inputMap.put(\"responseCode\", 404);\n" +
                            "            return false;\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", getRuleMapRule);


            Vertex impRuleRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.rule.ImpRuleRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule.rule;\n" +
                            "\n" +
                            "import org.clinical3po.backendservices.rule.AbstractRule;\n" +
                            "import org.clinical3po.backendservices.rule.Rule;\n" +
                            "import org.clinical3po.backendservices.rule.RuleEngine;\n" +
                            "\n" +
                            "import java.util.List;\n" +
                            "import java.util.Map;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 30/12/14.\n" +
                            " * This is the rule that will be loaded by the db script in initDatabase to bootstrap rule\n" +
                            " * loading for others. Also, it can be used to import rules developed and tested locally from\n" +
                            " * Rule Admin interface.\n" +
                            " *\n" +
                            " * Warning: it will replace any existing rules if Rule Class is the same.\n" +
                            " *\n" +
                            " * AccessLevel R [owner, admin, ruleAdmin]\n" +
                            " *\n" +
                            " * current R [owner] until workflow is done.\n" +
                            " *\n" +
                            " */\n" +
                            "public class ImpRuleRule extends AbstractRule implements Rule {\n" +
                            "    public boolean execute (Object ...objects) throws Exception {\n" +
                            "        Map<String, Object> inputMap = (Map<String, Object>)objects[0];\n" +
                            "        Map<String, Object> data = (Map<String, Object>)inputMap.get(\"data\");\n" +
                            "        Map<String, Object> payload = (Map<String, Object>) inputMap.get(\"payload\");\n" +
                            "        Map<String, Object> user = (Map<String, Object>)payload.get(\"user\");\n" +
                            "        String ruleClass = (String)data.get(\"ruleClass\");\n" +
                            "        String error = null;\n" +
                            "        String host = (String)user.get(\"host\");\n" +
                            "        if(host != null) {\n" +
                            "            if(!host.equals(data.get(\"host\"))) {\n" +
                            "                error = \"User can only import rule from host: \" + host;\n" +
                            "                inputMap.put(\"responseCode\", 403);\n" +
                            "            } else {\n" +
                            "                // make sure the ruleClass contains the host.\n" +
                            "                if(host != null && !ruleClass.contains(host)) {\n" +
                            "                    // you are not allowed to update rule as it is not owned by the host.\n" +
                            "                    error = \"ruleClass is not owned by the host: \" + host;\n" +
                            "                    inputMap.put(\"responseCode\", 403);\n" +
                            "                } else {\n" +
                            "                    // Won't check if rule exists or not here.\n" +
                            "                    Map eventMap = getEventMap(inputMap);\n" +
                            "                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get(\"data\");\n" +
                            "                    inputMap.put(\"eventMap\", eventMap);\n" +
                            "                    eventData.put(\"host\", host);\n" +
                            "\n" +
                            "                    eventData.put(\"ruleClass\", ruleClass);\n" +
                            "                    eventData.put(\"sourceCode\", data.get(\"sourceCode\"));\n" +
                            "                    eventData.put(\"createDate\", new java.util.Date());\n" +
                            "                    eventData.put(\"createUserId\", user.get(\"userId\"));\n" +
                            "                }\n" +
                            "            }\n" +
                            "        } else {\n" +
                            "            // This is owner to import rule, notice that no host is passed in.\n" +
                            "            Map eventMap = getEventMap(inputMap);\n" +
                            "            Map<String, Object> eventData = (Map<String, Object>)eventMap.get(\"data\");\n" +
                            "            inputMap.put(\"eventMap\", eventMap);\n" +
                            "\n" +
                            "            eventData.put(\"ruleClass\", ruleClass);\n" +
                            "            eventData.put(\"sourceCode\", data.get(\"sourceCode\"));\n" +
                            "            eventData.put(\"createDate\", new java.util.Date());\n" +
                            "            eventData.put(\"createUserId\", user.get(\"userId\"));\n" +
                            "        }\n" +
                            "\n" +
                            "        if(error != null) {\n" +
                            "            inputMap.put(\"result\", error);\n" +
                            "            return false;\n" +
                            "        } else {\n" +
                            "            return true;\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", impRuleRule);


            Vertex impRuleEvRule = graph.addVertex("class:Rule",
                    "ruleClass", "org.clinical3po.backendservices.rule.rule.ImpRuleEvRule",
                    "sourceCode", "/*\n" +
                            " * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.\n" +
                            " *\n" +
                            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            " * you may not use this file except in compliance with the License.\n" +
                            " * You may obtain a copy of the License at\n" +
                            " *\n" +
                            " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                            " *\n" +
                            " * Unless required by applicable law or agreed to in writing, software\n" +
                            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            " * See the License for the specific language governing permissions and\n" +
                            " * limitations under the License.\n" +
                            " */\n" +
                            "\n" +
                            "package org.clinical3po.backendservices.rule.rule;\n" +
                            "\n" +
                            "import com.fasterxml.jackson.core.type.TypeReference;\n" +
                            "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                            "import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;\n" +
                            "import org.clinical3po.backendservices.rule.Rule;\n" +
                            "import org.clinical3po.backendservices.rule.RuleEngine;\n" +
                            "import org.clinical3po.backendservices.server.DbService;\n" +
                            "import org.clinical3po.backendservices.util.ServiceLocator;\n" +
                            "import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;\n" +
                            "import com.orientechnologies.orient.core.db.record.OIdentifiable;\n" +
                            "import com.orientechnologies.orient.core.index.OIndex;\n" +
                            "import com.orientechnologies.orient.core.metadata.schema.OSchema;\n" +
                            "import com.orientechnologies.orient.core.record.impl.ODocument;\n" +
                            "import com.tinkerpop.blueprints.Vertex;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraph;\n" +
                            "import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;\n" +
                            "import org.slf4j.LoggerFactory;\n" +
                            "\n" +
                            "import java.util.*;\n" +
                            "import java.util.concurrent.ConcurrentMap;\n" +
                            "\n" +
                            "/**\n" +
                            " * Created by w.ding on 30/12/14.\n" +
                            " */\n" +
                            "public class ImpRuleEvRule extends AbstractRuleRule implements Rule {\n" +
                            "    static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImpRuleEvRule.class);\n" +
                            "    public boolean execute (Object ...objects) throws Exception {\n" +
                            "        Map<String, Object> eventMap = (Map<String, Object>) objects[0];\n" +
                            "        Map<String, Object> data = (Map<String, Object>) eventMap.get(\"data\");\n" +
                            "        impRule(data);\n" +
                            "        return true;\n" +
                            "    }\n" +
                            "\n" +
                            "}\n",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", impRuleEvRule);

            // grant access to certain rules to everybody.
            Vertex accessGetMenuRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.menu.GetMenuRule",
                    "accessLevel", "A",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessGetMenuRule);

            Vertex accessGetFormRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.form.GetFormRule",
                    "accessLevel", "A",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessGetFormRule);

            Vertex accessLogEventRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.log.LogEventRule",
                    "accessLevel", "A",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessLogEventRule);

            Vertex accessSignInUserRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.user.SignInUserRule",
                    "accessLevel", "A",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessSignInUserRule);

            Vertex accessGetPageRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.page.GetPageRule",
                    "accessLevel", "A",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessGetPageRule);


            Vertex accessRefreshTokenRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.user.RefreshTokenRule",
                    "accessLevel", "A",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessRefreshTokenRule);


            Vertex accessSignUpUserRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.user.SignUpUserRule",
                    "accessLevel", "A",
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessSignUpUserRule);


            roles = new ArrayList<String>();
            roles.add("user");
            Vertex accessGetRoleDropdownRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.role.GetRoleDropdownRule",
                    "accessLevel", "R",
                    "roles", roles,
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessGetRoleDropdownRule);


            roles = new ArrayList<String>();
            roles.add("user");
            Vertex accessGetClientDropdownRule = graph.addVertex("class:Access",
                    "ruleClass", "org.clinical3po.backendservices.rule.client.GetClientDropdownRule",
                    "accessLevel", "R",
                    "roles", roles,
                    "createDate", new java.util.Date());
            userOwner.addEdge("Create", accessGetClientDropdownRule);

            /*
            // create a counter for feed injector requestId
            ODocument requestId = new ODocument(schema.getClass("Counter"));
            requestId.field("name", "injector.requestId");
            requestId.field("value", 1);
            requestId.save();

            // create a counter for class loan number
            ODocument loanNumber = new ODocument(schema.getClass("Counter"));
            loanNumber.field("name", "injector.loanNumber");
            loanNumber.field("value", 1000000000);
            loanNumber.save();
            */

            Vertex eventId = graph.addVertex("class:Counter",
                    "name", "eventId",
                    "value", 10000);

            graph.commit();

        } catch (Exception e) {
            graph.rollback();
            e.printStackTrace();
        } finally {
            graph.shutdown();
        }

            /*

            ODocument categoryAdmin = new ODocument(schema.getClass("Role"));
            categoryAdmin.field("id", "categoryAdmin");
            categoryAdmin.field("description", "admin category for the host");
            categoryAdmin.save();

            ODocument prodAdmin = new ODocument(schema.getClass("Role"));
            prodAdmin.field("id", "productAdmin");
            prodAdmin.field("description", "admin user that can do anything with product");
            prodAdmin.save();


            ODocument blogAdmin = new ODocument(schema.getClass("Role"));
            blogAdmin.field("id", "blogAdmin");
            blogAdmin.field("description", "admin user that can do anything with blog");
            blogAdmin.save();

            ODocument blogUser = new ODocument(schema.getClass("Role"));
            blogUser.field("id", "blogUser");
            blogUser.field("description", "user that can post blog and update his own blog");
            blogUser.save();

            ODocument forumAdmin = new ODocument(schema.getClass("Role"));
            forumAdmin.field("id", "forumAdmin");
            forumAdmin.field("description", "admin user that can do anything with forum");
            forumAdmin.save();
            */

            /*
            ODocument m_productAdmin = new ODocument(schema.getClass("MenuItem"));
            m_productAdmin.field("id", "productAdmin");
            m_productAdmin.field("label", "Product Admin");
            m_productAdmin.field("path", "/page/org.clinical3po.backendservices-v-product-admin-home");
            m_productAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_productAdmin.field("createDate", new java.util.Date());
            m_productAdmin.save();

            ODocument m_forumAdmin = new ODocument(schema.getClass("MenuItem"));
            m_forumAdmin.field("id", "forumAdmin");
            m_forumAdmin.field("label", "Forum Admin");
            m_forumAdmin.field("path", "/page/org.clinical3po.backendservices-v-forum-admin-home");
            m_forumAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_forumAdmin.field("createDate", new java.util.Date());
            m_forumAdmin.save();

            ODocument m_blogAdmin = new ODocument(schema.getClass("MenuItem"));
            m_blogAdmin.field("id", "blogAdmin");
            m_blogAdmin.field("label", "Blog Admin");
            m_blogAdmin.field("path", "/page/org.clinical3po.backendservices-v-blog-admin-home");
            m_blogAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_blogAdmin.field("createDate", new java.util.Date());
            m_blogAdmin.save();

            ODocument m_newsAdmin = new ODocument(schema.getClass("MenuItem"));
            m_newsAdmin.field("id", "newsAdmin");
            m_newsAdmin.field("label", "News Admin");
            m_newsAdmin.field("path", "/page/org.clinical3po.backendservices-v-news-admin-home");
            m_newsAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_newsAdmin.field("createDate", new java.util.Date());
            m_newsAdmin.save();

            ODocument m_proxyAdmin = new ODocument(schema.getClass("MenuItem"));
            m_proxyAdmin.field("id", "proxyAdmin");
            m_proxyAdmin.field("label", "Proxy Admin");
            m_proxyAdmin.field("path", "/page/org.clinical3po.backendservices-v-proxy-admin-home");
            m_proxyAdmin.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_proxyAdmin.field("createDate", new java.util.Date());
            m_proxyAdmin.save();

            */

            /*

            ODocument m_product = new ODocument(schema.getClass("MenuItem"));
            m_product.field("id", "product");
            m_product.field("label", "Product");
            m_product.field("path", "/page/org.clinical3po.backendservices-v-product-home");
            m_product.field("left", true);
            m_product.field("roles", "anonymous,user,prodAdmin,admin,owner");
            m_product.field("createUserId", ServiceLocator.getInstance().getOwnerId());
            m_product.field("createDate", new java.util.Date());
            m_product.save();
            */

        logger.debug("Done refreshing db");
    }

}
