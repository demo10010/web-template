package com.huawei.springbootweb.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*预热次数，间隔时间*/
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
/*实际测试次数，间隔1s测试一次*/
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
public class JMHSample_01_HelloWorld {
    static class Demo {
        int id;
        String name;

        public Demo(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static List<Demo> demoList;

    static {
        demoList = new ArrayList();
        for (int i = 0; i < 10000; i++) {
            demoList.add(new Demo(i, "test"));
        }
    }

    /**
     * 必须是public
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testHashMapWithoutSize() {
        Map map = new HashMap();
        for (Demo demo : demoList) {
            map.put(demo.id, demo.name);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testHashMap() {
        Map map = new HashMap((int) (demoList.size() / 0.75f) + 1);
        for (Demo demo : demoList) {
            map.put(demo.id, demo.name);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_01_HelloWorld.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
