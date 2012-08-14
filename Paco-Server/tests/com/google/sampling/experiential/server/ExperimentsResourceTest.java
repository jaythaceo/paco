// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.sampling.experiential.server;

import static org.junit.Assert.*;

import com.google.sampling.experiential.shared.Experiment;

import org.junit.Test;
import org.restlet.Response;
import org.restlet.Request;
import org.restlet.data.Status;

/**
 * @author corycornelius@google.com (Cory Cornelius)
 *
 */
public class ExperimentsResourceTest extends PacoResourceTest {
  /*
   * create tests
   */
  @Test
  public void testCreate() {
    Experiment experiment = PacoTestHelper.constructExperiment();

    Request request = PacoTestHelper.post("/experiments", DAOHelper.toJson(experiment));
    Response response = new PacoApplication().handle(request);

    assertEquals(Status.SUCCESS_CREATED, response.getStatus());
    assertEquals("/observer/experiments/1", response.getLocationRef().getPath());
  }

  @Test
  public void testCreateWhenExperimentNull() {
    Request request = PacoTestHelper.post("/experiments", "");
    Response response = new PacoApplication().handle(request);

    assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.getStatus());
  }

  /*
   * index tests
   */
  @Test
  public void testIndex() {
    Request request = PacoTestHelper.get("/experiments");
    Response response = new PacoApplication().handle(request);

    assertEquals(Status.SUCCESS_OK, response.getStatus());
    assertEquals("[]", response.getEntityAsText());
  }

  @Test
  public void testIndexAfterCreatePublishedPublic() {
    PacoTestHelper.createPublishedPublicExperiment();

    Request request = PacoTestHelper.get("/experiments");
    Response response = new PacoApplication().handle(request);

    Experiment experiment = PacoTestHelper.constructExperiment();
    experiment.setId(1l);
    experiment.setVersion(1);

    String json = DAOHelper.toJson(experiment, Experiment.Summary.class);

    assertEquals(Status.SUCCESS_OK, response.getStatus());
    assertEquals("[" + json + "]", response.getEntityAsText());
  }

  @Test
  public void testIndexAfterCreatePublishedPrivate() {
    PacoTestHelper.createPublishedPrivateExperiment();

    Request request = PacoTestHelper.get("/experiments");
    Response response = new PacoApplication().handle(request);

    Experiment experiment = PacoTestHelper.constructExperiment();
    experiment.setId(1l);
    experiment.setVersion(1);

    String json = DAOHelper.toJson(experiment, Experiment.Summary.class);

    assertEquals(Status.SUCCESS_OK, response.getStatus());
    assertEquals("[" + json + "]", response.getEntityAsText());
  }

  @Test
  public void testIndexAfterCreateUnpublished() {
    PacoTestHelper.createUnpublishedExperiment();

    Request request = PacoTestHelper.get("/experiments");
    Response response = new PacoApplication().handle(request);

    Experiment experiment = PacoTestHelper.constructExperiment();
    experiment.setId(1l);
    experiment.setVersion(1);

    assertEquals(Status.SUCCESS_OK, response.getStatus());
    assertEquals("[]", response.getEntityAsText());
  }

  @Test
  public void testIndexAsImposterAfterCreatePublishedPrivate() {
    PacoTestHelper.createPublishedPrivateExperiment();

    helper.setEnvEmail("imposter@google.com");

    Request request = PacoTestHelper.get("/experiments");
    Response response = new PacoApplication().handle(request);

    Experiment experiment = PacoTestHelper.constructExperiment();
    experiment.setId(1l);
    experiment.setVersion(1);

    assertEquals(Status.SUCCESS_OK, response.getStatus());
    assertEquals("[]", response.getEntityAsText());
  }

  @Test
  public void testIndexAfterCreateAndDestroy() {
    PacoTestHelper.createPublishedPublicExperiment();
    PacoTestHelper.destroyExperiment();

    Request request = PacoTestHelper.get("/experiments");
    Response response = new PacoApplication().handle(request);

    assertEquals(Status.SUCCESS_OK, response.getStatus());
    assertEquals("[]", response.getEntityAsText());
  }
}
