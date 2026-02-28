package com.svrmslk.sendgridtest;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;

public class TestSendGridStandalone {

    public static void main(String[] args) throws IOException {
    
        String sendgridApiKey = "SG.BAlUk_66SghjjfghjjYOBV-DXh08Aig.MUeB9Ws6hRguZtQ-WlWVSaocvZhyY-Xsp_FmPF2HfE0";

   
        Email from = new Email("hfh@hotmail.com");
        Email to = new Email("hfhgmail.com");  
        String subject = "Test Email via SendGrid Java";
        Content content = new Content("text/html", "<p>Hello from SendGrid!</p>");

        Mail mail = new Mail(from, subject, to, content);

  
        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);

        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
        System.out.println("Headers: " + response.getHeaders());
    }
}
