/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.mock.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author eric.wittmann@redhat.com
 */
public class RtgovDatabaseSeeder extends HttpServlet {

    private static final long serialVersionUID = -4479968720865761165L;
    private static boolean done = false;

    /**
     * Constructor.
     */
    public RtgovDatabaseSeeder() {
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        if (!done) {
            done = true;
            seedDatabase();
        }

        resp.setContentType("text/plain");
        ServletOutputStream outputStream = resp.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
        writer.println("DONE");
        outputStream.flush();
    }

    /**
     * Do it.
     * @throws ServletException
     */
    private void seedDatabase() throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:jboss/GadgetServer");
            seedDB(ds);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * @param ds
     * @throws SQLException
     * @throws IOException
     */
    private void seedDB(DataSource ds) throws SQLException, IOException {
        Connection connection = ds.getConnection();
        try {
            String sql = DB_SEED_DATA;
            BufferedReader reader = new BufferedReader(new StringReader(sql));
            String line = null;
            while ( (line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    System.out.println(" DB> " + line);
                    connection.createStatement().execute(line);
                }
            }
        } finally {
            connection.close();
        }
    }

    private static final String DB_SEED_DATA =
            "INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Response Time','Jeff Yu','jeffyu@overlord.com','Response Time Gadget','http://localhost:8080/gadgets/rt-gadget/thumbnail.png','http://localhost:8080/gadgets/rt-gadget/gadget.xml');\r\n" +
            "INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Date & Time','Google','admin@google.com','Add a clock to your page. Click edit to change it to the color of your choice','http://gadgets.adwebmaster.net/images/gadgets/datetimemulti/thumbnail_en.jpg','http://www.gstatic.com/ig/modules/datetime_v3/datetime_v3.xml');\r\n" +
            "INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Currency Converter','Google','info@tofollow.com','currency converter widget','http://www.gstatic.com/ig/modules/currency_converter/currency_converter_content/en_us-thm.cache.png','http://www.gstatic.com/ig/modules/currency_converter/currency_converter_v2.xml');\r\n" +
            "INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Economic Data - ALFRED Graph','Research Department','webmaster@research.stlouisfed.org','Vintage Economic Data from the Federal Reserve Bank of St. Louis','http://research.stlouisfed.org/gadgets/images/alfredgraphgadgetthumbnail.png','http://research.stlouisfed.org/gadgets/code/alfredgraph.xml');";

}
