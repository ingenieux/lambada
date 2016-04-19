package io.ingenieux.lambada.testing;

import com.amazonaws.services.lambda.runtime.ClientContext;

import org.immutables.value.Value;

/**
 * Created by aldrin on 19/04/16.
 */
@Value.Immutable
public interface LambadaClientContext extends ClientContext {
}
