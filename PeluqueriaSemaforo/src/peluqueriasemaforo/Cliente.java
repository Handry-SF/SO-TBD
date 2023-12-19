package peluqueriasemaforo;

import java.util.concurrent.Semaphore;
/**
 *
 * @author Handry
 */
public class Cliente extends Thread 
{
    int id;
    Semaphore SillasEspera;
    Semaphore sillaCortar;

    public Cliente(int id, Semaphore SillasEspera, Semaphore sillaCortar) {
        this.id = id;
        this.SillasEspera = SillasEspera;
        this.sillaCortar = sillaCortar;
    }

    @Override
    public void run() {
        // Entra el cliente
        System.out.println("CLIENTE " + id + ": Hola Tienes Silla Disponible");		
        //Comprueba si hay sillas libres
        comprobarSillasEspera(SillasEspera, sillaCortar);		
        // Cliente se va
        System.out.println("CLIENTE " + id + ": Nos Vemos");
        System.out.println("--------------------------------");
    }

    private void comprobarSillasEspera(Semaphore semaforoSillasEspera, Semaphore semaforoSillaCortar) {
        try {   
            //Comprobar si el peluquero esta disponible
            if(semaforoSillasEspera.availablePermits() == Peluqueria.N && semaforoSillaCortar.availablePermits() > 0) {
                System.out.println("PELUQUERO: Hola, Si");
            } 
            //Mirar si hay sillas libres
            if(semaforoSillasEspera.availablePermits() > 0 ) {
                // Sentarse en una silla
                semaforoSillasEspera.acquire();
                System.out.println("PELUQUERO: Si Tengo Espacio CLIENTE " + id + "- Estado Disponible (Verde)");
                //Cortar el cabello
                comprobarSillaCortar(semaforoSillaCortar);
                //Liberar espacio en el semaforo
                semaforoSillasEspera.release();    
            //No hay sillas
            } else 
            {
                System.out.println("PELUQUERO: No Tengo Espacio, Vuelve Mas Tarde CLIENTE " + id + "- Estado No Disponible (Rojo)");
            }
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
    }

    private void comprobarSillaCortar(Semaphore semaforoSillaCortar) {
        try {
            //Sentado para cortar el cabello
            semaforoSillaCortar.acquire();
            System.out.println("- CLIENTE " + id + " Se Esta Cortando El Cabello " + "- Estado Ocupado (Verde)");
            //Cortar el cabello (5 segundos)
            Thread.sleep(5000);
            System.out.println("- CLIENTE " + id + " Se Ha Cortado El Cabello " + "- Estado Desocupado (Rojo)");
            //Librar espacio en el semaforo de cortar el cabello
            semaforoSillaCortar.release();
        } catch (InterruptedException e) {			
            e.printStackTrace();
        }
    }	
}
