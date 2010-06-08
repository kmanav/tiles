/**
 *
 */
package org.apache.tiles.web.util;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.AttributeContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.util.ApplicationAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link TilesDecorationFilter}.
 *
 * @version $Rev$ $Date$
 */
public class TilesDecorationFilterTest {

    private FilterConfig config;

    private ServletContext servletContext;

    private TilesDecorationFilter filter;

    /**
     * Sets up the test.
     * @throws ServletException If something goes wrong.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws ServletException {
        config = createMock(FilterConfig.class);
        servletContext = createMock(ServletContext.class);
        Enumeration<String> names = createMock(Enumeration.class);

        expect(config.getServletContext()).andReturn(servletContext);
        expect(config.getInitParameter(TilesDecorationFilter.CONTAINER_KEY_INIT_PARAMETER)).andReturn("key");
        expect(config.getInitParameter("attribute-name")).andReturn("attributeKey");
        expect(config.getInitParameter("definition")).andReturn("definitionKey");
        expect(config.getInitParameter("prevent-token")).andReturn("tokenKey");
        expect(names.hasMoreElements()).andReturn(true);
        expect(names.nextElement()).andReturn("definition(hello*)");
        expect(names.hasMoreElements()).andReturn(false);
        expect(config.getInitParameterNames()).andReturn(names);
        expect(config.getInitParameter("definition(hello*)")).andReturn("alternateDef");
        expect(config.getInitParameter("mutator")).andReturn(CustomAttributeMutator.class.getName());

        replay(config, names);
        filter = new TilesDecorationFilter();
        filter.init(config);
        verify(names);
    }

    /**
     * Tears down the test.
     */
    @After
    public void tearDown() {
        verify(config, servletContext);
    }

    /**
     * Test method for {@link org.apache.tiles.web.util.TilesDecorationFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}.
     * @throws ServletException If something goes wrong
     * @throws IOException If something goes wrong.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDoFilter() throws IOException, ServletException {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        FilterChain chain = createMock(FilterChain.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> applicationScope = createMock(Map.class);
        TilesContainer container = createMock(TilesContainer.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);

        expect(request.getAttribute("org.apache.tiles.decoration.PREVENT:tokenKey")).andReturn(null);
        expect(servletContext.getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE)).andReturn(applicationContext);
        expect(applicationContext.getApplicationScope()).andReturn(applicationScope);
        expect(applicationScope.get("key")).andReturn(container);
        expect(container.getAttributeContext(isA(ServletRequest.class))).andReturn(attributeContext);
        request.setAttribute("org.apache.tiles.decoration.PREVENT:tokenKey", true);
        expect(request.getAttribute("javax.servlet.include.servlet_path")).andReturn(null);
        expect(request.getServletPath()).andReturn("/tiles");
        container.render(eq("definitionKey"), isA(ServletRequest.class));
        chain.doFilter(request, response);

        replay(servletContext, request, response, chain, applicationContext, applicationScope, container, attributeContext);
        filter.doFilter(request, response, chain);
        verify(request, response, chain, applicationContext, applicationScope, container, attributeContext);
    }

    public static class CustomAttributeMutator implements AttributeContextMutator {

        @Override
        public void mutate(AttributeContext context,
                javax.servlet.ServletRequest request) {
            // Does nothing.
        }
    }
}
