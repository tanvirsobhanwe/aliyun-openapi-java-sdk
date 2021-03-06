/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyuncs.batchcompute.modelunittest;


import com.aliyuncs.batchcompute.model.v20150630.CreateJobRequest;
import com.aliyuncs.batchcompute.pojo.v20150630.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import junit.framework.TestCase;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guangchun.luo on 15/4/14.
 */
public class CreateJobRequestTest extends TestCase {

    @Test
    public void testDefaultConstructor() {
        CreateJobRequest req = new CreateJobRequest();
        assertEquals(req.getMethod(), MethodType.POST);
        assertEquals(req.getUriPattern(), "/jobs");
        assertEquals(req.getProduct(), "BatchCompute");
    }

    @Test
    public void testConstructor() throws ClientException, IOException {

        JobDescription job = createJob();

        CreateJobRequest req = new CreateJobRequest(job);

        assertEquals(req.getMethod(), MethodType.POST);
        assertEquals(req.getUriPattern(), "/jobs");
        assertEquals(req.getProduct(), "BatchCompute");


        String contentString = null;
        try {
            contentString = new String(req.getContent(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        ObjectMapper objectMapper = new ObjectMapper();
        JobDescription job2 = objectMapper.readValue(contentString, JobDescription.class);


        assertEquals("jobName1",job2.getJobName());
        assertEquals("xx",job2.getJobTag());
        assertTrue(job2.getTaskDag().getDependencies().containsKey("task_1"));
        assertTrue(1==job2.getPriority());

        Map<String, TaskDescription> taskDescMap2 = job2.getTaskDag().getTaskDescMap();

        assertTrue(taskDescMap2.containsKey("task_1"));


        TaskDescription a = taskDescMap2.get("task_1");
        assertEquals("img_id",a.getImageId());
        assertEquals("b", a.getEnvironmentVariables().get("a"));
        //assertEquals("snapshot_id",a.getBlockDeviceMapping().get("/dev/xxx").getSnapshotId());

        assertTrue(2 == a.getInstanceCount());
        assertEquals("oss://test", a.getPackageUri());
        //assertEquals("test", a.getLoadImage());
        assertEquals("xxx", a.getProgramName());
        assertEquals("a", a.getProgramArguments());

        assertEquals("GBK", a.getOssMappingLocale());
        assertTrue(false==a.isOssMappingLock());

        assertTrue(5 == a.getResourceDescription().getCpu());
        assertTrue(100000 == a.getResourceDescription().getMemory());

    }


    @Test
    public void testSetContent() throws ClientException, IOException {

        JobDescription job = createJob();

        CreateJobRequest req = new CreateJobRequest();

        req.setJobDescription(job);


        String contentString = null;
        try {
            contentString = new String( req.getContent(), "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        ObjectMapper objectMapper = new ObjectMapper();
        JobDescription job2 = objectMapper.readValue(contentString, JobDescription.class);


        assertEquals("jobName1",job2.getJobName());
        assertEquals("xx",job2.getJobTag());
        assertTrue(job2.getTaskDag().getDependencies().containsKey("task_1"));
        assertTrue(1==job2.getPriority());

        Map<String, TaskDescription> taskDescMap2 = job2.getTaskDag().getTaskDescMap();

        assertTrue(taskDescMap2.containsKey("task_1"));


        TaskDescription a = taskDescMap2.get("task_1");
        assertEquals("img_id",a.getImageId());
        assertEquals("b", a.getEnvironmentVariables().get("a"));
        //assertEquals("snapshot_id",a.getBlockDeviceMapping().get("/dev/xxx").getSnapshotId());

        assertTrue(2 == a.getInstanceCount());
        assertEquals("oss://test", a.getPackageUri());
        //assertEquals("test", a.getLoadImage());
        assertEquals("xxx", a.getProgramName());
        assertEquals("a", a.getProgramArguments());

        assertTrue(5 == a.getResourceDescription().getCpu());
        assertTrue(100000 == a.getResourceDescription().getMemory());


    }


    private JobDescription createJob() {

        TaskDag taskDag = new TaskDag();

        ArrayList<String> taskTargets = new ArrayList();
        taskTargets.add("task_2");

        taskDag.addDependencies("task_1", taskTargets);


        Map<String, TaskDescription> taskDescMap = new HashMap();

        TaskDescription task = new TaskDescription();


        BlockDevice blockDevice = new BlockDevice("snapshot_id");
        //task.addBlockDeviceMapping("/dev/xxx", blockDevice);
        task.addEnvironmentVariables("a", "b");

        task.setImageId("img_id");
        task.setInstanceCount(2);
        task.setPackageUri("oss://test");
        //task.setLoadImage("test");
        task.setProgramName("xxx");

        task.addOssMapping("oss_key", "value");
        task.setOssMappingLocale("GBK");
        task.setOssMappingLock(false);

        ResourceDescription resourceDesc = new ResourceDescription();
        resourceDesc.setCpu(5);
        resourceDesc.setMemory(100000);
        task.setResourceDescription(resourceDesc);

        task.setProgramArguments("a");

        taskDescMap.put("task_1", task);
        taskDag.setTaskDescMap(taskDescMap);


        JobDescription job = new JobDescription();
        job.setJobName("jobName1");
        job.setJobTag("xx");
        job.setTaskDag(taskDag);
        job.setPriority(1);
        return job;
    }


    @Test
    public void testJob2() {


        String IMAGE_ID = "img-linux-1429670072";
        String PROGRAM_NAME = "count_all_num.py";

        String OSS_BUCKET = "loak";
        String OSS_PROGRAM_PACKAGE_PATH = "diku_simple_test/program_package.tar.gz";


        String OSS_LOG_PATH = "diku_simple_test/log";
        String OSS_DATA_PACKAGE = "diku_simple_test/diku_test_file.tar.gz";


        TaskDag taskDag = new TaskDag();

        TaskDescription task = new TaskDescription();

        ResourceDescription resourceDesc = new ResourceDescription();
        resourceDesc.setCpu(1200);      //12 threads
        resourceDesc.setMemory(16000);  //16 G

        task.setResourceDescription(resourceDesc);
        task.setInstanceCount(1);
        task.setImageId(IMAGE_ID);
        task.setProgramName(PROGRAM_NAME);
        task.setProgramType("python");
        task.setTimeout(21600); //seconds
        task.setPackageUri("oss://" + OSS_BUCKET + "/" + OSS_PROGRAM_PACKAGE_PATH);

        task.setStderrRedirectPath("oss://" + OSS_BUCKET + "/" + OSS_LOG_PATH);
        task.setStdoutRedirectPath("oss://" + OSS_BUCKET + "/" + OSS_LOG_PATH);

        Map<String, TaskDescription> taskDescMap = new HashMap();

        taskDescMap.put("CountTask", task);

        taskDag.setTaskDescMap(taskDescMap);

        TaskDescription task2 = new TaskDescription();
        task2.setTaskName("CountTask2");
        taskDag.addTask("CountTask2", task2);

        assertEquals("CountTask2", taskDag.getTaskDescMap().get("CountTask2").getTaskName());


        JobDescription job = new JobDescription();
        job.setJobName("jobName1");
        job.setJobTag("JobTag");
        job.setTaskDag(taskDag);
        job.setPriority(0);

        CreateJobRequest req = null;

        try {
            req = new CreateJobRequest(job);
        } catch (ClientException e) {
            e.printStackTrace();
        }

        String contentStr = null;
        try {
            contentStr = new String( req.getContent(), "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        assertTrue(contentStr.indexOf("\"CountTask2\"")!=-1);
        assertTrue(contentStr.indexOf("\"CountTask\"")!=-1);
        assertTrue(contentStr.indexOf("\"python\"")!=-1);
        assertTrue(contentStr.indexOf(OSS_BUCKET)!=-1);
        assertTrue(contentStr.indexOf(PROGRAM_NAME)!=-1);
        assertTrue(contentStr.indexOf(IMAGE_ID)!=-1);

    }

}
