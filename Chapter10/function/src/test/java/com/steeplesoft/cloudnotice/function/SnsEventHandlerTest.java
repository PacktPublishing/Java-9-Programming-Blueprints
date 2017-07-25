package com.steeplesoft.cloudnotice.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class SnsEventHandlerTest {
    
    public SnsEventHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

//    @Test
    public void testHandleRequest() {
        System.out.println("handleRequest");
        
        SNS sns = new SNS();
        sns.setMessage("test message");

        SNSRecord record = new SNSRecord();
        record.setSns(sns);

        SNSEvent request = new SNSEvent();
        List<SNSRecord> records = new ArrayList<>();
        records.add(record);
        request.setRecords(records);

        Context context = new TestContext();
        SnsEventHandler instance = new SnsEventHandler();
        
        instance.handleRequest(request, context);
    }
    
}
