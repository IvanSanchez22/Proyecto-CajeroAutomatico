
package urjcatm;
import sienens.ATM;
import urjc.UrjcBankServer;
/**
 *
 * @author Iván
 */
public class ErrorExit extends AtmOperation {

    boolean errorComunicacion;

    public ErrorExit(ATM atm, UrjcBankServer server, boolean errorComunicación) {
        super(atm, server);
        this.errorComunicacion = errorComunicación;
    }

    //Getters y setters de errorComunicacion
    public boolean GeterrorComunicacion() {
        return errorComunicacion;
    }

    public void SeterrorComunicacion(boolean errorComunicacion) {
        this.errorComunicacion = errorComunicacion;
    }

    
    
    

    @Override
    public boolean doOperation() {
        atm.setTitle("Error de comunicación");//Layout de la pantalla de error
        atm.setOption(0, null);
        atm.setOption(1, null);
        atm.setOption(2, null);
        atm.setOption(3, null);
        atm.setOption(4, null);
        atm.setOption(5, null);
        atm.setInputAreaText(null);
        boolean retirada = atm.expelCreditCard(30);
        if (retirada == false) { //Si no recoge la tarjeta la retenemos permanentemente
            atm.retainCreditCard(true);
        }
        errorComunicacion = false;
        return errorComunicacion;
    }
}
