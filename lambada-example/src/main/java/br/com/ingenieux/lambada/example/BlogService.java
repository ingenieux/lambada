package br.com.ingenieux.lambada.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.ingenieux.lambada.runtime.ApiGateway;
import io.ingenieux.lambada.runtime.LambadaFunction;
import io.ingenieux.lambada.runtime.model.PassthoughRequest;

public class BlogService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LambadaFunction(timeout=60, api=@ApiGateway(path="/blogs/", method=ApiGateway.MethodType.POST))
    public void createBlog(InputStream inputStream, OutputStream outputStream, Context context) throws Exception {
        String inContent = IOUtils.toString(inputStream);

        PassthoughRequest<ObjectNode> req =
                PassthoughRequest.getRequest(
                        OBJECT_MAPPER,
                        new ByteArrayInputStream(inContent.getBytes()));

        System.err.println(OBJECT_MAPPER.writeValueAsString(req));

        IOUtils.write("{\"ok\"}", outputStream);
//        String inContent = IOUtils.toString(inputStream);
//
//        System.out.println(inContent);
//
//        IOUtils.write("{\"ok\"}", outputStream);
//        /*
//        ObjectNode requestAsObject = OBJECT_MAPPER.convertValue(request, ObjectNode.class);
//
//        System.err.println("createBlog(): " + requestAsObject);
//
//        final ObjectNode result = OBJECT_MAPPER.createObjectNode();
//
//        result.put("id", "new-blog");
//
//        return OBJECT_MAPPER.writeValueAsString(result);
//        */
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
