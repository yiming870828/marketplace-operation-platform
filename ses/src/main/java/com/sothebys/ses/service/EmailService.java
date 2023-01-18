package com.sothebys.ses.service;

import com.alibaba.fastjson.JSONObject;
import com.sothebys.ses.entity.EmailModule;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.*;

public class EmailService {

    public static void main(String[] args) {
        try {

            Credential cred = new Credential("AKID3zhz3tUr0we8Or7CoBKF3e3EEB7qOjsn", "ktuiIdmkV1NhqlSxIT8cv3zgCf4ISIyS");

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ses.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            SesClient client = new SesClient(cred, "ap-hongkong", clientProfile);
            SendEmailRequest req = new SendEmailRequest();
            req.setFromEmailAddress("owen_xym@hotmail.com");
            String[] destination1 = {"test_yiming123@163.com", "yang.yang@sothebys.com", "yiming.xie@sothebys.com"};
            req.setDestination(destination1);
            req.setSubject("test");

            Template template = new Template();
            template.setTemplateData("");

            String code = "{\"code\":\"MB22222\"}";

            template.setTemplateID(64370l);
            template.setTemplateData(code);

            req.setTemplate(template);

            SendEmailResponse resp = client.SendEmail(req);

            System.out.println(SendEmailResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }

    }

    public String sendEmail(String id) {
        try {
            Credential cred = new Credential("AKID3zhz3tUr0we8Or7CoBKF3e3EEB7qOjsn", "ktuiIdmkV1NhqlSxIT8cv3zgCf4ISIyS");

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ses.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            SesClient client = new SesClient(cred, "ap-hongkong", clientProfile);
            SendEmailRequest req = new SendEmailRequest();
            req.setFromEmailAddress("owen_xym@hotmail.com");
            String[] destination1 = {"test_yiming123@163.com", "yang.yang@sothebys.com"};
            req.setDestination(destination1);
            req.setSubject("test");

            EmailModule emailModule = EmailModule.builder().code(id).build();
            String code = JSONObject.toJSONString(emailModule);

            Template template = new Template();
            template.setTemplateID(64370l);
            template.setTemplateData(code);
            req.setTemplate(template);

            SendEmailResponse resp = client.SendEmail(req);

            System.out.println(SendEmailResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public static String createTemplate() throws TencentCloudSDKException {

        Credential cred = new Credential("AKID3zhz3tUr0we8Or7CoBKF3e3EEB7qOjsn", "ktuiIdmkV1NhqlSxIT8cv3zgCf4ISIyS");

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("ses.tencentcloudapi.com");

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        SesClient client = new SesClient(cred, "ap-hongkong", clientProfile);

        CreateEmailTemplateRequest req = new CreateEmailTemplateRequest();

        TemplateContent templateContent = new TemplateContent();
        templateContent.setText("dGhpcyBpcyBhIGV4YW1wbGUge3tjb2RlfX0=");

        req.setTemplateContent(templateContent);
        req.setTemplateName("xym-test");


        CreateEmailTemplateResponse resp = client.CreateEmailTemplate(req);

        System.out.println(CreateEmailTemplateResponse.toJsonString(resp));

        return null;

    }


}
