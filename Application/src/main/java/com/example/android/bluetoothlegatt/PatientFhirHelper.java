package com.example.android.bluetoothlegatt;


import android.os.StrictMode;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import java.util.List;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.okhttp.client.OkHttpRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.util.BundleUtil;

//Class for handling fhir server connection

public class PatientFhirHelper {

    private IGenericClient client;
    private FhirContext ctx;
    private String serverurl = "http://fhirtest.uhn.ca/baseDstu3";

    //Initialize server connection
    public PatientFhirHelper() {
        ctx = FhirContext.forDstu3();
        // Use OkHttp
        ctx.setRestfulClientFactory(new OkHttpRestfulClientFactory(ctx));
        client = ctx.newRestfulGenericClient(serverurl);
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    //Get a list of patients
    public List<Patient> getPatients() {
        // Invoke the client
        Bundle bundle = client.search().forResource(Patient.class)
                .where(new TokenClientParam("gender").exactly().code("unknown"))
                .prettyPrint()
                .returnBundle(Bundle.class)
                .execute();
        return BundleUtil.toListOfResourcesOfType(ctx, bundle, Patient.class);
    }

    public void setServer(String serverurl) {
        ctx = FhirContext.forDstu3();
        client = ctx.newRestfulGenericClient(serverurl);
    }

    public String getServer() {
        return this.serverurl;
    }

    public IGenericClient getClient() {
        return client;
    }

    //Method for uploading patient data (patientid and observation) to fhir server
    public void uploadPatientData (Patient patient, Observation observation, String server){

        //Reference data to patient
        observation.setSubject(new Reference(patient));
        String id = patient.getId();

        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);
        bundle.addEntry()
                .setFullUrl(id)
                .setResource(patient)
                .getRequest()
                    .setUrl("Patient")
                    .setIfNoneExist("identifier=http://acme.org/mrns|"+id)
                .setMethod(Bundle.HTTPVerb.POST);

        bundle.addEntry()
                .setResource(observation)
                .getRequest()
                    .setUrl("Observation")
                    .setMethod(Bundle.HTTPVerb.POST);

        Bundle resp = client.transaction().withBundle(bundle).execute();

    }

}
