
package urjcatm;


import javax.naming.CommunicationException;
import sienens.ATM;
import urjc.UrjcBankServer;


/**
 *
 * @author Iván
 */
public class ChangePassword extends AtmOperation {

    private ErrorExit error = new ErrorExit(atm, server, true);
    private IdiomSelection idioma = new IdiomSelection(atm, server);
    private InputText textoUsuario = new InputText(atm, false, 5); //Le pasamos 5 para que el usuario introduzca los dígitos que quiera, hasta un límite de 5 y false para que pueda ver la nueva contraseña
    private boolean test = true;

    
    public ChangePassword(ATM atm, UrjcBankServer server, IdiomSelection idioma, ErrorExit error) {
        super(atm, server);
        this.error = error;
        this.idioma = idioma;
    }

    public boolean isTest() { //Método para consultar test
        return test;
    }

    public void setTest(boolean test) { //Metodo para setear test cuando se recupera
        this.test = test;
    }
    

    @Override
    public boolean doOperation() {

        boolean exito = false;
        boolean textoRecogido = false;

        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("INTRODUZCA SU NUEVA CONTRASEÑA"));
        for (int i = 0; i < 6; i++) {
            atm.setOption(i, null);
        }

        textoRecogido = textoUsuario.waitText(10);//Llamada a InputText para recoger el texto

        if (textoUsuario.getInt() == 0) { //Caso en el que el usuario no pulsa nada
            atm.setInputAreaText(" ");
            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("RECOJA SU TARJETA"));
            boolean recogida = atm.expelCreditCard(10);
            if (recogida == false) {
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIEMPO EXCEDIDO"));
                atm.retainCreditCard(true);
                exito = false;
            }
        }

        if (textoUsuario.getText() == "N") {//Caso en el que el usuario pulsa la tecla N
            atm.setInputAreaText(" ");
            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TENGA UN BUEN DÍA"));
            boolean recogida = atm.expelCreditCard(10);
            if (recogida == false) {
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIEMPO EXCEDIDO"));
                atm.retainCreditCard(true);

            }
            exito = false;

        }

        if (textoRecogido == true) {//Caso en el que se recoge bien el texto
            char respuesta = ' ';
            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("SU NUEVA CONTRASEÑA ES"));
            atm.setInputAreaText(textoUsuario.getText());
            this.detenerPrograma();

            do { //Preguntamos al usuario si está de acuerdo con su nueva contraseña
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("¿ESTA DE ACUERDO?"));
                atm.setInputAreaText(" ");
                respuesta = atm.waitEvent(15);

            } while (respuesta != 0 && respuesta != 'N' && respuesta != 'Y');

            if (respuesta == 0) {//Caso en el que el usuario no confirma que está de acuerdo y por lo tanto la contraseña no se cambia
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TIEMPO EXCEDIDO"));
                atm.setInputAreaText(" ");
                this.detenerPrograma();
                exito = false;
            }

            if (respuesta == 'N') {//Caso en el que el usuario no esta deacuerdo y cancela la operativa
                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("CAMBIO CANCELADO"));
                atm.setInputAreaText(" ");
                this.detenerPrograma();
                exito = false;
            }

            if (respuesta == 'Y') {//Caso en el que el usuario está de acuerdo con la contraseña nueva introducida y decide hacer efectivo el cambio

                try { //Le pasamos al servidor la nueva contraseña y el número de la tarjeta del cliente
                    server.changePassword(textoUsuario.getInt(), atm.getCardNumber());
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("CONTRASEÑA CAMBIADA"));

                    this.detenerPrograma();

                    exito = true;
                } catch (CommunicationException ex) {
                    test = error.doOperation();
                    exito = false;
                }

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
