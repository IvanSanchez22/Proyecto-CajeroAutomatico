/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urjcatm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import javax.naming.CommunicationException;
import sienens.ATM;
import urjc.UrjcBankServer;

/**
 *
 * @author Iván
 */
public class UrjcATM {

    public static void main(String[] args) throws CommunicationException {

        String anuncios[] = new String[4];
        UrjcBankServer server = new UrjcBankServer();

        ATM atm = new ATM();

        ClientManagement client = new ClientManagement(atm, server);
        

        client.doOperation();
        
    }

}
