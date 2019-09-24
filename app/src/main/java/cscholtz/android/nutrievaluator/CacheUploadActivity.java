package cscholtz.android.nutrievaluator;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CacheUploadActivity extends AppCompatActivity {

    private Button startButton;
    private String nombre, sexo,edad,peso,talla,cintura,cadera,braquial,carpo,tricipital,bicipital,suprailiaco,subescapular;//reciben los parametros de los inputs
    private TemplatePDF templatePDF;
    private SQLiteOpen_Helper helper = new SQLiteOpen_Helper(this,"BD1",null,1);
    private String IMC,IPT,PESO_IDEAL,CMB,AMB,AGB,PT,CIN,RELCINCAD,CONTEXTURA; //reciben los string de texto a poner el el pdf
    private String FileName;
    private JSONObject jsonObject;
    private StorageReference storageReference;
    public int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_upload);

        startButton = (Button) findViewById(R.id.startButtonCache);
        storageReference = FirebaseStorage.getInstance().getReference();
        num = 0;

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EjecutarTareas();
            }
        });
    }
    
    public void EjecutarTareas() {
        String jsonString;
        try {
            InputStream is = getAssets().open("inputs_example.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer,"UTF-8");
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0;i<jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                InputEjemplo();
                EvaluarDatos();
                crearPDF();
            }
            Compressor.zip( "ArchivosPdfs","",false);
            subirArchivo();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void subirArchivo(){
        File f1 = new File(Environment.getExternalStorageDirectory().toString()+"/ZIPS/ArchivosPdfs.zip");
        Uri uri_file = Uri.fromFile(f1);
        StorageReference stg = storageReference.child("Cache").child(f1.getName());
        stg.putFile(uri_file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        num +=1;
                        if(num == 1){
                            showToast("Archivos Uploaded");
                        }
                    }
                });

    }

    public void crearPDF(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());
        templatePDF = new TemplatePDF(getApplicationContext());
        FileName = nombre+"_"+currentDate;
        templatePDF.openDocument(FileName);
        templatePDF.addMetaData("Evaluacion Nutricional"+nombre,"evaluacion","cs");
        templatePDF.addTitles("Evaluacion Nutricional","Paciente: "+nombre,currentDate);
        templatePDF.addParagraph(IMC);
        templatePDF.addParagraph(IPT);
        templatePDF.addParagraph(PESO_IDEAL);
        templatePDF.addParagraph(CMB);
        templatePDF.addParagraph(AMB);
        templatePDF.addParagraph(AGB);
        templatePDF.addParagraph(PT);
        templatePDF.addParagraph(CIN);
        templatePDF.addParagraph(RELCINCAD);
        templatePDF.addParagraph(CONTEXTURA);
        templatePDF.closeDocument();
    }

    public void EvaluarDatos(){
        Evaluator E = new Evaluator(nombre, sexo, new Integer(edad),  new Integer(tricipital),  new Integer(bicipital),  new Integer(suprailiaco), new Integer(subescapular),  new Float(peso),  new Float(talla),  new Float(cintura),  new Float(cadera),  new Float(braquial),  new Float(carpo));
        helper.abrir();
        String e = helper.getIdEdad(new Integer(edad));
        IMC = "IMC: "+String.format("%.2f",E.getIMC()) + " kg/mt² "+E.evaluarIMC();
        IPT = "%IPT: "+String.format("%.2f",E.getIPT())+"% "+E.evaluarIPT();
        PESO_IDEAL = "PESO IDEAL: "+String.format("%.2f",E.getPesoIdeal())+" kg";
        Integer[] rCMB = E.rangoPercentiles(E.getCMB(),E.Percentiles(helper.percentiles(e,E.HoM(),"CMB")));
        CMB = "CMB: "+String.format("%.0f",E.getCMB())+ " mm (P"+rCMB[0]+"- P"+rCMB[1]+") "+E.evaluarPercentilesCMB(rCMB[0],rCMB[1]);
        Integer[] rAMB = E.rangoPercentiles(E.getAMB(),E.Percentiles(helper.percentiles(e,E.HoM(),"AMB")));
        AMB = "AMB: "+String.format("%.0f",E.getAMB())+ " mm (P"+rAMB[0]+"- P"+rAMB[1]+") "+E.evaluarPercentiles(rAMB[0],rAMB[1]);
        Integer[] rAGB = E.rangoPercentiles(E.getAGB(),E.Percentiles(helper.percentiles(e,E.HoM(),"AGB")));
        AGB = "AGB: "+String.format("%.0f",E.getAGB())+ " mm (P"+rAGB[0]+"- P"+rAGB[1]+") "+E.evaluarPercentiles(rAGB[0],rAGB[1]);
        Integer[] rPT = E.rangoPercentiles(E.getPT(),E.Percentiles(helper.percentiles(e,E.HoM(),"PT")));
        PT = "PT: "+String.format("%.0f",E.getPT())+" mm (P"+rPT[0]+"- P"+rPT[1]+") "+E.evaluarPercentiles(rPT[0],rPT[1]);
        CIN = "CINTURA: "+String.format("%.2f",E.getCin())+" cm "+E.evaluarCintura();
        RELCINCAD = "REL CINT/CAD: "+String.format("%.2f",E.getRelCinCad())+" "+E.evaluarRelCinCad();
        CONTEXTURA = "CONTEXTURA: "+String.format("%.2f",E.getContextura())+" "+E.evaluarContextura();
        helper.cerrar();
    }

    public void InputEjemplo()throws Exception{
        nombre = jsonObject.getString("nombre");
        sexo = jsonObject.getString("sexo");
        edad = jsonObject.getString("edad");
        peso = jsonObject.getString("peso");
        talla = jsonObject.getString("talla");
        cintura = jsonObject.getString("cintura");
        cadera = jsonObject.getString("cadera");
        braquial = jsonObject.getString("braquial");
        carpo = jsonObject.getString("carpo");
        tricipital = jsonObject.getString("tricipital");
        bicipital = jsonObject.getString("bicipital");;
        suprailiaco = jsonObject.getString("suprailiaco");
        subescapular = jsonObject.getString("subescapular");
    }

    private  void showToast(String text){
        Toast.makeText(CacheUploadActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}
