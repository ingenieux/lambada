package br.com.ingenieux.lambada.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.InputStream;

import io.ingenieux.lambada.runtime.ApiGateway;
import io.ingenieux.lambada.runtime.LambadaFunction;

public class BlogService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LambadaFunction(timeout=60, api=@ApiGateway(path="/blogs/", method=ApiGateway.MethodType.POST))
    public String createBlog(InputStream inputStream, Context context) throws Exception {
        /*
        ObjectNode requestAsObject = OBJECT_MAPPER.convertValue(request, ObjectNode.class);

        System.err.println("createBlog(): " + requestAsObject);

        final ObjectNode result = OBJECT_MAPPER.createObjectNode();

        result.put("id", "new-blog");

        return OBJECT_MAPPER.writeValueAsString(result);
        */
        return "null";
    }

    @LambadaFunction(timeout=60, api=@ApiGateway(path="/blogs/{blogId}/", method=ApiGateway.MethodType.POST))
    public String createPost(InputStream inputStream, Context context) throws Exception {
        /*
        ObjectNode requestAsObject = OBJECT_MAPPER.convertValue(request, ObjectNode.class);

        System.err.println("createPost(): " + requestAsObject);

        final ObjectNode result = OBJECT_MAPPER.createObjectNode();

        result.put("id", "new-blog");

        return OBJECT_MAPPER.writeValueAsString(result);
        */
        return "null";
    }
}
