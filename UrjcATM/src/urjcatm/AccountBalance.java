package urjcatm;

import java.util.ArrayList;
import javax.naming.CommunicationException;
import sienens.ATM;
import urjc.UrjcBankServer;

/**
 *
 * @author Iván
 */
public class AccountBalance extends AtmOperation {

    private ErrorExit error = new ErrorExit(atm, server, true);
    private ArrayList<String> lista = new ArrayList();
    private IdiomSelection idioma = new IdiomSelection(atm, server);
    private boolean test = true;

    public AccountBalance(UrjcBankServer server, ATM atm, ErrorExit error, IdiomSelection idioma) {
        super(atm, server);
        this.error = error;
        this.idioma = idioma;
    }

    public boolean isTest() { //Para devolver si la comunicación con el servidor ha sido exitosa o no
        return test;
    }

    public void setTest(boolean test) { //Metodo para setear test cuando se recupera
        this.test = test;
    }
    

    @Override
    public boolean doOperation() {
        char ticket = ' ';
        int balance = 0;
        String consulta;
        boolean exito = false;
        test = true;

        atm.setInputAreaText(" ");
        for (int i = 0; i < 6; i++) {
            atm.setOption(i, null);
        }
        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SU SALDO ACTUAL ES DE"));

        try {
            balance = server.balance(atm.getCardNumber()); //Consultamos el balance de la cuenta del cliente para indicarselo posteriormente
        } catch (CommunicationException ex) {
            test = error.doOperation();
            exito = false;
        }

        if (test == true) {
            balance = balance / 100; //Pasamos el balance a euros
            consulta = String.valueOf(balance); //Convertimos balance a String para mostrárlo por pantalla
            atm.setInputAreaText(consulta + " euros");
            exito = true;

            this.detenerPrograma();
            do {//Le preguntamos si quiere un ticket hasta que pulse si, no o nada
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("¿DESEA UN TICKET?"));
                atm.setInputAreaText(" ");
                ticket = atm.waitEvent(15);

            } while (ticket != 0 && ticket != 'N' && ticket != 'Y');

            if (ticket == 0) { //Mensaje que se le muestra si no pulsa nada
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIEMPO EXCEDIDO"));
                atm.setInputAreaText(" ");
                this.detenerPrograma();

            }

            if (ticket == 'N') { //Mensaje que se le muestra si pulsa N
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TICKET DENEGADO"));
                atm.setInputAreaText(" ");
                this.detenerPrograma();

            }

            if (ticket == 'Y') { //Mensaje que se le muestra si pulsa Y y devolución del ticket
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TICKET IMPRESO"));
                lista.add(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIENE") + " " + consulta + " " + java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("EUROS"));
                atm.print(lista);
                lista.remove(0);

            }

        }
        return exito; //Aquí se da por hecho que la operación ha sido exitosa si se le muestra el balance independientemente de si desea ticket o no
    }

    private void detenerPrograma() {   //Ponemos a "Dormir" el programa durante los ms que queremos
        try {
            Thread.sleep(3 * 1000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
