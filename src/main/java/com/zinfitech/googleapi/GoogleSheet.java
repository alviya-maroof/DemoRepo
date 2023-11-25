package com.zinfitech.googleapi;

import static com.zinfitech.zinfiexception.ExceptionUtils.throwAsUncheckedException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.zinfitech.zinfiexception.ZinfiTechException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GoogleSheet {

  private static final String APPLICATION_NAME = "Product details";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  private static final List<String> SCOPES = Collections.singletonList(
      SheetsScopes.SPREADSHEETS_READONLY);
  private static final String CREDENTIALS_FILE_PATH = "credentials.json";
  private static GoogleSheet excelSheet;
  private Sheets sheets;

  private GoogleSheet() {
    try {
      sheets = getSpreadSheetService();
    } catch (IOException | GeneralSecurityException e) {
      throwAsUncheckedException(
          new ZinfiTechException("unable to connect google spreed drive\n", e));
    }
  }

  private Credential getCredentials(final NetHttpTransport httpTransport)
      throws IOException {
    // Load client secrets.
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  private Sheets getSpreadSheetService() throws IOException, GeneralSecurityException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    return new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  public static GoogleSheet getInstance() {
    if (Objects.isNull(excelSheet)) {
      excelSheet = new GoogleSheet();
    }
    return excelSheet;
  }

  public Sheets getGoogleSheetInstance() {
    return sheets;
  }
}
