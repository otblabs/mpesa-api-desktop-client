package com.otblabs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class Main extends JFrame implements ActionListener {
    static String mpesa_api_base_url = "https://mpesa-api-implementation.herokuapp.com";
    static  String checkoutRequestId;

    static JTextField phoneInput;
    static JFrame frame;
    static JButton initMpesa, transactions;
    static JLabel label;
    static String message;
    public static void main(String[] args)
    {

        frame = new JFrame("Mpesa");
        initMpesa = new JButton("Initiate Mpesa");
        transactions = new JButton("Confirm");
        label = new JLabel("");
        Main main = new Main();
        initMpesa.addActionListener(main);
        transactions.addActionListener(main);

        phoneInput = new JTextField(16);
        JPanel p = new JPanel();
        p.add(phoneInput);
        p.add(initMpesa);
        p.add(transactions);
        p.add(label);

        frame.add(p);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Initiate Mpesa")) {

            String phoneNumber = phoneInput.getText().trim();
            LNMResult lnmResult  = performStkPush(phoneNumber);
                if(lnmResult == null){
                    message = "failed to initialize mpesa";
                }else{

                    checkoutRequestId = lnmResult.getCheckoutRequestID();
                    try {
                        Thread.sleep(2000);

                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    message = lnmResult.getCustomerMessage();
                }
                JOptionPane.showMessageDialog(frame,message);


            phoneInput.setText("");
        }else if(s.equals("Confirm")){
            StkResponseData responseData = confirmTransaction(checkoutRequestId);
            System.out.println(responseData.toString());

            String data = "<html> <body>   Message "+responseData.getResultDescription() + " <br>"+
                    "  Reciept Number "+responseData.getMpesaReceiptNumber() +"<br>"+
                    "  Amount "+responseData.getAmount() + "<br>"+
                    "  Phone Number "+responseData.getPhoneNumber() + "<br>"+
                    "</body></html>";


            label.setText(data);

            //System.out.println("confirmed");
        }
    }

    static LNMResult performStkPush(String phone){
        //call our server that will then call safaricom Daraja APIs
          String stkpush_url = mpesa_api_base_url+ "/mpesastk/";

        try {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString("hello"))
                    .uri(URI.create(stkpush_url+phone+"/2"))
                    .setHeader("content-type", "application/json")
                    .build();
            return   HttpService.sendSingleResponseRequest(LNMResult.class,httpClient, request);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static StkResponseData confirmTransaction(String checkoutRequestId){
        try {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mpesa_api_base_url + "/mpesastk/transactions/"+checkoutRequestId))
                    .setHeader("content-type", "application/json")
                    .build();
            return   HttpService.sendSingleResponseRequest(StkResponseData.class,httpClient, request);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }


}
