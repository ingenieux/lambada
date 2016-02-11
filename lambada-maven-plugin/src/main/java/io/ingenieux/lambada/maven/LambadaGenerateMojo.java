/*
 * Copyright (c) 2016 ingenieux Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import io.ingenieux.lambada.runtime.LambadaFunction;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.codehaus.plexus.util.StringUtils.isNotBlank;

@Mojo(name = "generate",
        requiresProject = true,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class LambadaGenerateMojo
        extends AbstractLambadaMetadataMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}/META-INF/lambada/lambada-functions.json", property = "lambada.outputFile", required = true)
    private File outputFile;

    @Override
    protected void executeInternal() throws Exception {
        outputFile.getParentFile().mkdirs();

        final Set<Method> methodsAnnotatedWith = extractRuntimeAnnotations(LambadaFunction.class);

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

        OBJECT_MAPPER.writeValue(new FileOutputStream(outputFile), defList);
    }

}
