package io.ingenieux.lambada.runtime.model;

/**
 * Created by aldrin on 16/04/16.
 */
public interface BodyFunction<I, O> {
    public O execute(I input) throws Exception;
}
