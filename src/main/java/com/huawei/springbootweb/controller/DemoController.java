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
import java.util.RandomAccess;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController("/demo")
@Api(description = "controller上的描述信息")
public class DemoController {
    private static final String ZSET_KEY = "zset";

    private ExecutorService executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());

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


    @ApiOperation(value = "zset zincrby请求")
    @GetMapping("/add/{count}")
    public String zsetTest(@PathVariable("count") int count) {
//        Jedis resource = jedisPool.getResource();
//        Double aDouble = resource.zincrby(ZSET_KEY, score, "ip" + score % 3);
//        resource.close();

        testConcurrent(count);
        return " result : ";
    }

    private void testConcurrent(int endExclusive) {
        //提交500个并发线程
        CompletableFuture[] futures = new CompletableFuture[endExclusive];
        IntStream.range(0, endExclusive)
                .forEach(i -> futures[i] = CompletableFuture.supplyAsync(this::supplyMethod, executorService));

        CompletableFuture.allOf(futures).join();
        System.out.println("testConcurrent end !");
    }

    class RedisTest implements Callable {
        @Override
        public Object call() throws Exception {
            return supplyMethod();
        }
    }

    private String supplyMethod() {
        IntStream.range(0, 1000).forEach(score -> {
            Jedis resource = jedisPool.getResource();
            resource.zincrby(ZSET_KEY, score, "ip" + score % 3);
            resource.close();
        });
        Jedis resource = jedisPool.getResource();
        Long zcard = resource.zcard(ZSET_KEY);
        resource.close();
        return " result : " + zcard;
    }

    @ApiOperation(value = "zrem请求")
    @GetMapping("/zrem/{score}")
    public String zremTest(@PathVariable("score") Long score) {
        Jedis resource = jedisPool.getResource();
        //返回值 1 删除成功 0 删除失败(不存在)
        Long zset = resource.zrem("zset", "ip" + score % 3);
        return " result : " + zset;
    }
}
