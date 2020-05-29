package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.client.RestClient;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.SonarProject;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSonar83ClientTest {
    @Mock
    private Supplier<RestOperations> restOperationsSupplier;
    @Mock
    private RestOperations rest;
    @Mock
    private SonarSettings settings;
    private DefaultSonar83Client defaultSonar83Client;


    private static final String SONAR_URL = "http://sonar.com";

    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new SonarSettings();
        defaultSonar83Client = new DefaultSonar83Client(new RestClient(restOperationsSupplier), settings);
    }

    @Test
    public void getChangeLog() throws Exception {
        String changeLogJson = getJson("sonar83changelog.json");
        String changelogUrl = String.format(SONAR_URL + DefaultSonar83Client.URL_QUALITY_PROFILE_CHANGES,"foo","java");
        doReturn(new ResponseEntity<>(changeLogJson, HttpStatus.OK)).when(rest).exchange(eq(changelogUrl), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        JSONArray events = defaultSonar83Client.getQualityProfileConfigurationChanges(SONAR_URL,"foo", "java");
        assertThat(events.size(), is(3));
    }

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultSonar83ClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}
