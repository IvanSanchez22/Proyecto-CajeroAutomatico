/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urjcatm;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.CommunicationException;
import sienens.ATM;
import urjc.UrjcBankServer;

/**
 *
 * @author Iván
 */
public class Transfer extends AtmOperation {

    private boolean test = true;

    private ErrorExit error = new ErrorExit(atm, server, true);
    private IdiomSelection idioma = new IdiomSelection(atm, server);
    private InputText textoUsuario = new InputText(atm, false, 20);

    public Transfer(ATM atm, UrjcBankServer server, ErrorExit error, IdiomSelection idioma) {
        super(atm, server);
        this.error = error;
        this.idioma = idioma;
    }

    public boolean isTest() { //Devuelve el valor de test
        return test;
    }

    public void setTest(boolean test) { //Metodo para setear test cuando se recupera
        this.test = test;
    }

    @Override
    public boolean doOperation() {
        int cont = 0;
        char digito = ' ';
        char digitoCCC = ' ';
        int disponible = 0;

        String disponibleTransfer = "";
        String cantidad = "";

        int indicado = 0;
        boolean exito = true;

        try {
            disponible = server.balance(atm.getCardNumber()); //Consultamos la disponibilidad de la cuenta
        } catch (CommunicationException ex) {
            test = error.doOperation();
        }

        for (int i = 0; i < 6; i++) {
            atm.setOption(i, null);
        }

        if (test == true) { //Entramos si la comunicación con el servidor es correcta
            disponibleTransfer = String.valueOf(disponible);

            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SU SALDO ACTUAL ES DE"));
            atm.setInputAreaText(disponibleTransfer + "  centimos"); //Le mostramos la cantidad disponible para transferir en céntimos
            this.detenerPrograma();

            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SELECCIONE CUÁNTO QUIERE TRANSFERIR"));

            boolean textoRecogido = textoUsuario.waitText(10); //Leemos la cantidad que desea transferir
            indicado = textoUsuario.getInt(); //Almacenamos el entero en la variable indicado

            if (indicado > disponible && textoRecogido == true) { //Caso en el que se lee bien el texto pero no se dispone de la cantidad
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("CANTIDAD NO DISPONIBLE"));
                atm.setInputAreaText(" ");
                this.detenerPrograma();
                exito = false;
            }

            if (indicado <= disponible && textoRecogido == true) { //Caso en el que se lee bien el texto y se dispone de la cantidad
                boolean transfer = false;

                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("INTRODUZCA EL CCC DEL DESTINATARIO"));
                atm.setInputAreaText(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SON 20 DÍGITOS"));

                boolean CCCAdecuado = textoUsuario.waitTextTransfer(10);//Leemos el CCC del destinatario 

                if (textoUsuario.getText() == "N") { //Caso en el que el usuario cancela la operativa a la hora de introducir el CCC
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("OPERACIÓN CANCELADA"));
                    atm.setInputAreaText(" ");
                    this.detenerPrograma();
                    exito = false;
                }

                if (textoUsuario.getInt() == 0) { //Caso en el que el usuario no pulsa nada durante el límite de tiempo al introducir cada dígito
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIEMPO EXCEDIDO"));
                    atm.setInputAreaText(" ");
                    this.detenerPrograma();
                    exito = false;
                }

                if (CCCAdecuado == true) { //Si el número CCC es correcto el método waitTextTransfer nos devuelve true y entramos aquí

                    StringBuilder MyString = new StringBuilder(textoUsuario.getText()); //Eliminamos los espacios de la cadena con un StringBuilder del texto 
                    MyString = MyString.deleteCharAt(4); //Eliminamos espacio en la posición 4 de la cadena
                    MyString = MyString.deleteCharAt(8); //Eliminamos espacio en la posición 8 de la cadena
                    MyString = MyString.deleteCharAt(10); //Eliminamos espacio en la posición 10 de la cadena

                    System.out.println(MyString);  //Para comprobar en la consola que se eliminan las posiciones correctas de la cadena 

                    String CCC = MyString.toString(); //Pasamos el objeto StringBuilder a String sin los espacios para pasárle la cadena correctamente al método transfer

                    try {//Le pasamos al método transfer el número de la tarjeta, la cantidad a transferir y el CCC
                        transfer = server.transfer(atm.getCardNumber(), indicado, CCC);
                    } catch (CommunicationException ex) {
                        test = error.doOperation();
                        exito = false;
                    }
                    if (test == true && transfer == true) { //Caso en el que se realiza la transferencia de manera correcta y no se interrumpe la comunicación con el servidor
                        boolean resta = false;
                        try {//Restamos  dinero transferido
                            indicado = indicado / 1000;
                            resta = server.doOperation(atm.getCardNumber(), - indicado); //Restamos euros
                        } catch (CommunicationException ex) {
                            test = error.doOperation();
                            exito = false;
                        }
                        if (resta == true) { //Si se resta correctamente la cantidad de la cuenta
                            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("DINERO TRANSFERIDO"));
                            atm.setInputAreaText(" ");

                            this.detenerPrograma();
                            exito = true;
                        }
                    } else if (transfer == false || exito == false) { //caso en el que no se cumple alguna condición anterior
                        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("HA OCURRIDO ALGÚN PROBLEMA"));
                        atm.setInputAreaText(" ");
                        this.detenerPrograma();
                        exito = false;
                    }
                }

            }

            if (textoUsuario.getText() == "N" && textoUsuario.getInt() == 1) { //Entra cuando el usuario cancela la operación en el momento de indicar el dinero a transferir
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("OPERACIÓN CANCELADA"));
                atm.setInputAreaText(" ");
                exito = false;
                this.detenerPrograma();

            }

            if (textoUsuario.getInt() == 0 && textoRecogido == false) {//Entra cuando el usuario no pulsa nada en el momento de indicar el dinero a transferir
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIEMPO EXCEDIDO"));
                atm.setInputAreaText(" ");
                this.detenerPrograma();
                exito = false;
            }

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
