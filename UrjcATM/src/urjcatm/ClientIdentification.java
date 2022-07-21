/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urjcatm;



import javax.naming.CommunicationException;
import sienens.ATM;
import urjc.UrjcBankServer;


/**
 *
 * @author Iván
 */
public class ClientIdentification extends AtmOperation {
    
    private char control0= ' ';
    private char controlN = ' ';
    private ErrorExit error = new ErrorExit(atm, server, true);
    private IdiomSelection idioma = new IdiomSelection(atm, server);
    private InputText textoUsuario = new InputText(atm, true, 5); // como no se si la contraseña puede ser de 2, 3 o 4 dígitos le paso 5 


    public ClientIdentification(ATM atm, UrjcBankServer server, ErrorExit error, IdiomSelection idioma) {
        super(atm, server);
        this.error = error;
        this.idioma = idioma;

    }

    public char getControl0() { //Metodo para consultar la variable de control 0 sin romper la encapsulación
        return control0;
    }
    
    

    public char getControlN() { //Metodo para consultar la variable de control N sin romper la encapsulación
        return controlN;
    }

    public void setControlN(char controlN) {
        this.controlN = controlN;
    }
    
    

    @Override
    public boolean doOperation() {
        boolean test = true;
        boolean identificacion = false;// true si se verifica o false si no

        identificacion = textoUsuario.waitText(10); //Llamada a InputText para leer el texto

        if (textoUsuario.getInt() == 0) {//Caso en el que el usuario no pulsa nada
            atm.setInputAreaText(" ");
            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("RECOJA SU TARJETA"));
            boolean recogida = atm.expelCreditCard(10);
            if (recogida == false) {//Si el usuario no recoge su tarjeta en 10 segundos retenemos la tarjeta permanentemente
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIEMPO EXCEDIDO"));
                atm.retainCreditCard(true);
                identificacion = false;
            }
            control0 = '0';
        }

        if (textoUsuario.getText() == "N") { //Caso en el que el usuario pulsa N
            atm.setInputAreaText(" ");
            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TENGA UN BUEN DÍA"));
            boolean recogida = atm.expelCreditCard(10);
            if (recogida == false) { //Si el usuario no recoge su tarjeta en 10 segundos retenemos la tarjeta permanentemente
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIEMPO EXCEDIDO"));
                atm.retainCreditCard(true);

            }
            identificacion = false;
            controlN = 'N'; //Ponemos esta variable a N para salir del bucle en clientManagement
        }

        if (identificacion == true) {//Caso en el que el texto ha sido recogido de manera correcta
            
            atm.setInputAreaText(textoUsuario.getText()); //Le mostramos los asteriscos ya que le hemos pasado hidden como true a InputText

            try { //Comprobamos si la contraseña es correcta
                identificacion = server.testPassword(textoUsuario.getInt(), atm.getCardNumber()); 

            } catch (CommunicationException ex) {
                test = error.doOperation();
            }

            if (identificacion == true && test == true) { //Caso en el que el servidor verifica la contraseña y no se produce error de comunicación con el servidor
                atm.setInputAreaText(" ");
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("ELIJA OPERACIÓN"));

            }
        }
        return identificacion;
    }
    
    
   
    
    

    private void detenerPrograma() {   //Ponemos a "Dormir" el programa durante los ms que queremos
        try {
            Thread.sleep(2 * 1000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
