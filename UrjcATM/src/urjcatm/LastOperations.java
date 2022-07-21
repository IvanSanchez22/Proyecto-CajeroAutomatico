package urjcatm;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.CommunicationException;
import sienens.ATM;
import urjc.UrjcBankServer;
import java.util.Locale;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Iván
 */
public class LastOperations extends AtmOperation {

    private ArrayList listaString = new ArrayList();
    private ErrorExit error = new ErrorExit(atm, server, true);
    private IdiomSelection idioma = new IdiomSelection(atm, server);
    private boolean test = true;
    public LastOperations(ATM atm, UrjcBankServer server, ErrorExit error, IdiomSelection idioma) {
        super(atm, server);
        this.error = error;
        this.idioma = idioma;
    }

    public boolean isTest() { //método para consultar test
        return test;
    }

    public void setTest(boolean test) { //Metodo para setear test cuando se recupera
        this.test = test;
    }
    

    
    @Override
    public boolean doOperation() {
        boolean exito = true;
        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("GENERANDO TICKET"));
        atm.setInputAreaText(" ");
        for (int i = 0; i < 6; i++) {
            atm.setOption(i, null);
        }
        this.detenerPrograma();

        try {
            List<urjc.Operation> lista1 = server.getLastOperations(atm.getCardNumber()); //Guardamos las ultimas operaciones del cliente

            for (int i = 0; i < lista1.size(); i++) { //Recorremos la lista

                urjc.Operation op = lista1.get(i); //Guardamos la operación de la posición
                String fecha = op.getDate(); //Guardamos la fecha
                String detail = op.getDetail(); //Guardamos los detalles
                int cantidad = op.getAmount(); //Guardamos la cantidad
                
                //Lo añadimos todo a una lista
                listaString.add(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("FECHA") + " " + fecha + "\n" + java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("INFO") + " " + detail + "\n" + java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("CANTIDAD") + " " + cantidad + "\n");
                
            }
            //imprimimos la lista
            atm.print(listaString);
           
        } catch (CommunicationException ex) {
            test = error.doOperation();
            exito = false;
        }

        return exito;

    }
    
    

    private void detenerPrograma() {   //Ponemos a "Dormir" el programa durante los ms que queremos
        try {
            Thread.sleep(3 * 1000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
