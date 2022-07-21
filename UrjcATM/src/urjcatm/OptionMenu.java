/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urjcatm;

import java.util.ArrayList;
import sienens.ATM;
import urjc.UrjcBankServer;


/**
 *
 * @author Iván
 */
public class OptionMenu extends AtmOperation {

    private IdiomSelection idioma = new IdiomSelection(atm, server);
    private ErrorExit error = new ErrorExit(atm, server, true);

    private ChangePassword cambioContraseña = new ChangePassword(atm, server, idioma, error);
    private ArrayList<AtmOperation> propertiesList = new ArrayList<>();
    private WithdrawCash sacarDinero = new WithdrawCash(atm, server, error, idioma);
    private AccountBalance obtenerSaldo = new AccountBalance(server, atm, error, idioma);
    private LastOperations ultimasOperaciones = new LastOperations(atm, server, error, idioma);
    private Transfer transferencia = new Transfer(atm, server, error, idioma);
    private char operacion;

    public char getOperacion() {
        return operacion;
    }
    
    public OptionMenu(ATM atm, UrjcBankServer server, ErrorExit error, IdiomSelection idioma) {
        super(atm, server);
        
        this.error = error;
        this.idioma = idioma;
    }

    @Override
    public boolean doOperation() { //Menu que se le muestra al usuario para que decida que operación realizar

        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("ELIJA OPERACIÓN"));
        atm.setOption(0, java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SACAR DINERO"));
        atm.setOption(1, java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("OBTENER SALDO"));
        atm.setOption(2, java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TRANSFERENCIA"));
        atm.setOption(3, java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("ÚLTIMAS OPERACIONES"));
        atm.setOption(4, java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("CAMBIAR CONTRASEÑA"));
        atm.setOption(5, null);
        atm.setInputAreaText(null);
        operacion = atm.waitEvent(10);
        return false;
    }

    public boolean seguirOperando() { //Método para preguntar al usuario si quiere seguir realizando operaciones o por el contrario salir devolviendo false en caso de que no y true en caso de que sí
        char operar;
        boolean decision = false;

        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("¿DESEA SEGUIR OPERANDO?"));
        atm.setInputAreaText(" ");
        operar = atm.waitEvent(20);

        if (operar == 'Y') {
            decision = true;
        }

        if (operar == 'N') {
            decision = false;
        }

        if (operar == '0') {
            decision = false;
        }

        return decision;
    }

}
