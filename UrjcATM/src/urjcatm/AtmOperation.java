/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urjcatm;

import sienens.ATM;
import urjc.UrjcBankServer;

/**
 *
 * @author Iván
 */
public abstract class AtmOperation {

    protected UrjcBankServer server;
    protected ATM atm;

    public AtmOperation(ATM atm, UrjcBankServer server) {
        this.atm = atm;
        this.server = server;
    }

    public UrjcBankServer getServer() {
        return server;
    }

    public ATM getAtm() {
        return atm;
    }

    public abstract boolean doOperation();
}
