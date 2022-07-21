package urjcatm;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.CommunicationException;
import sienens.ATM;
import urjc.UrjcBankServer;

/**
 *
 * @author Iván
 */
public class WithdrawCash extends AtmOperation {

    private ErrorExit error = new ErrorExit(atm, server, true);
    private ArrayList<String> lista = new ArrayList();
    private IdiomSelection idioma = new IdiomSelection(atm, server);
    private InputText textoUsuario = new InputText(atm, false, 100);
    private boolean test = true;

    public WithdrawCash(ATM atm, UrjcBankServer server, ErrorExit error, IdiomSelection idioma) {
        super(atm, server);
        this.error = error;
        this.idioma = idioma;

    }

    public boolean isTest() { //Método para consultar modificación
        return test;
    }

    public void setTest(boolean test) { //Metodo para setear test cuando se recupera
        this.test = test;
    }
    

    @Override
    public boolean doOperation() {
        int disponible = 0;
        String cantidad;
        int indicado;
        boolean exito = false;
        boolean textoRecogido = false;
        int dineroEnCuenta = 0;
        for (int i = 0; i < 6; i++) {
            atm.setOption(i, null);
        }
        atm.setTitle(null);

        try { //Consultamos la cantidad disponible en el cajero
            disponible = server.avaiable(atm.getCardNumber());
        } catch (CommunicationException ex) {
            test = error.doOperation();
            exito = false;

        }

        try {//Consulta la cantidad disponible en la cuenta
            dineroEnCuenta = server.balance(atm.getCardNumber());
        } catch (CommunicationException ex) {
            test = error.doOperation();
            exito = false;
        }

        if (test == true) { //Comprobamos que la comunicación con el servidor es correcta
            dineroEnCuenta = dineroEnCuenta / 100; //Pasamos el dinero a euros
            cantidad = String.valueOf(dineroEnCuenta); //Convertimos dineroEncuenta a string para mostrárselo al usuario

            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SU SALDO ACTUAL ES DE"));
            atm.setInputAreaText(cantidad + " euros");

            this.detenerPrograma();
            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SELECCIONE CUÁNTO QUIERE RETIRAR"));

            textoRecogido = textoUsuario.waitText(10);//Llamamos a inputText para leer el valor introducido
            indicado = textoUsuario.getInt(); //Almacenamos el valor en la variable indicado

            if (textoRecogido == true && indicado <= disponible && indicado != 0 && indicado <= dineroEnCuenta) {//Entramos cuando se recoge un texto de manera correcta y indicado es menor o igual a la cantidad disponible en la cuenta y en el cajero y no es cero

                if ((indicado % 10) == 0) {//Entramos si la cantidad a retirar seleccionada por el usuario es múltiplo de 10

                    atm.setInputAreaText(" ");
                    exito = atm.expelAmount(indicado, 10); //Expulsamos el dinero seleccionado durante 10 segundos

                    if (exito == false) { //Si el dinero no es recogido
                        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("DINERO RECUPERADO"));
                        this.detenerPrograma();
                    }
                    if (exito == true) {//Si el dinero es recogido
                        
                        boolean resta = false;
                        try {//Restamos la cantidad retirada de la cuenta
                            resta = server.doOperation(atm.getCardNumber(), -indicado);
                        } catch (CommunicationException ex) {

                            test = error.doOperation();
                            exito = false;
                        }
                        if (resta == true) {
                            dineroEnCuenta = dineroEnCuenta - indicado;
                        }
                        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("OPERACIÓN EXITOSA"));
                        lista.add("  -" + indicado + " " + java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("EUROS"));
                        this.detenerPrograma();
                    }
                    if (exito == true) {//Imprimimos el ticket de la operación
                        atm.print(lista);
                    }
                } else {//Si la cantidad seleccionada no es múltiplo de 10
                    atm.setInputAreaText(" ");
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("CANTIDAD INADECUADA"));
                    this.detenerPrograma();
                }
            }

            if (textoUsuario.getText() == "N" || indicado > disponible || indicado > dineroEnCuenta) {//Si la cantidad seleccionada es mayor que el balance de la cuenta o si se pulsa N
                if (textoUsuario.getText() == "N") {//Si se pulsa N
                    atm.setInputAreaText(" ");
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("OPERACIÓN CANCELADA"));
                    this.detenerPrograma();
                } else if (indicado > disponible && indicado < dineroEnCuenta) { //Si indicado es mayor que disponible y menor que dinero enn cuenta
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("CANTIDAD NO DISPONIBLE EN CAJERO"));
                    this.detenerPrograma();
                } else if (indicado > dineroEnCuenta) {
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SALDO INSUFICIENTE"));
                    this.detenerPrograma();
                }
                exito = false;
            }
            if (textoUsuario.getInt() == 0) {//si no se pulsa nada
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("OPERACIÓN CANCELADA"));
                this.detenerPrograma();
                exito = false;
            }

        }
        return exito;
    }

    private void detenerPrograma() {   //Ponemos a "Dormir" el programa durante los ms que queremos
        try {
            Thread.sleep(2 * 1000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
