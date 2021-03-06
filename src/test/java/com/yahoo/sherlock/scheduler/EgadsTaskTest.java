package com.yahoo.sherlock.scheduler;

import com.beust.jcommander.internal.Lists;
import com.yahoo.egads.data.TimeSeries;
import com.yahoo.sherlock.enums.Granularity;
import com.yahoo.sherlock.exception.SherlockException;
import com.yahoo.sherlock.model.AnomalyReport;
import com.yahoo.sherlock.model.JobMetadata;
import com.yahoo.sherlock.service.DetectorService;
import com.yahoo.sherlock.settings.Constants;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class EgadsTaskTest {

    @Test
    public void testEgadsTaskError() throws SherlockException {
        JobMetadata j = new JobMetadata();
        AnomalyReport ar = new AnomalyReport();
        ar.setStatus(Constants.ERROR);
        int runtime = 1234;
        List<TimeSeries> tslist = Collections.emptyList();
        DetectorService ds = mock(DetectorService.class);
        JobExecutionService jes = mock(JobExecutionService.class);
        EgadsTask et = new EgadsTask(j, runtime, tslist, ds, jes);
        when(ds.runDetection(any(), anyDouble(), any(), anyInt(), anyString())).thenThrow(new SherlockException());
        when(jes.getSingletonReport(any(), any())).thenReturn(ar);
        et.run();
        assertEquals(et.getReports().get(0).getStatus(), Constants.ERROR);
    }

    @Test
    public void testEgadsTaskRunToCompletion() throws SherlockException {
        JobMetadata j = new JobMetadata();
        j.setGranularity(Granularity.HOUR.toString());
        j.setEffectiveQueryTime(123456);
        int runtime = 1234;
        List<TimeSeries> tslist = Collections.emptyList();
        DetectorService ds = mock(DetectorService.class);
        JobExecutionService jes = mock(JobExecutionService.class);
        EgadsTask et = new EgadsTask(j, runtime, tslist, ds, jes);
        List<AnomalyReport> arlist = Lists.newArrayList(new AnomalyReport(), new AnomalyReport(), new AnomalyReport());
        when(ds.runDetection(any(), anyDouble(), any(), anyInt(), anyString())).thenReturn(new ArrayList<>());
        when(jes.getReports(any(), any())).thenReturn(arlist);
        when(jes.getSingletonReport(any(), any())).thenReturn(new AnomalyReport());
        et.run();
        assertEquals(et.getReports().size(), 3);
    }

}
