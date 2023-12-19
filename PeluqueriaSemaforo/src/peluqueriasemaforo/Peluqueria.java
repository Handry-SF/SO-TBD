package peluqueriasemaforo;

import java.util.Random;
import java.util.concurrent.Semaphore;
/**
 *
 * @author Handry
 */
public class Peluqueria 
{
    
    public static int N = 1; //Numero de sillas en la peluqueria
    
    public static void main(String[] args) throws InterruptedException {
        //Variables
        int contador = 0; //Numero de clientes que se han pasado por la peluqueria en la sesion
        Semaphore SillasEspera = new Semaphore(N);
        Semaphore sillaCortar = new Semaphore(1); //Solo hay 1 peluquero

        //Crear 10 clientes
        for (int o = 0; o <= 10; o++)
        {
            Cliente c = new Cliente(contador, SillasEspera, sillaCortar);
            //Ejecutar accion del cliente
            c.start();

            contador++;

            //Tiempo para siguiente cliente se pase por la peluqueria (2.5-3s)
            Random r = new Random();
            int minT = 2500;
            int maxT = 3000;
            int resulT = r.nextInt(maxT-minT) + minT;
            Thread.sleep(resulT);
        }
    }  
}