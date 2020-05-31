package com.huawei.springbootweb.controller;

import com.google.gson.Gson;
import com.huawei.springbootweb.model.qo.QueryQo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@RestController("/demo")
@Api(description = "controller上的描述信息")
public class DemoController {

    @Autowired
    private JedisPool jedisPool;

    @ApiOperation(value = "Get请求")
    @GetMapping("/get/{docId}")
    public String get(@PathVariable("docId") String docId) {
        Jedis resource = jedisPool.getResource();
        Map<String, String> map = new HashMap<>();
        map.put("test", docId);

        IntStream.range(0, 1000).forEach(i -> map.put("key" + i, "value" + i));
        System.out.println(resource.hmset("hashMap", map));
        return " restful 【GET】method with parameter : " + docId;
    }

    @ApiOperation(value = "无参数")
    @GetMapping("none")
    public String none() {
        Jedis resource = jedisPool.getResource();
        String test = resource.get("test");
        List<String> hmget = resource.hmget("hashMap", "key200");

        List<String> hashMap = resource.hmget("hashMap");
        System.out.println(hmget);
        return "restful 【GET】method with none parameter" + test;
    }

    @ApiOperation(value = "Post请求")
    @PostMapping(value = "/post")
    public String post(@ApiParam(value = "查询条件", required = true) @RequestBody @Valid QueryQo queryQo) {
        String json = new Gson().toJson(queryQo);
        return "restful 【POST】 method with parameter :" + json;
    }
}
