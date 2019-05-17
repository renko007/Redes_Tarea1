// Java implementation of  Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 
  
import java.io.*; 
import java.text.*;
import java.util.Date;
import java.net.*;
  
// Server class 
public class Server  
{ 
    private static ServerSocket ss;

	public static void main(String[] args) throws IOException  
    { 
		System.out.println("Started "); 
        try {
        	ss = new ServerSocket(5056); 
        }
        catch (Exception e){ 
            e.printStackTrace(); 
        }   
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
            //aca irian todos los cambios de las respectivas operaciones
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                Date date = new Date();
                DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                String ip=s.getRemoteSocketAddress().toString();
                out.println(hourdateFormat.format(date) + "  conexion  " + ip + " conexion entrante  ");
                out.close();  
                System.out.println("Nuevo cliente conectado : " + s); 
                  
                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                
  
                // create a new thread object 
                Thread t = new ClientHandler(s, dis, dos); 
  
                // Invoking the start() method 
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        }
        
    } 
} 
  
// ClientHandler class 
class ClientHandler extends Thread  
{ 
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
      
  
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 
  
    @Override
    public void run()  
    { 
        String received; 
        String toreturn; 
        while (true)  
        { 
            try { 
  
                // Pregunta al usuario que quiere 
                dos.writeUTF("Insertar operacion: "); 
                  
                // recibe respuesta del cliente
                received = dis.readUTF(); 
                //recibe comando exit
                if(received.equals("Exit"))
                {
                	//guarda en el log.txt
                	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
                	Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    String ip=s.getRemoteSocketAddress().toString();
                    out.println(hourdateFormat.format(date) + "  salida  " + ip + " saliendo  ");
                    out.close();
                    System.out.println("Cliente " + this.s + " saliendo..."); 
                    System.out.println("Cerrando conexion."); 
                    this.s.close(); 
                    System.out.println("Conexion cerrada"); 
                    break; 
                } 
                //recibe comando ls
                if(received.equals("ls")) {
                	File act = new File (".");
                	String[] ficheros = act.list();
                	dos.writeInt(ficheros.length);
                	for (int i=0; i<ficheros.length; i++) {
                		dos.writeUTF(ficheros[i]);
                	}
                	//guarda en el log.txt
                	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
                	Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    String ip=s.getRemoteSocketAddress().toString();
                    out.println(hourdateFormat.format(date) + "  command  " + ip + " ls  ");
                    out.close();
                }
                //recibe comando get
                else if(received.contains("get")) {
                	String[] parts = received.split(" ");
                	String name=parts[1];
                	System.out.println(name);
                    byte [] bytes =new byte [16*1024];
                    InputStream in = new FileInputStream(name);
                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        dos.write(bytes, 0, count);
                    }
                    dos.writeUTF("stop");
                    //dos.flush();
                    in.close();
                    System.out.println("Subida realizada");
                    //guarda en el log.txt
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
                	Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    String ip=s.getRemoteSocketAddress().toString();
                    out.println(hourdateFormat.format(date) + "  operacion  " + ip + " get "+name);
                    out.close();
                }
                //recibe comando put
                else if(received.contains("put")) {
                	InputStream in = null;
                    OutputStream out = null;
                	try {
                        in = s.getInputStream();
                    } catch (IOException ex) {
                        System.out.println("Can't get socket input stream. ");
                    }

                    try {
                        out = new FileOutputStream(received.split(" ")[1]);
                    } catch (FileNotFoundException ex) {
                        System.out.println("File not found. ");
                    }

                    byte[] bytes = new byte[16*1024];

                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }
                    out.close();
                    System.out.println("Descarga realizada");
                    //guarda en el log.txt
                    PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
                	Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    String ip=s.getRemoteSocketAddress().toString();
                    out2.println(hourdateFormat.format(date) + "  operacion  " + ip + " put "+received.split(" ")[1]);
                    out2.close();
                }
                //recive comando delete
                else if(received.contains("delete")) {
                	String delete=received.split(" ")[1];
                	File file=new File(delete);
                	file.delete();
                	dos.writeUTF("Operacion completada");
                	//guarda en el log.txt
                	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
                	Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    String ip=s.getRemoteSocketAddress().toString();
                    out.println(hourdateFormat.format(date) + "  operacion  " + ip + " delete "+delete);
                    out.close();
                }
                else{
                	dos.writeUTF("Invalid input"); 
                }
            } catch (IOException e) { 
                e.printStackTrace(); 
            } 
        } 
          
        try
        { 
            // cerrando recursos 
            this.dis.close(); 
            this.dos.close(); 
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
} 
