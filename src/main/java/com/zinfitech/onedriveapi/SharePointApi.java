package com.zinfitech.onedriveapi;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.google.gson.Gson;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.requests.DriveCollectionPage;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;
import com.qapitol.sauron.common.crypt.Algorithm;
import com.qapitol.sauron.common.crypt.CryptoTool;
import com.qapitol.sauron.common.utils.FileAssistant;
import com.qapitol.sauron.crypt.CryptoToolImpl;
import com.zinfitech.zinfiexception.ZinfiTechException;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.Objects;
import okhttp3.Request;
import org.apache.commons.io.IOUtils;

public class SharePointApi {

  private final SharePointCredential sharePointCredential;
  final List<String> scopes = List.of("https://graph.microsoft.com/.default");
  final GraphServiceClient<Request> graphClient;

  public SharePointApi() {
    sharePointCredential = getCredential();
    this.graphClient = getGraphClient();
    sharePointCredential.setDriveId(driveId(userId(sharePointCredential.getEmailId())));
  }

  private SharePointCredential getCredential() {
    String credential;
    try {
      InputStream inputStream = FileAssistant.loadFile("sharepointcredential.json");
      credential = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      inputStream.close();
    } catch (IOException e) {
      throw new ZinfiTechException(e);
    }
    SharePointCredential sharePointData = new Gson().fromJson(credential,
        SharePointCredential.class);
    if (sharePointData.isEncrypted()) {
      return getDecryptedData(sharePointData);
    }
    return sharePointData;
  }

  private SharePointCredential getDecryptedData(SharePointCredential credential) {
    CryptoTool cryptoTool = new CryptoToolImpl();
    cryptoTool.init(Algorithm.find(credential.getCrypt().getAlgorithm()),
        credential.getCrypt().getKey());
    String clientId = cryptoTool.decrypt(credential.getClientId());
    String clientSecreteId = cryptoTool.decrypt(credential.getClientSecret());
    String tenantId = cryptoTool.decrypt(credential.getTenantId());
    return SharePointCredential.builder().clientId(clientId)
        .clientSecret(clientSecreteId).tenantId(tenantId).emailId(credential.getEmailId()).build();
  }

  public String userId(String userEmail) {
    UserCollectionPage userCollectionPage = graphClient.users().buildRequest()
        .filter("mail eq '" + userEmail + "'")
        .get();
    if (Objects.isNull(userCollectionPage) || userCollectionPage.getCurrentPage().isEmpty()) {
      throw new ZinfiTechException(userEmail + " mail id not azure portal or invalid email id");
    }
    return userCollectionPage.getCurrentPage().get(0).id;
  }

  public String driveId(String userId) {
    DriveCollectionPage driveCollectionPage = graphClient.users(userId).drives().buildRequest()
        .get();
    if (Objects.isNull(driveCollectionPage) || driveCollectionPage.getCurrentPage().isEmpty()) {
      throw new ZinfiTechException(userId + " drive id not found, contact IT admin");
    }
    return driveCollectionPage.getCurrentPage().get(0).id;
  }

  private GraphServiceClient<Request> getGraphClient() {
    final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
        .clientId(sharePointCredential.getClientId()).tenantId(sharePointCredential.getTenantId())
        .clientSecret(sharePointCredential.getClientSecret()).build();
    if (null == credential) {
      throw new ZinfiTechException("Unexpected error");
    }
    final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
        scopes, credential);
    return GraphServiceClient.builder()
        .authenticationProvider(authProvider).buildClient();
  }

  public InputStream getDriveItem(String fileName,
      List<HeaderOption> options) {
    DriveItemCollectionPage driveItemCollectionPage = graphClient.drives(
            sharePointCredential.getDriveId()).root()
        .children().buildRequest().filter("name eq '" + fileName + ".xlsx" + "'").get();
    if (Objects.isNull(driveItemCollectionPage) || driveItemCollectionPage.getCurrentPage()
        .isEmpty()) {
      throw new ZinfiTechException(fileName + " file not found in drive");
    }
    return graphClient.drives(sharePointCredential.getDriveId())
        .items(Objects.requireNonNull(driveItemCollectionPage.getCurrentPage().get(0).id)).content()
        .buildRequest(options)
        .get();
  }
}
