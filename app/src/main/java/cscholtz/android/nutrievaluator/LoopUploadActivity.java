package cscholtz.android.nutrievaluator;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.apache.pdfbox.util.Charsets;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoopUploadActivity extends AppCompatActivity {

    private Button startButton;
    private TextView tiempo;
    private String nombre;
    private String sexo;
    private String edad;
    private Float[] medidas = new Float[2];
    private Integer[] pliegues = new Integer[4];
    private Float[] circunferencias = new Float[4];
    private sqliteOpenHelper helper = new sqliteOpenHelper(this,"BD1",null,1); //clase que crea y hace consultas a la base de datos
    //reciben los string de texto a poner el el pdf, con valores obtenidos del evaluador
    private String imc;
    private String ipt;
    private String pesoideal;
    private String cmb;
    private String amb;
    private String agb;
    private String pt;
    private String cin;
    private String relcincad;
    private String contextura;
    //Clase para crear el archivo PDF

    //Guarda el nombre del archivo PDF actual
    private String fileName;
    //Referencia a Storage para acceder a FirebaseStorage en la Cloud
    private StorageReference storageReference;
    //JasonObjhect para recorrer el archivo .json
    private JSONObject jsonObject;
    private int num;
    private int len;
    //Variables para hacer la medicion dl tiempo de ejecucion de la tarea
    private SimpleDateFormat tsf;
    private String t1;
    private String t2;
    private Date d1;
    private Date d2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_upload);
        startButton = (Button) findViewById(R.id.startButtonLoop);
        tiempo = (TextView) findViewById(R.id.tiempoLoop);

        tsf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
        d1 = null;
        d2 = null;
        num = 0;
        storageReference = FirebaseStorage.getInstance().getReference();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1 = tsf.format(new Date());
                executeFunctions();
            }
        });
    }

    public void executeFunctions() {
        String jsonString;
        try {
            InputStream is = getAssets().open("inputs_example.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, Charsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);
            len= 50;
            for(int i = 0;i<len; i++){
                jsonObject = jsonArray.getJSONObject(i);
                inputSetter();
                evaluateData();
                createPDF();
                uploadFile();
            }
        }catch (Exception e){
            Log.e("ExecuteFunctions", e.toString());
        }
    }

    public void uploadFile(){
        File f1 = new File(Environment.getExternalStorageDirectory().toString()+"/PDF/"+ fileName +".pdf");
        Uri uriFile = Uri.fromFile(f1);
        StorageReference stg = storageReference.child("Loop").child(f1.getName());
        stg.putFile(uriFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        num +=1;
                        if(num == len){
                            t2 = tsf.format(new Date());
                            try {
                                d1 = tsf.parse(t1);
                                d2 = tsf.parse(t2);
                            } catch (Exception e) {
                                Log.e("SuccessListener", e.toString());
                            }
                            long diff = d2.getTime()-d1.getTime();
                            tiempo.setText(String.valueOf(diff)+" miliseconds");
                        }
                    }
                });
    }

    public void createPDF(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());
        TemplatePDF templatePDF;
        templatePDF = new TemplatePDF(getApplicationContext());
        fileName = nombre+"_"+currentDate;
        templatePDF.openDocument(fileName);
        templatePDF.addMetaData("Evaluacion Nutricional"+nombre,"evaluacion","cs");
        templatePDF.addTitles("Evaluacion Nutricional","Paciente: "+nombre,currentDate);
        templatePDF.addParagraph(imc);
        templatePDF.addParagraph(ipt);
        templatePDF.addParagraph(pesoideal);
        templatePDF.addParagraph(cmb);
        templatePDF.addParagraph(amb);
        templatePDF.addParagraph(agb);
        templatePDF.addParagraph(pt);
        templatePDF.addParagraph(cin);
        templatePDF.addParagraph(relcincad);
        templatePDF.addParagraph(contextura);
        templatePDF.closeDocument();
    }

    public void evaluateData(){
        Evaluator ev = new Evaluator(nombre, sexo, new Integer(edad),  medidas, pliegues, circunferencias);
        helper.abrir();
        String e = helper.getIdEdad(new Integer(edad));
        imc = "IMC: "+String.format("%.2f",ev.getIMC()) + " kg/mt² "+ev.evaluarIMC();
        ipt = "%IPT: "+String.format("%.2f",ev.getIPT())+"% "+ev.evaluarIPT();
        pesoideal = "PESO IDEAL: "+String.format("%.2f",ev.getPesoIdeal())+" kg";
        Integer[] rCMB = ev.rangoPercentiles(ev.getCMB(),ev.percentiles(helper.percentiles(e,sexo,"CMB")));
        cmb = "CMB: "+mmFloat(ev.getCMB())+percentilRange(rCMB[0],rCMB[1])+ev.evaluarPercentilesCMB(rCMB[0],rCMB[1]);
        Integer[] rAMB = ev.rangoPercentiles(ev.getAMB(),ev.percentiles(helper.percentiles(e,sexo,"AMB")));
        amb = "AMB: "+mmFloat(ev.getAMB())+percentilRange(rAMB[0],rAMB[1])+ev.evaluarPercentiles(rAMB[0],rAMB[1]);
        Integer[] rAGB = ev.rangoPercentiles(ev.getAGB(),ev.percentiles(helper.percentiles(e,sexo,"AGB")));
        agb = "AGB: "+mmFloat(ev.getAGB())+percentilRange(rAGB[0],rAGB[1])+ev.evaluarPercentiles(rAGB[0],rAGB[1]);
        Integer[] rPT = ev.rangoPercentiles(ev.getPT(),ev.percentiles(helper.percentiles(e,sexo,"PT")));
        pt = "PT: "+mmFloat(ev.getPT())+percentilRange(rPT[0],rPT[1])+ev.evaluarPercentiles(rPT[0],rPT[1]);
        cin = "CINTURA: "+String.format("%.2f",ev.getCin())+" cm "+ev.evaluarCintura();
        relcincad = "REL CINT/CAD: "+String.format("%.2f",ev.getRelCinCad())+" "+ev.evaluarRelCinCad();
        contextura = "CONTEXTURA: "+String.format("%.2f",ev.getContextura())+" "+ev.evaluarContextura();
        helper.cerrar();
    }

    public String mmFloat(double f){
        return String.format("%.0f",f)+"mm ";
    }

    public String percentilRange(Integer p1, Integer p2){
        return "(P"+p1+"- P"+p2+") ";
    }

    public void inputSetter() throws JSONException {
        nombre = jsonObject.getString("nombre");
        sexo = jsonObject.getString("sexo");
        edad = jsonObject.getString("edad");
        medidas[0] = Float.parseFloat(jsonObject.getString("peso"));
        medidas[2] = Float.parseFloat(jsonObject.getString("talla"));
        circunferencias[0] = Float.parseFloat(jsonObject.getString("cintura"));
        circunferencias[1] = Float.parseFloat(jsonObject.getString("cadera"));
        circunferencias[2] = Float.parseFloat(jsonObject.getString("braquial"));
        circunferencias[3] = Float.parseFloat(jsonObject.getString("carpo"));
        pliegues[0] = Integer.valueOf(jsonObject.getString("tricipital"));
        pliegues[1] = Integer.valueOf(jsonObject.getString("bicipital"));
        pliegues[2] = Integer.valueOf(jsonObject.getString("suprailiaco"));
        pliegues[3] = Integer.valueOf(jsonObject.getString("subescapular"));
    }

}
