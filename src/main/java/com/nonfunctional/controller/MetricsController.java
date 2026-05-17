package com.nonfunctional.controller;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private static final long START_TIME = System.currentTimeMillis();

    @GetMapping
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        metrics.put("system", getSystemMetrics());
        metrics.put("memory", getMemoryMetrics());
        metrics.put("threads", getThreadMetrics());
        metrics.put("runtime", getRuntimeMetrics());
        metrics.put("environment", getEnvironmentMetrics());
        return metrics;
    }

    @GetMapping("/system")
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> system = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        system.put("availableProcessors", runtime.availableProcessors());
        system.put("totalMemory", formatBytes(runtime.totalMemory()));
        system.put("freeMemory", formatBytes(runtime.freeMemory()));
        system.put("maxMemory", formatBytes(runtime.maxMemory()));
        system.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        
        return system;
    }

    @GetMapping("/memory")
    public Map<String, Object> getMemoryMetrics() {
        Map<String, Object> memory = new HashMap<>();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        
        Map<String, Object> heap = new HashMap<>();
        heap.put("init", formatBytes(heapMemory.getInit()));
        heap.put("used", formatBytes(heapMemory.getUsed()));
        heap.put("committed", formatBytes(heapMemory.getCommitted()));
        heap.put("max", formatBytes(heapMemory.getMax()));
        
        Map<String, Object> nonHeap = new HashMap<>();
        nonHeap.put("init", formatBytes(nonHeapMemory.getInit()));
        nonHeap.put("used", formatBytes(nonHeapMemory.getUsed()));
        nonHeap.put("committed", formatBytes(nonHeapMemory.getCommitted()));
        nonHeap.put("max", formatBytes(nonHeapMemory.getMax()));
        
        memory.put("heap", heap);
        memory.put("nonHeap", nonHeap);
        
        return memory;
    }

    @GetMapping("/threads")
    public Map<String, Object> getThreadMetrics() {
        Map<String, Object> threads = new HashMap<>();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        threads.put("threadCount", threadBean.getThreadCount());
        threads.put("peakThreadCount", threadBean.getPeakThreadCount());
        threads.put("daemonThreadCount", threadBean.getDaemonThreadCount());
        threads.put("totalStartedThreads", threadBean.getTotalStartedThreadCount());
        
        return threads;
    }

    @GetMapping("/runtime")
    public Map<String, Object> getRuntimeMetrics() {
        Map<String, Object> runtime = new HashMap<>();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        runtime.put("uptime", getUptimeFormatted());
        runtime.put("startTime", runtimeBean.getStartTime());
        runtime.put("vmName", runtimeBean.getVmName());
        runtime.put("vmVendor", runtimeBean.getVmVendor());
        runtime.put("vmVersion", runtimeBean.getVmVersion());
        
        return runtime;
    }

    @GetMapping("/environment")
    public Map<String, Object> getEnvironmentMetrics() {
        Map<String, Object> env = new HashMap<>();
        
        env.put("javaVersion", System.getProperty("java.version"));
        env.put("javaHome", System.getProperty("java.home"));
        env.put("osName", System.getProperty("os.name"));
        env.put("osVersion", System.getProperty("os.version"));
        env.put("osArch", System.getProperty("os.arch"));
        env.put("userDir", System.getProperty("user.dir"));
        env.put("userTimezone", System.getProperty("user.timezone"));
        
        return env;
    }

    private String getUptimeFormatted() {
        long uptimeMillis = System.currentTimeMillis() - START_TIME;
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        return String.format("%dd %dh %dm %ds", days, hours % 24, minutes % 60, seconds % 60);
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}