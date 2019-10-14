package cscholtz.android.nutrievaluator;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoopUploadActivity extends AppCompatActivity {

    private Button startButton;
    private TextView tiempo;

    private String nombre, sexo,edad,peso,talla,cintura,cadera,braquial,carpo,tricipital,bicipital,suprailiaco,subescapular;//reciben los parametros de los inputs
    private SQLiteOpen_Helper helper = new SQLiteOpen_Helper(this,"BD1",null,1); //clase que crea y hace consultas a la base de datos
    private String IMC,IPT,PESO_IDEAL,CMB,AMB,AGB,PT,CIN,RELCINCAD,CONTEXTURA; //reciben los string de texto a poner el el pdf, con valores obtenidos del evaluador

    //Clase para crear el archivo PDF
    private TemplatePDF templatePDF;
    //Guarda el nombre del archivo PDF actual
    private String FileName;

    //Referencia a Storage para acceder a FirebaseStorage en la Cloud
    private StorageReference storageReference;

    //JasonObjhect para recorrer el archivo .json
    private JSONObject jsonObject;
    private int num, len; //numero contador de archivos subidos a la nube // cantidad de input a sacar del archivo .json

    //Variables para hacer la medicion dl tiempo de ejecucion de la tarea
    private SimpleDateFormat tsf;
    private String t1, t2; //Time inicial y final
    private Date d1, d2; //Para obtener tiempo inicial y final

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
                ExecuteFunctions();
            }
        });
    }

    public void ExecuteFunctions() {
        String jsonString;
        try {
            InputStream is = getAssets().open("inputs_example.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer,"UTF-8");
            JSONArray jsonArray = new JSONArray(jsonString);
            len= 50;
            for(int i = 0;i<len; i++){
                jsonObject = jsonArray.getJSONObject(i);
                InputSetter();
                EvaluateData();
                CreatePDF();
                UploadFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void UploadFile(){
        File f1 = new File(Environment.getExternalStorageDirectory().toString()+"/PDF/"+FileName+".pdf");
        Uri uri_file = Uri.fromFile(f1);
        StorageReference stg = storageReference.child("Loop").child(f1.getName());
        stg.putFile(uri_file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        num +=1;
                        if(num == len){
                            t2 = tsf.format(new Date());
                            try {
                                d1 = tsf.parse(t1);
                                d2 = tsf.parse(t2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long diff = d2.getTime()-d1.getTime();
                            tiempo.setText(String.valueOf(diff)+" miliseconds");
                        }
                    }
                });
    }

    public void CreatePDF(){
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

    public void EvaluateData(){
        Evaluator E = new Evaluator(nombre, sexo, new Integer(edad),  new Integer(tricipital),  new Integer(bicipital),  new Integer(suprailiaco), new Integer(subescapular),  new Float(peso),  new Float(talla),  new Float(cintura),  new Float(cadera),  new Float(braquial),  new Float(carpo));
        helper.abrir();
        String e = helper.getIdEdad(new Integer(edad));
        IMC = "IMC: "+String.format("%.2f",E.getIMC()) + " kg/mt² "+E.evaluarIMC();
        IPT = "%IPT: "+String.format("%.2f",E.getIPT())+"% "+E.evaluarIPT();
        PESO_IDEAL = "PESO IDEAL: "+String.format("%.2f",E.getPesoIdeal())+" kg";
        Integer[] rCMB = E.rangoPercentiles(E.getCMB(),E.Percentiles(helper.percentiles(e,sexo,"CMB")));
        CMB = "CMB: "+String.format("%.0f",E.getCMB())+ " mm (P"+rCMB[0]+"- P"+rCMB[1]+") "+E.evaluarPercentilesCMB(rCMB[0],rCMB[1]);
        Integer[] rAMB = E.rangoPercentiles(E.getAMB(),E.Percentiles(helper.percentiles(e,sexo,"AMB")));
        AMB = "AMB: "+String.format("%.0f",E.getAMB())+ " mm (P"+rAMB[0]+"- P"+rAMB[1]+") "+E.evaluarPercentiles(rAMB[0],rAMB[1]);
        Integer[] rAGB = E.rangoPercentiles(E.getAGB(),E.Percentiles(helper.percentiles(e,sexo,"AGB")));
        AGB = "AGB: "+String.format("%.0f",E.getAGB())+ " mm (P"+rAGB[0]+"- P"+rAGB[1]+") "+E.evaluarPercentiles(rAGB[0],rAGB[1]);
        Integer[] rPT = E.rangoPercentiles(E.getPT(),E.Percentiles(helper.percentiles(e,sexo,"PT")));
        PT = "PT: "+String.format("%.0f",E.getPT())+" mm (P"+rPT[0]+"- P"+rPT[1]+") "+E.evaluarPercentiles(rPT[0],rPT[1]);
        CIN = "CINTURA: "+String.format("%.2f",E.getCin())+" cm "+E.evaluarCintura();
        RELCINCAD = "REL CINT/CAD: "+String.format("%.2f",E.getRelCinCad())+" "+E.evaluarRelCinCad();
        CONTEXTURA = "CONTEXTURA: "+String.format("%.2f",E.getContextura())+" "+E.evaluarContextura();
        helper.cerrar();
    }

    public void InputSetter()throws Exception{
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

}
