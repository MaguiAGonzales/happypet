package clases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.happypet.movil.happypet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import adaptadores.adaptadorMascotasDisponibles;


public class DataConnection extends AppCompatActivity {
    String nombre, tipo, sexo, anio, particularidades, salud, tamanio, adoptado, esterilizado, idUsuario;

    String funcion, encodedImage, data, image, cargarDatos;
    Activity context;
    ArrayList<DatosImage> listaImage = new ArrayList();
    DatosImage dataImage;
    JSONObject json_data;

    Boolean ok;
    String msg;

    Intent intenPadre;
    ProgressDialog progress;

    public DataConnection(Activity context, String funcion, String encodedImage,  String nombre, String tipo, String sexo, String anio, String particularidades, String salud, String tamanio, String adoptado, String esterilizado, String idUsuario){
        this.context = context;
        this.funcion = funcion;
        this.encodedImage = encodedImage;

        this.nombre = nombre;
        this.tipo = tipo;
        this.sexo = sexo;
        this.anio = anio;
        this.particularidades = particularidades;
        this.salud = salud;
        this.tamanio = tamanio;
        this.adoptado = adoptado;
        this.esterilizado = esterilizado;
        this.idUsuario = idUsuario;

        this.ok = false;
        this.msg = "";

        new GetAndSet(this.context).execute();
    }

    private String obtenerDatos(){

        StringBuffer response = null;
        try {
            String protocolo = "http://";
            String ip = this.context.getResources().getString(R.string.ipweb);
            String puerto = this.context.getResources().getString(R.string.puertoweb);
            puerto = puerto.equals("") ? "" : ":" + puerto;
            String rutaUrl = protocolo + ip + puerto + "/happypet-web/funciones/admin_mascota.php";

            URL obj = new URL( rutaUrl);
            System.out.println("Funcion: " + funcion);
            if (funcion.equals("setImage")){
                data = "f=" + URLEncoder.encode(funcion, "UTF-8")
                        + "&nombre=" + URLEncoder.encode(nombre, "UTF-8")
                        + "&tipo=" + URLEncoder.encode(tipo, "UTF-8")
                        + "&sexo=" + URLEncoder.encode(sexo, "UTF-8")
                        + "&anio=" + URLEncoder.encode(anio, "UTF-8")
                        + "&particularidades=" + URLEncoder.encode(particularidades, "UTF-8")
                        + "&salud=" + URLEncoder.encode(salud, "UTF-8")
                        + "&tamanio=" + URLEncoder.encode(tamanio, "UTF-8")
                        + "&adoptado=" + URLEncoder.encode(this.adoptado, "UTF-8")
                        + "&esterilizado=" + URLEncoder.encode(this.esterilizado, "UTF-8")
                        + "&adoptable=0"
                        + "&imagen=" + URLEncoder.encode(encodedImage, "UTF-8")
                        + "&id_usuario=" + this.idUsuario;
                System.out.println("datos obtenerdatos -------- >    " + data);
            }

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en; q=0.5");
            con.setDoOutput(true);

            con.setFixedLengthStreamingMode(data.getBytes().length);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream out = new BufferedOutputStream(con.getOutputStream());
            out.write(data.getBytes());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    private boolean filtrarDatos(){
        cargarDatos = obtenerDatos();

        System.out.println("RESPUESTA -------- >    " + cargarDatos.toString());
        try{
            if(!cargarDatos.equalsIgnoreCase("")){
                if(funcion.equals("setImage")){
                    json_data = new JSONObject(cargarDatos);
                    ok =  Boolean.valueOf(json_data.getString("success"));
                    msg = json_data.getString("msg");
                }
                return true;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    private void actividad(){
        progress.dismiss();

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

//        if(ok){
            setResult(2, intenPadre);
//        }else{
//            setResult(RESULT_CANCELED);
//        }

        context.finish();

        if(funcion.equals("insert")){
            Toast.makeText(context, "Imagen Insertada al servidor ", Toast.LENGTH_LONG).show();
        }
    }

    class GetAndSet extends AsyncTask<String, String, String>{
        private Context myContext;

        public GetAndSet(Context context){
            this.myContext = context;
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(this.myContext, "", "Cargando Mascotas Disponibles...");
        }

        @Override
        protected String doInBackground(String... params) {

            if(filtrarDatos()){
                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        actividad();
                    }
                });
            }
            return null;
        }

//        @Override
//        protected void onPostExecute() {
//            if (progress.isShowing()) { progress.dismiss(); }
//
//        }
    }


}
