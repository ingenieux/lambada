package io.ingenieux.lambada.maven;

import com.amazonaws.services.lambda.runtime.Context;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.ingenieux.lambada.runtime.ApiGateway;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.TreeSet;

@Mojo(name = "serve", requiresDirectInvocation = true, requiresProject = true,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ServeMojo extends AbstractLambadaMetadataMojo {
    /**
     * Port to Use
     */
    @Parameter(property = "lambada.port", defaultValue = "8080")
    Integer port;

    private final Set<PathHandler> handlers = new TreeSet<>();

    static class PathHandler implements Comparable<PathHandler> {
        String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        ApiGateway.MethodType methodType;

        public ApiGateway.MethodType getMethodType() {
            return methodType;
        }

        public void setMethodType(ApiGateway.MethodType methodType) {
            this.methodType = methodType;
        }

        Object instance;

        public Object getInstance() {
            return instance;
        }

        public void setInstance(Object instance) {
            this.instance = instance;
        }

        Method method;

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        @Override
        public int compareTo(PathHandler o) {
            return path.compareTo(o.path);
        }

        public boolean matchesRequest(HttpExchange event) {
            if (!event.getRequestMethod().equalsIgnoreCase(methodType.name()))
                return false;

            return true;
        }
    }

    @Override
    protected void executeInternal() throws Exception {
        final Set<Method> methodsAnnotatedWith = extractRuntimeAnnotations(ApiGateway.class);

        for (Method m : methodsAnnotatedWith) {
            ApiGateway a = m.getAnnotation(ApiGateway.class);
            PathHandler pathHandler = new PathHandler();

            pathHandler.setPath(a.path());
            pathHandler.setMethod(m);

            boolean bStatic = Modifier.isStatic(m.getModifiers());

            if (!bStatic) {
                pathHandler.setInstance(m.getDeclaringClass().newInstance());
            }

            pathHandler.setMethodType(a.method());

            handlers.add(pathHandler);
        }

        serve();


    }

    public void serve() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        getLog().info("Using port: " + port);

        for (PathHandler p : handlers) {
            getLog().info("Adding handler for path " + p.getPath());

            server.createContext(p.getPath(), new ServeHandler(p));
        }

        server.start();

        while (10 != System.in.read())
            ;

        server.stop(60000);
    }

    public class ServeHandler implements HttpHandler {
        PathHandler p;

        public ServeHandler(PathHandler p) {
            this.p = p;
        }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (!p.matchesRequest(ex)) {
                ex.sendResponseHeaders(404, 0L);
                ex.close();

                return;
            }

            try {
                byte[] output = invokePath(p, ex, ex.getRequestBody());

                ex.sendResponseHeaders(200, output.length);
                //ex.getRequestHeaders().set("Content-Type", "application/json; charset=utf8");
                ex.getResponseBody().write(output);
                ex.close();
            } catch (Exception e) {
                byte[] eAsByteArray = e.toString().getBytes();

                ex.sendResponseHeaders(500, eAsByteArray.length);
                ex.getRequestHeaders().set("Content-Type", "text/plain");
                ex.getResponseBody().write(eAsByteArray);
                ex.close();
            }
        }
    }

    static class ArgumentBuilder {
        int index = 0;

        Method method;

        Object[] args;

        Class<?>[] types;

        public ArgumentBuilder(Method m) {
            this.method = m;
            this.args = new Object[m.getParameterCount()];
            this.types = m.getParameterTypes();
        }

        public boolean matchAndLoad(Class<?> src, Object value) {
            if ((args.length + 1) < index)
                return false;

            if (!types[index].isAssignableFrom(src))
                return false;

            args[index] = value;

            index++;

            return true;
        }

        public Object[] getArgs() {
            return args;
        }
    }

    byte[] invokePath(PathHandler handler, HttpExchange event, InputStream payload) throws Exception {
        /**
         * Situation #1:
         *
         * InputStream, OutputStream, Context
         */
        {
            ArgumentBuilder a = new ArgumentBuilder(handler.getMethod());

            InputStream is = payload;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Context fakeContext = new FakeContext();

            a.matchAndLoad(InputStream.class, is);
            a.matchAndLoad(OutputStream.class, baos);
            a.matchAndLoad(Context.class, fakeContext);


            handler.method.invoke(handler.instance, a.getArgs());

            return baos.toByteArray();
        }

    }
}
