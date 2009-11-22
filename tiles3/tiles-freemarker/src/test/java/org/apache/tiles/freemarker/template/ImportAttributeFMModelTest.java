/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.freemarker.template;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tiles.Attribute;
import org.apache.tiles.freemarker.context.FreeMarkerTilesRequestContext;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.util.ApplicationContextUtil;
import org.apache.tiles.template.ImportAttributeModel;
import org.junit.Before;
import org.junit.Test;

import freemarker.core.Environment;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.utility.DeepUnwrap;

/**
 * Tests {@link ImportAttributeFMModel}.
 *
 * @version $Rev$ $Date$
 */
public class ImportAttributeFMModelTest {

    /**
     * The FreeMarker environment.
     */
    private Environment env;

    /**
     * The locale object.
     */
    private Locale locale;

    /**
     * The template.
     */
    private Template template;

    /**
     * The template model.
     */
    private TemplateHashModel model;

    /**
     * The writer.
     */
    private StringWriter writer;

    /**
     * The object wrapper.
     */
    private ObjectWrapper objectWrapper;

    /**
     * Sets up the model.
     */
    @Before
    public void setUp() {
        template = createMock(Template.class);
        model = createMock(TemplateHashModel.class);
        expect(template.getMacros()).andReturn(new HashMap<Object, Object>());
        writer = new StringWriter();
        objectWrapper = DefaultObjectWrapper.getDefaultInstance();
    }

    /**
     * Test method for {@link org.apache.tiles.freemarker.template.ImportAttributeFMModel
     * #execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[],
     * freemarker.template.TemplateDirectiveBody)}.
     * @throws TemplateException If something goes wrong.
     */
    @Test
    public void testExecute() throws TemplateException {
        ImportAttributeModel tModel = createMock(ImportAttributeModel.class);
        ImportAttributeFMModel fmModel = new ImportAttributeFMModel(tModel);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        HttpServletRequest request = createMock(HttpServletRequest.class);
        replay(request);
        HttpRequestHashModel requestModel = new HttpRequestHashModel(request, objectWrapper);

        GenericServlet servlet = createMock(GenericServlet.class);
        ServletContext servletContext = createMock(ServletContext.class);
        expect(servletContext.getAttribute(ApplicationContextUtil.APPLICATION_CONTEXT_ATTRIBUTE))
                .andReturn(applicationContext);
        expect(servlet.getServletContext()).andReturn(servletContext).times(2);
        replay(servlet, servletContext);
        ServletContextHashModel servletContextModel = new ServletContextHashModel(servlet, objectWrapper);
        expect(model.get(FreemarkerServlet.KEY_REQUEST)).andReturn(requestModel).anyTimes();
        expect(model.get(FreemarkerServlet.KEY_APPLICATION)).andReturn(servletContextModel);
        expect(template.getObjectWrapper()).andReturn(objectWrapper).times(2);
        initEnvironment();

        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        Map<String, Object> params = new HashMap<String, Object>();
        Attribute attribute = createMock(Attribute.class);
        params.put("name", objectWrapper.wrap("myName"));
        params.put("toName", objectWrapper.wrap("myToName"));
        params.put("ignore", objectWrapper.wrap(false));

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("one", "value1");
        attributes.put("two", "value2");
        expect(tModel.getImportedAttributes(eq("myName"), eq("myToName"), eq(false),
                isA(FreeMarkerTilesRequestContext.class))).andReturn(attributes);

        replay(tModel, body, attribute, applicationContext);
        fmModel.execute(env, params, null, body);
        assertEquals("value1", DeepUnwrap.unwrap(env.getCurrentNamespace().get("one")));
        assertEquals("value2", DeepUnwrap.unwrap(env.getCurrentNamespace().get("two")));
        verify(template, model, request, tModel, body, servlet, servletContext,
                attribute, applicationContext);
    }

    /**
     * Test method for {@link org.apache.tiles.freemarker.template.ImportAttributeFMModel
     * #execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[],
     * freemarker.template.TemplateDirectiveBody)}.
     * @throws TemplateException If something goes wrong.
     */
    @Test
    public void testExecuteRequest() throws TemplateException {
        ImportAttributeModel tModel = createMock(ImportAttributeModel.class);
        ImportAttributeFMModel fmModel = new ImportAttributeFMModel(tModel);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        HttpServletRequest request = createMock(HttpServletRequest.class);
        request.setAttribute("one", "value1");
        request.setAttribute("two", "value2");
        replay(request);
        HttpRequestHashModel requestModel = new HttpRequestHashModel(request, objectWrapper);

        GenericServlet servlet = createMock(GenericServlet.class);
        ServletContext servletContext = createMock(ServletContext.class);
        expect(servletContext.getAttribute(ApplicationContextUtil.APPLICATION_CONTEXT_ATTRIBUTE))
                .andReturn(applicationContext);
        expect(servlet.getServletContext()).andReturn(servletContext).times(2);
        replay(servlet, servletContext);
        ServletContextHashModel servletContextModel = new ServletContextHashModel(servlet, objectWrapper);
        expect(model.get(FreemarkerServlet.KEY_REQUEST)).andReturn(requestModel).anyTimes();
        expect(model.get(FreemarkerServlet.KEY_APPLICATION)).andReturn(servletContextModel);
        initEnvironment();

        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        Map<String, Object> params = new HashMap<String, Object>();
        Attribute attribute = createMock(Attribute.class);
        params.put("name", objectWrapper.wrap("myName"));
        params.put("toName", objectWrapper.wrap("myToName"));
        params.put("ignore", objectWrapper.wrap(false));
        params.put("scope", objectWrapper.wrap("request"));

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("one", "value1");
        attributes.put("two", "value2");
        expect(tModel.getImportedAttributes(eq("myName"), eq("myToName"), eq(false),
                isA(FreeMarkerTilesRequestContext.class))).andReturn(attributes);

        replay(tModel, body, attribute, applicationContext);
        fmModel.execute(env, params, null, body);
        verify(template, model, request, tModel, body, servlet, servletContext,
                attribute, applicationContext);
    }

    /**
     * Test method for {@link org.apache.tiles.freemarker.template.ImportAttributeFMModel
     * #execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[],
     * freemarker.template.TemplateDirectiveBody)}.
     * @throws TemplateException If something goes wrong.
     */
    @Test
    public void testExecuteSession() throws TemplateException {
        ImportAttributeModel tModel = createMock(ImportAttributeModel.class);
        ImportAttributeFMModel fmModel = new ImportAttributeFMModel(tModel);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpSession session = createMock(HttpSession.class);
        expect(request.getSession()).andReturn(session).times(2);
        session.setAttribute("one", "value1");
        session.setAttribute("two", "value2");
        replay(request, session);
        HttpRequestHashModel requestModel = new HttpRequestHashModel(request, objectWrapper);

        GenericServlet servlet = createMock(GenericServlet.class);
        ServletContext servletContext = createMock(ServletContext.class);
        expect(servletContext.getAttribute(ApplicationContextUtil.APPLICATION_CONTEXT_ATTRIBUTE))
                .andReturn(applicationContext);
        expect(servlet.getServletContext()).andReturn(servletContext).times(2);
        replay(servlet, servletContext);
        ServletContextHashModel servletContextModel = new ServletContextHashModel(servlet, objectWrapper);
        expect(model.get(FreemarkerServlet.KEY_REQUEST)).andReturn(requestModel).anyTimes();
        expect(model.get(FreemarkerServlet.KEY_APPLICATION)).andReturn(servletContextModel);
        initEnvironment();

        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        Map<String, Object> params = new HashMap<String, Object>();
        Attribute attribute = createMock(Attribute.class);
        params.put("name", objectWrapper.wrap("myName"));
        params.put("toName", objectWrapper.wrap("myToName"));
        params.put("ignore", objectWrapper.wrap(false));
        params.put("scope", objectWrapper.wrap("session"));

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("one", "value1");
        attributes.put("two", "value2");
        expect(tModel.getImportedAttributes(eq("myName"), eq("myToName"), eq(false),
                isA(FreeMarkerTilesRequestContext.class))).andReturn(attributes);

        replay(tModel, body, attribute, applicationContext);
        fmModel.execute(env, params, null, body);
        verify(template, model, request, session, tModel, body, servlet,
                servletContext, attribute, applicationContext);
    }

    /**
     * Test method for {@link org.apache.tiles.freemarker.template.ImportAttributeFMModel
     * #execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[],
     * freemarker.template.TemplateDirectiveBody)}.
     * @throws TemplateException If something goes wrong.
     */
    @Test
    public void testExecuteApplication() throws TemplateException {
        ImportAttributeModel tModel = createMock(ImportAttributeModel.class);
        ImportAttributeFMModel fmModel = new ImportAttributeFMModel(tModel);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        HttpServletRequest request = createMock(HttpServletRequest.class);
        replay(request);
        HttpRequestHashModel requestModel = new HttpRequestHashModel(request, objectWrapper);

        GenericServlet servlet = createMock(GenericServlet.class);
        ServletContext servletContext = createMock(ServletContext.class);
        expect(servlet.getServletContext()).andReturn(servletContext).anyTimes();
        expect(servletContext.getAttribute(ApplicationContextUtil.APPLICATION_CONTEXT_ATTRIBUTE))
                .andReturn(applicationContext);
        servletContext.setAttribute("one", "value1");
        servletContext.setAttribute("two", "value2");
        replay(servlet, servletContext);
        ServletContextHashModel servletContextModel = new ServletContextHashModel(servlet, objectWrapper);
        expect(model.get(FreemarkerServlet.KEY_REQUEST)).andReturn(requestModel).anyTimes();
        expect(model.get(FreemarkerServlet.KEY_APPLICATION)).andReturn(servletContextModel).anyTimes();
        initEnvironment();

        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        Map<String, Object> params = new HashMap<String, Object>();
        Attribute attribute = createMock(Attribute.class);
        params.put("name", objectWrapper.wrap("myName"));
        params.put("toName", objectWrapper.wrap("myToName"));
        params.put("ignore", objectWrapper.wrap(false));
        params.put("scope", objectWrapper.wrap("application"));

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("one", "value1");
        attributes.put("two", "value2");
        expect(tModel.getImportedAttributes(eq("myName"), eq("myToName"), eq(false),
                isA(FreeMarkerTilesRequestContext.class))).andReturn(attributes);

        replay(tModel, body, attribute, applicationContext);
        fmModel.execute(env, params, null, body);
        verify(template, model, request, tModel, body, servlet, servletContext,
                attribute, applicationContext);
    }

    /**
     * Initializes the FreeMarker environment.
     */
    private void initEnvironment() {
        replay(template, model);
        env = new Environment(template, model, writer);
        locale = Locale.ITALY;
        env.setLocale(locale);
    }
}
