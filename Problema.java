package caso;
import java.util.LinkedList;

public class Problema extends Thread{

	public static void main(String[] args) throws InterruptedException
	{
		final PC pc = new PC();


		// Thread del productor
		Thread t1 = new Thread(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					pc.producir();
				}

				catch(InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}); 
		
		// Thread para 'producir' hacia el buzon intermedio
		Thread t2 = new Thread(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					pc.producirPasaAIntermedio();
				}

				catch(InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}); 

		// Thread para consumir del buzon intermedio
		Thread t3 = new Thread(new Runnable() 
		{   public void run()
			{
				try 
				{
					pc.consumirDeIntermedio();
				}
				catch(InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		});
		
		// Thread para consumir del buzon de consumidores
		Thread t4 = new Thread(new Runnable() 
		{   public void run()
			{
				try 
				{
					pc.consumir();
				}
				catch(InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		});
		
		t1.start();
		t2.start(); 
		t3.start(); 
		t4.start();
		
		t1.join();
		t2.join();
		t3.join();
		t4.join(); 
	}



	public static class PC extends Thread
	{
		// Crear una lista compartida por consumidor y productor
		LinkedList<Integer> buzonIntermedio = new LinkedList<>(); 
		LinkedList<Integer> buzonProductores = new LinkedList<>(); 
		LinkedList<Integer> buzonConsumidores = new LinkedList<>(); 
		
		int capacidadBuzonProductores = 3; 
		int capacidadBuzonIntermedio = 3; 
		int capacidadBuzonConsumidores = 3; 

		
		// Productores -> Buzon productores
		// Con yield
		public void producir() throws InterruptedException
		{
			int valor = 0; 
			while(true) 
			{
				synchronized (this) {
					// Thread del productor espera mientras la lista este llena
					while(buzonProductores.size() == capacidadBuzonProductores) 
					{
						//Thread.yield();  
						wait(); 
					}

					System.out.println("Productor produjo a buzon productor: "+valor);


					// Para insertar los trabajos en la lista
					buzonProductores.add(valor++); 

					notify(); 
					Thread.sleep(1000);


				}
			}
		}
		
		// Buzon productores -> Buzon intermedio
		// Con espera pasiva
		public void producirPasaAIntermedio() throws InterruptedException
		{
			while(true) 
			{
				synchronized(this) 
				{
					while(buzonIntermedio.size() == capacidadBuzonIntermedio) 
					{
						wait(); 
					}
					
					int valor = buzonProductores.removeFirst();
					System.out.println("Producto pasa a buzon intermedio: " + valor);
					buzonIntermedio.add(valor); 
					
					notify(); 
					Thread.sleep(1000);
				}
			}
		}
		
		// Intermedio -> Buzon consumidores
		// Con espera activa
		public void consumirDeIntermedio() throws InterruptedException
		{
			while(true) 
			{
				synchronized (this) 
				{
					while(buzonIntermedio.size() == 0) 
					{
						wait(); 
					}
					int valor = buzonIntermedio.removeFirst(); 
					System.out.println(valor + " pasa al buzón de consumidores: "+ valor );
					buzonConsumidores.add(valor); 
					
					notify();
					Thread.sleep(1000);
					
				}
			}
		}
		
		// Consumidores -> Consumir
		// con yield
		public void consumir() throws InterruptedException
		{
			while(true) 
			{
				synchronized (this) 
				{
					// Thread del consumidor espera mientras la lista 
					// esté vacía 
					while(buzonConsumidores.size() == 0) 
					{
						wait(); 
					}

					// Tomar el primer elemento de la lista de productos
					int val = buzonConsumidores.removeFirst(); 
					// Append to the buzon of consumers

					System.out.println("Consumdidor consumió: "
							+ val); 
					
					
					notify(); 
					Thread.sleep(1000); 

				}
			}
		}
		
		
	}

}
