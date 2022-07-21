/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urjcatm;

import sienens.ATM;
import urjc.UrjcBankServer;

/**
 *
 * @author Iván
 */
public class InputText {

    private UrjcBankServer server;
    private ATM atm;
    private boolean hidden;
    private int numChars;
    private String text;
    private int numText;

    public InputText(ATM atm, boolean hidden, int numChars) {
        this.atm = atm;
        this.hidden = hidden;
        this.numChars = numChars;

    }

    boolean waitText(int seconds) {
        char digito = ' ';
        String cadena = "";
        int contadorDígitos = 0; //Variable para controlar que lea los digítos indicados en el constructor
        String oculto = "";
        boolean textoCapturado = false;

        do {
            digito = atm.waitEvent(seconds); //Leemos el primer dígito
            if (digito != 0 && digito != 'N' && digito != 'Y') { //Si es distinto de estos 3 entramos

                if (digito != '-') { //si el digito no es la barra para borrar entramos

                    cadena = cadena + Character.toString(digito); //Convertimos el dígito a String y se lo añadimos a la variable cadena para ir almacenando el texto
                    contadorDígitos = contadorDígitos + 1; //Incrementamos contador de dígitos

                    if (hidden == false) { //solo se lo mostramos al usuario en caso de que hidden sea false

                        atm.setInputAreaText(cadena);

                    } else { //Si hidden es true le mostramos asteriscos
                        oculto = oculto + Character.toString('*');
                        atm.setInputAreaText(oculto);
                    }
                }

                if (!cadena.equals("") && digito == '-') { //Si la cadena no es vacía y el usuario pulsa borrar
                    cadena = cadena.substring(0, cadena.length() - 1); //Le quitamos a la cadena almacenada el último dígito
                    contadorDígitos = contadorDígitos - 1; //Restamos 1 al contador de dígitos
                    
                    if (hidden == false) { //si hidden es false le mostramos la cadena actualizada

                        atm.setInputAreaText(cadena); 
                    }

                    if (hidden == true) { //Si hidden es true le mostramos un asterisco menos
                        oculto = oculto.substring(0, oculto.length() - 1);
                        atm.setInputAreaText(oculto);
                    }

                }

                if (digito == '-' && cadena.equals("")) { //si el usuario pulsa borrar cuando la cadena es vacía no mostramos nada

                    atm.setInputAreaText(" ");

                }

            }
        } while (digito != 0 && digito != 'N' && digito != 'Y' && contadorDígitos != numChars); // Si no sabemos los caracteres exactos que quiere leer el usuario le pasaremos un numChar muy grande
        // Y solo saldrá del bucle por alguna de las otras 3 condiciones
        
        if (digito == 0) { //si el usuario no pulsa nada el método devuelve false y numText se pone a 0

            textoCapturado = false;
            numText = 0;
        }
        if (digito == 'N') { //si el usuario pulsa N el método devuelve false y se pone numText a 1 y text a N
            textoCapturado = false;
            numText = 1;
            text = "N";
        }

        if (digito == 'Y' || contadorDígitos == numChars) { //si el usuario ha pulsado Y o hemos llegado al límite de dígitos especificado en el constructor
            
            if (contadorDígitos == 0) {//Si ha pulsado Y siendo la cadena vacía
                text = "";
                numText = 2;
                textoCapturado = false;
            }

            if (contadorDígitos != 0) { //si ha pulsado Y sin ser la cadena vacía asignamos a text la cadena y a numText el valor numérico
                textoCapturado = true;
                text = cadena;
                numText = Integer.parseInt(cadena);

            }

        }
        return textoCapturado;
    }

    boolean waitTextTransfer(int seconds) {
        char digito = ' ';
        String cadena = "";
        int contadorDígitos = 0; //Variable para controlar que lea los digítos indicados en el constructor
        String oculto = "";
        boolean textoCapturado = false;
        int cont = 0; // Variable para dejar espacios en blanco en posiciones concretas
        do {
            digito = atm.waitEvent(seconds); //Leemos el primer dígito
            if (digito != 0 && digito != 'N' && digito != 'Y') { //si es distinto de estos 3 entramos

                if (digito != '-') { //Entramos cuando el dígito es distinto de la barra para borrar
                    
                    if (cont == 4) { //Si el contador es igual a 4 metemos espacio a la cadena
                        cadena = cadena + " ";
                    }
                    if (cont == 8) { //Si el contador es igual a 8 metemos espacio a la cadena
                        cadena = cadena + " ";
                    }
                    if (cont == 10) { //Si el contador es igual a 10 metemos espacio a la cadena
                        cadena = cadena + " ";
                    }

                    cadena = cadena + Character.toString(digito);//Pasamos dígito a String y se lo sumamos a la cadena
                    cont = cont + 1; //Actualizamos contador de posiciones
                    contadorDígitos = contadorDígitos + 1; //Actualizamos contador de dígitos
                    
                    if (hidden == false) { //Si hidden es false mostramos la cadena
                        atm.setInputAreaText(cadena);
                    } else { //Si hidden es true mostramos un asterísco por cada dígito
                        oculto = oculto + Character.toString('*');
                        atm.setInputAreaText(oculto);
                    }
                }
                if (!cadena.equals("") && digito == '-') { //Si la cadena no es vacía y el usuario pulsa borrar
                    
                    if (hidden == false) { //Si hidden es false actualizamos la cadena y se la mostramos
                        cadena = cadena.substring(0, cadena.length() - 1);
                        atm.setInputAreaText(cadena);

                        //Decrementamos el contador solo si no es espacio en blanco
                        if (cadena.length() != 4 && cadena.length() != 8 && cadena.length() != 10) {
                            cont = cont - 1;
                            contadorDígitos = contadorDígitos - 1; //Decrementamos el contador de dígitos
                        }
                    } else {//Si hidden es true le msotramos un asterisco menos
                        oculto = oculto.substring(0, oculto.length() - 1);
                        atm.setInputAreaText(oculto);
                    }
                }

                if (digito == '-' && cadena.equals("")) { //Si el usuario pulsa borrar y la cadena es vacía no mostramos nada

                    atm.setInputAreaText(" ");

                }

            }
        } while (digito != 0 && digito != 'N' && contadorDígitos != numChars); //Solo se sale de forma correcta de este bucle cuando hay 20 dígitos

        if (digito == 0) { //Si el dígito es 0 el método devuelve false y ponemos numText a cero
            textoCapturado = false;
            numText = 0;
        }
        if (digito == 'N') { //Si el dígito es N el método devuelve false y asignamos N a text
            textoCapturado = false;
            text = "N";
            numText = 1;
        }

        if (contadorDígitos == numChars) { //Si el contador de dígitos es 20 es decir el número CCC es válido el método devuelve true y a text se le asigna la cadena
            textoCapturado = true;

            text = cadena;
            /*numText = Integer.parseInt(cadena); Aquí no se haría esto ya que cadena tiene espacios
            Y eliminamos los espacios y pasamos a integer externamente en transfer*/

        }

        return textoCapturado;
    }

    String getText() { //Metodo paraa consultar la cadena
        String texto = "";
        texto = this.text;
        return texto;
    }

    int getInt() { //metodo para consultar el valor
        int valor = 0;
        valor = this.numText;
        return valor;
    }

    private void detenerPrograma() {   //Ponemos a "Dormir" el programa durante los ms que queremos
        try {
            Thread.sleep(2 * 1000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
