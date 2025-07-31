package com.example.handler;

import com.example.controller.ApiController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApiHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ApiController apiController = new ApiController();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            String uri = request.uri();
            HttpMethod method = request.method();
            
            logger.info("Received {} request for {}", method, uri);
            
            FullHttpResponse response = handleRequest(request);
            
            // Set CORS headers
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");
            
            // Send response
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            
            ctx.writeAndFlush(response).addListener(keepAlive ? null : ChannelFutureListener.CLOSE);
            
        } catch (Exception e) {
            logger.error("Error handling request", e);
            sendErrorResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private FullHttpResponse handleRequest(FullHttpRequest request) throws Exception {
        String uri = request.uri();
        HttpMethod method = request.method();
        
        // Handle OPTIONS requests for CORS
        if (method == HttpMethod.OPTIONS) {
            return createResponse(HttpResponseStatus.OK, "{}");
        }
        
        // Parse query parameters
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri);
        String path = queryDecoder.path();
        
        // Route requests
        if (path.startsWith("/api/")) {
            return handleApiRequest(path, method);
        }
        
        // Default 404 for unknown paths
        return createResponse(HttpResponseStatus.NOT_FOUND, "{\"error\":\"Not Found\"}");
    }

    private FullHttpResponse handleApiRequest(String path, HttpMethod method) throws Exception {
        switch (path) {
            case "/api/not-found":
                if (method == HttpMethod.GET) {
                    Map<String, Object> response = apiController.alwaysNotFound();
                    return createJsonResponse(HttpResponseStatus.NOT_FOUND, response);
                }
                break;
                
            case "/api/greeting":
                if (method == HttpMethod.GET) {
                    String greeting = apiController.getGreeting();
                    return createTextResponse(HttpResponseStatus.OK, greeting);
                }
                break;
        }
        
        // Method not allowed
        return createResponse(HttpResponseStatus.METHOD_NOT_ALLOWED, "{\"error\":\"Method Not Allowed\"}");
    }

    private FullHttpResponse createJsonResponse(HttpResponseStatus status, Object data) throws Exception {
        String json = objectMapper.writeValueAsString(data);
        return createResponse(status, json);
    }

    private FullHttpResponse createTextResponse(HttpResponseStatus status, String content) {
        ByteBuf buffer = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buffer.readableBytes());
        return response;
    }

    private FullHttpResponse createResponse(HttpResponseStatus status, String content) {
        ByteBuf buffer = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buffer.readableBytes());
        return response;
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        FullHttpResponse response = createResponse(status, "{\"error\":\"" + message + "\"}");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Channel exception caught", cause);
        ctx.close();
    }
} 