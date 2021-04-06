/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package org.apache.logging.log4j.perf.jmh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;

/**
 * Benchmarks Log4j 2, Log4j 1, Logback and JUL using the DEBUG level which is enabled for this test. The configuration
 * for each uses a FileAppender
 */
// HOW TO RUN THIS TEST
// java -jar log4j-perf/target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 1 -wi 10 -i 20
//
// RUNNING THIS TEST WITH 4 THREADS:
// java -jar log4j-perf/target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 1 -wi 10 -i 20 -t 4
@State(Scope.Thread)
public class FileAppender2ThrowableBenchmark {
    public static final String MESSAGE = "This is a debug message";
    private FileHandler julFileHandler;

    private static final Throwable THROWABLE = getSimpleThrowable();
    private static final Throwable COMPLEX_THROWABLE = getComplexThrowable();

    private static Throwable getSimpleThrowable() {
        return new IllegalStateException("Test Throwable");
    }

    interface ThrowableHelper {
        void action();
    }

    // Used to create a deeper stack with many different classes
    // This makes the ThrowableProxy Map<String, CacheEntry> cache
    // perform more closely to real applications.
    interface TestIface0 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface1 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface2 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface3 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface4 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface5 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface6 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface7 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface8 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface9 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface10 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface11 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface12 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface13 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface14 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface15 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface16 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface17 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface18 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface19 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface20 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface21 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface22 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface23 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface24 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface25 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface26 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface27 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface28 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface29 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}
    interface TestIface30 extends FileAppender2ThrowableBenchmark.ThrowableHelper {}

    private static Throwable getComplexThrowable() {
        FileAppender2ThrowableBenchmark.ThrowableHelper helper = () -> {
            throw new IllegalStateException("Test Throwable");
        };
        try {
            for (int i = 0; i < 31; i++) {
                final FileAppender2ThrowableBenchmark.ThrowableHelper delegate = helper;
                helper = (FileAppender2ThrowableBenchmark.ThrowableHelper) Proxy.newProxyInstance(
                        FileAppender2ThrowableBenchmark.class.getClassLoader(),
                        new Class<?>[]{Class.forName(FileAppender2ThrowableBenchmark.class.getName() + "$TestIface" + (i % 31))},
                        (InvocationHandler) (proxy, method, args) -> {
                            try {
                                return method.invoke(delegate, args);
                            } catch (final InvocationTargetException e) {
                                throw e.getCause();
                            }
                        });
            }
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Failed to create stack", e);
        }
        try {
            helper.action();
        } catch (final IllegalStateException e) {
            return e;
        }
        throw new IllegalStateException("Failed to create throwable");
    }


    Logger log4j2Logger;
    Logger fileAppender;
    Logger rollingFileAppender;

    @Setup
    public void setUp() throws Exception {
        System.setProperty("log4j.configurationFile", "log4j2-file.xml");

        deleteLogFiles();

        log4j2Logger = LogManager.getLogger(FileAppender2ThrowableBenchmark.class);
        fileAppender = LogManager.getLogger("FileAppender");
        rollingFileAppender = LogManager.getLogger("RollingFileAppender");
    }

    @TearDown
    public void tearDown() {
        System.clearProperty("log4j.configurationFile");
        System.clearProperty("log4j.configuration");
        System.clearProperty("logback.configurationFile");

        deleteLogFiles();
    }

    private void deleteLogFiles() {
        final File logbackFile = new File("target/testlogback.log");
        logbackFile.delete();
        final File log4jFile = new File ("target/testlog4j.log");
        log4jFile.delete();
        final File log4jRandomFile = new File ("target/testRandomlog4j2.log");
        log4jRandomFile.delete();
        final File log4jMemoryFile = new File ("target/testMappedlog4j2.log");
        log4jMemoryFile.delete();
        final File log4j2File = new File ("target/testlog4j2.log");
        log4j2File.delete();
        final File julFile = new File("target/testJulLog.log");
        julFile.delete();
    }

    /*@BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void log4j2MMF() {
        log4j2MemoryLogger.debug(MESSAGE);
    }*/

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void log4j2FileAppender() {
        fileAppender.debug(MESSAGE, COMPLEX_THROWABLE);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void log4j2RollingFileAppender() {
        rollingFileAppender.debug(MESSAGE, COMPLEX_THROWABLE);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void log4j2File() {
        log4j2Logger.debug(MESSAGE, COMPLEX_THROWABLE);
    }

//    @BenchmarkMode(Mode.Throughput)
//    @OutputTimeUnit(TimeUnit.SECONDS)
//    @Benchmark
//    public void log4j2Builder() {
//        log4j2Logger.atDebug().withLocation().log(MESSAGE);
//    }

//    @BenchmarkMode(Mode.Throughput)
//    @OutputTimeUnit(TimeUnit.SECONDS)
//    @Benchmark
//    public void logbackFile() {
//        slf4jLogger.debug(MESSAGE);
//    }
//
//    @BenchmarkMode(Mode.Throughput)
//    @OutputTimeUnit(TimeUnit.SECONDS)
//    @Benchmark
//    public void logbackAsyncFile() {
//        slf4jAsyncLogger.debug(MESSAGE);
//    }
//
//    @BenchmarkMode(Mode.Throughput)
//    @OutputTimeUnit(TimeUnit.SECONDS)
//    @Benchmark
//    public void log4j1File() {
//        log4j1Logger.debug(MESSAGE);
//    }
//
//    @BenchmarkMode(Mode.Throughput)
//    @OutputTimeUnit(TimeUnit.SECONDS)
//    @Benchmark
//    public void julFile() {
//        // must specify sourceClass or JUL will look it up by walking the stack trace!
//        julLogger.logp(Level.INFO, getClass().getName(), "julFile", MESSAGE);
//    }
}
