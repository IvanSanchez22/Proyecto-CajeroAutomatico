/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urjcatm;

import java.util.Locale;
import sienens.ATM;
import urjc.UrjcBankServer;

/**
 *
 * @author Iván
 */
public class IdiomSelection extends AtmOperation {
    
    private Locale idioma;

    //Getters y setters de lan
    public Locale getIdioma() {
        return idioma;
    }

    public void setIdioma(Locale idioma) {
        this.idioma = idioma;
    }

    public IdiomSelection(ATM atm, UrjcBankServer server) {
        super(atm, server);
    }

    @Override
    public boolean doOperation() {

        

        
       
        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma).getString("SELECCIONE IDIOMA"));//Layout de la pantalla de idioma
        atm.setOption(0, "Español");
        atm.setOption(1, "English");
        atm.setOption(2, null);
        atm.setOption(3, "Euskera");
        atm.setOption(4, "Català ");
        atm.setOption(5, null);
        atm.setInputAreaText(null);
        
            return false;

        

    }
}
