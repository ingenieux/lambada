package io.ingenieux.lambada.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.ingenieux.lambada.maven.ann.LambadaFunction;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.serializers.XmlSerializer;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Mojo(name = "generate",
        requiresProject = true,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class LambadaGenerateMojo
        extends AbstractMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}/META-INF/lambada/lambada-functions.json", property = "lambada.outputFile", required = true)
    private File outputFile;
    /**
     * Maven Project
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    public void execute()
            throws MojoExecutionException {
        try {
            executeInternal();
        } catch (Exception exc) {
            throw new MojoExecutionException("Failure", exc);
        }
    }

    private void executeInternal() throws Exception {
        outputFile.getParentFile().mkdirs();

        ConfigurationBuilder cfg = new ConfigurationBuilder();

        cfg.setScanners(new MethodAnnotationsScanner());

        setClasspathUrls(cfg);

        final Reflections ref = new Reflections(cfg);

        final Set<Method> methodsAnnotatedWith = ref.getMethodsAnnotatedWith(LambadaFunction.class);

        final TreeSet<LambadaFunctionDefinition> definitionTreeSet = methodsAnnotatedWith.stream().map(m -> {
            LambadaFunction lF = m.getAnnotation(LambadaFunction.class);

            final LambadaFunctionDefinition result = new LambadaFunctionDefinition();

            final String name = defaultIfBlank(lF.name(), m.getName());

            result.setName(name); // #1

            final String handler = m.getDeclaringClass().getCanonicalName() + "::" + m.getName();

            result.setHandler(handler);

            result.setMemorySize(lF.memorySize()); // #2

            if (isNotBlank(lF.role())) {
                result.setRole(lF.role()); // #3
            }

            result.setTimeout(lF.timeout());  // #4

            if (isNotBlank(lF.description())) {
                result.setDescription(lF.description());  // #5
            }

            return result;
        }).collect(Collectors.toCollection(TreeSet::new));

        final List<LambadaFunctionDefinition> defList = new ArrayList<>(definitionTreeSet);

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        objectMapper.writeValue(new FileOutputStream(outputFile), defList);
    }

    public void setClasspathUrls(ConfigurationBuilder configurationBuilder) {
        List<String> classpathElements = null;
        try {
            classpathElements = project.getCompileClasspathElements();
            List<URL> projectClasspathList = new ArrayList<URL>();

            for (String element : classpathElements) {
                projectClasspathList.add(new File(element).toURI().toURL());
            }

            //for (Artifact a : project.getArtifacts()) {
            //    projectClasspathList.add(a.getFile().toURI().toURL());
            //}

            configurationBuilder.addUrls(projectClasspathList);

            configurationBuilder.addClassLoader(new URLClassLoader(projectClasspathList.toArray(new URL[0]), Thread.currentThread().getContextClassLoader()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
