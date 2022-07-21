package urjcatm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import sienens.ATM;
import urjc.UrjcBankServer;
import java.util.Locale;

/**
 *
 * @author Iván
 */
public class ClientManagement extends AtmOperation {

    private IdiomSelection idioma = new IdiomSelection(atm, server);
    private ErrorExit error = new ErrorExit(atm, server, true);
    private ClientIdentification ident = new ClientIdentification(atm, server, error, idioma);
    private OptionMenu menu = new OptionMenu(atm, server, error, idioma);
    private ChangePassword cambioContraseña = new ChangePassword(atm, server, idioma, error);
    private ArrayList<AtmOperation> propertiesList = new ArrayList<>();
    private WithdrawCash sacarDinero = new WithdrawCash(atm, server, error, idioma);
    private AccountBalance obtenerSaldo = new AccountBalance(server, atm, error, idioma);
    private LastOperations ultimasOperaciones = new LastOperations(atm, server, error, idioma);
    private Transfer transferencia = new Transfer(atm, server, error, idioma);
    private ArrayList<String> ficheroOperaciones = new ArrayList<>();

    ClientManagement(ATM atm, UrjcBankServer server) {
        super(atm, server);

    }

    @Override
    public boolean doOperation() {//Llamamos a presentOptions para que se repita el proceso 

        for (int i = 0; i < 20000; i++) {

            this.presentOptions();

        }
        return true;
    }

    private void presentOptions() {

        boolean comunicación = true;
        boolean test = false;
        int cont = 0;
        boolean identificado = false;
        int n = 3;
        char detectada = ' ';
        boolean idiomaElegido = false;
        char opIdioma = ' ';

        Locale loc = new Locale("es", "ES");//El idioma por defecto es español
        idioma.setIdioma(loc);

        while (error.GeterrorComunicacion() == false) {//No se vuelve al programa hasta que no se restablezca la conexión 
            atm.setTitle(" ");
            atm.setOption(0, null);
            atm.setOption(1, null);
            atm.setOption(2, null);
            atm.setOption(3, null);
            atm.setOption(4, null);
            atm.setOption(5, null);
            atm.setInputAreaText(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("RECONECTANDO"));
            atm.waitEvent(1);
            atm.setInputAreaText(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("RECONECTANDO."));
            atm.waitEvent(1);
            atm.setInputAreaText(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("RECONECTANDO.."));
            atm.waitEvent(1);
            atm.setInputAreaText(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("RECONECTANDO..."));
            atm.waitEvent(1);
            error.SeterrorComunicacion(server.comunicationAvaiable());
            test = true;
            comunicación = true;
            transferencia.setTest(test);
            sacarDinero.setTest(test);
            cambioContraseña.setTest(test);
            ultimasOperaciones.setTest(test);
            obtenerSaldo.setTest(test);
        }

        atm.setInputAreaText(" ");
        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("INTRODUZCA LA TARJETA"));//Layout de inicio
        for (int i = 0; i < 6; i++) {
            atm.setOption(i, null);
        }
        char c = atm.waitEvent(10);
        while (c != 1) {   //Seguimos pidiendo la tarjeta hasta que se detecte una
            c = atm.waitEvent(10);
        }

        test = server.comunicationAvaiable();

        if (test == true) { //si existe comunicación con el servidor retenemos la tarjeta temporalmente y comenzamos el proceso
            do {

                atm.retainCreditCard(false);
                if (cont == 0) { //Para que te pregunte el idioma a elegir únicamente 1 vez y no te lo pregunte en cada intento
                    do {
                        idioma.doOperation();//llamamos a doOperation de idiom selection para mostrar los idiomas disponibles para el usuario
                        opIdioma = atm.waitEvent(10);//Una vez que detecte la tarjeta se hará la elección de idioma

                        //Seteamos el idioma elegido para mostrar todo el programa de esa forma
                        if (opIdioma == 'A') {

                            loc = new Locale("es", "ES");
                            idioma.setIdioma(loc);
                            idiomaElegido = true;

                        } else if (opIdioma == 'B') {

                            loc = new Locale("en", "EN");
                            idioma.setIdioma(loc);
                            idiomaElegido = true;

                        } else if (opIdioma == 'D') {

                            loc = new Locale("eus", "EUS");
                            idioma.setIdioma(loc);
                            idiomaElegido = true;

                        } else if (opIdioma == 'E') {

                            loc = new Locale("cat", "CAT");
                            idioma.setIdioma(loc);
                            idiomaElegido = true;

                        } else if (opIdioma == 'N') { //Si se pulsa la tecla N en vez de elegir idioma le devolvemos la tarjeta
                            atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TENGA UN BUEN DÍA"));
                            for (int i = 0; i < 6; i++) {
                                atm.setOption(i, null);
                            }
                            boolean recogida = atm.expelCreditCard(10);

                            if (recogida == false) { //Si no recoge la tarjeta durante los próximos 10 segundos la retenemos permanentemente
                                atm.retainCreditCard(true);
                                atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TARJETA RETENIDA"));
                            }
                            idiomaElegido = false;

                        }

                    } while (opIdioma != 'A' && opIdioma != 'B' && opIdioma != 'D' && opIdioma != 'E' && opIdioma != 'N');
                }

                if (opIdioma == 'A' || opIdioma == 'B' || opIdioma == 'D' || opIdioma == 'E') {//Aquí solo entramos si algún idioma ha sido elegido
                    ident.setControlN(' '); //Le pasamos un caracter vacío a la variable de control N

                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("INTRODUZCA LA CONTRASEÑA"));
                    for (int i = 0; i < 6; i++) {
                        atm.setOption(i, null);
                    }

                    identificado = ident.doOperation(); //Llamamos a ClientIdentification para hacer la comprobación de la contraseña y recogemos el valor devuelto en la variable identificado

                    if (identificado == false) {  //Restamos uno a los 3 intentos iniciales y sumamos uno al contador de los que llevamos
                        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("CONTRASEÑA INCORRECTA"));
                        n = n - 1;
                        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("LE QUEDAN " + n + " INTENTOS")); //Mostramos al usuario los intentos restantes
                        atm.setInputAreaText(" ");
                        cont = cont + 1;
                    }
                }

                if (cont == 3) { //cuando llegamos al límite de intentos especificado retenemos permanentemente la tarjeta
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("LÍMITE DE INTENTOS SUPERADO"));
                    atm.retainCreditCard(true);
                    identificado = false;
                }

            } while (identificado == false && cont < 3 && ident.getControlN() != 'N' && idiomaElegido == true  && ident.getControl0()!='0');
        }

        test = server.comunicationAvaiable(); //volvemos a comprobar la comunicación con el servidor

        if (test == true) {
            if (identificado == true) {  //Entramos cuando se verifica el usuario en menos de 3 intentos
                String escrbirOperacion;
                boolean recogida = false;
                boolean seguir = false;
                int anuncioContador = 0;
                do {
                    menu.doOperation(); //Llamamos a option menu para que nos muestre las opciones disponibles
                    if (menu.getOperacion() == 'A') {

                        boolean exito = sacarDinero.doOperation();

                        if (exito == true) {  //Si la operación es realizada correctamente almacenamos el número de tarjeta y la operación y la guardamos en un ArrayList para posteriormente escribirla en el fichero
                            long num = atm.getCardNumber();
                            String numTarjeta = Long.toString(num);
                            escrbirOperacion = ("Retirada de efectivo, Número de tarjeta:  " + numTarjeta + "\n"); // String que almacenaremos en el ArratList
                            ficheroOperaciones.add(escrbirOperacion);
                        }

                        comunicación = sacarDinero.isTest(); //Comprobamos que durante la operación no se ha producido ningún error de comunicación

                        if (comunicación == true) { //Si no hay comunicación no se sigue
                            if (anuncioContador < 4) {   //Durante 4 operaciones no muestra anuncios repetidos
                                this.anuncios(anuncioContador);
                                anuncioContador = anuncioContador + 1;
                            } else if (anuncioContador == 4) {
                                anuncioContador = 0;  //Reiniciamos contador para que siga mostrando anuncios sin repetirlos
                                this.anuncios(anuncioContador + 1);
                            }

                            seguir = menu.seguirOperando(); //Preguntamos al usuario si desea seguir operando 
                        }

                    } else if (menu.getOperacion() == 'B') {

                        boolean exito = obtenerSaldo.doOperation();

                        if (exito == true) { //Si la operación es realizada correctamente almacenamos el número de tarjeta y la operación y la guardamos en un ArrayList para posteriormente escribirla en el fichero
                            long num = atm.getCardNumber();
                            String numTarjeta = Long.toString(num);
                            escrbirOperacion = ("Balance de la cuenta, Número de tarjeta:  " + numTarjeta + "\n");  // String que almacenaremos en el ArratList
                            ficheroOperaciones.add(escrbirOperacion);
                        }

                        comunicación = obtenerSaldo.isTest(); //Comprobamos que durante la operación no se ha producido ningún error de comunicación

                        if (comunicación == true) { //Si no hay comunicación no se sigue
                            if (anuncioContador < 4) {   //Durante 4 operaciones no muestra anuncios repetidos
                                this.anuncios(anuncioContador);
                                anuncioContador = anuncioContador + 1;
                            } else if (anuncioContador == 4) {
                                anuncioContador = 0;  //Reiniciamos contador para que siga mostrando anuncios sin repetirlos
                                this.anuncios(anuncioContador + 1);
                            }

                            seguir = menu.seguirOperando(); //Preguntamos al usuario si desea seguir operando 
                        }

                    } else if (menu.getOperacion() == 'C') {

                        boolean exito = transferencia.doOperation();

                        if (exito == true) { //Si la operación es realizada correctamente almacenamos el número de tarjeta y la operación y la guardamos en un ArrayList para posteriormente escribirla en el fichero
                            long num = atm.getCardNumber();
                            String numTarjeta = Long.toString(num);
                            escrbirOperacion = ("Transferencia , Número de tarjeta:  " + numTarjeta + "\n"); // String que almacenaremos en el ArratList
                            ficheroOperaciones.add(escrbirOperacion);
                        }

                        comunicación = transferencia.isTest(); //Comprobamos que durante la operación no se ha producido ningún error de comunicación

                        if (comunicación == true) { //Si no hay comunicación no se sigue
                            if (anuncioContador < 4) {   //Durante 4 operaciones no muestra anuncios repetidos
                                this.anuncios(anuncioContador);
                                anuncioContador = anuncioContador + 1;
                            } else if (anuncioContador == 4) {
                                anuncioContador = 0;  //Reiniciamos contador para que siga mostrando anuncios sin repetirlos
                                this.anuncios(anuncioContador + 1);
                            }

                            seguir = menu.seguirOperando(); //Preguntamos al usuario si desea seguir operando 
                        }

                    } else if (menu.getOperacion() == 'D') {

                        boolean exito = ultimasOperaciones.doOperation();

                        if (exito == true) { //Si la operación es realizada correctamente almacenamos el número de tarjeta y la operación y la guardamos en un ArrayList para posteriormente escribirla en el fichero
                            long num = atm.getCardNumber();
                            String numTarjeta = Long.toString(num);
                            escrbirOperacion = ("Últimas Operaciones, Número de tarjeta:  " + numTarjeta + "\n"); // String que almacenaremos en el ArratList
                            ficheroOperaciones.add(escrbirOperacion);
                        }

                        comunicación = ultimasOperaciones.isTest(); //Comprobamos que durante la operación no se ha producido ningún error de comunicación

                        if (comunicación == true) { //Si no hay comunicación no se sigue
                            if (anuncioContador < 4) {   //Durante 4 operaciones no muestra anuncios repetidos
                                this.anuncios(anuncioContador);
                                anuncioContador = anuncioContador + 1;
                            } else if (anuncioContador == 4) {
                                anuncioContador = 0; //Reiniciamos contador para que siga mostrando anuncios sin repetirlos
                                this.anuncios(anuncioContador + 1);
                            }

                            seguir = menu.seguirOperando(); //Preguntamos al usuario si desea seguir operando 

                        }

                    } else if (menu.getOperacion() == 'E') {

                        boolean exito = cambioContraseña.doOperation();

                        if (exito == true) { //Si la operación es realizada correctamente almacenamos el número de tarjeta y la operación y la guardamos en un ArrayList para posteriormente escribirla en el fichero
                            long num = atm.getCardNumber();
                            String numTarjeta = Long.toString(num);
                            escrbirOperacion = ("Cambio de contraseña, Número de tarjeta:  " + numTarjeta + "\n"); // String que almacenaremos en el ArratList
                            ficheroOperaciones.add(escrbirOperacion);
                        }

                        comunicación = cambioContraseña.isTest(); //Comprobamos que durante la operación no se ha producido ningún error de comunicación

                        if (comunicación == true) { //Si no hay comunicación no se sigue
                            if (anuncioContador < 4) {   //Durante 4 operaciones no muestra anuncios repetidos
                                this.anuncios(anuncioContador);
                                anuncioContador = anuncioContador + 1;
                            } else if (anuncioContador == 4) {
                                anuncioContador = 0;  //Reiniciamos contador para que siga mostrando anuncios sin repetirlos
                                this.anuncios(anuncioContador + 1);
                            }

                            seguir = menu.seguirOperando();  //Preguntamos al usuario si desea seguir operando 
                        }
                    } 
//Código para añadir una operación más si se necesitara
                    /*else if (menu.getOperacion() == 'F') {

                        boolean exito = transferencia.doOperation();

                        if (exito == true) { //Si la operación es realizada correctamente almacenamos el número de tarjeta y la operación y la guardamos en un ArrayList para posteriormente escribirla en el fichero
                            long num = atm.getCardNumber();
                            String numTarjeta = Long.toString(num);
                            escrbirOperacion = ("Transferencia , Número de tarjeta:  " + numTarjeta + "\n"); // String que almacenaremos en el ArratList
                            ficheroOperaciones.add(escrbirOperacion);
                        }

                        comunicación = transferencia.isTest(); //Comprobamos que durante la operación no se ha producido ningún error de comunicación

                        if (comunicación == true) { //Si no hay comunicación no se sigue
                            if (anuncioContador < 4) {   //Durante 4 operaciones no muestra anuncios repetidos
                                this.anuncios(anuncioContador);
                                anuncioContador = anuncioContador + 1;
                            } else if (anuncioContador == 4) {
                                anuncioContador = 0;  //Reiniciamos contador para que siga mostrando anuncios sin repetirlos
                                this.anuncios(anuncioContador + 1);
                            }

                            seguir = menu.seguirOperando(); //Preguntamos al usuario si desea seguir operando 
                        }*/ 
                    else if (menu.getOperacion() == 'N') { //Cuando el usuario pulsa la N en el menu ponemos seguir a false para salir del bucle
                        seguir = false;
                    }

                } while (seguir == true && comunicación == true); //solo saldremos del bucle si seguir es igual a false o si la comunicación se interrumpe

                if (comunicación == true) { //Si no ha habido errores de comunicación con el servidor realizando las operaciones 
                    this.ficheroOperaciones();// Escribimos en el fichero todas las operaciones realizadas
                }

                if (comunicación == true) { //Si todo ha ido como debe devolvemos la tarjeta al usuario durante 10 segundos y la retenemos permanentemente si no la recoge
                    atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TENGA UN BUEN DÍA"));
                    for (int i = 0; i < 6; i++) {
                        atm.setOption(i, null);
                    }
                    recogida = atm.expelCreditCard(10);
                    if (recogida == false) {
                        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("TARJETA RETENIDA"));
                        atm.retainCreditCard(true);

                    }
                }
            }

        }
    }

    private void ficheroOperaciones() { //Método para escribir las operaciones realizadas en el fichero que seleccionemos (el programa escribe todas las operaciones que se realicen durante una ejecución)
                                        //El registro de operaciones en el fichero se  hace siempre en español por que el banco estaría en españa pero si quisieramos podríamos hacer que se guardaran en el idioma seleccionado
        FileWriter fichero = null;
        try {

            fichero = new FileWriter("C:\\Users\\Iván\\Desktop\\Práctica POO\\UrjcATM\\ficheroOperaciones.txt"); //Ruta completa del fichero donde escribiremos

            // Escribimos linea a linea en el fichero
            for (int i = 0; i < ficheroOperaciones.size(); i++) { //Recorremos el arrayList y vamos escrbiendo el contenido de las posiciones
                String linea = ficheroOperaciones.get(i);
                fichero.write(linea + "\n");
            }

            fichero.close();

        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }

    private void anuncios(int cont) { //Recibe como parámetro el contador para que no se muestren anuncios repetidos
        String anuncios[] = new String[4];
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File("C:\\Users\\Ivan\\Desktop\\universidad\\Práctica POO\\UrjcATM");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;

            for (int i = 0; i < anuncios.length; i++) { //Recorremos el array y vamos guardando las líneas de los anuncios en sus posiciones correspondientes
                linea = br.readLine();
                anuncios[i] = linea;

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta 
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        atm.setTitle(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString("PUBLICIDAD"));
        atm.setInputAreaText(java.util.ResourceBundle.getBundle("res/string", idioma.getIdioma()).getString(anuncios[cont])); //Va mostrando anuncios distintos en operaciones sucesivas indicándole el anuncio que toca con el parámetro cont
        this.detenerPrograma();
    }

    private void detenerPrograma() {   //Ponemos a "Dormir" el programa durante los ms que queremos
        try {
            Thread.sleep(5 * 1000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
