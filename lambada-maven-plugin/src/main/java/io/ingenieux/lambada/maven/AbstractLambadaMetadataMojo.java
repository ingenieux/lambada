package io.ingenieux.lambada.maven;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.ingenieux.lambada.runtime.LambadaFunction;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A Abstract Metadata Extractor for Lambada Mojos
 */
public abstract class AbstractLambadaMetadataMojo extends AbstractMojo {
    /**
     * Maven Project
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }

    protected String defaultIfBlank(String one, String another) {
        return (null != one && (!one.trim().equals(""))) ? one : another;
    }

    public void setClasspathUrls(ConfigurationBuilder configurationBuilder) {
        List<String> classpathElements = null;
        try {
            classpathElements = project.getCompileClasspathElements();
            List<URL> projectClasspathList = new ArrayList<URL>();

            for (String element : classpathElements) {
                projectClasspathList.add(new File(element).toURI().toURL());
            }

            configurationBuilder.addUrls(projectClasspathList);

            configurationBuilder.addClassLoader(new URLClassLoader(projectClasspathList.toArray(new URL[0]), Thread.currentThread().getContextClassLoader()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void execute()
            throws MojoExecutionException {
        try {
            executeInternal();
        } catch (Exception exc) {
            throw new MojoExecutionException("Failure", exc);
        }
    }

    protected abstract void executeInternal() throws Exception;

    protected Set<Method> extractRuntimeAnnotations(Class<? extends Annotation> annotation) {
        ConfigurationBuilder cfg = new ConfigurationBuilder();

        cfg.setScanners(new MethodAnnotationsScanner());

        setClasspathUrls(cfg);

        final Reflections ref = new Reflections(cfg);

        return ref.getMethodsAnnotatedWith(annotation);
    }
}
