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

import com.google.common.base.Charsets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A Abstract Metadata Extractor for Lambada Mojos
 */
public abstract class AbstractLambadaMetadataMojo extends AbstractMojo {
  public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;

  /**
   * Maven Project
   */
  @Parameter(defaultValue = "${project}", required = true)
  protected MavenProject project;

  static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper()
          .enable(SerializationFeature.INDENT_OUTPUT)
          .enable(DeserializationFeature.WRAP_EXCEPTIONS)
          .enable(SerializationFeature.INDENT_OUTPUT)
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .enable(JsonParser.Feature.ALLOW_COMMENTS)
          .enable(JsonParser.Feature.ALLOW_YAML_COMMENTS)
          .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
          .setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

  protected String defaultIfBlank(String one, String another) {
    return (null != one && (!"".equals(one.trim()))) ? one : another;
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

  public void execute() throws MojoExecutionException {
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
